package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.service.impl.HlsCusAbsProductServiceImpl;
import com.hand.hls.app.utils.ArrayListUtils;
import com.hand.hls.ast.dto.NoticeManage;
import com.hand.hls.ast.service.INoticeManageService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.calc.exception.ChangeLimitException;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractArchiveService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.components.DocumentChangeCommon;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.exception.BeyondAmountLimitException;
import com.hand.hls.csh.exception.WriteOffTypeNullException;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.csh.service.*;
import com.hand.hls.fct.dto.HlsCusFctContractWithdrawCf;
import com.hand.hls.fct.mapper.HlsCusFctContractWithdrawCfMapper;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractRepaymentMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayInterfaceService;
import com.hand.hls.hls.dto.HlsCusHlsCreditLineTrx;
import com.hand.hls.hls.service.IHlsCreditLineTrxService;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import com.hand.hls.prj.dto.HlsBpMasterInceptRule;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsBpMasterInceptRuleMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationPaymentService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.service.IPrjProjectService;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.mapper.HlsSystemNoticeMapper;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.csh.dto.HlsCusPaymentDeduct;
import com.hand.hls.csh.service.HlsCusCshTransactionService;
import com.hand.hls.csh.service.HlsCusIWriteOffMatchService;
import com.hand.hls.csh.service.HlsCusPaymentDeductService;
import com.hand.hls.fct.dto.HlsCusFctQuotationCashflow;
import com.hand.hls.fct.service.HlsCusFctQuotationCashflowService;
import com.hand.hls.utils.service.HlsConstantUtil;
import com.hand.hls.wsdl.utils.SapConstants;
import com.mysql.jdbc.log.Log;
import hls.core.sys.event.service.SysEventService;
import com.hand.hls.exception.HlsCusException;
import jodd.util.ArraysUtil;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.hand.hls.utils.HlsCusMathUtil.add;
import static com.hand.hls.utils.HlsCusMathUtil.sub;
import static com.hand.hls.utils.HlsCusMathUtil.mul;
import static com.hand.hls.utils.HlsCusMathUtil.div;
import static com.hand.hls.utils.HlsCusMathUtil.round;
import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class CshWriteOffServiceImpl extends BaseServiceImpl<HlsCusCshWriteOff> implements AppContextInitListener, CshWriteOffService {

    public static final String WRITE_OFF_FLAG_FULL = "FULL";//完全核销标志
    public static final String WRITE_OFF_FLAG_NOT = "NOT";//未核销标志
    public static final String WRITE_OFF_FLAG_PARTIAL = "PARTIAL";//部分核销标志
    public static final String DEDUCTION = "DEDUCTION";
    public static final String REVERSE_FLAG_N = "N";
    public static final String POSTED_FLAG_N = "N";
    public static final String CSH_PAYMENT_REQ = "CSH_PAYMENT_REQ";
    public static final String CON_CONTRACT_CASHFLOW = "con_contract_cashflow";

    public static final String CON_CONTRACT = "CON_CONTRACT";

    public static final String WRITE_OFF_TYPE_PAYMENT_DEBT = "PAYMENT_DEBT";
    public static final String WRITE_OFF_TYPE_RECEIPT_CREDIT = "RECEIPT_CREDIT";
    public static final String WRITE_OFF_TYPE_RECEIPT_DEPOSIT = "RECEIPT_DEPOSIT";
    public static final String WRITE_OFF_TYPE_RECEIPT_ADVANCE_RECEIPT = "RECEIPT_ADVANCE_RECEIPT";
    public static final String WRITE_OFF_TYPE_ADVANCE_RECEIPT_CREDIT = "ADVANCE_RECEIPT_CREDIT";


    public static final String PAYMENT_FLAG_FULL = "FULL";
    public static final String PAYMENT_FLAG_NOT = "NOT";
    public static final String PAYMENT_FLAG_PARTIAL = "PARTIAL";
    public static final String DEPOSIT = "DEPOSIT";
    public static final String CREDIT = "CREDIT";
    public static final String TRANSACTION_TYPE_RECEIPT = "RECEIPT";
    public static final String TRANSACTION_TYPE_ADVANCE_RECEIPT = "ADVANCE_RECEIPT";
    public static final String CSH_TRANSACTION = "CSH_TRANSACTION";
    public static final String CSH_WRITE_OFF = "CSH_WRITE_OFF";
    private static final Long[] INSERT_CFITEMS = {1L, 11L, 13L, 9L, 65L};
    private static final int SCALE = 2;
    private static final Double _0D = 0D;
    private static final Long _1 = 1L;
    private static final Long _11 = 11L;
    private static final Long _13 = 13L;


    public static final String LOAN_INITIAL_Y = "Y";
    public static final String LOAN_INITIAL_N = "N";

    public static final String CSH_TRANSACTION_DTL = "CSH_TRANSACTION_DTL";

    /**
     * 二期功能：零售业务
     */
    public static final String BUSINESS_FLAG_RETAIL = "RETAIL";
    /**
     * 合同起租
     */
    public static final String CONTRACT_STATUS_INCEPT = "INCEPT";



    Map<String, String> params = new HashMap<String, String>();

    @Autowired
    HlsCusConContractMapper conContractMapper;

    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    HlsCusCshTransactionMapper cshTransactionMapper;
    @Autowired
    HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    CshTransactionService cshTransactionService;
    @Autowired
    HlsCusCshPaymentReqLnMapper cshPaymentReqLnMapper;
    @Autowired
    HlsCusCshBankAccountBalanceMapper bankAccountBalanceMapper;
    @Autowired
    CshBankAccountBalanceService cshBankAccountBalanceService;
    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private HlsCusFctQuotationCashflowService cashflowService;
    @Autowired
    private IConContractCashflowService iConContractCashflowService;
    @Autowired
    private HlsCusLonContractRepaymentMapper repaymentMapper;
    @Autowired
    private HlsCusLonContractMapper lonContractMapper;
    @Autowired
    private IHlsCreditLineTrxService hlsCreditLineTrxService;
    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private FndCodingRuleValuesService codingRuleValuesService;
    @Autowired
    private HlsCusIWriteOffMatchService hlsCusIWriteOffMatchService;
    @Autowired
    HlsCusCshTransactionService hlsCusCshTransactionService;
    @Autowired
    HlsCusPaymentDeductService hlsCusPaymentDeductService;
    @Autowired
    private IConContractCashflowService contractCashflowService;
    @Autowired
    private HlsCusCshPaymentReqDtMapper cshPaymentReqDtMapper;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;
    @Autowired
    private IMainCshWriteOffService mainCshWriteOffService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    HlsCusPrjProjectMapper prjProjectMapper;

    @Autowired
    HlsBpMasterInceptRuleMapper bpMasterInceptRuleMapper;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private IConContractArchiveService contractArchiveService;

    Calendar calendar = Calendar.getInstance();

    private int currentYear = calendar.get(Calendar.YEAR);

    private int currentMonth = calendar.get(Calendar.MONTH) + 1;

    private Map<String, CshTransactionCommon> writeOffRegistionMap = new HashMap<>();


    @Autowired
    private ICshAllocationService cshAllocationService;
    @Autowired
    private ICshAllocationCreditService cshAllocationCreditService;
    @Autowired
    private ICshAllocationReceiptService cshAllocationReceiptService;

    @Autowired
    private CshAllocationReceiptMapper cshAllocationReceiptMapper;

    @Autowired
    private CshAllocationCreditMapper cshAllocationCreditMapper;

    @Autowired
    private HlsCusCshTransactionDtlService cusCshTransactionDtlService;

    @Autowired
    private IGldFinanceIncomeDayInterfaceService iGldFinanceIncomeDayInterfaceService;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;


    @Autowired
    private JeTrxCommonService commonService;

    public static Long company_id = 0L;
    public static Map<Long, Double> amountMap;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private SysEventService sysEventService;

    @Autowired
    private HlsSystemNoticeMapper noticeMapper;
    @Autowired
    private FinanceBaseUtils financeBaseUtils;

    @Autowired
    private ICshAllocationAdvanceService advanceService;

    @Autowired
    private IGldContractCashflowService gldContractCashflowService;
    @Autowired
    private ICshAllocationDepositService depositService;

    @Resource
    private  CshTransactionRefundService cshTransactionRefundService;
    @Resource
    private  CshTransactionRefundLnMapper cshTransactionRefundLnMapper;
    @Resource
    private  CshPaymentReqLnBankAccountMapper cshPaymentReqLnBankAccountMapper;
    @Autowired
    private IYLMessageNoticeService messageNoticeService;

    /**
     * 付款反冲 1、插入核销反冲记录 csh_write_off 可能为多条 2、更改csh_transaction原核销记录核销标志、日期
     *
     * @throws ChangeLimitException
     */
    @Override
    public void paymentWriteOffReversed(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, HttpSession session) throws ChangeLimitException {
        // 查询现金事务对应的所有核销记录
        HlsCusCshWriteOff cshw = new HlsCusCshWriteOff();
        /*if (iPaymentWriteOffReversedService != null) {
            iPaymentWriteOffReversedService.beforeReverse(iRequest, cshWriteOff, session);
        }*/
        // cshw.setWriteOffType("PAYMENT_DEBT"); 由于有多种类型的核销事务  去掉这个限制 只使用id查询
        cshw.setCshTransactionId(cshWriteOff.getCshTransactionId());
        //List<HlsCusCshWriteOff> list = cshWriteOffMapper.select(cshw);
        List<HlsCusCshWriteOff> list = cshWriteOffMapper.selectAllCshWriteOff(cshw);
        List<HlsCusCshWriteOff> clist = list;
        HlsCusCshTransaction sss = new HlsCusCshTransaction();
        sss.setTransactionId(cshWriteOff.getCshTransactionId());
        // 更新原CshTransaction
//		HlsCusCshTransaction cshTransaction1 = cshTransactionMapper.selectByPrimaryKey(sss);
        HlsCusCshTransaction cshTransaction1 = cshTransactionMapper.selectCshTransactionById(sss).get(0);
        cshTransaction1.setReversedFlag("W");
        cshTransaction1.setReversedDate(cshWriteOff.getReversedDate());
        if (cshTransaction1.getWriteOffFlag().equals("PARTIAL")) {
            cshTransaction1.setWriteOffFlag("NOT");
        } else if (cshTransaction1.getWriteOffFlag().equals("FULL")) {
            cshTransaction1.setWriteOffFlag("NOT");
        }
        cshTransactionService.updateByPrimaryKeySelective(iRequest, cshTransaction1);
        //插入新的CshTransaction
        Long temp_tid = cshTransaction1.getTransactionId();
        if (cshWriteOff.getRemarks() != null) {
            cshTransaction1.setDescription(cshWriteOff.getRemarks());
        }
        cshTransaction1.setReversedFlag("R");
        cshTransaction1.setReversedTrxId(cshTransaction1.getTransactionId());
        cshTransaction1.setReversedDate(cshWriteOff.getReversedDate());
        cshTransaction1.setWriteOffAmount(CalculateUtil.mul(cshTransaction1.getWriteOffAmount(), -1D));

        cshTransaction1.setTransactionAmount(CalculateUtil.mul(cshTransaction1.getTransactionAmount(), -1D));
        cshTransaction1.setTransactionId(null);
        cshTransactionService.insertSelective(iRequest, cshTransaction1);
        // 更新账户余额表
        if (cshTransaction1.getTransactionAmount() != null && cshTransaction1.getTransactionAmount().compareTo(0D) != 0)
            paymentBankAccountBalance(iRequest, cshTransaction1);
        //原CshTransaction记录新CshTransaction的id
        cshTransaction1.setReversedTrxId(cshTransaction1.getTransactionId());
        cshTransaction1.setTransactionId(temp_tid);
        cshTransactionMapper.updateReversedTrxId(cshTransaction1);
        // 更新原先的CshTransaction
        // cshTransaction1.setTransactionId(temp_tid);
        // cshTransaction1.setReversedFlag("W");
        /*
         * cshTransaction1.setReversedTrxId(cshTransaction1.getTransactionId());
         */
        // cshTransaction1.setReversedDate(null);
        // cshTransaction1.setWriteOffAmount(cshTransaction1.getWriteOffAmount()*(-1));
        // cshTransaction1.setTransactionAmount(cshTransaction1.getTransactionAmount()*(-1));
        // cshTransactionMapper.updateByPrimaryKeySelective(cshTransaction1);
        //插入凭证事物流水表 by fjm 17.9.1
        AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");
        AbstractJeTrxService writeOffJetrxService = jeTrxCommonService.map.get("CSH_WRITE_OFF");

        Map transactionParams = new HashMap<>();
        transactionParams.put("jeTrxId", temp_tid);
        transactionParams.put("companyId", iRequest.getCompanyId());
        transactionParams.put("contractId", cshWriteOff.getContractId());
        String writeOffType = list.stream().findFirst().orElse(new HlsCusCshWriteOff()).getWriteOffType();
        if (//"PAYMENT_DEBT".equals(writeOffType)
                list.stream().filter(item -> "PAYMENT_DEBT".equals(item.getWriteOffType())).count() > 0) {
            transactionParams.put("sourceDoc", "CON_CONTRACT");
        } else if (//"LON_PAYMENT_DEBT".equals(writeOffType)
                list.stream().filter(item -> "LON_PAYMENT_DEBT".equals(item.getWriteOffType())).count() > 0) {
            transactionParams.put("sourceDoc", "LON_CONTRACT");
        } else if (//"FCT_PAYMENT_DEBT".equals(writeOffType)
                list.stream().filter(item -> "FCT_PAYMENT_DEBT".equals(item.getWriteOffType())).count() > 0) {
            transactionParams.put("sourceDoc", "FCT_CONTRACT");
        }
        transactionParams.put("reverseJeDate", cshWriteOff.getReversedDate());
        transactionParams.put("reverseJeTrxId", cshTransaction1.getTransactionId());
        transactionJeTrxService.process(iRequest, transactionParams);
        String desc = "";
        for (HlsCusCshWriteOff c : list) {
            String writeOffTypeInside = c.getWriteOffType();
            if ("RECEIPT_CREDIT".equalsIgnoreCase(writeOffTypeInside) || "RECEIPT_DEPOSIT".equalsIgnoreCase(writeOffTypeInside)) {
                CshTransactionCommon cshTransactionCommon = writeOffRegistionMap.get(writeOffTypeInside);
                try {
                    cshTransactionCommon.reversed(iRequest, c);
                } catch (BeyondAmountLimitException e) {
                    e.printStackTrace();
                } catch (com.hand.hls.csh.exception.ChangeLimitException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (WriteOffTypeNullException e) {
                    e.printStackTrace();
                } catch (ResMessageException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Map writeOffMap = new HashMap<>();
            Long temp_wfid = c.getWriteOffId();
            desc = c.getDescription();
            // 插入反冲核销记录，不同的是核销金额为负数
            c.setReversedFlag("R");
            c.setReversedWriteOffId(c.getWriteOffId());
            c.setReversedDate(cshWriteOff.getReversedDate());
            c.setDescription(cshWriteOff.getRemarks());
            c.setWriteOffDueAmount(CalculateUtil.mul(c.getWriteOffDueAmount(), -1D));
            c.setCshWriteOffAmount(CalculateUtil.mul(c.getCshWriteOffAmount(), -1D));
            self().insertSelective(iRequest, c);

            writeOffMap.put("jeTrxId", c.getWriteOffId());
            writeOffMap.put("companyId", iRequest.getCompanyId());
            writeOffMap.put("contractId", cshWriteOff.getContractId());
            if ("PAYMENT_DEBT".equals(c.getWriteOffType()) || "RECEIPT_CREDIT".equalsIgnoreCase(c.getWriteOffType()) || "RECEIPT_DEPOSIT".equalsIgnoreCase(c.getWriteOffType())) {
                writeOffMap.put("sourceDoc", "CON_CONTRACT");
            } else if ("LON_PAYMENT_DEBT".equals(c.getWriteOffType())) {
                writeOffMap.put("sourceDoc", "LON_CONTRACT");
            } else if ("FCT_PAYMENT_DEBT".equals(c.getWriteOffType())) {
                writeOffMap.put("sourceDoc", "FCT_CONTRACT");
            }
            writeOffMap.put("reverseJeDate", cshWriteOff.getReversedDate());
            writeOffMap.put("reverseJeTrxId", temp_wfid);

            // 修改原先的核销记录
            HlsCusCshWriteOff cshWriteOffOrigin = new HlsCusCshWriteOff();
            cshWriteOffOrigin.setReversedWriteOffId(c.getWriteOffId());
            cshWriteOffOrigin.setWriteOffId(temp_wfid);
            cshWriteOffOrigin.setDescription(desc);
            cshWriteOffOrigin.setReversedFlag("W");
            cshWriteOffOrigin.setWriteOffDueAmount(c.getWriteOffDueAmount() * (-1));
            cshWriteOffOrigin.setCshWriteOffAmount(c.getCshWriteOffAmount() * (-1));
            cshWriteOffOrigin.setReversedDate(cshWriteOff.getReversedDate());
            self().updateByPrimaryKeySelective(iRequest, cshWriteOffOrigin);

            writeOffJetrxService.process(iRequest, writeOffMap);

            // 更新付款申请行的已支付金额
            HlsCusCshPaymentReqLn cshPaymentReqLn = (HlsCusCshPaymentReqLn) cshPaymentReqLnMapper
                    .selectCshPaymentReqLnDetailByLnID(Arrays.asList(c.getPaymentReqLineId().toString())).get(0);

            if (CalculateUtil.add(c.getCshWriteOffAmount(), cshPaymentReqLn.getAmountPaid()).compareTo(0D) != 0) {
                // 回写申请行表已支付标志
                cshPaymentReqLn.setPaymentFlag("PARTIAL");
                cshPaymentReqLnMapper.updatePaymentFlag(cshPaymentReqLn);
            } else {
                cshPaymentReqLn.setPaymentFlag("NOT");
                cshPaymentReqLnMapper.updatePaymentFlag(cshPaymentReqLn);
            }

            // 更新行表已经支付标志
            cshPaymentReqLn.setAmountPaid(CalculateUtil.add(cshPaymentReqLn.getAmountPaid(), c.getCshWriteOffAmount()));
            cshPaymentReqLnMapper.updateAmountPaid(cshPaymentReqLn);


            //如果是付款 更新现金流表上的已收金额和核销标志
            if ("PAYMENT_DEBT".equals(writeOffTypeInside)) {
                finalInterpolation(clist, session);
            } else if ("LON_PAYMENT_DEBT".equals(writeOffTypeInside)) {
                //如果是融资还款  更新还款表上的已收金额和核销标志
                changeLonRepayment(iRequest, clist);
            } else if ("CT_FCT_PAYMENT_DEBT".equalsIgnoreCase(writeOffTypeInside)) {
                //保理    更新账户余额表
                changeFctrRePayment(iRequest, clist);
            }

        }

        /*if (iPaymentWriteOffReversedService != null) {
            iPaymentWriteOffReversedService.afterReverse(iRequest, cshWriteOff, session);
        }*/
    }

    @SuppressWarnings("ALL")
    private void paymentBankAccountBalance(IRequest iRequest, HlsCusCshTransaction cshTransaction) {
        Calendar c = Calendar.getInstance();
        c.setTime(cshTransaction.getTransactionDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        HlsCusCshBankAccountBalance accountBalance = null;

        if (year == currentYear && month == currentMonth) {
            HlsCusCshBankAccountBalance bankAccountBalance = bankAccountBalanceMapper.selectAccountBalance(
                    cshTransaction.getBankAccountId(), Long.valueOf(currentYear), Long.valueOf(currentMonth));

            if (bankAccountBalance == null) {
                bankAccountBalance = new HlsCusCshBankAccountBalance();

                bankAccountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                bankAccountBalance.setPeriodYear(Long.valueOf(year));
                bankAccountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance = bankAccountBalanceMapper.getCloseBankAccountBalance(bankAccountBalance).isEmpty() ? null : bankAccountBalanceMapper.getCloseBankAccountBalance(bankAccountBalance).get(0);
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

                cshBankAccountBalanceService.insertSelective(iRequest, bankAccountBalance);

            } else {
                accountBalance = new HlsCusCshBankAccountBalance();
                accountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                accountBalance.setPeriodYear(Long.valueOf(year));
                accountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance.setPeriodIncome(0d);
                accountBalance.setPeriodExpense(cshTransaction.getTransactionAmount());
                accountBalance.setLastUpdatedBy(iRequest.getUserId());
                accountBalance.setLastUpdateDate(new Date());
                cshBankAccountBalanceService.updateByPrimaryKeySelective(iRequest, accountBalance);
            }

        } else {
            HlsCusCshBankAccountBalance bankAccountBalance = bankAccountBalanceMapper
                    .selectAccountBalance(cshTransaction.getBankAccountId(), Long.valueOf(year), Long.valueOf(month));
            if (bankAccountBalance == null) {
                bankAccountBalance = new HlsCusCshBankAccountBalance();

                bankAccountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                bankAccountBalance.setPeriodYear(Long.valueOf(year));
                bankAccountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance = bankAccountBalanceMapper.getCloseBankAccountBalance(bankAccountBalance).isEmpty() ? null : bankAccountBalanceMapper.getCloseBankAccountBalance(bankAccountBalance).get(0);
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

                cshBankAccountBalanceService.insertSelective(iRequest, bankAccountBalance);

            } else {
                accountBalance = new HlsCusCshBankAccountBalance();
                accountBalance.setBankAccountId(cshTransaction.getBankAccountId());
                accountBalance.setPeriodYear(Long.valueOf(year));
                accountBalance.setPeriodMonth(Long.valueOf(month));
                accountBalance.setPeriodIncome(cshTransaction.getTransactionAmount());
                accountBalance.setPeriodExpense(0d);
                accountBalance.setLastUpdatedBy(iRequest.getUserId());
                accountBalance.setLastUpdateDate(new Date());
                cshBankAccountBalanceService.updateByPrimaryKeySelective(iRequest, accountBalance);

            }

            accountBalance = new HlsCusCshBankAccountBalance();
            accountBalance.setBankAccountId(cshTransaction.getBankAccountId());
            accountBalance.setPeriodYear(Long.valueOf(year));
            accountBalance.setPeriodMonth(Long.valueOf(month));
            accountBalance.setPeriodIncome(0d);
            accountBalance.setPeriodExpense(cshTransaction.getTransactionAmount());
            accountBalance.setLastUpdatedBy(iRequest.getUserId());
            accountBalance.setLastUpdateDate(new Date());
            cshBankAccountBalanceService.updateByPrimaryKeySelective(iRequest, accountBalance);
        }
    }

    public void changeFctrRePayment(IRequest iRequest, List<HlsCusCshWriteOff> clist) {
        for (int i = 0; i < clist.size(); i++) {
            HlsCusCshWriteOff writeOff = clist.get(i);
            double reverseAmount = writeOff.getWriteOffDueAmount();//反冲金额（负数）
            Long cashFlowId = writeOff.getCashflowId();//获取还款id
            HlsCusFctQuotationCashflow cf = new HlsCusFctQuotationCashflow();
            cf.setQuotationCashflowId(cashFlowId);
            cf = cashflowService.selectByPrimaryKey(iRequest, cf);

            Double receivedAmount = 0D;
            if (cf.getWriteOffAmount() != null) {
                receivedAmount = cf.getWriteOffAmount();
            }
            receivedAmount = CalculateUtil.add(receivedAmount, reverseAmount);//新还款金额
            if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
                cf.setWriteOffFlag("NOT");
            } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) > 0) {
                cf.setWriteOffFlag("PARTIAL");
            } else {
                throw new RuntimeException("金额超出限制");
            }
            cf.setWriteOffAmount(receivedAmount);
            cf.setReceivedAmount(receivedAmount);
            cashflowService.updateByPrimaryKeySelective(iRequest, cf);
        }
    }

    /**
     * 付款反冲，融资还款回写还款计划表
     **/
    public void changeLonRepayment(IRequest iRequest, List<HlsCusCshWriteOff> clist) {
        for (int i = 0; i < clist.size(); i++) {
            HlsCusCshWriteOff writeOff = clist.get(i);
            double reverseAmount = writeOff.getWriteOffDueAmount();//反冲金额（负数）
            Long repaymentId = writeOff.getRepaymentId();//获取还款id
            HlsCusLonContractRepayment repayment = new HlsCusLonContractRepayment();
            repayment.setRepaymentId(repaymentId);
            //repayment = repaymentMapper.selectByPrimaryKey(repayment);未知错误 desc by xuju
            Long contractId = repayment.getContractId();//融资合同id
            Double receivedAmount = 0D;
            if (repayment.getWriteOffAmount() != null) {
                receivedAmount = repayment.getWriteOffAmount();//还款计划上的还款金额
            }
            receivedAmount = CalculateUtil.add(receivedAmount, reverseAmount);//新还款金额
            if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
                repayment.setWriteOffFlag("NOT");
            } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) > 0) {
                repayment.setWriteOffFlag("PARTIAL");
            } else {
                throw new RuntimeException("金额超出限制");
            }
            repayment.setWriteOffAmount(receivedAmount);
            repaymentMapper.updateByPrimaryKeySelective(repayment);
            HlsCusLonContract lonContract = lonContractMapper.selectByPrimaryKey(contractId);
            //插入额度事物表
            HlsCusHlsCreditLineTrx hlsCreditLineTrx = new HlsCusHlsCreditLineTrx();
            hlsCreditLineTrx.setCompanyId(iRequest.getCompanyId());
            hlsCreditLineTrx.setTrxCode("LON_CONTRACT_REPAYMENT");
            hlsCreditLineTrx.setTrxAmount(reverseAmount);
            hlsCreditLineTrx.setSourceDocumentId(repaymentId);
            hlsCreditLineTrx.setSourceDocumentCategory(DocumentChangeCommon.getTableName(HlsCusLonContractRepayment.class));
            hlsCreditLineTrx.setCreditLineId(lonContract.getCreditLineId());
            hlsCreditLineTrx.setTrxDate(repayment.getDueDate());
            hlsCreditLineTrx = hlsCreditLineTrxService.insertSelective(iRequest, hlsCreditLineTrx);
            //更新额度主数据上对应的已占用额度
            hlsCreditLineTrxService.calcCreditMethod(iRequest, hlsCreditLineTrx.getTrxId());

        }
    }

    /**
     * 付款反冲，回写现金流表已收的金额
     *
     * @throws ChangeLimitException
     */
    public void finalInterpolation(List<HlsCusCshWriteOff> clist, HttpSession session) throws ChangeLimitException {
        double reverseAmount = 0.00;
        for (int i = 0; i < clist.size(); i++) {
            reverseAmount = CalculateUtil.add(reverseAmount, clist.get(i).getWriteOffDueAmount());
        }
        Long cashFlowId = clist.get(0).getCashflowId();
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setCashflowId(cashFlowId);
        HlsCusConContractCashflow contractCashflow = cashflowMapper.selectByPrimaryKey(cashflow);
        Double receivedAmount = contractCashflow.getReceivedAmount();
        contractCashflow.setLastReceivedDate(new Date());//最后核销日
        receivedAmount = CalculateUtil.add(receivedAmount, reverseAmount);
        if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(0)) == 0) {
            contractCashflow.setWriteOffFlag("NOT");
        } else if (new BigDecimal(receivedAmount.toString()).compareTo(new BigDecimal(contractCashflow.getDueAmount())) < 0) {
            contractCashflow.setWriteOffFlag("PARTIAL");
        } else {
            throw new ChangeLimitException("金额超出限制");
        }
        contractCashflow.setLastUpdatedBy((Long) session.getAttribute("userId"));
        contractCashflow.setLastUpdateDate(new Date());
        contractCashflow.setReceivedAmount(receivedAmount);

        cashflowMapper.updateOneConContractCashflow(contractCashflow);
    }

    @Override
    public void contextInitialized(ApplicationContext appCtx) {
        Map<String, CshTransactionCommon> map = appCtx.getBeansOfType(CshTransactionCommon.class);

        map.forEach((k, v) -> {
            writeOffRegistionMap.put(v.getWriteOffType(), v);
        });
    }

    @Override
    public List<Map> selectAllWriteOff(Map map, HttpSession session, IRequest iRequest) {
        List<Map> list = cshWriteOffMapper.selectAllWriteOff(map);
        return list;
    }

    /**
     * 获取核销对象
     *
     * @param request
     * @param conContractCashflow
     * @param cshPaymentReqDt
     * @param param
     * @param writeOffType
     * @param transactionId
     * @return
     */
    private HlsCusCshWriteOff getWriteOffObject(IRequest request, HlsCusConContractCashflow conContractCashflow, HlsCusCshPaymentReqDt cshPaymentReqDt, Map<String, Object> param, String writeOffType, Long transactionId) {
        Date transactionDate = (Date) param.get("transactionDate");
        Long paymentReqId = (Long) param.get("paymentReqId");
        Long cfItem = conContractCashflow.getCfItem();
        Long cfType = conContractCashflow.getCfType();
        Double deductAmount = cshPaymentReqDt.getDeductAmount();
        Long cashflowId = conContractCashflow.getCashflowId();
        Long contractId = conContractCashflow.getContractId();
        Long times = conContractCashflow.getTimes();
        HlsCusCshWriteOff result = new HlsCusCshWriteOff();
        result.setWriteOffType(writeOffType);
        result.setWriteOffDate(transactionDate);
        result.setCshTransactionId(transactionId);
        result.setCshWriteOffAmount(deductAmount);
        result.setWriteOffPrincipal(cshPaymentReqDt.getDeductPrincipal());
        result.setWriteOffInterest(cshPaymentReqDt.getDeductInterest());
        result.setWriteOffDocCategory(CON_CONTRACT);
        result.setReversedFlag(REVERSE_FLAG_N);
        result.setCashflowId(cashflowId);
        result.setContractId(contractId);
        result.setTimes(times);
        result.setCfItem(cfItem);
        result.setCfType(cfType);
        result.setWriteOffDueAmount(deductAmount);
        result.setPaymentReqId(paymentReqId);
        result.setPaymentReqLineId(cshPaymentReqDt.getPaymentReqLnId());
        self().insertSelective(request, result);

        HlsCusCshTransaction hlsCusCshTransaction = cshTransactionMapper.selectByPrimaryKey(transactionId);

        Map writeOffMap = new HashMap();
        writeOffMap.put("jeTrxId", result.getWriteOffId());
        writeOffMap.put("companyId", hlsCusCshTransaction.getCompanyId());
        writeOffMap.put("contractId", result.getContractId());
        writeOffMap.put("sourceDoc", "CON_CONTRACT");
        AbstractJeTrxService writeOffJeTrxService = (AbstractJeTrxService)JeTrxCommonService.map.get("CSH_WRITE_OFF");
        writeOffJeTrxService.process(request, writeOffMap);
        return result;
    }

    private void createDepositTransaction(IRequest request, HlsCusCshWriteOff cshWriteOff, Map<String, Object> map, HlsCusConContractCashflow conContractCashflow) {
        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        cshTransaction.setTransactionAmount(cshWriteOff.getCshWriteOffAmount());
        cshTransaction.setWriteOffFlag(WRITE_OFF_FLAG_FULL);
        cshTransaction.setCompanyId(request.getCompanyId());
        cshTransaction.setTransactionCategory(CSH_TRANSACTION);
        cshTransaction.setTransactionType(DEPOSIT);
        cshTransaction.setBusinessType(DEPOSIT);
        Date date = (Date) map.get("transactionDate");
        cshTransaction.setTransactionDate(date);
        cshTransaction.setPenaltyCalcDate(date);
        Map<String, String> params = new HashMap<>();
        String value = codingRuleValuesService.getCodeRuleValue(request, CSH_TRANSACTION, DEPOSIT, DEPOSIT, params);
        cshTransaction.setTransactionNum(value);
        cshTransaction.setReversedFlag(REVERSE_FLAG_N);
        cshTransaction.setPostedFlag(POSTED_FLAG_N);
        cshTransaction.setContractId(conContractCashflow.getContractId());
        cshTransaction.setSourceDocCategory(CSH_WRITE_OFF);
        cshTransaction.setSourceDocId(cshWriteOff.getWriteOffId());
        cshTransaction.setSourceDocLineId(cshWriteOff.getTransactionId());

        cshTransactionService.insertSelective(request, cshTransaction);
    }

    /**
     * 更新保证金现金流
     *
     * @param request
     * @param conContractCashflow
     * @param cshPaymentReqDt
     */
    private void updateDeductionCashflow(IRequest request, HlsCusConContractCashflow conContractCashflow, HlsCusCshPaymentReqDt cshPaymentReqDt) throws BeyondAmountLimitException {
        Double dueAmount = conContractCashflow.getDueAmount();
        Double principal = conContractCashflow.getPrincipal();
        Double interest = conContractCashflow.getInterest();
        Double receivedAmount = conContractCashflow.getReceivedAmount();
        Double receivedInterest = conContractCashflow.getReceivedInterest();
        Double receivedPrincipal = conContractCashflow.getReceivedPrincipal();
        Double deductAmount = cshPaymentReqDt.getDeductAmount();
        Double deductPrincipal = cshPaymentReqDt.getDeductPrincipal();
        Double deductInterest = cshPaymentReqDt.getDeductInterest();

        Double totalAmount = CalculateUtil.add(receivedAmount, deductAmount);
        Double totalInterest = CalculateUtil.add(receivedInterest, deductInterest);
        Double totalPrincipal = CalculateUtil.add(receivedPrincipal, deductPrincipal);

        conContractCashflow.setReceivedAmount(totalAmount);
        conContractCashflow.setReceivedInterest(totalInterest);
        conContractCashflow.setReceivedPrincipal(totalPrincipal);

        if (principal.compareTo(totalPrincipal) == 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_FULL);
        } else if (principal.compareTo(totalPrincipal) > 0 && totalPrincipal.compareTo(0D) != 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_PARTIAL);
        } else if (principal.compareTo(totalPrincipal) > 0 && totalPrincipal.compareTo(0D) == 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_NOT);
        } else {
            throw new BeyondAmountLimitException("已收本金不应当大于应收本金");
        }

        if (interest.compareTo(totalInterest) == 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_FULL);
        } else if (interest.compareTo(totalInterest) > 0 && totalInterest.compareTo(0D) != 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_PARTIAL);
        } else if (interest.compareTo(totalInterest) > 0 && totalInterest.compareTo(0D) == 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_NOT);
        } else {
            throw new BeyondAmountLimitException("已收利息不应当大于应收利息");
        }

        if (dueAmount.compareTo(totalAmount) == 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_FULL);
            conContractCashflow.setFullWriteOffDate(new Date());
        } else if (dueAmount.compareTo(totalAmount) > 0 && totalAmount.compareTo(0D) != 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_PARTIAL);
        } else if (dueAmount.compareTo(totalAmount) > 0 && totalAmount.compareTo(0D) == 0) {
            conContractCashflow.setWriteOffFlag(WRITE_OFF_FLAG_NOT);
        } else {
            throw new BeyondAmountLimitException("已收金额不应当大于应收金额");
        }

        contractCashflowService.updateByPrimaryKeySelective(request, conContractCashflow);
    }

    /**
     * 抵扣核销
     *
     * @param request
     * @param transactionId
     * @param transactionId
     */
    private void doWriteOffDetail(IRequest request, Long transactionId, HlsCusCshPaymentReqDt cshPaymentReqDt, Map<String, Object> param) throws BeyondAmountLimitException {
        Long sourceDocLineId = cshPaymentReqDt.getSourceDocLineId();
        String sourceDocCategory = cshPaymentReqDt.getSourceDocCategory();
        Long paymentReqLnId = cshPaymentReqDt.getPaymentReqLnId();
        HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn = cshPaymentReqLnMapper.selectByPrimaryKey(paymentReqLnId);
        Long originCashflowId = hlsCusCshPaymentReqLn.getSourceDocLineId();
        HlsCusConContractCashflow requestCashflow = cashflowMapper.selectByPrimaryKey(originCashflowId);
        if (!CON_CONTRACT.equalsIgnoreCase(sourceDocCategory))
            return;
        HlsCusConContractCashflow conContractCashflow = cashflowMapper.selectByPrimaryKey(sourceDocLineId);
        Long cfItem = conContractCashflow.getCfItem();
        Long cfType = conContractCashflow.getCfType();
        if (cfItem == 51 && cfType == 5) {//表示为保证金类型
            HlsCusCshWriteOff payment = getWriteOffObject(request, requestCashflow, cshPaymentReqDt, param, WRITE_OFF_TYPE_PAYMENT_DEBT, transactionId);
            HlsCusCshWriteOff deposit = getWriteOffObject(request, conContractCashflow, cshPaymentReqDt, param, WRITE_OFF_TYPE_RECEIPT_DEPOSIT, transactionId);
            this.createDepositTransaction(request, payment, param, conContractCashflow);
        } else {
            HlsCusCshWriteOff payment = getWriteOffObject(request, requestCashflow, cshPaymentReqDt, param, WRITE_OFF_TYPE_PAYMENT_DEBT, transactionId);
            HlsCusCshWriteOff credit = getWriteOffObject(request, conContractCashflow, cshPaymentReqDt, param, WRITE_OFF_TYPE_RECEIPT_CREDIT, transactionId);
        }
        this.updateDeductionCashflow(request, requestCashflow, cshPaymentReqDt);
        this.updateDeductionCashflow(request, conContractCashflow, cshPaymentReqDt);
        Double amount = hlsCusCshPaymentReqLn.getAmount();
        Double amountPaid = hlsCusCshPaymentReqLn.getAmountPaid();
        Double totalPaid = CalculateUtil.add(amountPaid, cshPaymentReqDt.getDeductAmount());
        hlsCusCshPaymentReqLn.setAmountPaid(totalPaid);
        if (amount.compareTo(totalPaid) == 0) {
            hlsCusCshPaymentReqLn.setPaymentFlag(PAYMENT_FLAG_FULL);
            hlsCusCshPaymentReqLn.setPaymentCompletedDate((Date) param.get("transactionDate"));
        } else if (amount.compareTo(totalPaid) > 0 && totalPaid.compareTo(0D) == 0) {
            hlsCusCshPaymentReqLn.setPaymentFlag(PAYMENT_FLAG_NOT);
        } else if (amount.compareTo(totalPaid) > 0 && totalPaid.compareTo(0D) != 0) {
            hlsCusCshPaymentReqLn.setPaymentFlag(PAYMENT_FLAG_PARTIAL);
        } else {
            throw new BeyondAmountLimitException("核销金额不应当大于应收金额");
        }
        cshPaymentReqLnService.updateByPrimaryKeySelective(request, hlsCusCshPaymentReqLn);
    }

    /**
     * 付款抵扣
     *
     * @param request
     * @param cshPaymentReqDts
     * @param map
     */
    private void paymentDeduction(IRequest request, List<HlsCusCshPaymentReqDt> cshPaymentReqDts, Map<String, Object> map) {
        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        //cshTransaction.setWriteOffAmount(0D);
        Double transactionAmount = cshPaymentReqDts.stream().mapToDouble(item -> item.getDeductAmount()).sum();
        cshTransaction.setTransactionAmount(transactionAmount);
        cshTransaction.setWriteOffFlag(WRITE_OFF_FLAG_FULL);
        cshTransaction.setCompanyId(request.getCompanyId());
        cshTransaction.setTransactionCategory(CSH_TRANSACTION);
        cshTransaction.setTransactionType(DEDUCTION);
        cshTransaction.setBusinessType(DEDUCTION);
        cshTransaction.setCurrencyCode("CNY");
        cshTransaction.setWriteOffAmount(transactionAmount);
        cshTransaction.setFullWriteOffDate(new Date());
        cshTransaction.setContractId(cshPaymentReqDts.get(0).getSourceDocId());
        Date date = (Date) map.get("transactionDate");

        Long bpBankAccountId = (Long) map.get("bpBankAccountId");
        String bpBankAccountName = (String) map.get("bpBankAccountName");
        String bpBankAccountNum = (String) map.get("bpBankAccountNum");
        String bpBankBranchName = (String) map.get("bpBankBranchName");
        String bpBankName = (String) map.get("bpBankName");

        cshTransaction.setBpBankAccountId(bpBankAccountId);
        cshTransaction.setBpBankAccountName(bpBankAccountName);
        cshTransaction.setBpBankAccountNum(bpBankAccountNum);
        cshTransaction.setBpBankBranchName(bpBankBranchName);
        cshTransaction.setBpBankName(bpBankName);

        cshTransaction.setTransactionDate(date);
        cshTransaction.setPenaltyCalcDate(date);
        Map<String, String> params = new HashMap<>();
        String value = codingRuleValuesService.getCodeRuleValue(request, CSH_TRANSACTION, DEDUCTION, DEDUCTION, params);
        cshTransaction.setTransactionNum(value);
        cshTransaction.setReversedFlag(REVERSE_FLAG_N);
        cshTransaction.setPostedFlag(POSTED_FLAG_N);
        cshTransaction.setSourceDocCategory(CSH_PAYMENT_REQ);
        Long paymentReqId = (Long) map.get("paymentReqId");
        cshTransaction.setSourceDocId(paymentReqId);
        cshTransaction.setSourceDocLineId(cshPaymentReqDts.get(0).getPaymentReqLnId());
        cshTransactionService.insertSelective(request, cshTransaction);

        Map transactionMap = new HashMap();
        transactionMap.put("jeTrxId", cshTransaction.getTransactionId());
        transactionMap.put("companyId", cshTransaction.getCompanyId());
        transactionMap.put("contractId", cshTransaction.getContractId());
        transactionMap.put("sourceDoc", "CON_CONTRACT");
        AbstractJeTrxService cshTransactionJeTrxService = (AbstractJeTrxService)JeTrxCommonService.map.get("CSH_TRANSACTION");
        cshTransactionJeTrxService.process(request, transactionMap);

        Long transactionId = cshTransaction.getTransactionId();
        Map<String, Object> param = new HashMap<>();
        param.put("transactionDate", date);
        param.put("paymentReqId", paymentReqId);
        for (HlsCusCshPaymentReqDt cshPaymentReqDt : cshPaymentReqDts) {
            Long paymentReqLnId = cshPaymentReqDt.getPaymentReqLnId();
            HlsCusCshWriteOff writeOff = new HlsCusCshWriteOff();
            writeOff.setPaymentReqLineId(paymentReqLnId);
            writeOff.setWriteOffType("RECEIPT_CREDIT");
            writeOff.setReversedFlag("N");
            List<HlsCusCshWriteOff> select = cshWriteOffMapper.select(writeOff);
            if (select.isEmpty()) {
                try {
                    doWriteOffDetail(request, transactionId, cshPaymentReqDt, param);
                    cshPaymentReqDt.setDeductFlag("Y");
                    hlsCusCshPaymentReqDtMapper.updateByPrimaryKeySelective(cshPaymentReqDt);
                } catch (BeyondAmountLimitException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新现金事物和账户余额
     *
     * @param iRequest
     * @param cshWriteOffs
     * @throws ChangeLimitException
     */
    private void saveWriteOffType(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {
        Double temp_TransactionAmount = 0D;
        HlsCusCshTransaction temp_cshTransaction = cshWriteOffs.get(0).getCshTransaction();
        if(temp_cshTransaction == null){
            temp_cshTransaction = cshTransactionMapper.selectByPrimaryKey(cshWriteOffs.get(0).getCshTransactionId());
        }
        Double transactionDueAmount = CshWriteOffPaymentServiceImpl.transactionDueAmount;//申请行上的申请金额总和
        Double amount_paid = 0.0D;//所有申请行上的已支付金额
        for (int i = 0; i < cshWriteOffs.size(); i++) {
            temp_TransactionAmount = CalculateUtil.add(temp_TransactionAmount, cshWriteOffs.get(i).getCshWriteOffAmount());//真实的申请金额总和
        }
        temp_cshTransaction.setTransactionAmount(temp_TransactionAmount);
        temp_cshTransaction.setWriteOffAmount(temp_TransactionAmount);
//        if (new BigDecimal(CalculateUtil.add(temp_TransactionAmount, amount_paid).toString()).compareTo(new BigDecimal(transactionDueAmount.toString())) == 0) {
//            temp_cshTransaction.setWriteOffFlag("FULL");
//        } else if (new BigDecimal(CalculateUtil.add(temp_TransactionAmount, amount_paid).toString()).compareTo(new BigDecimal(transactionDueAmount.toString())) < 0) {
//            temp_cshTransaction.setWriteOffFlag("PARTIAL");
//        } else {
//            throw new BeyondAmountLimitException();
//        }

//        if (transactionDueAmount.compareTo(0D) != 0) {
//            cshTransactionService.updateByPrimaryKeySelective(iRequest, temp_cshTransaction);
//        }
//        // 更新账户余额表
//        paymentBankAccountBalance(iRequest, temp_cshTransaction);

        Map<String, Object> map = new HashMap<>();
        HlsCusCshWriteOff randomWriteOff = cshWriteOffs.stream().findAny().orElseGet(HlsCusCshWriteOff::new);
        Date transactionDate = temp_cshTransaction.getTransactionDate();
        Long paymentReqId = randomWriteOff.getPaymentReqId();
        map.put("transactionDate", transactionDate);
        map.put("paymentReqId", paymentReqId);
        map.put("bpBankAccountId", temp_cshTransaction.getBpBankAccountId());
        map.put("bpBankAccountName", temp_cshTransaction.getBpBankAccountName());
        map.put("bpBankAccountNum", temp_cshTransaction.getBpBankAccountNum());
        map.put("bpBankBranchName", temp_cshTransaction.getBpBankBranchName());
        map.put("bpBankName", temp_cshTransaction.getBpBankName());
        for (HlsCusCshWriteOff item : cshWriteOffs) {
            if (item.getCfItem() != 0) {
                continue;
            }
            Long paymentReqLineId = item.getPaymentReqLineId();//获取还款申请行id
            HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
            cshPaymentReqDt.setPaymentReqLnId(paymentReqLineId);
            List<HlsCusCshPaymentReqDt> cshPaymentReqDts = cshPaymentReqDtMapper.select(cshPaymentReqDt);

            if (!cshPaymentReqDts.isEmpty()) {
                paymentDeduction(iRequest, cshPaymentReqDts, map);
            }
        }
        // validate(list);
        //validate(CshWriteOffPaymentServiceImpl.list,   session);
    }

    @Autowired
    private HlsCusCshPaymentReqDtMapper hlsCusCshPaymentReqDtMapper;

    private void saveWriteOffTypeNew(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session) throws com.hand.hls.csh.exception.ChangeLimitException, BeyondAmountLimitException {
        Double temp_TransactionAmount = 0D;
        HlsCusCshTransaction temp_cshTransaction = cshTransactionMapper.selectByPrimaryKey(cshWriteOffs.get(0).getCshTransactionId());
        Double transactionDueAmount = CshWriteOffPaymentServiceImpl.transactionDueAmount;//申请行上的申请金额总和
        Double amountPaid = CshWriteOffPaymentServiceImpl.amountPaid;//所有申请行上的已支付金额
        for (int i = 0; i < cshWriteOffs.size(); i++) {
            temp_TransactionAmount = temp_TransactionAmount + cshWriteOffs.get(i).getCshWriteOffAmount();//真实的申请金额总和
        }
        temp_cshTransaction.setTransactionAmount(temp_TransactionAmount);
        temp_cshTransaction.setWriteOffAmount(temp_TransactionAmount);

        if (new BigDecimal(temp_TransactionAmount + amountPaid).compareTo(new BigDecimal(transactionDueAmount)) == 0) {
            temp_cshTransaction.setWriteOffFlag("FULL");
        } else {
            temp_cshTransaction.setWriteOffFlag("PARTIAL");
        }
        if (transactionDueAmount.compareTo(0D) != 0) {
            cshTransactionService.updateByPrimaryKeySelective(iRequest, temp_cshTransaction);
        }

        Map<String, Object> map = new HashMap<>();

        Date transactionDate = temp_cshTransaction.getTransactionDate();
        Long paymentReqId = temp_cshTransaction.getSourceDocId();
        map.put("transactionDate", transactionDate);
        map.put("currencyCode", temp_cshTransaction.getCurrencyCode());
        map.put("paymentReqId", paymentReqId);
        map.put("bpBankAccountId", temp_cshTransaction.getBpBankAccountId());
        map.put("bpBankAccountName", temp_cshTransaction.getBpBankAccountName());
        map.put("bpBankAccountNum", temp_cshTransaction.getBpBankAccountNum());
        map.put("bpBankBranchName", temp_cshTransaction.getBpBankBranchName());
        map.put("bpBankName", temp_cshTransaction.getBpBankName());

        for (HlsCusCshWriteOff item : cshWriteOffs) {
            Long paymentReqLineId = item.getPaymentReqLineId();//获取还款申请行id
            HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
            cshPaymentReqDt.setPaymentReqLnId(paymentReqLineId);
            List<HlsCusCshPaymentReqDt> cshPaymentReqDts = hlsCusCshPaymentReqDtMapper.select(cshPaymentReqDt);

            if (!cshPaymentReqDts.isEmpty()) {
                this.paymentDeduction(iRequest, cshPaymentReqDts, map);
            }
        }
    }

    /*融资还款  更新现金事务表的核销标志  更新账户余额表*/
    public void updateLonPayment(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {
        //更新现金事务表
        Double sumWriteOffAmount = 0.0D;//申请总金额
        HlsCusCshTransaction tempCshTransaction = cshWriteOffs.get(0).getCshTransaction();
        if (new BigDecimal(sumWriteOffAmount.toString()).compareTo(new BigDecimal(tempCshTransaction.getTransactionAmount().toString())) == 0) {
            //如果现金事务表中的transaction_amount值与核销总金额相等
            tempCshTransaction.setWriteOffAmount(sumWriteOffAmount);
            tempCshTransaction.setWriteOffFlag("FULL");
            tempCshTransaction.setFullWriteOffDate(tempCshTransaction.getTransactionDate());

        } else if (new BigDecimal(sumWriteOffAmount.toString()).compareTo(new BigDecimal(tempCshTransaction.getTransactionAmount().toString())) < 0) {
            tempCshTransaction.setWriteOffAmount(sumWriteOffAmount);
            tempCshTransaction.setWriteOffFlag("PARTIAL");
        } else {
            throw new BeyondAmountLimitException();
        }
        cshTransactionService.updateByPrimaryKeySelective(iRequest, tempCshTransaction);
        //更新账户余额表
        paymentBankAccountBalance(iRequest, cshWriteOffs.get(0).getCshTransaction());
    }


    public void updateFctPayment(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {
        Double sumWriteOffAmount = 0.0D; //申请总金额
        HlsCusCshTransaction tempCshTransaction = cshWriteOffs.get(0).getCshTransaction();
        if (new BigDecimal(sumWriteOffAmount.toString()).compareTo(new BigDecimal(tempCshTransaction.getTransactionAmount().toString())) == 0) {
            //如果现金事务表中的transaction_amount值与核销总金额相等
            tempCshTransaction.setWriteOffAmount(sumWriteOffAmount);
            tempCshTransaction.setWriteOffFlag("FULL");
            tempCshTransaction.setFullWriteOffDate(tempCshTransaction.getTransactionDate());

        } else if (new BigDecimal(sumWriteOffAmount.toString()).compareTo(new BigDecimal(tempCshTransaction.getTransactionAmount().toString())) < 0) {
            tempCshTransaction.setWriteOffAmount(sumWriteOffAmount);
            tempCshTransaction.setWriteOffFlag("PARTIAL");
        } else {
            throw new BeyondAmountLimitException();
        }

        cshTransactionService.updateByPrimaryKeySelective(iRequest, tempCshTransaction);
        //更新账户余额表
        paymentBankAccountBalance(iRequest, cshWriteOffs.get(0).getCshTransaction());
    }

    @Override
    public void updateWriteOff(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session)
            throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException {
        company_id = (Long) session.getAttribute("companyId");
        amountMap = new HashMap<>();//键值对
        if (cshWriteOffs != null) {
            try {
                for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
                    String writeOffType = cshWriteOff.getWriteOffType();
                    CshTransactionCommon trans = writeOffRegistionMap.get(writeOffType);
                    if (trans == null) {
                        //判断核销类型
                        throw new WriteOffTypeNullException();
                    } else {
                        cshWriteOff.setCompanyId(company_id);
                        //根据核销类型走不同的方法
                        trans.process(iRequest, cshWriteOff);
                        //如果是费用类型的 收款核销 需要收益分摊
                        financeIncomeSharing(iRequest, cshWriteOffs, writeOffType);
                    }
                }
                String writeOffType = cshWriteOffs.stream().findFirst().orElse(new HlsCusCshWriteOff()).getWriteOffType();
                if ("PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
                    saveWriteOffType(iRequest, cshWriteOffs);
                } else if ("LON_PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
                    //融资还款  更新现金事务表的核销标志  更新账户余额表
                    updateLonPayment(iRequest, cshWriteOffs);
                } else if ("FCT_PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
                    //保理  更新现金事务表的核销标志  更新账户余额表
                    updateFctPayment(iRequest, cshWriteOffs);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            //匹配的临时表数据状态回写
            //hlsCusIWriteOffMatchService.updateWriteOffMatchCheck(iRequest, cshWriteOffs);

        }
    }

    /**
     * @Title: financeIncomeSharing
     * @Discription: 如果是费用类型的 收款核销 需要收益分摊
     * @Param: [cshWriteOffs, writeOffType]
     * @Return: void
     */
    private void financeIncomeSharing(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, String writeOffType) throws Exception {
        //如果是费用类型的 收款核销 需要收益分摊
        HashMap map = new HashMap();
        List<HlsCusConContract> contractList = new ArrayList<>();
        if ("RECEIPT_CREDIT".equals(writeOffType)) {
            for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
                Long cfItem = cshWriteOff.getCfItem();
                if (cfItem == 3 || cfItem == 41 || cfItem == 66 || cfItem == 69 || cfItem == 501) {

                    HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());
                    HlsCusConContract conContract = new HlsCusConContract();

                    map.put("bizType", "LEASE");
                    map.put("shareType", cashflow.getAmortizationMethod());
                    map.put("cfItem", cashflow.getCfItem());
                    conContract.setContractId(cshWriteOff.getContractId());
                    contractList.add(conContract);
                    iGldFinanceIncomeDayInterfaceService.start(iRequest, contractList, map);
                }
            }
        }
    }

    @Override
    public void updateWriteOff(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, Long companyId)
            throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException {
        company_id = companyId;
        amountMap = new HashMap<>();
        if (cshWriteOffs != null) {
            for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
                String writeOffType = cshWriteOff.getWriteOffType();
                CshTransactionCommon trans = writeOffRegistionMap.get(writeOffType);
                if (trans == null) {
                    throw new WriteOffTypeNullException();
                } else {
                    cshWriteOff.setCompanyId(company_id);
                    trans.process(iRequest, cshWriteOff);
                }
            }
            try {
                String writeOffType = cshWriteOffs.stream().findFirst().orElse(new HlsCusCshWriteOff()).getWriteOffType();
                if ("PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
                    saveWriteOffType(iRequest, cshWriteOffs);
                } else if ("LON_PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
                    //融资还款  更新现金事务表的核销标志  更新账户余额表
                    updateLonPayment(iRequest, cshWriteOffs);
                } else if ("FCT_PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
                    //保理  更新现金事务表的核销标志  更新账户余额表
                    updateFctPayment(iRequest, cshWriteOffs);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            //匹配的临时表数据状态回写
            hlsCusIWriteOffMatchService.updateWriteOffMatchCheck(iRequest, cshWriteOffs);
        }
    }

    /**
     * 核销事物-单行-核销明细
     *
     * @param cshWriteOff
     * @return
     */
    @Override
    public List<HlsCusCshWriteOff> selectAllCancelAfterVerificationDetail(HlsCusCshWriteOff cshWriteOff) {
        return cshWriteOffMapper.selectAllCancelAfterVerificationDetail(cshWriteOff);
    }

    public String getCodeValue(IRequest iRequest, HlsCusCshTransaction tempCshTransaction) {
        Map<String, String> params = new HashMap<String, String>();
        String value = codingRuleValuesService.getCodeRuleValue(iRequest, tempCshTransaction.getTransactionCategory(), tempCshTransaction.getTransactionType(), tempCshTransaction.getBusinessType(), params);
        return value;
    }

    public void createCshPaymentReqForDeduct(HlsCusCshPaymentReqHd hd, List<HlsCusPaymentDeduct> cshPaymentDeductList, HlsCusCshTransaction cshTransaction, IRequest request) throws BeyondAmountLimitException, java.text.ParseException {

        Double deductAmount = 0D;
        Double deductPrincipalAmount = 0D;
        Double deductInterestAmount = 0D;
        for (HlsCusPaymentDeduct list : cshPaymentDeductList) {
            deductAmount = CalculateUtil.add(deductAmount, list.getDeductAmount());
            deductPrincipalAmount = CalculateUtil.add(deductPrincipalAmount, list.getDeductiblePrincipal());
            deductInterestAmount = CalculateUtil.add(deductInterestAmount, list.getDeductibleInterest());

        }
        /*HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setCompany_id(hd.getCompany_id());
        cshPaymentReqHd.setPayment_req_id(null);
        cshPaymentReqHd.setDeductFlag(null);
        cshPaymentReqHd.setItemName(hd.getItemName());
        cshPaymentReqHd.setDocument_type("PAYMENT_REQ");
        cshPaymentReqHd.setDocument_category("CSH_PAYMENT_REQ");
        cshPaymentReqHd.setBusiness_type("PAYMENT_REQ");// 业务模式
        cshPaymentReqHd.setPayment_req_number(getCodeValue(request)); // 付款申请编号
        cshPaymentReqHd.setPayment_req_date(hd.getApply_pay_date());
        cshPaymentReqHd.setPayment_req_status("APPROVED");
        cshPaymentReqHd.setApply_pay_date(new Date());
        cshPaymentReqHd.setCurrency("CNY");
        cshPaymentReqHd.setObjectVersionNumber(new Long("1"));
        cshPaymentReqHd.setLastUpdatedBy(request.getUserId());
        cshPaymentReqHd.setLastUpdateDate(new Date());
        cshPaymentReqHd.setAmount(0D);
        paymentReqHdService.insertSelective(request, cshPaymentReqHd);*/


        HlsCusCshPaymentReqLn ln = new HlsCusCshPaymentReqLn();
        ln.setPaymentReqLnId(hd.getPaymentLnId());
        ln = cshPaymentReqLnMapper.selectByPrimaryKey(ln);
        //更新现金流表
        updateCashFlow(ln.getSourceDocLineId(), request, deductAmount, deductPrincipalAmount, deductInterestAmount);
        Long contractId = 0L;
        for (HlsCusPaymentDeduct deduct : cshPaymentDeductList) {
            /*HlsCusCshPaymentReqLn cshPaymentReqLn = new HlsCusCshPaymentReqLn();
            cshPaymentReqLn.setPayment_req_id(cshPaymentReqHd.getPayment_req_id());
            cshPaymentReqLn.setSource_doc_category("CON_CONTRACT");
            cshPaymentReqLn.setSource_doc_id(ln.getSource_doc_id());
            cshPaymentReqLn.setSource_doc_line_id(deduct.getCashflowId());
            cshPaymentReqLn.setAmount(deduct.getDeductAmount());
            cshPaymentReqLn.setAmount_paid(deduct.getDeductAmount());
            cshPaymentReqLn.setDescription("");// 输入的申请原因
            cshPaymentReqLn.setPayment_flag("FULL");
            cshPaymentReqLn.setPayment_method(ln.getPayment_method());
            cshPaymentReqLn.setBp_id(ln.getBp_id());
            cshPaymentReqLn.setBp_bank_account_id(ln.getBp_bank_account_id());
            cshPaymentReqLn.setBp_bank_account_name(ln.getBp_bank_account_name());//账户名
            cshPaymentReqLn.setBp_bank_account_num(ln.getBp_bank_account_num());
            cshPaymentReqLn.setBp_bank_branch_name(ln.getBp_bank_branch_name());//银行分行
            cshPaymentReqLn.setBp_bank_name(ln.getBp_bank_name());
            cshPaymentReqLn.setDescription(ln.getDescription());
            cshPaymentReqLn = cshPaymentReqLnService.insertSelective(request, cshPaymentReqLn);*/

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            //插入核销表（需要插入N条付款记录，N条收款记录）
            //付款记录
            HlsCusCshWriteOff paymentWriteOff = new HlsCusCshWriteOff();
            paymentWriteOff.setWriteOffType("PAYMENT_DEBT");
            paymentWriteOff.setWriteOffDate(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            paymentWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
            paymentWriteOff.setCshWriteOffAmount(deduct.getDeductAmount());
            paymentWriteOff.setReversedFlag("N");
            paymentWriteOff.setCashflowId(ln.getSourceDocLineId());
            paymentWriteOff.setContractId(ln.getSourceDocId());
            paymentWriteOff.setCfItem(0L);
            paymentWriteOff.setCfType(0L);
            paymentWriteOff.setTimes(0L);
            paymentWriteOff.setWriteOffDueAmount(deduct.getDeductAmount());
            paymentWriteOff.setWriteOffPrincipal(deduct.getDeductPrincipal());
            paymentWriteOff.setWriteOffInterest(deduct.getDeductInterest());
            //paymentWriteOff.setPaymentReqLineId(cshPaymentReqLn.getPayment_req_ln_id());
            //paymentWriteOff.setPaymentReqId(cshPaymentReqHd.getPayment_req_id());
            paymentWriteOff.setWriteOffDocCategory("CON_CONTRACT");
            paymentWriteOff = self().insertSelective(request, paymentWriteOff);

            //插入核销表
            //收款记录

            HlsCusConContractCashflow cashflow = cashflowMapper.selectByPrimaryKey(deduct);
            contractId = Long.valueOf(cashflow.getContractId());
            HlsCusCshWriteOff receiptWriteOff = new HlsCusCshWriteOff();
            receiptWriteOff.setWriteOffType("RECEIPT_CREDIT");
            receiptWriteOff.setWriteOffDate(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            receiptWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
            receiptWriteOff.setCshWriteOffAmount(deduct.getDeductAmount());
            receiptWriteOff.setReversedFlag("N");
            receiptWriteOff.setTimes(0L);
            receiptWriteOff.setCashflowId(deduct.getCashflowId());
            receiptWriteOff.setContractId(contractId);
            receiptWriteOff.setCfItem(cashflow.getCfItem());
            receiptWriteOff.setCfType(cashflow.getCfType());
            receiptWriteOff.setWriteOffDueAmount(deduct.getDeductAmount());
            receiptWriteOff.setWriteOffPrincipal(deduct.getDeductPrincipal());
            receiptWriteOff.setWriteOffInterest(deduct.getDeductInterest());
            //receiptWriteOff.setPaymentReqLineId(cshPaymentReqLn.getPayment_req_ln_id());
            //receiptWriteOff.setPaymentReqId(cshPaymentReqHd.getPayment_req_id());
            receiptWriteOff.setWriteOffDocCategory("CON_CONTRACT");
            receiptWriteOff = self().insertSelective(request, receiptWriteOff);

            //更新现金流表
            updateCashFlow(deduct.getCashflowId(), request, deduct.getDeductAmount(), deduct.getDeductPrincipal(), deduct.getDeductInterest());


            //凭证

            Map paymentWriteOffMap = new HashMap<>();
            Map receiptWriteOffMap = new HashMap<>();

            paymentWriteOffMap.put("jeTrxId", paymentWriteOff.getWriteOffId());
            paymentWriteOffMap.put("companyId", request.getCompanyId());
            paymentWriteOffMap.put("contractId", paymentWriteOff.getContractId());
            paymentWriteOffMap.put("sourceDoc", "CON_CONTRACT");

            receiptWriteOffMap.put("jeTrxId", receiptWriteOff.getWriteOffId());
            receiptWriteOffMap.put("companyId", request.getCompanyId());
            receiptWriteOffMap.put("contractId", receiptWriteOff.getContractId());
            receiptWriteOffMap.put("sourceDoc", "CON_CONTRACT");

            AbstractJeTrxService writeOffJeTrxService1 = jeTrxCommonService.map.get("CSH_WRITE_OFF");//核销事物
            AbstractJeTrxService writeOffJeTrxService2 = jeTrxCommonService.map.get("CSH_WRITE_OFF");//核销事物
            writeOffJeTrxService1.process(request, paymentWriteOffMap);
            writeOffJeTrxService2.process(request, receiptWriteOffMap);

        }

        Double re = cshPaymentDeductList.stream().filter(de -> ("5").equals(de.getCfItem())).mapToDouble(HlsCusPaymentDeduct::getDeductAmount).sum();
        cshTransaction.setTransactionAmount(re);
        cshTransaction.setContractId(contractId);
        hlsCusCshTransactionService.updateByPrimaryKey(request, cshTransaction);
        // return cshPaymentReqHd;
    }


    @Override
    public void paymentDeduct(IRequest iRequest, HlsCusCshPaymentReqHd cshPaymentReqHd) {
        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();

        cshTransaction.setTransactionCategory("CSH_TRANSACTION");
        cshTransaction.setTransactionType("DEDUCTION");
        cshTransaction.setBusinessType("DEDUCTION");
        cshTransaction.setTransactionNum(getCodeValue(iRequest, cshTransaction));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            cshTransaction.setTransactionDate(format.parse(format.format(new Date())));
            cshTransaction.setPenaltyCalcDate(format.parse(format.format(new Date())));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        cshTransaction.setCompanyId(iRequest.getCompanyId());
        cshTransaction.setTransactionAmount(0D);
        cshTransaction.setCurrencyCode(cshPaymentReqHd.getCurrency());
        cshTransaction.setPaymentMethod(cshPaymentReqHd.getPaymentMethod());
        cshTransaction.setBankAccountId(cshPaymentReqHd.getBankAccountId());
        cshTransaction.setBpId(cshPaymentReqHd.getBpId());
        cshTransaction.setBpBankAccountId(cshPaymentReqHd.getBpBankAccountId());
        cshTransaction.setBpBankName(cshPaymentReqHd.getBpBankName());
        cshTransaction.setBpBankBranchName(cshPaymentReqHd.getBpBankBranchName());
        cshTransaction.setBpBankAccountNum(cshPaymentReqHd.getBpBankAccountNum());
        cshTransaction.setBpBankAccountName(cshPaymentReqHd.getBpBankAccountName());
        cshTransaction.setReversedFlag("N");
        cshTransaction.setPostedFlag("N");
        cshTransaction.setWriteOffAmount(0D);
        cshTransaction = hlsCusCshTransactionService.insertSelective(iRequest, cshTransaction);


        HlsCusPaymentDeduct cshPaymentDeduct = new HlsCusPaymentDeduct();
        cshPaymentDeduct.setPaymentReqId(cshPaymentReqHd.getPaymentReqId());
        List<HlsCusPaymentDeduct> cshPaymentDeductList = hlsCusPaymentDeductService.select(iRequest, cshPaymentDeduct, 1, 999999);
        Double deductAmount = cshPaymentDeductList.stream().mapToDouble(HlsCusPaymentDeduct::getDeductAmount).sum();

        List<HlsCusPaymentDeduct> cshPaymentDeducts = new ArrayList<>();
        for (int i = 0; i < cshPaymentDeductList.size(); i++) {
            if (cshPaymentDeductList.get(i).getDeductAmount().compareTo(0D) != 0) {
                cshPaymentDeducts.add(cshPaymentDeductList.get(i));
            }
        }


        //付款头行， 核销表
        try {
            createCshPaymentReqForDeduct(cshPaymentReqHd, cshPaymentDeducts, cshTransaction, iRequest);
        } catch (BeyondAmountLimitException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transactionWriteOffDeduct(IRequest iRequest, List<HlsCusCshWriteOff> cshTransactions) {
        //处理抵扣

        for (int j = 0; j < cshTransactions.size(); j++) {
            HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
            cshTransaction = cshTransactions.get(j).getCshTransaction();
            cshTransaction.setSourceDocLineId(cshTransactions.get(j).getPaymentReqLineId());
            HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
            cshPaymentReqHd.setPaymentReqId(cshTransactions.get(j).getPaymentReqId());
            cshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(RequestHelper.getCurrentRequest(), cshPaymentReqHd);
            if ("Y".equals(cshPaymentReqHd.getDeductFlag()) && "N".equals(cshPaymentReqHd.getDeductWriteOffFlag())) {
                cshPaymentReqHd.setPaymentMethod(cshTransaction.getPaymentMethod());
                cshPaymentReqHd.setBankAccountId(cshTransaction.getBankAccountId());
                cshPaymentReqHd.setBpId(cshTransaction.getBpId());
                cshPaymentReqHd.setBankAccountId(cshTransaction.getBankAccountId());
                cshPaymentReqHd.setBpBankName(cshTransaction.getBpBankName());
                cshPaymentReqHd.setBpBankBranchName(cshTransaction.getBpBankBranchName());
                cshPaymentReqHd.setBpBankAccountNum(cshTransaction.getBpBankAccountNum());
                cshPaymentReqHd.setBpBankAccountName(cshTransaction.getBpBankAccountName());
                cshPaymentReqHd.setPaymentLnId(cshTransaction.getSourceDocLineId());
                paymentDeduct(iRequest, cshPaymentReqHd);
                //更新可处理标志
                cshPaymentReqHd.setDeductWriteOffFlag("Y");
                cshPaymentReqHdService.updateByPrimaryKey(RequestHelper.getCurrentRequest(), cshPaymentReqHd);
            }
        }
    }

    public void updateCashFlow(Long cashflowId, IRequest request, Double deductAmount, Double deductPrincipalAmount, Double deductInterestAmount) throws BeyondAmountLimitException {
        //更新现金流表
        HlsCusConContractCashflow conContractCashflow = cashflowMapper.selectByPrimaryKey(cashflowId);
        conContractCashflow.setReceivedAmount(CalculateUtil.add(conContractCashflow.getReceivedAmount(), deductAmount));
        conContractCashflow.setReceivedPrincipal(CalculateUtil.add(conContractCashflow.getReceivedPrincipal(), deductPrincipalAmount));
        conContractCashflow.setReceivedInterest(CalculateUtil.add(conContractCashflow.getReceivedInterest(), deductInterestAmount));

        if (conContractCashflow.getReceivedAmount().compareTo(0D) == 0) {
            conContractCashflow.setWriteOffFlag("NOT");
        } else if (conContractCashflow.getReceivedAmount().compareTo(conContractCashflow.getDueAmount()) < 0) {
            conContractCashflow.setWriteOffFlag("PARTIAL");
        } else if (conContractCashflow.getReceivedAmount().compareTo(conContractCashflow.getDueAmount()) == 0) {
            conContractCashflow.setWriteOffFlag("FULL");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                conContractCashflow.setFullWriteOffDate(format.parse(format.format(new Date())));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } else {
            throw new BeyondAmountLimitException();
        }
        contractCashflowService.updateByPrimaryKeySelective(request, conContractCashflow);
    }

    @Override
    public List<HlsCusCshWriteOff> queryWriteOff(IRequest requestCtx, HlsCusCshWriteOff hlsCusCshWriteOff, int pagenum, int pagesize, String sortName, String sortOrder) {
        String orderBy = null;
        if (sortName != null) {
            if (orderBy == null) {
                orderBy = sortName + " " + sortOrder;
            } else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        PageHelper.startPage(pagenum, pagesize);
        if (StringUtils.isNotEmpty(orderBy)) {
            PageHelper.orderBy(orderBy);
        }

        return cshWriteOffMapper.queryWriteOff(hlsCusCshWriteOff);
    }

    @Override
    public void updateContractReceivedStatus(HlsCusConContractCashflow conCashflow) {
        if (conCashflow.getCfItem() != null && conCashflow.getCfItem() == 1) {
            HlsCusConContract c = new HlsCusConContract();
            c.setContractId(conCashflow.getContractId());
            c = conContractMapper.selectByPrimaryKey(c);
            List<HlsCusConContractCashflow> cccList = cashflowMapper.select(conCashflow);
            boolean allWriteOffFlag = true;
            if (cccList != null && cccList.size() > 0) {
                for (HlsCusConContractCashflow ccc : cccList) {
                    if (!WRITE_OFF_FLAG_FULL.equalsIgnoreCase(ccc.getWriteOffFlag())) {
                        allWriteOffFlag = false;
                    }
                }
            }
            if (allWriteOffFlag) {
                c.setReceivedStatus("FULL");
            } else {
                c.setReceivedStatus("PARTIAL");
            }
            conContractMapper.updateByPrimaryKey(c);
        }
    }
    /**
     * @Description:发送消息
     * @Author: Wty
     * @Date: Created om 20:09 2018/6/19
     * @param: [userIdList, message] userIdList:需要发送的userId,message:需要发送的消息
     * @return: void
     */
    public void sendNotice(List<Long> userIdList, String message) {
        logger.debug("============= SEND NOTICE START ==============");
        for (Long userId : userIdList) {
            HlsSystemNotice hlsSystemNotice = new HlsSystemNotice();
            hlsSystemNotice.setNoticeMessage(message);
            hlsSystemNotice.setNoticeTitle("销项发票开具提醒");
            hlsSystemNotice.setNoticeType("NOTICE");
            hlsSystemNotice.setNoticeLevel(String.valueOf(Long.valueOf(2)));
            hlsSystemNotice.setSourceModule("ACP");
            hlsSystemNotice.setSourceUserId(userId);
            hlsSystemNotice.setNoticeDatetime(new Date());
//            hlsSystemNotice.setNotice_message(message);
//            hlsSystemNotice.setNotice_title("投资理财到期提醒");
//            hlsSystemNotice.setNotice_datetime(new Date());
//            hlsSystemNotice.setNotice_type("NOTICE");
//            hlsSystemNotice.setNotice_level(Long.valueOf(2));
//            hlsSystemNotice.setSource_module("INV_EXP");
//            hlsSystemNotice.setSource_user_id(userId);
            hlsSystemNotice.setLastUpdateDate(new Date());
            noticeMapper.insertSelective(hlsSystemNotice);
            sysEventService.setNoticeCache(hlsSystemNotice);
        }
    }

    @Override
    /**
     * @Discription:核销入口
     * @param: [iRequest, cshWriteOffs, session]
     * @return: void
     */
    public void writeOff(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session) throws BeyondAmountLimitException, IllegalArgumentException, WriteOffTypeNullException, ChangeLimitException, ResMessageException {

        //校验
        company_id = (Long) session.getAttribute("companyId");
        if (cshWriteOffs != null) {
            for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
                String writeOffType = cshWriteOff.getWriteOffType();
                CshTransactionCommon trans = writeOffRegistionMap.get(writeOffType);
                if (trans == null) {
                    throw new WriteOffTypeNullException();
                } else {
                    cshWriteOff.setCompanyId(company_id);
                    //根据核销类型走不同的方法

                   trans.process(iRequest, cshWriteOff);

                   HlsCusCshTransaction cshTransaction = cshTransactionMapper.selectByPrimaryKey(cshWriteOff.getCshTransactionId());
                   if ((TRANSACTION_TYPE_RECEIPT.equals(cshTransaction.getTransactionType())||TRANSACTION_TYPE_ADVANCE_RECEIPT.equals(cshTransaction.getTransactionType())) && "FULL".equals(cshTransaction.getWriteOffFlag())) {
                       //核销的时候 生成 收款的凭证（兴业特殊逻辑）
                       AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");
                       Map transactionParams = new HashMap<>();
                       transactionParams.put("jeTrxId", cshTransaction.getTransactionId());
                       transactionParams.put("companyId", iRequest.getCompanyId());
                       if (cshTransaction.getContractId() != null) {
                           transactionParams.put("jeSourceId", cshTransaction.getContractId());
                           transactionParams.put("jeSourceDoc", "CON_CONTRACT");
                       }
                       transactionJeTrxService.process(iRequest, transactionParams);
                    }

                }
            }

            String writeOffType = cshWriteOffs.stream().findFirst().orElse(new HlsCusCshWriteOff()).getWriteOffType();
            if ("PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
                saveWriteOffType(iRequest, cshWriteOffs);
            }
        }

    }

    @Override
    public HlsCusCshWriteOff getReceiptCredit(HlsCusCshWriteOff cshWriteOff) {
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
        wo.setCompanyId(cshWriteOff.getCompanyId());
        wo.setSubsequentCshTrxId(cshWriteOff.getSubsequentCshTrxId());
        wo.setSubseqCshWriteOffAmount(cshWriteOff.getSubseqCshWriteOffAmount());
        //wo.setIsPolishingDeposit(cshWriteOff.getIsPolishingDeposit());
        wo.setWriteOffDocCategory(DocumentChangeCommon.getTableName(HlsCusConContract.class));

        return wo;
    }


    @Override
    public HlsCusCshTransaction updateCshTrxAfterWriteoff(HlsCusCshWriteOff cshWriteOff) throws BeyondAmountLimitException {
        HlsCusCshTransaction ct = new HlsCusCshTransaction();

        HlsCusCshTransaction cshTransaction = cshTransactionMapper.queryDetailByIdList(cshWriteOff.getCshTransactionId()).stream().findAny().orElse(null);
        if (cshTransaction.getWriteOffAmount() == null) {
            cshTransaction.setWriteOffAmount(0d);
        }
        if (cshWriteOff.getWriteOffDueAmount() == null) {
            cshWriteOff.setWriteOffDueAmount(0d);
        }

        Double sumAmount = cshTransaction.getWriteOffAmount() + cshWriteOff.getWriteOffDueAmount();

        if (new BigDecimal(sumAmount).compareTo(new BigDecimal(0)) == 0) {
            ct.setWriteOffFlag("NOT");
            ct.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount).compareTo(new BigDecimal(cshTransaction.getTransactionAmount())) < 0) {
            ct.setWriteOffFlag("PARTIAL");
            ct.setWriteOffAmount(sumAmount);
        } else if (new BigDecimal(sumAmount).compareTo(new BigDecimal(cshTransaction.getTransactionAmount())) == 0) {
            ct.setWriteOffFlag("FULL");
            ct.setWriteOffAmount(sumAmount);
            ct.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        } else {
            throw new BeyondAmountLimitException();
        }

        ct.setTransactionId(cshWriteOff.getCshTransactionId());

        return ct;
    }

    @Override
    public HlsCusConContractCashflow updateConCashflowAfterWriteOff(HlsCusCshWriteOff cshWriteOff) {
        HlsCusConContractCashflow cf = cashflowMapper.selectByPrimaryKey(cshWriteOff.getCashflowId());

        if (cf.getReceivedAmount() == null) {
            cf.setReceivedAmount(0d);
        }
        if (cshWriteOff.getWriteOffPrincipal() == null) {
            cshWriteOff.setWriteOffPrincipal(0d);
        }

        if (cshWriteOff.getWriteOffInterest() == null) {
            cshWriteOff.setWriteOffInterest(0d);
        }


        if (cf.getReceivedPrincipal() == null) {
            cf.setReceivedPrincipal(cshWriteOff.getWriteOffPrincipal());
        } else {
            cf.setReceivedPrincipal(MathUtil.add(cf.getReceivedPrincipal(), cshWriteOff.getWriteOffPrincipal()));
        }

        if (cf.getReceivedInterest() == null) {
//            cf.setReceivedInterest(cshWriteOff.getWriteOffPrincipal());
            cf.setReceivedInterest(cshWriteOff.getWriteOffInterest());
        } else {
            cf.setReceivedInterest(MathUtil.add(cf.getReceivedInterest(), cshWriteOff.getWriteOffInterest()));
        }

        Double receivedAmount = MathUtil.add(cf.getReceivedAmount(), cshWriteOff.getWriteOffDueAmount());
        if (receivedAmount == 0) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("NOT");
            cf.setFullWriteOffDate(null);
        } else if (receivedAmount < cf.getDueAmount()) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("PARTIAL");
            cf.setFullWriteOffDate(null);
        } else if (receivedAmount.equals(cf.getDueAmount())) {
            cf.setReceivedAmount(receivedAmount);
            cf.setWriteOffFlag("FULL");
            cf.setFullWriteOffDate(cshWriteOff.getWriteOffDate());
        }

        //以下现金流不更新已收本金和已收利息
        List<Integer> cfItem = Arrays.asList(0, 2, 3, 5, 8, 9, 10, 52);
        if (cfItem.contains(Integer.valueOf(cf.getCfItem().toString()))) {
            cf.setReceivedPrincipal(0d);
            cf.setReceivedInterest(0d);
        }

        return cf;
    }

    @Override
    public HlsCusCshTransaction getCshTransaction(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, String writeOffType) {
        HlsCusCshTransaction cshTransaction = cshTransactionMapper.queryDetailByIdList(cshWriteOff.getCshTransactionId()).stream().findAny().orElse(null);
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
        cshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(iRequest,
                cshTransaction.getTransactionCategory(), cshTransaction.getTransactionType(), cshTransaction.getBusinessType(), params));

        return cshTransaction;
    }


    @Override
    /**
     * @Discription:核销反冲入口
     * @param: [requestContext, cshWriteOffs, session]
     * @return: void
     */
    public void writeOffReversed(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session) throws Exception {

        if (cshWriteOffs != null && cshWriteOffs.size() > 0) {
            mainCshWriteOffService.reverseCshWriteOffMain(iRequest, cshWriteOffs, cshWriteOffs.get(0).getReversedDate(), "收款核销反冲");

            //核销反冲 同时需要反冲收款
            String reversedReceiptFlag = cshWriteOffs.get(0).getReversedReceiptFlag();
            if ("Y".equals(reversedReceiptFlag)) {
                List<HlsCusCshTransaction> cshTransactionList = new ArrayList<>();
                Set<Long> transactionIds = cshWriteOffs.stream().collect(Collectors.groupingBy(HlsCusCshWriteOff::getCshTransactionId)).keySet();
                for (Long transactionId : transactionIds) {
                    HlsCusCshTransaction cshTransaction = cshTransactionMapper.selectByPrimaryKey(transactionId);
                    cshTransaction.setReversedDate(cshWriteOffs.get(0).getReversedDate());
                    cshTransaction.setRefV15(reversedReceiptFlag);

                    cshTransactionList.add(cshTransaction);
                }
                cshTransactionService.reverseCshTransaction(iRequest, cshTransactionList);
            }
        }
    }


    /**
     * @Title: deepCopy
     * @Discription: 深拷贝
     * @Param: [object]
     * @Return: T
     */
    private <T> T deepCopy(T object) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        T copyObject = (T) ois.readObject();
        return copyObject;
    }


    private HlsCusCshWriteOff getCshWriteOff(IRequest iRequest, HlsCusCshWriteOff cshWriteOff, HlsCusCshTransaction cshTransaction, Double writeOffAmount, String writeOffType) throws ParseException {
        //收款核销保证金
       /* if (cshWriteOff.getCfType() == 5) {
            cshWriteOff.setWriteOffType("RECEIPT_DEPOSIT");
        }else{
            //收款核销债权
            cshWriteOff.setWriteOffType("RECEIPT_CREDIT");
        }*/
        //modify 取消掉RECEIPT_DEPOSIT 的 WriteOffType
        cshWriteOff.setWriteOffType(writeOffType);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        cshWriteOff.setTransactionId(cshTransaction.getTransactionId());
        cshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
        cshWriteOff.setWriteOffDate(df.parse(df.format(new Date())));
        cshWriteOff.setCshWriteOffAmount(writeOffAmount);
        cshWriteOff.setWriteOffDueAmount(writeOffAmount);

        cshWriteOff.setReversedFlag("N");
        cshWriteOff.setWriteOffDocCategory("CON_CONTRACT");
        cshWriteOff.setCurrencyCode(cshTransaction.getCurrencyCode());
        cshWriteOff.setCompanyId(iRequest.getCompanyId());
        cshWriteOff.setImportFlag(cshTransaction.getImportFlag());

        return cshWriteOff;

    }

    @Override
    public void allocationConfirm(IRequest iRequest, List<HlsCusCshTransaction> cshTransactionList, HttpSession session) throws Exception {

        cshTransactionList.stream().forEach(item -> {
            item.setUnWriteOffAmount(nvl(item.getUnWriteOffAmount(), 0.0));
            item.setAdvanceReceiptAmount(nvl(item.getAdvanceReceiptAmount(), 0.0));
        });

        //保证金待核销金额汇总
        Double allocationDepositWriteOffAmountTotal = 0D;

        //金额校验 剩余可核销金额汇总（减去了预收款部分） >= 本次核销金额汇总
        //剩余可核销金额汇总
        Double unWriteOffAmountTotal = round(cshTransactionList.stream().collect(Collectors.summingDouble(HlsCusCshTransaction::getUnWriteOffAmount)), 2);
        //预核销金额汇总
        Double advanceReceiptAmountTotal = round(cshTransactionList.stream().collect(Collectors.summingDouble(HlsCusCshTransaction::getAdvanceReceiptAmount)), 2);
        //本次核销金额汇总

        //二期功能：核销为债权（CREDIT）
        String writeOffTypeFlag = null;

        List<HlsCusCshWriteOff> cshWriteOffList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cshTransactionList.get(0).getCshWriteOffList())){
            writeOffTypeFlag = CREDIT;
            cshWriteOffList = cshTransactionList.get(0).getCshWriteOffList();
        }

        Double canWriteOffAmountTotal = round(cshWriteOffList.stream().collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount)), 2);


        int transactionTypeSize = cshTransactionList.stream().collect(Collectors.groupingBy(HlsCusCshTransaction::getTransactionType)).size();

        String transactionType = TRANSACTION_TYPE_RECEIPT;
        String writeOffType = WRITE_OFF_TYPE_RECEIPT_CREDIT;

        if (transactionTypeSize > 1) {
            throw new ResMessageException("收款金额不能和待处理款项一起核销！");
        } else {
            transactionType = cshTransactionList.get(0).getTransactionType();
        }

        //TODO 2023-02-09 新增对核销为预收款/保证金的判断（核销类型） 收款核销为保证金：RECEIPT_DEPOSIT
        if (TRANSACTION_TYPE_RECEIPT.equals(transactionType)) {
            writeOffType = WRITE_OFF_TYPE_RECEIPT_CREDIT;
        } else {
            writeOffType = WRITE_OFF_TYPE_ADVANCE_RECEIPT_CREDIT;
        }

        if (advanceReceiptAmountTotal.compareTo(0.0) == 1 && TRANSACTION_TYPE_ADVANCE_RECEIPT.equals(transactionType)) {
            throw new ResMessageException("预收款核销为债权时，无法进行部分核销，请全额核销预收款！");
        }

        if (CREDIT.equals(writeOffTypeFlag)){
            if (unWriteOffAmountTotal.compareTo(add(canWriteOffAmountTotal, advanceReceiptAmountTotal)) != 0 && TRANSACTION_TYPE_RECEIPT.equals(transactionType)) {
                throw new ResMessageException("收款金额与待核销金额不一致！");
            }
        }
        if (DEPOSIT.equals(writeOffTypeFlag)){
            if (unWriteOffAmountTotal.compareTo(allocationDepositWriteOffAmountTotal) != 0) {
                throw new ResMessageException("剩余可核销金额与待核销金额不一致！");
            }
        }


        //按照核销规则排序 然后循环调用核销逻辑
        //核销规则为，按照收款记录的新增顺序，从早到晚，依次匹配债权。从最早新增的一笔收款开始，匹配债权列表中的所有现金流，按照现金流的新增顺序，从早到晚，依次匹配

        //按照收款记录的新增顺序，从早到晚
        cshTransactionList.stream().sorted(Comparator.comparing(HlsCusCshTransaction::getTransactionDate));
        //按照现金流的新增顺序，从早到晚
        cshWriteOffList.stream().sorted(Comparator.comparing(HlsCusCshWriteOff::getWriteOffOrder));
        //初始化分配金额
        cshWriteOffList.stream().forEach(item -> item.setAllocationAmount(0.0));
        cshTransactionList.stream().forEach(item -> {
                    Double unWriteOffAmount = sub(item.getUnWriteOffAmount(), item.getAdvanceReceiptAmount(), 2);
                    item.setAllocationAmount(unWriteOffAmount);
                }
        );

        List<HlsCusCshWriteOff> hlsCusCshWriteOffs = new ArrayList<>();
        //由于现在现金事务 和 核销事务 没有对应关系，于是根据规则匹配   实际的过程是，收款的未核销金额  逐步减少为0 ，债权的待核销金额 逐步增长到 FULL

        for (HlsCusCshTransaction transaction : cshTransactionList) {

            for (HlsCusCshWriteOff writeOff : cshWriteOffList) {

                HlsCusCshWriteOff clone = deepCopy(writeOff);

                //只要 receiptAllocationAmount >0 && writeOffDueAmount >0 就需要继续分配
                Double receiptAllocationAmount = transaction.getAllocationAmount();

                Double writeOffDueAmount = sub(writeOff.getWriteOffDueAmount(), writeOff.getAllocationAmount(), 2);

                //如果 收款剩余未核销金额 大于等于 债权剩余待核销金额 且 债权剩余待核销金额 大于0
                if (receiptAllocationAmount >= writeOffDueAmount && writeOffDueAmount > 0) {
                    clone = getCshWriteOff(iRequest, clone, transaction, writeOffDueAmount, writeOffType);
                    hlsCusCshWriteOffs.add(clone);
                    //收款剩余未核销金额  逐步减少
                    transaction.setAllocationAmount(sub(transaction.getAllocationAmount(), writeOffDueAmount, 2));
                    //债权剩余待核销金额 逐步增长
                    writeOff.setAllocationAmount(add(writeOff.getAllocationAmount(), writeOffDueAmount, 2));

                }//如果  收款剩余未核销金额 小于等于 债权剩余待核销金额 且 金额大于0  同时 债权剩余待核销金额 大于0
                else if (receiptAllocationAmount < writeOffDueAmount && receiptAllocationAmount > 0 && writeOffDueAmount > 0) {

                    clone = getCshWriteOff(iRequest, clone, transaction, receiptAllocationAmount, writeOffType);

                    //设置核销本金 和 核销利息
                    //如何核销金额小于待核销金额   优先核销利息 再核销本金
                    if (writeOff.getWriteOffInterest() >= receiptAllocationAmount) {
                        clone.setWriteOffInterest(receiptAllocationAmount);
                        clone.setWriteOffPrincipal(0.0);
                        writeOff.setWriteOffInterest(sub(writeOff.getWriteOffInterest(),receiptAllocationAmount));
                    } else {
                        clone.setWriteOffPrincipal(sub(receiptAllocationAmount, clone.getWriteOffInterest(), 2));
                        writeOff.setWriteOffInterest(0.0);
                        writeOff.setWriteOffPrincipal(sub(writeOff.getWriteOffPrincipal(),clone.getWriteOffPrincipal()));
                    }
                    hlsCusCshWriteOffs.add(clone);
                    //收款剩余未核销金额  逐步减少
                    transaction.setAllocationAmount(0.0);
                    //债权剩余待核销金额 逐步增长
                    writeOff.setAllocationAmount(add(writeOff.getAllocationAmount(), receiptAllocationAmount, 2));
                }
            }

            //核销为  预收款
//            if (nvl(transaction.getAdvanceReceiptAmount(), 0.0).compareTo(0.0) == 1) {
            if (nvl(transaction.getWriteOffDueAmount(), 0.0).compareTo(0.0) == 1) {
                HlsCusCshWriteOff advanceWriteOff = new HlsCusCshWriteOff();
                advanceWriteOff = getCshWriteOff(iRequest, advanceWriteOff, transaction, transaction.getAdvanceReceiptAmount(), WRITE_OFF_TYPE_RECEIPT_ADVANCE_RECEIPT);
                hlsCusCshWriteOffs.add(advanceWriteOff);
            }

            HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
            cshTransaction.setTransactionId(transaction.getTransactionId());
            cshTransaction.setContractId(transaction.getContractId());
            cshTransactionService.updateByPrimaryKeySelective(iRequest, cshTransaction);
        }

        self().writeOff(iRequest, hlsCusCshWriteOffs, session);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        //如果AllocationId 没有值 插入 ，否则更新
        Long allocationId = cshTransactionList.get(0).getAllocationId();
        if (allocationId == null) {

            //插入 分配相关表
            CshAllocation cshAllocation = new CshAllocation();
            cshAllocation.setAllocationNumber(codingRuleValuesService.getCodeRuleValue(iRequest, "CSH_TRX",
                    "ALLOCATION", "ALLOCATION", null));
            cshAllocation.setAllocationDate(df.parse(df.format(new Date())));
            cshAllocation.setAllocationSource(cshTransactionList.get(0).getAllocationSource());
            cshAllocation.setAllocationStatus("Y");
            cshAllocationService.insertSelective(iRequest, cshAllocation);

            for (HlsCusCshTransaction transaction : cshTransactionList) {
                CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
                cshAllocationReceipt.setAllocationId(cshAllocation.getAllocationId());
                cshAllocationReceipt.setTransactionId(transaction.getTransactionId());
                cshAllocationReceipt.setAdvanceReceiptAmount(transaction.getAdvanceReceiptAmount());
                cshAllocationReceiptService.insertSelective(iRequest, cshAllocationReceipt);
            }

            for (HlsCusCshWriteOff writeOff : cshWriteOffList) {
                CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
                cshAllocationCredit.setAllocationId(cshAllocation.getAllocationId());
                cshAllocationCredit.setCashflowId(writeOff.getCashflowId());
                cshAllocationCredit.setDueAmount(writeOff.getWriteOffDueAmount());
                cshAllocationCredit.setPrincipal(writeOff.getWriteOffPrincipal());
                cshAllocationCredit.setInterest(writeOff.getWriteOffInterest());
                //二期功能：新增到账形式字段
                cshAllocationCredit.setArriveModality(writeOff.getArriveModality());
                cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
            }
        } else {
            CshAllocation cshAllocation = new CshAllocation();
            cshAllocation.setAllocationStatus("Y");
            cshAllocation.setAllocationId(allocationId);
            cshAllocationService.updateByPrimaryKeySelective(iRequest, cshAllocation);

            //删除后 重新插入
            /*CshAllocationReceipt allocationReceipt = new CshAllocationReceipt();
            allocationReceipt.setAllocationId(allocationId);
            cshAllocationReceiptService.batchDelete(cshAllocationReceiptMapper.select(allocationReceipt));

            CshAllocationCredit allocationCredit = new CshAllocationCredit();
            allocationCredit.setAllocationId(allocationId);
            cshAllocationCreditService.batchDelete(cshAllocationCreditMapper.select(allocationCredit));

            for (HlsCusCshTransaction transaction : cshTransactionList) {
                CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
                cshAllocationReceipt.setAllocationId(allocationId);
                cshAllocationReceipt.setTransactionId(transaction.getTransactionId());
                cshAllocationReceipt.setAdvanceReceiptAmount(transaction.getAdvanceReceiptAmount());
                cshAllocationReceiptService.insertSelective(iRequest, cshAllocationReceipt);
            }

            if (CREDIT.equals(writeOffTypeFlag)){
                for (HlsCusCshWriteOff writeOff : cshWriteOffList) {
                    CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
                    cshAllocationCredit.setAllocationId(allocationId);
                    cshAllocationCredit.setCashflowId(writeOff.getCashflowId());
                    cshAllocationCredit.setDueAmount(writeOff.getWriteOffDueAmount());
                    cshAllocationCredit.setPrincipal(writeOff.getWriteOffPrincipal());
                    cshAllocationCredit.setInterest(writeOff.getWriteOffInterest());
                    //二期功能：新增到账形式字段
                    cshAllocationCredit.setArriveModality(writeOff.getArriveModality());
                    cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
                }
            }*/

            //二期功能：核销为保证金处理逻辑
            /*if(CollectionUtils.isNotEmpty(allocationDepositList)){
                for (CshAllocationDeposit deposit : allocationDepositList){
                    depositService.updateByPrimaryKeySelective(iRequest, deposit);
                }
            }*/
        }

    }

    /**
     * 二期功能：核销为保证金后，对原有对合同现金流进行保证金补足
     * @param iRequest
     * @param allocationDepositList
     */
    private void makeUpTheContractDeposit(IRequest iRequest, List<CshAllocationDeposit> allocationDepositList) {
        if (CollectionUtils.isNotEmpty(allocationDepositList)){
            for (CshAllocationDeposit deposit : allocationDepositList){
                HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
                contractCashflow.setCashflowId(deposit.getCashflowId());
                contractCashflow = hlsCusConContractCashflowService.selectByPrimaryKey(iRequest, contractCashflow);
                //保证金补足：本次核销的金额即为补足的保证金金额；保证金抵扣金额 = 保证金抵扣金额 - 本次核销金额
                if (contractCashflow.getDepositDeductAmount() != null){
                    Double depositDeductAmount = contractCashflow.getDepositDeductAmount();
                    Double writeOffDueAmount = deposit.getWriteOffDueAmount();
                    Double newDepositDeductAmount = sub(depositDeductAmount, writeOffDueAmount, 2);
                    contractCashflow.setDepositDeductAmount(newDepositDeductAmount);

                    hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest, contractCashflow);

                }
            }
        }
    }

    @Override
    public List<Long> allocationSave(IRequest iRequest, List<HlsCusCshTransaction> cshTransactionList, HttpSession session) throws Exception {
        //原收款拆分为财务与业务, 业务确认仅做保存分配
        List<Long> res = new ArrayList<>();
        cshTransactionList.stream().forEach(item -> {
            item.setUnWriteOffAmount(nvl(item.getUnWriteOffAmount(), 0.0));
            item.setAdvanceReceiptAmount(nvl(item.getAdvanceReceiptAmount(), 0.0));
        });

        //核销类型标识：CREDIT, ADVANCE, DEPOSIT
        String transactionTypeFlag = null;

        //金额校验 剩余可核销金额汇总（减去了预收款部分） >= 本次核销金额汇总
        //剩余可核销金额汇总
        Double unWriteOffAmountTotal = round(cshTransactionList.stream().collect(Collectors.summingDouble(HlsCusCshTransaction::getUnWriteOffAmount)), 2);
        //预核销金额汇总
        Double advanceReceiptAmountTotal = round(cshTransactionList.stream().collect(Collectors.summingDouble(HlsCusCshTransaction::getAdvanceReceiptAmount)), 2);
        //本次核销金额汇总
        List<HlsCusCshWriteOff> cshWriteOffList = cshTransactionList.get(0).getCshWriteOffList();
        Double canWriteOffAmountTotal = 0D;
        String transactionType = TRANSACTION_TYPE_RECEIPT;
        if (CollectionUtils.isNotEmpty(cshWriteOffList)){
            canWriteOffAmountTotal = round(cshWriteOffList.stream().collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount)), 2);
            transactionTypeFlag = "CREDIT";

            if (unWriteOffAmountTotal.compareTo(add(canWriteOffAmountTotal, advanceReceiptAmountTotal)) != 0 && TRANSACTION_TYPE_RECEIPT.equals(transactionType)) {
                throw new ResMessageException("收款金额与待核销金额不一致！");
            }

            for (HlsCusCshTransaction transaction : cshTransactionList) {
                HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
                cshTransaction.setTransactionId(transaction.getTransactionId());
                cshTransaction.setContractId(transaction.getContractId());
                cshTransactionService.updateByPrimaryKeySelective(iRequest, cshTransaction);
            }
        }


        //二期增加：核销为预收款处理
        List<CshAllocationAdvance> allocationAdvanceList = cshTransactionList.get(0).getCshAllocationAdvanceList();
        Double allocationAdvanceWriteOffAmountTotal = 0D;
        if (CollectionUtils.isNotEmpty(allocationAdvanceList)){
            allocationAdvanceWriteOffAmountTotal = round(allocationAdvanceList.stream().collect(Collectors.summingDouble(CshAllocationAdvance::getWriteOffDueAmount)), 2);
            transactionTypeFlag = "ADVANCE";

            if (unWriteOffAmountTotal.compareTo(allocationAdvanceWriteOffAmountTotal) != 0) {
                throw new ResMessageException("剩余可核销金额与待核销金额不一致！");
            }
        }

        //2023-01-03 二期增加：核销为保证金处理
        List<CshAllocationDeposit> allocationDepositList = cshTransactionList.get(0).getCshAllocationDepositList();
        Double allocationDepositWriteOffAmountTotal = 0D;
        if (CollectionUtils.isNotEmpty(allocationDepositList)){
            allocationDepositWriteOffAmountTotal = round(allocationDepositList.stream().collect(Collectors.summingDouble(CshAllocationDeposit::getWriteOffDueAmount)), 2);
            transactionTypeFlag = "DEPOSIT";

            if (unWriteOffAmountTotal.compareTo(allocationDepositWriteOffAmountTotal) != 0) {
                throw new ResMessageException("剩余可核销金额与待核销金额不一致！");
            }
        }



        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        //如果AllocationId 没有值 插入 ，否则更新
        Long allocationId = cshTransactionList.get(0).getAllocationId();
        if (allocationId == null) {

            //插入 分配相关表
            CshAllocation cshAllocation = new CshAllocation();
            cshAllocation.setAllocationNumber(codingRuleValuesService.getCodeRuleValue(iRequest, "CSH_TRX",
                    "ALLOCATION", "ALLOCATION", null));
            cshAllocation.setAllocationDate(df.parse(df.format(new Date())));
            cshAllocation.setAllocationSource(cshTransactionList.get(0).getAllocationSource());
            if ("ADVANCE".equals(transactionTypeFlag)){
                //二期功能：核销为预收款不需要财务确认
                cshAllocation.setAllocationStatus("Y");
            }else {
                cshAllocation.setAllocationStatus("N");
            }
            cshAllocationService.insertSelective(iRequest, cshAllocation);
            res.add(cshAllocation.getAllocationId());
            for (HlsCusCshTransaction transaction : cshTransactionList) {
                CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
                cshAllocationReceipt.setAllocationId(cshAllocation.getAllocationId());
                cshAllocationReceipt.setTransactionId(transaction.getTransactionId());
                cshAllocationReceipt.setAdvanceReceiptAmount(transaction.getAdvanceReceiptAmount());
                cshAllocationReceiptService.insertSelective(iRequest, cshAllocationReceipt);
            }

            for (HlsCusCshWriteOff writeOff : cshWriteOffList) {
                CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
                cshAllocationCredit.setAllocationId(cshAllocation.getAllocationId());
                cshAllocationCredit.setCashflowId(writeOff.getCashflowId());
                cshAllocationCredit.setDueAmount(writeOff.getWriteOffDueAmount());
                cshAllocationCredit.setPrincipal(writeOff.getWriteOffPrincipal());
                cshAllocationCredit.setInterest(writeOff.getWriteOffInterest());
                //二期功能：新增到账形式字段
                cshAllocationCredit.setArriveModality(writeOff.getArriveModality());
                cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
            }
            //二期增加：核销为预收款处理
            if ("ADVANCE".equals(transactionTypeFlag)){
                handleAllocationAdvance(iRequest, allocationAdvanceList, cshAllocation, cshTransactionList);
            }

            //2023-01-03 二期增加：核销为保证金处理
            /*if ("DEPOSIT".equals(transactionTypeFlag)){
                handleAllocationDeposit(iRequest, allocationDepositList, cshAllocation);
            }*/
        } else {

            CshAllocation cshAllocation = new CshAllocation();
            cshAllocation.setAllocationStatus("N");
            cshAllocation.setAllocationId(allocationId);
            cshAllocationService.updateByPrimaryKeySelective(iRequest, cshAllocation);

            //删除后 重新插入
            CshAllocationReceipt allocationReceipt = new CshAllocationReceipt();
            allocationReceipt.setAllocationId(allocationId);
            cshAllocationReceiptService.batchDelete(cshAllocationReceiptMapper.select(allocationReceipt));

            CshAllocationCredit allocationCredit = new CshAllocationCredit();
            allocationCredit.setAllocationId(allocationId);
            cshAllocationCreditService.batchDelete(cshAllocationCreditMapper.select(allocationCredit));

            for (HlsCusCshTransaction transaction : cshTransactionList) {
                CshAllocationReceipt cshAllocationReceipt = new CshAllocationReceipt();
                cshAllocationReceipt.setAllocationId(allocationId);
                cshAllocationReceipt.setTransactionId(transaction.getTransactionId());
                cshAllocationReceipt.setAdvanceReceiptAmount(transaction.getAdvanceReceiptAmount());
                cshAllocationReceiptService.insertSelective(iRequest, cshAllocationReceipt);
            }

            for (HlsCusCshWriteOff writeOff : cshWriteOffList) {
                CshAllocationCredit cshAllocationCredit = new CshAllocationCredit();
                cshAllocationCredit.setAllocationId(allocationId);
                cshAllocationCredit.setCashflowId(writeOff.getCashflowId());
                cshAllocationCredit.setDueAmount(writeOff.getWriteOffDueAmount());
                cshAllocationCredit.setPrincipal(writeOff.getWriteOffPrincipal());
                cshAllocationCredit.setInterest(writeOff.getWriteOffInterest());
                //二期功能：新增到账形式字段
                cshAllocationCredit.setArriveModality(writeOff.getArriveModality());
                cshAllocationCreditService.insertSelective(iRequest, cshAllocationCredit);
            }

            //二期功能：核销为保证金处理逻辑
            if (CollectionUtils.isNotEmpty(allocationDepositList)){
                for (CshAllocationDeposit deposit : allocationDepositList){
                    depositService.updateByPrimaryKeySelective(iRequest, deposit);
                }
            }

        }
        return res;
    }


    /**
     * 2023-01-03 二期增加：核销为保证金处理
     * @param iRequest
     * @param allocationDepositList
     * @param cshAllocation
     */
    private void handleAllocationDeposit(IRequest iRequest, List<CshAllocationDeposit> allocationDepositList, CshAllocation cshAllocation){
        for (CshAllocationDeposit deposit : allocationDepositList) {
            //插入保证金表中
            deposit.setAllocationId(cshAllocation.getAllocationId());
            depositService.insert(iRequest, deposit);
        }

    }

    /**
     * 二期新增：核销为预收款处理
     * @param iRequest
     * @param allocationAdvanceList
     * @param cshAllocation
     * @param cshTransactionList
     */
    private void handleAllocationAdvance(IRequest iRequest, List<CshAllocationAdvance> allocationAdvanceList, CshAllocation cshAllocation, List<HlsCusCshTransaction> cshTransactionList){

        for (CshAllocationAdvance advance : allocationAdvanceList) {
            //1.插入表中
            CshAllocationAdvance allocationAdvance = new CshAllocationAdvance();
            allocationAdvance.setAllocationId(cshAllocation.getAllocationId());
            allocationAdvance.setBpId(advance.getBpId());
            allocationAdvance.setBpName(advance.getBpName());
            allocationAdvance.setWriteOffDueAmount(advance.getWriteOffDueAmount());
            allocationAdvance.setWriteOffType(advance.getWriteOffType());
            allocationAdvance.setContractId(advance.getContractId());
            allocationAdvance.setCashflowId(advance.getCashflowId());
            advanceService.insertSelective(iRequest, allocationAdvance);

            //修改现金流已收代偿金额
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            conContractCashflow.setWriteOffFlag("FULL");
            conContractCashflow.setCashflowId(advance.getCashflowId());
            conContractCashflow.setReceivedCompAmount(advance.getWriteOffDueAmount());
            hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,conContractCashflow);

            //2.新增一条预收款记录写入csh_transaction表中
            //TODO 赋值确定
            for (HlsCusCshTransaction cusCshTransaction : cshTransactionList){
                HlsCusCshTransaction insertCshTransaction = new HlsCusCshTransaction();

                insertCshTransaction.setTransactionCategory(cusCshTransaction.getTransactionCategory());
                insertCshTransaction.setTransactionType(TRANSACTION_TYPE_ADVANCE_RECEIPT);
                insertCshTransaction.setBusinessType(TRANSACTION_TYPE_ADVANCE_RECEIPT);
                insertCshTransaction.setTransactionDate(cusCshTransaction.getTransactionDate());
                insertCshTransaction.setCompanyId(cusCshTransaction.getCompanyId());
                insertCshTransaction.setTransactionAmount(cusCshTransaction.getTransactionAmount()-nvl(cusCshTransaction.getWriteOffAmount(),0.0));
                insertCshTransaction.setCurrencyCode(cusCshTransaction.getCurrencyCode());
                insertCshTransaction.setPaymentMethod(cusCshTransaction.getPaymentMethod());
                insertCshTransaction.setBankAccountId(cusCshTransaction.getBankAccountId());
                insertCshTransaction.setBpId(advance.getBpId());
                insertCshTransaction.setBpBankAccountId(cusCshTransaction.getBpBankAccountId());
                insertCshTransaction.setBpBankName(cusCshTransaction.getBpBankName());
                insertCshTransaction.setBpBankBranchName(cusCshTransaction.getBpBankBranchName());
                insertCshTransaction.setBpBankAccountNum(cusCshTransaction.getBpBankAccountNum());
                insertCshTransaction.setBpBankAccountName(cusCshTransaction.getBpBankAccountName());
                insertCshTransaction.setReversedFlag("N");
                insertCshTransaction.setPostedFlag("N");
                insertCshTransaction.setDescription(cusCshTransaction.getDescription());
                insertCshTransaction.setSourceDocCategory("ALLOCATION_MANUAL");
                insertCshTransaction.setSourceDocId(allocationAdvance.getAllocationId());
                insertCshTransaction.setSourceDocLineId(allocationAdvance.getAdvanceId());
                insertCshTransaction.setWriteOffFlag("NOT");
                insertCshTransaction.setSourceTransactionId(cusCshTransaction.getTransactionId());
                insertCshTransaction.setComments(cusCshTransaction.getComments());
                //收款编号
                getTransactionNumAdvance(iRequest, insertCshTransaction);
                //写入现金事务表
                cshTransactionService.insertSelective(iRequest, insertCshTransaction);
                //更新原有的现金事务记录为完全核销
                HlsCusCshTransaction updateCshTransaction = new HlsCusCshTransaction();
                updateCshTransaction.setTransactionId(cusCshTransaction.getTransactionId());
                updateCshTransaction = cshTransactionService.selectByPrimaryKey(iRequest, updateCshTransaction);
                updateCshTransaction.setWriteOffFlag("FULL");
                cshTransactionService.updateByPrimaryKeySelective(iRequest, updateCshTransaction);

                //收款核销为预收款
                AbstractJeTrxService transactionJeTrxService = jeTrxCommonService.map.get("CSH_TRANSACTION");
                Map transactionParams = new HashMap<>();
                transactionParams.put("jeTrxId", cusCshTransaction.getTransactionId());
                transactionParams.put("companyId", iRequest.getCompanyId());
                if (insertCshTransaction.getContractId() != null) {
                    transactionParams.put("jeSourceId", insertCshTransaction.getContractId());
                    transactionParams.put("jeSourceDoc", "CON_CONTRACT");
                }
                transactionJeTrxService.process(iRequest, transactionParams);
            }
        }
    }

    private static final String WRITEOFF_TYPE_PAYMENT_DEBT = "PAYMENT_DEBT";
    private static final String WRITEOFF_TYPE_DEDUCTION = "DEDUCTION";
    private static final String WRITEOFF_TYPE_RECEIPT_CREDIT = "RECEIPT_CREDIT";

    private static final String WRITEOFF_TYPE_REFUND = "REFUND";

    private static final String DOCUMENT_TYPE_FCT_PAYMENT_REQ = "FCT_PAYMENT_REQ";
    private static final String WRITEOFF_TYPE_FCT_PAYMENT_DEBT = "FCT_PAYMENT_DEBT";
    private static final String WRITEOFF_DOC_CATEGORY_FCT_CONTRACT = "FCT_CONTRACT";
    private static final Long FCT_CONTRACT_WITHDRAW_CF_ITEM_40 = 40L;
    private static final String WRITEOFF_DOC_CATEGORY_CON_CONTRACT = "CON_CONTRACT";
    private static final String JE_CON_CONTRACT = "CON_CONTRACT";


    @Autowired
    private HlsCusFctContractWithdrawCfMapper fctContractWithdrawCfMapper;
    private static Logger logger = LoggerFactory.getLogger(CshWriteOffService.class);


    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationPaymentService hlsCusPrjQuotationPaymentService;
    @Autowired
    private IContractFinanceIncomeService iContractFinanceIncomeService;
    @Autowired
    private INoticeManageService noticeService;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private IConContractCashflowService conContractCashflowService;

    @Override
    /**
     * @Discription:付款支付
     * @param: [iRequest, hlsCusCshPaymentReqLnList, session]
     * @return: void
     */
    public void payment(IRequest iRequest, HlsCusCshPaymentTran hlsCusCshPaymentTran, HttpSession session) throws Exception {

        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        Long paymentReqId = hlsCusCshPaymentTran.getPaymentReqId();
        hlsCusCshPaymentReqHd.setPaymentReqId(paymentReqId);
        hlsCusCshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(iRequest, hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = hlsCusCshPaymentReqHdMapper.retailPaymentQuery(hlsCusCshPaymentReqHd).get(0);
        hlsCusCshPaymentReqHd.setActualPayDate(hlsCusCshPaymentTran.getTransactionDate());
        CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount = new CshPaymentReqLnBankAccount();
        cshPaymentReqLnBankAccount.setPaymentReqId(paymentReqId);
        List<CshPaymentReqLnBankAccount> reqLnBankAccounts = cshPaymentReqLnBankAccountMapper.selectCshPaymentReqLnBankAccount(cshPaymentReqLnBankAccount);
        for (int i = 0; i <reqLnBankAccounts.size() ; i++) {
            cshPaymentReqLnBankAccount.setCshBankId(reqLnBankAccounts.get(i).getCshBankId());
            cshPaymentReqLnBankAccount.setPaymentAmount(reqLnBankAccounts.get(i).getActualPaymentAmount());
            cshPaymentReqLnBankAccount.setActualPaymentDate(hlsCusCshPaymentTran.getTransactionDate());
            cshPaymentReqLnBankAccountMapper.updateByPrimaryKeySelective(cshPaymentReqLnBankAccount);
        }


        Double paymentAmount =  0D;
        Double compareAmount = 0D;
        if (BUSINESS_FLAG_RETAIL.equals(hlsCusCshPaymentTran.getBusinessFlag())){
            //二期功能：零售业务
            paymentAmount =  hlsCusCshPaymentReqHdMapper.queryRetailPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId());
            compareAmount = hlsCusCshPaymentReqHd.getAfterDeductAmount();
        }else {
            paymentAmount =  hlsCusCshPaymentReqHdMapper.queryPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId());
            compareAmount = hlsCusCshPaymentReqHd.getAfterDeductAmount();
        }

        if(compareAmount > paymentAmount){
            hlsCusCshPaymentReqHd.setPaymentApprovedStatus("PART_PAID");
            hlsCusCshPaymentReqHd.setPaymentStatus("PART_PAID");
        }else if(compareAmount.compareTo(paymentAmount) == 0 ){
            // 完全核销
            hlsCusCshPaymentReqHd.setPaymentApprovedStatus("PAID");
            hlsCusCshPaymentReqHd.setPaymentStatus("PAID");

            if (BUSINESS_FLAG_RETAIL.equals(hlsCusCshPaymentTran.getBusinessFlag())){
                //二期零售业务核销逻辑调整
                hlsCusCshPaymentReqHd.setBusinessFlag(hlsCusCshPaymentTran.getBusinessFlag());
                HlsCusCshPaymentReqLn cshPaymentReqLn = new HlsCusCshPaymentReqLn();
                cshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
                List<HlsCusCshPaymentReqLn> cshPaymentReqLnList = cshPaymentReqLnMapper.select(cshPaymentReqLn);
                Long contractID=null;
                for (HlsCusCshPaymentReqLn reqLn:cshPaymentReqLnList) {
                    //二期功能：因为一期原有的payment()方法是按申请头上的SourceContractId进行处理的，而二期功能的SourceContractId值
                    //        存放在行上，因此这里临时把行上的ContractId赋值到头上，处理完后记得要把SourceContractId的值设置为空，否则
                    //        零售业务的查询条件就会因为头上SourceContractId有值而查不出来数据
                    hlsCusCshPaymentReqHd.setSourceContractId(reqLn.getSourceDocId());
                    retailPayment(iRequest, hlsCusCshPaymentReqHd,reqLn);
                    contractID=reqLn.getSourceDocId();
                }
                //二期功能：处理完后记得要把SourceContractId的值设置为空，否则
                //         零售业务的查询条件就会因为头上SourceContractId有值而查不出来数据
                hlsCusCshPaymentReqHd.setSourceContractId(null);
                //二期功能：起租类型为"放款即起租”的零售合同，根据“实际支付日期”更改来调整合同现金流的“支付日期”所有期次的支付时间；
                // 同时更新合同状态为 起租
                updateDueDateByActualPayDate(iRequest, hlsCusCshPaymentReqHd);

                //零售业务付款支付后同步流水
                if("PAYMENT".equals(hlsCusCshPaymentReqHd.getPaymentType())) {
                    financeBaseUtils.paymentFlowItfc(iRequest, contractID);
                }
            }else {
                //核销
                payment(iRequest, hlsCusCshPaymentReqHd);
            }

        }else {
            throw new BeyondAmountLimitException("实际支付信息的付款金额大于本次付款总额！");
        }

        Double actualPayment=  0D;
        if (BUSINESS_FLAG_RETAIL.equals(hlsCusCshPaymentTran.getBusinessFlag())){
            //二期功能：零售业务
            actualPayment=  hlsCusCshPaymentReqHdMapper.queryRetailActualPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId());
        }else {
            actualPayment=  hlsCusCshPaymentReqHdMapper.queryActualPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId());
        }
        hlsCusCshPaymentReqHd.setActualHdPayAmount(actualPayment);
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqHd);

        //如果完全支付，则调用消息通知
        if("PAID".equals(hlsCusCshPaymentReqHd.getPaymentStatus())){
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
            HlsCusConContract hlsCusConContract1 = hlsCusConContractService.selectByPrimaryKey(iRequest, hlsCusConContract);
            //调用还款计划生成通知
            messageNoticeService.repayPlanCreatedNotify(hlsCusConContract1.getProjectId(),iRequest);
            //调用放款结果通知
            messageNoticeService.orderLoanResult(hlsCusConContract1.getProjectId(),iRequest);
        }

    }

    /**
     * 二期功能：退款申请支付
     *
     * @param iRequest
     * @param hlsCusCshPaymentTran
     * @param session
     * @throws Exception
     */
    @Override
    public void refundPayment(IRequest iRequest, HlsCusCshPaymentTran hlsCusCshPaymentTran, HttpSession session) throws Exception {
        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        Long refundId = hlsCusCshPaymentTran.getRefundId();
        transactionRefund.setRefundId(refundId);
        transactionRefund = cshTransactionRefundService.selectByPrimaryKey(iRequest, transactionRefund);
        transactionRefund.setActualPaymentDate(hlsCusCshPaymentTran.getTransactionDate());
        CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount1 = new CshPaymentReqLnBankAccount();
        cshPaymentReqLnBankAccount1.setRefundId(refundId);
        List<CshPaymentReqLnBankAccount> reqLnBankAccounts = cshPaymentReqLnBankAccountMapper.selectPaymentReqLnBankAccount(cshPaymentReqLnBankAccount1);
        for (int i = 0; i <reqLnBankAccounts.size() ; i++) {
            cshPaymentReqLnBankAccount1.setCshBankId(reqLnBankAccounts.get(i).getCshBankId());
            cshPaymentReqLnBankAccount1.setPaymentAmount(reqLnBankAccounts.get(i).getActualPaymentAmount());
            cshPaymentReqLnBankAccount1.setActualPaymentDate(hlsCusCshPaymentTran.getTransactionDate());
            cshPaymentReqLnBankAccountMapper.updateByPrimaryKeySelective(cshPaymentReqLnBankAccount1);
        }

        Double paymentAmount  = cshPaymentReqLnBankAccountMapper.queryRefundPaymentAmount(refundId);
        if (paymentAmount < transactionRefund.getRefundAmount()){
            //TODO 实际付款小于现金事务上的金额时，处理逻辑

        }else if(transactionRefund.getRefundAmount().compareTo(paymentAmount)  == 0){
            //修改申请单状态 PAID
            transactionRefund.setPaymentRefundStatus("PAID");

            CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount =new CshPaymentReqLnBankAccount();
            cshPaymentReqLnBankAccount.setRefundId(refundId);
            List<CshPaymentReqLnBankAccount> cshPaymentReqLnBankAccounts = cshPaymentReqLnBankAccountMapper.selectRefundCshPaymentReqLnBankAccount(cshPaymentReqLnBankAccount);


            List<HlsCusCshWriteOff> cshWriteOffList = new ArrayList<>();
            List<HlsCusCshTransaction> transactionList = new ArrayList<>();
            //对源现金事务处理
            List<HlsCusCshTransaction> sourceTransactionList = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


            for (int i = 0; i < cshPaymentReqLnBankAccounts.size(); i++) {
                //生成现金事务
                HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();

                //对源现金事务处理
                HlsCusCshTransaction sourceCshTransaction = new HlsCusCshTransaction();
                sourceCshTransaction.setTransactionId(cshPaymentReqLnBankAccounts.get(i).getSourceTransactionId());
                sourceCshTransaction.setWriteOffAmount(cshPaymentReqLnBankAccounts.get(i).getActualPaymentAmount());

                getTransactionNumRefund(iRequest, cshTransaction);

                cshTransaction.setTransactionDate(transactionRefund.getActualPaymentDate());
                cshTransaction.setPenaltyCalcDate(transactionRefund.getActualPaymentDate());
                cshTransaction.setCompanyId(iRequest.getCompanyId());
                cshTransaction.setTransactionAmount(cshPaymentReqLnBankAccounts.get(i).getActualPaymentAmount());
                cshTransaction.setCurrencyCode(cshPaymentReqLnBankAccounts.get(i).getCurrency());
                cshTransaction.setReversedFlag("N");
                cshTransaction.setPostedFlag("N");
                cshTransaction.setWriteOffFlag("NOT");


                cshTransaction.setContractId(cshPaymentReqLnBankAccounts.get(i).getSourceDocId());
                cshTransaction.setSourceDocCategory("CSH_TRANSACTION_REFUND");
                cshTransaction.setSourceDocId(cshPaymentReqLnBankAccounts.get(i).getRefundId());

                cshTransaction.setBpBankAccountName(cshPaymentReqLnBankAccounts.get(i).getCshBankAccountName());
                cshTransaction.setBpBankAccountNum(cshPaymentReqLnBankAccounts.get(i).getCshBankAccountNum());
                cshTransaction.setBpBankAccountId(cshPaymentReqLnBankAccounts.get(i).getCshBankAccountId());
                cshTransaction.setSourceDocLineId(cshPaymentReqLnBankAccounts.get(i).getCshBankId());


                cshTransaction = cshTransactionService.insertSelective(iRequest, cshTransaction);
                transactionList.add(cshTransaction);
                sourceTransactionList.add(sourceCshTransaction);

                //构造核销数据
                HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
                cshWriteOff.setCompanyId(iRequest.getCompanyId());
                cshWriteOff.setContractId(cshPaymentReqLnBankAccounts.get(i).getSourceDocId());

                //现金事务ID记录发起退款申请的现金事务ID
                cshWriteOff.setCshTransactionId(cshPaymentReqLnBankAccounts.get(i).getSourceTransactionId());

                //后续现金事务ID记录新生成的现金事务ID
                cshWriteOff.setSubsequentCshTrxId(cshTransaction.getTransactionId());


                cshWriteOff.setWriteOffType(WRITEOFF_TYPE_REFUND);
                cshWriteOff.setWriteOffDocCategory(WRITEOFF_DOC_CATEGORY_CON_CONTRACT);


                cshWriteOff.setWriteOffDate(sdf.parse(sdf.format(cshPaymentReqLnBankAccounts.get(i).getActualPaymentDate())));
                cshWriteOff.setCshWriteOffAmount(cshPaymentReqLnBankAccounts.get(i).getActualPaymentAmount());
                cshWriteOff.setReversedFlag("N");
                cshWriteOff.setCurrencyCode(cshPaymentReqLnBankAccounts.get(i).getCurrency());

                cshWriteOff.setWriteOffDueAmount(cshPaymentReqLnBankAccounts.get(i).getActualPaymentAmount());

                cshWriteOffList.add(cshWriteOff);
            }

            List<HlsCusCshWriteOff> hlsCusCshWriteOffLists = mainCshWriteOffService.cshWriteOffMainPay(iRequest, cshWriteOffList);

            //2023-02-13 对发起申请的现金事务进行处理：核销标记/核销金额/冻结金额
            if (CollectionUtils.isNotEmpty(sourceTransactionList)){
                for (HlsCusCshTransaction t : sourceTransactionList) {
                    HlsCusCshTransaction updateCshTransaction = hlsCusCshTransactionService.selectByPrimaryKey(iRequest, t);
                    updateCshTransaction.setWriteOffFlag("FULL");
                    updateCshTransaction.setWriteOffAmount(t.getWriteOffAmount());
                    updateCshTransaction.setBlockAmount(0D);

                    hlsCusCshTransactionService.updateByPrimaryKeySelective(iRequest, updateCshTransaction);

                }
            }


            //现金事务明细 ，核销数据 出凭证
            //jeTrxCshTransactionPayment(transactionList, hlsCusCshPaymentReqHd.getSourceContractId(),paymentReqId);
            //现金事务明细 插凭证流水表
            for (HlsCusCshTransaction cshTransaction : transactionList) {
                Map map = new HashMap<>();
                map.put("jeTrxId", cshTransaction.getTransactionId());
                map.put("companyId", cshTransaction.getCompanyId());
                if (cshTransaction.getContractId() != null){
                    map.put("contractId", cshTransaction.getContractId());
                    map.put("jeSourceDoc", JE_CON_CONTRACT);
                    map.put("jeSourceId", cshTransaction.getContractId());
                }

                AbstractJeTrxService transactionPaymentJeTrx = commonService.map.get("CSH_TRANSACTION_PAYMENT");
                transactionPaymentJeTrx.process(iRequest, map);
            }

            //jeTrxPayEqipment(hlsCusCshWriteOffLists, hlsCusCshPaymentReqHd.getSourceContractId());
            //核销数据 插凭证流水表
            for (HlsCusCshWriteOff cusCshWriteOff : hlsCusCshWriteOffLists) {
                Map params = new HashMap<>();
                params.put("jeTrxId", cusCshWriteOff.getWriteOffId());
                params.put("companyId", cusCshWriteOff.getCompanyId());
                if (cusCshWriteOff.getContractId() != null){
                    params.put("contractId", cusCshWriteOff.getContractId());
                    params.put("jeSourceDoc", JE_CON_CONTRACT);
                    params.put("jeSourceId", cusCshWriteOff.getContractId());
                }

                AbstractJeTrxService paymentEqimentJeTrx = commonService.map.get("DEPOSIT_REFUND");
                paymentEqimentJeTrx.process(iRequest, params);


            }
        }

        //更新申请单
        cshTransactionRefundService.updateByPrimaryKeySelective(iRequest, transactionRefund);
        //更新退款行表
        /*CshTransactionRefundLn cshTransactionRefundLn = new CshTransactionRefundLn();
        cshTransactionRefundLn.setRefundId(refundId);
        cshTransactionRefundLn.setBlockAmount();
        cshTransactionRefundLn.setCanReturnAmount();
        cshTransactionRefundLnMapper.updatePaymentAmount();*/
        //更新银行流水行表
        CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount = new CshPaymentReqLnBankAccount();
        cshPaymentReqLnBankAccount.setRefundId(refundId);
        cshPaymentReqLnBankAccount.setPaymentStatus("PAID");
        cshPaymentReqLnBankAccountMapper.updateCshPaymentReqLnBankAccountByLn(cshPaymentReqLnBankAccount);

    }

    /**
     * 二期功能：起租类型为"放款即起租”的零售合同，根据“实际支付日期”更改来调整合同现金流的“支付日期”所有期次的支付时间
     *
     * @param iRequest
     * @param hlsCusCshPaymentReqHd
     */
    private void updateDueDateByActualPayDate(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) {

        //实际支付日期
        Date actualPayDate = hlsCusCshPaymentReqHd.getActualPayDate();
        HlsCusCshPaymentReqLn cshPaymentReqLn = new HlsCusCshPaymentReqLn();
        cshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        List<HlsCusCshPaymentReqLn> cshPaymentReqLnList = cshPaymentReqLnMapper.select(cshPaymentReqLn);
        for (HlsCusCshPaymentReqLn reqLine : cshPaymentReqLnList) {
            Long contractId = reqLine.getSourceDocId();
            //获取合同信息
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(contractId);
            hlsCusConContract = conContractMapper.selectByPrimaryKey(hlsCusConContract);


            //判断放款即起租的零售合同:合同上的合作方（manufacturerId）所维护的起租规则
            HlsBpMasterInceptRule bpMasterInceptRule = new HlsBpMasterInceptRule();
            bpMasterInceptRule.setBpId(hlsCusConContract.getManufacturerId());
            bpMasterInceptRule.setEnabledFlag("Y");
            bpMasterInceptRule.setInceptRuleType("LOAN_DATE");
            List<HlsBpMasterInceptRule> bpMasterInceptRuleList = bpMasterInceptRuleMapper.select(bpMasterInceptRule);
            boolean loanFlag = CollectionUtils.isNotEmpty(bpMasterInceptRuleList) && bpMasterInceptRuleList.size() == 1;
            //判断起租类型为"放款即起租”的零售合同
            // 10.19修改 所有零售合同都会刷新起租日期
            if (hlsCusConContract.getContractNumber().startsWith("PRJ") && loanFlag){
                //获取报价的收租间隔月份及租期
                HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setQuotationId(hlsCusConContract.getQuotationId());
                hlsCusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
                //还租频率
                String rentingFrequency = hlsCusPrjQuotation.getRentingFrequency();
                if (StringUtils.isEmpty(rentingFrequency)){
                    return;
                }
                double rentingFrequencyDouble = Double.parseDouble(rentingFrequency);
                //租赁期限（月）
                String leaseTermM = hlsCusPrjQuotation.getLeaseTermM();
                if (StringUtils.isEmpty(leaseTermM)){
                    return;
                }
                double leaseTermMDouble = Double.parseDouble(leaseTermM);
                //总期数 = 租赁期限（月）/ 还租频率
                int period = (int) (leaseTermMDouble / rentingFrequencyDouble);
                //获取合同现金流
                List<HlsCusConContractCashflow> conContractCashflowList = hlsCusConContractCashflowMapper.
                        selectConContractCashFlowByContractIdAndQuotationId(hlsCusConContract.getContractId(),
                                hlsCusConContract.getQuotationId());
                List<HlsCusConContractCashflow> updateConContractCashflowList = new ArrayList<>();

                logger.info("period: {}", period);
                for (int i = 0; i <= period; i++) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(actualPayDate);
                    //按频率计算新的支付日期并赋值
                    c.add(Calendar.MONTH, (int) (i * rentingFrequencyDouble));

                    logger.info("newDueDate: {}", c.getTime());
                    //按期数去处理(一期可能有多条数据)
                    Long timeNum = (long) i;
                    List<HlsCusConContractCashflow> tempConContractCashflowList = conContractCashflowList.stream().filter(line -> timeNum.equals(line.getTimes())).collect(Collectors.toList());

                    logger.info("i: {},tempConContractCashflowList.size: {}", i, tempConContractCashflowList.size());

                    for (HlsCusConContractCashflow tempCashflow : tempConContractCashflowList) {
                        tempCashflow.setDueDate(c.getTime());
                        tempCashflow.set__status("update");
                    }
                    updateConContractCashflowList.addAll(tempConContractCashflowList);
                }
                //更新后的数据回写数据库
                if (CollectionUtils.isNotEmpty(updateConContractCashflowList)){
                    hlsCusConContractCashflowService.batchUpdate(iRequest, updateConContractCashflowList);
                }
            }
            //更新合同状态为 起租（INCEPT）
            hlsCusConContract.setContractStatus(CONTRACT_STATUS_INCEPT);
            if("10".equals(hlsCusConContract.getDepositDeduction())){
                hlsCusConContract.setDepositDeductMethod("AUTO");
            }else{
                hlsCusConContract.setDepositDeductMethod("MANUAL");
            }
            hlsCusConContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract);

            //查询最新的合同数据 起租时进行分摊计算
            HlsCusConContract ct = new HlsCusConContract();
            ct.setContractId(hlsCusConContract.getContractId());
            ct = hlsCusConContractService.selectByPrimaryKey(iRequest,ct);
            // 非零售合同才计算分摊逻辑
            String documentType = hlsCusConContract.getDocumentType();
            boolean retailFlag = IPrjProjectService.CONL.equals(documentType) || IPrjProjectService.CONLB.equals(documentType);
            if (retailFlag){
                gldContractCashflowService.clacFinanceIncomeRetail(iRequest, ct.getContractId(), ct.getVatRate(),ct.getIrr());
            }else{
                gldContractCashflowService.clacFinanceIncome(iRequest, ct.getContractId(), ct.getVatRate(),ct.getIrr());
            }
