package com.hand.hap.activiti.components;


import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCreditLineChanceApproverMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.HlsICreditLineChanceApproverService;
import com.hand.hls.fnd.dto.PrjMeetingJudge;
import com.hand.hls.fnd.mapper.PrjMeetingJudgeMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
public class ChanceMeetingTypeServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceMapper chanceMapper;

    @Autowired
    private PrjMeetingJudgeMapper prjMeetingJudgeMapper;

    @Autowired
    private HlsCreditLineChanceApproverMapper hlsCreditLineChanceApproverMapper;

    @Autowired
    private HlsICreditLineChanceApproverService hlsICreditLineChanceApproverService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        if ("APPROVED".equalsIgnoreCase(result)) {
            Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
            HlsCusHlsCreditLineChance chance = chanceMapper.selectByPrimaryKey(chanceId);
            String meetingType = chance.getMeetingType();
            delegateExecution.setVariable("meetingType", meetingType);
            addVoteMeeting(meetingType, chanceId, requestCtx);
        }
    }

    private void addVoteMeeting(String meetingType, Long chanceId, IRequest requestCtx) {
        if (!"OFFLINE_MEETING".equals(meetingType)) {
            return;
        }
        PrjMeetingJudge prjMeetingJudge = new PrjMeetingJudge();
        prjMeetingJudge.setEnabledFlag("Y");
        List<PrjMeetingJudge> judgeList = prjMeetingJudgeMapper.select(prjMeetingJudge);
        HlsCreditLineChanceApprover approver = new HlsCreditLineChanceApprover();
        approver.setChanceId(chanceId);
        List<HlsCreditLineChanceApprover> voteInfo = hlsCreditLineChanceApproverMapper.findVoteInfo(approver);
        //不为空说明是退回的单据删除旧的投票记录
        if (!CollectionUtils.isEmpty(voteInfo)) {
            hlsICreditLineChanceApproverService.batchDelete(voteInfo);
        }
        judgeList.forEach(v -> {
            HlsCreditLineChanceApprover chanceApprover = new HlsCreditLineChanceApprover();
            chanceApprover.setAllocationId(v.getJudgeUserAllocationId());
            hlsICreditLineChanceApproverService.insertSelective(requestCtx,chanceApprover);
        });


    }
}
