package com.hand.hls.hls.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshTransactionDtl;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.service.CshTransactionService;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.csh.service.HlsCusCshTransactionDtlService;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.hls.dto.HlsDurationDepositAccount;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationDepositAccountMapper;
import com.hand.hls.hls.mapper.HlsDurationDepositMapper;
import com.hand.hls.hls.mapper.HlsDurationHdMapper;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.hls.service.HlsDurationLnService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.service.HlsDurationDepositService;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationDepositServiceImpl extends BaseServiceImpl<HlsDurationDeposit> implements HlsDurationDepositService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private HlsDurationHdMapper hlsDurationHdMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private IActivitiStartService activitiStartService;

    @Autowired
    private HlsDurationDepositService hlsDurationDepositService;
    @Autowired
    private HlsDurationDepositMapper hlsDurationDepositMapper;
    @Autowired
    private HlsDurationHdService hlsDurationHdService;
    @Autowired
    private HlsDurationLnService hlsDurationLnService;
    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;
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
    @Autowired
    private HlsDurationDepositAccountMapper hlsDurationDepositAccountMapper;
    @Autowired
    private HlsCusCshTransactionDtlService cshTransactionDtlService;
    @Autowired
    private JeTrxCommonService commonService;

    private static final String JE_CON_CONTRACT = "CON_CONTRACT";

    @Override
    public List<HlsDurationDeposit> depositManagementQuery(HlsDurationDeposit hlsDurationDeposit, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsDurationDepositMapper.depositManagementQuery(hlsDurationDeposit);
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

    private void deduction(IRequest requestCtx, HlsDurationDeposit deposit, List<HlsDurationLn> lnList) throws ParseException, ResMessageException {
        //插入deduction 现金事务
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionType("DEDUCTION");
        hlsCusCshTransaction.setBusinessType("DEPOSIT");
        hlsCusCshTransaction.setTransactionAmount(0.0);
        hlsCusCshTransaction.setCurrencyCode(deposit.getCurrencyCode());
        hlsCusCshTransaction = setCshTransaction(requestCtx, deposit, hlsCusCshTransaction);
        cshTransactionService.insertSelective(requestCtx, hlsCusCshTransaction);

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

            } else if (ln.getSourceType().equals("CREDIT")) {

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

            //抵扣凭证 , csh_write_off
            AbstractJeTrxService paymentEqimentJeTrx = commonService.map.get("CSH_DEDUCTION");
            Map paramDeducts = new HashMap<>();
            paramDeducts.put("jeTrxId", cshWriteOff.getWriteOffId());
            paramDeducts.put("companyId", requestCtx.getCompanyId());
            paramDeducts.put("contractId", cshWriteOff.getContractId());
            paramDeducts.put("jeSourceDoc", JE_CON_CONTRACT);
            paramDeducts.put("jeSourceId", cshWriteOff.getContractId());
            paymentEqimentJeTrx.process(requestCtx, paramDeducts);

        }
    }


    private void refund(IRequest requestCtx, HlsDurationDeposit deposit, List<HlsDurationLn> lnList) throws ParseException, ResMessageException {
        //不再使用 4.2 现金事务的方式 处理退款 ，而是通过 52 保证金退款，502 保证金利息退款 现金流 操作
        //插入现金事务
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionType("REFUND");
        hlsCusCshTransaction.setBusinessType("DEPOSIT");
        lnList = lnList.stream().filter(item ->
                nvl(item.getRefundAmount(), 0.0).compareTo(0.0) == 1).collect(Collectors.toList());

        Double transactionAmount = round(lnList.stream().collect(Collectors.summingDouble(HlsDurationLn::getRefundAmount)), 2);

        //插入现金事务
        hlsCusCshTransaction.setTransactionAmount(transactionAmount);
        hlsCusCshTransaction.setCurrencyCode(deposit.getCurrencyCode());
        hlsCusCshTransaction = setCshTransaction(requestCtx, deposit, hlsCusCshTransaction);
        cshTransactionService.insertSelective(requestCtx, hlsCusCshTransaction);

        //插入现金事务明细表
        HlsDurationDepositAccount hlsDurationDepositAccount = new HlsDurationDepositAccount();
        hlsDurationDepositAccount.setDepositId(deposit.getSourceId());
        List<HlsDurationDepositAccount> depositAccountList = hlsDurationDepositAccountMapper.select(hlsDurationDepositAccount);

        for (HlsDurationDepositAccount depositAccount : depositAccountList) {
            HlsCusCshTransactionDtl dtl = new HlsCusCshTransactionDtl();
            dtl.setTransactionId(hlsCusCshTransaction.getTransactionId());
            dtl.setTransactionType("REFUND");
            dtl.setBusinessType("DEPOSIT");
            dtl.setContractId(deposit.getContractId());
            dtl.setTransactionAmount(depositAccount.getRefundAmount());
            dtl.setCurrencyCode(deposit.getCurrencyCode());
            dtl.setTransactionCategory("CSH_TRANSACTION");
            dtl.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, dtl.getTransactionCategory(),
                    "PAYMENT", "PAYMENT", null));
            dtl.setTransactionDate(df.parse(df.format(new Date())));
            dtl.setPenaltyCalcDate(df.parse(df.format(new Date())));
            dtl.setCompanyId(requestCtx.getCompanyId());
            dtl.setReversedFlag("N");
            dtl.setPostedFlag("N");
            dtl.setWriteOffFlag("NOT");
            dtl.setBankAccountId(depositAccount.getBankAccountId());
            dtl.setBpBankAccountId(deposit.getBpBankAccountId());
            dtl.setPaymentMethod(depositAccount.getPaymentMethod());
            dtl.setBankSlipNum(depositAccount.getBankSlipNum());
            dtl = cshTransactionDtlService.insertSelective(requestCtx, dtl);

            //生成凭证
            Map map = new HashMap<>();
            map.put("jeTrxId", dtl.getTransactionDtlId());
            map.put("companyId", requestCtx.getCompanyId());
            map.put("contractId", dtl.getContractId());
            map.put("jeSourceDoc", JE_CON_CONTRACT);
            map.put("jeSourceId", dtl.getContractId());
            AbstractJeTrxService transactionPaymentJeTrx = commonService.map.get("CSH_TRANSACTION_PAYMENT");
            transactionPaymentJeTrx.process(requestCtx, map);
        }

        //插入核销事务
        for (HlsDurationLn ln : lnList) {
            HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
            if (ln.getSourceType().equals("DEPOSIT") || ln.getSourceType().equals("INTEREST")) {
                cshWriteOff.setCshWriteOffAmount(ln.getRefundAmount());
                cshWriteOff.setWriteOffDueAmount(ln.getRefundAmount());
                cshWriteOff = setCshWriteOff(ln, cshWriteOff, hlsCusCshTransaction.getTransactionId(), "REFUND");
                cshWriteOff = cshWriteOffService.insertSelective(requestCtx, cshWriteOff);
                //生成凭证
                Map params = new HashMap<>();
                params.put("jeTrxId", cshWriteOff.getWriteOffId());
                params.put("companyId", requestCtx.getCompanyId());
                params.put("contractId", cshWriteOff.getContractId());
                params.put("jeSourceDoc", JE_CON_CONTRACT);
                params.put("jeSourceId", cshWriteOff.getContractId());
                AbstractJeTrxService paymentEqimentJeTrx = commonService.map.get("PAY_EQIPMENT");
                paymentEqimentJeTrx.process(requestCtx, params);

                //更新现金流
                updateCashflow(requestCtx, cshWriteOff);
            }
        }
    }

    @Override
    public List<HlsDurationDeposit> executeDeposit(IRequest iRequest, List<HlsDurationDeposit> depositList) {

        for (HlsDurationDeposit deposit : depositList) {
            Example durationLnExample = new Example(HlsDurationLn.class);
            durationLnExample.createCriteria().
                    andEqualTo("sourceId", deposit.getSourceId()).
                    andEqualTo("hdId", deposit.getHdId()).
                    andIn("sourceType", Arrays.asList("DEPOSIT", "INTEREST", "CREDIT"));

            List<HlsDurationLn> lnList = hlsDurationLnMapper.selectByExample(durationLnExample);
            databaseLockProvider.lock(deposit);

            if (HlsCusConstant.FLAG.Y.equals(deposit.getIsDeductionFlag())) {
                try {
                    deduction(iRequest, deposit, lnList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
           /* if (HlsCusConstant.FLAG.Y.equals(deposit.getIsRefundFlag())) {
                try {
                    refund(iRequest, deposit, lnList);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }*/


            deposit.setExecuteStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            hlsDurationDepositService.updateByPrimaryKeySelective(iRequest, deposit);


            HlsDurationHd hd = new HlsDurationHd();
            hd.setHdId(deposit.getHdId());
            hd.setExecuteStatus(HlsCusConstant.WORKFLOW_STATUS.APPROVED);
            hlsDurationHdService.updateByPrimaryKeySelective(iRequest, hd);
        }

        return depositList;
    }


    /**
     * @Title: submitDepositWfl
     * @Discription: 保证金执行 不再走工作流 方法废弃
     * @Param: [iRequest, depositList]
     * @Return: java.util.List<com.hand.hls.hls.dto.HlsDurationDeposit>
     */
    @Deprecated
    @Override
    public List<HlsDurationDeposit> submitDepositWfl(IRequest iRequest, List<HlsDurationDeposit> depositList) throws ResMessageException {

        HlsDurationDeposit deposit = hlsDurationDepositMapper.selectByPrimaryKey(depositList.get(0).getSourceId());

        HlsDurationHd hd = hlsDurationHdMapper.selectByPrimaryKey(deposit.getHdId());

        //锁表
        databaseLockProvider.lock(deposit);

        /*//获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (Objects.isNull(employee)) {
            throw new ResMessageException("获取提交人失败");
        }

        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);*/
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "DEPOSIT_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "DEPOSIT_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "DEPOSIT_WFL");
        params.put(IActivitiCommonService.BUSINESS_KEY, deposit.getSourceId());
        params.put("documentCategory", "DEPOSIT");
        params.put("documentName", "保证金执行" + hd.getDurationNumber());
        params.put("documentNumber", hd.getDurationNumber());
        params.put("hdId", deposit.getHdId());
        params.put("sourceId", deposit.getSourceId());
        params.put("contractId", deposit.getContractId());
        params.put("durationType", "DEPOSIT");
        params.put("startUserName", iRequest.getUserName());
        params.put("companyId", iRequest.getCompanyId());
        activitiStartService.start(iRequest, depositList, params);
        //修改变更状态
        deposit.setExecuteStatus("APPROVING");
        self().updateByPrimaryKeySelective(iRequest, deposit);
        return depositList;
    }

}