package com.hand.hls.activiti.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.activiti.dto.HlsCusActMeetingRiskList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: qixiang.shao
 * @Description:
 * @Date: Created in 16:58 2018/1/9
 * @Modified By:
 */
public interface HlsCusActMeetingRiskListMapper extends Mapper<HlsCusActMeetingRiskList> {

    List<HlsCusActMeetingRiskList> queryRiskListByProcessInstanceId(@Param("processInstanceId") Long processInstanceId);// 根据工作流实例Id查询风险防范措施

    List<HlsCusActMeetingRiskList> queryRiskListSelectedByProcessInstanceId(@Param("processInstanceId") Long processInstanceId); // 根据工作流实例Id查询已指定风险防范措施

    List<HlsCusActMeetingRiskList> queryPaymentPt(@Param("documentId") Long documentId);//查询符合付款前提条件的风险防范措施

    List<HlsCusActMeetingRiskList> queryConPaymentPt(@Param("documentId") Long documentId);
}
