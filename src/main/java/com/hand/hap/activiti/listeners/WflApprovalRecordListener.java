package com.hand.hap.activiti.listeners;

import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hls.sys.dto.SysWflApprovalRecords;
import com.hand.hls.sys.service.SysWflApprovalRecordsService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/8/20
 * @description: 兴业记录工作流相关信息 及 审批人的mac地址
 */

public class WflApprovalRecordListener implements TaskListener, IActivitiConstants, AppContextInitListener {
    @Autowired
    private SysWflApprovalRecordsService service;
    @Autowired
    private HttpSession session;

    private RepositoryService repositoryService;
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateTask delegateTask) {
        TaskEntity task = (TaskEntity) delegateTask;
        ExecutionEntity taskExecution = task.getExecution();
        IRequest iRequest = (IRequest) taskExecution.getVariable("iRequest");
        String pName = (String) taskExecution.getVariable("pName");
        String documentName = (String) taskExecution.getVariable("documentName");
        String documentNumber = (String) taskExecution.getVariable("documentNumber");
        String processInstanceId = taskExecution.getProcessInstanceId();
        String activityId = taskExecution.getActivityId();
        //获取session中的mac地址，LeafAuthenticationSuccessListener onAuthenticationSuccess 时插入
        String macAddress = null;
        if(session.getAttribute("machine_serial")!=null){
            macAddress = session.getAttribute("machine_serial").toString();
        }

        String assignee = task.getAssignee();
        //根据流程定义id获取bpmnModel对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        //获取当前节点信息
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        String taskName = flowNode.getName();

        Date approvalTime = new Date();
        //获取当前的审批意见
        String approveResultDesc = null;
        String comment = null;
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery().processInstanceId(taskExecution.getProcessInstanceId());
        List<ProcessInstance> processInstanceList = processInstanceQuery.list();
        if (processInstanceList.size() != 0) {
            ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) processInstanceList.get(0);
            approveResultDesc = executionEntity.getVariables().get("approveResultDesc") == null ? "" : executionEntity.getVariables().get("approveResultDesc").toString();
            comment = executionEntity.getVariables().get("comment") == null ? "" : executionEntity.getVariables().get("comment").toString();
        }

        SysWflApprovalRecords sysWflApprovalRecords = new SysWflApprovalRecords();
        sysWflApprovalRecords.setProcessInstanceId(Long.parseLong(processInstanceId));
        sysWflApprovalRecords.setActivityId(activityId);
        sysWflApprovalRecords.setDocumentName(documentName);
        sysWflApprovalRecords.setDocumentNumber(documentNumber);
        sysWflApprovalRecords.setProcessName(pName);
        sysWflApprovalRecords.setTaskName(taskName);
        sysWflApprovalRecords.setApprovalTime(approvalTime);
        sysWflApprovalRecords.setApprover(assignee);
        sysWflApprovalRecords.setAction(approveResultDesc);
        sysWflApprovalRecords.setApprovalComment(comment);
        sysWflApprovalRecords.setMacAddress(macAddress);
        service.insertSelective(iRequest, sysWflApprovalRecords);
    }


    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        runtimeService = applicationContext.getBean(RuntimeService.class);
        repositoryService = applicationContext.getBean(RepositoryService.class);
    }

}
