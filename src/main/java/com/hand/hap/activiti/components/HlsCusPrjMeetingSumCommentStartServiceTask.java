package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import lombok.SneakyThrows;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/20 21:12
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjMeetingSumCommentStartServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService service;

    public HlsCusPrjMeetingSumCommentStartServiceTask() {
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");

        if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equals(result)) {
            if (projectId != null) {
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(projectId);
                hlsCusPrjProject = service.selectByPrimaryKey(requestCtx, hlsCusPrjProject);
                String voteStatus = hlsCusPrjProject.getVoteStatus();
                String voteComment = hlsCusPrjProject.getVoteComment();
                String fiveCategories = hlsCusPrjProject.getFiveCategories();
                String meetingProcessInstanceId = delegateExecution.getProcessInstanceId();
                if (meetingProcessInstanceId != null) {
                    hlsCusPrjProject.setMeetingProcessInstanceId(meetingProcessInstanceId);
                    service.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
                }
                if (voteStatus != null) {
                    if ("VETO".equals(voteStatus) || "FURTHER".equals(voteStatus)) {
                        if (voteComment != null) {
                            delegateExecution.setVariable("functionUsage", "QUERY");
                            delegateExecution.setVariable("maintainType", "READONLY");
                        } else {
                            throw new HlsCusException("请填写业审委综合意见！");
                        }
                    } else {
                        if (voteComment != null && fiveCategories != null) {
                            delegateExecution.setVariable("functionUsage", "QUERY");
                            delegateExecution.setVariable("maintainType", "READONLY");
                        } else if (voteComment != null && fiveCategories == null) {
                            throw new HlsCusException("请填写风险分类！");
                        } else if (voteComment == null && fiveCategories != null) {
                            throw new HlsCusException("请填写业审委综合意见！");
                        } else {
                            throw new HlsCusException("请填写业审委综合意见或风险分类！");
                        }
                    }
                } else {
                    throw new HlsCusException("请填写业审委审议结果！");
                }
            }
        }
    }

}
