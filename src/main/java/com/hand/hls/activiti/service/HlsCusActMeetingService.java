package com.hand.hls.activiti.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.activiti.dto.HlsCusActMeeting;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 工作流上会信息Service
 * @Date: Created in 16:22 2017/12/17
 * @Modified By:
 */
public interface HlsCusActMeetingService extends IBaseService<HlsCusActMeeting>, ProxySelf<HlsCusActMeetingService> {

    Long  queryMeetingIdByDetails(IRequest iRequest, HlsCusActMeeting hlsCusActMeeting);// 判断上会信息是否已经存在,如果存在,应该是更新上会信息

    Long queryMeetingIdByProcessInstanceId(IRequest iRequest, Long processInstanceId);// 根据工作流实例Id查询上会信息Id

    List<HlsCusActMeeting> queryMeetingDetailByProcessInstanceId(IRequest iRequest, Long processInstanceId);// 根据工作流实例Id查询上会信息

    void updateMeetingType(HlsCusActMeeting hlsCusActMeeting);//根据工作流id更新上会类型

    void updateMeetingComprehensiveComment(IRequest iRequest, HlsCusActMeeting hlsCusActMeeting);// 更新上会综合意见

    boolean isOnlineMeeting(IRequest iRequest, Long processInsnstanceId);
}
