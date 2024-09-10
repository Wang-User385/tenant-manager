package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;
import org.apache.commons.lang3.StringUtils;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/8/30 9:28
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    private static final List<String> specificNodeIds = new ArrayList<>(
            Arrays.asList("FACTORING_PROJRCT_PROPOSALS9",
                    "FACTORING_PROJRCT_PROPOSALS10",
                    "FACTORING_PROJRCT_PROPOSALS11"));

    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";
    //移交
    private static final String DELEGATE = "DELEGATE";


    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(chanceId);
        chance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(requestCtx, chance);
        if (isValid(chance)) {
            updateCreditLineStatus(requestCtx, delegateExecution, chance, result);
        }
    }

    private void updateCreditLineStatus(IRequest requestCtx, DelegateExecution delegateExecution, HlsCusHlsCreditLineChance chance, String result) {
        if (APPROVED.equalsIgnoreCase(result)) {
            chance.setApprovedDate(new Date());
            chance.setCreditLineStatus(APPROVED);
        } else if (REJECTED.equalsIgnoreCase(result)) {
            chance.setCreditLineStatus(REJECTED);
            manualEnd(delegateExecution);
        } else if (PEER_REJECTED.equalsIgnoreCase(result)) {
            chance.setCreditLineStatus(PEER_REJECTED);
        } else if (DELEGATE.equalsIgnoreCase(result)) {
            chance.setCreditLineStatus(DELEGATE);
        }
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);
    }

    private void manualEnd(DelegateExecution delegateExecution) {
        List<Task> list = taskService
                .createTaskQuery()
                .processInstanceId(delegateExecution.getProcessInstanceId())
                .list();
        for (Task task : list) {
            String currentActivityId = task.getTaskDefinitionKey();
            if (!StringUtils.isEmpty(currentActivityId)) {
                if (specificNodeIds.contains(currentActivityId)) {
                    runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "执行特殊节点结束工作流");
                }
            }
        }
    }

    private boolean isValid(HlsCusHlsCreditLineChance chance) {
        return !APPROVED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !PEER_REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !DELEGATE.equalsIgnoreCase(chance.getCreditLineStatus());
    }

}
