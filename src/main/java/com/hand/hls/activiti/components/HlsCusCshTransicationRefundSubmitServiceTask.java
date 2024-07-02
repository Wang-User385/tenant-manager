package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.CshTransactionRefundLn;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.CshTransactionRefundLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.csh.mapper.HlsCusCshTransactionRefundMapper;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.csh.service.CshTransactionService;
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
public class HlsCusCshTransicationRefundSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    HlsCusCshTransactionRefundMapper hlsCusCshTransactionRefundMapper;
    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Autowired
    private CshTransactionRefundLnMapper cshTransactionRefundLnMapper;
    @Autowired
    CshWriteOffService cshWriteOffService;
    @Autowired
    CshTransactionService  cshTransactionService;
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
    public HlsCusCshTransicationRefundSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String refundId = delegateExecution.getProcessInstanceBusinessKey();

        HlsCusCshTransactionRefund hlsCusCshTransactionRefund = hlsCusCshTransactionRefundMapper.selectByPrimaryKey(refundId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            hlsCusCshTransactionRefund.setPaymentRefundStatus("PAID");
            hlsCusCshTransactionRefund.setRefundFlag("Y");
            cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, hlsCusCshTransactionRefund);
            CshTransactionRefundLn transactionRefundLn = new CshTransactionRefundLn();
            transactionRefundLn.setRefundId(hlsCusCshTransactionRefund.getRefundId());
            List<CshTransactionRefundLn> refundLnList = cshTransactionRefundLnMapper.select(transactionRefundLn);
            for (CshTransactionRefundLn refundLn : refundLnList) {
                //step 1 在核销表内新增一条数据
                HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                cshWriteOff.setWriteOffType("REFUND");
                cshWriteOff.setWriteOffDate(hlsCusCshTransactionRefund.getRefundDate());
                cshWriteOff.setCshTransactionId(hlsCusCshTransactionRefund.getTransactionId());
                cshWriteOff.setCshWriteOffAmount(refundLn.getRefundAmount());
                cshWriteOff.setReversedFlag("N");
                cshWriteOff.setWriteOffDueAmount(refundLn.getRefundAmount());
                cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
                //step 2 在现金事物表中增加一条退款记录
                HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
                hlsCusCshTransaction.setTransactionId(hlsCusCshTransactionRefund.getTransactionId());
                hlsCusCshTransaction = hlsCusCshTransactionMapper.selectByPrimaryKey(hlsCusCshTransactionRefund.getTransactionId());

                HlsCusCshTransaction returnCsh = new HlsCusCshTransaction(hlsCusCshTransaction);
                returnCsh.setTransactionId(null);
                returnCsh.setTransactionCategory("CSH_TRANSACTION");
                returnCsh.setTransactionType("REFUND");
                if (!"DEPOSIT".equals(hlsCusCshTransaction.getBusinessType()) && !"DEPOSIT_POOL".equals(hlsCusCshTransaction.getBusinessType())) {
                    returnCsh.setBusinessType("PAYMENT");
                } else {
                    //如果是保证金退款，则类型为DEPOSIT
                    returnCsh.setBusinessType(hlsCusCshTransaction.getBusinessType());
                }

                returnCsh.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, returnCsh.getTransactionCategory(), returnCsh.getTransactionType(), PAYMENT, params));
                returnCsh.setTransactionDate(hlsCusCshTransactionRefund.getRefundDate());
                returnCsh.setPenaltyCalcDate(hlsCusCshTransactionRefund.getRefundDate());
                returnCsh.setCompanyId(requestCtx.getCompanyId());
                returnCsh.setCurrencyCode(hlsCusCshTransactionRefund.getCurrencyCode());
                returnCsh.setTransactionAmount(refundLn.getRefundAmount());
                returnCsh.setReversedFlag("N");
                returnCsh.setReversedTrxId(null);
                returnCsh.setReversedDate(null);
                returnCsh.setPostedFlag("N");
                returnCsh.setContractId(null);
                returnCsh.setHandlingCharge(null);
                returnCsh.setWriteOffFlag("NOT");
                returnCsh.setWriteOffAmount(0d);
                returnCsh.setFullWriteOffDate(null);
                returnCsh.setSourceDocCategory("CSH_WRITE_OFF");
                returnCsh.setSourceDocId(hlsCusCshTransaction.getTransactionId());
                returnCsh.setSourceDocLineId(cshWriteOff.getWriteOffId());
                //self().insertSelective(requestCtx, returnCsh);
                cshTransactionService.insertSelective(requestCtx, returnCsh);


                //step 3 更新现金是服务中的核销金额和核销标志（判断是否核销完全，完全核销时更新完全核销日期）
                hlsCusCshTransactionMapper.updateCshTrByPrimaryKey(hlsCusCshTransaction);

                //step 4 更新核销表中的后续现金事物Id
                hlsCusCshWriteOffMapper.updateSubCshTrxId(cshWriteOff.getWriteOffId(), returnCsh.getTransactionId(), returnCsh.getTransactionAmount());

                //step 5 凭证
                Map<String, AbstractJeTrxService> interfaceMaps = jeTrxCommonService.map;
                AbstractJeTrxService writeOffJeTrxService = interfaceMaps.get("CSH_WRITE_OFF");
                AbstractJeTrxService transactionJeTrxService = interfaceMaps.get("CSH_TRANSACTION");

                /*更新凭证事物流水*/
             /*   Map writeOffParams = new HashMap<>();
                writeOffParams.put("jeTrxId", cshWriteOff.getWriteOffId());
                writeOffParams.put("companyId", requestCtx.getCompanyId());
                writeOffParams.put("contractId", cshWriteOff.getContractId());
                writeOffParams.put("sourceDoc", "CON_CONTRACT");
                writeOffJeTrxService.process(requestCtx, writeOffParams);

                Map transactionParams = new HashMap<>();
                transactionParams.put("jeTrxId", returnCsh.getTransactionId());
                transactionParams.put("companyId", requestCtx.getCompanyId());
                transactionParams.put("contractId", hlsCusCshTransaction.getContractId());
                transactionParams.put("sourceDoc", "CON_CONTRACT");
                transactionJeTrxService.process(requestCtx, transactionParams);*/


            }


            } else if ("REJECTED".equalsIgnoreCase(result)) {
            hlsCusCshTransactionRefund.setPaymentRefundStatus("REJECTED");
            cshTransactionRefundService.updateByPrimaryKeySelective(requestCtx, hlsCusCshTransactionRefund);

        }
    }

}
