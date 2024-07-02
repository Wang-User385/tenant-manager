package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.mapper.HlsCusActVarMapper;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.ast.service.INoticeManageService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.CshPaymentReqLnBankAccountMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.csh.service.IMainCshWriteOffService;
import com.hand.hls.fct.dto.HlsCusFctContractWithdrawCf;
import com.hand.hls.fct.mapper.HlsCusFctContractWithdrawCfMapper;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.hls.mapper.HlsCusFundingPlanLnMapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yenick
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContracPaymentNodeSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;


    @Autowired
    CshTransactionService cshTransactionService;


        Map<String, String> params = new HashMap<String, String>();


    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;


    @Autowired
    HlsCusConContractMapper conContractMapper;

    @Autowired
    HlsCusConContractCashflowMapper cashflowMapper;




        @Override
    public void execute(DelegateExecution delegateExecution){
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String contractId = String.valueOf(delegateExecution.getVariable("contractId"));

        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());

        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(Long.parseLong(delegateExecution.getProcessInstanceBusinessKey()));
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(requestCtx, hlsCusCshPaymentReqHd);

        if ("APPROVED".equalsIgnoreCase(result)) {

            flag = "APPROVED";

            hlsCusCshPaymentReqHd.setPaymentApprovedStatus(flag);
            hlsCusCshPaymentReqHd.setPaymentProcessInstanceId(processInstanceId);
            cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCshPaymentReqHd);

        } else if ("REJECTED".equalsIgnoreCase(result)) {
            flag = "REJECTED";
            hlsCusCshPaymentReqHd.setPaymentApprovedStatus(flag);
            hlsCusCshPaymentReqHd.setPaymentProcessInstanceId(processInstanceId);
            cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx, hlsCusCshPaymentReqHd);

        }











    }
}