//            gldContractCashflowService.clacFinanceIncome(iRequest, ct.getContractId(), ct.getVatRate(),ct.getIrr());
            //生成合同档案记录
            contractArchiveService.saveContractArchive(iRequest, ct);
        }

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void cshWriteOffSendSap(IRequest iRequest, List<HlsCusCshWriteOff> cshWriteOffs, HttpSession session, boolean insertFlag) throws Exception {
        if (CollectionUtils.isNotEmpty(cshWriteOffs)) {
            HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
            hlsCusCshTransaction.setTransactionId(cshWriteOffs.get(0).getCshTransactionId());
            hlsCusCshTransaction = cshTransactionService.selectByPrimaryKey(iRequest, hlsCusCshTransaction);

            accuracy(cshWriteOffs);

            logger.info("validata start");
            validata(iRequest, hlsCusCshTransaction, cshWriteOffs);
            logger.info("validata end");
            //当合同是代收模式，且包含指定的现金流时，插入承租人还款信息表
            if (insertFlag) {
                for (HlsCusCshWriteOff hlsCusCshWriteOff : cshWriteOffs) {
                    if (hlsCusCshWriteOff.getContractId() != null && hlsCusCshWriteOff.getCfItem() != null
                            && ArraysUtil.contains(INSERT_CFITEMS, hlsCusCshWriteOff.getCfItem())) {
                        HlsCusConContract hlsCusConContract = new HlsCusConContract();
                        hlsCusConContract.setContractId(hlsCusCshWriteOff.getContractId());
                        hlsCusConContract = conContractMapper.selectByPrimaryKey(hlsCusConContract);
                        if (hlsCusConContract != null && "Y".equals(hlsCusConContract.getSubstituteReceiptFlag())) {
                            hlsCusCshWriteOff.setInsertFlag("Y");
                        }
                    }
                }
            }
            // 发送sap前保存收款单
            CshWriteOffSlip cshWriteOffSlip = self().writeOffSendBefore(iRequest, hlsCusCshTransaction, cshWriteOffs);
            CshWriteOffSlipLn slipLn = new CshWriteOffSlipLn();
            slipLn.setSlipId(cshWriteOffSlip.getSlipId());
//            List<CshWriteOffSlipLn> list = cshWriteOffSlipLnService.query(iRequest, slipLn, 1, 0);
//
//            // TODO: 2022/6/28 经营租赁sap改造(经营租赁不发送sap)
//            boolean sendSapFlag = checkSendSap(list);
//            InterfaceSap sap;
//            if (sendSapFlag){
//                // 数据发送到sap
//                // 此方法不会修改该对象的值
//                sap = soapInterfaceWriteOffComponent.sendSapWithWriteOff(iRequest, hlsCusCshTransaction, cshWriteOffSlip.getSlipId(), list);
//            }else {
//                sap = new InterfaceSap();
//                sap.setInterfaceStatus(HlsConstantUtil.SapInterfaceStatus.SUCCESS);
//            }
//            // 业务系统核销
//            logger.info(String.valueOf(cshWriteOffSlip.getSlipId()));
//            logger.info("before write off");
//            try {
//                self().writeOffWithOutError(iRequest, cshWriteOffSlip.getSlipId(), sap, list);
//            }catch (Exception e){
//                if (!sendSapFlag) {
//                    cshWriteOffSlipService.saveStatus(iRequest, cshWriteOffSlip.getSlipId(), HlsConstantUtil.SlipStatus.FAILURE, e.getMessage());
//                }
//                throw e;
//            }
            logger.info("after write off");
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public CshWriteOffSlip writeOffSendBefore(IRequest iRequest, HlsCusCshTransaction hlsCusCshTransaction, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {
        logger.info("writeOffSendBefore lock");
        HlsCusCshTransaction hlsCusCshTransactionLock = new HlsCusCshTransaction();
        hlsCusCshTransactionLock.setTransactionId(hlsCusCshTransaction.getTransactionId());
        hlsCusCshTransactionLock.setLastUpdateDate(new Date());
        cshTransactionService.updateByPrimaryKeySelective(iRequest,hlsCusCshTransactionLock);
        // 保存
        logger.info("writeOffSendBefore saveForSap");
//        CshWriteOffSlip cshWriteOffSlip = cshWriteOffSlipService.saveForSap(iRequest, HlsConstantUtil.SlipType.RECEIVE, cshWriteOffs);

        // 冻结金额
        logger.info("writeOffSendBefore blockAmountForWriteOffSLip");
//        blockAmountForWriteOffSLip(iRequest, cshWriteOffSlip.getTransactionId(), cshWriteOffs);

        return new CshWriteOffSlip();
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void blockAmountForWriteOffSLip(IRequest iRequest, Long transactionId, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException {
        // 冻结金额
        double blockAmount = 0D;
        // 现金流
        for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
            Long cashflowId = cshWriteOff.getCashflowId();
            if (null != cashflowId) {
                HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
                cashflow.setCashflowId(cashflowId);
                cashflow = contractCashflowService.selectByPrimaryKey(iRequest, cashflow);
                // 应收
                cashflow.setBlockAmount(MathUtil.add(
                        Optional.ofNullable(cashflow.getBlockAmount()).orElse(0D),
                        cshWriteOff.getWriteOffDueAmount(),
                        SCALE
                ));
                if (Double.compare(
                        MathUtil.round(cashflow.getDueAmount(),SCALE),
                        MathUtil.add(
                                Optional.ofNullable(cashflow.getReceivedAmount()).orElse(0D),
                                cashflow.getBlockAmount(),
                                SCALE
                        )
                ) < 0) {
                    throw new BeyondAmountLimitException("合同编号：" + cshWriteOff.getContractNumber() +
                            ",期数：" + cshWriteOff.getTimes() + ",性质：" + cshWriteOff.getCfItemN() +
                            ";<br/>核销金额，冻结金额与已核销金额之和大于应收金额!");
                }
                if (cashflow.getCfItem().equals(_1) ||  // 租金
                        cashflow.getCfItem().equals(_11) ||  // 提前结清款
                        cashflow.getCfItem().equals(_13)) {  // 回购款项
                    // 本金
                    cashflow.setBlockPrincipal(MathUtil.add(
                            Optional.ofNullable(cashflow.getBlockPrincipal()).orElse(0D),
                            Optional.ofNullable(cshWriteOff.getWriteOffPrincipal()).orElse(0D),
                            SCALE
                    ));
                    if (Double.compare(
                            MathUtil.round(Optional.ofNullable(cashflow.getPrincipal()).orElse(0D),SCALE),
                            MathUtil.add(
                                    Optional.ofNullable(cashflow.getReceivedPrincipal()).orElse(0D),
                                    cashflow.getBlockPrincipal(),
                                    SCALE
                            )
                    ) < 0) {
                        throw new BeyondAmountLimitException("合同编号：" + cshWriteOff.getContractNumber() +
                                ",期数：" + cshWriteOff.getTimes() + ",性质：" + cshWriteOff.getCfItemN() +
                                ";<br/>核销本金，冻结本金与已核销本金之和大于应收本金!");
                    }
                    // 利息
                    cashflow.setBlockInterest(MathUtil.add(
                            Optional.ofNullable(cashflow.getBlockInterest()).orElse(0D),
                            Optional.ofNullable(cshWriteOff.getWriteOffInterest()).orElse(0D),
                            SCALE
                    ));
                    if (Double.compare(
                            MathUtil.round(Optional.ofNullable(cashflow.getInterest()).orElse(0D),SCALE),
                            MathUtil.add(
                                    Optional.ofNullable(cashflow.getReceivedInterest()).orElse(0D),
                                    cashflow.getBlockInterest(),
                                    SCALE
                            )
                    ) < 0) {
                        throw new BeyondAmountLimitException("合同编号：" + cshWriteOff.getContractNumber() +
                                ",期数：" + cshWriteOff.getTimes() + ",性质：" + cshWriteOff.getCfItemN() +
                                ";<br/>核销利息，冻结利息与已核销利息之和大于应收利息!");
                    }
                }
                int count = conContractCashflowMapper.updateBlockAmount(cashflowId, cashflow.getBlockAmount(), cashflow.getBlockPrincipal(), cashflow.getBlockInterest());
                if(count != 1){
                    throw new BeyondAmountLimitException("冻结金额占用有误，请联系管理员检查!");
                }
            }
            blockAmount = MathUtil.add(blockAmount, cshWriteOff.getWriteOffDueAmount(), SCALE);
        }
        // 现金事务
        HlsCusCshTransaction transaction = new HlsCusCshTransaction();
        transaction.setTransactionId(transactionId);
        transaction = cshTransactionService.selectByPrimaryKey(iRequest, transaction);
        transaction.setBlockAmount(
                MathUtil.add(
                        Optional.ofNullable(transaction.getBlockAmount()).orElse(0D),
                        blockAmount,
                        SCALE
                )
        );
        if (Double.compare(
                MathUtil.round(transaction.getTransactionAmount(),SCALE),
                MathUtil.add(
                        Optional.ofNullable(transaction.getWriteOffAmount()).orElse(0D),
                        transaction.getBlockAmount(),
                        SCALE
                )
        ) < 0) {
            throw new BeyondAmountLimitException("现金事务编号：" + transaction.getTransactionNum() +
                    ";<br/>核销金额，冻结金额与已核销金额之和大于现金事务金额!");
        }
        cshTransactionService.blockAmount(iRequest, transaction.getTransactionId(), blockAmount);
    }
    /**
     * 校验核销金额
     *
     * @throws BeyondAmountLimitException
     */
    private void validata(IRequest iRequest, HlsCusCshTransaction transaction, List<HlsCusCshWriteOff> cshWriteOffs) throws BeyondAmountLimitException, HlsCusException {
        long[] contractIds = cshWriteOffs.stream().filter(
                item -> !StringUtils.equals(HlsConstantUtil.TransactionType.RECEIPT_ADVANCE_RECEIPT, item.getWriteOffType())
        ).mapToLong(HlsCusCshWriteOff::getContractId).distinct().toArray();
        for (long contractId : contractIds) {
            HlsCusConContract hlsCusConContract = conContractMapper.querySapStatusForReceived(contractId);
            if(!StringUtils.equals(SapConstants.SUCCESS, hlsCusConContract.getSapStatus())){
                throw new HlsCusException("借据["+hlsCusConContract.getContractNumber()+"]同步未成功不能认领!");
            }
        }
        HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
        hlsCusCshWriteOff.setCshTransactionId(transaction.getTransactionId());
        hlsCusCshWriteOff.setReversedFlag("N");
        List<HlsCusCshWriteOff> hlsCusCshWriteOffList = self().select(iRequest, hlsCusCshWriteOff, 1, 0);
        // 已核销的总金额
        double totalWriteOffAmount = MathUtil.round(CollectionUtils.isEmpty(hlsCusCshWriteOffList) ? _0D :
                hlsCusCshWriteOffList.stream().mapToDouble(HlsCusCshWriteOff::getWriteOffDueAmount).sum(), SCALE);
        // 剩余可核销金额
        double canWriteOffAmount = MathUtil.sub(
                transaction.getTransactionAmount(),
                MathUtil.add(
                        Optional.ofNullable(transaction.getBlockAmount()).orElse(_0D),
                        totalWriteOffAmount,
                        SCALE
                ),
                SCALE
        );
        // 待核销总金额
        double totalAmount = MathUtil.round(cshWriteOffs.stream().mapToDouble(HlsCusCshWriteOff::getWriteOffDueAmount).sum(), SCALE);
        if(Double.compare(canWriteOffAmount, totalAmount) < 0){
            throw new BeyondAmountLimitException("现金事务 "+transaction.getTransactionNum()+" 核销总金额"+totalAmount+"大于剩余可核销金额"+canWriteOffAmount+"!");
        }
        // 冻结金额
        double blockAmount = 0D;
        for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
            if(StringUtils.equals(WRITE_OFF_TYPE_ADVANCE_RECEIPT_CREDIT, cshWriteOff.getWriteOffType()) ||  // 预收款核销债权
                    StringUtils.equals(WRITE_OFF_TYPE_RECEIPT_DEPOSIT, cshWriteOff.getWriteOffType()) ||  // 收款核销保证金
                    StringUtils.equals(WRITE_OFF_TYPE_RECEIPT_CREDIT, cshWriteOff.getWriteOffType())){  // 收款核销债权
                if(Double.compare(MathUtil.round(cshWriteOff.getSurplusAmount(),SCALE), MathUtil.round(cshWriteOff.getWriteOffDueAmount(),SCALE)) < 0){
                    throw new BeyondAmountLimitException("合同编号："+cshWriteOff.getContractNumber()+"核销"+cshWriteOff.getCfItemN()+
                            ",核销金额"+cshWriteOff.getWriteOffDueAmount()+"大于剩余可核销金额"+cshWriteOff.getSurplusAmount()+"!");
                }
            }

            // 收款核销预售款
            if(StringUtils.equals("RECEIPT_ADVANCE_RECEIPT", cshWriteOff.getWriteOffType())){
                if(null == cshWriteOff.getAdvanceBpId()){
                    throw new HlsCusException("收款核销预收款，预收款挂帐商业伙伴不能为空!");
                }
            }

            Long cashflowId = cshWriteOff.getCashflowId();
            if (null != cashflowId) {
                HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
                cashflow.setCashflowId(cashflowId);
                cashflow = contractCashflowService.selectByPrimaryKey(iRequest, cashflow);
                // 应收
                cashflow.setBlockAmount(
                        MathUtil.add(
                                Optional.ofNullable(cashflow.getBlockAmount()).orElse(0D),
                                cshWriteOff.getWriteOffDueAmount(),
                                SCALE
                        )
                );
                if (Double.compare(
                        MathUtil.round(cashflow.getDueAmount(),SCALE),
                        MathUtil.add(
                                Optional.ofNullable(cashflow.getReceivedAmount()).orElse(0D),
                                cashflow.getBlockAmount(),
                                SCALE
                        )
                ) < 0) {
                    throw new BeyondAmountLimitException("合同编号：" + cshWriteOff.getContractNumber() +
                            ",期数：" + cshWriteOff.getTimes() + ",性质：" + cshWriteOff.getCfItemN() +
                            ";<br/>核销金额，冻结金额与已核销金额之和大于应收金额!");
                }
                if (cashflow.getCfItem().equals(_1) ||  // 租金
                        cashflow.getCfItem().equals(_11) ||  // 提前结清款
                        cashflow.getCfItem().equals(_13)) {  // 回购款项
                    // 本金
                    cashflow.setBlockPrincipal(MathUtil.add(
                            Optional.ofNullable(cashflow.getBlockPrincipal()).orElse(0D),
                            Optional.ofNullable(cshWriteOff.getWriteOffPrincipal()).orElse(0D),
                            SCALE
                    ));
                    if (Double.compare(
                            MathUtil.round(Optional.ofNullable(cashflow.getPrincipal()).orElse(0D),SCALE),
                            MathUtil.add(
                                    Optional.ofNullable(cashflow.getReceivedPrincipal()).orElse(0D),
                                    cashflow.getBlockPrincipal(),
                                    SCALE
                            )
                    ) < 0) {
                        throw new BeyondAmountLimitException("合同编号：" + cshWriteOff.getContractNumber() +
                                ",期数：" + cshWriteOff.getTimes() + ",性质：" + cshWriteOff.getCfItemN() +
                                ";<br/>核销本金，冻结本金与已核销本金之和大于应收本金!");
                    }
                    // 利息
                    cashflow.setBlockInterest(MathUtil.add(
                            Optional.ofNullable(cashflow.getBlockInterest()).orElse(0D),
                            Optional.ofNullable(cshWriteOff.getWriteOffInterest()).orElse(0D),
                            SCALE
                    ));
                    if (Double.compare(
                            MathUtil.round(Optional.ofNullable(cashflow.getInterest()).orElse(0D),SCALE),
                            MathUtil.add(
                                    Optional.ofNullable(cashflow.getReceivedInterest()).orElse(0D),
                                    cashflow.getBlockInterest(),
                                    SCALE
                            )
                    ) < 0) {
                        throw new BeyondAmountLimitException("合同编号：" + cshWriteOff.getContractNumber() +
                                ",期数：" + cshWriteOff.getTimes() + ",性质：" + cshWriteOff.getCfItemN() +
                                ";<br/>核销利息，冻结利息与已核销利息之和大于应收利息!");
                    }
                }
            }
            blockAmount = MathUtil.add(blockAmount, cshWriteOff.getWriteOffDueAmount(), SCALE);
        }
        // 现金事务
        if (Double.compare(
                MathUtil.round(transaction.getTransactionAmount(),SCALE),
                MathUtil.add(
                        Optional.ofNullable(transaction.getWriteOffAmount()).orElse(0D),
                        MathUtil.add(
                                Optional.ofNullable(transaction.getBlockAmount()).orElse(0D),
                                blockAmount,
                                SCALE
                        ),
                        SCALE
                )
        ) < 0) {
            throw new BeyondAmountLimitException("现金事务编号：" + transaction.getTransactionNum() +
                    ";<br/>核销金额，冻结金额与已核销金额之和大于现金事务金额!");
        }
    }

    /**
     * 更改器方法  -- 处理精度
     */
    public void accuracy(List<HlsCusCshWriteOff> cshWriteOffs){
        for (HlsCusCshWriteOff cshWriteOff : cshWriteOffs) {
            cshWriteOff.setWriteOffDueAmount(MathUtil.round(Optional.ofNullable(cshWriteOff.getWriteOffDueAmount()).orElse(0.0), SCALE));
            cshWriteOff.setWriteOffPrincipal(MathUtil.round(Optional.ofNullable(cshWriteOff.getWriteOffPrincipal()).orElse(0.0), SCALE));
            cshWriteOff.setWriteOffInterest(MathUtil.round(Optional.ofNullable(cshWriteOff.getWriteOffInterest()).orElse(0.0), SCALE));
            if(null != cshWriteOff.getSurplusAmount()){
                cshWriteOff.setSurplusAmount(MathUtil.round(cshWriteOff.getSurplusAmount(), SCALE));
            }
        }
    }

    public void payment(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd) throws Exception {
        CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount =new CshPaymentReqLnBankAccount();
        cshPaymentReqLnBankAccount.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        List<CshPaymentReqLnBankAccount> cshPaymentReqLnBankAccounts = new ArrayList<>();
        if (BUSINESS_FLAG_RETAIL.equals(hlsCusCshPaymentReqHd.getBusinessFlag())){
            //二期功能：零售业务
            cshPaymentReqLnBankAccount.setSourceDocId(hlsCusCshPaymentReqHd.getSourceContractId());
            cshPaymentReqLnBankAccounts = cshPaymentReqLnBankAccountMapper.selectRetailCshPaymentReqLnBankAccount(cshPaymentReqLnBankAccount);
        }else {
            cshPaymentReqLnBankAccounts = cshPaymentReqLnBankAccountMapper.selectCshPaymentReqLnBankAccount(cshPaymentReqLnBankAccount);
        }
        List<HlsCusCshWriteOff> cshWriteOffList = new ArrayList<>();
        List<HlsCusCshTransaction> transactionList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < cshPaymentReqLnBankAccounts.size(); i++) {
            //生成现金事务
            HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
            cshTransaction.setTransactionDate(hlsCusCshPaymentReqHd.getActualPayDate());
            cshTransaction.setCompanyId(iRequest.getCompanyId());
            cshTransaction.setTransactionAmount(Double.valueOf(cshPaymentReqLnBankAccounts.get(i).getPaymentAmount()));
            cshTransaction.setCurrencyCode(cshPaymentReqLnBankAccounts.get(i).getCurrency());
            getTransactionNum(iRequest, cshTransaction);
            cshTransaction.setContractId(cshPaymentReqLnBankAccounts.get(i).getSourceDocId());
            cshTransaction.setSourceDocCategory("CSH_PAYMENT_REQ");
            cshTransaction.setSourceDocId(cshPaymentReqLnBankAccounts.get(i).getPaymentReqId());
            cshTransaction.setBusinessType("PAYMENT");
            cshTransaction.setTransactionType("PAYMENT");
            cshTransaction.setBpBankAccountName(cshPaymentReqLnBankAccounts.get(i).getCshBankAccountName());
            cshTransaction.setBpBankAccountNum(cshPaymentReqLnBankAccounts.get(i).getCshBankAccountNum());
            cshTransaction.setBpBankAccountId(cshPaymentReqLnBankAccounts.get(i).getCshBankAccountId());
            cshTransaction.setSourceDocLineId(cshPaymentReqLnBankAccounts.get(i).getCshBankId());

            cshTransaction.setTransactionCategory("CSH_TRANSACTION");

            cshTransaction = cshTransactionService.insertSelective(iRequest, cshTransaction);
            transactionList.add(cshTransaction);

            //构造核销数据
            HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
            cshWriteOff.setCompanyId(cshPaymentReqLnBankAccounts.get(i).getCompanyId());
            if (DOCUMENT_TYPE_FCT_PAYMENT_REQ.equals(cshPaymentReqLnBankAccounts.get(i).getDocumentType())) {
                cshWriteOff.setWriteOffType(WRITEOFF_TYPE_FCT_PAYMENT_DEBT);
                cshWriteOff.setWriteOffDocCategory(WRITEOFF_DOC_CATEGORY_FCT_CONTRACT);
                //设置cashFlowId时 需要设置为现金流的主键（WithdrawCfId） 但保理cshPaymentReqLn.getSourceDocLineId()获取的是提款id
                HlsCusFctContractWithdrawCf hlsCusFctContractWithdrawCf = new HlsCusFctContractWithdrawCf();
                hlsCusFctContractWithdrawCf.setWithdrawId(cshPaymentReqLnBankAccounts.get(i).getSourceDocLineId());
                hlsCusFctContractWithdrawCf.setCfItem(FCT_CONTRACT_WITHDRAW_CF_ITEM_40);
                List<HlsCusFctContractWithdrawCf> cashFlowList = fctContractWithdrawCfMapper.select(hlsCusFctContractWithdrawCf);
                if (cashFlowList != null && cashFlowList.size() != 0) {
                    hlsCusFctContractWithdrawCf = cashFlowList.get(0);
                }
                cshWriteOff.setCashflowId(hlsCusFctContractWithdrawCf.getWithdrawCfId());
            } else {
                cshWriteOff.setWriteOffType(WRITEOFF_TYPE_PAYMENT_DEBT);
                cshWriteOff.setWriteOffDocCategory(WRITEOFF_DOC_CATEGORY_CON_CONTRACT);
                //租赁的cashFlowId正常设置即可
                cshWriteOff.setCashflowId(cshPaymentReqLnBankAccounts.get(i).getSourceDocLineId());
            }
            cshWriteOff.setWriteOffDate(sdf.parse(sdf.format(cshPaymentReqLnBankAccounts.get(i).getActualPayDate())));
            cshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
            //减去抵扣金额作为核销金额
            cshWriteOff.setCshWriteOffAmount(Double.valueOf(cshPaymentReqLnBankAccounts.get(i).getPaymentAmount()));
            cshWriteOff.setReversedFlag("N");
            cshWriteOff.setCashflowId(cshPaymentReqLnBankAccounts.get(i).getSourceDocLineId());
            cshWriteOff.setContractId(cshPaymentReqLnBankAccounts.get(i).getSourceDocId());
            cshWriteOff.setCurrencyCode(cshPaymentReqLnBankAccounts.get(i).getCurrency());
            cshWriteOff.setTimes(Long.valueOf(cshPaymentReqLnBankAccounts.get(i).getTimes()==null?0L:cshPaymentReqLnBankAccounts.get(i).getTimes()));
            cshWriteOff.setCfItem(Long.valueOf(cshPaymentReqLnBankAccounts.get(i).getCfItem() ==null?"0": cshPaymentReqLnBankAccounts.get(i).getCfItem() ));
            cshWriteOff.setCfType(Long.valueOf(cshPaymentReqLnBankAccounts.get(i).getCfType() ==null?"0": cshPaymentReqLnBankAccounts.get(i).getCfType() ));
            cshWriteOff.setWriteOffDueAmount(Double.valueOf(cshPaymentReqLnBankAccounts.get(i).getPaymentAmount()));
            cshWriteOff.setPaymentReqId(cshPaymentReqLnBankAccounts.get(i).getPaymentReqId());
            cshWriteOff.setPaymentReqLineId(cshPaymentReqLnBankAccounts.get(i).getPaymentReqLnId());
            cshWriteOffList.add(cshWriteOff);
        }
        Double actPaymentAmount =  0D;

        if (BUSINESS_FLAG_RETAIL.equals(hlsCusCshPaymentReqHd.getBusinessFlag())){
            //二期功能：零售业务
            actPaymentAmount =  OracleUtils.nvl(hlsCusCshPaymentReqHdMapper.queryRetailActualPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId()), 0.0);
        }else {
            actPaymentAmount =  OracleUtils.nvl(cshPaymentReqLnBankAccountMapper.queryActualPaymentAmount(hlsCusCshPaymentReqHd.getPaymentReqId()), 0.0);
        }
        List<HlsCusCshWriteOff> hlsCusCshWriteOffLists = new ArrayList<>();
        hlsCusCshWriteOffLists = mainCshWriteOffService.cshWriteOffMainPay(iRequest, cshWriteOffList);

        String writeOffType = cshWriteOffList.stream().findFirst().orElse(new HlsCusCshWriteOff()).getWriteOffType();
        if ("PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
            saveWriteOffType(iRequest, hlsCusCshWriteOffLists);
            //更新现金流表
            HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
            hlsCusConContractCashflow.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
            hlsCusConContractCashflow.setReceivedAmount(actPaymentAmount);
            hlsCusConContractCashflow.setWriteOffFlag("FULL");
            hlsCusConContractCashflowMapper.updateCashflowByinfo(hlsCusConContractCashflow);
            //更新合同表
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
            hlsCusConContract.setContractStatus("INCEPT");
            hlsCusConContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract);
        }

        //保证金退还现金流支付时，不进行quotationReCalc重新报价
        if (cshPaymentReqLnBankAccounts.get(0).getCfItem().equalsIgnoreCase("52")) {
            //遍历csh_write_off , 判断 cf_item是否为0 ，若为0 ，即设备款，修改其中一行的 FIRST_LEASE_PAY_FLAG 字段为Y
            for (HlsCusCshWriteOff cshWriteOff : hlsCusCshWriteOffLists) {
                updateConCashflowAfterWriteOff(cshWriteOff);
            }
        }else{
        //进件放款不改变报价信息
           /* HlsCusPrjQuotation hlsCusPrjQuotations = new HlsCusPrjQuotation();
            if (BUSINESS_FLAG_RETAIL.equals(hlsCusCshPaymentReqHd.getBusinessFlag())){
                //二期功能：零售业务
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
                hlsCusConContract = conContractMapper.selectByPrimaryKey(hlsCusConContract);
                hlsCusPrjQuotations.setQuotationId(hlsCusConContract.getQuotationId());
                hlsCusPrjQuotations = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotations);
            } else {
                hlsCusPrjQuotations.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
                hlsCusPrjQuotations = hlsCusPrjQuotationMapper.queryQuotationByConId(hlsCusPrjQuotations);
            }

            HlsCusPrjQuotation cusPrjQuotations = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, hlsCusPrjQuotations);
            cusPrjQuotations.setLeaseItemAmount(actPaymentAmount);
            cusPrjQuotations.setExchangeRate(cshPaymentReqLnBankAccounts.get(0).getRate());
            if (cshPaymentReqLnBankAccounts.get(0).getCfItem().equalsIgnoreCase("0")) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                cusPrjQuotations.setLeaseStartDate(hlsCusCshPaymentReqHd.getActualPayDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(hlsCusCshPaymentReqHd.getActualPayDate());
                cal.add(Calendar.YEAR, 1);
                Date nextAdjustmentDate = cal.getTime();
                cusPrjQuotations.setNextAdjustmentDate(nextAdjustmentDate);
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
                hlsCusConContract.setNextAdjustmentDate(nextAdjustmentDate);
                hlsCusConContractService.updateByPrimaryKeySelective(iRequest, hlsCusConContract);

            }
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, cusPrjQuotations);
            System.out.println(cusPrjQuotations.getLeaseItemAmount());
            //二期添加备注：对于二期功能，报价方案中，sourceDocumentCategory = CON_CONTRACT 此方法会去获取项目报价详情表中的Excel内容，并从中获取期数的值。
            // 当前会出现Excel上期数的值(value)为空，返回 期数未取到 的报错，并且会删除原来的合同现金流，导致支付失败。
            // 因此对于二期的合同支付，跳过报价重算逻辑
            if (!CON_CONTRACT.equals(cusPrjQuotations.getSourceDocumentCategory())){
                hlsCusPrjQuotationService.quotationReCalc(iRequest, cusPrjQuotations.getQuotationId(), true);
            }
            HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
            prjQuotationCashflowParameter.setQuotationId(cusPrjQuotations.getQuotationId());
            List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);


            for (int j = 0; j < prjQuotationCashflowList.size(); j++) {
                if (prjQuotationCashflowList.get(j).getCfItem() == 0) {
                    HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                    BeanRefUtils.beanToBean(prjQuotationCashflowList.get(j), conContractCashflow, hlsBeanRefUtilService);

                    // prjQuotationCashflow times double -> conContractCashflow times long
                    conContractCashflow.setTimes(prjQuotationCashflowList.get(j).getTimes().longValue());
                    conContractCashflow.setContractId(cshPaymentReqLnBankAccounts.get(0).getSourceDocId());
                    conContractCashflow.setCfStatus("RELEASE");
                    conContractCashflow.setWriteOffFlag("NOT");
                    conContractCashflow.setBillingStatus("NOT");
                    conContractCashflow.setOverdueStatus("N");
                    conContractCashflow.setPenaltyProcessStatus("N");
                    conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
                    conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
                    conContractCashflow.setGeneratedSourceDocId(cusPrjQuotations.getQuotationId());
                    conContractCashflow.setGeneratedSourceDocLineId(cshPaymentReqLnBankAccounts.get(0).getSourceDocLineId());
                    conContractCashflow.setCashflowId(cshPaymentReqLnBankAccounts.get(0).getSourceDocLineId());

                    HlsCusConContractCashflow cashflowNew = new HlsCusConContractCashflow();
                    cashflowNew = cashflowMapper.selectConContractCashflow(cshPaymentReqLnBankAccounts.get(0).getSourceDocLineId());
                    Double receivedAmount = MathUtil.add(cashflowNew.getReceivedAmount(), actPaymentAmount);
                    if (cashflowNew.getReceivedPrincipal() == null) {
                        conContractCashflow.setReceivedPrincipal(0.0D);
                    }

                    if (cashflowNew.getReceivedInterest() == null) {
                        conContractCashflow.setReceivedInterest(0.0D);
                    }

                    if (cshWriteOffList.get(0).getWriteOffPrincipal() == null) {
                        cshWriteOffList.get(0).setWriteOffPrincipal(0.0D);
                    }

                    if (cshWriteOffList.get(0).getWriteOffInterest() == null) {
                        cshWriteOffList.get(0).setWriteOffInterest(0.0D);
                    }
                    Double receivedPrincipal = MathUtil.add(cashflowNew.getReceivedPrincipal(), cshWriteOffList.get(0).getWriteOffPrincipal());
                    Double receivedInterest = MathUtil.add(cashflowNew.getReceivedInterest(), cshWriteOffList.get(0).getWriteOffInterest());
                    conContractCashflow.setReceivedAmount(receivedAmount);
                    conContractCashflow.setDueAmount(receivedAmount);
                    conContractCashflow.setReceivedPrincipal(receivedPrincipal);
                    conContractCashflow.setReceivedInterest(receivedInterest);
                    conContractCashflow.setWriteOffFlag("FULL");
                    conContractCashflow.setFullWriteOffDate(cshWriteOffList.get(0).getWriteOffDate());


                    if (checkReceivedPrincipalAndInterest(cashflowNew)) {
                        conContractCashflow.setReceivedPrincipal(0.0D);
                        conContractCashflow.setReceivedInterest(0.0D);
                    }
                    conContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);

                }

            }*/
        }




        List<HlsCusCshWriteOff> cusCshWriteOffList = new ArrayList<HlsCusCshWriteOff>();
        //遍历csh_write_off , 判断 cf_item是否为0 ，若为0 ，即设备款，修改其中一行的 FIRST_LEASE_PAY_FLAG 字段为Y
        for (HlsCusCshWriteOff cshWriteOff : hlsCusCshWriteOffLists) {
            HlsCusCshWriteOff writeOffNew = new HlsCusCshWriteOff();
            writeOffNew = cshWriteOffMapper.queryByCfItem(cshWriteOff);
            if (writeOffNew != null) {
                cusCshWriteOffList.add(writeOffNew);
            }

        }
        //修改其中一行数据的  firstLeasePayFlag 为Y
        if (cusCshWriteOffList.size() > 0) {
            HlsCusCshWriteOff hlsCshWriteOff = cusCshWriteOffList.get(0);
            hlsCshWriteOff.setFirstLeasePayFlag("Y");
            cshWriteOffMapper.updateByPrimaryKeySelective(hlsCshWriteOff);
        }

        //现金事务明细 ，核销数据 出凭证
        Long paymentReqId = hlsCusCshPaymentReqHd.getPaymentReqId();
        jeTrxCshTransactionPayment(transactionList, hlsCusCshPaymentReqHd.getSourceContractId(),paymentReqId);
        jeTrxPayEqipment(hlsCusCshWriteOffLists, hlsCusCshPaymentReqHd.getSourceContractId());


        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
        HlsCusConContract hlsCusConContract1 = hlsCusConContractService.selectByPrimaryKey(iRequest, hlsCusConContract);
        //修改进件订单状态
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusConContract1.getProjectId());
        hlsCusPrjProject.setOrderStatus("INCEPT");
        prjProjectMapper.updateWflProject(hlsCusPrjProject);
        //修改付款表状态
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd1 = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd1.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount2 = new CshPaymentReqLnBankAccount();
        cshPaymentReqLnBankAccount2.setPaymentReqLnId(paymentReqId);
        cshPaymentReqLnBankAccount2.setPaymentStatus("PAID");
        cshPaymentReqLnBankAccountMapper.updateCshPaymentReqLnBankAccountByLn(cshPaymentReqLnBankAccount2);

    }

    public void retailPayment(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd,HlsCusCshPaymentReqLn reqLn) throws Exception {
        List<HlsCusCshWriteOff> cshWriteOffList = new ArrayList<>();
        List<HlsCusCshTransaction> transactionList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HlsCusCshPaymentReqDt cshPaymentReqDt = new HlsCusCshPaymentReqDt();
        cshPaymentReqDt.setPaymentReqLnId(reqLn.getPaymentReqLnId());
        List<HlsCusCshPaymentReqDt> cshPaymentReqDts = cshPaymentReqDtMapper.select(cshPaymentReqDt);
        Double deductAmount = 0D;
        if(CollectionUtils.isNotEmpty(cshPaymentReqDts)){
            for(HlsCusCshPaymentReqDt dt:cshPaymentReqDts){
                deductAmount+=dt.getDeductAmount();
            }
        }
        Double realAmount = reqLn.getAmount()-deductAmount;

        //生成现金事务
        HlsCusCshTransaction cshTransaction = new HlsCusCshTransaction();
        cshTransaction.setTransactionDate(hlsCusCshPaymentReqHd.getActualPayDate());
        cshTransaction.setCompanyId(iRequest.getCompanyId());
        cshTransaction.setTransactionAmount(realAmount);
        cshTransaction.setCurrencyCode(hlsCusCshPaymentReqHd.getCurrency());
        getTransactionNum(iRequest, cshTransaction);
        cshTransaction.setContractId(reqLn.getSourceDocId());
        cshTransaction.setSourceDocCategory("CSH_PAYMENT_REQ");
        cshTransaction.setBusinessType("PAYMENT");
        cshTransaction.setTransactionType("PAYMENT");
        cshTransaction.setBpBankAccountName(reqLn.getBpBankAccountName());
        cshTransaction.setBpBankAccountNum(reqLn.getBpBankAccountNum());
        cshTransaction.setBpBankAccountId(reqLn.getBpBankAccountId());
        cshTransaction.setSourceDocId(hlsCusCshPaymentReqHd.getPaymentReqId());
        cshTransaction.setSourceDocLineId(reqLn.getPaymentReqLnId());
        cshTransaction.setTransactionCategory("CSH_TRANSACTION");

        cshTransaction = cshTransactionService.insertSelective(iRequest, cshTransaction);
        transactionList.add(cshTransaction);

        //构造核销数据
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setCompanyId(hlsCusCshPaymentReqHd.getCompanyId());
        if (DOCUMENT_TYPE_FCT_PAYMENT_REQ.equals(hlsCusCshPaymentReqHd.getDocumentType())) {
            cshWriteOff.setWriteOffType(WRITEOFF_TYPE_FCT_PAYMENT_DEBT);
            cshWriteOff.setWriteOffDocCategory(WRITEOFF_DOC_CATEGORY_FCT_CONTRACT);
            //设置cashFlowId时 需要设置为现金流的主键（WithdrawCfId） 但保理cshPaymentReqLn.getSourceDocLineId()获取的是提款id
            HlsCusFctContractWithdrawCf hlsCusFctContractWithdrawCf = new HlsCusFctContractWithdrawCf();
            hlsCusFctContractWithdrawCf.setWithdrawId(reqLn.getSourceDocLineId());
            hlsCusFctContractWithdrawCf.setCfItem(FCT_CONTRACT_WITHDRAW_CF_ITEM_40);
            List<HlsCusFctContractWithdrawCf> cashFlowList = fctContractWithdrawCfMapper.select(hlsCusFctContractWithdrawCf);
            if (CollectionUtils.isNotEmpty(cashFlowList)) {
                hlsCusFctContractWithdrawCf = cashFlowList.get(0);
            }
            cshWriteOff.setCashflowId(hlsCusFctContractWithdrawCf.getWithdrawCfId());
        } else {
            cshWriteOff.setWriteOffType(WRITEOFF_TYPE_PAYMENT_DEBT);
            cshWriteOff.setWriteOffDocCategory(WRITEOFF_DOC_CATEGORY_CON_CONTRACT);
            //租赁的cashFlowId正常设置即可
            cshWriteOff.setCashflowId(reqLn.getSourceDocLineId());
        }
        cshWriteOff.setWriteOffDate(sdf.parse(sdf.format(hlsCusCshPaymentReqHd.getActualPayDate())));
        cshWriteOff.setCshTransactionId(cshTransaction.getTransactionId());
        //减去抵扣金额作为核销金额
        cshWriteOff.setCshWriteOffAmount(realAmount);
        cshWriteOff.setReversedFlag("N");
        cshWriteOff.setCashflowId(reqLn.getSourceDocLineId());
        cshWriteOff.setContractId(reqLn.getSourceDocId());
        cshWriteOff.setCurrencyCode(hlsCusCshPaymentReqHd.getCurrency());
        HlsCusConContractCashflow cashflow = conContractCashflowMapper.selectByPrimaryKey(reqLn.getSourceDocLineId());
        cshWriteOff.setTimes(cashflow.getTimes());
        cshWriteOff.setCfItem(cashflow.getCfItem());
        cshWriteOff.setCfType(cashflow.getCfType());
        cshWriteOff.setWriteOffDueAmount(realAmount);
        cshWriteOff.setPaymentReqId(reqLn.getPaymentReqId());
        cshWriteOff.setPaymentReqLineId(reqLn.getPaymentReqLnId());
        cshWriteOffList.add(cshWriteOff);


        List<HlsCusCshWriteOff> hlsCusCshWriteOffLists = mainCshWriteOffService.cshWriteOffMainPay(iRequest, cshWriteOffList);

        String writeOffType = cshWriteOffList.stream().findFirst().orElse(new HlsCusCshWriteOff()).getWriteOffType();
        if ("PAYMENT_DEBT".equalsIgnoreCase(writeOffType)) {
            saveWriteOffType(iRequest, hlsCusCshWriteOffLists);
        }

        //保证金退还现金流支付时，不进行quotationReCalc重新报价
        if (cashflow.getCfItem()==52L) {
            //遍历csh_write_off , 判断 cf_item是否为0 ，若为0 ，即设备款，修改其中一行的 FIRST_LEASE_PAY_FLAG 字段为Y
            for (HlsCusCshWriteOff cusCshWriteOff : hlsCusCshWriteOffLists) {
                updateConCashflowAfterWriteOff(cusCshWriteOff);
            }
        }else{

            HlsCusPrjQuotation hlsCusPrjQuotations = new HlsCusPrjQuotation();
            //二期功能：零售业务
            HlsCusConContract hlsCusConContract = new HlsCusConContract();
            hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
            hlsCusConContract = conContractMapper.selectByPrimaryKey(hlsCusConContract);
            hlsCusPrjQuotations.setQuotationId(hlsCusConContract.getQuotationId());
            hlsCusPrjQuotations = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotations);

            HlsCusPrjQuotation cusPrjQuotations = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest, hlsCusPrjQuotations);
            // 728修改，不修改leaseItemAmount
            //            cusPrjQuotations.setLeaseItemAmount(actPaymentAmount);
            //todo 取不到
