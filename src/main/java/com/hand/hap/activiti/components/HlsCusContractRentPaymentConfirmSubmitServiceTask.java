package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.cont.dto.HlsCusConContractRentPaymentConfirm;
import com.hand.hls.cont.mapper.HlsCusConContractRentPaymentConfirmMapper;
import com.hand.hls.cont.service.IHlsCusConContractRentPaymentConfirmService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.gld.service.HlsCusConContractService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:
 * @author: congweijing
 * @date: 2021/4/29 16:33
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractRentPaymentConfirmSubmitServiceTask implements JavaDelegate, IActivitiBean, IHlsCusActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IHlsCusConContractRentPaymentConfirmService conContractRentPaymentConfirmService;
    @Autowired
    private HlsCusConContractRentPaymentConfirmMapper confirmMapper;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    /**
     * 结束监听
     *
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        String result = (String) delegateExecution.getVariable("approveResult");
        //关闭查询时附带权限
        requestCtx.setAttribute("wflRuleControlFlag", "Y");
        HlsCusConContractRentPaymentConfirm rentPaymentConfirm = new HlsCusConContractRentPaymentConfirm();
        String paymentConfirmId = delegateExecution.getProcessInstanceBusinessKey();
        rentPaymentConfirm.setPaymentConfirmId(Long.parseLong(paymentConfirmId));
        rentPaymentConfirm = confirmMapper.selectByPrimaryKey(rentPaymentConfirm);
        //databaseLockProvider.lock(rentPaymentConfirm);
        if ("APPROVING".equalsIgnoreCase(rentPaymentConfirm.getStatus())) {
            if ("APPROVED".equalsIgnoreCase(result)) {
                //审批通过逻辑
                try {
                    hlsCusConContractService.submitContractConfirmApproved(requestCtx, rentPaymentConfirm);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            }
            rentPaymentConfirm.setStatus(result);
            conContractRentPaymentConfirmService.updateByPrimaryKeySelective(requestCtx, rentPaymentConfirm);
        }
    }

    /**
     * 一键通过/一键拒绝
     *
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        HlsCusConContractRentPaymentConfirm rentPaymentConfirm = new HlsCusConContractRentPaymentConfirm();
        String paymentConfirmId = hlsCusProcess.getBussinessKey();
        rentPaymentConfirm.setPaymentConfirmId(Long.parseLong(paymentConfirmId));
        rentPaymentConfirm = confirmMapper.selectByPrimaryKey(rentPaymentConfirm);
        databaseLockProvider.lock(rentPaymentConfirm);
        if ("APPROVING".equalsIgnoreCase(rentPaymentConfirm.getStatus())) {
            if (PASS.equals(hlsCusProcess.getType())) {
                rentPaymentConfirm.setStatus("APPROVED");
                //审批通过逻辑
                try {
                    hlsCusConContractService.submitContractConfirmApproved(iRequest, rentPaymentConfirm);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
            } else if (REJECT.equals(hlsCusProcess.getType())) {
                rentPaymentConfirm.setStatus("REJECTED");
            }
            conContractRentPaymentConfirmService.updateByPrimaryKeySelective(iRequest, rentPaymentConfirm);
        }

    }
}
