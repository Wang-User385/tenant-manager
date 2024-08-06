package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.IPrjProjectService;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 投放审查工作流结束保存事件
 * @Author: lipan
 * @Date: Created in 16:51 2024/7/21
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractInceptServiceTask implements JavaDelegate, IActivitiBean {
    private static final Logger logger = LoggerFactory.getLogger(PrjSignCreateContractServiceTask.class);


    private static final String PROJECT = "project";

    private static final String IREQUEST = "iRequest";
    private static final String APPROVE_RESULT = "approveResult";
    private static final String START_USER_ID = "startUserId";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    private final static String CONTRACT_TEXT_STATUS_AUDITED = "AUDITED";
    public static final String APPROVING = "APPROVING";

    @Autowired
    private IConContractService conContractService;
    @Autowired
    private IPrjProjectService prjProjectService;
    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private HlsCusConContractCashflowService cashflowService;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    HlsCusBpMasterBankAccountMapper hlsCusBpMasterBankAccountMapper;
    @Autowired
    HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution delegateExecution) {
        logger.info("--------进件投放审查工作流 结束监听器HlsCusConContractInceptServiceTask--------");
        String prj = (String) delegateExecution.getVariable(PROJECT);
        HlsCusPrjProject prjProject = JSON.parseObject(prj, HlsCusPrjProject.class);

        IRequest requestCtx = (IRequest) delegateExecution.getVariable(IREQUEST);
        String result = (String) delegateExecution.getVariable(APPROVE_RESULT);
        String userId = String.valueOf(delegateExecution.getVariable(START_USER_ID));
        requestCtx.setUserId(Long.valueOf(userId));

        HlsCusPrjProject project = prjProjectService.selectByPrimaryKey(requestCtx, prjProject);

        if(APPROVING.equalsIgnoreCase(project.getInvestmentStatus())) {
            //流程审批通过后生成合同
            if (APPROVED.equalsIgnoreCase(result)) {
                //创建合同及现金流数据
                /*conContractService.saveConContractFromPrjProjectSign(requestCtx, prjProject);

                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                //查询现金流数据
                List<HlsCusConContractCashflow> cashflowList = cashflowService.selectConLoanRequest(requestCtx,conContractCashflow, 1, 1);
                //创建付款头表和行表数据
                cshPaymentReqHdService.tariffPaymentReqCreate(requestCtx, cashflowList);*/

                project.setInvestmentStatus(APPROVED);
                prjProject.setSignDate(new Date());
                prjProjectService.updateByPrimaryKeySelective(requestCtx, project);
            }else if(REJECTED.equalsIgnoreCase(result)){
                //审批未通过，修改投放审查审批状态
                project.setInvestmentStatus(REJECTED);
                prjProjectService.updateByPrimaryKeySelective(requestCtx, project);
            }
        }

    }

    //创建付款申请头表和行表
    /*public void createPaymentReq(IRequest requestCtx,HlsCusPrjProject project){
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setProjectId(project.getProjectId());
        HlsCusConContract hlsCusConContract1 = conContractService.selectByPrimaryKey(requestCtx, hlsCusConContract);
        List<HlsCusPrjQuotation> hlsCusPrjQuotations = hlsCusPrjQuotationMapper.selectQuoByProjectId(project.getProjectId());
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        //hlsCusBpMaster.setBpId
        List<HlsCusBpMaster> select = hlsCusBpMasterMapper.select(hlsCusBpMaster);
        HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount = hlsCusBpMasterBankAccountMapper.selectBankByBpId(hlsCusBpMaster.getBpId());
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setCompanyId(requestCtx.getCompanyId());
        hlsCusCshPaymentReqHd.setDocumentCategory("CSH_PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setDocumentType("PAYMENT_REQ");
        hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
        String value = getCodeValue(requestCtx);
        hlsCusCshPaymentReqHd.setPaymentReqNumber(value);
        hlsCusCshPaymentReqHd.setPaymentReqStatus("APPROVED");
        hlsCusCshPaymentReqHd.setProjectName(project.getProjectName());
        hlsCusCshPaymentReqHd.setProjectId(project.getProjectId());
        hlsCusCshPaymentReqHd.setPaymentReqDate(hlsCusPrjQuotations.get(0).getLeaseStartDate());
        hlsCusCshPaymentReqHd.setCurrency("CNY");
        hlsCusCshPaymentReqHd.setSourceDocId(project.getProjectId());
        hlsCusCshPaymentReqHd.setSourceDocType("CON_CONTRACT");
        hlsCusCshPaymentReqHd.setDeductFlag("N");
        hlsCusCshPaymentReqHd.setSourceContractId(hlsCusConContract1.getContractId());
        hlsCusCshPaymentReqHd.setProposedLaunchDate(hlsCusConContract1.getRefundDate());
        hlsCusCshPaymentReqHd.setLoanTotalAmount(hlsCusConContract1.getLoanTotalAmount());
        hlsCusCshPaymentReqHd.setContractCurrency(hlsCusConContract1.getContractCurrency());
        hlsCusCshPaymentReqHd.setContractCurrencyId(hlsCusConContract1.getContractCurrency());
        hlsCusCshPaymentReqHd.setFinanceAmount(hlsCusConContract1.getFinanceAmount());
        hlsCusCshPaymentReqHd.setSumToufangAmount(hlsCusConContract1.getSumToufangAmount());
        hlsCusCshPaymentReqHd.setContractBalance(hlsCusConContract1.getContractBalance());
        hlsCusCshPaymentReqHd.setProjectName(project.getProjectName());
        hlsCusCshPaymentReqHd.setBpName(hlsDurationDepositList.get(0).getBpName());
        hlsCusCshPaymentReqHd.setFinanceAmount(hlsDurationDepositList.get(0).getFinanceAmount());
        hlsCusCshPaymentReqHd.setEmployeeId(hlsDurationHd.getApplyPerson());
        hlsCusCshPaymentReqHd.setUnitId(hlsDurationHd.getApplyUnitId());
        hlsCusCshPaymentReqHd.setPaymentType("REFUND_DEPOSIT");
        hlsCusCshPaymentReqHd.setTransferStatus("NEW");
        hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
        hlsCusCshPaymentReqHd.setCreatedBy(requestCtx.getUserId());
        hlsCusCshPaymentReqHd.setCreationDate(new Date());
        hlsCusCshPaymentReqHd.setLoanType("ROUTINE");
        cshPaymentReqHdService.insertSelective(requestCtx, hlsCusCshPaymentReqHd);
    }

    public String getCodeValue(IRequest requestContext) {
        Map<String, String> params = new HashMap<String, String>();
        return codingRuleValuesService.getCodeRuleValue(requestContext, "CSH_PAYMENT_REQ", "PAYMENT_REQ", "PAYMENT_REQ", params);
    }*/

}
