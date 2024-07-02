package com.hand.hls.activiti.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.activiti.dto.HlsCusActMeetingJudge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 上会评审信息Mapper
 * @Date: Created in 19:09 2017/12/17
 * @Modified By:
 */
public interface HlsCusActMeetingJudgeMapper extends Mapper<HlsCusActMeetingJudge> {

    Long queryJudgeIdByDetails(HlsCusActMeetingJudge hlsCusActMeetingJudge);// 根据限定内容查询上会评审信息的Id


    List<HlsCusActMeetingJudge> queryAllJudgeDetailByProcessInstacneId(@Param("processInstanceId") Long processInstanceId);// 根据工作流实例Id查询所有上会评审的相关信息

    List<HlsCusActMeetingJudge> queryAllJudgeDetailByMeetingId(@Param("meetingId") Long meetingId);

    List<HlsCusActMeetingJudge> queryJudgeDetailByProcessInstanceId(@Param("processInstanceId") Long processInstanceId);// 根据工作流实例Id查询评审信息

    List<HlsCusActMeetingJudge> queryPartAgreeDetailDetailByProcessInstanceId(@Param("processInstanceId") Long processInstanceId);// 根据工作流实例Id查询所有待满足条件的信息

    void updateToBeSatisfiedConditionById(HlsCusActMeetingJudge hlsCusActMeetingJudge); // 更新待满足条件

    List<String> queryMemberCodeInToBeSatisfiedConditionById(@Param("processInstanceId") Long processInstanceId); // 查询是谁提出了待满足条件

    List<HlsCusActMeetingJudge> queryEmployeeCodeByUserName(HlsCusActMeetingJudge hlsCusActMeetingJudge);
}
