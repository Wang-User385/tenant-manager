package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.mapper.ProjectApprovalConditionMapper;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import com.hand.hls.prj.service.ProjectApprovalConditionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectApprovalConditionServiceImpl extends BaseServiceImpl<ProjectApprovalCondition> implements ProjectApprovalConditionService{
    @Autowired
    private ProjectApprovalConditionMapper mapper;
    @Override
    public List<ProjectApprovalCondition> queryAll(IRequest requestContext, ProjectApprovalCondition projectApprovalCondition, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(projectApprovalCondition);
    }

    @Override
    public List<ProjectApprovalCondition> queryAll2(IRequest requestContext, ProjectApprovalCondition projectApprovalCondition, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll2(projectApprovalCondition);
    }
}