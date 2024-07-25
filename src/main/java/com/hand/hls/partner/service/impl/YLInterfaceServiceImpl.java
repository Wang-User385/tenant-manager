package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterRoleMapper;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import com.hand.hls.bp.dto.HlsBpSpouse;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsBpSpouseMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.credit.service.TongDunService;
import com.hand.hls.csh.dto.CshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.mapper.UploadAttachListMapper;
import com.hand.hls.partner.service.YLInterfaceService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.web.logs.mapper.HlsWsRequestsMapper;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
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

    private static final HashMap<String, HashMap<String,String>> fileTypeMap = new HashMap<String, HashMap<String,String>>();
    private static void putData(String fileType,String documentName,String projectAttachmentCategory){
        HashMap<String, String> vMap = new HashMap<>();
        vMap.put("documentName", documentName);
        vMap.put("projectAttachmentCategory", projectAttachmentCategory);
        fileTypeMap.put(fileType,vMap);
    }
    static {
        putData("JY_FQ_XXCJSYSQ","个人信息采集及使用授权协议","EXAMINE");
        putData("TRADE","汽车买卖合同","CONTRACT");
        putData("CAR_SERVICE","车辆服务协议","CONTRACT");
        putData("CAR_HANDOVER_AND_PAY_CONFIRMx0","汽车交付确认书","CONTRACT");
        putData("LEASE","融资租赁合同","CONTRACT");
        putData("NOTICEx0","客户告知函","CONTRACT");
        putData("OWNERSHIP_STATEMENT","租赁物所有权转移接受确认函","CONTRACT");
        putData("CONFIRM_PAYMENT_DELEGATION","委托付款确认书","CONTRACT");
        putData("AUTHORIZATIONx0","授权委托书（抵押物）","CONTRACT");
        putData("MORTGAGE","车辆抵押合同","CONTRACT");
        putData("LICENSE_FRONT_IMGS","行驶证正面","CONTRACT");
        putData("DRIVEN_LICENSE_SUB","驾照主副页","CONTRACT");
        putData("REGISTRATION_CERTIFICATE","登记证","CONTRACT");
        putData("PERSON_AND_CAR","承租人、车辆、业务员合影","CONTRACT");
        putData("LICENSE_AND_PICK_UP_IMG","行驶证+车钥匙+身份证+前挡风玻璃vin码+提车确认单图片","CONTRACT");
        putData("VEHICLE_CERTIFICATE","车辆合格证","CONTRACT");
        putData("INSURANCE_POLICYx0","保险（车辆保险单）-支持多张，最多6张","CONTRACT");
        putData("REGISTRATION_CERTIFICATE_MORTGAGED","有抵押信息后的登记证（首页至空白页）","MORTGAGE");
        putData("ASSET_TRANSFER_AGREE","资产转让协议","");
    }
    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;

    @Override
    @Transactional
    public String placeOrder(String decryptedStr,IRequest iRequest) {

        PlaceOrderDTO placeOrderDTO = JSONObject.parseObject(decryptedStr, PlaceOrderDTO.class);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject jsonObject1 = new JSONObject();

        //判断该客户存不存在
        //如果存在，判断名称和电话一不一致，不一致就修改
        //如果不存在，就新增
        HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
        HlsBpMasterRole hlsBpMasterRole = null;
        HlsCusBpMaster bpMaster = null;;
        List<HlsCusBpMaster> bpMasters = hlsCusBpMasterMapper.selectMasterByIdCardNo(placeOrderDTO.getIdCardNo());
        if (bpMasters.size()==0){
            bpMaster = new HlsCusBpMaster();
            hlsBpMasterRole = new HlsBpMasterRole();
            Map<String, String> params = new HashMap<String, String>();
            String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "HLS_BP_MASTER", "NP", "NP", params);
            bpMaster.setBpCode(codeRuleValue);
            bpMaster.setBpName(placeOrderDTO.getName());
            bpMaster.setIdCardNo(placeOrderDTO.getIdCardNo());
            bpMaster.setPhone(placeOrderDTO.getMobile());
            Date idExpirationDate = null;
            Date idIssueDate = null;
            try {
                idExpirationDate = simpleDateFormat.parse(placeOrderDTO.getIdexp());
                idIssueDate = simpleDateFormat.parse(placeOrderDTO.getIdissue());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bpMaster.setIdIssueDate(idIssueDate);
            bpMaster.setIdExpirationDate(idExpirationDate);
            bpMaster.setCreationDate(new Date());
            String format = simpleDateFormat.format(new Date());
            bpMaster.setCreationDateStr(format);
            bpMaster.setCreatedBy(iRequest.getUserId());
            bpMaster.setBpCategory("TENANT");
            bpMaster.setBpType("TENANT");
            bpMaster.setSource("1");
            bpMaster.setBpClass("NP");
            bpMaster.setIdType("ID_CARD");
            hlsCusBpMasterMapper.insertSelective(bpMaster);
            hlsBpMasterRole.setBpId(bpMaster.getBpId());
            hlsBpMasterRole.setBpType("TENANT");
            hlsBpMasterRole.setBpCategory("TENANT");
            hlsBpMasterRole.setEnabledFlag("Y");
            hlsBpMasterRole.setPrimaryFlag("Y");
            hlsCusBpMasterRoleMapper.insertSelective(hlsBpMasterRole);

        }else{
            bpMaster = bpMasters.get(0);
            if (!placeOrderDTO.getName().equals(bpMaster.getBpName())){
                bpMaster.setBpName(placeOrderDTO.getName());
            }
            if (!placeOrderDTO.getMobile().equals(bpMaster.getPhone())){
                bpMaster.setPhone(placeOrderDTO.getMobile());
            }
            hlsCusBpMasterMapper.updateByPrimaryKeySelective(bpMaster);
            //设置flag判断是否有TENANT
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
        }

//        获取当前客户所有的项目，判断项目状态
        List<HlsCusPrjProject> list = prjProjectMapper.selectProjectByIdCardNo(placeOrderDTO.getIdCardNo());
        list.stream().forEach(x->{
//            如果项目是取消、拒绝、结束允许下单，否则不允许
            if (!"CLOSED".equals(x.getProjectStatus())||!"CANCEL".equals(x.getProjectStatus())||
                    !"REJECTED".equals(x.getProjectStatus())){
                jsonObject1.put("code","400");
                jsonObject1.put("message","存在在途单");
            }
        });
//        判断循环之后的结果，如果不允许创建，返回信息
        if ("400".equals(jsonObject1.getString("code"))){
            return jsonObject1.toJSONString();
        }

        //获取订单编号,将订单编号入库
        /*编码规则*/
        Map<String, String> params = new HashMap<String, String>();
        params.put("PARAMETER_01","YL");
        String codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(iRequest,"PRJ_PROJECT_IMPORT", "PRJLB", "LEASEBACK", params);

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectNumber(codeRuleValue);
        hlsCusPrjProject.setCompanyId(1L);
        hlsCusPrjProject.setTenantId(bpMaster.getBpId());
        hlsCusPrjProject.setProjectStatus("NEW");
        hlsCusPrjProject.setPreStatus("NEW");
        hlsCusPrjProject.setOrderStatus("START");
        //当前进件业务只有一家合作商，暂时只插入固定的这个合作商
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        //hlsCusBpMaster.setBpName("杭州易靓好车汽车服务有限公司");
        hlsCusBpMaster.setBpCode("BP202407230057");
        hlsCusBpMaster.setBpType("MANUFACTURER");
        List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.selectHlsBpMaster(hlsCusBpMaster);
        hlsCusPrjProject.setManufacturerId(hlsCusBpMasters.get(0).getBpId());
        //根据合作商id，查询产品定义表中的业务经理插入到项目表中
        HlsProductDefinition hlsProductDefinition = new HlsProductDefinition();
        hlsProductDefinition.setBpId(hlsCusBpMasters.get(0).getBpId());
        List<HlsProductDefinition> hlsProductDefinitionList = hlsProductDefinitionMapper.selectHlsProductDefinitionList(hlsProductDefinition);
        if (hlsProductDefinitionList.size() == 0){
            jsonObject1.put("code","400");
            jsonObject1.put("message","该合作商对应的产品为空，需在产品定义功能中维护新的产品");
            return jsonObject1.toJSONString();
        }
        hlsCusPrjProject.setEmployeeId(hlsProductDefinitionList.get(0).getEmployeeId());
        hlsCusPrjProject.setUnitId(hlsProductDefinitionList.get(0).getUnitId());
        hlsCusPrjProject.setLeaseItemType(hlsProductDefinitionList.get(0).getLeaseItemType());
        prjProjectMapper.insertSelective(hlsCusPrjProject);


        //创建关联人信息
        hlsCusPrjProjectBp.setBpId(bpMaster.getBpId());
        hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
        hlsCusPrjProjectBp.setBpCategroy("TENANT");
        hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);

        //        设置返回信息
        jsonObject1.put("code","200");
        jsonObject1.put("message","下单成功");
        jsonObject1.put("orderNo",codeRuleValue);
        return jsonObject1.toJSONString();
    }

    @Override
    @Transactional
    public String closeOrder(String decryptedStr){

        CloseOrderDTO closeOrderDTO = JSONObject.parseObject(decryptedStr, CloseOrderDTO.class);
        JSONObject jsonObject1 = new JSONObject();

        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(closeOrderDTO.getOrderNo());
        CshPaymentReqHd cshPaymentReqHd =prjProjectMapper.selectPaymentByOrderNo(closeOrderDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            jsonObject1.put("code","100003");
            jsonObject1.put("message","订单不存在");
            return jsonObject1.toJSONString();
        }else{
            String projectStatus = hlsCusPrjProject.getProjectStatus();
            if ("APPROVING".equals(projectStatus)||"Y".equals(hlsCusPrjProject.getLoanInitialLease())||
                    "APPROVING".equals(cshPaymentReqHd.getPaymentReqStatusDesc())){
                jsonObject1.put("code","100101");
                jsonObject1.put("message","订单状态和操作不相符");
                return jsonObject1.toJSONString();
            }
        }
        //修改订单状态
        hlsCusPrjProject.setProjectStatus("CLOSED");
        prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        jsonObject1.put("code","200");
        jsonObject1.put("message","取消成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String queryOrder(String decryptedStr){
        QueryOrderDTO queryOrderDTO = JSONObject.parseObject(decryptedStr, QueryOrderDTO.class);
        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号查询相对应的还款信息,判断订单存不存在，不存在直接返回
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(queryOrderDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            jsonObject1.put("code","100003");
            jsonObject1.put("message","订单不存在");
            return jsonObject1.toJSONString();
        }else{
            List<RepayPlanTermInfoDTO> repayPlanTermInfoDTOList = prjProjectMapper.selectRepayPlanByOrderNo(queryOrderDTO.getOrderNo());
            QueryOrder queryOrder = prjProjectMapper.selectQueryOrderByOrderNo(queryOrderDTO.getOrderNo());
            queryOrder.setRepayPlanTermInfoDTOList(repayPlanTermInfoDTOList);
            queryOrder.setStatus("NORMAL");
//            订单存在，判断合同状态是否为起租后状态、结清状态
//            如果是，则返回数据，如果不是，返回错误
            String contractStatus = prjProjectMapper.selectContractByOrderNo(queryOrderDTO.getOrderNo());
            if ("ET".equals(contractStatus)  || "INCEPT".equals(contractStatus)){
                jsonObject1.put("code","200");
                jsonObject1.put("message","查询成功");
                return jsonObject1.toJSONString();
            }else{
                jsonObject1.put("code","100101");
                jsonObject1.put("message","订单状态和操作不相符");
                return jsonObject1.toJSONString();
            }
        }
    }

    @Override
    @Transactional
    public String repayment(String decryptedStr){
        RepayMent repayMent = JSONObject.parseObject(decryptedStr, RepayMent.class);

        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号查询数据
        List<HlsCusCshTransaction> hlsCusCshTransactionList = prjProjectMapper.selectTranSactionByOrderNo(repayMent.getOrderNo());
        if (hlsCusCshTransactionList.size()==0){
            jsonObject1.put("code","400");
            jsonObject1.put("message","查询数据为空");
            return jsonObject1.toJSONString();
        }
        List<TermRepayDetailApplyDTO> termRepayDetailApplyDTOList = repayMent.getTermRepayDetailApplyDTOList();
        //保存数据到事务表
        for (TermRepayDetailApplyDTO termRepayDetailApplyDTO : termRepayDetailApplyDTOList) {
            //判断还款方式是否为蚂蚁链代扣，如果是则判断结算单号、代扣交易单号是否为空
            if ("蚂蚁链代扣".equals(repayMent.getRepayType())){
                if (termRepayDetailApplyDTO.getTransactionNo()==null){
                    jsonObject1.put("code","400");
                    jsonObject1.put("message","结算单号为空");
                    return jsonObject1.toJSONString();
                }
                if ("蚂蚁链代扣".equals(termRepayDetailApplyDTO.getExternalDeductNo())){
                    jsonObject1.put("code","400");
                    jsonObject1.put("message","代扣交易单号为空");
                    return jsonObject1.toJSONString();
                }
            }
//            将数据保存入库
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

        //设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","还款成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String compensatoryTrialCalculation(String decryptedStr){

        CompensatoryTrialCalculationDTO compensatoryTrialCalculationDTO = JSONObject.parseObject(decryptedStr, CompensatoryTrialCalculationDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(compensatoryTrialCalculationDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            jsonObject1.put("code","400");
            jsonObject1.put("message","订单不存在");
            return jsonObject1.toJSONString();
        }

//            计算本金、利息、罚息、应付金额
            CompensatoryTrialCalculationDTO compensatoryTrialCalculation1 = prjProjectMapper.selectCTCByOrderNo(compensatoryTrialCalculationDTO);

            if (compensatoryTrialCalculation1==null){
                jsonObject1.put("code","400");
                jsonObject1.put("message","数据不存在");
                return jsonObject1.toJSONString();
            }else{
                //            判断传入时间是否为空，如果为空则用现在时间，如果不为空，则用传入时间
                if (compensatoryTrialCalculationDTO.getTrialTime()==null){
                    compensatoryTrialCalculation1.setTrialTime(String.valueOf(new Date()));
                }else{
                    compensatoryTrialCalculation1.setTrialTime(compensatoryTrialCalculationDTO.getTrialTime());
                }
                //设置返回订单号
                compensatoryTrialCalculation1.setOrderNo(compensatoryTrialCalculationDTO.getOrderNo());
//            设置返回期次号
                compensatoryTrialCalculation1.setTermNo(compensatoryTrialCalculationDTO.getTermNo());
                //罚息暂时为0
                compensatoryTrialCalculation1.setPenalty(0L);
//            设置返回数据
                jsonObject1.put("code","200");
                jsonObject1.put("message","还款成功");
                jsonObject1.put("result",compensatoryTrialCalculation1);
                return jsonObject1.toJSONString();
            }
    }

    @Override
    @Transactional
    public String claimsSubrogation(String decryptedStr){
        ClaimsSubrogationDTO claimsSubrogationDTO = JSONObject.parseObject(decryptedStr, ClaimsSubrogationDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号查询数据
        //List<HlsCusCshTransaction> hlsCusCshTransactionList = prjProjectMapper.selectTranSactionByOrderNo(claimsSubrogationDTO.getOrderNo());

        //根据订单编号和期次获取需要代偿的现金流数据
        HlsCusConContractCashflow conContractCashflow = conContractCashflowMapper.queryClaimsSubrogation(claimsSubrogationDTO);

        if (!ObjectUtils.isEmpty(conContractCashflow)){
            jsonObject1.put("code","400");
            jsonObject1.put("message","代偿数据不存在");
            return jsonObject1.toJSONString();
       }

        //将代偿数据入库
        conContractCashflow.setPlanType("COMP");
        conContractCashflow.setDueCompAmount(Double.valueOf(claimsSubrogationDTO.getSubstituteAmount())/100);
        conContractCashflowMapper.updateByPrimaryKeySelective(conContractCashflow);
//            将数据保存入库
        /*for (HlsCusCshTransaction hlsCusCshTransaction : hlsCusCshTransactionList) {
            if (hlsCusCshTransaction.getTermNo().equals(claimsSubrogationDTO.getTermNo())){
                hlsCusCshTransaction.setRepayAmount(claimsSubrogationDTO.getSubstituteAmount());
                hlsCusCshTransaction.setPaymentMethod("代偿");
                hlsCusCshTransactionMapper.insertSelective(hlsCusCshTransaction);
            }
        }*/


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","还款成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String advancesSettleTrialCalculation(String decryptedStr){
        AdvancesSettleComputeDTO advancesSettleComputeDTO = JSONObject.parseObject(decryptedStr, AdvancesSettleComputeDTO.class);

        JSONObject jsonObject1 = new JSONObject();

//            查询结算金额


        AdvancesSettleComputeDTO advancesSettleComputeDTO1 = new AdvancesSettleComputeDTO();
        advancesSettleComputeDTO1.setOrderNo(advancesSettleComputeDTO.getOrderNo());
        //            判断试算日期是否为空，如果为空则使用当前日期，如果有，则使用传入日期
        if (advancesSettleComputeDTO.getTrialTime()==null){
            advancesSettleComputeDTO1.setTrialTime(String.valueOf(new Date()));
        }
        advancesSettleComputeDTO1.setTrialTime(advancesSettleComputeDTO.getTrialTime());
        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","试算成功");
        return jsonObject1.toJSONString();
    }

    @Override
    @Transactional
    public String advancesSettleRequest(String decryptedStr){

        AdvancesSettleRequestDTO advancesSettleRequestDTO = JSONObject.parseObject(decryptedStr, AdvancesSettleRequestDTO.class);


        JSONObject jsonObject1 = new JSONObject();

//            保存还款金额


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","还款成功");
        return jsonObject1.toJSONString();
    }

    @Override
    @Transactional
    public String dataAcquisition(String decryptedStr,IRequest iRequest) throws ParseException {
        DataAcquisitionDTO dataAcquisitionDTO = JSONObject.parseObject(decryptedStr, DataAcquisitionDTO.class);

        JSONObject jsonObject1 = new JSONObject();

//        首先判断订单状态
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(dataAcquisitionDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            jsonObject1.put("code","400");
            jsonObject1.put("message","订单不存在");
            return jsonObject1.toJSONString();
        }
        //根据项目id获取租赁物信息,如果为空，那么直接入库，如果不为空，判断订单状态
        List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectLeaseItemMapper.selectLeaseItemByProjectId(hlsCusPrjProject.getProjectId());

        //租赁物信息
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem1 = null;
        //融资方案相关信息
        FinanceInfo financeInfo = dataAcquisitionDTO.getFinanceInfo();
//            销售信息
        SaleInfo saleInfo = dataAcquisitionDTO.getSaleInfo();
//            风控审核相关数据
        String riskInfo = dataAcquisitionDTO.getRiskInfo();
        hlsCusPrjProject.setRiskInfo(riskInfo);
        PreRiskAuditData preRiskAuditData = null;
        if (riskInfo!=null){
            preRiskAuditData  = JSONObject.parseObject(riskInfo, PreRiskAuditData.class);
        }
        if(preRiskAuditData != null){
            hlsCusPrjProject.setFinanceAmount(Double.valueOf(preRiskAuditData.getFinancingamount())/100);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        CarInfo carInfo = dataAcquisitionDTO.getCarInfo();


        if (hlsCusPrjProjectLeaseItemList.size()>0){
            hlsCusPrjProjectLeaseItem1 = hlsCusPrjProjectLeaseItemList.get(0);
        }else{
            hlsCusPrjProjectLeaseItem1 = new HlsCusPrjProjectLeaseItem();
        }
        //项目id
        hlsCusPrjProjectLeaseItem1.setProjectId(hlsCusPrjProject.getProjectId());
//            车架号
        hlsCusPrjProjectLeaseItem1.setFrameNumber(carInfo.getVin());
//            车辆出厂日期
        try {
            Date productDate = simpleDateFormat.parse(carInfo.getCarProductionDate());
            hlsCusPrjProjectLeaseItem1.setProductDate(productDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//            发动机号
        hlsCusPrjProjectLeaseItem1.setEngineNumber(carInfo.getEngineNumber());
        //车辆指导价
        hlsCusPrjProjectLeaseItem1.setListPrice(financeInfo.getCarGuidePrice().doubleValue()/100);
//            车辆售价
        hlsCusPrjProjectLeaseItem1.setSellingPrice(financeInfo.getCarSalePrice().doubleValue()/100);
//            申请融资额
        hlsCusPrjProjectLeaseItem1.setFinanceAmount(financeInfo.getApplyLoanAmount().doubleValue()/100);

        //dataAcquisitionDTO;
        //车辆品牌
        hlsCusPrjProjectLeaseItem1.setBrandC(carInfo.getBrandName());
        //车系
        hlsCusPrjProjectLeaseItem1.setSeriesC(carInfo.getSeriesName());
        //车型
        hlsCusPrjProjectLeaseItem1.setModelC(carInfo.getModelName());
        //车辆颜色
        hlsCusPrjProjectLeaseItem1.setColorC(carInfo.getColor());
        prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
            hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKeySelective(hlsCusPrjProjectLeaseItem1);
        }else{
            hlsCusPrjProjectLeaseItemMapper.insertSelective(hlsCusPrjProjectLeaseItem1);
        }


        PrjProjectLeaseItemSales prjProjectLeaseItemSales = null;
        if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
            List<PrjProjectLeaseItemSales> prjProjectLeaseItemSales1 = projectLeaseItemSalesMapper.prjProjectLeaseItemSalesQuery(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
            if (prjProjectLeaseItemSales1.size()>0){
                prjProjectLeaseItemSales = prjProjectLeaseItemSales1.get(0);
            }else{
                prjProjectLeaseItemSales = new PrjProjectLeaseItemSales();
            }
        }else{
            prjProjectLeaseItemSales = new PrjProjectLeaseItemSales();
        }
//            销售方统一社会信用代码
        prjProjectLeaseItemSales.setUnifiedSocialCreditCode(saleInfo.getSellerCode());
//            销售方统一社会信用代码名称
        prjProjectLeaseItemSales.setSalesName(saleInfo.getSellerName());
//            上牌主体社会代码
        prjProjectLeaseItemSales.setRegisterSocialCreditCode(saleInfo.getLicensePlateOwnerCode());
//            上牌主体名称
        prjProjectLeaseItemSales.setRegisterName(saleInfo.getLicensePlateOwnerName());
//            抵押人社会代码
        prjProjectLeaseItemSales.setMortgageSocialCreditCode(saleInfo.getMortgagorCode());
//            抵押人名称
        prjProjectLeaseItemSales.setMortgageName(saleInfo.getMortgagorName());
//            抵押城市名称
        prjProjectLeaseItemSales.setMortgageCity(saleInfo.getMortgageCityName());
        prjProjectLeaseItemSales.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());


        //强制保险金额
        PrjLeaseItemInsurance prjLeaseItemInsurance = null;
        if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
            prjLeaseItemInsurance = prjLeaseItemInsuranceMapper.selectInsByLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
        }else{
            prjLeaseItemInsurance = new PrjLeaseItemInsurance();
        }
        if (prjLeaseItemInsurance==null){
            prjLeaseItemInsurance = new PrjLeaseItemInsurance();
        }
        prjLeaseItemInsurance.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
        double compulsoryAmount = carInfo.getMandatoryInsuranceAmount().doubleValue();
        prjLeaseItemInsurance.setCompulsoryAmount(compulsoryAmount / 100);
        //商业保险类型
        prjLeaseItemInsurance.setCommercialInsurance(carInfo.getCommercialInsuranceType());
        //延保金额
        prjLeaseItemInsurance.setExtendedWarrantyAmount(Double.valueOf(preRiskAuditData.getYanbaoje())/100);
        //车辆保险金额 clbxje
        prjLeaseItemInsurance.setInsuranceAmount(Double.valueOf(preRiskAuditData.getClbxje())/10);

        if (prjProjectLeaseItemSales.getSalesId()!=null){
            projectLeaseItemSalesMapper.updateByPrimaryKeySelective(prjProjectLeaseItemSales);
        }else{
            projectLeaseItemSalesMapper.insertSelective(prjProjectLeaseItemSales);
        }
        prjLeaseItemInsurance.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
        if (prjLeaseItemInsurance.getInsuranceId()!=null){
            prjLeaseItemInsuranceMapper.updateByPrimaryKeySelective(prjLeaseItemInsurance);
        }else{
            prjLeaseItemInsuranceMapper.insertSelective(prjLeaseItemInsurance);
        }

        String projectStatus = hlsCusPrjProject.getProjectStatus();
        //        如果订单状态为放款之后，不允许修改
        if ("放款之后".equals(projectStatus)){
            jsonObject1.put("code","400");
            jsonObject1.put("message","订单已放款，不允许修改");
            return jsonObject1.toJSONString();
        }
        //如果订单为业务申请之后，放款之前，对字段进行校验
        //品牌名称、车系名称、车型名称、车辆颜色
        if ("申请之后,放款之前".equals(projectStatus)){
            if (preRiskAuditData!=null){
                if (hlsCusPrjProjectLeaseItemList.size()>0){
                    //判断车系名称与riskInfo中的值是否相同
                    if (!hlsCusPrjProjectLeaseItem1.getBrandC().equals(preRiskAuditData.getCarbrand2())){
                        jsonObject1.put("code","400");
                        jsonObject1.put("message","品牌名称与riskInfo中的值不同");
                        return jsonObject1.toJSONString();
                    }
                    if (hlsCusPrjProjectLeaseItem1.getSeriesC().equals(preRiskAuditData.getChexi())){
                        jsonObject1.put("code","400");
                        jsonObject1.put("message","车系名称与riskInfo中的值不同");
                        return jsonObject1.toJSONString();
                    }
                    if (hlsCusPrjProjectLeaseItem1.getModelC().equals(preRiskAuditData.getCartype())){
                        jsonObject1.put("code","400");
                        jsonObject1.put("message","车型名称与riskInfo中的值不同");
                        return jsonObject1.toJSONString();
                    }
                    if (hlsCusPrjProjectLeaseItem1.getColorC().equals(preRiskAuditData.getCarcolor())){
                        jsonObject1.put("code","400");
                        jsonObject1.put("message","车辆颜色与riskInfo中的值不同");
                        return jsonObject1.toJSONString();
                    }
                }
            }
        }

        //获取租赁物相关信息，并入库
//            if ("申请之前".equals(projectStatus)){

                HlsCusPrjQuotation prjQuotation = null;
                if (hlsCusPrjProject.getProjectId()!=null){
                    List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.selectQuoByProjectId(hlsCusPrjProject.getProjectId());
                    if (hlsCusPrjQuotations.size()>0){
                        prjQuotation = hlsCusPrjQuotations.get(0);
                    }
                }
                if (prjQuotation==null){
                    prjQuotation = new HlsCusPrjQuotation();
                    prjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
                    //期次
                    prjQuotation.setLeaseTimes(Long.valueOf(financeInfo.getTermCount()));
//            月租
                    prjQuotation.setPmt(financeInfo.getMonthPayment().doubleValue()/100);
//            利率
                    prjQuotation.setIntRate(financeInfo.getRate().doubleValue());
//            首付款
                    prjQuotation.setDownPayment(financeInfo.getFirstPayment().doubleValue()/100);
                    //剩余车辆价款 (分)
                    prjQuotation.setSurplusAmount(financeInfo.getCarRestPrice().doubleValue()/100);
                    //融资金额
                    prjQuotation.setFinanceAmount(financeInfo.getApplyLoanAmount().doubleValue()/100);
                    //起息日
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    prjQuotation.setLeaseStartDate(formatter.parse(financeInfo.getStartRentDate()));
                    //最后一期还款日接口还没传

                }else{
                    if (!prjQuotation.getLeaseTimes().equals(Long.valueOf(financeInfo.getTermCount()))&&
                            !prjQuotation.getPmt().equals(financeInfo.getMonthPayment().doubleValue()/100)&&
                            !prjQuotation.getIntRate().equals(financeInfo.getRate().doubleValue()/100)&&
                            prjQuotation.getDownPayment().equals(financeInfo.getFirstPayment().doubleValue()/100) &&
                            prjQuotation.getSurplusAmount().equals(financeInfo.getCarRestPrice().doubleValue()/100)
                    ){
                        //期次
                        prjQuotation.setLeaseTimes(Long.valueOf(financeInfo.getTermCount()));
//            月租
                        prjQuotation.setPmt(financeInfo.getMonthPayment().doubleValue()/100);
//            利率
                        prjQuotation.setIntRate(financeInfo.getRate().doubleValue());
//            首付款
                        prjQuotation.setDownPayment(financeInfo.getFirstPayment().doubleValue()/100);
                        //剩余车辆价款 (分)
                        prjQuotation.setSurplusAmount(financeInfo.getCarRestPrice().doubleValue()/100);
                    }
                }


                //品牌
                hlsCusPrjProjectLeaseItem1.setBrandC(carInfo.getBrandName());
//            车系
                hlsCusPrjProjectLeaseItem1.setSeriesC(carInfo.getSeriesName());
//            车型
                hlsCusPrjProjectLeaseItem1.setModelC(carInfo.getModelName());
                //            车辆颜色
                hlsCusPrjProjectLeaseItem1.setColorC(carInfo.getColor());
                if (preRiskAuditData!=null){
                    //进件信息
                    hlsCusPrjProject.setDivision(preRiskAuditData.getProline());
                    
                    HlsCusBpMaster hlsCusBpMaster = hlsCusBpMasterMapper.selectByProjectId(hlsCusPrjProject.getProjectId());
                    //性别
                    hlsCusBpMaster.setGender(preRiskAuditData.getSex());
                    //民族
                    hlsCusBpMaster.setEthnicity(preRiskAuditData.getNation());
                    //出生日期
                    Date dateOfBirth = null;
                    try {
                        dateOfBirth = simpleDateFormat.parse(preRiskAuditData.getBirthdate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusBpMaster.setDateOfBirth(dateOfBirth);
                    //年龄
                    hlsCusBpMaster.setAge(Long.valueOf(preRiskAuditData.getAge()));
                    //国籍
                    hlsCusBpMaster.setNationality(preRiskAuditData.getNationality());
                    //户籍所属省份
                    hlsCusBpMaster.setDomicileProvince(preRiskAuditData.getDomicileshen());
                    //户籍所属市
                    hlsCusBpMaster.setDomicileCity(preRiskAuditData.getHomeaddresspcity());
                    //户籍所属区
                    hlsCusBpMaster.setDomicileDistrict(preRiskAuditData.getDomicilequ());
                    //户籍地址
                    hlsCusBpMaster.setDomicileAddress(preRiskAuditData.getDomicileaddress());
                    //是否本地户籍
                    hlsCusBpMaster.setDomicileLocalFlag(preRiskAuditData.getIslocaldomicile());
                    //签发机关
                    hlsCusBpMaster.setIdIssueOrgan(preRiskAuditData.getIssuegov());
                    //是否长期有效
                    hlsCusBpMaster.setIdLongTerm(preRiskAuditData.getIsenable());
                    //居住地址省
                    hlsCusBpMaster.setHouseProvince(preRiskAuditData.getHomeaddressprovince());
                    //居住地址市
                    hlsCusBpMaster.setHouseCity(preRiskAuditData.getHomeaddresspcity());
                    //居住地址区县
                    hlsCusBpMaster.setHouseDistrict(preRiskAuditData.getJzdzqx());
                    //居住地址
                    hlsCusBpMaster.setHouseAddress(preRiskAuditData.getHomeaddress());
                    //房产类型
                    hlsCusBpMaster.setHouseType(preRiskAuditData.getHousetype());
                    //婚姻状况
                    hlsCusBpMaster.setMaritalStatus(preRiskAuditData.getMarriage());
                    //子女人数
                    hlsCusBpMaster.setNumberOfChildren(Long.valueOf(preRiskAuditData.getChildnum()));
                    //有无驾照
                    hlsCusBpMaster.setDriverLicenseFlag(preRiskAuditData.getIsdriverlicence());
                    //驾照类型
                    hlsCusBpMaster.setDriverLicenseType(preRiskAuditData.getDriverlicencetype());
                    //驾照状态
                    hlsCusBpMaster.setDriverLicenseStatus(preRiskAuditData.getDriverstatus());
                    //驾照截止日期
                    Date driverLicenseDeadline = null;
                    try {
                        driverLicenseDeadline = simpleDateFormat.parse(preRiskAuditData.getJzjzrq());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusBpMaster.setDriverLicenseDeadline(driverLicenseDeadline);
                    //违章分数
                    hlsCusBpMaster.setViolationScore(Long.valueOf(preRiskAuditData.getWzfs()));
                    //违章罚款
                    hlsCusBpMaster.setViolationFines(Long.valueOf(preRiskAuditData.getWzfk()));
                    //学历
                    hlsCusBpMaster.setHighestDegree(preRiskAuditData.getDiploma());
                    //单位名称
                    hlsCusBpMaster.setWorkingCompany(preRiskAuditData.getCompany());
                    //所属行业
                    hlsCusBpMaster.setEconomicInduClassify(preRiskAuditData.getIndustry());
                    //单位性质
                    hlsCusBpMaster.setJobNature(preRiskAuditData.getCorpnprop());
                    //职业
                    hlsCusBpMaster.setProfession(preRiskAuditData.getOccu());
                    //当前职位
                    hlsCusBpMaster.setPosition(preRiskAuditData.getPosition());
                    //个人月收入
                    hlsCusBpMaster.setAnnualIncome(Double.valueOf(preRiskAuditData.getSalary())/100);
                    //单位电话
                    hlsCusBpMaster.setWorkPhone(preRiskAuditData.getCompanyphone());
                    //公司所属省份
                    hlsCusBpMaster.setCompanyProvince(preRiskAuditData.getCompanyshen());
                    //公司所属市
                    hlsCusBpMaster.setCompanyCity(preRiskAuditData.getCompanyshi());
                    //公司所属区
                    hlsCusBpMaster.setCompanyDistrict(preRiskAuditData.getCompanyqu());
                    //公司地址
                    hlsCusBpMaster.setCompanyAddress(preRiskAuditData.getCompaddr());


                    HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = null;
                    if (hlsCusBpMaster.getBpId()!=null){
                        hlsCusBpMasterBankAccount = hlsCusBpMasterBankAccountMapper.selectBankByBpId(hlsCusBpMaster.getBpId());
                    }
                    if (hlsCusBpMasterBankAccount==null){
                        hlsCusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
                    }
                    //银行卡号
                    hlsCusBpMasterBankAccount.setBankAccountNum(preRiskAuditData.getCardno());
                    //实际驾驶人与申请人关系
                    hlsCusPrjProject.setDriverAndApplicant(preRiskAuditData.getSjjsrysqrgx());
                    //有无担保人
                    if ("1".equals(preRiskAuditData.getIssureor())){
                        //首先判断数据库有没有该担保人
                        HlsCusBpMaster hlsCusBpMaster1 = null;
                        List<HlsCusBpMaster> hlsCusBpMaster1s = hlsCusBpMasterMapper.selectMasterByIdCardNo(preRiskAuditData.getSureid());

                        if (hlsCusBpMaster1s.size()==0){
                            HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                            hlsCusBpMaster1 = new HlsCusBpMaster();
                            //担保人姓名
                            hlsCusBpMaster1.setBpName(preRiskAuditData.getSurename());
                            //担保人证件类型
                            hlsCusBpMaster1.setIdType(preRiskAuditData.getSurecertype());
                            //担保人身份证
                            hlsCusBpMaster1.setIdCardNo(preRiskAuditData.getSureid());
                            //担保人手机
                            hlsCusBpMaster1.setPhone(preRiskAuditData.getSuremobi());
                            hlsCusBpMaster1.setCreationDate(new Date());
                            String format = simpleDateFormat.format(new Date());
                            hlsCusBpMaster1.setCreationDateStr(format);
                            //创建人
                            hlsCusBpMaster1.setCreatedBy(iRequest.getUserId());
                            //来源
                            hlsCusBpMaster1.setSource("1");
                            hlsCusBpMaster1.setBpClass("NP");
                            hlsCusBpMaster1.setBpCategory("GUARANTOR");
                            hlsCusBpMasterMapper.insertSelective(hlsCusBpMaster1);
                            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                            hlsCusPrjProjectBp.setBpId(hlsCusBpMaster1.getBpId());
                            hlsCusPrjProjectBp.setRefV02("GUARANTOR");
                            hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);
                        }else{
                            hlsCusBpMaster1 = hlsCusBpMaster1s.get(0);
                            HlsCusPrjProjectBp hlsCusPrjProjectBp = hlsCusPrjProjectBpMapper.selectProjectBpByBpId(hlsCusBpMaster1.getBpId());
                            //担保人姓名
                            hlsCusBpMaster1.setBpName(preRiskAuditData.getSurename());
                            //担保人证件类型
                            hlsCusBpMaster1.setIdType(preRiskAuditData.getSurecertype());
                            //担保人身份证
                            hlsCusBpMaster1.setIdCardNo(preRiskAuditData.getSureid());
                            //担保人手机
                            hlsCusBpMaster1.setPhone(preRiskAuditData.getSuremobi());
                            hlsCusBpMaster1.setLastUpdatedBy(iRequest.getUserId());
                            hlsCusBpMaster1.setLastUpdateDate(new Date());
                            //来源
                            hlsCusBpMaster1.setSource("1");
                            hlsCusBpMaster1.setBpClass("NP");
                            hlsCusBpMaster1.setBpCategory("GUARANTOR");
                            hlsCusBpMasterMapper.updateByPrimaryKeySelective(hlsCusBpMaster1);
                            if (hlsCusPrjProjectBp==null){
                                hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                                hlsCusPrjProjectBp.setBpId(hlsCusBpMaster1.getBpId());
                                hlsCusPrjProjectBp.setRefV02("GUARANTOR");
                                hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);
                            }else{
                                hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                                hlsCusPrjProjectBp.setBpId(hlsCusBpMaster1.getBpId());
                                hlsCusPrjProjectBp.setRefV02("GUARANTOR");
                                hlsCusPrjProjectBpMapper.updateByPrimaryKeySelective(hlsCusPrjProjectBp);
                            }
                        }
                    }
                    //有无共同承租人
                    if ("1".equals(preRiskAuditData.getIscop())){
                        HlsCusBpMaster hlsCusBpMaster2 = null;
                        List<HlsCusBpMaster> hlsCusBpMaster2s = hlsCusBpMasterMapper.selectMasterByIdCardNo(preRiskAuditData.getCoid());
                        if (hlsCusBpMaster2s.size()==0){
                            HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                            hlsCusBpMaster2 = new HlsCusBpMaster();
                            //共同借款人姓名
                            hlsCusBpMaster2.setBpName(preRiskAuditData.getConame());
                            //共同承租人证件类型
                            hlsCusBpMaster2.setIdType(preRiskAuditData.getCocerttype());
                            //共同借款人身份证
                            hlsCusBpMaster2.setIdCardNo(preRiskAuditData.getCoid());
                            //共同借款人手机
                            hlsCusBpMaster2.setPhone(preRiskAuditData.getComobile());
                            //共同承租人居住地址
                            hlsCusBpMaster2.setHouseAddress(preRiskAuditData.getCoaddr());
                            //共同借款人工作单位
                            hlsCusBpMaster2.setWorkingCompany(preRiskAuditData.getCocompany());
                            //共同承租人公司电话
                            hlsCusBpMaster2.setWorkPhone(preRiskAuditData.getCocomtel());
                            //共同借款人公司地址
                            hlsCusBpMaster2.setCompanyAddress(preRiskAuditData.getCocomaddr());
                            hlsCusBpMaster2.setCreationDate(new Date());
                            String format = simpleDateFormat.format(new Date());
                            hlsCusBpMaster2.setCreationDateStr(format);
                            //创建人
                            hlsCusBpMaster2.setCreatedBy(iRequest.getUserId());
                            //来源
                            hlsCusBpMaster2.setSource("1");
                            hlsCusBpMaster2.setBpClass("NP");
                            hlsCusBpMaster2.setBpCategory("TENANT-SEC");
                            hlsCusBpMasterMapper.insertSelective(hlsCusBpMaster2);
                            hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                            hlsCusPrjProjectBp.setBpId(hlsCusBpMaster2.getBpId());
                            hlsCusPrjProjectBp.setRefV02("TENANT-SEC");
                            hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);
                        }else{
                            hlsCusBpMaster2 = hlsCusBpMaster2s.get(0);
                            HlsCusPrjProjectBp hlsCusPrjProjectBp = hlsCusPrjProjectBpMapper.selectProjectBpByBpId(hlsCusBpMaster2.getBpId());
                            //共同借款人姓名
                            hlsCusBpMaster2.setBpName(preRiskAuditData.getConame());
                            //共同承租人证件类型
                            hlsCusBpMaster2.setIdType(preRiskAuditData.getCocerttype());
                            //共同借款人身份证
                            hlsCusBpMaster2.setIdCardNo(preRiskAuditData.getCoid());
                            //共同借款人手机
                            hlsCusBpMaster2.setPhone(preRiskAuditData.getComobile());
                            //共同承租人居住地址
                            hlsCusBpMaster2.setHouseAddress(preRiskAuditData.getCoaddr());
                            //共同借款人工作单位
                            hlsCusBpMaster2.setWorkingCompany(preRiskAuditData.getCocompany());
                            //共同承租人公司电话
                            hlsCusBpMaster2.setWorkPhone(preRiskAuditData.getCocomtel());
                            //共同借款人公司地址
                            hlsCusBpMaster2.setCompanyAddress(preRiskAuditData.getCocomaddr());
                            hlsCusBpMaster2.setLastUpdatedBy(iRequest.getUserId());
                            hlsCusBpMaster2.setLastUpdateDate(new Date());
                            //来源
                            hlsCusBpMaster2.setSource("1");
                            hlsCusBpMaster2.setBpClass("NP");
                            hlsCusBpMaster2.setBpCategory("TENANT-SEC");
                            hlsCusBpMasterMapper.updateByPrimaryKeySelective(hlsCusBpMaster2);
                            if (hlsCusPrjProjectBp==null){
                                hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                                hlsCusPrjProjectBp.setBpId(hlsCusBpMaster2.getBpId());
                                hlsCusPrjProjectBp.setRefV02("TENANT-SEC");
                                hlsCusPrjProjectBpMapper.insertSelective(hlsCusPrjProjectBp);
                            }else{
                                hlsCusPrjProjectBp.setProjectId(hlsCusPrjProject.getProjectId());
                                hlsCusPrjProjectBp.setBpId(hlsCusBpMaster2.getBpId());
                                hlsCusPrjProjectBp.setRefV02("TENANT-SEC");
                                hlsCusPrjProjectBpMapper.updateByPrimaryKeySelective(hlsCusPrjProjectBp);
                            }
                        }
                    }
                    List<HlsBpSpouse> hlsBpSpouseList = hlsBpSpouseMapper.selectByBpId(hlsCusBpMaster.getBpId());
                    if (hlsBpSpouseList.size()==0){
                        //联系人
                        HlsBpSpouse hlsBpSpouse = new HlsBpSpouse();
                        //联系人与承租人关系
                        hlsBpSpouse.setRelationship(preRiskAuditData.getContreleship());
                        //联系人姓名
                        hlsBpSpouse.setPersonName(preRiskAuditData.getContname());
                        //联系人当前居住地址
                        hlsBpSpouse.setAddress(preRiskAuditData.getContaddr());
                        //联系人移动电话
                        hlsBpSpouse.setCellPhone(preRiskAuditData.getPartymobile());
                        hlsBpSpouse.setBpId(hlsCusBpMaster.getBpId());
                        hlsBpSpouseMapper.insertSelective(hlsBpSpouse);
                    }else{
                        HlsBpSpouse hlsBpSpouse = hlsBpSpouseList.get(0);
                        //联系人与承租人关系
                        hlsBpSpouse.setRelationship(preRiskAuditData.getContreleship());
                        //联系人姓名
                        hlsBpSpouse.setPersonName(preRiskAuditData.getContname());
                        //联系人当前居住地址
                        hlsBpSpouse.setAddress(preRiskAuditData.getContaddr());
                        //联系人移动电话
                        hlsBpSpouse.setCellPhone(preRiskAuditData.getPartymobile());
                        hlsBpSpouse.setBpId(hlsCusBpMaster.getBpId());
                        hlsBpSpouseMapper.updateByPrimaryKeySelective(hlsBpSpouse);
                    }
                    //配偶姓名
                    hlsCusBpMaster.setBpNameSp(preRiskAuditData.getSpousename());
//            配偶性别
                    hlsCusBpMaster.setGenderSp(preRiskAuditData.getSpousesex());
                    //配偶出生日期
                    Date dateOfBirthSp = null;
                    try {
                        dateOfBirthSp = simpleDateFormat.parse(preRiskAuditData.getSpousebir());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusBpMaster.setDateOfBirthSp(dateOfBirthSp);
                    //配偶证件号码
                    hlsCusBpMaster.setIdCardNoSp(preRiskAuditData.getSpouseidcard());
                    //配偶单位地址
                    hlsCusBpMaster.setAddressSp(preRiskAuditData.getSpoucecompaddr());
                    //配偶联系电话
                    hlsCusBpMaster.setSpousePhone(Long.valueOf(preRiskAuditData.getSpousephone()));
                    //配偶公司名称
                    hlsCusBpMaster.setSpouseJobsUnit(preRiskAuditData.getSpousecomp());
                    //配偶公司所属行业
                    hlsCusBpMaster.setInduClassifySp(preRiskAuditData.getSpousecompind());
                    //配偶公司性质
                    hlsCusBpMaster.setCompanyNatureSp(preRiskAuditData.getSpousecomptype());
                    //配偶职业类型
                    hlsCusBpMaster.setProfessionSp(preRiskAuditData.getSpousezylx());
                    //配偶职位
                    hlsCusBpMaster.setJobTitleSp(preRiskAuditData.getSpousezw());

                    //制造商
                    hlsCusPrjProjectLeaseItem1.setManufacturer(preRiskAuditData.getCarfac());
                    //车辆类型
                    hlsCusPrjProjectLeaseItem1.setVoitureType(preRiskAuditData.getVehicletype());
                    //车辆准载(定员)
                    hlsCusPrjProjectLeaseItem1.setVehicleCapacity(preRiskAuditData.getCarzkcount());
                    //是否进口
                    hlsCusPrjProjectLeaseItem1.setIsImport(preRiskAuditData.getSfjk());
                    //燃料类型
                    hlsCusPrjProjectLeaseItem1.setFuelType(preRiskAuditData.getRllx());
                    //车辆评估价格
                    hlsCusPrjProjectLeaseItem1.setEvaluationValue(Double.valueOf(preRiskAuditData.getClpgjg()));
                    //首次登记日期
                    //转让登记日期
                    Date firstRegistrationDate = null;
                    Date transferRegistrationDate = null;
                    try {
                        firstRegistrationDate = simpleDateFormat.parse(preRiskAuditData.getScdjrq());
                        transferRegistrationDate = simpleDateFormat.parse(preRiskAuditData.getTransferencedate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusPrjProjectLeaseItem1.setFirstRegistrationDate(firstRegistrationDate);
                    hlsCusPrjProjectLeaseItem1.setTransferRegistrationDate(transferRegistrationDate);
                    //车牌号
                    hlsCusPrjProjectLeaseItem1.setLicensePlateNumber(preRiskAuditData.getChepaihao());
                    //车辆使用性质
                    hlsCusPrjProjectLeaseItem1.setNatureOfVehicle(preRiskAuditData.getCarnatureofuse());
//            上牌城市
                    hlsCusPrjProjectLeaseItem1.setCityCode(preRiskAuditData.getRegisteredcity());
                    //首次上牌日
                    Date firstPlateDate = null;
                    try {
                        firstPlateDate = simpleDateFormat.parse(preRiskAuditData.getScspr());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hlsCusPrjProjectLeaseItem1.setFirstPlateDate(firstPlateDate);
                    //表显里程
                    hlsCusPrjProjectLeaseItem1.setOdometerReading(Integer.valueOf(preRiskAuditData.getBxlc()));
                    //车辆年限
                    hlsCusPrjProjectLeaseItem1.setVehicleAge(Integer.valueOf(preRiskAuditData.getCarlife()));
                    //车辆所有人
                    hlsCusPrjProjectLeaseItem1.setPropPerson(preRiskAuditData.getCaraffiliation());
                    //租赁物融资金额
                    hlsCusPrjProjectLeaseItem1.setLeaseItemAmount(Double.valueOf(preRiskAuditData.getFinancingamount())/100);

                    PrjProjectLeaseItemMortgage prjProjectLeaseItemMortgage = null;
                    if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
                        List<PrjProjectLeaseItemMortgage> prjProjectLeaseItemMortgages = projectLeaseItemMortgageMapper.prjProjectLeaseItemMortgageByLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                        if (prjProjectLeaseItemMortgages.size()>0){
                            prjProjectLeaseItemMortgage = prjProjectLeaseItemMortgages.get(0);
                        }
                    }
                    if (prjProjectLeaseItemMortgage==null){
                        prjProjectLeaseItemMortgage = new PrjProjectLeaseItemMortgage();
                    }
                    //抵押次数
                    prjProjectLeaseItemMortgage.setNumberOfMortgages(Integer.valueOf(preRiskAuditData.getDycs()));
                    //过户次数
                    prjProjectLeaseItemMortgage.setNumberOfTransfers(Integer.valueOf(preRiskAuditData.getGhjcs()));
                    //近1年抵押次数
                    prjProjectLeaseItemMortgage.setNumberOfMortgagesOne(Integer.valueOf(preRiskAuditData.getJyndics()));
                    //近1年过户次数
                    prjProjectLeaseItemMortgage.setNumberOfTransfersOne(Integer.valueOf(preRiskAuditData.getLast1yearguohucount()));
                    //近2年过户次数
                    prjProjectLeaseItemMortgage.setNumberOfTransfersTwo(Integer.valueOf(preRiskAuditData.getLast2yearguohucount()));
                    //是否有车辆登记证补领记录
                    prjProjectLeaseItemMortgage.setIsRenewalRecord(preRiskAuditData.getIsregister());
                    //近半年是否有车辆登记证补领记录
                    prjProjectLeaseItemMortgage.setIsHalfRenewalRecord(preRiskAuditData.getIsregisterhy());
                    //上一次抵押登记日期
                    //最近一次解押日期
                    Date lastTransfersDate = null;
                    Date recentlyTransfersDate = null;
                    try {
                        lastTransfersDate = simpleDateFormat.parse(preRiskAuditData.getLastmortgagedate());
                        recentlyTransfersDate = simpleDateFormat.parse(preRiskAuditData.getLastdtecompressiondate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    prjProjectLeaseItemMortgage.setLastTransfersDate(lastTransfersDate);
                    prjProjectLeaseItemMortgage.setRecentlyTransfersDate(recentlyTransfersDate);
                    //抵押状态
                    prjProjectLeaseItemMortgage.setTransfersStatus(preRiskAuditData.getMortgagestatus());
                    //解押天数
                    prjProjectLeaseItemMortgage.setDaysToRelease(Integer.valueOf(preRiskAuditData.getJyts()));

                    //是否有交强险
                    prjLeaseItemInsurance.setIsCompulsoryInsurance(preRiskAuditData.getSfyjqx());
                    //交强险到期日期
                    Date compulsoryEndDate = null;
                    //车损险到期日期
                    Date vehicleEndDate = null;
                    //第三者责任险到期日期
                    Date thirdEndDate = null;
                    try {
                        compulsoryEndDate = simpleDateFormat.parse(preRiskAuditData.getJqxdqrq());
                        vehicleEndDate = simpleDateFormat.parse(preRiskAuditData.getCsxdqrq());
                        thirdEndDate = simpleDateFormat.parse(preRiskAuditData.getSzxdqrq());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    prjLeaseItemInsurance.setCompulsoryEndDate(compulsoryEndDate);
                    //是否有车损险
                    prjLeaseItemInsurance.setIsVehicleDamage(preRiskAuditData.getSfycsx());

                    prjLeaseItemInsurance.setVehicleEndDate(vehicleEndDate);
                    //是否有第三者责任险
                    prjLeaseItemInsurance.setIsThirdParty(preRiskAuditData.getSfyszx());
                    prjLeaseItemInsurance.setThirdEndDate(thirdEndDate);


                    PrjProjectLeaseItemCondition prjProjectLeaseItemCondition = null;
                    if (hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId()!=null){
                        List<PrjProjectLeaseItemCondition> prjProjectLeaseItemConditions = projectLeaseItemConditionMapper.prjProjectLeaseItemConditionByLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                        if (prjProjectLeaseItemConditions.size()>0){
                            prjProjectLeaseItemCondition = prjProjectLeaseItemConditions.get(0);
                        }
                    }
                    if (prjProjectLeaseItemCondition==null){
                        prjProjectLeaseItemCondition = new PrjProjectLeaseItemCondition();
                    }
                    //车况信息
                    //是否年检
                    prjProjectLeaseItemCondition.setIsAnnualInspection(preRiskAuditData.getIscheckyear());
                    //是否安装GPS
                    hlsCusPrjProjectLeaseItem1.setGpsIsInstallation(preRiskAuditData.getSfazgps());
                    //贷款用途
                    hlsCusPrjProjectLeaseItem1.setLoanPurpose(preRiskAuditData.getUsage());
                    //上牌类型
                    hlsCusPrjProjectLeaseItem1.setPlateType(preRiskAuditData.getSptype());
                    //牌照归属
                    hlsCusPrjProjectLeaseItem1.setLicensePlateOwnership(preRiskAuditData.getPaizhaogs());
                    //车辆交易价
                    hlsCusPrjProjectLeaseItem1.setPrice(Double.valueOf(preRiskAuditData.getCljyjg())/100);
                    //购置税
                    hlsCusPrjProjectLeaseItem1.setPurchaseTax(Double.valueOf(preRiskAuditData.getGouzhis())/100);
                    //车辆保险金额
                    hlsCusPrjProjectLeaseItem1.setInsurancePremium(Double.valueOf(preRiskAuditData.getClbxje())/100);
                    //GPS费用
                    hlsCusPrjProjectLeaseItem1.setGpsFee(Double.valueOf(preRiskAuditData.getGpsfy())/100);
                    //上牌发票金额
                    hlsCusPrjProjectLeaseItem1.setPlateInvoiceAmount(Double.valueOf(preRiskAuditData.getCtac())/100);
                    //装饰品金额
                    hlsCusPrjProjectLeaseItem1.setAccessoryAmount(Double.valueOf(preRiskAuditData.getZspje())/100);
                    //车辆税额
                    hlsCusPrjProjectLeaseItem1.setVehicleTax(Double.valueOf(preRiskAuditData.getCheliangse())/100);
                    //是否水泡
                    prjProjectLeaseItemCondition.setIsWaterDamaged(preRiskAuditData.getSfsp());
                    //是否营转非
                    prjProjectLeaseItemCondition.setIsConversion(preRiskAuditData.getSfyzf());
                    //事故状况
                    prjProjectLeaseItemCondition.setAccidentStatus(preRiskAuditData.getSgzk());
                    //发动机大修
                    prjProjectLeaseItemCondition.setEngineOverhaul(preRiskAuditData.getFdjdx());
                    //是否重大改装车
                    prjProjectLeaseItemCondition.setIsSignificantlyModified(preRiskAuditData.getSfzdgzc());
                    //原车主证件类型
                    prjProjectLeaseItemCondition.setOriginalOwnerCardType(preRiskAuditData.getYuanchezzjlx());
                    //原车主姓名
                    prjProjectLeaseItemCondition.setOriginalOwnerName(preRiskAuditData.getYuanczxm());
                    //原车主证件号
                    prjProjectLeaseItemCondition.setOriginalOwnerCardNum(preRiskAuditData.getYuanchezzjhm());
                    //原车主户籍所在地址
                    prjProjectLeaseItemCondition.setOriginalOwnerAddress(
                            preRiskAuditData.getYuanchezhuhujishengfen()
                                    +preRiskAuditData.getYuanchezhuhujishi()+preRiskAuditData.getCarownersdomicilelast());
                    //维修保养情况
                    prjProjectLeaseItemCondition.setMaintenanceInfo(preRiskAuditData.getBywxqk());
                    //精品加装
                    prjProjectLeaseItemCondition.setPremiumAddOn(preRiskAuditData.getJpjz());

                    //首付比例
                    prjQuotation.setDownPaymentRatio(Double.valueOf(preRiskAuditData.getPaymentratio()));
                    //尾款比例

                    prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
                    prjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
                    if (prjQuotation.getQuotationId()!=null){
                        hlsCusPrjQuotationMapper.updateByPrimaryKeySelective(prjQuotation);
                    }else{
                        hlsCusPrjQuotationMapper.insertSelective(prjQuotation);
                    }

                    Example example = new Example(HlsCusPrjProjectLeaseItem.class);
                    example.createCriteria().andEqualTo("projectLeaseItemId",hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    hlsCusPrjProjectLeaseItemMapper.updateByExample(hlsCusPrjProjectLeaseItem1,example);
//                    hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKeySelective(hlsCusPrjProjectLeaseItem1);
                    prjProjectLeaseItemMortgage.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjProjectLeaseItemMortgage.getMortgageId()!=null){
                        projectLeaseItemMortgageMapper.updateByPrimaryKeySelective(prjProjectLeaseItemMortgage);
                    }else{
                        projectLeaseItemMortgageMapper.insertSelective(prjProjectLeaseItemMortgage);
                    }
                    prjProjectLeaseItemSales.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjProjectLeaseItemSales.getSalesId()!=null){
                        projectLeaseItemSalesMapper.updateByPrimaryKeySelective(prjProjectLeaseItemSales);
                    }else{
                        projectLeaseItemSalesMapper.insertSelective(prjProjectLeaseItemSales);
                    }
                    prjProjectLeaseItemCondition.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjProjectLeaseItemCondition.getConditionId()!=null){
                        projectLeaseItemConditionMapper.updateByPrimaryKeySelective(prjProjectLeaseItemCondition);
                    }else{
                        projectLeaseItemConditionMapper.insertSelective(prjProjectLeaseItemCondition);
                    }
                    prjLeaseItemInsurance.setProjectLeaseItemId(hlsCusPrjProjectLeaseItem1.getProjectLeaseItemId());
                    if (prjLeaseItemInsurance.getInsuranceId()!=null){
                        prjLeaseItemInsuranceMapper.updateByPrimaryKeySelective(prjLeaseItemInsurance);
                    }else{
                        prjLeaseItemInsuranceMapper.insertSelective(prjLeaseItemInsurance);
                    }
                    hlsCusBpMasterMapper.updateByPrimaryKeySelective(hlsCusBpMaster);
                    hlsCusBpMasterBankAccount.setBpId(hlsCusBpMaster.getBpId());
                    if (hlsCusBpMasterBankAccount.getBankAccountId()!=null){
                        hlsCusBpMasterBankAccountMapper.updateByPrimaryKeySelective(hlsCusBpMasterBankAccount);
                    }else{
                        hlsCusBpMasterBankAccountMapper.insertSelective(hlsCusBpMasterBankAccount);
                    }
                }else{
                    prjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
                    prjQuotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
                    if (prjQuotation.getQuotationId()!=null){
                        hlsCusPrjQuotationMapper.updateByPrimaryKeySelective(prjQuotation);
                    }else{
                        hlsCusPrjQuotationMapper.insertSelective(prjQuotation);
                    }
                    hlsCusPrjProjectLeaseItemMapper.updateByPrimaryKeySelective(hlsCusPrjProjectLeaseItem1);
                }
//            }




        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","数据采集成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String overdueRepurchaseTrialCalculation(String decryptedStr){
        OverdueRepurchaseTrialCalculationDTO overdueRepurchaseTrialCalculationDTO = JSONObject.parseObject(decryptedStr, OverdueRepurchaseTrialCalculationDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号查询数据


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","试算成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String overdueRepurchaseRequest(String decryptedStr){
        OverdueRepurchaseRequestDTO overdueRepurchaseRequestDTO = JSONObject.parseObject(decryptedStr, OverdueRepurchaseRequestDTO.class);


        JSONObject jsonObject1 = new JSONObject();

        //将数据入库


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","逾期回购成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String queryWithholdingState(String decryptedStr){
        QueryWithholdingStateDTO queryWithholdingStateDTO = JSONObject.parseObject(decryptedStr, QueryWithholdingStateDTO.class);

        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号查询数据


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","查询成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String stopWithholding(String decryptedStr){
        StopWithholdingDTO stopWithholdingDTO = JSONObject.parseObject(decryptedStr, StopWithholdingDTO.class);


        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号和期次号，修改状态


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","暂停代扣成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String recoverWithholding(String decryptedStr){
        RecoverWithholdingDTO recoverWithholdingDTO = JSONObject.parseObject(decryptedStr, RecoverWithholdingDTO.class);


        JSONObject jsonObject1 = new JSONObject();

        //根据订单编号和期次号，修改状态


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","恢复代扣成功");
        return jsonObject1.toJSONString();
    }

    @Override
    public String businessApplication(String decryptedStr,HttpServletRequest request){
        BusinessApplicationDTO businessApplicationDTO = JSONObject.parseObject(decryptedStr, BusinessApplicationDTO.class);


        JSONObject jsonObject1 = new JSONObject();

        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(businessApplicationDTO.getOrderNo());
        if (hlsCusPrjProject==null){
            jsonObject1.put("code","100003");
            jsonObject1.put("message","订单不存在");
            return jsonObject1.toJSONString();
        }
        //根据action，执行操作
        String action = businessApplicationDTO.getAction();
        if ("PRE_RISK".equals(action)){
            String s = tongDunService.preliminaryValid(hlsCusPrjProject.getProjectId(), request);
//            校验客户信息查询授权书是否已经上传


            if ("Accept".equals(s)){
                //        设置返回信息
                jsonObject1.put("code","200");
                jsonObject1.put("message","预审成功");
                return jsonObject1.toJSONString();
            }
            jsonObject1.put("code","400");
            jsonObject1.put("message","预审失败");
            return jsonObject1.toJSONString();
        }
        else if ("APPLY_PRE_RISK".equals(action)){
            String s = tongDunService.interlocutoryValid(hlsCusPrjProject.getProjectId(), request);
            if ("Accept".equals(s)){
                //        设置返回信息
                jsonObject1.put("code","200");
                jsonObject1.put("message","审核成功");
                return jsonObject1.toJSONString();
            }
            else if ("Reject".equals(s) || "Error".equals(s)){
                jsonObject1.put("code","400");
                jsonObject1.put("message","审核失败");
                return jsonObject1.toJSONString();
            }
        }
        else if ("RE_APPLY_PRE_RISK".equals(action)){
            String s = tongDunService.interlocutoryValid(hlsCusPrjProject.getProjectId(), request);
            if ("Accept".equals(s)){
                //        设置返回信息
                jsonObject1.put("code","200");
                jsonObject1.put("message","审核成功");
                return jsonObject1.toJSONString();
            }
            else if ("Reject".equals(s) || "Error".equals(s)){
                jsonObject1.put("code","400");
                jsonObject1.put("message","审核失败");
                return jsonObject1.toJSONString();
            }
        }
        else if ("APPLY_WITHHOLD_CONTRACT".equals(action)){
//            申请代扣签约
        }
        else if ("APPLY_LOAN".equals(action)||"RE_APPLY_LOAN".equals(action)){
            //申请放款
            String riskInfo = hlsCusPrjProject.getRiskInfo();
            //获取riskinfo数据
            PreRiskAuditData preRiskAuditData = JSONObject.parseObject(riskInfo, PreRiskAuditData.class);
            if (preRiskAuditData==null){
                jsonObject1.put("code","400");
                jsonObject1.put("message","riskinfo信息不能为空");
                return jsonObject1.toJSONString();
            }
            //获取租赁物信息
            List<HlsCusPrjProjectLeaseItem> hlsCusPrjProjectLeaseItemList = hlsCusPrjProjectLeaseItemMapper.selectLeaseItemByProjectId(hlsCusPrjProject.getProjectId());
            //获取报价
            List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.selectQuoByProjectId(hlsCusPrjProject.getProjectId());
            //获取附件
            List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachments = hlsCusPrjProjectAttachmentMapper.queryByProjectId(hlsCusPrjProject.getProjectId());
            if (hlsCusPrjQuotations.size()==0){
                jsonObject1.put("code","400");
                jsonObject1.put("message","报价不能为空");
                return jsonObject1.toJSONString();
            }
            if (hlsCusPrjProjectLeaseItemList.size()==0){
                jsonObject1.put("code","400");
                jsonObject1.put("message","租赁物不能为空");
                return jsonObject1.toJSONString();
            }
            if (hlsCusPrjProjectAttachments.size()==0){
                jsonObject1.put("code","400");
                jsonObject1.put("message","附件不能为空");
                return jsonObject1.toJSONString();
            }
            HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusPrjQuotations.get(0);
            HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = hlsCusPrjProjectLeaseItemList.get(0);
            if (preRiskAuditData.getCarbrand2().equals(hlsCusPrjProjectLeaseItem.getBrandC())&&
                    preRiskAuditData.getChexi().equals(hlsCusPrjProjectLeaseItem.getSeriesC())&&
                    preRiskAuditData.getCartype().equals(hlsCusPrjProjectLeaseItem.getModelC())&&
                    preRiskAuditData.getCarcolor().equals(hlsCusPrjProjectLeaseItem.getColorC())&&
                    preRiskAuditData.getFinancingamount().equals(hlsCusPrjProjectLeaseItem.getFinanceAmount())&&
                    preRiskAuditData.getClxsjg().equals(hlsCusPrjProjectLeaseItem.getSellingPrice())&&
                    preRiskAuditData.getCfpp().equals(hlsCusPrjProjectLeaseItem.getListPrice())&&
                    preRiskAuditData.getYfzj().equals(hlsCusPrjQuotation.getPmt())&&
                    preRiskAuditData.getNhll().equals(hlsCusPrjQuotation.getIntRate())&&
                    preRiskAuditData.getSfje().equals(hlsCusPrjQuotation.getDownPayment())){
                if (hlsCusPrjProjectAttachments.size()>0){
                    //获取审批通过日的毫秒值
                    long approvedtTime = hlsCusPrjProject.getApprovedDate().getTime();
                    //获取当前时间毫秒值
                    long nowTime = new Date().getTime();
                    //计算间隔的时间
                    long days = (nowTime - approvedtTime) / (24 * 60 * 60 * 1000);
                    if ("APPLY_LOAN".equals(action)){
                        if (days>=30){
                            jsonObject1.put("code","400");
                            jsonObject1.put("message","审批通过超过三十天");
                            return jsonObject1.toJSONString();
                        }
                    }else if ("RE_APPLY_LOAN".equals(action)){
                        if (days>=50){
                            jsonObject1.put("code","400");
                            jsonObject1.put("message","再次审批通过超过五十天");
                            return jsonObject1.toJSONString();
                        }
                    }

                }
            }
        }
        else{
            //抵押材料审核
        }


        //            设置返回状态
        jsonObject1.put("code","200");
        jsonObject1.put("message","业务申请成功");
        return jsonObject1.toJSONString();
    }

    private void replaceAttach(HlsCusPrjProjectAttachment prjAttachment,String fileId){
        //删除附件
        FndAttachmentMulti prjMulti = new FndAttachmentMulti();
        prjMulti.setTableName("PRJ_PROJECT_ATTACHMENT");
        prjMulti.setTablePkValue(prjAttachment.getProjectAttachmentId().toString());
        List<FndAttachmentMulti> prjMultiList = fndAttachmentMultiMapper.select(prjMulti);
        for(FndAttachmentMulti multi : prjMultiList){
            fndAttachmentMapper.deleteByPrimaryKey(multi.getAttachmentId());
            fndAttachmentMultiMapper.deleteByPrimaryKey(multi.getRecordId());
        }
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
            insertMulti.setTableName(multi.getTableName());
            insertMulti.setTablePkValue(multi.getTablePkValue());
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

    public String imageSync(String decryptedStr){
        ImageSyncDTO imageSyncDTO = JSONObject.parseObject(decryptedStr, ImageSyncDTO.class);
        JSONObject resJson = new JSONObject();

        String orderNo = imageSyncDTO.getOrderNo();
        List<File> files = imageSyncDTO.getFiles();
        HlsCusPrjProject hlsCusPrjProject = prjProjectMapper.selectProjectByOrderNo(orderNo);
        Long projectId = hlsCusPrjProject.getProjectId();

        for(File file : files){
            //step1 影像文件是否已上传
            String fileId = file.getFileId();
            UploadAttachList uploadAttachList = uploadAttachListMapper.selectByFileId(fileId);
            if(uploadAttachList == null){
                resJson.put("success",false);
                resJson.put("message","fileId[" + fileId + "]不存在");
                return JSONObject.toJSONString(resJson);
            }
            if(!"Y".equals(uploadAttachList.getUploadFlag())){
                resJson.put("success",false);
                resJson.put("message","fileId[" + fileId + "]影像文件未上传");
                return JSONObject.toJSONString(resJson);
            }
            //step2 根据文件类型，检查单据是否可以更新
            String fileType = file.getFileType();
            /**
             * fileType                               材料名称                                       传输时机
             *JY_FQ_XXCJSYSQ                         个人信息采集及使用授权协议                         风控预审前
             *TRADE                                  汽车买卖合同                                    申请放款前
             *CAR_SERVICE                            车辆服务协议                                    申请放款前
             *CAR_HANDOVER_AND_PAY_CONFIRMx0         汽车交付确认书                                  申请放款前
             *LEASE                                  融资租赁合同                                    申请放款前
             *NOTICEx0                               客户告知函                                     申请放款前
             *OWNERSHIP_STATEMENT                    租赁物所有权转移接受确认函                         申请放款前
             *CONFIRM_PAYMENT_DELEGATION             委托付款确认书                                  申请放款前
             *AUTHORIZATIONx0                        授权委托书（抵押物）                             申请放款前
             *MORTGAGE                               车辆抵押合同                                   申请放款前
             *LICENSE_FRONT_IMGS                     行驶证正面                                     申请放款前（再次）
             *DRIVEN_LICENSE_SUB                     驾照主副页                                     申请放款前（再次）
             *REGISTRATION_CERTIFICATE               登记证                                        申请放款前（再次）
             *PERSON_AND_CAR                         承租人、车辆、业务员合影                          申请放款前（再次）
             *LICENSE_AND_PICK_UP_IMG                行驶证+车钥匙+身份证+前挡风玻璃vin码+提车确认单图片   申请放款前（再次）
             *VEHICLE_CERTIFICATE                    车辆合格证                                     申请放款前（再次）
             *INSURANCE_POLICYx0                     保险（车辆保险单）-支持多张，最多6张                申请放款前（再次）
             *REGISTRATION_CERTIFICATE_MORTGAGED     有抵押信息后的登记证（首页至空白页）                 抵押材料审核前
             *ASSET_TRANSFER_AGREE                   资产转让协议                                    结清后
             */
            if("JY_FQ_XXCJSYSQ".equals(fileType)){
                //预审前
                String preStatus = hlsCusPrjProject.getPreStatus();
                if(StringUtils.isNotEmpty(preStatus) && !"NEW".equals(preStatus)){
                    resJson.put("success",false);
                    resJson.put("message","fileId[" + fileId + "],进件已完成预审，“个人信息采集及使用授权协议”不允许同步");
                    return JSONObject.toJSONString(resJson);
                }
            }else if("TRADE".equals(fileType) || "CAR_SERVICE".equals(fileType) || "CAR_HANDOVER_AND_PAY_CONFIRMx0".equals(fileType) || "LEASE".equals(fileType)
                    || "NOTICEx0".equals(fileType) || "OWNERSHIP_STATEMENT".equals(fileType) || "CONFIRM_PAYMENT_DELEGATION".equals(fileType) || "AUTHORIZATIONx0".equals(fileType)
                    || "MORTGAGE".equals(fileType) || "LICENSE_FRONT_IMGS".equals(fileType) || "DRIVEN_LICENSE_SUB".equals(fileType) || "REGISTRATION_CERTIFICATE".equals(fileType)
                    || "PERSON_AND_CAR".equals(fileType) || "LICENSE_AND_PICK_UP_IMG".equals(fileType) || "VEHICLE_CERTIFICATE".equals(fileType) || "INSURANCE_POLICYx0".equals(fileType)){
                //放款前
                String investmentStatus = hlsCusPrjProject.getInvestmentStatus();
                if(StringUtils.isNotEmpty(investmentStatus) && !"NEW".equals(investmentStatus) && !"REJECTED".equals(investmentStatus)){
                    resJson.put("success",false);
                    resJson.put("message","fileId[" + fileId + "],进件投放审查流程中/投放审查通过，“申请放款前相关材料”不允许同步");
                    return JSONObject.toJSONString(resJson);
                }
            }else if("REGISTRATION_CERTIFICATE_MORTGAGED".equals(fileType)){
                //抵押材料审核前
            }else if("ASSET_TRANSFER_AGREE".equals(fileType)){
                //结清后
            }
        }
        //step3 覆盖更新附件
        for(File file : files){
            HlsCusPrjProjectAttachment prjAttachment = null;
            String fileType = file.getFileType();
            if("JY_FQ_XXCJSYSQ".equals(fileType) || "TRADE".equals(fileType) || "CAR_SERVICE".equals(fileType) || "CAR_HANDOVER_AND_PAY_CONFIRMx0".equals(fileType) || "LEASE".equals(fileType)
                    || "NOTICEx0".equals(fileType) || "OWNERSHIP_STATEMENT".equals(fileType) || "CONFIRM_PAYMENT_DELEGATION".equals(fileType) || "AUTHORIZATIONx0".equals(fileType)
                    || "MORTGAGE".equals(fileType) || "LICENSE_FRONT_IMGS".equals(fileType) || "DRIVEN_LICENSE_SUB".equals(fileType) || "REGISTRATION_CERTIFICATE".equals(fileType)
                    || "PERSON_AND_CAR".equals(fileType) || "LICENSE_AND_PICK_UP_IMG".equals(fileType) || "VEHICLE_CERTIFICATE".equals(fileType) || "INSURANCE_POLICYx0".equals(fileType)){
                //预审前、放款前
                HashMap<String, String> vMap = fileTypeMap.get(fileType);
                String documentName = vMap.get("documentName");
                String projectAttachmentCategory = vMap.get("projectAttachmentCategory");
                prjAttachment = hlsCusPrjProjectAttachmentMapper.selectAttachYl(projectId,projectAttachmentCategory,documentName);
                if(prjAttachment == null){
                    prjAttachment = new HlsCusPrjProjectAttachment();
                    prjAttachment.setProjectId(projectId);
                    prjAttachment.setProjectAttachmentCategory(projectAttachmentCategory);
                    prjAttachment.setDocumentName(documentName);
                    hlsCusPrjProjectAttachmentMapper.insertSelective(prjAttachment);
                }
                this.replaceAttach(prjAttachment,file.getFileId());
            }else if("REGISTRATION_CERTIFICATE_MORTGAGED".equals(fileType)){
                //抵押材料审核前
            }else if("ASSET_TRANSFER_AGREE".equals(fileType)){
                //结清后
            }

        }

        resJson.put("success",true);
        resJson.put("message","成功");
        return JSONObject.toJSONString(resJson);
    }
}