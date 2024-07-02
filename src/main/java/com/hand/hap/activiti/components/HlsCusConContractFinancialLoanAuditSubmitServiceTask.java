package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.impl.ActivitiServiceImpl;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.csh.dto.CshPaymentReqLnBankAccount;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.ProjectCreditConditionMapper;
import com.hand.hls.csh.service.CshPaymentReqLnBankAccountService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.csh.service.IHlsCusCshPaymentReqLnService;
import com.hand.hls.utils.ResMessageException;
import leaf.bm.components.RecordHelper;
import lombok.SneakyThrows;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractFinancialLoanAuditSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;

    @Autowired
    private IHlsCusCshPaymentReqLnService cshPaymentReqLnService;

    @Autowired
    private CshPaymentReqLnBankAccountService bankAccountService;


    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");

        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(Long.parseLong(delegateExecution.getProcessInstanceBusinessKey()));
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(requestCtx, hlsCusCshPaymentReqHd);


        if ("APPROVED".equalsIgnoreCase(result)) {

            HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
            hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
            List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = cshPaymentReqLnService.selectSelective(requestCtx, hlsCusCshPaymentReqLn);
            CshPaymentReqLnBankAccount bankAccount = new CshPaymentReqLnBankAccount();
            List<CshPaymentReqLnBankAccount> bankAccountList = new ArrayList<>();

            //update 20230313:业务要求去掉线下支付的的功能，仅保留线上支付方式
            for (HlsCusCshPaymentReqLn cusCshPaymentReqLn : hlsCusCshPaymentReqLnList) {
                bankAccount.setPaymentReqLnId(cusCshPaymentReqLn.getPaymentReqLnId());
                bankAccountList.addAll(bankAccountService.selectSelective(requestCtx, bankAccount));
            }
            if (bankAccountList.size() == 0) {
                handleHistoryComment(delegateExecution);
                throw new ResMessageException("请填写实际支付信息!");
            }

            /*  update 20230313:业务要求去掉线下支付的的功能，仅保留线上支付方式
            //二期功能：付款方式为 线上支付（TT）的付款行记录
            int paymentMethodOfTTCount = 0;
            //二期功能：付款方式为 线下支付（OTHER）的付款行记录
            int paymentMethodOfOtherCount = 0;

            for (HlsCusCshPaymentReqLn cusCshPaymentReqLn : hlsCusCshPaymentReqLnList) {
                bankAccount.setPaymentReqLnId(cusCshPaymentReqLn.getPaymentReqLnId());
                bankAccountList.addAll(bankAccountService.selectSelective(requestCtx, bankAccount));
                if ("TT".equals(cusCshPaymentReqLn.getPaymentMethod())){
                    paymentMethodOfTTCount++;
                }
                if ("OTHER".equals(cusCshPaymentReqLn.getPaymentMethod())){
                    paymentMethodOfOtherCount++;
                }
            }
            //二期功能：如果付款行中的付款方式为 线上支付（TT），则实际支付信息必填；线下支付（OTHER）则不强制校验
            if (paymentMethodOfTTCount > 0 && bankAccountList.size() == 0) {
                throw new ResMessageException("付款行的付款方式为 线上支付，请填写实际支付信息!");
            }
            //二期功能：如果付款行中的付款方式为 线下支付（OTHER），且填写了实际支付信息，则校验 实际支付信息中的付款方式只能为 线下支付（OTHER）
            if (paymentMethodOfOtherCount > 0 && bankAccountList.size() > 0) {
                //二期功能：付款方式为 线上支付（TT）的实际支付信息行记录
                int lnBankPaymentMethodOfTTCount = 0;

                for (CshPaymentReqLnBankAccount lnBankAccount : bankAccountList) {
                    if ("TT".equals(lnBankAccount.getPaymentName())){
                        lnBankPaymentMethodOfTTCount++;
                    }
                }

                if (lnBankPaymentMethodOfTTCount > 0){
                    throw new ResMessageException("付款行的付款方式为 线下支付 时，实际支付信息中的付款方式只能选择 线下支付!");
                }
            }*/
            double sumLonAmount = hlsCusCshPaymentReqLnList.stream().mapToDouble(HlsCusCshPaymentReqLn::getDueAmountLn).sum();
            double sumPayAmount = bankAccountList.stream().mapToDouble(CshPaymentReqLnBankAccount::getPaymentAmount).sum();
            if (bankAccountList.size() > 0 && BigDecimal.valueOf(sumPayAmount).compareTo(BigDecimal.valueOf(sumLonAmount)) != 0) {
                handleHistoryComment(delegateExecution);
                throw new ResMessageException("支付总额不等于放款总额!");
            }

        }


    }

    private void handleHistoryComment(DelegateExecution delegateExecution) {
        Map map = new HashMap();
        map.put("proc_inst_id_", delegateExecution.getProcessInstanceId());
        List<Map> variableList = RecordHelper.select("act_hi_comment", map);
        int max=0;
        for (int i = 0; i < variableList.size(); i++) {
            if(Long.valueOf((String) variableList.get(max).get("id_"))<Long.valueOf((String) variableList.get(i).get("id_"))){
                max=i;
            }
        }
        map.put("id_",variableList.get(max).get("id_"));
        RecordHelper.delete("act_hi_comment", map);
        map.put("id_",variableList.get(max-1).get("id_"));
        RecordHelper.delete("act_hi_comment", map);
        map.put("id_",variableList.get(max-2).get("id_"));
        RecordHelper.delete("act_hi_comment", map);
        variableList = RecordHelper.select("act_hi_comment", map);
    }
}