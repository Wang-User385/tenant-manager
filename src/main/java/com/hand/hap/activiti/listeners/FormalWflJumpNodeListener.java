package com.hand.hap.activiti.listeners;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.mapper.ActHiTaskinstMapper;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.IPrjProjectService;
import lombok.SneakyThrows;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
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
public class FormalWflJumpNodeListener implements  TaskListener, IActivitiBean  {
    @Autowired
    private TaskService taskService;
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private ActHiTaskinstMapper actHiTaskinstMapper;
    @Autowired
    HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @SneakyThrows
    @Override
    public void notify(DelegateTask delegateTask) {
        TaskEntity task = (TaskEntity) delegateTask;
        String prj = (String) delegateTask.getVariable("project");
        HlsCusPrjProject prjProject1 = JSON.parseObject(prj, HlsCusPrjProject.class);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(prjProject1.getProjectId());
        //IRequest requestCtx = (IRequest) task.getVariable("iRequest");

        //获取项目信息
        HlsCusPrjProject prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProject1.getProjectId());
        String returnStatus = prjProject.getConfirmStatus();
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
        Authentication.setAuthenticatedUserId(task.getAssignee());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, approveResultResult);
        taskService.addComment(task.getId(), task.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, comment);
        // 自动审批
        taskService.complete(task.getId(), vMap);



    }


}
