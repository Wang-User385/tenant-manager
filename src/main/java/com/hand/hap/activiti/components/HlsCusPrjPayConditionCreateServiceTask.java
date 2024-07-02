package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/18 19:52
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjPayConditionCreateServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private IProjectCreditConditionService service;

    public HlsCusPrjPayConditionCreateServiceTask() {
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String creditConditionIds = (String) delegateExecution.getVariable("creditConditionIds");
        String processDefinitionId =  delegateExecution.getProcessDefinitionId().split(":")[0];

        requestCtx.setAttribute("processDefinitionId", processDefinitionId);

        String processDefinitionId2 = requestCtx.getAttribute("processDefinitionId");

        if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equals(result)) {
            ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
            String a = Arrays.asList(creditConditionIds.split(",")).get(0);
            projectCreditCondition.setCreditConditionId(Long.valueOf(a));
            projectCreditCondition = service.selectByPrimaryKey(requestCtx, projectCreditCondition);
            try {
                if (projectCreditCondition.getEndTaskNodeId() == null) {
                    throw new RuntimeException();
                } else {
                    String endTaskNodeId = projectCreditCondition.getEndTaskNodeId();
                    delegateExecution.setVariable("endTaskNodeId", endTaskNodeId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new HlsCusException("请填写必输字段！");
            }
        }
    }
}
