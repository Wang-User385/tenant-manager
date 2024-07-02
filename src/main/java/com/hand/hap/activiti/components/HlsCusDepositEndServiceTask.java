package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationDepositMapper;
import com.hand.hls.hls.mapper.HlsDurationHdMapper;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationDepositService;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.hls.service.HlsDurationLnService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.*;

/**
 * description 保证金执行结束过程
 *
 * @author Eugene Song 2020年6月11日
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositEndServiceTask implements JavaDelegate, IActivitiBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String REJECTED = "REJECTED";

    private static final String APPROVED = "APPROVED";

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private HlsDurationDepositService hlsDurationDepositService;
    @Autowired
    private HlsDurationDepositMapper hlsDurationDepositMapper;
    @Autowired
    private HlsDurationHdService hlsDurationHdService;
    @Autowired
    private HlsDurationHdMapper hlsDurationHdMapper;

    @Autowired
    private HlsDurationLnService hlsDurationLnService;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;

    @Autowired
    private CshTransactionService cshTransactionService;

    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;

    @Autowired
    private IConContractCashflowService cashflowService;

    @Autowired
    private CshWriteOffService cshWriteOffService;


    public HlsCusDepositEndServiceTask() {

    }


    /**
     * @Title: setCshTransaction
     * @Discription: 构建现金事务
     * @Param: [requestCtx, deposit, hlsCusCshTransaction]
     * @Return: com.hand.hls.csh.dto.HlsCusCshTransaction
     */
    private HlsCusCshTransaction setCshTransaction(IRequest requestCtx, HlsDurationDeposit deposit, HlsCusCshTransaction hlsCusCshTransaction) throws ParseException {

        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                "PAYMENT", "PAYMENT", null));
        hlsCusCshTransaction.setTransactionDate(df.parse(df.format(new Date())));
        hlsCusCshTransaction.setPenaltyCalcDate(df.parse(df.format(new Date())));
        hlsCusCshTransaction.setCompanyId(requestCtx.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");
        hlsCusCshTransaction.setBankAccountId(deposit.getBankAccountId());
        hlsCusCshTransaction.setBpBankAccountId(deposit.getBpBankAccountId());

        return hlsCusCshTransaction;
    }

    /**
     * @Title: setCshWriteOff
     * @Discription: 构建核销事务
     * @Param: [ln, cshWriteOff, cshTransactionId, writeOffType]
     * @Return: com.hand.hls.csh.dto.HlsCusCshWriteOff
     */
    private HlsCusCshWriteOff setCshWriteOff(HlsDurationLn ln, HlsCusCshWriteOff cshWriteOff, Long cshTransactionId, String writeOffType) throws ParseException {

        cshWriteOff.setWriteOffType(writeOffType);
        cshWriteOff.setWriteOffDate(df.parse(df.format(new Date())));
        cshWriteOff.setCshTransactionId(cshTransactionId);
        cshWriteOff.setReversedFlag("N");
        cshWriteOff.setCashflowId(ln.getCashflowId());
        cshWriteOff.setContractId(ln.getContractId());
        HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(ln.getCashflowId());
        cshWriteOff.setTimes(cashflow.getTimes());
        cshWriteOff.setCfItem(cashflow.getCfItem());
        cshWriteOff.setCfType(cashflow.getCfType());
        cshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
        cshWriteOff.setImportFlag("N");
        cshWriteOff.setFirstLeasePayFlag("N");

        return cshWriteOff;
    }


    private void updateCashflow(IRequest requestCtx, HlsCusCshWriteOff cshWriteOff) throws ResMessageException {

        HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        //剩余可核销金额 < 本次核销金额
        Double residualAmount = sub(cashflow.getDueAmount(), nvl(cashflow.getReceivedAmount(), 0.0));
        if (residualAmount.compareTo(cshWriteOff.getWriteOffDueAmount()) == -1) {
            throw new ResMessageException("抵扣金额:" + cshWriteOff.getWriteOffDueAmount() + " 大于剩余可核销金额: " + residualAmount);
        }

        Double residualPrincipal = sub(nvl(cashflow.getPrincipal(), 0.0), nvl(cashflow.getReceivedPrincipal(), 0.0));
        if (residualPrincipal.compareTo(nvl(cshWriteOff.getWriteOffPrincipal(),0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffPrincipal(),0.0) + " 大于剩余可核销本金: " + residualPrincipal);
        }

        Double residualInterest = sub(nvl(cashflow.getInterest(), 0.0), nvl(cashflow.getReceivedInterest(), 0.0));
        if (residualInterest.compareTo(nvl(cshWriteOff.getWriteOffInterest(),0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffInterest(),0.0) + " 大于剩余可核销本金: " + residualInterest);
        }

        cashflow.setReceivedAmount(add(nvl(cashflow.getReceivedAmount(), 0.0), cshWriteOff.getWriteOffDueAmount()));
        cashflow.setReceivedPrincipal(add(nvl(cashflow.getReceivedPrincipal(), 0.0), nvl(cshWriteOff.getWriteOffPrincipal(),0.0)));
        cashflow.setReceivedInterest(add(nvl(cashflow.getReceivedInterest(), 0.0), nvl(cshWriteOff.getWriteOffInterest(),0.0)));

        if (residualAmount.compareTo(cshWriteOff.getWriteOffDueAmount()) == 0) {
            cashflow.setWriteOffFlag("FULL");
        } else {
            cashflow.setWriteOffFlag("PARTIAL");
        }
        cashflowService.updateByPrimaryKeySelective(requestCtx, cashflow);


    }

    private void deduction(IRequest requestCtx, HlsDurationDeposit deposit, List<HlsDurationLn> lnList) throws ParseException, ResMessageException {
        //插入deduction 现金事务
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionType("DEDUCTION");
        hlsCusCshTransaction.setBusinessType("DEDUCTION");
        hlsCusCshTransaction.setTransactionAmount(0.0);
        hlsCusCshTransaction.setCurrencyCode(deposit.getCurrencyCode());
        hlsCusCshTransaction = setCshTransaction(requestCtx, deposit, hlsCusCshTransaction);
        cshTransactionService.insertSelective(requestCtx, hlsCusCshTransaction);

        //插入核销记录
        //获取抵扣金额大于0 的记录
        lnList = lnList.stream().filter(item ->
                nvl(item.getDeductionAmount(),0.0).compareTo(0.0) == 1).collect(Collectors.toList());

        for (HlsDurationLn ln : lnList) {
            HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
            if (ln.getSourceType().equals("DEPOSIT") || ln.getSourceType().equals("INTEREST")) {


                cshWriteOff.setCshWriteOffAmount(ln.getDeductionAmount());
                cshWriteOff.setWriteOffDueAmount(ln.getDeductionAmount());
                cshWriteOff.setWriteOffPrincipal(ln.getDeductionPrincipal());
                cshWriteOff.setWriteOffInterest(ln.getDeductionInterest());
                cshWriteOff = setCshWriteOff(ln, cshWriteOff, hlsCusCshTransaction.getTransactionId(), "PAYMENT_DEBT");
                cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
                //更新现金流
                updateCashflow(requestCtx, cshWriteOff);

            } else if (ln.getSourceType().equals("CREDIT")) {

                cshWriteOff.setCshWriteOffAmount(ln.getDeductionAmount());
                cshWriteOff.setWriteOffDueAmount(ln.getDeductionAmount());
                cshWriteOff.setWriteOffPrincipal(ln.getDeductionPrincipal());
                cshWriteOff.setWriteOffInterest(ln.getDeductionInterest());
                cshWriteOff = setCshWriteOff(ln, cshWriteOff, hlsCusCshTransaction.getTransactionId(), "RECEIPT_CREDIT");
                cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
                //更新现金流
                updateCashflow(requestCtx, cshWriteOff);
            }
        }
    }


    private void refund(IRequest requestCtx, HlsDurationDeposit deposit, List<HlsDurationLn> lnList) throws ParseException, ResMessageException {
        //不再使用 4.2 现金事务的方式 处理退款 ，而是通过 52 保证金退款，502 保证金利息退款 现金流 操作
        //插入现金事务
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionType("REFUND");
        hlsCusCshTransaction.setBusinessType("DEPOSIT");
        lnList = lnList.stream().filter(item ->
                nvl(item.getRefundAmount(),0.0).compareTo(0.0) == 1).collect(Collectors.toList());

        Double transactionAmount = round(lnList.stream().collect(Collectors.summingDouble(HlsDurationLn::getRefundAmount)), 2);

        hlsCusCshTransaction.setTransactionAmount(transactionAmount);
        hlsCusCshTransaction.setCurrencyCode(deposit.getCurrencyCode());
        hlsCusCshTransaction = setCshTransaction(requestCtx, deposit, hlsCusCshTransaction);
        cshTransactionService.insertSelective(requestCtx, hlsCusCshTransaction);
        //插入核销事务
        for (HlsDurationLn ln : lnList) {

            HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();

            if (ln.getSourceType().equals("DEPOSIT") || ln.getSourceType().equals("INTEREST")) {

                cshWriteOff.setCshWriteOffAmount(ln.getRefundAmount());
                cshWriteOff.setWriteOffDueAmount(ln.getRefundAmount());
                cshWriteOff = setCshWriteOff(ln, cshWriteOff, hlsCusCshTransaction.getTransactionId(), "REFUND");
                cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
                //更新现金流
                updateCashflow(requestCtx, cshWriteOff);
            }
        }


    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long sourceId = (Long) delegateExecution.getVariable("sourceId");
        Long hdId = (Long) delegateExecution.getVariable("hdId");
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsDurationDeposit deposit = hlsDurationDepositMapper.selectByPrimaryKey(sourceId);


        Example durationLnExample = new Example(HlsDurationLn.class);
        durationLnExample.createCriteria().
                andEqualTo("sourceId", sourceId).
                andEqualTo("hdId", hdId).
                andIn("sourceType", Arrays.asList("DEPOSIT", "INTEREST", "CREDIT"));

        List<HlsDurationLn> lnList = hlsDurationLnMapper.selectByExample(durationLnExample);


        databaseLockProvider.lock(deposit);

        if (APPROVED.equalsIgnoreCase(result)) {

            if (HlsCusConstant.FLAG.Y.equals(deposit.getIsDeductionFlag())) {
                try {
                    deduction(requestCtx, deposit, lnList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (HlsCusConstant.FLAG.Y.equals(deposit.getIsRefundFlag())) {
                try {
                    refund(requestCtx, deposit, lnList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }

        deposit.setExecuteStatus(result);
        deposit.setProcessInstanceId(processInstanceId);
        hlsDurationDepositService.updateByPrimaryKeySelective(requestCtx, deposit);


        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        hd.setExecuteInstanceId(processInstanceId);
        hd.setExecuteStatus(result);
        hlsDurationHdService.updateByPrimaryKeySelective(requestCtx, hd);
    }
}
