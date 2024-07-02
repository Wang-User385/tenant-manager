package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import com.hand.hls.prj.dto.ProjectMeetingApprover;

import java.util.List;

public interface ProjectApprovalConditionMapper extends Mapper<ProjectApprovalCondition>{
    List<ProjectApprovalCondition> queryAll(ProjectApprovalCondition projectApprovalCondition);
    List<ProjectApprovalCondition> queryAll2(ProjectApprovalCondition projectApprovalCondition);

    List<ProjectApprovalCondition> queryBefore(ProjectApprovalCondition projectApprovalCondition);
    List<ProjectApprovalCondition> queryAfter(ProjectApprovalCondition projectApprovalCondition);
}