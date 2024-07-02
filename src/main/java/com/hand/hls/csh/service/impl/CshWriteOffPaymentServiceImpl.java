package com.hand.hls.csh.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cap.mapper.HlsCusCapitalInvestmentPlanLnMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.components.DocumentChangeCommon;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqDtMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.csh.service.*;
import com.hand.hls.fnd.dto.HLSCurrency;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.HLSCurrencyMapper;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import hls.core.sys.event.service.SysEventService;
import hls.core.sys.event.utils.SysEventCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class CshWriteOffPaymentServiceImpl implements CshTransactionCommon {

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
    private CshBankAccountBalanceService bankAccountBalanceService;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private IConContractCashflowService contractCashflowService;
    @Autowired
    private HlsCusConContractCashflowMapper contractCashflowMapper;
    @Autowired
    private HlsCfItemMapper hlsCfItemMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HLSCurrencyMapper hlsCurrencyMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusConContractService conContractService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusCapitalInvestmentPlanLnMapper capitalInvestmentPlanLnMapper;

    Map<String, String> params = new HashMap<String, String>();

    private static final String writeOffType = "PAYMENT_DEBT";
    public static Double transactionDueAmount = 0d;
    public static Double amountPaid = 0d;
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
        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
        conContractCashflow.setCashflowId(cashflowId);
        conContractCashflow = contractCashflowMapper.selectByPrimaryKey(conContractCashflow);

        Double amount = conContractCashflow.getReceivedAmount();
        Long contract_id = conContractCashflow.getContractId();
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(contract_id);

        conContract = conContractMapper.selectByPrimaryKey(conContract);
        String currency = conContract.getCurrency();
        HLSCurrency hlsCurrency = new HLSCurrency();
        if (currency == null) {
            throw new BeyondAmountLimitException("合同币种为空,请检查!");
        }
        hlsCurrency.setCurrencyCode(currency);
        hlsCurrency = hlsCurrencyMapper.selectOne(hlsCurrency);

        HlsCashflowItem hlsCashflowItem = new HlsCashflowItem();
        hlsCashflowItem.setCfType(conContractCashflow.getCfType().toString());
        hlsCashflowItem.setCfItem(conContractCashflow.getCfItem().toString());
        hlsCashflowItem = hlsCfItemMapper.selectOne(hlsCashflowItem);

        DecimalFormat df = new DecimalFormat("###,##0.00");
        params.put("amount", df.format(amount));
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
            temp_cshTransaction.setTransactionAmount(transactionAmount);
            temp_cshTransaction.setTransactionNum(getCodeValue(iRequest, temp_cshTransaction));
            temp_cshTransaction.setSourceDocLineId(paymentReqLineId);
            cshTransactionService.insertSelective(iRequest, temp_cshTransaction);
            if (deductionAmount.compareTo(0D) != 0 && transactionAmount.compareTo(0D) == 0) {
                return;
            }
            //现金付款
            // 事件修改开始 by 12614

            params.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_TRANSACTION_PAYMENT);
            params.put("level", 1L);
            sysEventService.createEvent(iRequest, temp_cshTransaction.getBpId(), "CSH_TRANSACTION", "", params);
            // 事件修改结束，注释下方原始的事件注册
//			eventService.CshTransactionEvent(iRequest, "CSH_TRANSACTION.PAYMENT", temp_cshTransaction.getTransactionId());
        }
        if (temp_cshTransaction.getTransactionId() != null) {
            cshWriteOff.setCshTransactionId(temp_cshTransaction.getTransactionId());
        }
        cshWriteOff.setWriteOffDocCategory(DocumentChangeCommon.getTableName(HlsCusConContract.class));
        cshWriteOffService.insertSelective(iRequest, cshWriteOff);
        //付款核销债务
        params.put("eventCode", SysEventCodeUtil.PROPERTY_CSH_WRITE_OFF_PAYMENT_DEBT);
        params.put("cfName", hlsCashflowItem.getDescription());
        //FIXME bpName参数不确定
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(temp_cshTransaction.getBpId());
        hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        params.put("bpName", hlsCusBpMaster.getBpName());
        //sysEventService.createEvent(iRequest,contract_id,"CON_CONTRACT","",params);

