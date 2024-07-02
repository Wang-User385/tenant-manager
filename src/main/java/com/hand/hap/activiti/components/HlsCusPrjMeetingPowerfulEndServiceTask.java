package com.hand.hap.activiti.components;


import com.hand.hap.activiti.core.IActivitiConstants;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjMeetingPowerfulEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String DIRECTOR = "DIRECTOR";
    private static final String PRESIDENT = "PRESIDENT";

    @Autowired
    private TaskService taskService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    DatabaseLockProvider databaseLockProvider;

    public HlsCusPrjMeetingPowerfulEndServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");
        String currentActivityId = delegateExecution.getCurrentActivityId();
        String powerfulPerson = (String) delegateExecution.getVariable("powerfulPerson");
        //有权人节点id
        String directorActivityId = "sid-4Xq5O0HC-PaKE-4W2W-8O9j-cywcm9zMGtoW";
        String presidentActivityId = "sid-rVgANl4e-5e79-4ALB-86w1-8SeL83tR6rwY";

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);

        if (powerfulPerson != null && powerfulPerson.trim().length() != 0) {
            if (DIRECTOR.equals(powerfulPerson) && directorActivityId.equals(currentActivityId)) {
                this.endTask(requestCtx, prjProject, result);
            }
            if (PRESIDENT.equals(powerfulPerson) && presidentActivityId.equals(currentActivityId)) {
                this.endTask(requestCtx, prjProject, result);
            }
        }
    }

    private void endTask(IRequest requestCtx, HlsCusPrjProject prjProject, String result) {
        databaseLockProvider.lock(prjProject);
        String taskDefinitionKey = "sid-0HFyefQO-qElg-4aEs-8wfY-ixMUqYk3RA4A";
        List<Task> taskList = taskService.createTaskQuery().taskDefinitionKey(taskDefinitionKey).list();
        if (taskList.size() > 0) {
            for (Task item : taskList) {
                Long processInstanceId = prjProject.getProcessInstanceId();
                if (processInstanceId != null) {
                    if (String.valueOf(processInstanceId).equals(item.getProcessInstanceId())) {
                        if(!prjProject.getProjectStatus().equals(result)){
                            prjProject.setProjectStatus(result);
                            hlsCusPrjProjectService.updateByPrimaryKey(requestCtx, prjProject);
                            //结束节点
                            Map<String, Object> vMap = new HashMap<String, Object>(2);
                            vMap.put("approveResult", HlsCusConstant.WORKFLOW_STATUS.APPROVED);
                            vMap.put("approveResultResult", "同意");
                            vMap.put("comment", "编号为" + prjProject.getProjectNumber() + "的项目投票已完成。");
                            // 添加审批备注
                            taskService.addComment(item.getId(), item.getProcessInstanceId(), IActivitiConstants.COMMENT_ACTION, "自动审批");
                            taskService.addComment(item.getId(), item.getProcessInstanceId(), IActivitiConstants.PROP_COMMENT, "编号为" + prjProject.getProjectNumber() + "的项目投票已完成。");
                            // 自动审批
                            taskService.complete(item.getId(), vMap);
                        }
                    }
                }
            }
        }
    }
}
