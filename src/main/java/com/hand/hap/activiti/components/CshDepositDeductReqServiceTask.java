package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.service.ICshDepositDeductReqHdService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CshDepositDeductReqServiceTask implements JavaDelegate, IActivitiBean {
    private static final String I_REQUEST = "iRequest";
    private static final String APPROVE_RESULT = "approveResult";
    private static final String START_USER_ID = "startUserId";

    private static final String CSH_DEPOSIT_DEDUCT_REQ_HD = "cshDepositDeductReqHd";

    @Autowired
    private ICshDepositDeductReqHdService iCshDepositDeductReqHdService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable(I_REQUEST);
        String result = (String) delegateExecution.getVariable(APPROVE_RESULT);
        String userId = String.valueOf(delegateExecution.getVariable(START_USER_ID));
        iRequest.setUserId(Long.valueOf(userId));

        String cshDepositDeductReqHdStr = (String) delegateExecution.getVariable(CSH_DEPOSIT_DEDUCT_REQ_HD);
        CshDepositDeductReqHd cshDepositDeductReqHd = JSON.parseObject(cshDepositDeductReqHdStr, CshDepositDeductReqHd.class);

        //审批结束
        iCshDepositDeductReqHdService.updateReqStatus(iRequest, cshDepositDeductReqHd.getReqHdId(), result);
    }
}
