package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hls.activiti.mapper.HlsCusActVarMapper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Eugene Song
 * 收款退款结束方法
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusCshTransactionRefundSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusCshTransactionRefundMapper hlsCusCshTransactionRefundMapper;
    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Autowired
    private HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private CshTransactionService cshTransactionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String refundId = String.valueOf(delegateExecution.getVariable("documentId"));

        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund = hlsCusCshTransactionRefundMapper.selectByPrimaryKey(refundId);
        transactionRefund.setProcessInstanceId(Long.parseLong(delegateExecution.getProcessInstanceId()));
        transactionRefund.setRefundStatus(result);
        cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, transactionRefund);

        HlsCusCshTransaction cshTransaction = hlsCusCshTransactionMapper.selectByPrimaryKey(transactionRefund.getTransactionId());
        List<HlsCusCshTransaction> cshTransactionList = new ArrayList<>();

        cshTransaction.setReturnDueAmount(transactionRefund.getRefundAmount());
        cshTransaction.setTransactionDate(transactionRefund.getRefundDate());
        cshTransaction.setBankAccountId(transactionRefund.getBankAccountId());
        cshTransaction.setBpBankAccountId(transactionRefund.getBpBankAccountId());
        cshTransaction.setBankSlipNum(transactionRefund.getBankSlipNum());
        cshTransaction.setCurrencyCode(transactionRefund.getCurrencyCode());
        cshTransactionList.add(cshTransaction);
        //审批通过 不直接退款 ，需要在 付款管理 待支付清单中退款
       /* if ("APPROVED".equalsIgnoreCase(result)) {
            try {
                cshTransactionService.refundCshTransaction(requestCtx, cshTransactionList);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        }*/
    }
}