package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.PrjMeetingJudge;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProjectMeetingApprover;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface IProjectMeetingApproverService extends IBaseService<ProjectMeetingApprover>, ProxySelf<IProjectMeetingApproverService> {

    ResponseData createApprover(String approvalId, IRequest request);

    ResponseData insertApprover(IRequest request, List<PrjMeetingJudge> list, String approvalId);

    List<ProjectMeetingApprover> queryAll(IRequest var1, ProjectMeetingApprover var2, int var3, int var4);

    List<ProjectMeetingApprover> queryAllReply(IRequest var1, ProjectMeetingApprover var2, int var3, int var4);

    List<ProjectMeetingApprover> queryProjectJudge(IRequest var1, ProjectMeetingApprover var2, int var3, int var4);

    List<ProjectMeetingApprover> queryInfo(IRequest var1, ProjectMeetingApprover var2, int var3, int var4);

    List<ProjectMeetingApprover> queryAllByProjectId(IRequest iRequest, ProjectMeetingApprover projectMeetingApprover);

    List<Map> selectPrjSumComment(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject);

    List<ProjectMeetingApprover> addApproval(IRequest var1, ProjectMeetingApprover meetingApprover) throws InvocationTargetException, IllegalAccessException, IOException;

    List<ProjectMeetingApprover> addApprovalReply(IRequest var1, ProjectMeetingApprover meetingApprover) throws InvocationTargetException, IllegalAccessException, IOException;

}