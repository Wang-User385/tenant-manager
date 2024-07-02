package com.hand.hls.activiti.service.impl;


import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.activiti.dto.HlsCusActAssigneeNode;
import com.hand.hls.activiti.mapper.HlsCusActAssigneeNodeMapper;
import com.hand.hls.activiti.service.HlsCusActAssigneeNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusActAssigneeNodeServiceImpl extends BaseServiceImpl<HlsCusActAssigneeNode> implements HlsCusActAssigneeNodeService {

    @Autowired
    HlsCusActAssigneeNodeMapper hlsCusActAssigneeNodeMapper;

    @Override
    public List<HlsCusActAssigneeNode> selectNodeByIdAndType(HlsCusActAssigneeNode hlsCusActAssigneeNode) {
        return hlsCusActAssigneeNodeMapper.selectNodeByIdAndType(hlsCusActAssigneeNode);
    }

    @Override
    public void updateAssigneeCodeByIdAndType(IRequest iRequest, HlsCusActAssigneeNode hlsCusActAssigneeNode) {
        hlsCusActAssigneeNodeMapper.updateAssigneeCodeByIdAndType(hlsCusActAssigneeNode);
    }
}
