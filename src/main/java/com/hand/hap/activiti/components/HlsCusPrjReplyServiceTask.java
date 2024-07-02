package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.mapper.JcFundFillingLnMapper;
import com.hand.hls.fp.mapper.JcFundFillingMapper;
import com.hand.hls.fp.mapper.JcFundPlanScheduleMapper;
import com.hand.hls.fp.service.JcFundFillingService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.mapper.HlsDurationHdMapper;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMeetingMapper;
import com.hand.hls.prj.mapper.PrjProjectApprovalMapper;
import com.hand.hls.prj.mapper.ProjectApprovalConditionMapper;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import com.hand.hls.prj.service.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : qzk
 * @date : 2020/4/11
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjReplyServiceTask implements JavaDelegate, IActivitiBean {

    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVING = "APPROVING";
    @Autowired
    private HlsDurationHdService service;
    @Autowired
    private HlsDurationHdMapper mapper;
    @Autowired
    private JcFundPlanScheduleMapper scheduleMapper;
    @Autowired
    private JcFundFillingService fillingService;
    @Autowired
    private JcFundFillingLnMapper lnMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String SUMMARY_FLAG = "Y";
    @Autowired
    private HlsCusPrjProjectMeetingMapper meetingMapper;
    @Autowired
    private PrjProjectMeetingService meetingService;
    @Autowired
    private ProjectMeetingApproverMapper approverMapper;
    @Autowired
    private IProjectMeetingApproverService approverService;
    @Autowired
    private PrjProjectApprovalMapper approvalMapper;
    @Autowired
    private IProjectApprovalService approvalService;
    @Autowired
    private HlsCusPrjProjectService prjProjectService;
    @Autowired
    private ProjectApprovalConditionMapper conditionMapper;

    public void copyChangeToNormal(IRequest request, HlsDurationHd hlsDurationHd) {
        //将change直接更新为normal 将原normal数据更新为 history
        HlsCusPrjProjectMeeting meeting = new HlsCusPrjProjectMeeting();
        meeting.setProjectId(hlsDurationHd.getProjectId());
        List<HlsCusPrjProjectMeeting> meetingNormalList = meetingMapper.queryInfo(meeting);
        List<HlsCusPrjProjectMeeting> meetingChangeList = meetingMapper.queryInfoChange(meeting);
        //变更批复 change
        PrjProjectApproval approval1 = new PrjProjectApproval();
        approval1.setProjectId(hlsDurationHd.getProjectId().toString());
        List<PrjProjectApproval> approvalChangeList = approvalMapper.queryAllChange(approval1);
        //未变更前批复 normal
        PrjProjectApproval approval = new PrjProjectApproval();
        approval.setProjectId(hlsDurationHd.getProjectId().toString());
        List<PrjProjectApproval> approvalNormalList = approvalMapper.queryAll(approval);
        //更新原normal为history
        if (meetingNormalList.size() > 0) {
            for (HlsCusPrjProjectMeeting meeting1 : meetingNormalList) {
                meeting1.setDataClass("HISTORY");
                meetingService.updateByPrimaryKey(request, meeting1);
                //上会审批人员更新
                ProjectMeetingApprover approver = new ProjectMeetingApprover();
                approver.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approverNormalList = approverMapper.queryAllByProjectId(approver);
                if (approverNormalList.size() > 0) {
                    for (ProjectMeetingApprover approver1 : approverNormalList) {
                        approver1.setDataClass("HISTORY");
                        approver1.setProjectMeetingId(meeting1.getProjectMeetingId());
                        approverService.updateByPrimaryKeySelective(request, approver1);
                    }
                }
            }
            //复制批复更新
            if (approvalNormalList.size() > 0) {
                for (PrjProjectApproval projectApproval : approvalNormalList) {
                    projectApproval.setDataClass("HISTORY");
                    //将原复制出来的三个字段 留版存入history中
                    projectApproval.setFinanceAmountHis(approvalChangeList.get(0).getFinanceAmountHis());
                    projectApproval.setLeaseTermHis(approvalChangeList.get(0).getLeaseTermHis());
                    projectApproval.setIntRateHis(approvalChangeList.get(0).getIntRateHis());
                    approvalService.updateByPrimaryKey(request, projectApproval);
                }
            }
        }

        //更新原CHANGE为 NORMAL
        if (meetingNormalList.size() > 0) {
            for (HlsCusPrjProjectMeeting meeting2 : meetingChangeList) {
                meeting2.setDataClass("NORMAL");
                meetingService.updateByPrimaryKey(request, meeting2);
                //上会审批人员更新
                ProjectMeetingApprover approver1 = new ProjectMeetingApprover();
                approver1.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approverChangeList = approverMapper.queryAllByProjectIdChange(approver1);
                if (approverChangeList.size() > 0) {
                    for (ProjectMeetingApprover approver2 : approverChangeList) {
                        approver2.setDataClass("NORMAL");
                        approver2.setProjectMeetingId(meeting2.getProjectMeetingId());
                        approverService.updateByPrimaryKeySelective(request, approver2);
                    }
                }
            }
            //复制批复更新
            if (approvalChangeList.size() > 0) {
                for (PrjProjectApproval projectApproval1 : approvalChangeList) {
                    projectApproval1.setDataClass("NORMAL");
                    projectApproval1.setFinanceAmountHis(approvalNormalList.get(0).getFinanceAmount());
                    projectApproval1.setIntRateHis(approvalNormalList.get(0).getIntRate());
                    projectApproval1.setLeaseTermHis(approvalNormalList.get(0).getLeaseTerm());
                    approvalService.updateByPrimaryKey(request, projectApproval1);
                }
            }
        }
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = "";
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String durationHd = (String) delegateExecution.getVariable("durationHd");
        HlsDurationHd hd = JSON.parseObject(durationHd, HlsDurationHd.class);
        HlsDurationHd hlsDurationHd = mapper.selectByPrimaryKey(hd);

        //获取当前的审批意见
        String approveResultDesc = (String) delegateExecution.getVariables().get("approveResultDesc");
        String comment = (String) delegateExecution.getVariables().get("comment");
        String assignee = (String) delegateExecution.getVariables().get("assignee");

        if (APPROVING.equalsIgnoreCase(hlsDurationHd.getDurationStatus())) {
            if (APPROVED.equalsIgnoreCase(result)) {
                flag = APPROVED;
                hlsDurationHd.setDurationStatus(flag);
                service.updateByPrimaryKeySelective(requestCtx, hlsDurationHd);
            } else if ("APPROVED_M".equalsIgnoreCase(result) && assignee != null) {
                ProjectMeetingApprover meetingApprover = new ProjectMeetingApprover();
                meetingApprover.setApproverUserId(Long.valueOf(assignee));
                meetingApprover.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approvers = approverMapper.queryAllReply(meetingApprover);
                if (approvers.size() > 0) {
                    for (ProjectMeetingApprover projectMeetingApprover : approvers) {
                        projectMeetingApprover.setVoteComment(comment);
                        projectMeetingApprover.setVoteResult(approveResultDesc);
                        approverService.updateByPrimaryKey(requestCtx, projectMeetingApprover);
                    }
                }
            } else if ("APPROVED_QUA".equalsIgnoreCase(result) && assignee != null) {
                ProjectMeetingApprover meetingApprover = new ProjectMeetingApprover();
                meetingApprover.setApproverUserId(Long.valueOf(assignee));
                meetingApprover.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approvers = approverMapper.queryAllReply(meetingApprover);
                if (approvers.size() > 0) {
                    for (ProjectMeetingApprover projectMeetingApprover : approvers) {
                        projectMeetingApprover.setVoteComment(comment);
                        projectMeetingApprover.setVoteResult(approveResultDesc);
                        approverService.updateByPrimaryKey(requestCtx, projectMeetingApprover);
                    }
                }
            } else if ("REJECTED_M".equalsIgnoreCase(result) && assignee != null) {
                ProjectMeetingApprover meetingApprover = new ProjectMeetingApprover();
                meetingApprover.setApproverUserId(Long.valueOf(assignee));
                meetingApprover.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approvers = approverMapper.queryAllReply(meetingApprover);
                if (approvers.size() > 0) {
                    for (ProjectMeetingApprover projectMeetingApprover : approvers) {
                        projectMeetingApprover.setVoteComment(comment);
                        projectMeetingApprover.setVoteResult(approveResultDesc);
                        approverService.updateByPrimaryKey(requestCtx, projectMeetingApprover);
                    }
                }
            } else if ("SUSPEND_M".equalsIgnoreCase(result) && assignee != null) {
                ProjectMeetingApprover meetingApprover = new ProjectMeetingApprover();
                meetingApprover.setApproverUserId(Long.valueOf(assignee));
                meetingApprover.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approvers = approverMapper.queryAllReply(meetingApprover);
                if (approvers.size() > 0) {
                    for (ProjectMeetingApprover projectMeetingApprover : approvers) {
                        projectMeetingApprover.setVoteComment(comment);
                        projectMeetingApprover.setVoteResult(approveResultDesc);
                        approverService.updateByPrimaryKey(requestCtx, projectMeetingApprover);
                    }
                }
            } else if(assignee != null){
                flag = REJECTED;
                hlsDurationHd.setDurationStatus(flag);
                service.updateByPrimaryKeySelective(requestCtx, hlsDurationHd);
            }

            if (APPROVED.equalsIgnoreCase(flag)) {
                //审批通过覆盖数据
                copyChangeToNormal(requestCtx, hlsDurationHd);
                //恢复暂挂项目状态
                HlsCusPrjProject prjProject = new HlsCusPrjProject();
                prjProject.setProjectId(hlsDurationHd.getProjectId());
                prjProject.setProjectStatus(APPROVED);
                prjProjectService.updateByPrimaryKeySelective(requestCtx,prjProject);
            }
        }
    }
}
