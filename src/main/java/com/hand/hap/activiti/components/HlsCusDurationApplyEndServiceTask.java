package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProjectMeetingApprover;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.ProjectMeetingApproverMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * description 存续期结束过程
 *
 * @author Eugene Song 2020年6月11日
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusDurationApplyEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String REJECTED = "REJECTED";
    private static final String APPROVED = "APPROVED";
    private static final String APPROVING = "APPROVING";
    private static final String NORMAL = "NORMAL";
    private static final String TERMINATE = "TERMINATE";
    private static final String ET = "ET";
    private static final String CON_TERMINATION = "CON_TERMINATION";
    public static final String DURATION_APPROVED = "DURATION_APPROVED";
    public static final String PRESIDENT_APPROVED = "PRESIDENT_APPROVED";

    @Autowired
    private HlsDurationHdService service;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsDurationHdService hdService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper projectMapper;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsCusConContractService contractService;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;
    @Autowired
    private ProjectMeetingApproverMapper approverMapper;
    @Autowired
    private IProjectMeetingApproverService approverService;
    @Autowired
    private IGldContractCashflowService gldContractCashflowService;

    public HlsCusDurationApplyEndServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = "";
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long hdId = (Long) delegateExecution.getVariable("hdId");
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsDurationHd hlsDurationHd = new HlsDurationHd();

        hlsDurationHd.setHdId(hdId);
        hlsDurationHd = service.selectByPrimaryKey(requestCtx, hlsDurationHd);

        //获取当前的审批意见
        String approveResultDesc = (String) delegateExecution.getVariables().get("approveResultDesc");
        String comment = (String) delegateExecution.getVariables().get("comment");
        String assignee = (String) delegateExecution.getVariables().get("assignee");

        databaseLockProvider.lock(hlsDurationHd);
        if (APPROVING.equalsIgnoreCase(hlsDurationHd.getDurationStatus())) {
            if (APPROVED.equalsIgnoreCase(result) || DURATION_APPROVED.equalsIgnoreCase(result) || PRESIDENT_APPROVED.equalsIgnoreCase(result)) {
                flag = APPROVED;

                //如果是所有涉及保证金退款的变更  在保证金管理功能执行
                //如果是正常结清 审批通过直接执行
                if (TERMINATE.equals(hlsDurationHd.getDurationType())) {
                    //解押&&回购
                    hdService.executeLeaseItem(requestCtx, hlsDurationHd);
                    //更新合同状态
                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setProjectId(hlsDurationHd.getProjectId());
                    conContract.setDataClass(NORMAL);
                    List<HlsCusConContract> contractList = contractMapper.select(conContract);
                    contractList.stream().forEach(item -> {
                        item.setContractStatus(TERMINATE);
                        item.set__status("update");
                    });
                    contractService.batchUpdate(requestCtx, contractList);
                }
                //提前结清
                if (ET.equals(hlsDurationHd.getDurationType())) {
                    service.executeEt(requestCtx, hlsDurationHd);
                    //结清后 需要重新分摊
                    HlsCusConContract cusConContract = new HlsCusConContract();
                    cusConContract.setProjectId(hlsDurationHd.getProjectId());
                    List<HlsCusConContract> conContractLists = contractMapper.conGetContract(cusConContract);
                    for (HlsCusConContract ct : conContractLists) {
                        gldContractCashflowService.clacFinanceIncome(requestCtx, ct.getContractId(), ct.getVatRate(), ct.getXirr());
                    }
                    //变更通过 则原业务申请单据更新
//                    HlsDurationHd durationHd = new HlsDurationHd();
//                    durationHd.setHdId(hlsDurationHd.getHdId());
//                    durationHd.setDurationStatus("PASS");
//                    hdService.updateByPrimaryKey(requestCtx,durationHd);
                }
                //审批通过则需要将虚拟合同恢复签约状态
                HlsCusPrjProject project = new HlsCusPrjProject();
                project.setProjectId(hlsDurationHd.getProjectId());

                //针对合同 如果合同下所有支付表都已结清 则更新合同状态为结清 projectMapper
                List<HlsCusPrjProject> projects = projectMapper.queryContractEt(project);
                if (projects.size() > 0) {
                    project.setContractStatus("SIGN");
                } else {
                    //合同终止自动更新需新增条件：投放额（不含关税）的金额=合同金额
                    if(project.getLeaseItemAmount() == project.getActualPaymentAmount()){
                        project.setContractStatus("ET");
                    }else{
                        project.setContractStatus("SIGN");
                    }

                }

                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, project);
                hlsDurationHd.setDurationStatus(flag);
                hlsDurationHd.setDurationInstanceId(processInstanceId);
                service.updateByPrimaryKeySelective(requestCtx, hlsDurationHd);

            } else if (REJECTED.equalsIgnoreCase(result)) {
                flag = REJECTED;
                hlsDurationHd.setDurationStatus(flag);
                hlsDurationHd.setDurationInstanceId(processInstanceId);
                service.updateByPrimaryKeySelective(requestCtx, hlsDurationHd);
            } else if ("APPROVED_M".equalsIgnoreCase(result) && assignee != null) {
                ProjectMeetingApprover meetingApprover = new ProjectMeetingApprover();
                meetingApprover.setApproverUserId(Long.valueOf(assignee));
                meetingApprover.setProjectId(hlsDurationHd.getProjectId());
                List<ProjectMeetingApprover> approvers = approverMapper.queryAll(meetingApprover);
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
                List<ProjectMeetingApprover> approvers = approverMapper.queryAll(meetingApprover);
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
                List<ProjectMeetingApprover> approvers = approverMapper.queryAll(meetingApprover);
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
                List<ProjectMeetingApprover> approvers = approverMapper.queryAll(meetingApprover);
                if (approvers.size() > 0) {
                    for (ProjectMeetingApprover projectMeetingApprover : approvers) {
                        projectMeetingApprover.setVoteComment(comment);
                        projectMeetingApprover.setVoteResult(approveResultDesc);
                        approverService.updateByPrimaryKey(requestCtx, projectMeetingApprover);
                    }
                }
            }
        }

    }
}
