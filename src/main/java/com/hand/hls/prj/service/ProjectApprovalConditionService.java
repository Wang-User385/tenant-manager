package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import com.hand.hls.prj.dto.ProjectMeetingApprover;

import java.util.List;

public interface ProjectApprovalConditionService extends IBaseService<ProjectApprovalCondition>, ProxySelf<ProjectApprovalConditionService>{
    List<ProjectApprovalCondition> queryAll(IRequest var1, ProjectApprovalCondition var2, int var3, int var4);
    List<ProjectApprovalCondition> queryAll2(IRequest var1, ProjectApprovalCondition var2, int var3, int var4);

}