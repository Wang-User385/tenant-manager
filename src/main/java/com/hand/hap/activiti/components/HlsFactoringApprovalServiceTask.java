package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/9/4 17:26
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringApprovalServiceTask implements JavaDelegate, IActivitiBean {
    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";

    //移交
    private static final String DELEGATE = "DELEGATE";

    //终止
    private static final String TERMINATE = "TERMINATE";


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    private static final List<String> specificNodeIds = new ArrayList<>(
            Arrays.asList("FACTORING_BUSINESS_APPROVALS6",
                    "FACTORING_BUSINESS_APPROVALS7",
                    "FACTORING_BUSINESS_APPROVALS8","FACTORING_BUSINESS_APPROVALS9"));

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);
        prjProject.setVirConProcessInstatnceId(processInstanceId);
        if (isValid(prjProject)) {
            if (APPROVED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(APPROVED);
                prjProject.setApprovedDate(new Date());
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(REJECTED);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
                manualEnd(delegateExecution);
            } else if (PEER_REJECTED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(PEER_REJECTED);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            } else if (DELEGATE.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(DELEGATE);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            }
        }
    }

    private void manualEnd(DelegateExecution delegateExecution) {
        List<Task> list = taskService
                .createTaskQuery()
                .processInstanceId(delegateExecution.getProcessInstanceId())
                .list();
        for (Task task : list) {
            String currentActivityId = task.getTaskDefinitionKey();
            if (!StringUtils.isEmpty(currentActivityId)) {
                if (specificNodeIds.contains(currentActivityId)) {
                    runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "执行特殊节点结束工作流");
                }
            }
        }
    }

    private boolean isValid(HlsCusPrjProject prjProject) {
        return !APPROVED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !REJECTED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !PEER_REJECTED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !DELEGATE.equalsIgnoreCase(prjProject.getProjectStatus());
    }
}
