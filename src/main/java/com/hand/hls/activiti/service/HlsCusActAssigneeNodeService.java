package com.hand.hls.activiti.service;


import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.activiti.dto.HlsCusActAssigneeNode;

import java.util.List;

public interface HlsCusActAssigneeNodeService extends IBaseService<HlsCusActAssigneeNode>, ProxySelf<HlsCusActAssigneeNodeService> {

    List<HlsCusActAssigneeNode> selectNodeByIdAndType(HlsCusActAssigneeNode hlsCusActAssigneeNode);// 根据流程ID和经办人类型查询指定人的节点信息

    void updateAssigneeCodeByIdAndType(IRequest iRequest, HlsCusActAssigneeNode hlsCusActAssigneeNode); // 根据流程ID和经办人类型重新指定经办人

}
