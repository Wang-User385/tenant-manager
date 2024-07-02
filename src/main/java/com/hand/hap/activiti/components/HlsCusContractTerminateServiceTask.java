package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description: 合同结束工作流审批结束
 * @author: congweijing
 * @date: 2021/5/21 14:05
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractTerminateServiceTask implements JavaDelegate, IActivitiBean,IHlsCusActivitiBean {
    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractMapper contractMapper;

    /**
     * 结束监听
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        String result = (String) delegateExecution.getVariable("approveResult");

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        String projectId = delegateExecution.getProcessInstanceBusinessKey();
        hlsCusPrjProject.setProjectId(Long.parseLong(projectId));
        databaseLockProvider.lock(hlsCusPrjProject);
        if ("APPROVED".equalsIgnoreCase(result)) {
            //审批通过逻辑
            hlsCusPrjProject.setContractStatus("TERMINATE");
            //支付表结束
            HlsCusConContract contract = new HlsCusConContract();
            contract.setProjectId(hlsCusPrjProject.getProjectId());
            contract.setContractStatus("TERMINATE");
            contractMapper.updateByProjectId(contract);
        }
        hlsCusPrjProject.setTerminateApprovalStatus(result);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx,hlsCusPrjProject);
    }
    /**
     * 一键通过/一键拒绝
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        String projectId = hlsCusProcess.getBussinessKey();

        hlsCusPrjProject.setProjectId(Long.parseLong(projectId));
        databaseLockProvider.lock(hlsCusPrjProject);

        if (PASS.equals(hlsCusProcess.getType())) {
            hlsCusPrjProject.setTerminateApprovalStatus("APPROVED");
            //审批通过逻辑
            hlsCusPrjProject.setContractStatus("TERMINATE");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusPrjProject.setTerminateApprovalStatus("REJECTED");
        }
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest,hlsCusPrjProject);
    }
}
