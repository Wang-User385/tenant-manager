package com.hand.hls.activiti.service.impl;


import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;
import com.hand.hls.activiti.mapper.HlsCusActMeetingJudgeMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 上会评审信息实现类
 * @Date: Created in 19:12 2017/12/17
 * @Modified By:
 */
@Service
@Transactional
public class HlsCusActMeetingJudgeServiceImpl extends BaseServiceImpl<HlsCusActMeetingJudge> implements HlsCusActMeetingJudgeService {
    @Autowired
    private HlsCusActMeetingJudgeMapper hlsCusActMeetingJudgeMapper;

    @Override
    public Long queryJudgeIdByDetails(IRequest iRequest, HlsCusActMeetingJudge hlsCusActMeetingJudge) {
        return hlsCusActMeetingJudgeMapper.queryJudgeIdByDetails(hlsCusActMeetingJudge);
    }


    @Override
    public List<HlsCusActMeetingJudge> queryAllJudgeDetailByMeetingId(IRequest iRequest, Long meetingId) {
        return hlsCusActMeetingJudgeMapper.queryAllJudgeDetailByMeetingId(meetingId);
    }

    @Override
    public List<HlsCusActMeetingJudge> queryAllJudgeDetailByProcessInstacneId(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingJudgeMapper.queryAllJudgeDetailByProcessInstacneId(processInstanceId);
    }

    @Override
    public List<HlsCusActMeetingJudge> queryJudgeDetailByProcessInstanceId(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingJudgeMapper.queryJudgeDetailByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<HlsCusActMeetingJudge> queryPartAgreeDetailDetailByProcessInstanceId(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingJudgeMapper.queryPartAgreeDetailDetailByProcessInstanceId(processInstanceId);
    }

    @Override
    public void updateToBeSatisfiedConditionById(IRequest iRequest, HlsCusActMeetingJudge hlsCusActMeetingJudge) {
        hlsCusActMeetingJudgeMapper.updateToBeSatisfiedConditionById(hlsCusActMeetingJudge);
    }

    @Override
    public List<String> queryMemberCodeInToBeSatisfiedConditionById(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingJudgeMapper.queryMemberCodeInToBeSatisfiedConditionById(processInstanceId);
    }


}
