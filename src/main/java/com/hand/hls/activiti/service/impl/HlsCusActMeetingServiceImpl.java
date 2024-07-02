package com.hand.hls.activiti.service.impl;


import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.activiti.dto.HlsCusActMeeting;
import com.hand.hls.activiti.mapper.HlsCusActMeetingMapper;
import com.hand.hls.activiti.service.HlsCusActMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 工作流上会信息实现类
 * @Date: Created in 16:23 2017/12/17
 * @Modified By:
 */
@Service
@Transactional
public class HlsCusActMeetingServiceImpl extends BaseServiceImpl<HlsCusActMeeting> implements HlsCusActMeetingService {

    @Autowired
    private HlsCusActMeetingMapper hlsCusActMeetingMapper;

    @Override
    public Long queryMeetingIdByDetails(IRequest iRequest, HlsCusActMeeting hlsCusActMeeting) {
        return hlsCusActMeetingMapper.queryMeetingIdByDetails(hlsCusActMeeting);
    }

    @Override
    public Long queryMeetingIdByProcessInstanceId(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingMapper.queryMeetingIdByProcessInstanceId(processInstanceId);
    }

    @Override
    public List<HlsCusActMeeting> queryMeetingDetailByProcessInstanceId(IRequest iRequest, Long processInstanceId) {
        return hlsCusActMeetingMapper.queryMeetingDetailByProcessInstanceId(processInstanceId);
    }

    @Override
    public void updateMeetingType(HlsCusActMeeting hlsCusActMeeting) {
        hlsCusActMeetingMapper.updateMeetingType(hlsCusActMeeting);
    }

    @Override
    public void updateMeetingComprehensiveComment(IRequest iRequest, HlsCusActMeeting hlsCusActMeeting) {
        hlsCusActMeetingMapper.updateMeetingComprehensiveComment(hlsCusActMeeting);
    }

    @Override
    public boolean isOnlineMeeting(IRequest iRequest, Long processInsnstanceId) {
        String meetingType = hlsCusActMeetingMapper.queryMeetingTypeByProcessInstanceId(processInsnstanceId);
        if (meetingType != null && "ONLINE".equals(meetingType)) {
            return true;
        } else {
            return false;
        }
    }
}
