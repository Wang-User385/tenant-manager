 package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.custom.JumpActivityCmd;
import com.hand.hap.activiti.dto.ActHiTaskTrans;
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
 * 工作流审批退回值经办人任务监听器
 * Created by qixiang.shao on 2017/11/19
 */
@Component
public class HlsCusActivitiSkipEndNodeEventTask implements TaskListener,IActivitiBean {
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

        // 如果是审批退回操作，则执行以下方法
        if ("REJECTED".equals(clickButtonEvent)) {
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(delegateTask.getProcessInstanceId()).list();
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                JumpActivityCmd jumpActivityCmd = null;

                insertTaskTrans(task);
                if (i == 0) {
                    jumpActivityCmd = new JumpActivityCmd(task.getId(), "sid-rHAU6o6D-6QbN-4laU-89LQ-cSSMgkK3aMmb");// 遍历所有节点,都执行强制跳到结束节点的跳转动作,配置时需要把结束的Id写死
                } else {
                    jumpActivityCmd = new JumpActivityCmd(task.getId(), "sid-activiti-workflow-end");// 遍历所有节点,都执行强制跳到结束节点的跳转动作,配置时需要把结束的Id写死
                }
                //把并行网关的execution干掉
                processEngineConfiguration.getCommandExecutor().execute(jumpActivityCmd);
            }
            //并行节点其中一个节点走完,另外一个节点退回的时候会出现问题,第二次再经过并行节点结束的时候,不会等待另外一个分支汇集就直接能到后面了,加了一个sql,手工处理了act_ru_exection表的并行网关

            //taskNewMapper.deleteParallel(procintId);
        } else if ("APPROVED_RETURN".equals(clickButtonEvent)) {
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(delegateTask.getProcessInstanceId()).list();

            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                JumpActivityCmd jumpActivityCmd = null;
                insertTaskTrans(task);
                if (i == 0) {
                    jumpActivityCmd = new JumpActivityCmd(task.getId(), "sid-rHAU6o6D-6QbN-4laU-89LQ-cSSMgkK3aMmb");// 遍历所有节点,都执行强制跳到结束节点的跳转动作,配置时需要把结束的Id写死
                } else {
                    jumpActivityCmd = new JumpActivityCmd(task.getId(), "sid-activiti-workflow-end");// 遍历所有节点,都执行强制跳到结束节点的跳转动作,配置时需要把结束的Id写死
                }
                processEngineConfiguration.getCommandExecutor().execute(jumpActivityCmd);
            }
            //并行节点其中一个节点走完,另外一个节点退回的时候会出现问题,第二次再经过并行节点结束的时候,不会等待另外一个分支汇集就直接能到后面了,加了一个sql,手工处理了act_ru_exection表的并行网关
            //taskNewMapper.deleteParallel(procintId);
        }
    }

    private void insertTaskTrans(Task task){
        ActHiTaskTrans trans = new ActHiTaskTrans();
        trans.setTaskId(Long.parseLong(task.getId()));
        trans.setActivityId(task.getTaskDefinitionKey());
        trans.setProcInstId(task.getProcessInstanceId());
        actHiTaskTransMapper.insert(trans);
    }
}
