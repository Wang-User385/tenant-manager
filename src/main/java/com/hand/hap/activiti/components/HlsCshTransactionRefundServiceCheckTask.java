package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.utils.ResMessageException;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @description
 * @author dql
 * @date 2024/9/4 19:36:38
 */
@Service
public class HlsCshTransactionRefundServiceCheckTask  implements TaskListener, IActivitiBean {
    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Autowired
    private HlsCusCshTransactionRefundMapper cshTransactionRefundMapper;

    @Override
    public void notify(DelegateTask delegateTask) {
        TaskEntity task = (TaskEntity) delegateTask;
        Long refundId = (Long) delegateTask.getVariable("refundId");
        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund.setRefundId(refundId);
        transactionRefund = cshTransactionRefundMapper.selectByPrimaryKey(transactionRefund);

        String clickButtonEvent = delegateTask.getVariable("approveResult").toString();// 获取当前节点的点击操作
        //审批通过前校验当前已确认支付
        if (!APPROVED.equalsIgnoreCase(transactionRefund.getRefundStatus()) && !REJECTED.equalsIgnoreCase(transactionRefund.getRefundStatus())) {
            if (APPROVED.equalsIgnoreCase(clickButtonEvent)) {
                if (!"PAID".equals(transactionRefund.getPaymentRefundStatus()) ){
                    try {
                        throw new ResMessageException("当前单据未支付无法确认");
                    } catch (ResMessageException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
