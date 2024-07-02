package com.hand.hap.activiti.listeners;

import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.custom.JumpActivityCmd;
import com.hand.hap.activiti.dto.TaskActionRequestExt;
import com.hand.hap.activiti.mapper.ActHiTaskinstMapper;
import com.hand.hap.activiti.mapper.TaskNewMapper;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.sys.dto.SysWflApprovalRecords;
import com.hand.hls.sys.service.SysWflApprovalRecordsService;
import lombok.SneakyThrows;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.*;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/8/20
 * @description: 节点跳转
 */
@Component
public class WflJumpNodeListener implements  TaskListener, IActivitiBean  {
    @Autowired
    private TaskService taskService;
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private ActHiTaskinstMapper actHiTaskinstMapper;

    @SneakyThrows
    @Override
    public void notify(DelegateTask delegateTask) {
//            TaskEntity task = (TaskEntity) delegateTask;
//                IRequest currentRequest = RequestHelper.getCurrentRequest();
//                TaskActionRequestExt actionRequest = new TaskActionRequestExt();
//                actionRequest.setAction("APPROVED");
//                actionRequest.setComment("重复审批,系统自动审批通过");
//                actionRequest.setAssignee("1");
//                actionRequest.setCurrentTaskId(task.getId());
//
//                List<RestVariable> variables = new   ArrayList<RestVariable>();
//                RestVariable restVariable1 = new RestVariable();
//                restVariable1.setName("approveResult");
//                restVariable1.setValue("APPROVED");
//                variables.add(restVariable1);
//
//                RestVariable restVariable2 = new RestVariable();
//                restVariable1.setName("approveResultDesc");
//                restVariable1.setValue("同意");
//                variables.add(restVariable1);
//
//                RestVariable restVariable3 = new RestVariable();
//                restVariable1.setName("comment");
//                restVariable1.setValue("重复审批,系统自动审批通过");
//                variables.add(restVariable1);
//
//                actionRequest.setVariables(variables);
//                activitiService.executeTaskAction(currentRequest, task.getId(), actionRequest, false);
        //查询到最后一次退回的所有审批人,如果之前审批了



        TaskEntity task = (TaskEntity) delegateTask;

        Map map = new HashMap();
        String lastApproveAction = "";
        map.put("procInstId", task.getProcessInstanceId());
        map.put("userId",task.getAssignee());
        lastApproveAction = actHiTaskinstMapper.selectDumpApprove(map);
        if(lastApproveAction==null){
            return;
        }

        Map<String, Object> vMap = new HashMap<String, Object>();

        String approveResult = "APPROVED";
        String approveResultResult = "同意";
        vMap.put("approveResult", approveResult);
        vMap.put("approveResultDesc", approveResultResult);
        vMap.put("comment", "重复审批,系统自动审批通过");

        // 添加审批备注
        Authentication.setAuthenticatedUserId(task.getAssignee());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, approveResultResult);
        taskService.addComment(task.getId(), task.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, "重复审批,系统自动审批通过");
        // 自动审批
        taskService.complete(task.getId(), vMap);



    }


}
