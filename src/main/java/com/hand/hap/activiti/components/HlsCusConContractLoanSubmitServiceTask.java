package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mail.dto.Message;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.service.IEmailService;
import com.hand.hap.mail.service.IMessageService;
import com.hand.hls.activiti.mapper.HlsCusActVarMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.csh.service.IHlsCusCshPaymentReqLnService;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.hls.dto.HlsCusFundingPlanLn;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.ResMessageException;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import hls.core.sys.event.service.SysEventService;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yenick
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractLoanSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private IHlsCusCshPaymentReqLnService cshPaymentReqLnService;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCreditLineService hlsCreditLineService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private HlsCusBpMasterService hlsCusBpMasterService;

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private HlsCusEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusActVarMapper hlsCusActVarMapper;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private IHlsCusPrjQuotationHistoryService hlsCusPrjQuotationHistoryService;

    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;

    @Autowired
    private IHlsCusPrjQuotationCashflowHistoryService hlsCusPrjCashflowHistoryService;

    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;

    @Autowired
    private IHlsCusPrjQuotationDetailsHistoryService hlsCusPrjQuotationDetailsHistoryService;
    @Autowired
    private HlsCusFundingPlanLnMapper hlsCusFundingPlanLnMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;

    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;

    @Autowired
    private EasVenderBasicSycnExecutor easVenderBasicSycnExecutor;
    @Autowired
    private IMessageService messageService;
    private static final String EMAIL = "EMAIL";
    private static final String P = "P";
    private static final String CSH = "CSH";
    private static final String NORMAL = "NORMAL";
    private static final String SNZL = "SNZL";
    @Autowired
    private IEmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(HlsCusConContractLoanSubmitServiceTask.class);

    @Autowired
    private FinanceBaseUtils financeBaseUtils;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private SysUserMapper sysUserMapper;

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String contractId = String.valueOf(delegateExecution.getVariable("contractId"));
        String projectId = String.valueOf(delegateExecution.getVariable("projectId"));
        String unitId = String.valueOf(delegateExecution.getVariable("unitId"));
        String companyId = String.valueOf(delegateExecution.getVariable("companyId"));
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());

