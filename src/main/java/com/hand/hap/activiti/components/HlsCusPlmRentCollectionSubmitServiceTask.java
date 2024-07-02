package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;

import com.hand.hls.plm.rc.service.HlsCusIRentCollectionService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author yuanyuan 2019/03/26 10:19 AM
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmRentCollectionSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusIRentCollectionService rentCollectionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        //审批结果
        String result = (String) delegateExecution.getVariable("approveResult");
        //流程ID
        String processInstanceId = delegateExecution.getProcessInstanceId();

        //更新催收状态
        if("APPROVED".equalsIgnoreCase(result)){
            rentCollectionService.updateCollectionStatus(processInstanceId,"APPROVED");
        }else{
            rentCollectionService.updateCollectionStatus(processInstanceId,"REJECTED");
        }

    }
}
