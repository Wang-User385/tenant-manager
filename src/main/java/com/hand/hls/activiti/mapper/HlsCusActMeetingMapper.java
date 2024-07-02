package com.hand.hls.activiti.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.activiti.dto.HlsCusActMeeting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description: 工作流上会信息Mapper
 * @Date: Created in 16:21 2017/12/17
 * @Modified By:
 */
public interface HlsCusActMeetingMapper extends Mapper<HlsCusActMeeting> {

    Long queryMeetingIdByDetails(HlsCusActMeeting hlsCusActMeeting);// 判断上会信息是否已经存在,如果存在,应该是更新上会信息

    Long queryMeetingIdByProcessInstanceId(@Param("processInstanceId") Long processInstanceId);// 根据工作流实例Id查询上会信息Id

    List<HlsCusActMeeting> queryMeetingDetailByProcessInstanceId(@Param("processInstanceId") Long processInstanceId); // 根据工作流实例Id查询上会信息

    void updateMeetingType(HlsCusActMeeting hlsCusActMeeting);//根据工作流id更新上会类型

    void updateMeetingComprehensiveComment(HlsCusActMeeting hlsCusActMeeting); // 更新上会综合意见

    String queryMeetingTypeByProcessInstanceId(@Param("processInstanceId") Long processInsnstanceId); // 根据工作流信息查询上会的类型
}
