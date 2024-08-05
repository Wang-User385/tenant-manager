package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterRoleMapper;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.service.*;
import com.hand.hls.partner.service.IPrjQuotationCalcService;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import com.hand.hls.bp.dto.HlsBpSpouse;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsBpSpouseMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.credit.service.TongDunService;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.mapper.UploadAttachListMapper;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.sys.dto.SysDocumentList;
import com.hand.hls.sys.mapper.SysDocumentListMapper;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.hand.hls.utils.HlsCusMathUtil;
import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = Exception.class)
public class YLInterfaceServiceImpl implements YLInterfaceService {

    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private PrjLeaseItemInsuranceMapper prjLeaseItemInsuranceMapper;
    @Autowired
    private HlsCusBpMasterBankAccountMapper hlsCusBpMasterBankAccountMapper;
    @Autowired
    private ProjectLeaseItemMortgageMapper projectLeaseItemMortgageMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private ProjectLeaseItemSalesMapper projectLeaseItemSalesMapper;
    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;
    @Autowired
    private ProjectLeaseItemConditionMapper projectLeaseItemConditionMapper;
    @Autowired
    private TongDunService tongDunService;
    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;
    @Autowired
    private HlsBpSpouseMapper hlsBpSpouseMapper;
    @Autowired
    private UploadAttachListMapper uploadAttachListMapper;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private HlsCusBpMasterRoleMapper hlsCusBpMasterRoleMapper;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;
    @Autowired
    private IPrjQuotationCalcService prjQuotationCalcService;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private CshTransactionService cshTransactionService;
    @Autowired
    private CshWriteOffService cshWriteOffService;
    @Autowired
    private IConContractCashflowService cashflowService;
    @Autowired
    private SysDocumentListMapper sysDocumentListMapper;

    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusCshTransactionMapper transactionMapper;
    @Autowired
    private ICshAllocationService cshAllocationService;
    @Autowired
    private ICshAllocationReceiptService cshAllocationReceiptService;
    @Autowired
    private ICshAllocationCreditService cshAllocationCreditService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    /**
     * 工作流相关的常量
     */
    //换行符
    private static final String BR = "<br>";
    /**
     * 用于代码获取工作流提交的实现类
     */
    private static final String PROJECT_SIGN_WORK_FLOW = "ADVERTISING_REVIEW_WORK_FLOW";
    private static final String PROJECT = "project";
    private static final String PROJECT_NAME = "projectName";
    private static final String DOCUMENT_ID = "documentId";
    private static final String WORKFLOW_TYPE = "workFlowType";
    private static final String DOCUMENT_NUMBER = "documentNumber";
    private static final String LEASE_CHANNEL = "leaseChannel";

