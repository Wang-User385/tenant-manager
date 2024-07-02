package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.activiti.dto.HlsCusActMeeting;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;
import com.hand.hls.activiti.mapper.HlsCusActMeetingMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingJudgeService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SavePrjMeetingCommentsTask implements TaskListener, IActivitiBean {


    @Autowired
    private HlsCusActMeetingJudgeService hlsCusActMeetingJudgeService;

    @Autowired
    private HlsCusActMeetingMapper hlsCusActMeetingMapper;


    @Override
    public void notify(DelegateTask delegateTask) {

        IRequest iRequest = RequestHelper.getCurrentRequest();
        String prj = (String)delegateTask.getExecution().getVariable("hlsCusPrjProject");
        HlsCusPrjProject prjProject= JSON.parseObject(prj, HlsCusPrjProject.class);
        String result = (String)delegateTask.getExecution().getVariable("approveResult");
        String comment = (String)delegateTask.getExecution().getVariable("comment");

        HlsCusActMeeting hlsCusActMeeting = new HlsCusActMeeting();
        hlsCusActMeeting.setDocumentCategory(prjProject.getDocumentCategory());
        hlsCusActMeeting.setDocumentId(prjProject.getProjectId());
        hlsCusActMeeting.setProcessInstanceId(Long.valueOf(delegateTask.getProcessInstanceId()));

        Long meetingId = hlsCusActMeetingMapper.queryMeetingIdByDetails(hlsCusActMeeting);
        Long assignee = iRequest.getAttribute("allocationId");

        HlsCusActMeetingJudge hlsCusActMeetingJudge = new HlsCusActMeetingJudge();
        hlsCusActMeetingJudge.setJudgeMemberCode(iRequest.getUserName());
        hlsCusActMeetingJudge.setMeetingId(meetingId);
        hlsCusActMeetingJudge.setProcessInstanceId(Long.valueOf(delegateTask.getProcessInstanceId()));
        hlsCusActMeetingJudge.setCompanyId(iRequest.getCompanyId());
        hlsCusActMeetingJudge.setAllocationId(assignee);
        hlsCusActMeetingJudge.setJudgeSuggestion(result);
        hlsCusActMeetingJudge.setJudgeComment(comment);

        hlsCusActMeetingJudgeService.insert(iRequest,hlsCusActMeetingJudge);
    }
}