//        //流程中会更改数据 要重新查
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(Long.parseLong(delegateExecution.getProcessInstanceBusinessKey()));
        //databaseLockProvider.lock(hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(requestCtx, hlsCusCshPaymentReqHd);

        //更新支付表状态
        if ("APPROVING".equalsIgnoreCase(hlsCusCshPaymentReqHd.getPaymentReqStatus())) {
            if ("APPROVED".equalsIgnoreCase(result)) {

                flag = "APPROVED";

                //更新支付表状态 -  付款申请通过
                String msg= "工作流结束通知-放款申请审批流程-" + hlsCusCshPaymentReqHd.getBpName();
                if (hlsCusCshPaymentReqHd.getSourceContractId() != null) {
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
                    HlsCusConContract hlsCusConContractNew = hlsCusConContractService.selectByPrimaryKey(requestCtx, hlsCusConContract);
                    if (!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                        hlsCusConContractNew.setContractStatus("PAYMENTED");
                    }
                    //更新支付表综合融资成本率
                    hlsCusConContractNew.setComCostRate(hlsCusCshPaymentReqHd.getComCostRate());
                    hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContractNew);
                    msg = "工作流结束通知-放款申请审批流程-" + hlsCusConContractNew.getContractName()+"-" + hlsCusCshPaymentReqHd.getBpName() + "-" + hlsCusConContractNew.getContractNumber();
                }


                HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
                hlsCusFundingPlanLn.setFundingPlanId(hlsCusCshPaymentReqHd.getFundingPlanId());
                List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.selectPaymentPlanInfo(hlsCusFundingPlanLn);
                for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
                    HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                    conContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(i).getPlanId());
                    conContractCashflow.setProcessStatus("PAYMENT_APPROVED");
                    hlsCusConContractCashflowService.updateByPrimaryKeySelective(requestCtx, conContractCashflow);
                }


                hlsCusCshPaymentReqHd.setPaymentReqStatus(flag);
                hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
                hlsCusCshPaymentReqHd.setSendFlag("Y");
                hlsCusCshPaymentReqHd.setProcessInstanceId(processInstanceId);
                cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCshPaymentReqHd);


                // 更新采购合同总额
                HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
                hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
                List<HlsCusCshPaymentReqLn> paymentReqLnList = cshPaymentReqLnService.select(requestCtx, hlsCusCshPaymentReqLn, 1, 999);
                for(HlsCusCshPaymentReqLn paymentReqLn:paymentReqLnList){
                    if(paymentReqLn.getPurchaseContractId() != null){
                        HlsCusCshPaymentReqLn cshPaymentReqLn = new HlsCusCshPaymentReqLn();
                        cshPaymentReqLn.setPurchaseContractId(paymentReqLn.getPurchaseContractId());
                        List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = cshPaymentReqLnService.select(requestCtx, cshPaymentReqLn, 1, 999);
                        for(HlsCusCshPaymentReqLn temp:hlsCusCshPaymentReqLnList){
                            temp.setPurchaseContractAmountSum(paymentReqLn.getPurchaseContractAmountSum()+paymentReqLn.getDueAmountLn());
                            temp.setPurchaseContractBalance(paymentReqLn.getPurchaseContractBalance()-paymentReqLn.getDueAmountLn());
                            cshPaymentReqLnService.updateByPrimaryKey(requestCtx,temp);
                        }
                    }
                }

                String url = "/CSH/CSH_SET/CSH002/csh002_csh_payment_req_hd_detail.lview?layout_code=CSH002F2QNEW&payment_req_id="+hlsCusCshPaymentReqHd.getPaymentReqId()+"&project_id="+projectId+"&maintain_type=READONLY&function_usage=QUERY&function_code=CSH002F2QNEW";
                //放款申请提交发送通知给黄蕾、李宁、任江舟
                String[] sendUsers = {"huanglei", "li.ning","renjiangzhou"};
                //保留原始的userId
                Long originalUserId = requestCtx.getUserId();
                for (String userName : sendUsers) {
                    Map<String, Object> paramsEvent = new HashMap<>();
                    SysUser user = new SysUser();
                    user = sysUserMapper.queryUserByUserName(userName);
                    if(user != null) {
                        requestCtx.setUserId(user.getUserId());
                        paramsEvent.put("message", msg);
                        paramsEvent.put("noticeTitle", "放款申请审批流程");
                        paramsEvent.put("noticeType", "NOTICE");
                        paramsEvent.put("url", url);
                        paramsEvent.put("level", 1L);
                        sysEventService.eventSave(requestCtx, hlsCusCshPaymentReqHd.getPaymentReqId(), hlsCusCshPaymentReqHd.getDocumentCategory(), hlsCusCshPaymentReqHd.getDocumentType(), "BAC", "CON_PAYMENT_WFL", "P2D", paramsEvent);
                    }
                }
                requestCtx.setUserId(originalUserId);


                //调用财务共享接口上传应付报账单  大单只有线上支付才推共享
                if("TT".equals(paymentReqLnList.get(0).getPaymentMethod())) {
                    financeBaseUtils.postPaymentItfc(requestCtx,hlsCusCshPaymentReqHd.getPaymentReqId());
                }
            } else if ("APPROVED_RETURN".equalsIgnoreCase(result) || "REJECTED".equalsIgnoreCase(result)) {
                flag = "REJECTED";
                hlsCusCshPaymentReqHd.setPaymentReqStatus(flag);
                hlsCusCshPaymentReqHd.setProcessInstanceId(processInstanceId);
                cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCshPaymentReqHd);


                HlsCusFundingPlanLn hlsCusFundingPlanLn = new HlsCusFundingPlanLn();
                hlsCusFundingPlanLn.setFundingPlanId(hlsCusCshPaymentReqHd.getFundingPlanId());
                List<HlsCusFundingPlanLn> hlsCusFundingPlanLns = hlsCusFundingPlanLnMapper.selectPaymentPlanInfo(hlsCusFundingPlanLn);
                for (int i = 0; i < hlsCusFundingPlanLns.size(); i++) {
                    HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                    conContractCashflow.setCashflowId(hlsCusFundingPlanLns.get(i).getPlanId());
                    conContractCashflow.setProcessStatus("PAYMENT_APPROVING");
                    hlsCusConContractCashflowService.updateByPrimaryKeySelective(requestCtx, conContractCashflow);
                }
                //更新支付表状态 - 付款申请否决
                if (hlsCusCshPaymentReqHd.getSourceContractId() != null) {
                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
                    HlsCusConContract hlsCusConContractNew = hlsCusConContractService.selectByPrimaryKey(requestCtx, hlsCusConContract);
                    if (!"Y".equalsIgnoreCase(hlsCusConContractNew.getInceptFlag())) {
                        hlsCusConContractNew.setContractStatus("UNPAYMENTED");
                    }

                    hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContractNew);
                }
            }


            HlsCusConContract conContract = new HlsCusConContract();
            conContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
            IRequest request = RequestHelper.getCurrentRequest(true);
            request.setAttribute("wflRuleControlFlag", "Y");
            conContract = hlsCusConContractService.selectByPrimaryKey(request, conContract);


            HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
            hlsCusPrjQuotation.setQuotationId(conContract.getQuotationId());
