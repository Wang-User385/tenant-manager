package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.mapper.AstFcEstimateMapper;
import com.hand.hls.ast.service.IAstFcEstimateService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class AstFctimateBSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private AstFcEstimateMapper astFcEstimateMapper;

    @Autowired
    private IAstFcEstimateService astFcEstimateService;
    public AstFctimateBSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String scoreId = delegateExecution.getProcessInstanceBusinessKey();

        AstFcEstimate astFcEstimate = astFcEstimateMapper.selectByPrimaryKey(scoreId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            astFcEstimate.setNowCountStatus("APPROVED");

        } else if ("REJECTED".equalsIgnoreCase(result)) {
            astFcEstimate.setNowCountStatus("REJECTED");
        }
        astFcEstimateService.updateByPrimaryKeySelective(requestCtx, astFcEstimate);
    }

}
