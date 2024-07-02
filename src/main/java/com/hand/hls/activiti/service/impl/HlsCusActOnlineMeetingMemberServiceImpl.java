package com.hand.hls.activiti.service.impl;


import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;
import com.hand.hls.activiti.dto.HlsCusActOnlineMeetingMember;
import com.hand.hls.activiti.mapper.HlsCusActOnlineMeetingMemberMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingJudgeService;
import com.hand.hls.activiti.service.HlsCusActMeetingService;
import com.hand.hls.activiti.service.HlsCusActOnlineMeetingMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流上会评审成员Service实现类
 * Created by qixiang.shao on 2017/12/11
 */
@Service
public class HlsCusActOnlineMeetingMemberServiceImpl extends BaseServiceImpl<HlsCusActOnlineMeetingMember> implements HlsCusActOnlineMeetingMemberService {
    @Autowired
    private HlsCusActOnlineMeetingMemberMapper hlsCusActOnlineMeetingMemberMapper;
    @Autowired
    private HlsCusActOnlineMeetingMemberService hlsCusActOnlineMeetingMemberService;
    @Autowired
    private HlsCusActMeetingService hlsCusActMeetingService;
    @Autowired
    private HlsCusActMeetingJudgeService hlsCusActMeetingJudgeService;

    @Override
    public List<HlsCusActOnlineMeetingMember> queryMeetingMemberByProcessInstanceId(Long processInstanceId) {
        return hlsCusActOnlineMeetingMemberMapper.queryMeetingMemberByProcessInstanceId(processInstanceId);
    }

    @Override
    public void judgeSave(IRequest iRequest, HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMember) {
        List<HlsCusActOnlineMeetingMember> hlsCusActOnlineMeetingMemberList = new ArrayList<HlsCusActOnlineMeetingMember>();
        //先将已插入的删除
        List<HlsCusActOnlineMeetingMember> list = new ArrayList<HlsCusActOnlineMeetingMember>();
        List<HlsCusActMeetingJudge> hlsCusActMeetingJudgeList = new ArrayList<>();
        Long processInstanceId = hlsCusActOnlineMeetingMember.getProcessInstanceId();
        list = hlsCusActOnlineMeetingMemberService.queryMeetingMemberByProcessInstanceId(processInstanceId);
        if (list.size() > 0) {
            hlsCusActOnlineMeetingMemberService.batchDelete(list);
        }
        String employeeCodeString = hlsCusActOnlineMeetingMember.getEmployeeCodeString();
        String[] employeeCodeArr = employeeCodeString.split("\\|");
        for (int index = 0; index < employeeCodeArr.length; index++) {
            HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMemberAdd = new HlsCusActOnlineMeetingMember();
            hlsCusActOnlineMeetingMemberAdd.setOnlineMeetingMemberCode(employeeCodeArr[index]);
            hlsCusActOnlineMeetingMemberAdd.setProcessInstanceId(processInstanceId);
            Long meetingId = hlsCusActMeetingService.queryMeetingIdByProcessInstanceId(iRequest, processInstanceId);
            hlsCusActOnlineMeetingMemberAdd.setMeetingId(meetingId);
            hlsCusActOnlineMeetingMemberAdd.set__status("add");
            hlsCusActOnlineMeetingMemberList.add(hlsCusActOnlineMeetingMemberAdd);
            /*插入act_meeting_judge*/
            HlsCusActMeetingJudge hlsCusActMeetingJudge = new HlsCusActMeetingJudge();
            hlsCusActMeetingJudge.setJudgeMemberCode(employeeCodeArr[index]);
            hlsCusActMeetingJudge.setProcessInstanceId(processInstanceId);
            hlsCusActMeetingJudge.setMeetingId(meetingId);
            hlsCusActMeetingJudge.set__status("add");
            hlsCusActMeetingJudgeList.add(hlsCusActMeetingJudge);
        }
        hlsCusActMeetingJudgeService.batchUpdate(iRequest, hlsCusActMeetingJudgeList);
        hlsCusActOnlineMeetingMemberService.batchUpdate(iRequest, hlsCusActOnlineMeetingMemberList);
    }
}