//		cshWritiOffEventService.cshWritiOffEvent(iRequest, "CSH_WRITE_OFF.PAYMENT_DEBT", cshWriteOff.getWriteOffId());

        HlsCusCshPaymentReqLn cshPaymentReqLn = (HlsCusCshPaymentReqLn) cshPaymentReqLnService
                .selectCshPaymentReqLnDetailByLnID(Arrays.asList(paymentReqLineId.toString())).get(0);
        cshWriteOff.setPaymentReqId(cshPaymentReqLn.getPaymentReqId());
        Double cshWriteOffAmount = cshWriteOff.getCshWriteOffAmount();
        cshWriteOffAmount = CalculateUtil.sub(cshWriteOffAmount, deductionAmount);
        cshWriteOff.setCshWriteOffAmount(cshWriteOffAmount);
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
            // cshPaymentReqLnService.updatePaymentFlag(cshPaymentReqLn);
        } else if ((new BigDecimal(cshPaymentReqLn.getAmountPaid().toString()).compareTo(new BigDecimal(cshPaymentReqLn.getAmount().toString()))) == -1 && cshPaymentReqLn.getAmountPaid() > 0) {
            cshPaymentReqLn.setPaymentFlag("PARTIAL");
            // cshPaymentReqLnService.updatePaymentFlag(cshPaymentReqLn);
        } else {
            throw new BeyondAmountLimitException();
        }
        cshPaymentReqLnService.updateByPrimaryKeySelective(iRequest, cshPaymentReqLn);
        list.add(conContractCashflow.getCashflowId());
        //根据不同的合同现金流ID，保存相应的支付金额
        if (amountMap.get(conContractCashflow.getCashflowId()) == null) {
            amountMap.put(conContractCashflow.getCashflowId(), cshPaymentReqLn.getAmount());
        } else {
            Double lnAmount = CalculateUtil.add(amountMap.get(conContractCashflow.getCashflowId()), cshPaymentReqLn.getAmount());
            amountMap.put(conContractCashflow.getCashflowId(), lnAmount);
        }
        transactionDueAmount = CalculateUtil.add(transactionDueAmount, cshPaymentReqLn.getAmount());
        // amount_paid = amount_paid+(cshPaymentReqLn.getAmount() -
        // Double.parseDouble(cshPaymentReqLn.getUnpaid_amount()));
