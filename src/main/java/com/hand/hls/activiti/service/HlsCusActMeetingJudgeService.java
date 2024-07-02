package com.hand.hls.activiti.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 上会评审信息Service
 * @Date: Created in 19:10 2017/12/17
 * @Modified By:
 */
public interface HlsCusActMeetingJudgeService extends IBaseService<HlsCusActMeetingJudge>, ProxySelf<HlsCusActMeetingJudgeService> {

    Long queryJudgeIdByDetails(IRequest iRequest, HlsCusActMeetingJudge hlsCusActMeetingJudge); // 根据限定内容查询上会评审信息的Id

    List<HlsCusActMeetingJudge> queryAllJudgeDetailByProcessInstacneId(IRequest iRequest, Long processInstanceId);// 根据工作流实例Id查询所有上会评审信息的Id

    List<HlsCusActMeetingJudge> queryAllJudgeDetailByMeetingId(IRequest iRequest, Long meetingId);

    List<HlsCusActMeetingJudge> queryJudgeDetailByProcessInstanceId(IRequest iRequest, Long processInstanceId);// 根据工作流实例Id查询所有上会评审信息(code转中文)

    List<HlsCusActMeetingJudge> queryPartAgreeDetailDetailByProcessInstanceId(IRequest iRequest, Long processInstanceId);// 根据工作流实例Id查询所有待满足条件的信息

    void updateToBeSatisfiedConditionById(IRequest iRequest, HlsCusActMeetingJudge hlsCusActMeetingJudge);// 更新待满足条件

    List<String> queryMemberCodeInToBeSatisfiedConditionById(IRequest iRequest, Long processInstanceId);// 查询是谁提出了待满足条件
}
