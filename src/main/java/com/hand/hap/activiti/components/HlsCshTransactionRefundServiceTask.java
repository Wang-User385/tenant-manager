package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.service.CshTransactionRefundService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description 退款申请工作流结束监听器
 * @author dql
 * @date 2024/8/30 10:50:52
 */
@Service
public class HlsCshTransactionRefundServiceTask  implements JavaDelegate, IActivitiBean {
    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long refundId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund.setRefundId(refundId);
        transactionRefund = cshTransactionRefundService.selectByPrimaryKey(requestCtx, transactionRefund);
        if (!APPROVED.equalsIgnoreCase(transactionRefund.getRefundStatus()) && !REJECTED.equalsIgnoreCase(transactionRefund.getRefundStatus())) {
            if (APPROVED.equalsIgnoreCase(result)) {
                transactionRefund.setLastUpdateDate(new Date());
                transactionRefund.setRefundStatus(APPROVED);
                cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, transactionRefund);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                transactionRefund.setLastUpdateDate(new Date());
                transactionRefund.setRefundStatus(REJECTED);
                transactionRefund.setPaymentRefundStatus("CANCEL_PAY");
                cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, transactionRefund);
            }
        }
    }
}