//            cusPrjQuotations.setExchangeRate(cshPaymentReqLnBankAccounts.get(0).getRate());
            if (cashflow.getCfItem()==0L) {
                cusPrjQuotations.setLeaseStartDate(hlsCusCshPaymentReqHd.getActualPayDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(hlsCusCshPaymentReqHd.getActualPayDate());
                cal.add(Calendar.YEAR, 1);
                Date nextAdjustmentDate = cal.getTime();
                cusPrjQuotations.setNextAdjustmentDate(nextAdjustmentDate);
                HlsCusConContract cusConContract = new HlsCusConContract();
                cusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceContractId());
                cusConContract.setNextAdjustmentDate(nextAdjustmentDate);
                hlsCusConContractService.updateByPrimaryKeySelective(iRequest, cusConContract);
            }
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, cusPrjQuotations);
            //二期添加备注：对于二期功能，报价方案中，sourceDocumentCategory = CON_CONTRACT 此方法会去获取项目报价详情表中的Excel内容，并从中获取期数的值。
            // 当前会出现Excel上期数的值(value)为空，返回 期数未取到 的报错，并且会删除原来的合同现金流，导致支付失败。
            // 因此对于二期的合同支付，跳过报价重算逻辑
            if (!CON_CONTRACT.equals(cusPrjQuotations.getSourceDocumentCategory())){
                if(!"PRJ_PROJECT".equals(cusPrjQuotations.getSourceDocumentCategory())) {
                    hlsCusPrjQuotationService.quotationReCalc(iRequest, cusPrjQuotations.getQuotationId(), true);
                }
            }
            HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
            prjQuotationCashflowParameter.setQuotationId(cusPrjQuotations.getQuotationId());
            List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);


            for (int j = 0; j < prjQuotationCashflowList.size(); j++) {
                if (prjQuotationCashflowList.get(j).getCfItem() == 0) {
                    HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                    BeanRefUtils.beanToBean(prjQuotationCashflowList.get(j), conContractCashflow, hlsBeanRefUtilService);

                    // prjQuotationCashflow times double -> conContractCashflow times long
                    conContractCashflow.setTimes(prjQuotationCashflowList.get(j).getTimes().longValue());
                    conContractCashflow.setContractId(reqLn.getSourceDocId());
                    conContractCashflow.setCfStatus("RELEASE");
                    conContractCashflow.setWriteOffFlag("NOT");
                    conContractCashflow.setBillingStatus("NOT");
                    conContractCashflow.setOverdueStatus("N");
                    conContractCashflow.setPenaltyProcessStatus("N");
                    conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
                    conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
                    conContractCashflow.setGeneratedSourceDocId(cusPrjQuotations.getQuotationId());
                    conContractCashflow.setGeneratedSourceDocLineId(prjQuotationCashflowList.get(j).getQuotationCashflowId());
                    conContractCashflow.setCashflowId(reqLn.getSourceDocLineId());

                    HlsCusConContractCashflow cashflowNew = cashflowMapper.selectConContractCashflow(reqLn.getSourceDocLineId());
                    Double receivedAmount = MathUtil.add(cashflowNew.getReceivedAmount(), reqLn.getAmount());
                    if (cashflowNew.getReceivedPrincipal() == null) {
                        conContractCashflow.setReceivedPrincipal(0.0D);
                    }

                    if (cashflowNew.getReceivedInterest() == null) {
                        conContractCashflow.setReceivedInterest(0.0D);
                    }

                    if (cshWriteOffList.get(0).getWriteOffPrincipal() == null) {
                        cshWriteOffList.get(0).setWriteOffPrincipal(0.0D);
                    }

                    if (cshWriteOffList.get(0).getWriteOffInterest() == null) {
                        cshWriteOffList.get(0).setWriteOffInterest(0.0D);
                    }
                    Double receivedPrincipal = MathUtil.add(cashflowNew.getReceivedPrincipal(), cshWriteOffList.get(0).getWriteOffPrincipal());
                    Double receivedInterest = MathUtil.add(cashflowNew.getReceivedInterest(), cshWriteOffList.get(0).getWriteOffInterest());
                    conContractCashflow.setReceivedAmount(receivedAmount);
                    // 728更改，不修改dueAmount
//                    conContractCashflow.setDueAmount(receivedAmount);
                    conContractCashflow.setReceivedPrincipal(receivedPrincipal);
                    conContractCashflow.setReceivedInterest(receivedInterest);
                    conContractCashflow.setWriteOffFlag("FULL");
                    conContractCashflow.setFullWriteOffDate(cshWriteOffList.get(0).getWriteOffDate());


                    if (checkReceivedPrincipalAndInterest(cashflowNew)) {
                        conContractCashflow.setReceivedPrincipal(0.0D);
                        conContractCashflow.setReceivedInterest(0.0D);
                    }
                    conContractCashflowService.updateByPrimaryKeySelective(iRequest, conContractCashflow);

                }

            }
        }


        List<HlsCusCshWriteOff> cusCshWriteOffList = new ArrayList<HlsCusCshWriteOff>();
        //遍历csh_write_off , 判断 cf_item是否为0 ，若为0 ，即设备款，修改其中一行的 FIRST_LEASE_PAY_FLAG 字段为Y
        for (HlsCusCshWriteOff cusCshWriteOff : hlsCusCshWriteOffLists) {
            HlsCusCshWriteOff writeOffNew = new HlsCusCshWriteOff();
            writeOffNew = cshWriteOffMapper.queryByCfItem(cusCshWriteOff);
            if (writeOffNew != null) {
                cusCshWriteOffList.add(writeOffNew);
            }

        }
        //修改其中一行数据的  firstLeasePayFlag 为Y
        if (CollectionUtils.isNotEmpty(cusCshWriteOffList)) {
            HlsCusCshWriteOff hlsCshWriteOff = cusCshWriteOffList.get(0);
            hlsCshWriteOff.setFirstLeasePayFlag("Y");
            cshWriteOffMapper.updateByPrimaryKeySelective(hlsCshWriteOff);
        }

        //现金事务明细 ，核销数据 出凭证
        Long paymentReqId = hlsCusCshPaymentReqHd.getPaymentReqId();
        jeTrxCshTransactionPayment(transactionList, hlsCusCshPaymentReqHd.getSourceContractId(),paymentReqId);
        jeTrxPayEqipment(hlsCusCshWriteOffLists, hlsCusCshPaymentReqHd.getSourceContractId());
    }


    boolean checkReceivedPrincipalAndInterest(HlsCusConContractCashflow cashflow) {
        boolean flag = false;
        if (cashflow.getCashflowId() != null) {
            HlsCusConContractCashflow cusConContractCashflow = new HlsCusConContractCashflow();
            cusConContractCashflow.setCashflowId(cashflow.getCashflowId());
            cusConContractCashflow = cashflowMapper.selectByPrimaryKey(cusConContractCashflow);
            List<Integer> cfItem = Arrays.asList(0, 2, 3, 5, 8, 9, 10);
            if (cfItem.contains(Integer.valueOf(cusConContractCashflow.getCfItem().toString()))) {
                flag = true;
            }
        }

        return flag;
    }

    private void getTransactionNum(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionType("PAYMENT");
        hlsCusCshTransaction.setBusinessType("PAYMENT");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                hlsCusCshTransaction.getTransactionType(), hlsCusCshTransaction.getBusinessType(), params));
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
        hlsCusCshTransaction.setCompanyId(hlsCusCshTransaction.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");

    }

    private void getTransactionNumRefund(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionType("REFUND");
        hlsCusCshTransaction.setBusinessType("PAYMENT");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx,
                hlsCusCshTransaction.getTransactionCategory(),
                hlsCusCshTransaction.getTransactionType(),
                hlsCusCshTransaction.getBusinessType(),
                params));

    }

    private void getTransactionNumDudect(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        hlsCusCshTransaction.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransaction.setTransactionType("DEDUCTION");
        hlsCusCshTransaction.setBusinessType("DEDUCTION");
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransaction.getTransactionCategory(),
                hlsCusCshTransaction.getTransactionType(), hlsCusCshTransaction.getBusinessType(), params));
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
        hlsCusCshTransaction.setCompanyId(hlsCusCshTransaction.getCompanyId());
        hlsCusCshTransaction.setReversedFlag("N");
        hlsCusCshTransaction.setPostedFlag("N");
        hlsCusCshTransaction.setWriteOffFlag("NOT");

    }

    private void getTransactionDtlNum(IRequest requestCtx, HlsCusCshTransactionDtl hlsCusCshTransactionDtl) {
        hlsCusCshTransactionDtl.setTransactionCategory("CSH_TRANSACTION");
        hlsCusCshTransactionDtl.setTransactionType("PAYMENT");
        hlsCusCshTransactionDtl.setBusinessType("PAYMENT");
        hlsCusCshTransactionDtl.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx, hlsCusCshTransactionDtl.getTransactionCategory(),
                hlsCusCshTransactionDtl.getTransactionType(), hlsCusCshTransactionDtl.getBusinessType(), params));
        hlsCusCshTransactionDtl.setPenaltyCalcDate(hlsCusCshTransactionDtl.getTransactionDate());
        hlsCusCshTransactionDtl.setCompanyId(hlsCusCshTransactionDtl.getCompanyId());
        hlsCusCshTransactionDtl.setReversedFlag("N");
        hlsCusCshTransactionDtl.setPostedFlag("N");
        hlsCusCshTransactionDtl.setWriteOffFlag("NOT");

    }
    private void getTransactionNumAdvance(IRequest requestCtx, HlsCusCshTransaction hlsCusCshTransaction) {
        hlsCusCshTransaction.setTransactionNum(codingRuleValuesService.getCodeRuleValue(requestCtx,
                hlsCusCshTransaction.getTransactionCategory(),
                hlsCusCshTransaction.getTransactionType(),
                hlsCusCshTransaction.getBusinessType(),
                params));
        hlsCusCshTransaction.setPenaltyCalcDate(hlsCusCshTransaction.getTransactionDate());
    }

    public void jeTrxCshTransactionPayment(List<HlsCusCshTransaction> cusCshTransactionList, Long contractId,Long paymentReqId) {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        //现金事务明细 插凭证流水表
        for (HlsCusCshTransaction cshTransaction : cusCshTransactionList) {
            Map map = new HashMap<>();
            map.put("jeTrxId", cshTransaction.getTransactionId());
            map.put("companyId", cshTransaction.getCompanyId());
            map.put("contractId", cshTransaction.getContractId());
            map.put("jeSourceDoc", JE_CON_CONTRACT);
            map.put("jeSourceId", contractId);
            AbstractJeTrxService transactionPaymentJeTrx = commonService.map.get("CSH_TRANSACTION_PAYMENT");
            transactionPaymentJeTrx.process(iRequest, map);
        }
    }

    public void jeTrxPayEqipment(List<HlsCusCshWriteOff> hlsCusCshWriteOffLists, Long contractId) {

        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        //核销数据 插凭证流水表
        for (HlsCusCshWriteOff cusCshWriteOff : hlsCusCshWriteOffLists) {
            Map params = new HashMap<>();
            params.put("jeTrxId", cusCshWriteOff.getWriteOffId());
            params.put("companyId", cusCshWriteOff.getCompanyId());
            params.put("contractId", cusCshWriteOff.getContractId());
            params.put("jeSourceDoc", JE_CON_CONTRACT);
            params.put("jeSourceId", contractId);
            AbstractJeTrxService paymentEqimentJeTrx = commonService.map.get("PAY_EQIPMENT");
            paymentEqimentJeTrx.process(iRequest, params);


        }
    }

    /**
     * 释放额度
     */
    @Override
    public void releaseCredit(IRequest iRequest, List<HlsCusCshWriteOff> hlsCusCshWriteOffList) throws HlsCusException {
        try {
            for (HlsCusCshWriteOff cshWriteOff : hlsCusCshWriteOffList) {
                self().releaseCredit(iRequest, cshWriteOff.getCfItem(), cshWriteOff.getContractId(), cshWriteOff.getWriteOffPrincipal());
            }
        } catch (Exception e) {
            throw new HlsCusException("额度释放失败，请让管理员检查并重新核销!");
        }
    }

    @Override
    public void releaseCredit(IRequest iRequest, Long cfItem, Long contractId, Double writeOffPrincipal) {
        if (cfItem != null &&
                (cfItem.equals(_1) || cfItem.equals(_11) || cfItem.equals(_13))) {
            HlsCusConContract hlsCusConContract = conContractMapper.selectByPrimaryKey(contractId);
            // 额度释放
            Double amount = Optional.ofNullable(writeOffPrincipal).orElse(0D);
            //TODO 零售业务核销后额度释放
            //hlsCreditLineService.releaseCredit(iRequest, hlsCusConContract.getCreditLineId(), amount);
        }
    }


}
