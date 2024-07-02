 package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.custom.JumpActivityCmd;
import com.hand.hap.activiti.dto.ActHiTaskTrans;
import com.hand.hap.activiti.dto.TaskNew;
import com.hand.hap.activiti.mapper.ActHiTaskTransMapper;
import com.hand.hap.activiti.mapper.TaskNewMapper;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

 /**
  * 工作流经办人节点审批通过跳转到退回前节点 任务监听器
  * Created by zhangdan 2022-11-16
  */
 @Component
 public class HlsActivitiJumpToReturnNodeTask implements TaskListener, IActivitiBean {
     @Autowired
     private TaskService taskService;
     @Autowired
     private ProcessEngineConfigurationImpl processEngineConfiguration;
     @Autowired
     private TaskNewMapper taskNewMapper;
     @Autowired
     private ActHiTaskTransMapper actHiTaskTransMapper;

     @Override
     public void notify(DelegateTask delegateTask) {
         String clickButtonEvent = delegateTask.getVariable("approveResult").toString();// 获取当前节点的点击操作
         String procintId =  delegateTask.getProcessInstanceId();

         List<Task> taskList = taskService.createTaskQuery().processInstanceId(procintId).list();
         Task task = taskList.get(0);
         if ("REJECTED".equals(clickButtonEvent)) {

         }else if("APPROVED_LAST".equals(clickButtonEvent)){
             List<ActHiTaskTrans> lastTastLists = actHiTaskTransMapper.selectLastTaskInactive(procintId);
             for (int i = 0; i < lastTastLists.size(); i++) {
                 ActHiTaskTrans taskNew= lastTastLists.get(i);
                 JumpActivityCmd jumpActivityCmd = new JumpActivityCmd(task.getId(), taskNew.getActivityId());
                 processEngineConfiguration.getCommandExecutor().execute(jumpActivityCmd);
             }
         }else if("APPROVED".equals(clickButtonEvent)){
             //如果重走的话 删除之前已执行完毕的并行分支
             taskNewMapper.deleteParallel(procintId);
         }
         ActHiTaskTrans deleteTrans = new ActHiTaskTrans();
         deleteTrans.setProcInstId(procintId);
         actHiTaskTransMapper.delete(deleteTrans);
     }
 }
