package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationDepositMapper;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationDepositService;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.add;
import static com.hand.hls.utils.HlsCusMathUtil.sub;

/**
 * description 存续期结束过程
 *
 * @author Eugene Song 2020年6月11日
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusDurationEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String REJECTED = "REJECTED";

    private static final String APPROVED = "APPROVED";
    public static final String DURATION_APPROVED = "DURATION_APPROVED";

    @Autowired
    private HlsDurationHdService service;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;

    @Autowired
    FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private HlsDurationDepositMapper hlsDurationDepositMapper;
    @Autowired
    private CshTransactionService cshTransactionService;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private CshWriteOffService cshWriteOffService;
    @Autowired
    private IConContractCashflowService cashflowService;
    @Autowired
    private HlsDurationDepositService hlsDurationDepositService;
    @Autowired
    private HlsDurationHdService hlsDurationHdService;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public HlsCusDurationEndServiceTask() {

    }
    public String getCodeValue(IRequest requestContext) {
        Map<String, String> params = new HashMap<String, String>();
        return codingRuleValuesService.getCodeRuleValue(requestContext, "CSH_PAYMENT_REQ", "PAYMENT_REQ", "PAYMENT_REQ", params);
    }

    private void updateCashflow(IRequest requestCtx, HlsCusCshWriteOff cshWriteOff) throws ResMessageException {

        HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
        //剩余可核销金额 < 本次核销金额
        Double residualAmount = sub(cashflow.getDueAmount(), nvl(cashflow.getReceivedAmount(), 0.0));
        if (residualAmount.compareTo(cshWriteOff.getWriteOffDueAmount()) == -1) {
            throw new ResMessageException("抵扣金额:" + cshWriteOff.getWriteOffDueAmount() + " 大于剩余可核销金额: " + residualAmount);
        }

        Double residualPrincipal = sub(nvl(cashflow.getPrincipal(), 0.0), nvl(cashflow.getReceivedPrincipal(), 0.0));
        if (residualPrincipal.compareTo(nvl(cshWriteOff.getWriteOffPrincipal(), 0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffPrincipal(), 0.0) + " 大于剩余可核销本金: " + residualPrincipal);
        }

        Double residualInterest = sub(nvl(cashflow.getInterest(), 0.0), nvl(cashflow.getReceivedInterest(), 0.0));
        if (residualInterest.compareTo(nvl(cshWriteOff.getWriteOffInterest(), 0.0)) == -1) {
            throw new ResMessageException("抵扣本金:" + nvl(cshWriteOff.getWriteOffInterest(), 0.0) + " 大于剩余可核销本金: " + residualInterest);
        }

        cashflow.setReceivedAmount(add(nvl(cashflow.getReceivedAmount(), 0.0), cshWriteOff.getWriteOffDueAmount()));
        cashflow.setReceivedPrincipal(add(nvl(cashflow.getReceivedPrincipal(), 0.0), nvl(cshWriteOff.getWriteOffPrincipal(), 0.0)));
        cashflow.setReceivedInterest(add(nvl(cashflow.getReceivedInterest(), 0.0), nvl(cshWriteOff.getWriteOffInterest(), 0.0)));

        if (residualAmount.compareTo(cshWriteOff.getWriteOffDueAmount()) == 0) {
            cashflow.setWriteOffFlag("FULL");
        } else {
            cashflow.setWriteOffFlag("PARTIAL");
        }
        cashflowService.updateByPrimaryKeySelective(requestCtx, cashflow);


    }

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
    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = "";
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long hdId = (Long) delegateExecution.getVariable("hdId");
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsDurationHd hlsDurationHd = new HlsDurationHd();

        hlsDurationHd.setHdId(hdId);
        hlsDurationHd = service.selectByPrimaryKey(requestCtx, hlsDurationHd);



        databaseLockProvider.lock(hlsDurationHd);


        if ((APPROVED.equalsIgnoreCase(result)|| DURATION_APPROVED.equalsIgnoreCase(result))&& !hlsDurationHd.getDurationStatus().equalsIgnoreCase("APPROVED")) {
            flag = "APPROVED";

            HlsDurationDeposit hlsDurationDeposit = new HlsDurationDeposit();
            hlsDurationDeposit.setHdId(hdId);
            List<HlsDurationDeposit> hlsDurationDepositList  =   hlsDurationDepositMapper.hlsDurationDepositDetailQuery(hlsDurationDeposit);
            if ("Y".equalsIgnoreCase(hlsDurationDepositList.get(0).getIsRefundFlag())) {
               HlsDurationLn hlsDurationLn =new HlsDurationLn();
                hlsDurationLn.setHdId(hdId);
                hlsDurationLn.setContractId(hlsDurationDepositList.get(0).getContractId());
                hlsDurationLn.setCfItem(Long.valueOf(52));
                List<HlsDurationLn> HlsDurationLnList = hlsDurationLnMapper.hlsDurationLnDepositQuery(hlsDurationLn);
                HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
                hlsCusCshPaymentReqHd.setCompanyId(requestCtx.getCompanyId());
                hlsCusCshPaymentReqHd.setDocumentCategory("CSH_PAYMENT_REQ");
                hlsCusCshPaymentReqHd.setDocumentType("PAYMENT_REQ");
                hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
                String value = getCodeValue(requestCtx);
                hlsCusCshPaymentReqHd.setPaymentReqNumber(value);
                hlsCusCshPaymentReqHd.setPaymentReqStatus("APPROVED");
                hlsCusCshPaymentReqHd.setProjectName(hlsDurationHd.getProjectName());
                hlsCusCshPaymentReqHd.setProjectId(hlsDurationHd.getProjectId());
                hlsCusCshPaymentReqHd.setPaymentReqDate(hlsDurationDepositList.get(0).getRefundDate());
                hlsCusCshPaymentReqHd.setCurrency(hlsDurationDepositList.get(0).getCurrency());
                hlsCusCshPaymentReqHd.setSourceDocId(hlsDurationHd.getProjectId());
                hlsCusCshPaymentReqHd.setSourceDocType("CON_CONTRACT");
                hlsCusCshPaymentReqHd.setDeductFlag("N");
                hlsCusCshPaymentReqHd.setSourceContractId(hlsDurationDepositList.get(0).getContractId());
                hlsCusCshPaymentReqHd.setProposedLaunchDate(hlsDurationDepositList.get(0).getRefundDate());
                hlsCusCshPaymentReqHd.setLoanTotalAmount(HlsDurationLnList.get(0).getRefundAmount());
                hlsCusCshPaymentReqHd.setContractCurrency(hlsDurationDepositList.get(0).getContractCurrency());
                hlsCusCshPaymentReqHd.setContractCurrencyId(hlsDurationDepositList.get(0).getContractCurrency());
                hlsCusCshPaymentReqHd.setFinanceAmount(hlsDurationDepositList.get(0).getFinanceAmount());
                hlsCusCshPaymentReqHd.setSumToufangAmount(hlsDurationDepositList.get(0).getSumToufangAmount());
                hlsCusCshPaymentReqHd.setContractBalance(hlsDurationDepositList.get(0).getContractBalance());
                hlsCusCshPaymentReqHd.setProjectName(hlsDurationDepositList.get(0).getPrjProjectName());
                hlsCusCshPaymentReqHd.setBpName(hlsDurationDepositList.get(0).getBpName());
                hlsCusCshPaymentReqHd.setFinanceAmount(hlsDurationDepositList.get(0).getFinanceAmount());
                hlsCusCshPaymentReqHd.setEmployeeId(hlsDurationHd.getApplyPerson());
                hlsCusCshPaymentReqHd.setUnitId(hlsDurationHd.getApplyUnitId());
                hlsCusCshPaymentReqHd.setPaymentType("REFUND_DEPOSIT");
                hlsCusCshPaymentReqHd.setTransferStatus("NEW");
                hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
                hlsCusCshPaymentReqHd.setCreatedBy(requestCtx.getUserId());
                hlsCusCshPaymentReqHd.setCreationDate(new Date());
                hlsCusCshPaymentReqHd.setLoanType("ROUTINE");
                cshPaymentReqHdService.insertSelective(requestCtx, hlsCusCshPaymentReqHd);
                HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = new HlsCusCshPaymentReqLn();
                hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
                hlsCusCshPaymentReqLn.setSourceDocCategory("CON_CONTRACT");
                hlsCusCshPaymentReqLn.setSourceDocId(hlsCusCshPaymentReqHd.getSourceDocId());
                hlsCusCshPaymentReqLn.setSourceDocLineId(HlsDurationLnList.get(0).getCashflowId());
                hlsCusCshPaymentReqLn.setPaymentMethod("TT");
                hlsCusCshPaymentReqLn.setSeqNum(Long.valueOf(1));
                hlsCusCshPaymentReqLn.setDueAmountLn(HlsDurationLnList.get(0).getRefundAmount());
                hlsCusCshPaymentReqLn.setSumDueAmount(HlsDurationLnList.get(0).getRefundAmount());
                hlsCusCshPaymentReqLn.setCurrency(hlsCusCshPaymentReqHd.getCurrency());
                hlsCusCshPaymentReqLn.setBpId(hlsDurationDepositList.get(0).getBpId());
                hlsCusCshPaymentReqLn.setBpBankAccountId(hlsDurationDepositList.get(0).getBpBankAccountId());
                hlsCusCshPaymentReqLn.setBpBankBranchName(hlsDurationDepositList.get(0).getBankFullName());
                hlsCusCshPaymentReqLn.setBpBankAccountNum(hlsDurationDepositList.get(0).getBpBankAccountNum());
                hlsCusCshPaymentReqLn.setBpBankAccountName(hlsDurationDepositList.get(0).getBpBankAccountName());
                hlsCusCshPaymentReqLn.setSwift(hlsDurationDepositList.get(0).getSwift());
                hlsCusCshPaymentReqLn.setBankCode(hlsDurationDepositList.get(0).getBankCode());
                hlsCusCshPaymentReqLn.setIban(hlsDurationDepositList.get(0).getIban());
                cshPaymentReqLnService.insertSelective(requestCtx, hlsCusCshPaymentReqLn);
            }
            if ("Y".equalsIgnoreCase(hlsDurationDepositList.get(0).getIsDeductionFlag())) {
/*
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
*/
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                //插入deduction 现金事务
                HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
                hlsCusCshTransaction.setTransactionType("DEDUCTION");
                hlsCusCshTransaction.setBusinessType("DEPOSIT");
                hlsCusCshTransaction.setTransactionAmount(0.0);
                hlsCusCshTransaction.setCurrencyCode(hlsDurationDepositList.get(0).getCurrencyCode());
                hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
                hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                        "PAYMENT", "PAYMENT", null));
                hlsCusCshTransaction.setTransactionDate(df.parse(df.format(new Date())));
                hlsCusCshTransaction.setPenaltyCalcDate(df.parse(df.format(new Date())));
                hlsCusCshTransaction.setCompanyId(requestCtx.getCompanyId());
                hlsCusCshTransaction.setReversedFlag("N");
                hlsCusCshTransaction.setPostedFlag("N");
                hlsCusCshTransaction.setWriteOffFlag("NOT");
                hlsCusCshTransaction.setBpId(hlsDurationDepositList.get(0).getBpId());
                hlsCusCshTransaction.setContractId(hlsDurationDepositList.get(0).getContractId());
                hlsCusCshTransaction.setBankAccountId(hlsDurationDepositList.get(0).getBankAccountId());
                hlsCusCshTransaction.setSourceDocCategory("CSH_PAYMENT_REQ");
                hlsCusCshTransaction.setBpBankAccountId(hlsDurationDepositList.get(0).getBpBankAccountId());
                cshTransactionService.insertSelective(requestCtx, hlsCusCshTransaction);

                Example durationLnExample = new Example(HlsDurationLn.class);
                durationLnExample.createCriteria().
                        andEqualTo("sourceId", hlsDurationDepositList.get(0).getSourceId()).
                        andEqualTo("hdId", hdId).
                        andIn("sourceType", Arrays.asList("DEPOSIT", "INTEREST", "CREDIT"));

                List<HlsDurationLn> lnList = hlsDurationLnMapper.selectByExample(durationLnExample);

                //插入核销记录
                //获取抵扣金额大于0 的记录
                lnList = lnList.stream().filter(item ->
                        nvl(item.getDeductionAmount(), 0.0).compareTo(0.0) == 1).collect(Collectors.toList());

                for (HlsDurationLn ln : lnList) {
                    HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                    if (ln.getSourceType().equals("DEPOSIT") || ln.getSourceType().equals("INTEREST")) {
                        cshWriteOff.setCshWriteOffAmount(ln.getDeductionAmount());
                        cshWriteOff.setWriteOffDueAmount(ln.getDeductionAmount());
                        cshWriteOff.setWriteOffPrincipal(ln.getDeductionPrincipal());
                        cshWriteOff.setWriteOffInterest(ln.getDeductionInterest());
                        cshWriteOff.setCompanyId(requestCtx.getCompanyId());
                        cshWriteOff = setCshWriteOff(ln, cshWriteOff, hlsCusCshTransaction.getTransactionId(), "PAYMENT_DEBT");
                        cshWriteOff = cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
                        //更新现金流

                        updateCashflow(requestCtx, cshWriteOff);

                    }else if (ln.getSourceType().equals("CREDIT")) {

                        cshWriteOff.setCshWriteOffAmount(ln.getDeductionAmount());
                        cshWriteOff.setWriteOffDueAmount(ln.getDeductionAmount());
                        cshWriteOff.setWriteOffPrincipal(ln.getDeductionPrincipal());
                        cshWriteOff.setWriteOffInterest(ln.getDeductionInterest());
                        cshWriteOff.setCompanyId(requestCtx.getCompanyId());
                        cshWriteOff = setCshWriteOff(ln, cshWriteOff, hlsCusCshTransaction.getTransactionId(), "RECEIPT_CREDIT");

                        cshWriteOff = cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
                        //更新现金流
                        updateCashflow(requestCtx, cshWriteOff);
                    }


                }

                hlsDurationDepositList.get(0).setExecuteStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED);
                hlsDurationDepositService.updateByPrimaryKeySelective(requestCtx, hlsDurationDepositList.get(0));


                HlsDurationHd hd = new HlsDurationHd();
                hd.setHdId(hdId);
                hd.setDurationInstanceId(processInstanceId);
              /*  hd.setExecuteStatus(flag);
                hd.setExecuteStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED);*/
                hd.setDurationStatus(flag);
                hlsDurationHdService.updateByPrimaryKeySelective(requestCtx, hd);
            }


        } else if (REJECTED.equalsIgnoreCase(result)) {
            flag = "REJECTED";
            HlsDurationHd hd = new HlsDurationHd();
            hd.setHdId(hdId);
/*
            hd.setExecuteStatus(flag);
*/
            hd.setDurationStatus(flag);
            hlsDurationHdService.updateByPrimaryKeySelective(requestCtx, hd);
        }

    }
}
