package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.dto.PrjMeetingJudge;
import com.hand.hls.fnd.service.PrjMeetingJudgeService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IProjectApprovalService;
import com.hand.hls.sys.service.IFndCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectMeetingApproverServiceImpl extends BaseServiceImpl<ProjectMeetingApprover> implements IProjectMeetingApproverService {

    @Autowired
    private IProjectApprovalService projectApprovalService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private PrjMeetingJudgeService prjMeetingJudgeService;
    @Autowired
    private IProjectMeetingApproverService service;
    @Autowired
    private ProjectMeetingApproverMapper mapper;
    @Autowired
    private IFndCompanyService fndCompanyService;

    @Override
    public ResponseData createApprover(String approvalId, IRequest request) {
        PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
        prjProjectApproval.setApprovalId(Long.valueOf(approvalId));
        prjProjectApproval = projectApprovalService.selectByPrimaryKey(request, prjProjectApproval);
        String projectId = prjProjectApproval.getProjectId();
        String status = prjProjectApproval.getStatus();

        projectId = projectId.split("-")[0];
        List<String> projectIdList = Arrays.asList(projectId);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(Long.valueOf(projectId));
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);
        String industryVoting = hlsCusPrjProject.getIndustryVoting();

        if(industryVoting != null){
            PrjMeetingJudge prjMeetingJudge = new PrjMeetingJudge();
            prjMeetingJudge.setJudgeType(industryVoting);
            List<PrjMeetingJudge> list = prjMeetingJudgeService.selectSelective(request, prjMeetingJudge);

            list.forEach(item -> {
                projectIdList.forEach(id -> {
                    ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
                    projectMeetingApprover.setApprovalId(Long.valueOf(approvalId));
                    projectMeetingApprover.setApproverUserId(item.getJudgeUserAllocationId());
                    projectMeetingApprover.setUnitName(item.getUnitName());
                    projectMeetingApprover.setEmployeeCode(item.getEmployeeCode());
                    projectMeetingApprover.setPositionName(item.getPositionName());
                    projectMeetingApprover.setDirectorFlag(item.getDirectorFlag());
                    projectMeetingApprover.setEnabledFlag("N");
                    projectMeetingApprover.setProjectId(Long.valueOf(id));
                    service.insertSelective(request, projectMeetingApprover);
                });
            });
        }
        return new ResponseData();
    }

    @Override
    public ResponseData insertApprover(IRequest request, List<PrjMeetingJudge> list, String approvalId) {
        FndCompany fndCompany = new FndCompany();
        fndCompany.setCompanyId(request.getCompanyId());
        fndCompany = fndCompanyService.selectByPrimaryKey(request, fndCompany);
        String employeeCode = request.getEmployeeCode();
        String positionCode = request.getAttribute("positionCode") == null ? "" : request.getAttribute("positionCode");
        String unitCode = request.getAttribute("unitCode") == null ? "" : request.getAttribute("unitCode");
        String authorityRuleString = '"' + fndCompany.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + employeeCode + '"';

        if (!list.isEmpty()) {
            list.forEach(item -> {
                ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
                PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
                prjProjectApproval.setApprovalId(Long.valueOf(approvalId));
                prjProjectApproval = projectApprovalService.selectByPrimaryKey(request, prjProjectApproval);
                String projectId = prjProjectApproval.getProjectId();
                if (!projectId.isEmpty()) {
                    String[] projectIds = projectId.split("-");
                    List<String> projectIdList = Arrays.asList(projectIds);

                    projectIdList.forEach(id -> {
                        projectMeetingApprover.setApprovalId(Long.valueOf(approvalId));
                        projectMeetingApprover.setApproverUserId(item.getJudgeUserAllocationId());
                        projectMeetingApprover.setEmployeeCode(item.getEmployeeCode());
                        projectMeetingApprover.setUnitName(item.getUnitName());
                        projectMeetingApprover.setPositionName(item.getPositionName());
                        projectMeetingApprover.setDirectorFlag(item.getDirectorFlag());
                        projectMeetingApprover.setEnabledFlag("N");
                        projectMeetingApprover.setAuthorityRuleString(authorityRuleString);
                        projectMeetingApprover.setProjectId(Long.valueOf(id));
                        service.insertSelective(request, projectMeetingApprover);
                    });
                }
            });
        }
        return new ResponseData();
    }

    @Override
    public List<ProjectMeetingApprover> queryAll(IRequest requestContext, ProjectMeetingApprover projectMeetingApprover, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(projectMeetingApprover);
    }

    @Override
    public List<ProjectMeetingApprover> queryAllReply(IRequest requestContext, ProjectMeetingApprover projectMeetingApprover, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAllReply(projectMeetingApprover);
    }

    @Override
    public List<ProjectMeetingApprover> queryInfo(IRequest requestContext, ProjectMeetingApprover projectMeetingApprover, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryInfo(projectMeetingApprover);
    }

    @Override
    public List<ProjectMeetingApprover> queryAllByProjectId(IRequest iRequest, ProjectMeetingApprover projectMeetingApprover) {
        return mapper.queryAllByProjectId(projectMeetingApprover);
    }

    @Override
    public List<Map> selectPrjSumComment(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) {
        return mapper.selectPrjSumComment(hlsCusPrjProject);
    }

    @Override
    public List<ProjectMeetingApprover> queryProjectJudge(IRequest requestContext, ProjectMeetingApprover projectMeetingApprover, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryProjectJudge(projectMeetingApprover);
    }
    /*添加评委*/
    @Override
    public List<ProjectMeetingApprover> addApproval(IRequest requestContext, ProjectMeetingApprover meetingApprover) throws InvocationTargetException, IllegalAccessException, IOException {
        //查询评委表信息
        List<ProjectMeetingApprover> meetingApprovers = mapper.queryInfo(meetingApprover);
        //查询该项目下评委表信息
        List<ProjectMeetingApprover> projectMeetingApprovers = mapper.queryAllByProjectId(meetingApprover);
        if(projectMeetingApprovers.size()>0){
            for(ProjectMeetingApprover delApprover : projectMeetingApprovers){
                //删除原项目评委信息
                service.deleteByPrimaryKey(delApprover);
            }
        }
        //遍历评委表并插入项目项目评委表
        if(meetingApprovers.size() > 0){
            for(ProjectMeetingApprover approver : meetingApprovers ){
                ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover ();
                projectMeetingApprover.setProjectId(meetingApprover.getProjectId());
                projectMeetingApprover.setProjectMeetingId(nvl(meetingApprover.getProjectMeetingId(),null));
                projectMeetingApprover.setDataClass("NORMAL");
                projectMeetingApprover.setPositionName(approver.getPositionName());
                projectMeetingApprover.setPositionId(approver.getPositionId());
                projectMeetingApprover.setUnitName(approver.getUnitName());
                projectMeetingApprover.setUnitId(approver.getUnitId());
                projectMeetingApprover.setCompanyId(approver.getCompanyId());
                projectMeetingApprover.setApproverUserId(Long.valueOf(approver.getJudgeUserAllocationId()));
                service.insertSelective(requestContext, projectMeetingApprover);
            }
        }else{
            throw new IllegalArgumentException("评委不存在，请先在上会评审定义功能定义评委！");
        }
        return meetingApprovers;
    }

    /*添加评委*/
    @Override
    public List<ProjectMeetingApprover> addApprovalReply(IRequest requestContext, ProjectMeetingApprover meetingApprover) throws InvocationTargetException, IllegalAccessException, IOException {
        //查询评委表信息
        List<ProjectMeetingApprover> meetingApprovers = mapper.queryInfo(meetingApprover);
        //查询该项目下评委表信息
        List<ProjectMeetingApprover> projectMeetingApprovers = mapper.queryAllByProjectId(meetingApprover);
        if(projectMeetingApprovers.size()>0){
            for(ProjectMeetingApprover delApprover : projectMeetingApprovers){
                //删除原项目评委信息
                service.deleteByPrimaryKey(delApprover);
            }
        }
        //遍历评委表并插入项目项目评委表
        if(meetingApprovers.size() > 0){
            for(ProjectMeetingApprover approver : meetingApprovers ){
                ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover ();
                projectMeetingApprover.setProjectId(meetingApprover.getProjectId());
                projectMeetingApprover.setDataClass("CHANGE");
                projectMeetingApprover.setPositionName(approver.getPositionName());
                projectMeetingApprover.setPositionId(approver.getPositionId());
                projectMeetingApprover.setUnitName(approver.getUnitName());
                projectMeetingApprover.setUnitId(approver.getUnitId());
                projectMeetingApprover.setCompanyId(approver.getCompanyId());
                projectMeetingApprover.setApproverUserId(Long.valueOf(approver.getJudgeUserAllocationId()));
                service.insertSelective(requestContext, projectMeetingApprover);
            }
        }else{
            throw new IllegalArgumentException("评委不存在，请先在上会评审定义功能定义评委！");
        }
        return meetingApprovers;
    }
}