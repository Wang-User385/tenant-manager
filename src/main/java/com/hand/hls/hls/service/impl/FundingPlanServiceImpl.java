package com.hand.hls.hls.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.ContractPaymentPlan;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.ContractPaymentPlanMapper;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.CshPaymentReqDt;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqDt;
import com.hand.hls.csh.mapper.CshPaymentReqDtMapper;
import com.hand.hls.csh.mapper.CshPaymentReqLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.service.CshPaymentReqDtService;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hls.dto.*;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.hls.mapper.HlsCusFundingPlanMapper;
import com.hand.hls.hls.service.IFundingBranchReferrerService;
import com.hand.hls.hls.service.IFundingPlanLnService;
import com.hand.hls.hls.service.IMarketingChannelService;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.dto.HlsCusPrjProjectSupplement;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.PrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectSupplementMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectAttachmentService;
import com.hand.hls.utils.HlsCusCheckNull;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.service.IFundingPlanService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingPlanServiceImpl extends BaseServiceImpl<HlsCusFundingPlan> implements IFundingPlanService{

    public static final String NEW = "NEW";
    public static final String APPROVED = "APPROVED";
    public static final String APPROVING = "APPROVING";
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    ContractPaymentPlanMapper contractPaymentPlanMapper;
    @Autowired
    HlsCusFundingPlanMapper hlsCusFundingPlanMapper;
    @Autowired
    IFundingPlanLnService  fundingPlanLnService;
    @Autowired
    HlsCusFundingPlanLnMapper hlsCusFundingPlanLnMapper;
    @Autowired
    CshPaymentReqDtMapper cshPaymentReqDtMapper;
    @Autowired
    CshPaymentReqDtService cshPaymentReqDtService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private HlsCusPrjProjectSupplementMapper hlsCusPrjProjectSupplementMapper;

    public static final String NOT = "NOT";
    public static final String INFLOW = "INFLOW";
    public static final String RELEASE = "RELEASE";
    public static final String REPORTING = "REPORTING";
    public static final String REPORTED = "REPORTED";
    public static final String INDIRECT = "INDIRECT";

    public static final String HLS_FUNDING_PLAN = "HLS_FUNDING_PLAN";

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private IMarketingChannelService iMarketingChannelService;
    @Autowired
    private IFundingBranchReferrerService iFundingBranchReferrerService;
    @Autowired
    private HlsCusPrjProjectAttachmentService hlsCusPrjProjectAttachmentService;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;

    @Autowired
    HlsCusConContractCashflowService hlsCusConContractCashflowService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService ;
    @Override
    public HlsCusFundingPlan createFundingPlan(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan){
        hlsCusFundingPlan.setStatus(NEW);
        Map<String, String> params = new HashMap<>();
        String value = codingRuleValuesService.getCodeRuleValue(iRequest, "CAP_PLAN", "CAP_PLAN", "CAP_PLAN", params);
        hlsCusFundingPlan.setFundingPlanNumber(value);
        hlsCusFundingPlan = this.insertSelective(iRequest,hlsCusFundingPlan);

        /*//插入放款计划
        ContractPaymentPlan contractPaymentPlan = new ContractPaymentPlan();
        contractPaymentPlan.setContractId(hlsCusFundingPlan.getContractId());
        List<ContractPaymentPlan> contractPaymentPlanList = contractPaymentPlanMapper.select(contractPaymentPlan);
        for (int i = 0; i < contractPaymentPlanList.size(); i++) {
            HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
            hlsCusFundingPlanLn.setFundingPlanId(hlsCusFundingPlan.getFundingPlanId());
            hlsCusFundingPlanLn.setPlanId(contractPaymentPlanList.get(i).getPlanId());
            fundingPlanLnService.insertSelective(iRequest,hlsCusFundingPlanLn);
        }*/

        return hlsCusFundingPlan;
    }
    private void approveWfl(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan) throws ResMessageException {

        List<HlsCusFundingPlan> hlsCusFundingPlans= hlsCusFundingPlanMapper.queryForFundingPlanDetail(hlsCusFundingPlan) ;
        hlsCusFundingPlan = hlsCusFundingPlans.get(0);
        if (APPROVED.equals(hlsCusFundingPlan.getStatus()) || APPROVING.equals(hlsCusFundingPlan.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        if(hlsCusFundingPlan.getCreditPeriodFrom() != null&&hlsCusFundingPlan.getCreditPeriodTo()!=null) {
            if (hlsCusFundingPlan.getProposedLaunchDate().compareTo(hlsCusFundingPlan.getCreditPeriodFrom()) < 0
                    || hlsCusFundingPlan.getProposedLaunchDate().compareTo(hlsCusFundingPlan.getCreditPeriodTo()) > 0) {
                throw new ResMessageException("拟投放日期必须在授信审批通知书有效期内！");
            }
        }
        if(hlsCusFundingPlan.getCreditPeriodFrom() == null&&hlsCusFundingPlan.getCreditPeriodTo()==null){
            throw new ResMessageException("该合同无授信审批同意通知书有效期！");
        }
        HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        hlsCusPrjProjectSupplement.setProjectId(hlsCusFundingPlan.getRefProjectId());
        hlsCusPrjProjectSupplement.setSuppleStatus(APPROVED);
        List<HlsCusPrjProjectSupplement> hlsCusPrjProjectSupplements = hlsCusPrjProjectSupplementMapper.select(hlsCusPrjProjectSupplement);
        Date fromDate ;

        if(hlsCusPrjProjectSupplements.size()>0) {
            //根据提交时间排序
            hlsCusPrjProjectSupplements.stream().sorted(Comparator.comparing(HlsCusPrjProjectSupplement::getSuppleSubmitDate));
            LocalDate localDateFrom = DateToLocaleDate(hlsCusPrjProjectSupplements.get(hlsCusPrjProjectSupplements.size()-1).getApproveDate());
            fromDate= LocalDateToDate(localDateFrom.plusDays(180));
        }else{
            LocalDate localDateFrom = DateToLocaleDate(hlsCusFundingPlan.getCreditPeriodFrom());
            fromDate =  LocalDateToDate(localDateFrom.plusDays(180));
        }
        if(hlsCusFundingPlan.getProposedLaunchDate().compareTo(fromDate)>0){
            throw new ResMessageException("项目经理填写的拟投放日期必须在授信审批期限从到项目经理填写的拟投放日期小于180天!");
        }

        databaseLockProvider.lock(hlsCusFundingPlan);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new ResMessageException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        List<HlsCusFundingPlan> cs = new ArrayList<>();
        cs.add(hlsCusFundingPlan);
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "FUNDING_PLAN_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "FUNDING_PLAN_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "FUNDING_PLAN_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, hlsCusFundingPlan.getFundingPlanId());
        params.put("documentCategory", "CAP_PLAN");
        params.put("hlsCusFundingPlan", JSON.toJSONString(hlsCusFundingPlan));
        params.put("documentName", hlsCusFundingPlan.getContractName());
        params.put("documentNumber", hlsCusFundingPlan.getFundingPlanNumber());
        params.put("startUserName", iRequest.getUserName());

        params.put("unitId", hlsCusFundingPlan.getUnitId());
        params.put("companyId", iRequest.getCompanyId());
        params.put("contractId", hlsCusFundingPlan.getContractId());
        params.put("projectId", hlsCusFundingPlan.getProjectId());
        params.put("assistUnitId", hlsCusFundingPlan.getAssistUnitId());
        activitiStartService.start(iRequest, cs, params);

        HlsCusFundingPlan fundingPlan = new HlsCusFundingPlan();
        fundingPlan.setFundingPlanId(hlsCusFundingPlan.getFundingPlanId());
        fundingPlan.setStatus(APPROVING);
        this.updateByPrimaryKeySelective(iRequest,fundingPlan);

        HlsCusFundingPlan cusFundingPlan = new HlsCusFundingPlan();
        cusFundingPlan = this.selectByPrimaryKey(iRequest , fundingPlan);
        HlsCusConContract cusConContractNew = new HlsCusConContract();
        HlsCusConContract hlsCusConContractNew = new HlsCusConContract();
        if(cusFundingPlan.getContractId() != null){
            cusConContractNew.setContractId(cusFundingPlan.getContractId());
            hlsCusConContractNew =  hlsCusConContractService.selectByPrimaryKey(iRequest , cusConContractNew);
            if(!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                hlsCusConContractNew.setContractStatus("REPORTING");
            }
            hlsCusConContractService.updateByPrimaryKeySelective(iRequest , hlsCusConContractNew );
        }


    }
    @Override
    public List<HlsCusFundingPlan> fundingPlanSubmit(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan) throws ResMessageException, ParameterNullException {
        //状态检查
        dateCheck(hlsCusFundingPlan);
        //启动工作流
        approveWfl( iRequest,  hlsCusFundingPlan);

        List<HlsCusFundingPlan> hlsCusFundingPlans = new ArrayList<>();
        hlsCusFundingPlans.add(hlsCusFundingPlan);
        return  hlsCusFundingPlans;
    }


    public void dateCheck(HlsCusFundingPlan hlsCusFundingPlan) throws ResMessageException {
        /**
         * 新建时校验
         */
        if(HlsCusCheckNull.isNull(hlsCusFundingPlan.getFundingPlanId())){
            HlsCusFundingPlan fundingPlan = new HlsCusFundingPlan();
            fundingPlan.setFundingPlanId(hlsCusFundingPlan.getFundingPlanId());
            List<HlsCusFundingPlan> hlsCusFundingPlans =  hlsCusFundingPlanMapper.select(fundingPlan);
            if(!hlsCusFundingPlans.isEmpty()) {
                Long count = hlsCusFundingPlans.stream().filter(item -> NEW.equals(item.getStatus()) || APPROVING.equals(item.getStatus())).count();
                if (count > 0) {
                    throw new ResMessageException("已经创建了申请,无需重复创建!");
                }
            }
        }
        /**
         * 提交时校验
         */
        else{
            HlsCusFundingPlan fundingPlan = new HlsCusFundingPlan();
            fundingPlan.setFundingPlanId(hlsCusFundingPlan.getFundingPlanId());
            HlsCusFundingPlan fp =  hlsCusFundingPlanMapper.selectByPrimaryKey(fundingPlan);
            if (APPROVED.equals(fp.getStatus()) || APPROVING.equals(fp.getStatus())) {
                throw new ResMessageException("已经提交了申请,无需重复提交!");
            }
        }
    }
    @Override
    public List<HlsCusFundingPlanLn> selectPlan(IRequest iRequest, List<HlsCusFundingPlanLn> hlsCusFundingPlanLns) throws ResMessageException {
        hlsCusFundingPlanLns = fundingPlanLnService.batchUpdate(iRequest,hlsCusFundingPlanLns);
        for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(0).getPlanId());

        }
        return hlsCusFundingPlanLns;
    }
    @Override
    public  List<HlsCusFundingPlan> removePlan(IRequest iRequest, List<HlsCusFundingPlan> hlsCusFundingPlans)throws ResMessageException{
        for (int i = 0; i < hlsCusFundingPlans.size(); i++) {
            HlsCusFundingPlan hlsCusFundingPlan = hlsCusFundingPlanMapper.selectByPrimaryKey(hlsCusFundingPlans.get(i).getFundingPlanId());
            if("APPROVING".equals(hlsCusFundingPlan.getStatus())||"APPROVED".equals(hlsCusFundingPlan.getStatus())){
                throw new ResMessageException("只能删除新建或废弃的单据！");
            }
            HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
            hlsCusFundingPlanLn.setFundingPlanId(hlsCusFundingPlans.get(i).getFundingPlanId());
            List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.select(hlsCusFundingPlanLn);
            for (int j = 0; j < hlsCusFundingPlanLns.size(); j++) {
                HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt = new HlsCusCshPaymentReqDt();
                hlsCusCshPaymentReqDt.setFundingPlanLnId(hlsCusFundingPlanLns.get(j).getFundingPlanLnId());
                List<HlsCusCshPaymentReqDt> hlsCusCshPaymentReqDts = cshPaymentReqDtMapper.select(hlsCusCshPaymentReqDt);
                for (int k = 0; k < hlsCusCshPaymentReqDts.size(); k++) {
                    HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                    hlsCusConContractCashflow.setCashflowId(hlsCusCshPaymentReqDts.get(k).getSourceDocLineId());
                    hlsCusConContractCashflow.setFundingPlanStatus("");
                    hlsCusConContractCashflow.setProcessStatus("");
                    hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,hlsCusConContractCashflow);
                }
                cshPaymentReqDtService.batchDelete(hlsCusCshPaymentReqDts);

                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                conContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(j).getPlanId());
                conContractCashflow.setFundingPlanStatus("");
                conContractCashflow.setProcessStatus("");
                hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,conContractCashflow);
            }
            fundingPlanLnService.batchDelete(hlsCusFundingPlanLns);
        }
        this.batchDelete(hlsCusFundingPlans);
        return hlsCusFundingPlans;
    }
    public List<HlsCusFundingPlan> queryForFundingPlan(IRequest iRequest,HlsCusFundingPlan hlsCusFundingPlan){
        List<HlsCusFundingPlan> fundingPlans = hlsCusFundingPlanMapper.queryForFundingPlan(hlsCusFundingPlan);
        return fundingPlans;
    }

    @Override
    public HlsCusFundingPlan fundingPlanCheck(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan)throws ResMessageException{
        HlsCusFundingPlan fundingPlan = new HlsCusFundingPlan();
        fundingPlan.setFundingPlanId(hlsCusFundingPlan.getFundingPlanId());
        iRequest.setAttribute("wflRuleControlFlag", "Y");
        List<HlsCusFundingPlan> fundingPlans = this.queryForFundingPlan(iRequest,fundingPlan);
        fundingPlan = fundingPlans.get(0);
        long daysBetween=(hlsCusFundingPlan.getProposedLaunchDate().getTime()-fundingPlan.getProposedLaunchDate().getTime()+1000000)/(3600*24*1000);
        if(Math.abs(daysBetween)>5){
            throw new ResMessageException("项目经理填写拟投放日期与金融市场部填写拟投放日期小于5个工作日!");
        }
        HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement = new HlsCusPrjProjectSupplement();
        hlsCusPrjProjectSupplement.setProjectId(hlsCusFundingPlan.getRefProjectId());
        hlsCusPrjProjectSupplement.setSuppleStatus(APPROVED);
        List<HlsCusPrjProjectSupplement> hlsCusPrjProjectSupplements = hlsCusPrjProjectSupplementMapper.select(hlsCusPrjProjectSupplement);
        Date fromDate ;

        if(hlsCusPrjProjectSupplements.size()>0) {
            //根据提交时间排序
            hlsCusPrjProjectSupplements.stream().sorted(Comparator.comparing(HlsCusPrjProjectSupplement::getSuppleSubmitDate).reversed());
            LocalDate localDateFrom = DateToLocaleDate(hlsCusPrjProjectSupplements.get(0).getApproveDate());
            fromDate= LocalDateToDate(localDateFrom.plusDays(180));
        }else{
            LocalDate localDateFrom = DateToLocaleDate(fundingPlan.getCreditPeriodFrom());
            fromDate =  LocalDateToDate(localDateFrom.plusDays(180));
        }
        if(hlsCusFundingPlan.getProposedLaunchDate().compareTo(fromDate)>0){
            throw new ResMessageException("项目经理填写的拟投放日期必须在授信审批期限从到项目经理填写的拟投放日期小于180天!");
        }
        if(fundingPlan.getCreditPeriodFrom()!= null && fundingPlan.getCreditPeriodTo() != null) {
            if (hlsCusFundingPlan.getProposedLaunchDate().compareTo(fundingPlan.getCreditPeriodFrom()) < 0
                    || hlsCusFundingPlan.getProposedLaunchDate().compareTo(fundingPlan.getCreditPeriodTo()) > 0) {
                throw new ResMessageException("项目经理填写的拟投放日期必须在授信审批通知书有效期内!");
            }
        }

        return hlsCusFundingPlan;
    }

    public static LocalDate DateToLocaleDate(Date date) {

        Instant  instant = date.toInstant();

        ZoneId zoneId  = ZoneId.systemDefault();

        return instant.atZone(zoneId).toLocalDate();

    }
    public static Date LocalDateToDate(LocalDate localDate) {

        ZoneId zoneId = ZoneId.systemDefault();

        ChronoZonedDateTime<LocalDate> zonedDateTime = localDate.atStartOfDay(zoneId);

        return Date.from(zonedDateTime.toInstant());

    }

    @Override
    public HlsCusFundingPlan saveFundingPlan(IRequest iRequest, HlsCusFundingPlan hlsCusFundingPlan){

        if (hlsCusFundingPlan.getHlsCusCapMarketingChannelList() != null) {
            for (HlsCusCapMarketingChannel hlsCusCapMarketingChannel : hlsCusFundingPlan.getHlsCusCapMarketingChannelList()) {
                if (hlsCusCapMarketingChannel.getMarketingChannelId() != null) {
                    iMarketingChannelService.updateByPrimaryKeySelective(iRequest, hlsCusCapMarketingChannel);
                }else {
                    iMarketingChannelService.insertSelective(iRequest, hlsCusCapMarketingChannel);
                }
            }
        }
        if (hlsCusFundingPlan.getPrjProjectAttachments() != null) {
            for (HlsCusPrjProjectAttachment prjProjectAttachment : hlsCusFundingPlan.getPrjProjectAttachments()) {
                if (prjProjectAttachment.getProjectAttachmentId() != null) {
                    hlsCusPrjProjectAttachmentService.updateByPrimaryKeySelective(iRequest, prjProjectAttachment);
                }else {
                    prjProjectAttachment.setProjectId(prjProjectAttachment.getFundingPlanId());
                    prjProjectAttachment.setProjectAttachmentCategory(HLS_FUNDING_PLAN);
                    prjProjectAttachment.setPrjProjectType(HLS_FUNDING_PLAN);
                    hlsCusPrjProjectAttachmentService.insertSelective(iRequest, prjProjectAttachment);
                }
            }
        }
        if (hlsCusFundingPlan.getFundingBranchReferrerList() != null) {
            for (FundingBranchReferrer fundingBranchReferrer : hlsCusFundingPlan.getFundingBranchReferrerList()) {
                if (fundingBranchReferrer.getBranchReferrerId() != null) {
                    iFundingBranchReferrerService.updateByPrimaryKeySelective(iRequest, fundingBranchReferrer);
                }else {
                    iFundingBranchReferrerService.insertSelective(iRequest, fundingBranchReferrer);
                }
            }
        }

        Double loanTotalAmount = 0D;
        Double loanNetAmount = 0D;
        Double deductAmount = 0D;
        if (hlsCusFundingPlan.getHlsCusFundingPlanLnList() != null) {
            for (HlsCusFundingPlanLn hlsCusFundingPlanLn : hlsCusFundingPlan.getHlsCusFundingPlanLnList()) {
                loanTotalAmount = HlsCusMathUtil.add(loanTotalAmount,hlsCusFundingPlanLn.getDueAmount());

                if (hlsCusFundingPlanLn.getFundingPlanLnId() != null) {
                    fundingPlanLnService.updateByPrimaryKeySelective(iRequest, hlsCusFundingPlanLn);

                }else{
                    fundingPlanLnService.insertSelective(iRequest, hlsCusFundingPlanLn);
                    HlsCusConContractCashflow hlsCusConContractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusFundingPlanLn.getPlanId());
                    hlsCusConContractCashflow.setFundingPlanStatus(REPORTING);
                    hlsCusConContractCashflow.setProcessStatus(REPORTING);
                    hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,hlsCusConContractCashflow);
                    if(hlsCusConContractCashflow.getCfItem() == 0) {
                        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                        conContractCashflow.setContractId(hlsCusConContractCashflow.getContractId());
                        conContractCashflow.setDueDate(hlsCusConContractCashflow.getDueDate());
                        conContractCashflow.setWriteOffFlag(NOT);
                        conContractCashflow.setCfDirection(INFLOW);
                        conContractCashflow.setCfStatus(RELEASE);
                        List<HlsCusConContractCashflow> hlsCusConContractCashflows = hlsCusConContractCashflowMapper.select(conContractCashflow);
                        for (int j = 0; j < hlsCusConContractCashflows.size(); j++) {
                            if(!REPORTING.equals(hlsCusConContractCashflows.get(j).getFundingPlanStatus())
                                    &&!REPORTED.equals(hlsCusConContractCashflows.get(j).getFundingPlanStatus())){
                                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = hlsCusPrjQuotationCashflowMapper.selectByPrimaryKey(hlsCusConContractCashflows.get(j).getGeneratedSourceDocLineId());
                                if(INDIRECT.equals(hlsCusPrjQuotationCashflow.getReceiptType())){
                                    HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt = new HlsCusCshPaymentReqDt();
                                    hlsCusCshPaymentReqDt.setFundingPlanLnId(hlsCusFundingPlanLn.getFundingPlanLnId());
                                    hlsCusCshPaymentReqDt.setSourceDocId(hlsCusConContractCashflows.get(j).getContractId());
                                    hlsCusCshPaymentReqDt.setSourceDocLineId(hlsCusConContractCashflows.get(j).getCashflowId());
                                    hlsCusCshPaymentReqDt.setDeductAmount(hlsCusConContractCashflows.get(j).getDueAmount());
                                    cshPaymentReqDtService.insert(iRequest, hlsCusCshPaymentReqDt);

                                    HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                                    contractCashflow.setCashflowId(hlsCusConContractCashflows.get(j).getCashflowId());
                                    contractCashflow.setFundingPlanStatus(REPORTING);
                                    contractCashflow.setProcessStatus(REPORTING);
                                    hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,contractCashflow);
                                }
                            }
                        }
                    }
                }


                HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
                cashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(hlsCusFundingPlanLn.getPlanId());

                if(cashflow.getCfItem()!=90){
                    loanNetAmount = HlsCusMathUtil.add(loanNetAmount,cashflow.getDueAmount());
                }

                HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
                cshPaymentReqDt.setFundingPlanLnId(hlsCusFundingPlanLn.getFundingPlanLnId());
                List<HlsCusCshPaymentReqDt> cshPaymentReqDts = cshPaymentReqDtMapper.select(cshPaymentReqDt);
                for (int i = 0; i < cshPaymentReqDts.size(); i++) {
                    deductAmount = HlsCusMathUtil.add(deductAmount,cshPaymentReqDts.get(i).getDeductAmount());
                }
            }
        }
        hlsCusFundingPlan.setLoanTotalAmount(loanTotalAmount);
        hlsCusFundingPlan.setLoanNetAmount(HlsCusMathUtil.sub(loanNetAmount,deductAmount));

        hlsCusFundingPlan = this.updateByPrimaryKeySelective(iRequest,hlsCusFundingPlan);

        return hlsCusFundingPlan;
    }
}