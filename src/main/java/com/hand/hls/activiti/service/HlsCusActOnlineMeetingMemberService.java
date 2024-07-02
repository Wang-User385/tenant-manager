package com.hand.hls.activiti.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.activiti.dto.HlsCusActOnlineMeetingMember;

import java.util.List;

/**
 * 工作流上会审批成员Service
 * Created by qixiang.shao on 2017/11/22
 */
public interface HlsCusActOnlineMeetingMemberService extends IBaseService<HlsCusActOnlineMeetingMember>, ProxySelf<HlsCusActOnlineMeetingMemberService> {

    List<HlsCusActOnlineMeetingMember> queryMeetingMemberByProcessInstanceId(Long processInstanceId);// 根据项目实例Id查询项目上会评审委员会成员

    void judgeSave(IRequest iRequest, HlsCusActOnlineMeetingMember hlsCusActOnlineMeetingMember);

}
