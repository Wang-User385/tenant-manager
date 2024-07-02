package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.archive.dto.HlsCusArchive;
import com.hand.hls.archive.dto.HlsCusArchiveApproval;
import com.hand.hls.archive.service.HlsCusArchiveApprovalService;
import com.hand.hls.archive.service.HlsCusArchiveService;
import com.hand.hls.exception.HlsCusException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:移交申请结束监听
 * @author: congweijing
 * @date: 2021-08-05 15:31
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusArchiveSubmitServiceTask implements JavaDelegate, IActivitiBean, IHlsCusActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusArchiveApprovalService hlsCusArchiveApprovalService;
    @Autowired
    private HlsCusArchiveService hlsCusArchiveService;
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
        String endTaskFlag = "N";
        if (delegateExecution.getVariable("endTaskFlag") != null) {
            endTaskFlag = delegateExecution.getVariable("endTaskFlag").toString();
        }

        if (endTaskFlag.equals("Y")) {
            //监听器执行了两遍，很奇怪，用标志来使其执行一遍
            return;
        }

        Long projectId = null;
        HlsCusArchiveApproval para = new HlsCusArchiveApproval();
        para.setProcessInstanceId(processInstanceId);
        List<HlsCusArchiveApproval> list = hlsCusArchiveApprovalService.selectSelective(iRequest,para);
        for (HlsCusArchiveApproval hlsCusArchiveApproval : list) {
            HlsCusArchive hlsCusArchive = new HlsCusArchive();
            hlsCusArchive.setArchiveId(hlsCusArchiveApproval.getArchiveId());
            hlsCusArchive = hlsCusArchiveService.selectByPrimaryKey(iRequest,hlsCusArchive);
            projectId = hlsCusArchive.getProjectId();
            databaseLockProvider.lock(hlsCusArchive);
            if(hlsCusArchive.getHandoverDate() == null){
                hlsCusArchive.setHandoverDate(new Date());
            }
            hlsCusArchive.setHandoverStatus(result);

            hlsCusArchiveService.updateByPrimaryKey(iRequest,hlsCusArchive);
        }

        if ("APPROVED".equalsIgnoreCase(result) && projectId!=null) {
            //审批通过逻辑
            try {
                hlsCusArchiveService.approvedWfl(iRequest,projectId);
            } catch (HlsCusException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        delegateExecution.setVariable("endTaskFlag", "Y");

    }

    /**
     * 一键通过/一键拒绝
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        String bussinessKey = hlsCusProcess.getBussinessKey();
        Long processInstanceId = Long.parseLong(hlsCusProcess.getProcessMap().get(0).get("proc_inst_id_").toString());
        Long projectId = null;
        HlsCusArchiveApproval para = new HlsCusArchiveApproval();
        para.setProcessInstanceId(processInstanceId);
        List<HlsCusArchiveApproval> list = hlsCusArchiveApprovalService.selectSelective(iRequest,para);
        for (HlsCusArchiveApproval hlsCusArchiveApproval : list) {

            HlsCusArchive hlsCusArchive = new HlsCusArchive();
            hlsCusArchive.setArchiveId(hlsCusArchiveApproval.getArchiveId());
            hlsCusArchive = hlsCusArchiveService.selectByPrimaryKey(iRequest,hlsCusArchive);
            projectId = hlsCusArchive.getProjectId();
            databaseLockProvider.lock(hlsCusArchive);
            if (PASS.equals(hlsCusProcess.getType())) {
                //审批通过逻辑
                try {
                    hlsCusArchiveService.approvedWfl(iRequest,projectId);
                } catch (HlsCusException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }
                if(hlsCusArchive.getHandoverDate() == null){
                    hlsCusArchive.setHandoverDate(new Date());
                }
                hlsCusArchive.setHandoverStatus("APPROVED");
            } else if (REJECT.equals(hlsCusProcess.getType())) {
                hlsCusArchive.setHandoverStatus("REJECTED");
            }
            hlsCusArchiveService.updateByPrimaryKey(iRequest,hlsCusArchive);
        }
    }
}
