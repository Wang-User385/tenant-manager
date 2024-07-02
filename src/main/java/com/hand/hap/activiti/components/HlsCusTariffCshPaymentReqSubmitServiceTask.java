package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description: 关税付款申请审批工作流结束监听
 * @author: congweijing
 * @date: 2021/5/18 10:08
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusTariffCshPaymentReqSubmitServiceTask implements JavaDelegate, IActivitiBean ,IHlsCusActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService1;
    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";
    /**
     * 结束监听
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        String result = (String) delegateExecution.getVariable("approveResult");
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());


        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        String paymentReqId = delegateExecution.getProcessInstanceBusinessKey();
        hlsCusCshPaymentReqHd.setPaymentReqId(Long.parseLong(paymentReqId));
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);
        if ("APPROVED".equalsIgnoreCase(result)) {
            //审批通过逻辑
            hlsCusCshPaymentReqHd.setProcessInstanceId(processInstanceId);
            hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
            cshPaymentReqHdService1.tariffPaymentReqApproved(requestCtx,hlsCusCshPaymentReqHd);
        }
        hlsCusCshPaymentReqHd.setPaymentReqStatus(result);
        cshPaymentReqHdService.updateByPrimaryKeySelective(requestCtx,hlsCusCshPaymentReqHd);

    }

    /**
     * 一键通过/一键拒绝
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        String paymentReqId = hlsCusProcess.getBussinessKey();
        Long processInstanceId = Long.parseLong(hlsCusProcess.getProcessMap().get(0).get("proc_inst_id_").toString());

        hlsCusCshPaymentReqHd.setPaymentReqId(Long.parseLong(paymentReqId));
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);

        if (PASS.equals(hlsCusProcess.getType())) {
            hlsCusCshPaymentReqHd.setPaymentReqStatus("APPROVED");
            //审批通过逻辑
            hlsCusCshPaymentReqHd.setProcessInstanceId(processInstanceId);
            hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
            cshPaymentReqHdService1.tariffPaymentReqApproved(iRequest,hlsCusCshPaymentReqHd);
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusCshPaymentReqHd.setPaymentReqStatus("REJECTED");
        }
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest,hlsCusCshPaymentReqHd);
    }
}
