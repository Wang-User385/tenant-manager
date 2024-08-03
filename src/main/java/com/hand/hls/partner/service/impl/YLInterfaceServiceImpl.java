package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterRoleMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
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
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
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
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsWsRequestsMapper hlsWsRequestsMapper;
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
    private IHlsWsRequestsService logService;
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
    private HlsCusConContractCashflowService contractCashflowService;
    @Autowired
    private ICshAllocationService cshAllocationService;
    @Autowired
    private ICshAllocationReceiptService cshAllocationReceiptService;
    @Autowired
    private ICshAllocationCreditService cshAllocationCreditService;
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

        //新增附件信息
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

        //step7: 返回信息
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

    @Override
    public String dataAcquisition(String decryptedStr,IRequest iRequest) throws HlsCusException {
        JSONObject jsonObject1 = new JSONObject();


        jsonObject1.put("code","200");
        jsonObject1.put("message","数据采集成功");
        return jsonObject1.toJSONString();
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
                hlsCusPrjProject.setPreStatus("APPROVED");
                prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
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
            }else if ("Reject".equals(s) || "Error".equals(s)){
                returnJson.put("code","400");
                returnJson.put("message","审核失败");
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
            List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.queryByProjectId(hlsCusPrjProject.getProjectId());
            if (hlsCusPrjProjectAttachments.size()==0){
                returnJson.put("code","400");
                returnJson.put("message","附件不能为空");
                throw new HlsCusException(returnJson.toJSONString());
            }
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
                    //再次发起投放审查流程前就不再校验是否在流程中了
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
        Boolean signFlag = false;
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
            //附件有合同相关的，就修改签约状态
            if(prjAttachment.getProjectAttachmentCategory().equals("CONTRACT")){
                signFlag = true;
            }
            this.replaceAttach(prjAttachment,file.getFileId());
        }
        if(signFlag){
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

    private List<PrjProjectApproval> dateCheck(HlsCusPrjProject dto) throws ResMessageException {
        /**
         * 提交时校验
         */
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(dto.getProjectId());
        HlsCusPrjProject hlsCusPrjProjectList = prjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
        if ("APPROVING".equals(hlsCusPrjProjectList.getInvestmentStatus()) ) {
            throw new ResMessageException("已经提交了申请,无需重复提交!");
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
}