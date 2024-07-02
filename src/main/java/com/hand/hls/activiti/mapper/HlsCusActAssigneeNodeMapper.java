package com.hand.hls.activiti.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.activiti.dto.HlsCusActAssigneeNode;

import java.util.List;

public interface HlsCusActAssigneeNodeMapper extends Mapper<HlsCusActAssigneeNode> {

    List<HlsCusActAssigneeNode> selectNodeByIdAndType(HlsCusActAssigneeNode hlsCusActAssigneeNode);//根据流程ID和经办人类型查询指定经办人Code

    void updateAssigneeCodeByIdAndType(HlsCusActAssigneeNode hlsCusActAssigneeNode);
}
