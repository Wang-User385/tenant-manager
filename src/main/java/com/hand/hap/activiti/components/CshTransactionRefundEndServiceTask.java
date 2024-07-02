package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.utils.HlsConstantUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description：二期功能：退款申请审批结束处理逻辑
 * @Author：liangxian.chen@hand-china.com
 * @Date：2022/12/26 15:16
 * @Version：1.0
 */
@Component
public class CshTransactionRefundEndServiceTask  implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusCshTransactionRefundMapper hlsCusCshTransactionRefundMapper;
    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private CshTransactionService cshTransactionService;

    private static final String RESULT_REJECTED = "REJECTED";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long refundId = Long.valueOf(String.valueOf(delegateExecution.getVariable("documentId")));

        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund.setRefundId(refundId);
        transactionRefund = cshTransactionRefundService.selectByPrimaryKey(requestCtx, transactionRefund);
        transactionRefund.setProcessInstanceId(Long.parseLong(delegateExecution.getProcessInstanceId()));
        //设置单据状态
        transactionRefund.setRefundStatus(result);
        cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, transactionRefund);

        //流程拒绝，释放冻结金额
        if (RESULT_REJECTED.equals(result)){
            List<HlsCusCshTransactionRefund> transactionRefundList = cshTransactionRefundService.refundInfoLnQuery(requestCtx, transactionRefund, 1, 0);

            for (HlsCusCshTransactionRefund refund : transactionRefundList) {
                if(HlsConstantUtil.TransactionType.ADVANCE_RECEIPT.equals(refund.getTransactionType()) ||
                        HlsConstantUtil.TransactionType.DEPOSIT.equals(refund.getTransactionType())) {
                    double unBlockAmount = 0D - refund.getBlockAmount();
                    cshTransactionService.blockAmount(requestCtx, refund.getTransactionId(), unBlockAmount);
                }
            }
        }

    }
}
