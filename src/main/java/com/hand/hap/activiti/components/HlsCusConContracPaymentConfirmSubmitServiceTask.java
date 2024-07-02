package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;

import com.hand.hls.ast.service.INoticeManageService;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bill.service.IhlsBillRequestService;

import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;

import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.csh.service.IMainCshWriteOffService;
import com.hand.hls.fct.mapper.HlsCusFctContractWithdrawCfMapper;

import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;

import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Yenick
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContracPaymentConfirmSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;









    @Autowired
    CshTransactionService cshTransactionService;



    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;


    @Autowired
    HlsCusConContractMapper conContractMapper;


    @Autowired
    HlsCusConContractCashflowMapper cashflowMapper;

    @Autowired
    private hlsBillRequestMapper hlsBillRequestMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;




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
            HlsCusCshPaymentReqLn  hlsCusCshPaymentReqln =new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqln.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqlns = hlsCusCshPaymentReqLnMapper.select(hlsCusCshPaymentReqln);
            for (HlsCusCshPaymentReqLn paymentReqDt : hlsCusCshPaymentReqlns) {
                hlsBillRequest   hlsBillRequest = new hlsBillRequest();
                hlsBillRequest.setBillId(paymentReqDt.getBillId());
                hlsBillRequest hlsBillRequests= hlsBillRequestMapper.selectByPrimaryKey(hlsBillRequest);
                if (hlsBillRequests != null) {
                    hlsBillRequests.setBillStatus("TICKETS_ISSUED");

                    hlsBillRequestMapper.updateByPrimaryKeySelective(hlsBillRequests);
                }

            }
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