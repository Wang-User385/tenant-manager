package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.mapper.ProjectCreditConditionMapper;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/4/28 15:28
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjCreditConditionSubmitServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";
    private static final String APPROVED_RETURN = "APPROVED_RETURN";
    private static final String REJECTED = "REJECTED";


    @Autowired
    private IProjectCreditConditionService service;
    @Autowired
    private ProjectCreditConditionMapper mapper;

    public HlsCusPrjCreditConditionSubmitServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {

        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String creditConditionIds = (String) delegateExecution.getVariable("creditConditionIds");
        List<String> creditConditionIdList = Arrays.asList(creditConditionIds.split(","));

        if(!creditConditionIdList.isEmpty()){
            creditConditionIdList.forEach(item -> {
                ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
                projectCreditCondition.setCreditConditionId(Long.parseLong(item));
                projectCreditCondition = service.selectByPrimaryKey(requestCtx, projectCreditCondition);
                if (APPROVED.equalsIgnoreCase(result)) {

                    projectCreditCondition.setApprovalFlag("APPROVED");

                } else if (APPROVED_RETURN.equalsIgnoreCase(result)) {

                    projectCreditCondition.setApprovalFlag("APPROVED_RETURN");

                } else if (REJECTED.equalsIgnoreCase(result)) {

                    projectCreditCondition.setApprovalFlag("REJECTED");
                }

                service.updateByPrimaryKeySelective(requestCtx, projectCreditCondition);
            });
        }
    }
}
