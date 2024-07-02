package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.CshTransactionRefundLn;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.CshTransactionRefundLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusCshTransicationRefundSubmitBeforeServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    HlsCusCshTransactionRefundMapper hlsCusCshTransactionRefundMapper;
    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Autowired
    private CshTransactionRefundLnMapper cshTransactionRefundLnMapper;
    @Autowired
    CshWriteOffService cshWriteOffService;
    @Autowired
    HlsCusCshWriteOffMapper hlsCusCshWriteOffMapper;
    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    HlsCusCshTransactionMapper hlsCusCshTransactionMapper;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    public static final String PAYMENT = "PAYMENT";
    Map<String, String> params = new HashMap<String, String>();
    public HlsCusCshTransicationRefundSubmitBeforeServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String refundId = delegateExecution.getProcessInstanceBusinessKey();

        HlsCusCshTransactionRefund hlsCusCshTransactionRefund = hlsCusCshTransactionRefundMapper.selectByPrimaryKey(refundId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            hlsCusCshTransactionRefund.setPaymentRefundStatus("APPROVED");
            cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, hlsCusCshTransactionRefund);



        }
    }

}
