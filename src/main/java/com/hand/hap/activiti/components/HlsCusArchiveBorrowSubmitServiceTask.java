package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.archive.dto.HlsCusArchiveBorrow;
import com.hand.hls.archive.service.HlsCusArchiveBorrowService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:档案借阅
 * @author: congweijing
 * @date: 2021-08-07 13:48
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusArchiveBorrowSubmitServiceTask implements JavaDelegate, IActivitiBean, IHlsCusActivitiBean {

    @Autowired
    private HlsCusArchiveBorrowService hlsCusArchiveBorrowService;
    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";
    /**
     * 结束监听
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        String result = (String) delegateExecution.getVariable("approveResult");
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        Long borrowId = Long.valueOf(delegateExecution.getProcessInstanceBusinessKey());

        HlsCusArchiveBorrow hlsCusArchiveBorrow = new HlsCusArchiveBorrow();
        hlsCusArchiveBorrow.setBorrowId(borrowId);
        hlsCusArchiveBorrow.setBorrowReturnStatus(result);
        hlsCusArchiveBorrowService.updateByPrimaryKeySelective(iRequest,hlsCusArchiveBorrow);
    }
    /**
     * 一键通过/一键拒绝
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        String borrowId = hlsCusProcess.getBussinessKey();
        Long processInstanceId = Long.parseLong(hlsCusProcess.getProcessMap().get(0).get("proc_inst_id_").toString());
        HlsCusArchiveBorrow hlsCusArchiveBorrow = new HlsCusArchiveBorrow();
        hlsCusArchiveBorrow.setBorrowId(Long.parseLong(borrowId));
        if (PASS.equals(hlsCusProcess.getType())) {
            //审批通过逻辑
            hlsCusArchiveBorrow.setBorrowReturnStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusArchiveBorrow.setBorrowReturnStatus("REJECTED");
        }
        hlsCusArchiveBorrowService.updateByPrimaryKeySelective(iRequest,hlsCusArchiveBorrow);
    }
}
