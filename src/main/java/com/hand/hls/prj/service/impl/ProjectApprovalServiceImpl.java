package com.hand.hls.prj.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.PrjProjectApproval;
import com.hand.hls.prj.dto.ProjectApprovalCondition;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.PrjProjectApprovalMapper;
import com.hand.hls.prj.mapper.ProjectApprovalConditionMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IProjectApprovalService;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.wfl.service.IActivitiCommonService.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectApprovalServiceImpl extends BaseServiceImpl<PrjProjectApproval> implements IProjectApprovalService {

    private static final String COLLECTING = "COLLECTING";

    private static final String MEETED = "MEETED";

    private static final String WORK_FLOW_TYPE = "FCT_MAKEAPPROVELNOTICE_WFL";

    @Autowired
    private IProjectApprovalService projectApprovalService;
    @Autowired
    private IProjectMeetingApproverService projectMeetingApproverService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private PrjProjectApprovalMapper mapper;

    @Override
    public List<PrjProjectApproval> queryAll(IRequest requestContext, PrjProjectApproval prjProjectApproval, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(prjProjectApproval);
    }

    @Override
    public List<PrjProjectApproval> queryAllReply(IRequest requestContext, PrjProjectApproval prjProjectApproval, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAllChange(prjProjectApproval);
    }


    @Override
    public ResponseData approvalSubmit(IRequest requestCtx, String approvalId, String meetingId, String meetingTime) {
        if (!approvalId.isEmpty()) {
            //更新会议表
            PrjProjectApproval prjProjectApproval = new PrjProjectApproval();
            prjProjectApproval.setApprovalId(Long.valueOf(approvalId));
            prjProjectApproval = projectApprovalService.selectByPrimaryKey(requestCtx, prjProjectApproval);
            prjProjectApproval.setMeetingId(Long.valueOf(meetingId));
            prjProjectApproval.setMeetingTime(meetingTime);
            prjProjectApproval = projectApprovalService.updateByPrimaryKeySelective(requestCtx, prjProjectApproval);
            //更新项目表
            String projectIds = prjProjectApproval.getProjectId();
            String[] projectId = projectIds.split("-");
            List<String> splitList = Arrays.asList(projectId);
            splitList.forEach(item -> {
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(Long.valueOf(item));
                hlsCusPrjProject.setApprovalStatus("VOTING");
//                hlsCusPrjProject.setVoteStatus("TO_VOTE");
                hlsCusPrjProject.setApprovalId(Long.valueOf(approvalId));
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
            });
            //更新会议评委表
            ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
            projectMeetingApprover.setApprovalId(Long.valueOf(approvalId));
            requestCtx.setAttribute("wflRuleControlFlag", "Y");
            List<ProjectMeetingApprover> list = projectMeetingApproverService.selectSelective(requestCtx, projectMeetingApprover);
            if (list.size() > 0) {
                list.forEach(item -> {
                    item.setEnabledFlag("Y");
                    item.setVoteStatus("TO_VOTE");
                    item.set__status("update");
                });
            }
            projectMeetingApproverService.batchUpdate(requestCtx, list);
        }
        return new ResponseData();
    }


    /**
     * LDY-提交工作流
     *
     * @param requestCtx
     * @param dto
     * @param pageNum
     * @param pageSize
     */
    public void approvalWflSubmit(IRequest requestCtx, HlsCusPrjProject dto, int pageNum, int pageSize) throws ResMessageException {
        //锁表
        databaseLockProvider.lock(dto);
        // 提交前校验
        dateCheck(dto);

        dto = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, dto);

        //提交工作流
        List<HlsCusPrjProject> list = new ArrayList<>();
        list.add(dto);
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("workFlowType", WORK_FLOW_TYPE);
        if ("PROJECT_CREDIT".equals(dto.getMeetingType())) {
            params.put("wflKey", WORK_FLOW_TYPE);
        } else if ("PROJECT_CREDIT_CHANGE".equals(dto.getMeetingType())) {
            params.put("wflKey", "PRJ_PROJECT_CHANGEWILL_WFL");
        }
//        params.put("hlsCusPrjProject", dto);
//        params.put("documentCategory", WORK_FLOW_TYPE);
//        params.put("documentType", WORK_FLOW_TYPE);
//        params.put("functionUsage", "");
//        params.put("maintainType", "");
        //params.put("projectId", dto.getProjectId());
        //params.put("businessKey", dto.getProjectId());
        //params.put("approvalId", dto.getApprovalId());
//        params.put("companyId", dto.getCompanyId());
//        params.put("allocationIdRisk", dto.getAllocationIdRisk());
//        params.put(WORK_FLOW_NAME, WORK_FLOW_TYPE);
//        params.put(BUSINESS_KEY, dto.getProjectId());
//        params.put(DEMO_NAME, WORK_FLOW_TYPE);
//        params.put("documentName", dto.getProjectName());
//        params.put("documentNumber", dto.getProjectNumber());
//        params.put("versionFlag", "N");
//        String aviationFlag = "N";
//        if(dto.getUnitId() == 114L){
//            //航空管理部
//            aviationFlag = "Y";
//        }
//        params.put("aviationFlag", aviationFlag);
        activitiStartService.start(requestCtx, list, params);

        Long projectId = dto.getProjectId();
        Long approvalId = dto.getApprovalId();
        ProjectMeetingApprover projectMeetingApprover = new ProjectMeetingApprover();
        projectMeetingApprover.setProjectId(projectId);
        projectMeetingApprover.setApprovalId(approvalId);
        requestCtx.setAttribute("wflRuleControlFlag", "Y");
        List<ProjectMeetingApprover> approverList = projectMeetingApproverService.selectSelective(requestCtx, projectMeetingApprover);
        for (ProjectMeetingApprover dt : approverList) {
            if (dt.getVoteResult() == null) {
                dt.setEmptyFlag("Y");
                projectMeetingApproverService.updateByPrimaryKeySelective(requestCtx, dt);
            }
        }


        dto = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, dto);
        //更改状态
        dto.setApprovalStatus(COLLECTING);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, dto);
    }

    private List<PrjProjectApproval> dateCheck(HlsCusPrjProject dto) throws ResMessageException {
        /**
         * 提交时校验
         */
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(dto.getProjectId());
        HlsCusPrjProject hlsCusPrjProjectList = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject);
        if (COLLECTING.equals(hlsCusPrjProjectList.getStatus()) || MEETED.equals(hlsCusPrjProjectList.getStatus())) {
            throw new ResMessageException("已经提交了申请,无需重复提交!");
        }

        return null;
    }
}