/*
            hlsCusPrjQuotation.setSourceDocumentCategory("CON_CONTRACT");
*/
            List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationMapper.select(hlsCusPrjQuotation);
            hlsCusPrjQuotation = hlsCusPrjQuotationList.get(0);

            Long version_count = hlsCusPrjQuotationHistoryService.selectVersionCountCon(conContract);
            version_count++;

            JSONObject jsonDTO = (JSONObject) JSONObject.toJSON(hlsCusPrjQuotation);
            HlsCusPrjQuotationHistory hlsCusPrjQuotationHistory = jsonDTO.toJavaObject(HlsCusPrjQuotationHistory.class);
            hlsCusPrjQuotationHistory.setQuotationId(null);
            hlsCusPrjQuotationHistory.setVersionId(version_count);
            hlsCusPrjQuotationHistory.setSourceQuotationId(hlsCusPrjQuotation.getQuotationId());
            hlsCusPrjQuotationHistory.setChangeCategory("CON_PAYMENT");
            hlsCusPrjQuotationHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationHistory);
            //复制现金流
            List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowService.queryQuotationCashFlowById(hlsCusPrjQuotation);
            for (HlsCusPrjQuotationCashflow dt2 : hlsCusPrjQuotationCashflowList) {
                JSONObject jsonCashflowDTO = (JSONObject) JSONObject.toJSON(dt2);
                HlsCusPrjQuotationCashflowHistory hlsCusPrjQuotationCashflowHistory = jsonCashflowDTO.toJavaObject(HlsCusPrjQuotationCashflowHistory.class);
                hlsCusPrjQuotationCashflowHistory.setQuotationCashflowId(null);
                hlsCusPrjQuotationCashflowHistory.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                if (dt2.getCfItem() != 90 && dt2.getCfItem() != 91) {
                    hlsCusPrjCashflowHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationCashflowHistory);
                } else {
                    //对承兑汇票现金流单独处理
                    if (dt2.getCfItem() == 91) {
                        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow2 = new HlsCusPrjQuotationCashflow();
                        hlsCusPrjQuotationCashflow2.setQuotationCashflowId(dt2.getSourceCashflowId());
                        hlsCusPrjQuotationCashflow2 = hlsCusPrjQuotationCashflowService.selectByPrimaryKey(requestCtx, hlsCusPrjQuotationCashflow2);
                        JSONObject jsonCashflowDTO2 = (JSONObject) JSONObject.toJSON(hlsCusPrjQuotationCashflow2);
                        HlsCusPrjQuotationCashflowHistory hlsCusPrjQuotationCashflowHistory2 = jsonCashflowDTO2.toJavaObject(HlsCusPrjQuotationCashflowHistory.class);
                        hlsCusPrjQuotationCashflowHistory2.setQuotationCashflowId(null);
                        hlsCusPrjQuotationCashflowHistory2.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                        hlsCusPrjQuotationCashflowHistory2 = hlsCusPrjCashflowHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationCashflowHistory2);
                        hlsCusPrjQuotationCashflowHistory.setSourceCashflowId(hlsCusPrjQuotationCashflowHistory2.getQuotationCashflowId());
                        hlsCusPrjCashflowHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationCashflowHistory);
                    }
                }
            }

            //复制details
            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            hlsCusPrjQuotationDetails.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
            for (HlsCusPrjQuotationDetails dt3 : hlsCusPrjQuotationDetailsList) {
                JSONObject jsonDetailDTO = (JSONObject) JSONObject.toJSON(dt3);
                HlsCusPrjQuotationDetailsHistory hlsCusPrjQuotationDetailsHistory = jsonDetailDTO.toJavaObject(HlsCusPrjQuotationDetailsHistory.class);
                hlsCusPrjQuotationDetailsHistory.setQuotationDeatilId(null);
                hlsCusPrjQuotationDetailsHistory.setQuotationId(hlsCusPrjQuotationHistory.getQuotationId());
                hlsCusPrjQuotationDetailsHistoryService.insertSelective(requestCtx, hlsCusPrjQuotationDetailsHistory);
            }
        }


    }
}