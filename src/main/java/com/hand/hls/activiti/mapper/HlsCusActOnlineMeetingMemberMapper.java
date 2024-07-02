package com.hand.hls.activiti.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.activiti.dto.HlsCusActOnlineMeetingMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流评审会成员Mapper映射层
 * Created by qixiang.shao on 2017/12/11
 */
public interface HlsCusActOnlineMeetingMemberMapper extends Mapper<HlsCusActOnlineMeetingMember> {

    List<HlsCusActOnlineMeetingMember> queryMeetingMemberByProcessInstanceId(@Param("processInstanceId") Long processInstanceId);// 根据工作流实例Id查询评审委员会成员

    List<HlsCusActOnlineMeetingMember> queryMeetingMemberByPorcessInstanceIdLov(@Param("processInstanceId") Long processInstanceId);// 根据工作流实例Id查询评审委员会成员Lov

    List<HlsCusActOnlineMeetingMember> queryMeetingMemberByDocumentInfo(@Param("projectId") Long projectId, @Param("projectDocumentCategory") String projectDocumentCategory);// 根据表单信息查询评审委员会成员

}
