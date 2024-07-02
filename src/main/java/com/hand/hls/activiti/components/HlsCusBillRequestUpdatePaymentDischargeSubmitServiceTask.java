package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bill.service.IhlsBillRequestService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusBillRequestUpdatePaymentDischargeSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private hlsBillRequestMapper hlsBillRequestMapper;
    @Autowired
    private IhlsBillRequestService hlsBillRequestService;
    public HlsCusBillRequestUpdatePaymentDischargeSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String billId = delegateExecution.getProcessInstanceBusinessKey();

        hlsBillRequest hlsBillRequest = hlsBillRequestMapper.selectByPrimaryKey(billId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            hlsBillRequest.setBillStatus("PAYMENT_DISCHARGE");
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            hlsBillRequest.setBillStatus("REJECTED");
        }
        hlsBillRequestService.updateByPrimaryKeySelective(requestCtx, hlsBillRequest);
    }

}
