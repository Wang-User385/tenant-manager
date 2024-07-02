package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import com.hand.hls.csh.mapper.HlsCusDepositRefundMapper;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.gld.components.JeTrxCommonService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName HlsCusDepositRefundSubmitServiceTask
 * @Description //TODO
 * @Author yuan.yuan01@hand-china.com
 * @Date 2019/3/11 1:17 PM
 * @Version 1.0
 **/
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositRefundSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private JeTrxCommonService commonService;
    @Autowired
    private HlsCusDepositRefundMapper depositRefundMapper;
    @Autowired
    private CshTransactionService cshTransactionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long depositRefundId = (Long) delegateExecution.getVariable("documentId");
        HlsCusDepositRefund depositRefund=new HlsCusDepositRefund();
        depositRefund.setDepositRefundId(depositRefundId);

        if("APPROVED".equalsIgnoreCase(result)){
            //头数据
            depositRefund=depositRefundMapper.selectDepositRefundData(depositRefund).get(0);

            try {
                HlsCusCshTransaction cshTransaction=new HlsCusCshTransaction();
                cshTransaction.setTransactionId(depositRefund.getTransactionId());
                cshTransaction.setBpId(depositRefund.getBpId());
                cshTransaction.setBpName(depositRefund.getBpName());
                cshTransaction.setBpBankAccountId(depositRefund.getBpBankAccountId());
                cshTransaction.setBpBankAccountNum(depositRefund.getBpBankAccountNum());
                cshTransaction.setBpBankAccountName(depositRefund.getBpBankAccountName());
                cshTransaction.setBpBankName(depositRefund.getBpBankName());
                cshTransaction.setBpBankBranchName(depositRefund.getBpBankBranchName());
                cshTransaction.setBankAccountId(depositRefund.getBankAccountId());
                cshTransaction.setBankAccountName(depositRefund.getBankAccountName());
                cshTransaction.setBankName(depositRefund.getBankName());
                cshTransaction.setBankAccountNum(depositRefund.getBankAccountNum());
                cshTransaction.setBankBranchName(depositRefund.getBankBranchName());
                //cshTransaction.setCfType(depositRefund.getCfType());
                cshTransaction.setCfItem(depositRefund.getCfItem());
                cshTransaction.setContractId(depositRefund.getContractId());
                cshTransaction.setCashflowId(depositRefund.getCashflowId());
                cshTransaction.setTimes(depositRefund.getTimes());
                cshTransaction.setDescription(depositRefund.getDescription());
                cshTransaction.setBankSlipNum(depositRefund.getBankSlipNum());
                cshTransaction.setTransactionDate(depositRefund.getRefundDate());
                cshTransaction.setPaymentMethod(depositRefund.getRefundMethod());
                cshTransaction.setTransactionAmount(depositRefund.getTransactionAmount());
                cshTransaction.setWriteOffAmount(depositRefund.getWriteOffAmount());
                cshTransaction.setReturnDueAmount(depositRefund.getRefundAmount());
                cshTransaction.setBusinessType("DEPOSIT");
                cshTransaction.setTransactionCategory("CSH_TRANSACTION");
                cshTransaction.setSourceDocCategory(depositRefund.getRefundDocCategory());
                cshTransaction.setSourceDocId(depositRefund.getContractId());
                cshTransaction.setSourceDocLineId(depositRefund.getCashflowId());
                cshTransactionService.updateReturn(iRequest,iRequest.getCompanyId(),cshTransaction);
               // cshWriteOffService.updateWriteOff(iRequest,cshwriteoffs,iRequest.getCompanyId());
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }

            depositRefund.setRefundStatus("APPROVED");
        }else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            depositRefund.setRefundStatus("APPROVED_RETURN");
        }else{
            depositRefund.setRefundStatus("REJECTED");
        }

        depositRefundMapper.updateByPrimaryKeySelective(depositRefund);
    }
}
