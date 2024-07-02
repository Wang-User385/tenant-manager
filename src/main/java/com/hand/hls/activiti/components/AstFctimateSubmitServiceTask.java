package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.mapper.AstFcEstimateMapper;
import com.hand.hls.ast.service.IAstFcEstimateService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.dto.AssetsDisposalDetail;
import com.hand.hls.pam.mapper.AssetsDisposalDetailMapper;
import com.hand.hls.pam.mapper.AssetsDisposalMapper;
import com.hand.hls.pam.service.IAssetsDisposalDetailService;
import com.hand.hls.pam.service.IAssetsDisposalService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
public class AstFctimateSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private AstFcEstimateMapper astFcEstimateMapper;

    @Autowired
    private IAstFcEstimateService astFcEstimateService;
    public AstFctimateSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String scoreId = delegateExecution.getProcessInstanceBusinessKey();

        AstFcEstimate astFcEstimate = astFcEstimateMapper.selectByPrimaryKey(scoreId);
        AstFcEstimate astFcEstimateResult = astFcEstimateMapper.calculationNowCount(Long.valueOf(scoreId));
        if ("APPROVED".equalsIgnoreCase(result)) {
            astFcEstimate.setRaiseStatus("APPROVED");

            astFcEstimate.setBreakRate(astFcEstimateResult.getBreakRate());
            astFcEstimate.setNowCount(astFcEstimateResult.getNowCount());
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            astFcEstimate.setRaiseStatus("REJECTED");
        }
        astFcEstimateService.updateByPrimaryKeySelective(requestCtx, astFcEstimate);
    }

}
