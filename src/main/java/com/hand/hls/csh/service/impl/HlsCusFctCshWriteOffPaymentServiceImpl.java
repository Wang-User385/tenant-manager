package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cap.mapper.HlsCusCapitalInvestmentPlanLnMapper;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.components.DocumentChangeCommon;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.*;
import com.hand.hls.fct.dto.HlsCusFctContract;
import com.hand.hls.fct.dto.HlsCusFctProject;
import com.hand.hls.fct.mapper.HlsCusFctContractMapper;
import com.hand.hls.fct.service.HlsCusFctProjectService;
import com.hand.hls.fnd.dto.HLSCurrency;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.HLSCurrencyMapper;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.mapper.HlsCusFctQuotationCashflowMapper;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.event.utils.SysEventCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctCshWriteOffPaymentServiceImpl implements CshTransactionCommon {

    @Autowired
    private CshTransactionService cshTransactionService;
    @Autowired
    private CshWriteOffService cshWriteOffService;
    @Autowired
    private HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;
    @Autowired
    private HlsCusCshPaymentReqDtMapper cshPaymentReqDtMapper;
    @Autowired
    CshBankAccountBalanceService bankAccountBalanceService;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private IConContractCashflowService contractCashflowService;
    @Autowired
    private HlsCusConContractCashflowMapper contractCashflowMapper;
    @Autowired
    private HlsCusFctQuotationCashflowService quotationCashflowService;
    @Autowired
    private HlsCusFctQuotationCashflowMapper quotationCashflowMapper;
    @Autowired
    private HlsCfItemMapper hlsCfItemMapper;
    @Autowired
    private HlsCusFctContractMapper fctContractMapper;
    @Autowired
    private HlsCusFctProjectService hlsCusFctProjectService;
    @Autowired
    private HLSCurrencyMapper hlsCurrencyMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusCapitalInvestmentPlanLnMapper capitalInvestmentPlanLnMapper;


    Map<String, String> params = new HashMap<String, String>();

    private static final String writeOffType = "CT_FCT_PAYMENT_DEBT";
    public static Double transactionDueAmount = 0d;
    public static List<Long> list = new ArrayList<Long>();
    static HlsCusCshTransaction temp_cshTransaction = null;
    public static Double amount_paid = 0D;
    public Map<Long, Double> amountMap;

    Calendar calendar = Calendar.getInstance();

    private int currentYear = calendar.get(Calendar.YEAR);

    private int currentMonth = calendar.get(Calendar.MONTH) + 1;

    @Override
    public String getWriteOffType() {
        return writeOffType;
    }

    /**
     * 生成编码
     *
     * @param iRequest
     * @param
     * @return
     */
    public String getCodeValue(IRequest iRequest, HlsCusCshTransaction temp_cshTransaction) {
        Map<String, String> params = new HashMap<String, String>();
        String value = codingRuleValuesService.getCodeRuleValue(iRequest, temp_cshTransaction.getTransactionCategory(), temp_cshTransaction.getTransactionType(), temp_cshTransaction.getBusinessType(), params);
        return value;
    }
    @Override
    public void process(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        Long company_id = CshWriteOffServiceImpl.company_id;
        amountMap = CshWriteOffServiceImpl.amountMap;
        Map<String, Object> params = new HashMap<>();
        Long cashflowId = cshWriteOff.getCashflowId();
        HlsCusFctQuotationCashflow fctQuotationCashflow = new HlsCusFctQuotationCashflow();
        fctQuotationCashflow.setQuotationCashflowId(cashflowId);
        fctQuotationCashflow = quotationCashflowMapper.selectByPrimaryKey(fctQuotationCashflow);

        Double amount = fctQuotationCashflow.getReceivedAmount();
        if (amount == null) {
            amount = 0D;
        }
        Long contract_id = fctQuotationCashflow.getContractId();
        HlsCusFctContract fctContract = new HlsCusFctContract();
        fctContract.setContractId(contract_id);

        fctContract = fctContractMapper.selectByPrimaryKey(fctContract);
        String currency = fctContract.getCurrency();
        HLSCurrency hlsCurrency = new HLSCurrency();
        if(currency==null){
            throw new BeyondAmountLimitException("合同币种为空,请检查!");
        }
        hlsCurrency.setCurrencyCode(currency);
        hlsCurrency = hlsCurrencyMapper.selectOne(hlsCurrency);

        HlsCashflowItem hlsCashflowItem = new HlsCashflowItem();
        hlsCashflowItem.setCfType(fctQuotationCashflow.getCfType().toString());
        hlsCashflowItem.setCfItem(fctQuotationCashflow.getCfItem().toString());
        hlsCashflowItem = hlsCfItemMapper.selectOne(hlsCashflowItem);


        params.put("currency", hlsCurrency.getCurrencySymbol());

        Long paymentReqLineId = cshWriteOff.getPaymentReqLineId();
        // 支付金额+已支付金额 <= 总应付金额 才让加，否则超出总额了
        HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
        cshPaymentReqDt.setPaymentReqLnId(paymentReqLineId);
        List<HlsCusCshPaymentReqDt> select = cshPaymentReqDtMapper.select(cshPaymentReqDt);
        Double deductionAmount = 0D;
//		for (HlsCusCshPaymentReqDt item:select) {
//			deductionAmount = CalculateUtil.add(deductionAmount,item.getDeductAmount());
//		}

        if (cshWriteOff.getCshTransaction() != null) {
            transactionDueAmount = 0d;
            list = new ArrayList<Long>();
            temp_cshTransaction = null;
            amount_paid = 0D;
            temp_cshTransaction = cshWriteOff.getCshTransaction();
            if (company_id == null) {
                throw new IllegalArgumentException("公司ID获取失败!");
            } else {
                temp_cshTransaction.setCompanyId(company_id);
            }
            Double transactionAmount = temp_cshTransaction.getTransactionAmount();
            transactionAmount = CalculateUtil.sub(transactionAmount, deductionAmount);
            //当付款申请下有抵扣金额,切抵扣金额与申请金额相同时,直接返回
            temp_cshTransaction.setTransactionAmount(cshWriteOff.getCshWriteOffAmount());
            temp_cshTransaction.setWriteOffAmount(cshWriteOff.getCshWriteOffAmount());
            temp_cshTransaction.setTransactionNum(getCodeValue(iRequest, temp_cshTransaction));
            temp_cshTransaction.setWriteOffFlag("FULL");
            cshTransactionService.insertSelective(iRequest, temp_cshTransaction);
            if (deductionAmount.compareTo(0D) != 0 && transactionAmount.compareTo(0D) == 0) {
                return;
            }
            //现金付款
            // 事件修改开始 by 12614
            DecimalFormat df =new DecimalFormat("###,##0.00");
            params.put("amount", df.format(cshWriteOff.getCshWriteOffAmount()));
            params.put("level", 1L);
            params.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_TRANSACTION_PAYMENT);
            sysEventService.createEvent(iRequest, temp_cshTransaction.getBpId(), "CSH_TRANSACTION", "", params);
            // 事件修改结束，注释下方原始的事件注册
//			eventService.CshTransactionEvent(iRequest, "CSH_TRANSACTION.PAYMENT", temp_cshTransaction.getTransactionId());
        }
        if (temp_cshTransaction.getTransactionId() != null) {
            cshWriteOff.setCshTransactionId(temp_cshTransaction.getTransactionId());
        }
        cshWriteOff.setWriteOffDocCategory(DocumentChangeCommon.getTableName(HlsCusFctContract.class));
        cshWriteOffService.insertSelective(iRequest, cshWriteOff);


        //付款核销债务
        params.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_PAYMENT_DEBT);
        params.put("cfName", hlsCashflowItem.getDescription());
        //FIXME bpName参数不确定
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(temp_cshTransaction.getBpId());
        hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        params.put("bpName", hlsCusBpMaster.getBpName());
        /*why*/
        // sysEventService.createEvent(iRequest, contract_id, "FCT_CONTRACT", "", params);

