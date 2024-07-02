package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * description 评委会委员表决
 *
 * @author MengXi Liu 2021年12月2日
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusJuryMembersVoteServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private IProjectMeetingApproverService approverService;

    public HlsCusJuryMembersVoteServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");

        //获取当前的审批意见
        String approveResultDesc = (String) delegateExecution.getVariables().get("approveResultDesc");
        String comment = (String) delegateExecution.getVariables().get("comment");
        String assignee = (String) delegateExecution.getVariables().get("assignee");

        if (assignee != null) {
            ProjectMeetingApprover meetingApprover = new ProjectMeetingApprover();
            meetingApprover.setApproverUserId(Long.valueOf(assignee));
            meetingApprover.setProjectId(projectId);
            List<ProjectMeetingApprover> meetingApprovers = approverService.selectSelective(requestCtx, meetingApprover);
            meetingApprovers.get(0).setVoteComment(comment);
            meetingApprovers.get(0).setVoteResult(approveResultDesc);
            approverService.updateByPrimaryKey(requestCtx, meetingApprovers.get(0));
        }
    }
}
