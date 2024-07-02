package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProjectMeetingApproverMapper extends Mapper<ProjectMeetingApprover> {

    List<ProjectMeetingApprover> queryAll(ProjectMeetingApprover projectMeetingApprover);

    List<ProjectMeetingApprover> queryAllReply(ProjectMeetingApprover projectMeetingApprover);

    List<ProjectMeetingApprover> queryInfo(ProjectMeetingApprover projectMeetingApprover);

    List<ProjectMeetingApprover> queryAllByProjectId(ProjectMeetingApprover projectMeetingApprover);

    List<ProjectMeetingApprover> queryAllByProjectIdChange(ProjectMeetingApprover projectMeetingApprover);

    List<ProjectMeetingApprover> queryProjectJudge(ProjectMeetingApprover projectMeetingApprover);

    List<ProjectMeetingApprover> queryProjectJudgeWfl(ProjectMeetingApprover projectMeetingApprover);

    List<Map> selectPrjSumComment(HlsCusPrjProject hlsCusPrjProject);

    void deleteMeetingApprover(ProjectMeetingApprover projectMeetingApprover);

    void updateMeetingJudgeFlag(ProjectMeetingApprover projectMeetingApprover);

    void updateMeetingApproverStatus(@Param("projectId") Long projectId, @Param("dataClass") String dataClass);

    List<Map> queryAnalyse(ProjectMeetingApprover projectMeetingApprover);

    List<ProjectMeetingApprover> queryMeetingApproval(ProjectMeetingApprover projectMeetingApprover);
}