//		cshWritiOffEventService.cshWritiOffEvent(iRequest, "CSH_WRITE_OFF.PAYMENT_DEBT", cshWriteOff.getWriteOffId());

        HlsCusCshPaymentReqLn cshPaymentReqLn = (HlsCusCshPaymentReqLn) cshPaymentReqLnService
                .selectCshPaymentReqLnDetailByLnID(Arrays.asList(paymentReqLineId.toString())).get(0);
        cshWriteOff.setPaymentReqId(cshPaymentReqLn.getPaymentReqId());
        Double cshWriteOffAmount = cshWriteOff.getCshWriteOffAmount();
        cshWriteOffAmount = CalculateUtil.sub(cshWriteOffAmount, deductionAmount);
        cshWriteOff.setCshWriteOffAmount(cshWriteOffAmount);
        cshWriteOff.setWriteOffDueAmount(cshWriteOffAmount);
        if (cshPaymentReqLn.getAmountPaid() == null) {
            cshPaymentReqLn.setAmountPaid(0d);
        }
        amount_paid = CalculateUtil.add(amount_paid, cshPaymentReqLn.getAmountPaid());
        // 回写申请行表已支付金额
        cshPaymentReqLn.setAmountPaid(CalculateUtil.add(cshPaymentReqLn.getAmountPaid(), cshWriteOffAmount));
        //cshPaymentReqLnService.updateAmountPaid(cshPaymentReqLn);
        // 如果正好支付+已支付的 等于本次申请的 ，那么将payment_flag置为FULL
        if (new BigDecimal(cshPaymentReqLn.getAmountPaid().toString()).compareTo(new BigDecimal(cshPaymentReqLn.getAmount().toString())) == 0) {
            cshPaymentReqLn.setPaymentFlag("FULL");
            cshPaymentReqLn.setPaymentCompletedDate(temp_cshTransaction.getTransactionDate());
            //cshPaymentReqLnService.updatePaymentFlag(cshPaymentReqLn);
        } else if(new BigDecimal(cshPaymentReqLn.getAmountPaid().toString()).compareTo(new BigDecimal(cshPaymentReqLn.getAmount().toString()))==-1 &&cshPaymentReqLn.getAmountPaid()>0){
            cshPaymentReqLn.setPaymentFlag("PARTIAL");
            //cshPaymentReqLnService.updatePaymentFlag(cshPaymentReqLn);
        }else{
           throw new BeyondAmountLimitException();
        }
        cshPaymentReqLnService.updateByPrimaryKeySelective(iRequest,cshPaymentReqLn);
        list.add(cshPaymentReqLn.getCashflowId());
        //根据不同的合同现金流ID，保存相应的支付金额
        if (amountMap.get(cshPaymentReqLn.getCashflowId()) == null) {
            amountMap.put(cshPaymentReqLn.getCashflowId(), cshPaymentReqLn.getAmount());
        } else {
            Double lnAmount = amountMap.get(cshPaymentReqLn.getCashflowId()) + cshPaymentReqLn.getAmount();
            amountMap.put(cshPaymentReqLn.getCashflowId(), lnAmount);
        }
        transactionDueAmount = CalculateUtil.add( transactionDueAmount , cshPaymentReqLn.getAmount());
        // amount_paid = amount_paid+(cshPaymentReqLn.getAmount() -
        // Double.parseDouble(cshPaymentReqLn.getUnpaid_amount()));
        HlsCusFctQuotationCashflow quotationCashflow = new HlsCusFctQuotationCashflow();
        quotationCashflow.setQuotationCashflowId(cshPaymentReqLn.getCashflowId());
        HlsCusFctQuotationCashflow cashflow = quotationCashflowService.selectByPrimaryKey(iRequest, quotationCashflow);

        if (cashflow.getReceivedAmount() == null) {
            cashflow.setReceivedAmount(0D);
        }
        Double sumAmount = CalculateUtil.add(cashflow.getReceivedAmount(), cshWriteOff.getCshWriteOffAmount());

        if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            cashflow.setWriteOffFlag("NOT");
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cashflow.getDueAmount().toString())) < 0 &&sumAmount>0) {
            cashflow.setWriteOffFlag("PARTIAL");
            cashflow.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cashflow.getDueAmount().toString())) == 0) {
            cashflow.setWriteOffFlag("FULL");
            cashflow.setWriteOffAmount(sumAmount);
            cashflow.setFullWriteOffDate(new Date());
        } else {
            throw new BeyondAmountLimitException();
        }
        cashflow.setReceivedAmount(sumAmount);
        cashflow.setWriteOffAmount(sumAmount);
        quotationCashflowService.updateByPrimaryKeySelective(iRequest, cashflow);
        /*更新项目表(虚拟合同)的已用授信额度*/
        HlsCusFctContract hlsCusFctContract = new HlsCusFctContract();
        hlsCusFctContract.setContractId(contract_id);
        hlsCusFctContract = fctContractMapper.selectByPrimaryKey(hlsCusFctContract);
        HlsCusFctProject hlsCusFctProject = new HlsCusFctProject();
        hlsCusFctProject.setProjectId(hlsCusFctContract.getProjectId());
        hlsCusFctProject = hlsCusFctProjectService.selectByPrimaryKey(iRequest, hlsCusFctProject);
        Double exposureAmount;
        if (hlsCusFctProject.getCreditExposureAmount() == null) {
            exposureAmount = 0D;
        } else {
            exposureAmount = hlsCusFctProject.getCreditExposureAmount();
        }
        hlsCusFctProject.setCreditExposureAmount(CalculateUtil.add( exposureAmount , sumAmount));
        hlsCusFctProject = hlsCusFctProjectService.updateByPrimaryKey(iRequest, hlsCusFctProject);


        //新增凭证事物
        Map writeOffMap = new HashMap<>();
        Map transactionMap = new HashMap<>();
        writeOffMap.put("jeTrxId", cshWriteOff.getWriteOffId());
        writeOffMap.put("companyId", company_id);
        writeOffMap.put("contractId", cshWriteOff.getContractId());
        writeOffMap.put("sourceDoc", "FCT_CONTRACT");
        transactionMap.put("jeTrxId", temp_cshTransaction.getTransactionId());
        transactionMap.put("companyId", company_id);
        transactionMap.put("contractId", cshWriteOff.getContractId());
        transactionMap.put("sourceDoc", "FCT_CONTRACT");
        AbstractJeTrxService writeOffJeTrxService = jeTrxCommonService.map.get("CSH_WRITE_OFF");//核销事物
        AbstractJeTrxService cshTransactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");//现金事物
        writeOffJeTrxService.process(iRequest, writeOffMap);
        cshTransactionJeTrxService.process(iRequest, transactionMap);

        //更新资金投放计划金额状态
        if(fctContract.getPlanLnId()!=null) {
            capitalInvestmentPlanLnMapper.updateInvestmentAmount(fctContract.getPlanLnId());
            capitalInvestmentPlanLnMapper.updateInvestmentAmountSum(null,fctContract.getPlanLnId());
        }
    }

    @Override
    public void reversed(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        // 1、核销表插入一条记录
        if (cshWriteOff.getWriteOffDueAmount() != null) {
            cshWriteOff.setCshWriteOffAmount(0 - cshWriteOff.getWriteOffDueAmount());
        }
        if (cshWriteOff.getWriteOffDueAmount() != null) {
            cshWriteOff.setWriteOffDueAmount(0 - cshWriteOff.getWriteOffDueAmount());
        }
        if (cshWriteOff.getWriteOffPrincipal() != null) {
            cshWriteOff.setWriteOffPrincipal(0 - cshWriteOff.getWriteOffPrincipal());
        }
        if (cshWriteOff.getWriteOffInterest() != null) {
            cshWriteOff.setWriteOffInterest(0 - cshWriteOff.getWriteOffInterest());
        }

        HlsCusCshWriteOff writeOff = getReceiptCredit(cshWriteOff);
        writeOff.setReversedFlag("R");
        writeOff.setReversedWriteOffId(cshWriteOff.getWriteOffId());
        writeOff.setReversedDate(cshWriteOff.getReversedDate());
        cshWriteOffService.insertSelective(iRequest, writeOff);
        // 2、更新现金事物表
        HlsCusCshTransaction cshTran = updateCshTrxAfterWriteoff(cshWriteOff);
        cshTransactionService.updateCshTrByPrimaryKey(cshTran);
        // 3、更新原核销表数据
        cshWriteOffMapper.updateCshWriteOffbyId(cshWriteOff.getWriteOffId(), "W", writeOff.getWriteOffId(),
                cshWriteOff.getReversedDate(), iRequest.getUserId(), new Date());
        // 4、插入一条现金事物表
        HlsCusCshTransaction cshTransaction = getCshTransaction(iRequest, cshWriteOff, "REFUND");
        cshTransaction.setTransactionAmount(cshWriteOff.getWriteOffDueAmount());
        cshTransaction.setReversedFlag("R");
        cshTransaction.setReversedTrxId(cshWriteOff.getSubsequentCshTrxId());
        cshTransaction.setReversedDate(cshWriteOff.getReversedDate());
        cshTransaction.setSourceDocLineId(writeOff.getWriteOffId());
        cshTransactionService.insertSelective(iRequest, cshTransaction);
        // 5、更新后续现金事物表
        HlsCusCshTransaction tcx = new HlsCusCshTransaction();
        tcx.setTransactionId(cshWriteOff.getSubsequentCshTrxId());
        tcx.setReversedFlag("W");
        tcx.setReversedTrxId(cshTransaction.getTransactionId());
        tcx.setReversedDate(cshTransaction.getReversedDate());
        cshTransactionService.updateCshTrByPrimaryKey(tcx);
        // 6、更新核销插入核销数据的关联事物Id
        cshWriteOffMapper.updateSubCshTrxId(writeOff.getWriteOffId(), cshTransaction.getTransactionId(),
                cshTransaction.getTransactionAmount());
        // 更新账户余额表
        //更新现金流
        HlsCusFctQuotationCashflow quotationCashflow = new HlsCusFctQuotationCashflow();
        quotationCashflow.setQuotationCashflowId(cshWriteOff.getCashflowId());
        HlsCusFctQuotationCashflow cashflow = quotationCashflowService.selectByPrimaryKey(iRequest, quotationCashflow);

        if (cashflow.getReceivedAmount() == null) {
            cashflow.setReceivedAmount(0D);
        }
        Double sumAmount = CalculateUtil.add(cashflow.getReceivedAmount(), cshWriteOff.getCshWriteOffAmount());

        if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            cashflow.setWriteOffFlag("NOT");
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cashflow.getDueAmount().toString())) < 0 &&sumAmount>0) {
            cashflow.setWriteOffFlag("PARTIAL");
            cashflow.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cashflow.getDueAmount().toString())) == 0) {
            cashflow.setWriteOffFlag("FULL");
            cashflow.setWriteOffAmount(sumAmount);
            cashflow.setFullWriteOffDate(new Date());
        } else {
            throw new BeyondAmountLimitException();
        }
        cashflow.setReceivedAmount(sumAmount);
        cashflow.setWriteOffAmount(sumAmount);
        quotationCashflowService.updateByPrimaryKeySelective(iRequest, cashflow);

        paymentBankAccountBalance(iRequest, cshTransaction);
    }

    private HlsCusCshWriteOff getReceiptCredit(HlsCusCshWriteOff cshWriteOff) {
        HlsCusCshWriteOff wo = new HlsCusCshWriteOff();

        wo.setWriteOffType(cshWriteOff.getWriteOffType());
        wo.setWriteOffDate(cshWriteOff.getWriteOffDate());
        wo.setCshTransactionId(cshWriteOff.getCshTransactionId());
        wo.setCshWriteOffAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setReversedFlag("N");
        wo.setDescription(cshWriteOff.getDescription());
        wo.setCashflowId(cshWriteOff.getCashflowId());
        wo.setContractId(cshWriteOff.getContractId());
        wo.setTimes(cshWriteOff.getTimes());
        wo.setCfItem(cshWriteOff.getCfItem());
        wo.setCfType(cshWriteOff.getCfType());
        wo.setWriteOffDueAmount(cshWriteOff.getWriteOffDueAmount());
        wo.setWriteOffPrincipal(cshWriteOff.getWriteOffPrincipal());
        wo.setWriteOffInterest(cshWriteOff.getWriteOffInterest());

        return wo;
    }

    private void paymentBankAccountBalance(IRequest iRequest, HlsCusCshTransaction cshTransaction) {
        Calendar c = Calendar.getInstance();
        c.setTime(cshTransaction.getTransactionDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        HlsCusCshBankAccountBalance accountBalance = null;

        if (year == currentYear && month == currentMonth) {
            HlsCusCshBankAccountBalance bankAccountBalance = bankAccountBalanceService.selectAccountBalance(
                    cshTransaction.getBankAccountId(), Long.valueOf(currentYear), Long.valueOf(currentMonth));

            if (bankAccountBalance == null) {
                bankAccountBalance = new HlsCusCshBankAccountBalance();

                bankAccountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                bankAccountBalance.setPeriodYear(Long.valueOf(year));
                bankAccountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance = bankAccountBalanceService.getCloseBankAccountBalance(bankAccountBalance);
                if (cshTransaction.getTransactionAmount() == null) {
                    cshTransaction.setTransactionAmount(0d);
                }
                if (accountBalance == null) {
                    bankAccountBalance.setOpeningBalance(0d);
                    bankAccountBalance.setBalance(0 - cshTransaction.getTransactionAmount());
                } else {
                    bankAccountBalance.setOpeningBalance(accountBalance.getBalance());
                    bankAccountBalance.setBalance(accountBalance.getBalance() - cshTransaction.getTransactionAmount());
                }
                bankAccountBalance.setPeriodIncome(0d);
                bankAccountBalance.setPeriodExpense(cshTransaction.getTransactionAmount());
                bankAccountBalanceService.insertSelective(iRequest, bankAccountBalance);

            } else {
                accountBalance = new HlsCusCshBankAccountBalance();
                accountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                accountBalance.setPeriodYear(Long.valueOf(year));
                accountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance.setPeriodIncome(0d);
                accountBalance.setPeriodExpense(cshTransaction.getTransactionAmount());
                bankAccountBalanceService.updateByPrimaryKeySelective(iRequest, accountBalance);
            }

        } else {
            HlsCusCshBankAccountBalance bankAccountBalance = bankAccountBalanceService
                    .selectAccountBalance(cshTransaction.getBankAccountId(), Long.valueOf(year), Long.valueOf(month));
            if (bankAccountBalance == null) {
                bankAccountBalance = new HlsCusCshBankAccountBalance();

                bankAccountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                bankAccountBalance.setPeriodYear(Long.valueOf(year));
                bankAccountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance = bankAccountBalanceService.getCloseBankAccountBalance(bankAccountBalance);
                if (cshTransaction.getTransactionAmount() == null) {
                    cshTransaction.setTransactionAmount(0d);
                }
                if (accountBalance == null) {
                    bankAccountBalance.setOpeningBalance(0d);
                    bankAccountBalance.setBalance(0 - cshTransaction.getTransactionAmount());
                } else {
                    bankAccountBalance.setOpeningBalance(accountBalance.getBalance());
                    bankAccountBalance.setBalance(accountBalance.getBalance() - cshTransaction.getTransactionAmount());
                }
                bankAccountBalance.setPeriodIncome(0d);
                bankAccountBalance.setPeriodExpense(cshTransaction.getTransactionAmount());
                bankAccountBalanceService.insertSelective(iRequest, bankAccountBalance);

            } else {
                accountBalance = new HlsCusCshBankAccountBalance();
                accountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                accountBalance.setPeriodYear(Long.valueOf(year));
                accountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance.setPeriodIncome(cshTransaction.getTransactionAmount());
                accountBalance.setPeriodExpense(0d);
                bankAccountBalanceService.updateByPrimaryKeySelective(iRequest, accountBalance);

            }

            accountBalance = new HlsCusCshBankAccountBalance();
            accountBalance.setBankAccountId(cshTransaction.getBankAccountId());
            accountBalance.setPeriodYear(Long.valueOf(year));
            accountBalance.setPeriodMonth(Long.valueOf(month));
            accountBalance.setPeriodIncome(0d);
            accountBalance.setPeriodExpense(cshTransaction.getTransactionAmount());
            bankAccountBalanceService.updateByPrimaryKeySelective(iRequest, accountBalance);
        }

    }

    private HlsCusCshTransaction getCshTransaction(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, String writeOffType) {
        List<HlsCusCshTransaction> cshTransactions = cshTransactionService.queryDetailByIdList(cshWriteOff.getCshTransactionId());

        HlsCusCshTransaction cshTransaction = cshTransactions.get(0);

        cshTransaction.setTransactionId(null);
        cshTransaction.setTransactionCategory("CSH_TRANSACTION");
        if ("RECEIPT_ADVANCE_RECEIPT".equals(writeOffType)) {
            cshTransaction.setTransactionType("ADVANCE_RECEIPT");
            cshTransaction.setBusinessType("ADVANCE_RECEIPT");
        } else if ("RECEIPT_DEPOSIT".equals(writeOffType)) {

            cshTransaction.setTransactionType("DEPOSIT");
            cshTransaction.setBusinessType("DEPOSIT");
        } else if ("RECEIPT_DEPOSIT_POOL".equals(writeOffType)) {
            cshTransaction.setTransactionType("DEPOSIT_POOL");
            cshTransaction.setBusinessType("DEPOSIT");
        } else if ("REFUND".equals(writeOffType)) {
            cshTransaction.setTransactionType("REFUND");
            cshTransaction.setBusinessType("PAYMENT");
        }
        if (cshWriteOff.getContractId() != null) {
            cshTransaction.setContractId(cshWriteOff.getContractId());
        }
        cshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(iRequest, cshTransaction.getTransactionCategory(), cshTransaction.getTransactionType(), cshTransaction.getBusinessType(), params));
        cshTransaction.setTransactionAmount(cshWriteOff.getWriteOffDueAmount());
        cshTransaction.setReversedFlag("N");
        cshTransaction.setReversedTrxId(null);
        cshTransaction.setReversedDate(null);
        cshTransaction.setPostedFlag("N");
        cshTransaction.setHandlingCharge(null);
        cshTransaction.setDescription(cshWriteOff.getDescription());
        cshTransaction.setWriteOffFlag("NOT");
        cshTransaction.setWriteOffAmount(null);
        cshTransaction.setFullWriteOffDate(null);
        cshTransaction.setSourceDocCategory("CSH_WRITE_OFF");

        return cshTransaction;
    }

    private HlsCusCshTransaction updateCshTrxAfterWriteoff(HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        HlsCusCshTransaction ct = new HlsCusCshTransaction();

        List<HlsCusCshTransaction> cshTransactions = cshTransactionService.queryDetailByIdList(cshWriteOff.getCshTransactionId());

        HlsCusCshTransaction cshTransaction = cshTransactions.get(0);

        if (cshTransaction.getWriteOffAmount() == null) {
            cshTransaction.setWriteOffAmount(0d);
        }
        if (cshWriteOff.getWriteOffDueAmount() == null) {
            cshWriteOff.setWriteOffDueAmount(0d);
        }

        Double sumAmount =CalculateUtil.add(  cshTransaction.getWriteOffAmount() , cshWriteOff.getWriteOffDueAmount());

        if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            ct.setWriteOffFlag("NOT");
            ct.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cshTransaction.getTransactionAmount().toString())) < 0 && sumAmount>0) {
            ct.setWriteOffFlag("PARTIAL");
            ct.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cshTransaction.getTransactionAmount().toString())) == 0) {
            ct.setWriteOffFlag("FULL");
            ct.setWriteOffAmount(sumAmount);
            ct.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        } else {
            throw new BeyondAmountLimitException();
        }

        ct.setTransactionId(cshWriteOff.getCshTransactionId());

        return ct;
    }
}
