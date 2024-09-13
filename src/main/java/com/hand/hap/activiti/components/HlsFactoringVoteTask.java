package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.mapper.HlsCreditLineChanceApproverMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.HlsICreditLineChanceApproverService;
import com.hand.hls.fnd.dto.PrjMeetingJudge;
import com.hand.hls.fnd.mapper.PrjMeetingJudgeMapper;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.SysUserMapper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/9/12 14:03
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringVoteTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCreditLineChanceApproverMapper hlsCreditLineChanceApproverMapper;

    @Autowired
    private PrjMeetingJudgeMapper prjMeetingJudgeMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private HlsICreditLineChanceApproverService hlsICreditLineChanceApproverService;

    private static final String APPROVE = "APPROVE";
    private static final String REFUSE = "REFUSE";
    private static final String CONDITION = "CONDITION";

    @Autowired
    private HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    private static final String VOTE_APPROVED = "10";
    private static final String VOTE_REJECTED = "20";
    private static final String VOTE_CONDITION = "30";

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        generateApprover(delegateExecution, chanceId, requestCtx, result, processInstanceId);

    }

    private void generateApprover(DelegateExecution delegateExecution, Long chanceId, IRequest requestCtx, String result, Long processInstanceId) {
        HlsCreditLineChanceApprover approver = new HlsCreditLineChanceApprover();
        List<Task> list = taskService
                .createTaskQuery()
                .processInstanceId(delegateExecution.getProcessInstanceId())
                .list();
        String nodeId = list.get(0).getTaskDefinitionKey();
        List<String> commentInfo = hlsCusHlsCreditLineChanceMapper.getWflComment(processInstanceId, chanceId, nodeId);
        PrjMeetingJudge prjMeetingJudge = new PrjMeetingJudge();
        prjMeetingJudge.setEnabledFlag("Y");
        approver.setChanceId(chanceId);
        List<PrjMeetingJudge> judgeList = prjMeetingJudgeMapper.select(prjMeetingJudge);
        List<SysUser> users = sysUserMapper.findAllocationIdByUserID(requestCtx.getUserId());
        List<HlsCreditLineChanceApprover> voteInfo = hlsCreditLineChanceApproverMapper.findVoteInfo(approver);
        if (!CollectionUtils.isEmpty(users)) {
            approver.setAllocationId(users.get(0).getAllocationId());
        }
        if (!CollectionUtils.isEmpty(commentInfo)) {
            approver.setVoteComment(commentInfo.get(commentInfo.size() - 1));
        }
        //防止工作流的缺陷
        for (HlsCreditLineChanceApprover hlsCreditLineChanceApprover : voteInfo) {
            if (Objects.equals(hlsCreditLineChanceApprover.getAllocationId(), approver.getAllocationId())) {
                return;
            }
        }
        if (APPROVE.equals(result)) {
            approver.setVoteStatus(VOTE_APPROVED);
        } else if (CONDITION.equals(result)) {
            approver.setVoteStatus(VOTE_CONDITION);
        } else if (REFUSE.equals(result)) {
            approver.setVoteStatus(VOTE_REJECTED);
        }
        if (!CollectionUtils.isEmpty(judgeList)) {
            //当所有人已经投票但是还是走了这函数说明退回重新开始投票
            if (judgeList.size() == voteInfo.size()) {
                hlsICreditLineChanceApproverService.batchDelete(voteInfo);
            }
            hlsICreditLineChanceApproverService.insert(requestCtx, approver);
        }
    }
}