    //流程编码
    private final static String WORK_FLOW = "ADVERTISING_REVIEW_WORK_FLOW";
    //流程分类
    private final static String DEMO_NAME = "ADVERTISING_REVIEW_WORK_FLOW";
    private final static String DOCUMENT_NAME = "投放审查工作流";
    private final static String DOCUMENT_CATEGORY = "CON_CONTRACT";
    private final static String DOCUMENT_TYPE = " CONLB";
    @Override
    public String placeOrder(String decryptedStr,IRequest iRequest) throws HlsCusException {
        PlaceOrderDTO placeOrderDTO = JSONObject.parseObject(decryptedStr, PlaceOrderDTO.class);
        JSONObject returnJson = new JSONObject();

        //当前进件业务只有一家合作商，暂时只插入固定的这个合作商
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpCode("BP202407230057");
        hlsCusBpMaster.setBpType("MANUFACTURER");
        List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.selectHlsBpMaster(hlsCusBpMaster);
        //根据合作商id，查询产品定义表中的业务经理插入到商业伙伴创建人字段中
        HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
        hlsProductDefinition.setBpId(hlsCusBpMasters.get(0).getBpId());
        List<HlsProductDefinition> hlsProductDefinitionList = hlsProductDefinitionMapper.selectHlsProductDefinitionList(hlsProductDefinition);
        if (hlsProductDefinitionList.size() == 0){
            returnJson.put("code","400");
            returnJson.put("message","该合作商对应的产品为空，需在产品定义功能中维护新的产品");
            throw new HlsCusException(returnJson.toJSONString());
        }
        //step1:新增或更新hls_bp_master
        HlsCusBpMaster bpMaster = new HlsCusBpMaster();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date idIssueDate = null;
        Date idExpirationDate = null;
        try{
            idIssueDate = simpleDateFormat.parse(placeOrderDTO.getIdissue());
        }catch (ParseException e) {
            e.printStackTrace();
            returnJson.put("code","400");
            returnJson.put("message","证件签发日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        try {
            if("长期".equals(placeOrderDTO.getIdexp())){
                idExpirationDate = simpleDateFormat.parse(Long.parseLong(placeOrderDTO.getIdissue().substring(0,4)) + 100 + placeOrderDTO.getIdissue().substring(4,18));
            }else{
                idExpirationDate = simpleDateFormat.parse(placeOrderDTO.getIdexp());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            returnJson.put("code","400");
            returnJson.put("message","证件到期日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        bpMaster.setIdIssueDate(idIssueDate);
        bpMaster.setIdExpirationDate(idExpirationDate);
        bpMaster.setBpName(placeOrderDTO.getName());
        bpMaster.setPhone(placeOrderDTO.getMobile());

        List<HlsCusBpMaster> bpMasters = hlsCusBpMasterMapper.selectMasterByIdCardNo(placeOrderDTO.getIdCardNo());
        if (bpMasters.size()==0){
            String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "HLS_BP_MASTER", "NP", "NP", new HashMap<String, String>());
            bpMaster.setBpCode(codeRuleValue);
            bpMaster.setIdCardNo(placeOrderDTO.getIdCardNo());
            bpMaster.setCreationDate(new Date());
            bpMaster.setCreationDateStr(simpleDateFormat.format(new Date()));
            bpMaster.setCreatedBy(hlsProductDefinitionList.get(0).getUserId());
            bpMaster.setBpCategory("TENANT");
            bpMaster.setBpType("TENANT");
            bpMaster.setSource("1");
            bpMaster.setBpClass("NP");
            bpMaster.setIdType("ID_CARD");
            hlsCusBpMasterMapper.insertSelective(bpMaster);
        }else{
            bpMaster.setBpId(bpMasters.get(0).getBpId());
            hlsCusBpMasterMapper.updateByPrimaryKeySelective(bpMaster);
        }

        //step2:新增hls_bp_master_role
        HlsBpMasterRole hlsBpMasterRole = new HlsBpMasterRole();
        Boolean flag = false;
        List<String> stringList = hlsCusBpMasterRoleMapper.selectRoleById(bpMaster.getBpId());
        for (String s : stringList) {
            if (s.equals("TENANT")){
                flag = true;
            }
        }
        if (!flag){
            hlsBpMasterRole = new HlsBpMasterRole();
            hlsBpMasterRole.setBpId(bpMaster.getBpId());
            hlsBpMasterRole.setBpType("TENANT");
            hlsBpMasterRole.setBpCategory("TENANT");
            hlsBpMasterRole.setEnabledFlag("Y");
            hlsBpMasterRole.setPrimaryFlag("Y");
            hlsCusBpMasterRoleMapper.insertSelective(hlsBpMasterRole);
        }

        //step3: 获取当前客户所有的项目，判断项目状态
        List<HlsCusPrjProject> list = prjProjectMapper.selectProjectByIdCardNo(placeOrderDTO.getIdCardNo());
        if(list.size() > 0){
            returnJson.put("code","400");
            returnJson.put("message","存在在途单");
            throw new HlsCusException(returnJson.toJSONString());
        }

        //step4: 新增prj_project
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        Map<String, String> params = new HashMap<String, String>();
        params.put("PARAMETER_01","YL");
        String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(iRequest,"PRJ_PROJECT_IMPORT", "PRJLB", "LEASEBACK", params);

        hlsCusPrjProject.setManufacturerId(hlsCusBpMasters.get(0).getBpId());
        hlsCusPrjProject.setProjectNumber(codeRuleValue);
        hlsCusPrjProject.setCompanyId(1L);
        hlsCusPrjProject.setTenantId(bpMaster.getBpId());
        hlsCusPrjProject.setProjectStatus("NEW");
        hlsCusPrjProject.setPreStatus("NEW");
        hlsCusPrjProject.setOrderStatus("START");
        hlsCusPrjProject.setEmployeeId(hlsProductDefinitionList.get(0).getEmployeeId());
        hlsCusPrjProject.setUnitId(hlsProductDefinitionList.get(0).getUnitId());
        hlsCusPrjProject.setLeaseItemType(hlsProductDefinitionList.get(0).getLeaseItemType());
        hlsCusPrjProject.setInceptType(hlsProductDefinitionList.get(0).getInceptType());

        HlsCusBpMasterBankAccount bankAccountInfo = hlsCusBpMasterBankAccountMapper.selectBankByBpId(hlsCusBpMasters.get(0).getBpId());
        hlsCusPrjProject.setBankAccountNum(bankAccountInfo.getBankAccountNum());
        hlsCusPrjProject.setBankAccountName(bankAccountInfo.getBankAccountName());
        hlsCusPrjProject.setBankFullName(bankAccountInfo.getBankFullName());
        hlsCusPrjProject.setBankBranchName(bankAccountInfo.getBankBranchName());
        prjProjectMapper.insertSelective(hlsCusPrjProject);

        //step5: 新增prj_quotation
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
        hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        hlsCusPrjQuotation.setPriceList(hlsProductDefinitionList.get(0).getPriceList());
        hlsCusPrjQuotationMapper.insertSelective(hlsCusPrjQuotation);

        //step6: 新增prj_project_bp
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        hlsCusPrjProjectBp.setBpId(bpMaster.getBpId());
        hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
        hlsCusPrjProjectBp.setBpCategroy("TENANT");
        hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);

        //step7： 新增prj_project_lease_item
        HlsCusPrjProjectLeaseItem leaseItem = new HlsCusPrjProjectLeaseItem();
        leaseItem.setProjectId(hlsCusPrjProject.getProjectId());
        hlsCusPrjProjectLeaseItemMapper.insertSelective(leaseItem);

        //step8： 新增prj_lease_item_sales
        PrjProjectLeaseItemSales leaseItemSales = new PrjProjectLeaseItemSales();
        leaseItemSales.setProjectLeaseItemId(leaseItem.getProjectLeaseItemId());
        projectLeaseItemSalesMapper.insertSelective(leaseItemSales);

        //step9： 新增prj_lease_item_insurance
        PrjLeaseItemInsurance leaseItemInsurance = new PrjLeaseItemInsurance();
        leaseItemInsurance.setProjectLeaseItemId(leaseItem.getProjectLeaseItemId());
        prjLeaseItemInsuranceMapper.insertSelective(leaseItemInsurance);

        //step10： 新增prj_lease_item_mortgage
        PrjProjectLeaseItemMortgage leaseItemMortgages = new PrjProjectLeaseItemMortgage();
        leaseItemMortgages.setProjectLeaseItemId(leaseItem.getProjectLeaseItemId());
        projectLeaseItemMortgageMapper.insertSelective(leaseItemMortgages);

        //step11： 新增prj_lease_item_condition
        PrjProjectLeaseItemCondition leaseItemConditions = new PrjProjectLeaseItemCondition();
        leaseItemConditions.setProjectLeaseItemId(leaseItem.getProjectLeaseItemId());
        projectLeaseItemConditionMapper.insertSelective(leaseItemConditions);

        //step12： 新增prj_project_attachment
        HlsCusPrjProjectAttachment prjAttachment = new HlsCusPrjProjectAttachment();
        SysDocumentList sysDocumentList = new SysDocumentList();
        List<SysDocumentList> sysDocumentLists = sysDocumentListMapper.selectSysDocumentList(sysDocumentList);
        for (int i = 0; i < sysDocumentLists.size(); i++) {
            prjAttachment.setProjectId(hlsCusPrjProject.getProjectId());
            prjAttachment.setProjectAttachmentCategory(sysDocumentLists.get(i).getDocumentCategory());
            prjAttachment.setDocumentName(sysDocumentLists.get(i).getDocumentListName());
            prjAttachment.setAttachmentCode(sysDocumentLists.get(i).getDocumentType());
            hlsCusPrjProjectAttachmentMapper.insertSelective(prjAttachment);
        }

        //step13: 返回信息
        returnJson.put("code","200");
        returnJson.put("message","下单成功");
        returnJson.put("orderNo",codeRuleValue);
        return returnJson.toJSONString();
    }

    @Override
    public String closeOrder(String decryptedStr) throws HlsCusException {
        CloseOrderDTO closeOrderDTO = JSONObject.parseObject(decryptedStr, CloseOrderDTO.class);
        JSONObject returnJson = new JSONObject();

        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(closeOrderDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            returnJson.put("code","100003");
            returnJson.put("message","订单不存在");
            throw new HlsCusException(returnJson.toJSONString());
        }else{
            String projectStatus = hlsCusPrjProject.getProjectStatus();
            if ("APPROVING".equals(projectStatus)||"Y".equals(hlsCusPrjProject.getLoanInitialLease())){
                returnJson.put("code","100101");
                returnJson.put("message","订单状态和操作不相符");
                throw new HlsCusException(returnJson.toJSONString());
            }
        }
        //修改订单状态
        hlsCusPrjProject.setOrderStatus("CLOSED");
        prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        returnJson.put("code","200");
        returnJson.put("message","取消成功");
        return returnJson.toJSONString();
    }

    @Override
    public String queryOrder(String decryptedStr) throws HlsCusException {
        QueryOrderDTO queryOrderDTO = JSONObject.parseObject(decryptedStr, QueryOrderDTO.class);
        JSONObject returnJson = new JSONObject();

        //根据订单编号查询相对应的还款信息,判断订单存不存在，不存在直接返回
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(queryOrderDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            returnJson.put("code","100003");
            returnJson.put("message","订单不存在");
            throw new HlsCusException(returnJson.toJSONString());
        }
        List<RepayPlanTermInfoDTO> repayPlanTermInfoDTOList = prjProjectMapper.selectRepayPlanByOrderNo(queryOrderDTO.getOrderNo());
        QueryOrder queryOrder = prjProjectMapper.selectQueryOrderByOrderNo(queryOrderDTO.getOrderNo());
        if(queryOrder==null){
            queryOrder = new QueryOrder();
        }
        queryOrder.setRepayPlanTermInfoDTOList(repayPlanTermInfoDTOList);
        queryOrder.setStatus("NORMAL");
        //订单存在，判断合同状态是否为起租后状态、结清状态
        //如果是，则返回数据，如果不是，返回错误
        String contractStatus = prjProjectMapper.selectContractByOrderNo(queryOrderDTO.getOrderNo());
        if ("ET".equals(contractStatus)  || "INCEPT".equals(contractStatus)){
            returnJson.put("code","200");
            returnJson.put("message","查询成功");
            return returnJson.toJSONString();
        }else{
            returnJson.put("code","100101");
            returnJson.put("message","订单状态和操作不相符");
            throw new HlsCusException(returnJson.toJSONString());
        }
    }

    @Override
    public String repayment(String decryptedStr) throws HlsCusException {
        RepayMent repayMent = JSONObject.parseObject(decryptedStr, RepayMent.class);
        JSONObject returnJson = new JSONObject();

        List<HlsCusCshTransaction> hlsCusCshTransactionList = prjProjectMapper.selectTranSactionByOrderNo(repayMent.getOrderNo());
        if (hlsCusCshTransactionList.size()==0){
            returnJson.put("code","400");
            returnJson.put("message","查询数据为空");
            throw new HlsCusException(returnJson.toJSONString());
        }
        List<TermRepayDetailApplyDTO> termRepayDetailApplyDTOList = repayMent.getTermRepayDetailApplyDTOList();
        for (TermRepayDetailApplyDTO termRepayDetailApplyDTO : termRepayDetailApplyDTOList) {
            //判断还款方式是否为蚂蚁链代扣，如果是则判断结算单号、代扣交易单号是否为空
            if ("蚂蚁链代扣".equals(repayMent.getRepayType())){
                if (termRepayDetailApplyDTO.getTransactionNo()==null){
                    returnJson.put("code","400");
                    returnJson.put("message","结算单号为空");
                    return returnJson.toJSONString();
                }
                if ("蚂蚁链代扣".equals(termRepayDetailApplyDTO.getExternalDeductNo())){
                    returnJson.put("code","400");
                    returnJson.put("message","代扣交易单号为空");
                    return returnJson.toJSONString();
                }
            }
            //将数据保存入库
            for (HlsCusCshTransaction hlsCusCshTransaction : hlsCusCshTransactionList) {
                if (hlsCusCshTransaction.getTermNo().equals(termRepayDetailApplyDTO.getTermNo())){
                    hlsCusCshTransaction.setRepayPrincipal(termRepayDetailApplyDTO.getRepayPrincipal());
                    hlsCusCshTransaction.setRepayInterest(termRepayDetailApplyDTO.getRepayInterest());
                    hlsCusCshTransaction.setRepayAmount(termRepayDetailApplyDTO.getRepayAmount());
                    hlsCusCshTransaction.setPaymentMethod(repayMent.getRepayType());
                    hlsCusCshTransaction.setTransactionNo(termRepayDetailApplyDTO.getTransactionNo());
                    hlsCusCshTransaction.setExternalDeductNo(termRepayDetailApplyDTO.getExternalDeductNo());
                    hlsCusCshTransactionMapper.insertSelective(hlsCusCshTransaction);
                }
            }
        }

        returnJson.put("code","200");
        returnJson.put("message","还款成功");
        return returnJson.toJSONString();
    }

    @Override
    public String compensatoryTrialCalculation(String decryptedStr) throws HlsCusException {
        CompensatoryTrialCalculationDTO compensatoryTrialCalculationDTO = JSONObject.parseObject(decryptedStr, CompensatoryTrialCalculationDTO.class);
        JSONObject returnJson = new JSONObject();

        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(compensatoryTrialCalculationDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            returnJson.put("code","400");
            returnJson.put("message","订单不存在");
            throw new HlsCusException(returnJson.toJSONString());
        }
        //计算本金、利息、罚息、应付金额
        CompensatoryTrialCalculationDTO compensatoryTrialCalculation1 = prjProjectMapper.selectCTCByOrderNo(compensatoryTrialCalculationDTO);
        if (compensatoryTrialCalculation1==null){
            returnJson.put("code","400");
            returnJson.put("message","数据不存在");
            throw new HlsCusException(returnJson.toJSONString());
        }
        //判断传入时间是否为空，如果为空则用现在时间，如果不为空，则用传入时间
        if (compensatoryTrialCalculationDTO.getTrialTime()==null){
            compensatoryTrialCalculation1.setTrialTime(String.valueOf(new Date()));
        }else{
            compensatoryTrialCalculation1.setTrialTime(compensatoryTrialCalculationDTO.getTrialTime());
        }
        //设置返回订单号
        compensatoryTrialCalculation1.setOrderNo(compensatoryTrialCalculationDTO.getOrderNo());
        //设置返回期次号
        compensatoryTrialCalculation1.setTermNo(compensatoryTrialCalculationDTO.getTermNo());
        //罚息暂时为0
        compensatoryTrialCalculation1.setPenalty(0L);

        returnJson.put("code","200");
        returnJson.put("message","试算成功");
        returnJson.put("result",compensatoryTrialCalculation1);
        return returnJson.toJSONString();
    }

    @Override
    public String claimsSubrogation(String decryptedStr) throws HlsCusException{
        ClaimsSubrogationDTO claimsSubrogationDTO = JSONObject.parseObject(decryptedStr, ClaimsSubrogationDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号查询数据
        //List<HlsCusCshTransaction> hlsCusCshTransactionList = prjProjectMapper.selectTranSactionByOrderNo(claimsSubrogationDTO.getOrderNo());

        //根据订单编号和期次获取需要代偿的现金流数据
        HlsCusConContractCashflow conContractCashflow = conContractCashflowMapper.queryClaimsSubrogation(claimsSubrogationDTO);

        if (ObjectUtils.isEmpty(conContractCashflow)){
            jsonObject1.put("code","400");
            jsonObject1.put("message","代偿数据不存在");
            throw new HlsCusException(jsonObject1.toJSONString());
        }
        if (!Objects.equals((long) (conContractCashflow.getDueAmount()*100), claimsSubrogationDTO.getSubstituteAmount())) {
            jsonObject1.put("code","400");
            jsonObject1.put("message","代偿金额不匹配！");
            throw new HlsCusException(jsonObject1.toJSONString());
        }



        //将代偿数据入库
        conContractCashflow.setPlanType("COMP");
        conContractCashflow.setDueCompAmount(Double.valueOf(claimsSubrogationDTO.getSubstituteAmount())/100);
        conContractCashflowMapper.updateByPrimaryKeySelective(conContractCashflow);


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","代偿成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String advancesSettleTrialCalculation(String decryptedStr)throws HlsCusException{
        AdvancesSettleComputeDTO advancesSettleComputeDTO = JSONObject.parseObject(decryptedStr, AdvancesSettleComputeDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        //            判断试算日期是否为空，如果为空则使用当前日期，如果有，则使用传入日期
        if (advancesSettleComputeDTO.getTrialTime()==null){
            advancesSettleComputeDTO.setTrialTime(String.valueOf(new Date()));
        }
        CalculationResultsDto calculationResultsDto = calculationResult(advancesSettleComputeDTO.getOrderNo(),advancesSettleComputeDTO.getTrialTime(),
                "ET",11L,null);

        if (ObjectUtils.isEmpty(calculationResultsDto)){
            jsonObject1.put("code","400");
            jsonObject1.put("message","现金流数据不存在，请查看该订单是否已经做过回购或提前结清！");
            throw new HlsCusException(jsonObject1.toJSONString());
        }

        //试算数据封装：
        AdvancesSettleComputeDTO settleComputeDTO = new AdvancesSettleComputeDTO();
        settleComputeDTO.setOrderNo(advancesSettleComputeDTO.getOrderNo());
        settleComputeDTO.setPayableAmount((long) (calculationResultsDto.getPayableAmount()*100));
        settleComputeDTO.setDeductAmount((long)(calculationResultsDto.getDeductAmount()*100));
        settleComputeDTO.setTrialTime(advancesSettleComputeDTO.getTrialTime());
        settleComputeDTO.setTermNos(calculationResultsDto.getTermNos());
        settleComputeDTO.setDeductNos(calculationResultsDto.getDeductNos());
        settleComputeDTO.setPrincipal((long) (calculationResultsDto.getPrincipal()*100));
        settleComputeDTO.setInterest((long) (calculationResultsDto.getInterest()*100));
        settleComputeDTO.setPenalty((long) (calculationResultsDto.getPenalty()*100));
        settleComputeDTO.setOtherFee(0L);
        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","试算成功");
        jsonObject1.put("result",settleComputeDTO);
        return jsonObject1.toJSONString();
    }

    @Override
    public String advancesSettleRequest(String decryptedStr) throws HlsCusException{

        AdvancesSettleRequestDTO advancesSettleRequestDTO = JSONObject.parseObject(decryptedStr, AdvancesSettleRequestDTO.class);


        JSONObject jsonObject1 = new JSONObject();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);

        CalculationResultsDto calculationResultsDto = calculationResult(advancesSettleRequestDTO.getOrderNo(),dateString,
                "ET",11L,date);

        if (ObjectUtils.isEmpty(calculationResultsDto)){
            jsonObject1.put("code","400");
            jsonObject1.put("message","现金流数据不存在，请查看该订单是否已经做过回购或提前结清！");
            throw new HlsCusException(jsonObject1.toJSONString());
        }

        if (!Objects.equals((long) (calculationResultsDto.getPayableAmount()*100), advancesSettleRequestDTO.getPayableAmount())) {
            jsonObject1.put("code","400");
            jsonObject1.put("message","提前结清金额与计算金额不匹配！");
            throw new HlsCusException(jsonObject1.toJSONString());
        }

        HlsCusConContractCashflow conContractCashflow = calculationResultsDto.getConContractCashflow();
        //冻结所有已到期应收未收且未代偿租金（不足整期按整期算）、未到期租金现金流，冻结所有滞纳金
        conContractCashflowMapper.updateCashflowBlock(conContractCashflow.getContractId());
        //插入提前结清现金流
        this.conContractCashflowMapper.insertSelective(conContractCashflow);


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","提前结清成功");
        return jsonObject1.toJSONString();
    }

    private void checkRequired(DataAcquisitionDTO dataAcquisitionDTO,SaleInfo saleInfo,CarInfo carInfo,FinanceInfo financeInfo,PreRiskAuditData preRiskAuditData) throws HlsCusException {
        JSONObject returnJson = new JSONObject();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<DataAcquisitionDTO>> dtoChecks = validator.validate(dataAcquisitionDTO);
        if (!dtoChecks.isEmpty()) {
            StringBuilder message=new StringBuilder();
            for (ConstraintViolation<DataAcquisitionDTO> violation : dtoChecks) {
                message.append(violation.getMessage()).append(" ");
            }
            returnJson.put("code","400");
            returnJson.put("message",message.toString());
            throw new HlsCusException(returnJson.toJSONString());
        }

        Set<ConstraintViolation<SaleInfo>> saleInfoChecks = validator.validate(saleInfo);
        if (!saleInfoChecks.isEmpty()) {
            StringBuilder message=new StringBuilder();
            for (ConstraintViolation<SaleInfo> violation : saleInfoChecks) {
                message.append(violation.getMessage()).append(" ");
            }
            returnJson.put("code","400");
            returnJson.put("message",message.toString());
            throw new HlsCusException(returnJson.toJSONString());
        }

        Set<ConstraintViolation<CarInfo>> carInfoChecks = validator.validate(carInfo);
        if (!carInfoChecks.isEmpty()) {
            StringBuilder message=new StringBuilder();
            for (ConstraintViolation<CarInfo> violation : carInfoChecks) {
                message.append(violation.getMessage()).append(" ");
            }
            returnJson.put("code","400");
            returnJson.put("message",message.toString());
            throw new HlsCusException(returnJson.toJSONString());
        }

        Set<ConstraintViolation<FinanceInfo>> financeInfoChecks = validator.validate(financeInfo);
        if (!financeInfoChecks.isEmpty()) {
            StringBuilder message=new StringBuilder();
            for (ConstraintViolation<FinanceInfo> violation : financeInfoChecks) {
                message.append(violation.getMessage()).append(" ");
            }
            returnJson.put("code","400");
            returnJson.put("message",message.toString());
            throw new HlsCusException(returnJson.toJSONString());
        }

        if(preRiskAuditData != null){
            Set<ConstraintViolation<PreRiskAuditData>> preRiskAuditDataChecks = validator.validate(preRiskAuditData);
            if (!preRiskAuditDataChecks.isEmpty()) {
                StringBuilder message=new StringBuilder();
                for (ConstraintViolation<PreRiskAuditData> violation : preRiskAuditDataChecks) {
                    message.append(violation.getMessage()).append(" ");
                }
                returnJson.put("code","400");
                returnJson.put("message",message.toString());
                throw new HlsCusException(returnJson.toJSONString());
            }
        }
    }

    private void checkEqual(SaleInfo saleInfo, CarInfo carInfo, FinanceInfo financeInfo, PreRiskAuditData preRiskAuditData) throws HlsCusException {
        if(preRiskAuditData == null){
            return;
        }

        StringBuilder message=new StringBuilder();
        //销售信息
        if (!saleInfo.getSellerName().equals(preRiskAuditData.getDealername())){
            message.append("销售方统一社会信用代码名称 ");
        }
        if (!saleInfo.getSalesCityCode().equals(preRiskAuditData.getDealercity())){
            message.append("销售城市code ");
        }

        //租赁物相关信息
        if (!carInfo.getBrandName().equals(preRiskAuditData.getCarbrand2())){
            message.append("品牌名称 ");
        }
        if (!carInfo.getSeriesName().equals(preRiskAuditData.getChexi())){
            message.append("车系名称 ");
        }
        if (!carInfo.getModelName().equals(preRiskAuditData.getCartype())){
            message.append("车型名称 ");
        }
        if (!carInfo.getColor().equals(preRiskAuditData.getCarcolor())){
            message.append("车辆颜色 ");
        }

        //融资方案相关信息
        if (!financeInfo.getTermCount().equals(preRiskAuditData.getShenqingqixain())){
            message.append("期数 ");
        }
        double monthPayment = Double.parseDouble(financeInfo.getMonthPayment())/100;
        double yfzj = 0D;
        //double yfzj = Double.parseDouble(preRiskAuditData.getYfzj());
        if(StringUtils.isNotEmpty(preRiskAuditData.getYfzj()) && preRiskAuditData.getYfzj() != null){
            yfzj = Double.parseDouble(preRiskAuditData.getYfzj());
        }
        if (monthPayment!=yfzj){
            message.append("月租(分) ");
        }
        if (!financeInfo.getRate().equals(preRiskAuditData.getRzll()) || !financeInfo.getRate().equals(preRiskAuditData.getNhll())){
            message.append("利率 ");
        }
        double firstPayment = Double.parseDouble(financeInfo.getFirstPayment())/100;
        //double sfje = Double.parseDouble(preRiskAuditData.getSfje());
        double sfje = 0D;
        if(StringUtils.isNotEmpty(preRiskAuditData.getSfje()) && preRiskAuditData.getSfje() != null){
            sfje = Double.parseDouble(preRiskAuditData.getSfje());
        }
        if (firstPayment!=sfje){
            message.append("首付款(分) ");
        }
        double carGuidePrice = Double.parseDouble(financeInfo.getCarGuidePrice())/100;
        double cfpp = Double.parseDouble(preRiskAuditData.getCfpp());
        if (carGuidePrice!=cfpp){
            message.append("车辆指导价(分) ");
        }
        double carSalePrice = Double.parseDouble(financeInfo.getCarSalePrice())/100;
        //double clxsjg = Double.parseDouble(preRiskAuditData.getClxsjg());
        double clxsjg = 0D;
        if(StringUtils.isNotEmpty(preRiskAuditData.getClxsjg()) && preRiskAuditData.getClxsjg() != null){
            clxsjg = Double.parseDouble(preRiskAuditData.getClxsjg());
        }
        if (carSalePrice!=clxsjg){
            message.append("车辆售价(分) ");
        }
        double applyLoanAmount = Double.parseDouble(financeInfo.getApplyLoanAmount())/100;
        double financingamount = Double.parseDouble(preRiskAuditData.getFinancingamount());
        if (applyLoanAmount!=financingamount){
            message.append("申请融资额(分) ");
        }

        if(StringUtils.isNotEmpty(message)){
            message.append("与风控审核数据不一致");
            JSONObject returnJson = new JSONObject();
            returnJson.put("code","400");
            returnJson.put("message",message.toString());
            throw new HlsCusException(returnJson.toJSONString());
        }
    }

    private void checkChange(SaleInfo saleInfo, CarInfo carInfo, FinanceInfo financeInfo,
                             HlsCusPrjQuotation prjQuotation,HlsCusPrjProjectLeaseItem leaseItem,PrjProjectLeaseItemSales leaseItemSales) throws HlsCusException {
        StringBuilder message=new StringBuilder();
        //销售信息
        if(!saleInfo.getSellerCode().equals(leaseItemSales.getUnifiedSocialCreditCode())){
            message.append("销售方统一社会信用代码 ");
        }
        if(!saleInfo.getSellerName().equals(leaseItemSales.getDealerName())){
            message.append("销售方统一社会信用代码名称 ");
        }
        if(!saleInfo.getSalesCityCode().equals(leaseItemSales.getCityId())){
            message.append("销售城市code ");
        }

        //租赁物相关信息
        if(!carInfo.getBrandName().equals(leaseItem.getBrandC())){
            message.append("品牌名称 ");
        }
        if(!carInfo.getSeriesName().equals(leaseItem.getSeriesC())){
            message.append("车系名称 ");
        }
        if(!carInfo.getModelName().equals(leaseItem.getModelC())){
            message.append("车型名称 ");
        }
        if(!carInfo.getColor().equals(leaseItem.getColorC())){
            message.append("车辆颜色 ");
        }

        //融资方案相关信息
        long termCount = Long.parseLong(financeInfo.getTermCount());
        long leaseTimes = prjQuotation.getLeaseTimes();
        if(termCount != leaseTimes){
            message.append("期数 ");
        }

        double monthPayment = Double.parseDouble(financeInfo.getMonthPayment())/100;
        double pmt = prjQuotation.getPmt();
        if(monthPayment != pmt){
            message.append("月租(分) ");
        }

        double rate = Double.parseDouble(financeInfo.getRate());
        double intRate = prjQuotation.getIntRate();
        if(rate != intRate){
            message.append("利率 ");
        }

        double firstPayment = Double.parseDouble(financeInfo.getFirstPayment())/100;
        double downPayment = prjQuotation.getDownPayment();
        if(firstPayment != downPayment){
            message.append("首付款(分) ");
        }

        double carGuidePrice = Double.parseDouble(financeInfo.getCarGuidePrice())/100;
        double listPrice = leaseItem.getListPrice();
        if(carGuidePrice != listPrice){
            message.append("车辆指导价(分) ");
        }

        double carSalePrice = Double.parseDouble(financeInfo.getCarSalePrice())/100;
        double sellingPrice = leaseItem.getSellingPrice();
        if(carSalePrice != sellingPrice){
            message.append("车辆售价(分) ");
        }

        double applyLoanAmount = Double.parseDouble(financeInfo.getApplyLoanAmount())/100;
        double financeAmount = prjQuotation.getFinanceAmount();
        if(applyLoanAmount != financeAmount){
            message.append("申请融资额(分) ");
        }

        double carRestPrice = Double.parseDouble(financeInfo.getCarRestPrice())/100;
        double surplusAmount = prjQuotation.getSurplusAmount();
        if(carRestPrice != surplusAmount){
            message.append("剩余车辆价款(分) ");
        }

        if(StringUtils.isNotEmpty(message)){
            JSONObject returnJson = new JSONObject();
            returnJson.put("code","400");
            returnJson.put("message","正审已通过，不允许修改：" + message.toString());
            throw new HlsCusException(returnJson.toJSONString());
        }
    }

    private void setBusinessData(SaleInfo saleInfo, CarInfo carInfo, FinanceInfo financeInfo,
                                 HlsCusPrjQuotation prjQuotation,HlsCusPrjProjectLeaseItem leaseItem,
                                 PrjProjectLeaseItemSales leaseItemSales,PrjLeaseItemInsurance leaseItemInsurance) throws HlsCusException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //销售信息
        leaseItemSales.setUnifiedSocialCreditCode(saleInfo.getSellerCode());//销售方统一社会信用代码
        leaseItemSales.setSalesName(saleInfo.getSellerName());//销售方统一社会信用代码名称
        leaseItemSales.setRegisterSocialCreditCode(saleInfo.getLicensePlateOwnerCode());//上牌主体社会代码
        leaseItemSales.setRegisterName(saleInfo.getLicensePlateOwnerName());//上牌主体名称
        leaseItemSales.setMortgageSocialCreditCode(saleInfo.getMortgagorCode());//抵押人社会代码
        leaseItemSales.setMortgageName(saleInfo.getMortgagorName());//抵押人名称
        leaseItem.setCityCode(saleInfo.getLicensePlateCityCode());//上牌城市Code（国标码）
        leaseItemSales.setMortgageCity(saleInfo.getMortgageCityName());//抵押城市名称
        //leaseItemSales.setCityId(saleInfo.getSalesCityCode());//销售城市code

        //租赁物相关信息
        leaseItem.setBrandC(carInfo.getBrandName());//品牌名称
        leaseItem.setSeriesC(carInfo.getSeriesName());//车系名称
        leaseItem.setModelC(carInfo.getModelName());//车型名称
        leaseItem.setFrameNumber(carInfo.getVin());//vin码/车架号
        leaseItem.setColorC(carInfo.getColor());//车辆颜色
        try {
            if(!carInfo.getCarProductionDate().isEmpty()) {
                Date productDate = simpleDateFormat.parse(carInfo.getCarProductionDate());
                leaseItem.setProductDate(productDate);//车辆出厂日期
            }
        } catch (ParseException e) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("code","400");
            returnJson.put("message","车辆出厂日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        leaseItem.setEngineNumber(carInfo.getEngineNumber());//发动机号
        String mandatoryInsuranceAmount = carInfo.getMandatoryInsuranceAmount();
        if(StringUtils.isNotEmpty(mandatoryInsuranceAmount)){
            leaseItemInsurance.setCompulsoryAmount(Double.parseDouble(mandatoryInsuranceAmount)/100);//强制保险金额 (分)（选填）
        }
        leaseItemInsurance.setCommercialInsurance(carInfo.getCommercialInsuranceType());//商业保险类型

        //融资方案相关信息
        prjQuotation.setLeaseTimes(Long.valueOf(financeInfo.getTermCount()));//期次
        prjQuotation.setPmt(Double.valueOf(financeInfo.getMonthPayment())/100);//月租(分)
        prjQuotation.setIntRate(Double.valueOf(financeInfo.getRate()));//利率
        prjQuotation.setDownPayment(Double.valueOf(financeInfo.getFirstPayment())/100);//首付款(分)
        leaseItem.setListPrice(Double.valueOf(financeInfo.getCarGuidePrice())/100);//车辆指导价(分)
        leaseItem.setSellingPrice(Double.valueOf(financeInfo.getCarSalePrice())/100);//车辆售价(分)
        prjQuotation.setFinanceAmount(Double.valueOf(financeInfo.getApplyLoanAmount())/100);//申请融资额(分)
        prjQuotation.setSurplusAmount(Double.valueOf(financeInfo.getCarRestPrice())/100);//剩余车辆价款(分)
        try {
            Date startRentDate = simpleDateFormat.parse(financeInfo.getStartRentDate());
            prjQuotation.setLeaseStartDate(startRentDate);//起息日
        }catch (ParseException e) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("code","400");
            returnJson.put("message","起息日格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
    }

    private void saveGuarantee(PreRiskAuditData preRiskAuditData,HlsCusPrjProject hlsCusPrjProject) {
        //step1：保存hls_bp_master
        List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.selectMasterByIdCardNo(preRiskAuditData.getSureid());
        HlsCusBpMaster guaBpMaster = null;
        if (hlsCusBpMasters.isEmpty()){
            guaBpMaster = new HlsCusBpMaster();
            guaBpMaster.setCreationDateStr(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            guaBpMaster.setSource("1");
            guaBpMaster.setBpClass("NP");
            guaBpMaster.setBpCategory("GUARANTOR");
        }else{
            guaBpMaster = hlsCusBpMasters.get(0);
        }
        guaBpMaster.setBpName(preRiskAuditData.getSurename());//担保人姓名
        guaBpMaster.setIdType(preRiskAuditData.getSurecertype());//担保人证件类型
        guaBpMaster.setIdCardNo(preRiskAuditData.getSureid());//担保人身份证
        guaBpMaster.setPhone(preRiskAuditData.getSuremobi());//担保人手机

        if(guaBpMaster.getBpId() == null){
            hlsCusBpMasterMapper.insertSelective(guaBpMaster);

        }else{
            hlsCusBpMasterMapper.updateByPrimaryKeySelective(guaBpMaster);
        }

        //step2：保存hls_bp_master_role
        Boolean flag = false;
        List<String> stringList = hlsCusBpMasterRoleMapper.selectRoleById(guaBpMaster.getBpId());
        for (String s : stringList) {
            if (s.equals("GUARANTOR")){
                flag = true;
            }
        }
        if (!flag){
            HlsBpMasterRole hlsBpMasterRole = new HlsBpMasterRole();
            hlsBpMasterRole.setBpId(guaBpMaster.getBpId());
            hlsBpMasterRole.setBpType("GUARANTOR");
            hlsBpMasterRole.setBpCategory("GUARANTOR");
            hlsBpMasterRole.setEnabledFlag("Y");
            if(stringList.isEmpty()){
                hlsBpMasterRole.setPrimaryFlag("Y");
            }
            hlsCusBpMasterRoleMapper.insertSelective(hlsBpMasterRole);
        }

        //step3：保存prj_project_bp
        HlsCusPrjProjectBp hlsCusPrjProjectBp = hlsCusPrjProjectBpMapper.selectGurProjectBp(hlsCusPrjProject.getProjectId());
        if(hlsCusPrjProjectBp == null){
            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectBp.setBpCategroy("GUARANTOR");
            hlsCusPrjProjectBp.setBpId(guaBpMaster.getBpId());
            hlsCusPrjProjectBp.setRefV02(preRiskAuditData.getDbryczrgx());//担保人与承租人关系
            hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);
        }else{
            hlsCusPrjProjectBp.setBpId(guaBpMaster.getBpId());
            hlsCusPrjProjectBp.setRefV02(preRiskAuditData.getDbryczrgx());//担保人与承租人关系
            hlsCusPrjProjectBpMapper.updateByPrimaryKeySelective(hlsCusPrjProjectBp);
        }
    }

    private void saveSecTenant(PreRiskAuditData preRiskAuditData, HlsCusPrjProject hlsCusPrjProject) {
        //step1：保存hls_bp_master
        List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.selectMasterByIdCardNo(preRiskAuditData.getCoid());
        HlsCusBpMaster secTenantBpMaster = null;
        if (hlsCusBpMasters.isEmpty()){
            secTenantBpMaster = new HlsCusBpMaster();
            secTenantBpMaster.setCreationDateStr(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            secTenantBpMaster.setSource("1");
            secTenantBpMaster.setBpClass("NP");
            secTenantBpMaster.setBpCategory("TENANT-SEC");
        }else{
            secTenantBpMaster = hlsCusBpMasters.get(0);
        }
        secTenantBpMaster.setBpName(preRiskAuditData.getConame());//共同借款人姓名
        secTenantBpMaster.setIdType(preRiskAuditData.getCocerttype());//共同承租人证件类型
        secTenantBpMaster.setIdCardNo(preRiskAuditData.getCoid());//共同借款人身份证
        secTenantBpMaster.setPhone(preRiskAuditData.getComobile());//共同借款人手机
        secTenantBpMaster.setHouseAddress(preRiskAuditData.getCoaddr());//共同承租人居住地址
        secTenantBpMaster.setWorkingCompany(preRiskAuditData.getCocompany());//共同借款人工作单位
        secTenantBpMaster.setWorkPhone(preRiskAuditData.getCocomtel());//共同承租人公司电话
        secTenantBpMaster.setCompanyAddress(preRiskAuditData.getCocomaddr());//共同借款人公司地址

        if(secTenantBpMaster.getBpId() == null){
            hlsCusBpMasterMapper.insertSelective(secTenantBpMaster);

        }else{
            hlsCusBpMasterMapper.updateByPrimaryKeySelective(secTenantBpMaster);
        }

        //step2：保存hls_bp_master_role
        Boolean flag = false;
        List<String> stringList = hlsCusBpMasterRoleMapper.selectRoleById(secTenantBpMaster.getBpId());
        for (String s : stringList) {
            if (s.equals("TENANT-SEC")){
                flag = true;
            }
        }
        if (!flag){
            HlsBpMasterRole hlsBpMasterRole = new HlsBpMasterRole();
            hlsBpMasterRole.setBpId(secTenantBpMaster.getBpId());
            hlsBpMasterRole.setBpType("TENANT-SEC");
            hlsBpMasterRole.setBpCategory("TENANT-SEC");
            hlsBpMasterRole.setEnabledFlag("Y");
            if(stringList.isEmpty()){
                hlsBpMasterRole.setPrimaryFlag("Y");
            }
            hlsCusBpMasterRoleMapper.insertSelective(hlsBpMasterRole);
        }

        //step3：保存prj_project_bp
        HlsCusPrjProjectBp hlsCusPrjProjectBp = hlsCusPrjProjectBpMapper.selectSecTenantProjectBp(hlsCusPrjProject.getProjectId());
        if(hlsCusPrjProjectBp == null){
            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectBp.setBpCategroy("TENANT-SEC");
            hlsCusPrjProjectBp.setBpId(secTenantBpMaster.getBpId());
            hlsCusPrjProjectBp.setRefV02(preRiskAuditData.getCorelation());//共同借款人社会关系
            hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);
        }else{
            hlsCusPrjProjectBp.setBpId(secTenantBpMaster.getBpId());
            hlsCusPrjProjectBp.setRefV02(preRiskAuditData.getCorelation());//共同借款人社会关系
            hlsCusPrjProjectBpMapper.updateByPrimaryKeySelective(hlsCusPrjProjectBp);
        }
    }

    private void setRiskData(PreRiskAuditData preRiskAuditData,HlsCusPrjProject hlsCusPrjProject,HlsCusPrjQuotation prjQuotation,
                            HlsCusPrjProjectLeaseItem leaseItem,PrjProjectLeaseItemSales leaseItemSales,PrjLeaseItemInsurance leaseItemInsurance,
                            PrjProjectLeaseItemMortgage leaseItemMortgages,PrjProjectLeaseItemCondition leaseItemConditions,
                            HlsCusBpMaster bpMaster,HlsCusBpMasterBankAccount bpMasterBankAccount,HlsBpSpouse bpMasterSpouse) throws HlsCusException {
        if(preRiskAuditData == null){
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject returnJson = new JSONObject();
        returnJson.put("code","400");

        //正审通过后，不允许再传风控数据
        if("APPROVED".equals(hlsCusPrjProject.getProjectStatus())){
            returnJson.put("message","正审已通过，不允许采集风控审核相关数据");
            throw new HlsCusException(returnJson.toJSONString());
        }

        hlsCusPrjProject.setRiskInfo(preRiskAuditData.toString());//风控审核相关数据
        hlsCusPrjProject.setDivision(preRiskAuditData.getProline());//产品线
        bpMasterBankAccount.setBankAccountNum(preRiskAuditData.getCardno());//银行卡号
        bpMaster.setGender(preRiskAuditData.getSex());//性别
        bpMaster.setEthnicity(preRiskAuditData.getNation());//民族
        try {
            if(!preRiskAuditData.getBirthdate().isEmpty()) {
                Date dateOfBirth = simpleDateFormat1.parse(preRiskAuditData.getBirthdate());
                bpMaster.setDateOfBirth(dateOfBirth);//出生日期
            }
        } catch (ParseException e) {
            returnJson.put("message","出生日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        bpMaster.setAge(Long.valueOf(preRiskAuditData.getAge()));//年龄
        bpMaster.setNationality(preRiskAuditData.getNationality());//国籍
        bpMaster.setDomicileProvince(preRiskAuditData.getDomicileshen());//户籍所属省份
        bpMaster.setDomicileCity(preRiskAuditData.getDomicileshi());//户籍所属市
        bpMaster.setDomicileDistrict(preRiskAuditData.getDomicilequ());//户籍所属区
        bpMaster.setDomicileAddress(preRiskAuditData.getDomicileaddress());//户籍地址
        bpMaster.setDomicileLocalFlag(preRiskAuditData.getIslocaldomicile());//是否本地户籍
        bpMaster.setIdIssueOrgan(preRiskAuditData.getIssuegov());//签发机关
        bpMaster.setIdLongTerm(preRiskAuditData.getIsenable());//是否长期有效
        bpMaster.setHouseProvince(preRiskAuditData.getHomeaddressprovince());//居住地址省
        bpMaster.setHouseCity(preRiskAuditData.getHomeaddresspcity());//居住地址市
        bpMaster.setHouseDistrict(preRiskAuditData.getJzdzqx());//居住地址区县
        bpMaster.setHouseAddress(preRiskAuditData.getHomeaddress());//居住地址
        bpMaster.setHouseType(preRiskAuditData.getHousetype());//房产类型
        bpMaster.setMaritalStatus(preRiskAuditData.getMarriage());//婚姻状况
        //bpMaster.setNumberOfChildren(Long.valueOf(preRiskAuditData.getChildnum()));//子女人数
        if(StringUtils.isNotEmpty(preRiskAuditData.getChildnum()) && preRiskAuditData.getChildnum() != null){
            bpMaster.setNumberOfChildren(Long.valueOf(preRiskAuditData.getChildnum()));//子女人数
        }
        bpMaster.setDriverLicenseFlag(preRiskAuditData.getIsdriverlicence());//有无驾照
        bpMaster.setDriverLicenseType(preRiskAuditData.getDriverlicencetype());//驾照类型
        bpMaster.setDriverLicenseStatus(preRiskAuditData.getDriverstatus());//驾照状态
        try {
            if(!preRiskAuditData.getJzjzrq().isEmpty()) {
                Date driverLicenseDeadline = simpleDateFormat1.parse(preRiskAuditData.getJzjzrq());
                bpMaster.setDriverLicenseDeadline(driverLicenseDeadline);//驾照截止日期
            }
        } catch (ParseException e) {
            returnJson.put("message","驾照截止日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        //违章分数
        if(StringUtils.isNotEmpty(preRiskAuditData.getWzfs()) && preRiskAuditData.getWzfs() != null){
            bpMaster.setViolationScore(Long.valueOf(preRiskAuditData.getWzfs()));//违章分数
        }
        //违章罚款
        if(StringUtils.isNotEmpty(preRiskAuditData.getWzfk()) && preRiskAuditData.getWzfk() != null){
            bpMaster.setViolationFines(Long.valueOf(preRiskAuditData.getWzfk()));//违章罚款
        }
        bpMaster.setHighestDegree(preRiskAuditData.getDiploma());//学历
        bpMaster.setWorkingCompany(preRiskAuditData.getCompany());//单位名称
        bpMaster.setEconomicInduClassify(hlsCusBpMasterMapper.selectHlsStatClassByCode(preRiskAuditData.getIndustry()).toString());//所属行业
        bpMaster.setJobNature(preRiskAuditData.getCorpnprop());//单位性质
        bpMaster.setProfession(preRiskAuditData.getOccu());//职业
        bpMaster.setPosition(preRiskAuditData.getPosition());//当前职位
        if(StringUtils.isNotEmpty(preRiskAuditData.getSalary()) && preRiskAuditData.getSalary() != null){
            bpMaster.setMonthlyIncome(Double.valueOf(preRiskAuditData.getSalary()));//个人月收入
        }
        bpMaster.setWorkPhone(preRiskAuditData.getCompanyphone());//单位电话
        bpMaster.setCompanyProvince(preRiskAuditData.getCompanyshen());//公司所属省份
        bpMaster.setCompanyCity(preRiskAuditData.getCompanyshi());//公司所属市
        bpMaster.setCompanyDistrict(preRiskAuditData.getCompanyqu());//公司所属区
        bpMaster.setCompanyAddress(preRiskAuditData.getCompaddr());//公司地址
        hlsCusPrjProject.setDriverAndApplicant(preRiskAuditData.getSjjsrysqrgx());//实际驾驶人与申请人关系

        if ("1".equals(preRiskAuditData.getIssureor())){
            saveGuarantee(preRiskAuditData,hlsCusPrjProject);//有无担保人
        }

        if ("1".equals(preRiskAuditData.getIscop())){
            saveSecTenant(preRiskAuditData,hlsCusPrjProject);//有无共同承租人
        }

        //直系亲属关系
        bpMaster.setBpNameSp(preRiskAuditData.getSpousename());//配偶姓名
        bpMaster.setIdCardNoSp(preRiskAuditData.getSpouseidcard());//配偶身份证
        //bpMaster.setSpousePhone(Long.valueOf(preRiskAuditData.getSpousephone()));
        if(StringUtils.isNotEmpty(preRiskAuditData.getSpousephone()) && preRiskAuditData.getSpousephone() != null){
            bpMaster.setSpousePhone(Long.valueOf(preRiskAuditData.getSpousephone()));//配偶联系电话
        }
        bpMaster.setGenderSp(preRiskAuditData.getSpousesex());//配偶性别
        try {
            if(!preRiskAuditData.getSpousebir().isEmpty()){
                Date  dateOfBirthSp = simpleDateFormat1.parse(preRiskAuditData.getSpousebir());
                bpMaster.setDateOfBirthSp(dateOfBirthSp);//配偶出生日期
            }
        } catch (ParseException e) {
            returnJson.put("message","配偶出生日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        bpMaster.setSpouseJobsUnit(preRiskAuditData.getSpousecomp());//配偶公司名称
        if(StringUtils.isNotEmpty(preRiskAuditData.getSpousecompind()) && preRiskAuditData.getSpousecompind() != null) {
            bpMaster.setInduClassifySp(hlsCusBpMasterMapper.selectHlsStatClassByCode(preRiskAuditData.getSpousecompind()).toString());//配偶公司所属行业
        }
        bpMaster.setCompanyNatureSp(preRiskAuditData.getSpousecomptype());//配偶公司性质
        bpMaster.setProfessionSp(preRiskAuditData.getSpousezylx());//配偶职业类型
        bpMaster.setJobTitleSp(preRiskAuditData.getSpousezw());//配偶职位
        bpMaster.setAddressSp(preRiskAuditData.getSpoucecompaddr());//配偶单位地址
        //联系人数量
        bpMasterSpouse.setRelationship(preRiskAuditData.getContreleship());//联系人与承租人关系
        bpMasterSpouse.setPersonName(preRiskAuditData.getContname());//联系人姓名
        bpMasterSpouse.setAddress(preRiskAuditData.getContaddr());//联系人当前居住地址
        bpMasterSpouse.setCellPhone(preRiskAuditData.getPartymobile());//联系人移动电话
        leaseItemSales.setDealerNumber(preRiskAuditData.getDealerid());//经销商编号
        leaseItemSales.setDealerName(preRiskAuditData.getDealername());//经销商名称
        leaseItemSales.setProvinceId(Integer.valueOf(preRiskAuditData.getDealerprovince()));//经销商所在省份
        leaseItemSales.setCityId(Integer.valueOf(preRiskAuditData.getDealercity()));//经销商所在城市
        if(StringUtils.isNotEmpty(preRiskAuditData.getDealerqu()) && preRiskAuditData.getDealerqu() != null){
            leaseItemSales.setDistrictId(Integer.valueOf(preRiskAuditData.getDealerqu()));//经销商所属区县
        }
        leaseItemSales.setDealerAddress(preRiskAuditData.getDealerdaqu());//经销商所属大区
        leaseItem.setManufacturer(preRiskAuditData.getCarfac());//制造商
        leaseItem.setVoitureType(preRiskAuditData.getVehicletype());//车辆类型（小型普通客车）
        //车辆品牌
        //车辆型号
        //车系
        //车辆颜色
        //档位形式
        leaseItem.setVehicleCapacity(preRiskAuditData.getCarzkcount());//车辆准载(定员)
        leaseItem.setIsImport(preRiskAuditData.getSfjk());//是否进口
        //车架号
        //发动机号码
        leaseItem.setFuelType(preRiskAuditData.getRllx());//燃料类型
        //车辆出厂日期
        if(StringUtils.isNotEmpty(preRiskAuditData.getClpgjg()) && preRiskAuditData.getClpgjg() != null){
            leaseItem.setEvaluationValue(Double.valueOf(preRiskAuditData.getClpgjg()));//车辆评估价格
        }
        try {
            if(!preRiskAuditData.getScdjrq().isEmpty()) {
                Date firstRegistrationDate = simpleDateFormat1.parse(preRiskAuditData.getScdjrq());
                leaseItem.setFirstRegistrationDate(firstRegistrationDate);//首次登记日期
            }
        } catch (ParseException e) {
            returnJson.put("message","首次登记日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        try {
            if(!preRiskAuditData.getTransferencedate().isEmpty()) {
                Date transferRegistrationDate = simpleDateFormat1.parse(preRiskAuditData.getTransferencedate());
                leaseItem.setTransferRegistrationDate(transferRegistrationDate);//转让登记日期
            }
        } catch (ParseException e) {
            returnJson.put("message","转让登记日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        leaseItem.setLicensePlateNumber(preRiskAuditData.getChepaihao());//车牌号
        leaseItem.setNatureOfVehicle(preRiskAuditData.getCarnatureofuse());//车辆使用性质
        leaseItem.setCityCode(preRiskAuditData.getRegisteredcity());//上牌城市
        try {
            if(!preRiskAuditData.getScspr().isEmpty()) {
                Date firstPlateDate = simpleDateFormat1.parse(preRiskAuditData.getScspr());
                leaseItem.setFirstPlateDate(firstPlateDate);//首次上牌日
            }
        } catch (ParseException e) {
            returnJson.put("message","首次上牌日格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getBxlc()) && preRiskAuditData.getBxlc() != null){
            leaseItem.setOdometerReading(Integer.valueOf(preRiskAuditData.getBxlc()));//表显里程（公里数）
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getCarlife()) && preRiskAuditData.getCarlife() != null){
            leaseItem.setVehicleAge(Integer.valueOf(preRiskAuditData.getCarlife()));//车辆年限
        }
        leaseItem.setPropPerson(preRiskAuditData.getCaraffiliation());//车辆所有人
        if(StringUtils.isNotEmpty(preRiskAuditData.getDycs()) && preRiskAuditData.getDycs() != null){
            leaseItemMortgages.setNumberOfMortgages(Integer.valueOf(preRiskAuditData.getDycs()));//抵押次数
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getGhjcs()) && preRiskAuditData.getGhjcs() != null){
            leaseItemMortgages.setNumberOfTransfers(Integer.valueOf(preRiskAuditData.getGhjcs()));//过户次数
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getJyndics()) && preRiskAuditData.getJyndics() != null){
            leaseItemMortgages.setNumberOfMortgagesOne(Integer.valueOf(preRiskAuditData.getJyndics()));//近1年抵押次数
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getLast1yearguohucount()) && preRiskAuditData.getLast1yearguohucount() != null){
            leaseItemMortgages.setNumberOfTransfersOne(Integer.valueOf(preRiskAuditData.getLast1yearguohucount()));//近1年过户次数
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getLast2yearguohucount()) && preRiskAuditData.getLast2yearguohucount() != null){
            leaseItemMortgages.setNumberOfTransfersTwo(Integer.valueOf(preRiskAuditData.getLast2yearguohucount()));//近2年过户次数
        }
        leaseItemMortgages.setIsRenewalRecord(preRiskAuditData.getIsregister());//是否有车辆登记证补领记录
        leaseItemMortgages.setIsHalfRenewalRecord(preRiskAuditData.getIsregisterhy());//近半年是否有车辆登记证补领记录
        try {
            if(!preRiskAuditData.getLastmortgagedate().isEmpty()) {
                Date lastTransfersDate = simpleDateFormat1.parse(preRiskAuditData.getLastmortgagedate());
                leaseItemMortgages.setLastTransfersDate(lastTransfersDate);//上一次抵押登记日期
            }
        } catch (ParseException e) {
            returnJson.put("message","上一次抵押登记日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        try {
            if(!preRiskAuditData.getLastdtecompressiondate().isEmpty()) {
                Date recentlyTransfersDate = simpleDateFormat1.parse(preRiskAuditData.getLastdtecompressiondate());
                leaseItemMortgages.setRecentlyTransfersDate(recentlyTransfersDate);//最近一次解押日期
            }
        } catch (ParseException e) {
            returnJson.put("message","最近一次解押日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        leaseItemMortgages.setTransfersStatus(preRiskAuditData.getMortgagestatus());//抵押状态
        if(StringUtils.isNotEmpty(preRiskAuditData.getJyts()) && preRiskAuditData.getJyts() != null){
            leaseItemMortgages.setDaysToRelease(Integer.valueOf(preRiskAuditData.getJyts()));//解押天数
        }
        leaseItemInsurance.setIsCompulsoryInsurance(preRiskAuditData.getSfyjqx());//是否有交强险
        try {
            if(!preRiskAuditData.getJqxdqrq().isEmpty()) {
                Date compulsoryEndDate = simpleDateFormat1.parse(preRiskAuditData.getJqxdqrq());
                leaseItemInsurance.setCompulsoryEndDate(compulsoryEndDate);//交强险到期日期
            }
        } catch (ParseException e) {
            returnJson.put("message","交强险到期日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        leaseItemInsurance.setIsVehicleDamage(preRiskAuditData.getSfycsx());//是否有车损险
        try {
            if(!preRiskAuditData.getCsxdqrq().isEmpty()) {
                Date vehicleEndDate = simpleDateFormat1.parse(preRiskAuditData.getCsxdqrq());
                leaseItemInsurance.setVehicleEndDate(vehicleEndDate);//车损险到期日期
            }
        } catch (ParseException e) {
            returnJson.put("message","车损险到期日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        leaseItemInsurance.setIsThirdParty(preRiskAuditData.getSfyszx());//是否有第三者责任险
        try {
            if(!preRiskAuditData.getSzxdqrq().isEmpty()) {
                Date thirdEndDate = simpleDateFormat1.parse(preRiskAuditData.getSzxdqrq());
                leaseItemInsurance.setThirdEndDate(thirdEndDate);//第三者责任险到期日期
            }
        } catch (ParseException e) {
            returnJson.put("message","第三者责任险到期日期格式错误");
            throw new HlsCusException(returnJson.toJSONString());
        }
        leaseItemConditions.setIsAnnualInspection(preRiskAuditData.getIscheckyear());//是否年检
        leaseItem.setGpsIsInstallation(preRiskAuditData.getSfazgps());//是否安装GPS
        //车抵贷产品名称
        leaseItem.setLoanPurpose(preRiskAuditData.getUsage());//贷款用途
        leaseItem.setLeaseItemAmount(Double.valueOf(preRiskAuditData.getFinancingamount()));//融资金额
        //核批租赁项目总价
        //借款申请金额
        //申请期限
        leaseItemConditions.setAccidentStatus(preRiskAuditData.getSgzk());//事故状况
        leaseItemConditions.setEngineOverhaul(preRiskAuditData.getFdjdx());//发动机大修
        leaseItemConditions.setIsWaterDamaged(preRiskAuditData.getSfsp());//是否水泡
        leaseItemConditions.setIsConversion(preRiskAuditData.getSfyzf());//是否营转非
        leaseItemConditions.setIsSignificantlyModified(preRiskAuditData.getSfzdgzc());//是否重大改装车
        leaseItem.setPlateType(preRiskAuditData.getSptype());//上牌类型
        leaseItem.setLicensePlateOwnership(preRiskAuditData.getPaizhaogs());//牌照归属
        if(StringUtils.isNotEmpty(preRiskAuditData.getCljyjg()) && preRiskAuditData.getCljyjg() != null){
            leaseItem.setPrice(Double.valueOf(preRiskAuditData.getCljyjg()));//车辆交易价
        }
        leaseItemConditions.setOriginalOwnerCardType(preRiskAuditData.getYuanchezzjlx());//原车主证件类型
        leaseItemConditions.setOriginalOwnerName(preRiskAuditData.getYuanczxm());//原车主姓名
        leaseItemConditions.setOriginalOwnerCardNum(preRiskAuditData.getYuanchezzjhm());//原车主证件号
        leaseItemConditions.setOriginalOwnerAddress(
                preRiskAuditData.getYuanchezhuhujishengfen()
                        +preRiskAuditData.getYuanchezhuhujishi()+preRiskAuditData.getCarownersdomicilelast());//原车主户籍所在省份 原车主户籍所在市 原车主户籍所在区县
        leaseItemConditions.setMaintenanceInfo(preRiskAuditData.getBywxqk());//维修保养情况
        leaseItemConditions.setPremiumAddOn(preRiskAuditData.getJpjz());//精品加装
        //月付租金
        //年化利率
        //首付金额
        //车辆销售价格
        //车辆厂商指导价格
        //融资利率
        prjQuotation.setDownPaymentRatio(Double.valueOf(preRiskAuditData.getPaymentratio()));//首付比例
        //尾款比例
        //附加品比例
        //尾款
        //附加品金额
        if(StringUtils.isNotEmpty(preRiskAuditData.getGouzhis()) && preRiskAuditData.getGouzhis() != null){
            leaseItem.setPurchaseTax(Double.valueOf(preRiskAuditData.getGouzhis()));//购置税
        }
        //强制保险金额
        //商业保险
        if(StringUtils.isNotEmpty(preRiskAuditData.getCtac()) && preRiskAuditData.getCtac() != null){
            leaseItem.setPlateInvoiceAmount(Double.valueOf(preRiskAuditData.getCtac()));//上牌发票金额
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getCheliangse()) && preRiskAuditData.getCheliangse() != null){
            leaseItem.setVehicleTax(Double.valueOf(preRiskAuditData.getCheliangse()));//车辆税额
        }
        //服务合同费
        if(StringUtils.isNotEmpty(preRiskAuditData.getYanbaoje()) && preRiskAuditData.getYanbaoje() != null){
            leaseItemInsurance.setExtendedWarrantyAmount(Double.valueOf(preRiskAuditData.getYanbaoje()));//延保金额
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getZspje()) && preRiskAuditData.getZspje() != null){
            leaseItem.setAccessoryAmount(Double.valueOf(preRiskAuditData.getZspje()));//装饰品金额
        }
        if(StringUtils.isNotEmpty(preRiskAuditData.getGpsfy()) && preRiskAuditData.getGpsfy() != null){
            leaseItem.setGpsFee(Double.valueOf(preRiskAuditData.getGpsfy()));//GPS费用
        }
        //其他费用
        //佣金
        //月还款额
        if(StringUtils.isNotEmpty(preRiskAuditData.getClbxje()) && preRiskAuditData.getClbxje() != null){
            leaseItemInsurance.setInsuranceAmount(Double.valueOf(preRiskAuditData.getClbxje())/10);//车辆保险金额
        }
    }

    @Override
    public String dataAcquisition(String decryptedStr,IRequest iRequest) throws HlsCusException {
        JSONObject returnJson = new JSONObject();

        DataAcquisitionDTO dataAcquisitionDTO = JSONObject.parseObject(decryptedStr, DataAcquisitionDTO.class);
        SaleInfo saleInfo = dataAcquisitionDTO.getSaleInfo();
        CarInfo carInfo = dataAcquisitionDTO.getCarInfo();
        FinanceInfo financeInfo = dataAcquisitionDTO.getFinanceInfo();
        PreRiskAuditData preRiskAuditData = null;
        if(dataAcquisitionDTO.getRiskInfo() != null){
            preRiskAuditData  = JSONObject.parseObject(dataAcquisitionDTO.getRiskInfo(), PreRiskAuditData.class);
        }

        //step1: 请求报文必填项校验
        checkRequired(dataAcquisitionDTO,saleInfo,carInfo,financeInfo,preRiskAuditData);

        //step2: 业务数据与风控数据一致性校验
        checkEqual(saleInfo,carInfo,financeInfo,preRiskAuditData);

        //step3: 订单是否存在
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(dataAcquisitionDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            returnJson.put("code","400");
            returnJson.put("message","订单不存在");
            throw new HlsCusException(returnJson.toJSONString());
        }

        //step4: 查询数据库里的DTO
        HlsCusPrjQuotation prjQuotation = hlsCusPrjQuotationMapper.selectQuoByProjectId(hlsCusPrjProject.getProjectId()).get(0);//报价方案
        HlsCusPrjProjectLeaseItem leaseItem = hlsCusPrjProjectLeaseItemMapper.selectLeaseItemByProjectId(hlsCusPrjProject.getProjectId()).get(0);//租赁物基本信息
        PrjProjectLeaseItemSales leaseItemSales = projectLeaseItemSalesMapper.prjProjectLeaseItemSalesQuery(leaseItem.getProjectLeaseItemId()).get(0);//租赁物销售信息
        PrjLeaseItemInsurance leaseItemInsurance = prjLeaseItemInsuranceMapper.selectInsByLeaseItemId(leaseItem.getProjectLeaseItemId());//租赁物保险信息
        PrjProjectLeaseItemMortgage leaseItemMortgages = projectLeaseItemMortgageMapper.prjProjectLeaseItemMortgageByLeaseItemId(leaseItem.getProjectLeaseItemId()).get(0);//租赁物抵押信息
        PrjProjectLeaseItemCondition leaseItemConditions = projectLeaseItemConditionMapper.prjProjectLeaseItemConditionByLeaseItemId(leaseItem.getProjectLeaseItemId()).get(0);//租赁物车况信息

        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectByProjectId(hlsCusPrjProject.getProjectId());//承租人基本信息
        HlsCusBpMasterBankAccount bpMasterBankAccount = hlsCusBpMasterBankAccountMapper.selectBankByBpId(bpMaster.getBpId());//承租人银行账号信息（如果没有则new一个）
        if(bpMasterBankAccount == null){
            bpMasterBankAccount = new HlsCusBpMasterBankAccount();
            bpMasterBankAccount.setBpId(bpMaster.getBpId());
        }
        List<HlsBpSpouse> hlsBpSpouseList = hlsBpSpouseMapper.selectByBpId(bpMaster.getBpId());
        HlsBpSpouse bpMasterSpouse = null;//承租人关联人信息（如果没有则new一个）
        if(hlsBpSpouseList.size() == 0){
            bpMasterSpouse = new HlsBpSpouse();
            bpMasterSpouse.setBpId(bpMaster.getBpId());
        }else{
            bpMasterSpouse = hlsBpSpouseList.get(0);
        }

        //step5: 与数据库里的数据比对（正审通过后，投放审查前）
        String projectStatus = hlsCusPrjProject.getProjectStatus();//正审状态
        String investmentStatus = hlsCusPrjProject.getInvestmentStatus();//投放审查状态
        //订单状态
        String orderStatus = hlsCusPrjProject.getOrderStatus();
        //预审状态为审批中、订单状态是起租或者关闭、进件状态为审批中不能进行数据采集
        if ("APPROVING".equals(projectStatus)){
            //设置返回状态
            returnJson.put("code","400");
            returnJson.put("message","正审状态为审批中，不允许进行数据采集");
            throw new HlsCusException(returnJson.toJSONString());
        }
        if ("INCEPT".equals(orderStatus)){
            //设置返回状态
            returnJson.put("code","400");
            returnJson.put("message","订单状态为已起租，不允许进行数据采集");
            throw new HlsCusException(returnJson.toJSONString());
        }
        if ("CLOSED".equals(orderStatus)){
            //设置返回状态
            returnJson.put("code","400");
            returnJson.put("message","订单状态为已关闭，不允许进行数据采集");
            throw new HlsCusException(returnJson.toJSONString());
        }
        if("APPROVED".equals(projectStatus)){
            if("APPROVING".equals(investmentStatus)){
                returnJson.put("code","400");
                returnJson.put("message","投放审查审批中，不允许进行数据采集");
                throw new HlsCusException(returnJson.toJSONString());
            }
            if("APPROVED".equals(investmentStatus)){
                returnJson.put("code","400");
                returnJson.put("message","投放审查已通过，不允许进行数据采集");
                throw new HlsCusException(returnJson.toJSONString());
            }
            checkChange(saleInfo,carInfo,financeInfo,prjQuotation,leaseItem,leaseItemSales);
        }

        //step6：设置业务数据
        setBusinessData(saleInfo,carInfo,financeInfo,prjQuotation,leaseItem,leaseItemSales,leaseItemInsurance);

        //step7：设置风控数据
        setRiskData(preRiskAuditData, hlsCusPrjProject,prjQuotation,
                leaseItem,leaseItemSales,leaseItemInsurance,leaseItemMortgages,leaseItemConditions,
                bpMaster,bpMasterBankAccount,bpMasterSpouse);

        //step8: 更新数据
        prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        hlsCusPrjQuotationMapper.updateByPrimaryKeySelective(prjQuotation);
        hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKeySelective(leaseItem);
        projectLeaseItemSalesMapper.updateByPrimaryKeySelective(leaseItemSales);
        prjLeaseItemInsuranceMapper.updateByPrimaryKeySelective(leaseItemInsurance);
        projectLeaseItemMortgageMapper.updateByPrimaryKeySelective(leaseItemMortgages);
        projectLeaseItemConditionMapper.updateByPrimaryKeySelective(leaseItemConditions);
        hlsCusBpMasterMapper.updateByPrimaryKeySelective(bpMaster);
        if(bpMasterBankAccount.getBankAccountId() == null){
            hlsCusBpMasterBankAccountMapper.insertSelective(bpMasterBankAccount);
        }else{
            hlsCusBpMasterBankAccountMapper.updateByPrimaryKeySelective(bpMasterBankAccount);
        }
        if(bpMasterSpouse.getSpouseId() == null){
            hlsBpSpouseMapper.insertSelective(bpMasterSpouse);
        }else{
            hlsBpSpouseMapper.updateByPrimaryKeySelective(bpMasterSpouse);
        }

        //step9: 报价计算
        try{
            prjQuotationCalcService.prjQuotationCalc(prjQuotation.getQuotationId(),iRequest);
        }catch (Exception e){
            e.printStackTrace();
            returnJson.put("code","400");
            returnJson.put("message","报价计算异常，请联系管理员");
            throw new HlsCusException(returnJson.toJSONString());
        }

        //step10：计算后再次比对月还款额
        HlsCusPrjQuotationCashflow quoCashflow = new HlsCusPrjQuotationCashflow();
        quoCashflow.setQuotationId(prjQuotation.getQuotationId());
        quoCashflow.setTimes(1L);
        quoCashflow.setCfItem(1L);
        List<HlsCusPrjQuotationCashflow> quoCashflowList = hlsCusPrjQuotationCashflowMapper.select(quoCashflow);
        if(!quoCashflowList.isEmpty()){
            double dueAmount = quoCashflowList.get(0).getDueAmount();
            double pmt = prjQuotation.getPmt();
            if(dueAmount != pmt){
                returnJson.put("code","400");
                returnJson.put("message","报价计算的月租金与传输的月租金不一致！");
                throw new HlsCusException(returnJson.toJSONString());
            }
        }

        returnJson.put("code","200");
        returnJson.put("message","数据采集成功");
        return returnJson.toJSONString();
    }

    @Override
    public String overdueRepurchaseTrialCalculation(String decryptedStr) throws HlsCusException{
        OverdueRepurchaseTrialCalculationDTO overdueRepurchaseTrialCalculationDTO = JSONObject.parseObject(decryptedStr, OverdueRepurchaseTrialCalculationDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        CalculationResultsDto calculationResultsDto = calculationResult(overdueRepurchaseTrialCalculationDTO.getOrderNo(),overdueRepurchaseTrialCalculationDTO.getTrialTime(),
                "REPO",13L,null);

        if (ObjectUtils.isEmpty(calculationResultsDto)){
            jsonObject1.put("code","400");
            jsonObject1.put("message","回购现金流数据不存在，请查看该订单是否已经做过回购或提前结清！");
            throw new HlsCusException(jsonObject1.toJSONString());
        }

        //试算数据封装：
        OverdueRepurchaseTrialCalculationDTO trialCalculationDTO = new OverdueRepurchaseTrialCalculationDTO();
        trialCalculationDTO.setOrderNo(overdueRepurchaseTrialCalculationDTO.getOrderNo());
        trialCalculationDTO.setPayableAmount((long) (calculationResultsDto.getPayableAmount()*100));
        trialCalculationDTO.setDeductAmount((long)(calculationResultsDto.getDeductAmount()*100));
        trialCalculationDTO.setTrialTime(overdueRepurchaseTrialCalculationDTO.getTrialTime());
        trialCalculationDTO.setTermNos(calculationResultsDto.getTermNos());
        trialCalculationDTO.setDeductNos(calculationResultsDto.getDeductNos());
        trialCalculationDTO.setPrincipal((long) (calculationResultsDto.getPrincipal()*100));
        trialCalculationDTO.setInterest((long) (calculationResultsDto.getInterest()*100));
        trialCalculationDTO.setPenalty(0L);
        trialCalculationDTO.setOtherFee(0L);



        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","试算成功");
        jsonObject1.put("result",trialCalculationDTO);
        return jsonObject1.toJSONString();
    }

    @Override
    public String overdueRepurchaseRequest(String decryptedStr, IRequest iRequest, HttpSession session) throws HlsCusException{
        OverdueRepurchaseRequestDTO overdueRepurchaseRequestDTO = JSONObject.parseObject(decryptedStr, OverdueRepurchaseRequestDTO.class);


        JSONObject jsonObject1 = new JSONObject();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);

        CalculationResultsDto calculationResultsDto = calculationResult(overdueRepurchaseRequestDTO.getOrderNo(),dateString,
                "REPO",13L,date);

        if (ObjectUtils.isEmpty(calculationResultsDto)){
            jsonObject1.put("code","400");
            jsonObject1.put("message","回购现金流数据不存在，请查看该订单是否已经做过回购或提前结清！");
            throw new HlsCusException(jsonObject1.toJSONString());
        }

        if (!Objects.equals((long) (calculationResultsDto.getPayableAmount()*100), overdueRepurchaseRequestDTO.getBuybackAmount())) {
            jsonObject1.put("code","400");
            jsonObject1.put("message","回购金额与计算金额不匹配！");
            throw new HlsCusException(jsonObject1.toJSONString());
        }

        HlsCusConContractCashflow conContractCashflow = calculationResultsDto.getConContractCashflow();

        //冻结所有已到期应收未收且未代偿租金（不足整期按整期算）、未到期租金现金流，冻结所有滞纳金
        conContractCashflowMapper.updateCashflowBlock(conContractCashflow.getContractId());

        //插入回购现金流
        this.conContractCashflowMapper.insertSelective(conContractCashflow);

        List<HlsCusCshWriteOff> hlsCusCshWriteOffs = new ArrayList<>();
        List<HlsCusCshTransaction> transactionList = new ArrayList<>();
        //已收代偿自动核销为租金
        for (HlsCusConContractCashflow cc : calculationResultsDto.getWriteOffList()) {
            //根据现金流查询代偿现金事务数据
            transactionList = transactionMapper.queryTransactionByCashflowId(cc.getContractId(),cc.getCashflowId());

            //初始化剩余未核销金额
            transactionList.stream().forEach(item -> {
                        Double unWriteOffAmount = HlsCusMathUtil.sub(item.getTransactionAmount()-nvl(item.getWriteOffAmount(),0.0), nvl(item.getAdvanceReceiptAmount(),0.0), 2);
                        item.setAllocationAmount(unWriteOffAmount);
                    }
            );

            Double allocationAmount = 0.00;
            for (HlsCusCshTransaction cshTransaction : transactionList) {
                Double receiptAllocationAmount = cshTransaction.getAllocationAmount();
                Double writeOffDueAmount = HlsCusMathUtil.sub(cc.getDueAmount(), allocationAmount, 2);
                //核销
                HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                cshWriteOff.setCshWriteOffAmount(cshTransaction.getTransactionAmount());
                cshWriteOff.setWriteOffDueAmount(cshTransaction.getTransactionAmount());
                cshWriteOff.setDueAmount(cshTransaction.getTransactionAmount());
                cshWriteOff.setCompanyId(iRequest.getCompanyId());
                cshWriteOff.setCreationDate(new Date());
                cshWriteOff.setWriteOffPrincipal(cc.getPrincipal());
                cshWriteOff.setWriteOffInterest(cc.getInterest());
                cshWriteOff = setCshWriteOff(cc, cshWriteOff, cshTransaction.getTransactionId(), "RECEIPT_CREDIT");

                //如果 收款剩余未核销金额 大于等于 债权剩余待核销金额 且 债权剩余待核销金额 大于0
                if (receiptAllocationAmount >= writeOffDueAmount && writeOffDueAmount > 0) {
                    //收款剩余未核销金额  逐步减少
                    cshTransaction.setAllocationAmount(HlsCusMathUtil.sub(cshTransaction.getAllocationAmount(), writeOffDueAmount, 2));
                    //债权剩余待核销金额 逐步增长
                    allocationAmount = HlsCusMathUtil.add(allocationAmount, writeOffDueAmount, 2);
                    hlsCusCshWriteOffs.add(cshWriteOff);

                }//如果  收款剩余未核销金额 小于等于 债权剩余待核销金额 且 金额大于0  同时 债权剩余待核销金额 大于0
                else if (receiptAllocationAmount < writeOffDueAmount && receiptAllocationAmount > 0 && writeOffDueAmount > 0) {
                    //设置核销本金 和 核销利息
                    //如何核销金额小于待核销金额   优先核销利息 再核销本金
                    if (cc.getInterest() >= receiptAllocationAmount) {
                        cshWriteOff.setWriteOffInterest(receiptAllocationAmount);
                        cc.setPrincipal(0.0);
                        cc.setInterest(HlsCusMathUtil.sub(cc.getInterest(),receiptAllocationAmount));
                    } else {
                        cshWriteOff.setWriteOffPrincipal(HlsCusMathUtil.sub(receiptAllocationAmount, nvl(cshWriteOff.getWriteOffInterest(),0.0), 2));
                        cc.setInterest(0.0);
                        cc.setPrincipal(HlsCusMathUtil.sub(cc.getPrincipal(),cshWriteOff.getWriteOffPrincipal()));
                    }
                    hlsCusCshWriteOffs.add(cshWriteOff);
                    //收款剩余未核销金额  逐步减少
                    cshTransaction.setAllocationAmount(0.0);
                    //债权剩余待核销金额 逐步增长
                    allocationAmount = HlsCusMathUtil.add(allocationAmount, receiptAllocationAmount, 2);
                }

                HlsCusCshTransaction transaction = new HlsCusCshTransaction();
                transaction.setTransactionId(cshTransaction.getTransactionId());
                transaction.setContractId(cc.getContractId());
                cshTransactionService.updateByPrimaryKeySelective(iRequest, cshTransaction);

            }

            //现金流核销
            try {
                cshWriteOffService.writeOff(iRequest, hlsCusCshWriteOffs, session);
            }catch (Exception e) {
                jsonObject1.put("code","400");
                jsonObject1.put("message","代偿租金核销失败！"+e.getMessage());
                throw new HlsCusException(jsonObject1.toJSONString());
            }

            //插入 分配相关表
            saveAllocation(transactionList,cc,iRequest);

        }

        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","逾期回购成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String queryWithholdingState(String decryptedStr) throws HlsCusException {
        QueryWithholdingStateDTO queryWithholdingStateDTO = JSONObject.parseObject(decryptedStr, QueryWithholdingStateDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号查询数据


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","查询成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String stopWithholding(String decryptedStr) throws HlsCusException {
        StopWithholdingDTO stopWithholdingDTO = JSONObject.parseObject(decryptedStr, StopWithholdingDTO.class);


        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号和期次号，修改状态


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","暂停代扣成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String recoverWithholding(String decryptedStr) throws HlsCusException {
        RecoverWithholdingDTO recoverWithholdingDTO = JSONObject.parseObject(decryptedStr, RecoverWithholdingDTO.class);


        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号和期次号，修改状态


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","恢复代扣成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String businessApplication(String decryptedStr,HttpServletRequest request,IRequest iRequest) throws HlsCusException, ResMessageException {
        BusinessApplicationDTO businessApplicationDTO = JSONObject.parseObject(decryptedStr, BusinessApplicationDTO.class);
        JSONObject returnJson = new JSONObject();

        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(businessApplicationDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            returnJson.put("code","100003");
            returnJson.put("message","订单不存在");
            throw new HlsCusException(returnJson.toJSONString());
        }
        //根据action，执行操作
        String action = businessApplicationDTO.getAction();
        if ("PRE_RISK".equals(action)){
            String s = tongDunService.preliminaryValid(hlsCusPrjProject.getProjectId(), request);
//            校验客户信息查询授权书是否已经上传
            if ("Accept".equals(s)){
                /*hlsCusPrjProject.setPreStatus("APPROVED");
                prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);*/
                returnJson.put("code","200");
                returnJson.put("message","预审成功");
                return returnJson.toJSONString();
            }else{
                returnJson.put("code","400");
                returnJson.put("message","预审失败");
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else if ("APPLY_PRE_RISK".equals(action)){
            String s = tongDunService.interlocutoryValid(hlsCusPrjProject.getProjectId(), request);
            if ("Accept".equals(s)){
                hlsCusPrjProject.setProjectStatus("APPROVED");
                hlsCusPrjProject.setApprovedDate(new Date());
                prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
                returnJson.put("code","200");
                returnJson.put("message","审核成功");
                return returnJson.toJSONString();
            }else if ("Reject".equals(s)){
                returnJson.put("code","400");
                returnJson.put("message","同盾请求接口返回审批拒绝");
                throw new HlsCusException(returnJson.toJSONString());
            }else if ("Review".equals(s)){
                returnJson.put("code","400");
                returnJson.put("message","同盾请求接口返回谨慎通过，已发起进件正审流程");
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else if ("RE_APPLY_PRE_RISK".equals(action)){
            String s = tongDunService.interlocutoryValid(hlsCusPrjProject.getProjectId(), request);
            if ("Accept".equals(s)){
                returnJson.put("code","200");
                returnJson.put("message","审核成功");
                return returnJson.toJSONString();
            }else if ("Reject".equals(s) || "Error".equals(s)){
                returnJson.put("code","400");
                returnJson.put("message","审核失败");
                throw new HlsCusException(returnJson.toJSONString());
            }
        }else if ("APPLY_WITHHOLD_CONTRACT".equals(action)){
            //申请代扣签约
        }else if ("APPLY_LOAN".equals(action)||"RE_APPLY_LOAN".equals(action)){
            //申请放款前，校验（合同、协议文件、抵质押材料）的相关附件是否已经上传
            String multiMessage = checkAttachMulti(hlsCusPrjProject.getProjectId());
            //如果校验不通过直接返回
            if (!multiMessage.isEmpty()){
                //设置返回状态
                returnJson.put("code","400");
                returnJson.put("message","该进件项目的:"+multiMessage.substring(0, multiMessage.length() - 1)+"附件未上传");
                throw new HlsCusException(returnJson.toJSONString());
            }
            //获取riskinfo数据
            PreRiskAuditData preRiskAuditData = JSONObject.parseObject(hlsCusPrjProject.getRiskInfo(), PreRiskAuditData.class);
            if (preRiskAuditData==null){
                returnJson.put("code","400");
                returnJson.put("message","riskinfo信息不能为空");
                throw new HlsCusException(returnJson.toJSONString());
            }
            //获取报价
            List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.selectQuoByProjectId(hlsCusPrjProject.getProjectId());
            if (hlsCusPrjQuotations.size()==0){
                returnJson.put("code","400");
                returnJson.put("message","报价不能为空");
                throw new HlsCusException(returnJson.toJSONString());
            }
            //获取租赁物信息
            List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectLeaseItemMapper.selectLeaseItemByProjectId(hlsCusPrjProject.getProjectId());
            if (hlsCusPrjProjectLeaseItemList.size()==0){
                returnJson.put("code","400");
                returnJson.put("message","租赁物不能为空");
                throw new HlsCusException(returnJson.toJSONString());
            }
            //获取附件
            /*List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.queryByProjectId(hlsCusPrjProject.getProjectId());
            if (hlsCusPrjProjectAttachments.size()==0){
                returnJson.put("code","400");
                returnJson.put("message","附件不能为空");
                throw new HlsCusException(returnJson.toJSONString());
            }*/
            HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusPrjQuotations.get(0);
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = hlsCusPrjProjectLeaseItemList.get(0);
            if (preRiskAuditData.getCarbrand2().equals(hlsCusPrjProjectLeaseItem.getBrandC())&&
                    preRiskAuditData.getChexi().equals(hlsCusPrjProjectLeaseItem.getSeriesC())&&
                    preRiskAuditData.getCartype().equals(hlsCusPrjProjectLeaseItem.getModelC())&&
                    preRiskAuditData.getCarcolor().equals(hlsCusPrjProjectLeaseItem.getColorC())&&
                    (Double.compare(Double.valueOf(preRiskAuditData.getFinancingamount()),hlsCusPrjProjectLeaseItem.getFinanceAmount()) == 0)&&
                    (Double.compare(Double.valueOf(preRiskAuditData.getClxsjg()),hlsCusPrjProjectLeaseItem.getSellingPrice()) == 0)&&
                    (Double.compare(Double.valueOf(preRiskAuditData.getCfpp()),hlsCusPrjProjectLeaseItem.getListPrice()) == 0)&&
                    (Double.compare(Double.valueOf(preRiskAuditData.getYfzj()),hlsCusPrjQuotation.getPmt()) == 0)&&
                    (Double.compare(Double.valueOf(preRiskAuditData.getNhll()),hlsCusPrjQuotation.getIntRate()) == 0)&&
                    (Double.compare(Double.valueOf(preRiskAuditData.getSfje()),hlsCusPrjQuotation.getDownPayment()) == 0)){
                //获取审批通过日的毫秒值
                long approvedtTime = hlsCusPrjProject.getApprovedDate().getTime();
                //获取当前时间毫秒值
                long nowTime = new Date().getTime();
                //计算间隔的时间
                long days = (nowTime - approvedtTime) / (24 * 60 * 60 * 1000);
                if ("APPLY_LOAN".equals(action)){
                    if (days>=30){
                        returnJson.put("code","400");
                        returnJson.put("message","审批通过超过三十天");
                        throw new HlsCusException(returnJson.toJSONString());
                    }
                    //发起投放审查流程前先校验
                    dateCheck(hlsCusPrjProject);
                    //发起投放审查流程
                    signWorkFlowSubmit(iRequest, hlsCusPrjProject);
                    hlsCusPrjProject.setInvestmentStatus("APPROVING");
                    prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
                }else if ("RE_APPLY_LOAN".equals(action)){
                    if (days>=50){
                        returnJson.put("code","400");
                        returnJson.put("message","再次审批通过超过五十天");
                        throw new HlsCusException(returnJson.toJSONString());
                    }
                    //再次发起投放审查流程前先校验有没有流程中的
                    dateCheck(hlsCusPrjProject);
                    //发起投放审查流程
                    signWorkFlowSubmit(iRequest, hlsCusPrjProject);
                    hlsCusPrjProject.setInvestmentStatus("APPROVING");
                    prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
                }
            }else{
                returnJson.put("code","400");
                returnJson.put("message","风控与业务数据不一致");
                throw new HlsCusException(returnJson.toJSONString());
            }

        }else{
            //抵押材料审核
        }

        returnJson.put("code","200");
        returnJson.put("message","业务申请成功");
        return returnJson.toJSONString();
    }

    private void replaceAttach(HlsCusPrjProjectAttachment prjAttachment,String fileId){
        //删除附件
//        FndAttachmentMulti prjMulti = new FndAttachmentMulti();
//        prjMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
//        prjMulti.setTablePkValue(prjAttachment.getProjectAttachmentId().toString());
//        List<FndAttachmentMulti> prjMultiList = fndAttachmentMultiMapper.select(prjMulti);
//        for(FndAttachmentMulti multi : prjMultiList){
//            fndAttachmentMapper.deleteByPrimaryKey(multi.getAttachmentId());
//            fndAttachmentMultiMapper.deleteByPrimaryKey(multi.getRecordId());
//        }
        //插入附件
        UploadAttachList uploadAttachList = uploadAttachListMapper.selectByFileId(fileId);
        FndAttachmentMulti uploadMulti = new FndAttachmentMulti();
        uploadMulti.setTableName("GT_UPLOAD_ATTACH_LIST");
        uploadMulti.setTablePkValue(uploadAttachList.getListId().toString());
        List<FndAttachmentMulti> uploadMultiList = fndAttachmentMultiMapper.select(uploadMulti);
        for(FndAttachmentMulti multi : uploadMultiList){
            FndAttachment attach = fndAttachmentMapper.selectByPrimaryKey(multi.getAttachmentId());
            FndAttachment insertAttach = new FndAttachment();
            insertAttach.setSourceTypeCode("fnd_atm_attachment_multi");
            insertAttach.setFileName(attach.getFileName());
            insertAttach.setFilePath(attach.getFilePath());
            insertAttach.setMimeType(attach.getMimeType());
            insertAttach.setFileSize(attach.getFileSize());
            insertAttach.setFileTypeCode(attach.getFileTypeCode());
            insertAttach.setCreatedBy(RequestHelper.getCurrentRequest().getUserId());
            insertAttach.setCreationDate(new Date());
            insertAttach.setLastUpdatedBy(RequestHelper.getCurrentRequest().getUserId());
            insertAttach.setLastUpdateDate(new Date());
            fndAttachmentMapper.insertSelective(insertAttach);

            FndAttachmentMulti insertMulti = new FndAttachmentMulti();
            insertMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
            insertMulti.setTablePkValue(prjAttachment.getProjectAttachmentId().toString());
            insertMulti.setAttachmentId(insertAttach.getAttachmentId());
            insertMulti.setCreatedBy(RequestHelper.getCurrentRequest().getUserId());
            insertMulti.setCreationDate(new Date());
            insertMulti.setLastUpdatedBy(RequestHelper.getCurrentRequest().getUserId());
            insertMulti.setLastUpdateDate(new Date());
            fndAttachmentMultiMapper.insertSelective(insertMulti);

            FndAttachment condition = new FndAttachment();
            condition.setAttachmentId(insertAttach.getAttachmentId());
            condition.setSourcePkValue(insertMulti.getRecordId().toString());
            fndAttachmentMapper.updateByPrimaryKeySelective(condition);
        }
    }

    @Override
    public String imageSync(String decryptedStr) throws HlsCusException {
        ImageSyncDTO imageSyncDTO = JSONObject.parseObject(decryptedStr, ImageSyncDTO.class);
        JSONObject returnJson = new JSONObject();

        String orderNo = imageSyncDTO.getOrderNo();
        List<File> files = imageSyncDTO.getFiles();
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(orderNo);
        Long projectId = hlsCusPrjProject.getProjectId();
        for(File file : files){
            //step1 影像文件是否已上传
            String fileId = file.getFileId();
            UploadAttachList uploadAttachList = uploadAttachListMapper.selectByFileId(fileId);
            if(uploadAttachList == null){
                returnJson.put("success",false);
                returnJson.put("message","fileId[" + fileId + "]不存在");
                throw new HlsCusException(returnJson.toJSONString());
            }
            if(!"Y".equals(uploadAttachList.getUploadFlag())){
                returnJson.put("success",false);
                returnJson.put("message","fileId[" + fileId + "]影像文件未上传");
                throw new HlsCusException(returnJson.toJSONString());
            }
            //step2 根据文件类型，检查单据是否可以更新
            String fileType = file.getFileType();
            if("JY_FQ_XXCJSYSQ".equals(fileType)){
                //预审前
                String preStatus = hlsCusPrjProject.getPreStatus();
                if(StringUtils.isNotEmpty(preStatus) && !"NEW".equals(preStatus)){
                    returnJson.put("success",false);
                    returnJson.put("message","fileId[" + fileId + "],进件已完成预审，“个人信息采集及使用授权协议”不允许同步");
                    throw new HlsCusException(returnJson.toJSONString());
                }
            }else if("TRADE".equals(fileType) || "CAR_SERVICE".equals(fileType) || "CAR_HANDOVER_AND_PAY_CONFIRMx0".equals(fileType) || "LEASE".equals(fileType)
                    || "NOTICEx0".equals(fileType) || "OWNERSHIP_STATEMENT".equals(fileType) || "CONFIRM_PAYMENT_DELEGATION".equals(fileType) || "AUTHORIZATIONx0".equals(fileType)
                    || "MORTGAGE".equals(fileType) || "LICENSE_FRONT_IMGS".equals(fileType) || "DRIVEN_LICENSE_SUB".equals(fileType) || "REGISTRATION_CERTIFICATE".equals(fileType)
                    || "PERSON_AND_CAR".equals(fileType) || "LICENSE_AND_PICK_UP_IMG".equals(fileType) || "VEHICLE_CERTIFICATE".equals(fileType) || "INSURANCE_POLICYx0".equals(fileType)){
                //放款前
                String investmentStatus = hlsCusPrjProject.getInvestmentStatus();
                if(StringUtils.isNotEmpty(investmentStatus) && !"NEW".equals(investmentStatus) && !"REJECTED".equals(investmentStatus)){
                    returnJson.put("success",false);
                    returnJson.put("message","fileId[" + fileId + "],进件投放审查流程中/投放审查通过，“申请放款前相关材料”不允许同步");
                    throw new HlsCusException(returnJson.toJSONString());
                }
            }else if("REGISTRATION_CERTIFICATE_MORTGAGED".equals(fileType)){
                //抵押材料审核前
            }else if("ASSET_TRANSFER_AGREE".equals(fileType)){
                //结清后
            }
        }
        //step3 覆盖更新附件
        for(File file : files){
            String fileType = file.getFileType();
            HlsCusPrjProjectAttachment prjAttachment = hlsCusPrjProjectAttachmentMapper.selectAttachYl(projectId,fileType);
            if(prjAttachment == null){
                returnJson.put("success",false);
                returnJson.put("message","该进件的fileType[" + fileType + "]清单不存在！");
                throw new HlsCusException(returnJson.toJSONString());
            }
            this.replaceAttach(prjAttachment,file.getFileId());
        }
        //查询附件有合同相关的，就修改签约状态
        Integer ConAttachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, null,"'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(ConAttachMulti > 0){
            //修改签约状态
            hlsCusPrjProject.setSignStatus("SIGN");
            prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }
        returnJson.put("success",true);
        returnJson.put("message","成功");
        return JSONObject.toJSONString(returnJson);
    }

    /**
     * @Title: setCshTransaction
     * @Discription: 构建现金事务
     * @Param: [requestCtx, hlsCusCshTransaction]
     * @Return: com.hand.hls.csh.dto.HlsCusCshTransaction
     */
    private HlsCusCshTransaction setCshTransaction(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {

        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                "PAYMENT", "PAYMENT", null));
        hlsCusCshTransaction.setTransactionDate(getAfterMonth(new Date(),0));
        hlsCusCshTransaction.setPenaltyCalcDate(getAfterMonth(new Date(),0));
        hlsCusCshTransaction.setCompanyId(requestCtx.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");
//        hlsCusCshTransaction.setBankAccountId(deposit.getBankAccountId());
//        hlsCusCshTransaction.setBpBankAccountId(deposit.getBpBankAccountId());

        return hlsCusCshTransaction;
    }

    /*获取inputDate日期number个月之后的日期*/
    private static Date getAfterMonth(Date inputDate, int number) {
        Calendar c = Calendar.getInstance();//获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        c.setTime(inputDate);//设置日历时间
        c.add(Calendar.MONTH, number);//在日历的月份上增加6个月
        return c.getTime();
    }

    /**
     * @Title: setCshWriteOff
     * @Discription: 构建核销事务
     * @Param: [ln, cshWriteOff, cshTransactionId, writeOffType]
     * @Return: com.hand.hls.csh.dto.HlsCusCshWriteOff
     */
    private HlsCusCshWriteOff setCshWriteOff(HlsCusConContractCashflow ln, HlsCusCshWriteOff cshWriteOff, Long cshTransactionId, String writeOffType){

        cshWriteOff.setWriteOffType(writeOffType);
        cshWriteOff.setWriteOffDate(getAfterMonth(new Date(),0));
        cshWriteOff.setCshTransactionId(cshTransactionId);
        cshWriteOff.setReversedFlag("N");
        cshWriteOff.setCashflowId(ln.getCashflowId());
        cshWriteOff.setContractId(ln.getContractId());
        cshWriteOff.setTimes(ln.getTimes());
        cshWriteOff.setCfItem(ln.getCfItem());
        cshWriteOff.setCfType(ln.getCfType());
        cshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
        cshWriteOff.setImportFlag("N");
        cshWriteOff.setFirstLeasePayFlag("N");
        cshWriteOff.setCf_direction(ln.getCfDirection());
        cshWriteOff.setContractName(ln.getContractName());
        cshWriteOff.setContractNumber(ln.getContractNumber());
        cshWriteOff.setCalcDate(new Date());

        return cshWriteOff;
    }
    //更新现金流
    private void updateCashflow(IRequest requestCtx, HlsCusCshWriteOff cshWriteOff) {
        HlsCusConContractCashflow cashflow = conContractCashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        cashflow.setReceivedAmount(cshWriteOff.getWriteOffDueAmount());
        cashflow.setReceivedPrincipal(nvl(cshWriteOff.getWriteOffPrincipal(), 0.0));
        cashflow.setReceivedInterest(nvl(cshWriteOff.getWriteOffInterest(), 0.0));
        cashflow.setWriteOffFlag("FULL");
        cashflowService.updateByPrimaryKeySelective(requestCtx, cashflow);
    }

    //现金流数据查询
    private CalculationResultsDto  calculationResult(String orderNo,String trialTime,String type,Long cfItem,Date date) {
        CalculationResultsDto calculationResultsDto = new CalculationResultsDto();
        //根据订单编号和日期查询出到期日期
        Date dueDate = conContractCashflowMapper.queryDueDate(orderNo,trialTime);

        //查询出需要回购的现金流数据
        List<HlsCusConContractCashflow> queryUnReceivedByOrderNoList = conContractCashflowMapper.queryUnReceivedByOrderNo(orderNo,dueDate);
        if (ObjectUtils.isEmpty(queryUnReceivedByOrderNoList)){
            return null;
        }

        Double deductAmount = 0.00;  //抵扣金额
        Double payableAmount = 0.00;  //应付金额
        Double principal = 0.00; //总本金
        Double interest = 0.00; //总利息
        List<Integer> termNos = new ArrayList<>();  //期次信息
        List<Integer> deductNos = new ArrayList<>(); //抵扣期次
        List<HlsCusConContractCashflow> writeOffList = new ArrayList<>(); //需要自动核销为租金的代偿数据
        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
        for (HlsCusConContractCashflow c : queryUnReceivedByOrderNoList) {
            //回购不涉及到罚息金额
            if (c.getCfItem().equals(9L) && "REPO".equals(type)) {
                continue;
            }
            termNos.add(c.getTimes().intValue());
            payableAmount = payableAmount + (c.getDueAmount()-c.getReceivedAmount());
            principal = principal+ (c.getPrincipal()-c.getReceivedPrincipal());
            interest = interest + (c.getInterest()-c.getReceivedInterest());
            if (null != c.getPlanType() && "COMP".equals(c.getPlanType()) && c.getReceivedCompAmount()>0) {
                deductAmount = deductAmount + c.getReceivedCompAmount();
                payableAmount = payableAmount - c.getReceivedCompAmount();
                principal = principal - c.getPrincipal();
                interest = interest - c.getInterest();
                deductNos.add(c.getTimes().intValue());
                writeOffList.add(c);
            }
            if (c.getDueDate().equals(dueDate) && c.getCfItem().equals(1L)) {
                payableAmount = payableAmount+c.getOutstandingPrincipal();
                principal = principal+c.getOutstandingPrincipal();
                conContractCashflow.setContractId(c.getContractId());
                conContractCashflow.setQuotationId(c.getQuotationId());
                conContractCashflow.setCfItem(cfItem);
                conContractCashflow.setCfType(11L);
                conContractCashflow.setCfDirection(c.getCfDirection());
                conContractCashflow.setCfStatus("RELEASE");
                conContractCashflow.setTimes(c.getTimes());
            }

        }
        conContractCashflow.setDueDate(date);
        conContractCashflow.setCalcDate(date);
        conContractCashflow.setFinIncomeDate(date);
        conContractCashflow.setDueAmount(payableAmount);
        conContractCashflow.setPrincipal(principal);
        conContractCashflow.setInterest(interest);
        conContractCashflow.setOutstandingPrincipal(0.0);
        if ("REPO".equals(type)) {
            conContractCashflow.setPlanType("REPO");
        }
        calculationResultsDto.setPayableAmount(payableAmount);
        calculationResultsDto.setPrincipal(principal);
        calculationResultsDto.setInterest(interest);
        calculationResultsDto.setDeductAmount(deductAmount);
        calculationResultsDto.setTermNos(termNos);
        calculationResultsDto.setDeductNos(deductNos);
        calculationResultsDto.setWriteOffList(writeOffList);
        calculationResultsDto.setConContractCashflow(conContractCashflow);
        return calculationResultsDto;
    }

    /**
     * 进件投放审查工作流提交
     * @param iRequest 请求
     * @param project 进件投放审查申请数据
     * @param workFlowType 用于代码获取工作流提交的实现类
     */
    private void signWorkFlowSubmit(IRequest iRequest, HlsCusPrjProject project){
        List<HlsCusPrjProject> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        list.add(project);

        String bpName = prjProjectMapper.selectTenantNameByProject(project);
        map.put(IActivitiCommonService.WORK_FLOW_NAME, WORK_FLOW);
        map.put(IActivitiCommonService.DEMO_NAME, DEMO_NAME);
        map.put(IActivitiCommonService.BUSINESS_KEY, project.getProjectId());
        map.put("projectId", project.getProjectId());
        //单据类别
        map.put("documentCategory",DOCUMENT_CATEGORY);
        //单据类型
        map.put("documentType", DOCUMENT_TYPE);
        //单据名称
        map.put("documentName", DOCUMENT_NAME);
        //单据编号
        map.put("documentNumber", project.getProjectNumber());
        //设置工作流参数
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(project));
        map.put(PROJECT, jsonObject.toString());
        map.put(PROJECT_NAME, bpName);
        map.put(DOCUMENT_TYPE, "CON");
        map.put(DOCUMENT_ID, project.getProjectId());
        map.put("workFlowType", WORK_FLOW);
        //map.put(WORKFLOW_TYPE, workFlowType);
        //map.put(DOCUMENT_NAME, bpName);
        //map.put(DOCUMENT_NUMBER, project.getProjectNumber());
        //map.put(LEASE_CHANNEL, project.getLeaseChannel());
        //map.put("manufacturerId", project.getManufacturerId());
        /*iRequest.setUserId(Long.valueOf(10001));
        iRequest.setCompanyId(Long.valueOf(1));
        iRequest.setEmployeeCode("ADMIN");
        iRequest.setRoleId(Long.valueOf(10146));
        iRequest.setUserName("admin");
        iRequest.setEmployeeName("管理员");*/
        activitiStartService.start(iRequest, list, map);
    }

    private List<PrjProjectApproval> dateCheck(HlsCusPrjProject dto) throws HlsCusException {
        /**
         * 提交时校验
         */
        JSONObject returnJson = new JSONObject();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(dto.getProjectId());
        HlsCusPrjProject hlsCusPrjProjectList = prjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
        if ("APPROVING".equals(hlsCusPrjProjectList.getInvestmentStatus()) ) {
            returnJson.put("success",false);
            returnJson.put("message","已经提交了申请,无需重复提交!");
            throw new HlsCusException(returnJson.toJSONString());
        }

        return null;
    }

    /**
     * 插入分配相关表
     * @param transactionList
     * @param cc
     * @param iRequest
     */
    private void saveAllocation (List<HlsCusCshTransaction> transactionList,HlsCusConContractCashflow cc,IRequest iRequest) {
        CshAllocation cshAllocation = new CshAllocation();
        cshAllocation.setAllocationNumber(codingRuleValuesService.getCodeRuleValue(iRequest, "CSH_TRX",
                "ALLOCATION", "ALLOCATION", null));
        cshAllocation.setAllocationDate(new Date());
        cshAllocation.setAllocationSource("MANUAL");
        cshAllocation.setAllocationStatus("N");
        cshAllocationService.insertSelective(iRequest, cshAllocation);

        for (HlsCusCshTransaction transaction : transactionList) {
            CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
            cshAllocationReceipt.setAllocationId(cshAllocation.getAllocationId());
            cshAllocationReceipt.setTransactionId(transaction.getTransactionId());
            cshAllocationReceipt.setAdvanceReceiptAmount(transaction.getAdvanceReceiptAmount());
            cshAllocationReceiptService.insertSelective(iRequest, cshAllocationReceipt);
        }

        CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
        cshAllocationCredit.setAllocationId(cshAllocation.getAllocationId());
        cshAllocationCredit.setCashflowId(cc.getCashflowId());
        cshAllocationCredit.setDueAmount(cc.getDueAmount());
        cshAllocationCredit.setPrincipal(cc.getPrincipal());
        cshAllocationCredit.setInterest(cc.getInterest());
        cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
    }
    private String checkAttachMulti(Long projectId){
        Integer attachMulti = 0;
        StringBuilder message = new StringBuilder();
        //汽车买卖合同附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "TRADE","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《汽车买卖合同附件》、");
        }
        //车辆服务协议附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "CAR_SERVICE","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《车辆服务协议》、");
        }
        //汽车交付确认书附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "CAR_HANDOVER_AND_PAY_CONFIRM","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《汽车交付确认书》、");
        }
        //融资租赁合同附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "LEASE","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《融资租赁合同》、");
        }
        //客户告知函附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "NOTICE","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《客户告知函》、");
        }
        //租赁物所有权转移接受确认函附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "OWNERSHIP_STATEMENT","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《租赁物所有权转移接受确认函》、");
        }
        //委托付款确认书附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "CONFIRM_PAYMENT_DELEGATION","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《委托付款确认书》、");
        }
        //授权委托书（抵押物）附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "AUTHORIZATION","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《授权委托书（抵押物）》、");
        }
        //车辆抵押合同附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "MORTGAGE","'PRJ_PROJECT_ATTACHMENT'", "CONTRACT");
        if(attachMulti == 0){
            message.append("《车辆抵押合同》、");
        }
        //行驶证正面附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "LICENSE_FRONT_IMGS","'PRJ_PROJECT_ATTACHMENT'", "LOAN");
        if(attachMulti == 0){
            message.append("《行驶证正面》、");
        }
        //驾照主副页附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "DRIVEN_LICENSE_SUB","'PRJ_PROJECT_ATTACHMENT'", "LOAN");
        if(attachMulti == 0){
            message.append("《驾照主副页》、");
        }
        //登记证附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "REGISTRATION_CERTIFICATE","'PRJ_PROJECT_ATTACHMENT'", "LOAN");
        if(attachMulti == 0){
            message.append("《登记证》、");
        }
        //承租人、车辆、业务员合影附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "PERSON_AND_CAR","'PRJ_PROJECT_ATTACHMENT'", "LOAN");
        if(attachMulti == 0){
            message.append("《承租人、车辆、业务员合影》、");
        }
        //行驶证+车钥匙+身份证+前挡风玻璃vin码+提车确认单图片附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "LICENSE_AND_PICK_UP_IMG","'PRJ_PROJECT_ATTACHMENT'", "LOAN");
        if(attachMulti == 0){
            message.append("《行驶证+车钥匙+身份证+前挡风玻璃vin码+提车确认单图片》、");
        }
        //车辆合格证附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "VEHICLE_CERTIFICATE","'PRJ_PROJECT_ATTACHMENT'", "LOAN");
        if(attachMulti == 0){
            message.append("《车辆合格证》、");
        }
        //保险（车辆保险单）-支持多张,最多6张附件是否上传
        attachMulti = hlsCusPrjProjectAttachmentMapper.selectAttachMultiYlByCode(projectId, "INSURANCE_POLICY","'PRJ_PROJECT_ATTACHMENT'", "LOAN");
        if(attachMulti == 0){
            message.append("《保险（车辆保险单）-支持多张,最多6张》、");
        }
        return message.toString();
    }
}