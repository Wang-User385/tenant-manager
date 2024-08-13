package com.hand.hap.activiti.components;

import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: lipan
 * @date: 2024/8/11
 * @description: 进件正审风控审核（自动审核）节点跳转
 */
@Component
public class FormalWflJumpNodeServiceTask implements JavaDelegate, IActivitiBean  {
    @Autowired
    private TaskService taskService;
    @Autowired
    HlsCusPrjProjectMapper hlsCusPrjProjectMapper;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        String endTaskFlag = "N";
        if (delegateExecution.getVariable("endTaskFlag") != null) {
            endTaskFlag = delegateExecution.getVariable("endTaskFlag").toString();
        }

        if (endTaskFlag.equals("Y")) {
            //监听器执行了两遍，很奇怪，用标志来使其执行一遍
            return;
        }
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        //IRequest requestCtx = (IRequest) task.getVariable("iRequest");
        //获取项目信息
        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(projectId);
        String returnStatus = prjProject.getConfirmStatus();
        //获取流程实例id
        String taskDefinitionKey = "sid-CLP0n90Z-PXL4-4Q7V-8tkf-1r07cxSMTfxH";
        List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey(taskDefinitionKey).list();
        //String processInstanceId = delegateExecution.getProcessInstanceId();
        //List<Task> tasks1 = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        Task task = null;
        if (!tasks.isEmpty()) {
            task = tasks.get(0);
        }
        String approveResult = "";
        String approveResultResult = "";
        String comment = "";
        //判断同盾接口返回的结果是通过还是拒绝或谨慎通过
        if(returnStatus.equals("APPROVED")){
            approveResult = "APPROVED";
            approveResultResult = "同意";
            comment = "同盾风控系统返回审批通过,系统自动审批通过该节点";
        }else if(returnStatus.equals("REJECTED")){
            approveResult = "REJECTED";
            approveResultResult = "拒绝";
            comment = "同盾风控系统返回审批拒绝,系统自动审批拒绝该节点";
        }else if(returnStatus.equals("CAREFUL_APPROVED")){
            approveResult = "CAREFUL_APPROVED";
            approveResultResult = "谨慎通过";
            comment = "同盾风控系统返回谨慎通过,系统自动审批通过该节点";
        }
        Map<String, Object> vMap = new HashMap<String, Object>();


        vMap.put("approveResult", approveResult);
        vMap.put("approveResultDesc", approveResultResult);
        vMap.put("comment", comment);

        // 添加审批备注
        String assignee = (String) delegateExecution.getVariables().get("assignee");
        task.setAssignee("510");
        Authentication.setAuthenticatedUserId(task.getAssignee());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, approveResultResult);
        taskService.addComment(task.getId(), task.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, comment);
        // 自动审批
        taskService.complete(task.getId(), vMap);
        delegateExecution.setVariable("endTaskFlag", "Y");
    }

    @Override
    public String getBeanName() {
        return IActivitiBean.super.getBeanName();
    }
}