//		HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
//		contractCashflow.setCashflowId(cshPaymentReqLn.getCashflow_id());
//		HlsCusConContractCashflow cashflow = contractCashflowService.selectByPrimaryKey(iRequest, contractCashflow);

        if (conContractCashflow.getReceivedAmount() == null) {
            conContractCashflow.setReceivedAmount(0d);
        }
        Double sumAmount = CalculateUtil.add(conContractCashflow.getReceivedAmount(), cshWriteOff.getCshWriteOffAmount());

        if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            conContractCashflow.setWriteOffFlag("NOT");
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(conContractCashflow.getDueAmount().toString())) < 0 && sumAmount > 0) {
            conContractCashflow.setWriteOffFlag("PARTIAL");
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(conContractCashflow.getDueAmount().toString())) == 0) {
            conContractCashflow.setWriteOffFlag("FULL");
            conContractCashflow.setFullWriteOffDate(cshPaymentReqLn.getPaymentReqDate());
            ;
        } else {
            throw new BeyondAmountLimitException();
        }
        conContractCashflow.setReceivedAmount(sumAmount);

        contractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);

        //新增凭证事物
        Map writeOffMap = new HashMap<>();
        Map transactionMap = new HashMap<>();
        writeOffMap.put("jeTrxId", cshWriteOff.getWriteOffId());
        writeOffMap.put("companyId", company_id);
        writeOffMap.put("contractId", cshWriteOff.getContractId());
        writeOffMap.put("sourceDoc", "CON_CONTRACT");
        transactionMap.put("jeTrxId", temp_cshTransaction.getTransactionId());
        transactionMap.put("companyId", company_id);
        transactionMap.put("contractId", cshWriteOff.getContractId());
        transactionMap.put("sourceDoc", "CON_CONTRACT");
        AbstractJeTrxService writeOffJeTrxService = jeTrxCommonService.map.get("CSH_WRITE_OFF");//核销事物
        AbstractJeTrxService cshTransactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");//现金事物
        writeOffJeTrxService.process(iRequest, writeOffMap);
        cshTransactionJeTrxService.process(iRequest, transactionMap);

        //更新资金投放计划金额状态
        if (conContract.getPlanLnId() != null) {
            capitalInvestmentPlanLnMapper.updateInvestmentAmount(conContract.getPlanLnId());
            capitalInvestmentPlanLnMapper.updateInvestmentAmountSum(null, conContract.getPlanLnId());
        }
    }

    private HlsCusConContractCashflow updateConCashflowAfterWriteOff(IRequest iRequest, HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        HlsCusConContractCashflow cf = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());

        if (cf.getCfItem() == 9) {
            //罚息现金流核销，核销金额累加为已收核销
            Double receivedAmount = cf.getReceivedAmount();
            if (null == receivedAmount) {
                receivedAmount = 0D;
            }
            receivedAmount = CalculateUtil.add(receivedAmount, cshWriteOff.getWriteOffDueAmount());
            cf.setReceivedAmount(receivedAmount);
        }


        if (cf.getReceivedAmount() == null) {
            cf.setReceivedAmount(0d);
        }

        if (cf.getReceivedPrincipal() == null) {
            cf.setReceivedPrincipal(cshWriteOff.getWriteOffPrincipal());
        } else {
            cf.setReceivedPrincipal(CalculateUtil.add(cf.getReceivedPrincipal(), cshWriteOff.getWriteOffPrincipal()));
        }

        if (cf.getReceivedInterest() == null) {
            cf.setReceivedInterest(0d);
        } else {
            cf.setReceivedInterest(CalculateUtil.add(cf.getReceivedInterest(), cshWriteOff.getWriteOffInterest()));
        }

        Double receivedAmount = CalculateUtil.add(cf.getReceivedAmount(), cshWriteOff.getWriteOffDueAmount());
        if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("NOT");
        } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(cf.getDueAmount().toString())) == -1 && receivedAmount > 0) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("PARTIAL");
        } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(cf.getDueAmount().toString())) == 0) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("FULL");
            cf.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        } else {
            throw new BeyondAmountLimitException();
        }

        cf.setLastReceivedDate(cshWriteOff.getWriteOffDate());

        //判断如果保证金完全核销，插入保证金退回的现金流
        HlsCusConContract cusConContract = new HlsCusConContract();
        cusConContract.setContractId(cf.getContractId());
        cusConContract = conContractService.selectByPrimaryKey(iRequest, cusConContract);

        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
        conContractCashflow.setContractId(cusConContract.getContractId());
        conContractCashflow.setTimes(cusConContract.getLeaseTimes());
        List<HlsCusConContractCashflow> list = hlsCusConContractCashflowService.select(iRequest, conContractCashflow, 1, 10000);

        if (51 == cf.getCfItem() && "FULL".equalsIgnoreCase(cf.getWriteOffFlag())) {
            HlsCusConContractCashflow conCash = new HlsCusConContractCashflow();
            Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(cf);
            hlsBeanRefUtilService.setFieldValue(conCash, mapCsh);
            conCash.setCashflowId(null);
            conCash.setWriteOffFlag("NOT");
            conCash.setCfDirection("OUTFLOW");
            conCash.setCfItem(52L);
            conCash.setCfType(52L);
            conCash.setTimes(cusConContract.getLeaseTimes());
            conCash.setReceivedAmount(0D);
            conCash.setOutstandingPrincipal(0D);
            conCash.setLastReceivedDate(null);
            conCash.setFullWriteOffDate(null);
            if (list.size() != 0) {
                conCash.setDueDate(list.get(0).getDueDate());
            }
            conCash = hlsCusConContractCashflowService.insertSelective(iRequest, conCash);


            //插入现金事物
            //cshTransactionService
            HlsCusCshTransaction cshDeposit = new HlsCusCshTransaction();
            cshDeposit.setTransactionCategory("CSH_TRANSACTION");
            cshDeposit.setBusinessType("DEPOSIT");
            cshDeposit.setTransactionType("DEPOSIT");
            Map<String, String> params = new HashMap<String, String>();
            cshDeposit.setTransactionNum(fndCodingRuleValuesService.getCodeRuleValue(iRequest, cshDeposit.getTransactionCategory(), cshDeposit.getTransactionType(), cshDeposit.getBusinessType(), params));
            cshDeposit.setTransactionDate(new Date());
            cshDeposit.setPenaltyCalcDate(new Date());
            cshDeposit.setCompanyId(iRequest.getCompanyId());
            cshDeposit.setTransactionAmount(conCash.getDueAmount());
            cshDeposit.setCurrencyCode("CNY");
            cshDeposit.setPaymentMethod("T/T");
            cshDeposit.setReversedFlag("N");
            cshDeposit.setContractId(conCash.getContractId());
            cshDeposit.setSourceDocId(conCash.getContractId());
            cshDeposit.setSourceDocLineId(conCash.getCashflowId());
            cshDeposit.setSourceDocCategory("CON_CONTRACT");
            cshDeposit.setWriteOffFlag("NOT");
            cshDeposit.setWriteOffAmount(0D);
            cshDeposit.setBpId(cusConContract.getTenantId());
            cshDeposit = cshTransactionService.insertSelective(iRequest, cshDeposit);

        }
        return cf;
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
        // 7、更新现金流表数据
        HlsCusConContractCashflow conCashflow = updateConCashflowAfterWriteOff(iRequest, cshWriteOff);
        cashflowMapper.updateOne(conCashflow);
        // 更新账户余额表

        //paymentBankAccountBalance(iRequest,cshTransaction);
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

        Double sumAmount = CalculateUtil.add(cshTransaction.getWriteOffAmount(), cshWriteOff.getWriteOffDueAmount());

        if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            ct.setWriteOffFlag("NOT");
            ct.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount.toString()).compareTo(new BigDecimal(cshTransaction.getTransactionAmount().toString())) < 0 && sumAmount > 0) {
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
