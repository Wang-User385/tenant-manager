package hls.core.hls.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.hand.hap.system.mapper.SysConfigMapper;
import com.hand.hls.bp.job.HlsDayEndJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.ConContractCashflowMapper;
import com.hand.hls.cont.mapper.ConOverduePenaltyRptLnMapper;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusConDebtExemptionReqCfMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionParaMapper;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.PrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.service.impl.HlsPenaltyCalExecutor;
import com.hand.hls.utils.service.FakeRequestService;
import hls.core.fnd.dto.HlsPenaltyProfileDtl;
import hls.core.fnd.mapper.HlsPenaltyProfileDtlMapper;
import hls.core.hls.service.HlsDayEndLsExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.*;
import static com.hand.hls.utils.HlsCusMathUtil.sub;
@Component
@Transactional
public class HlsDayEndLsExecuterImpl implements HlsDayEndLsExecutor {
    private static final Logger logger = LoggerFactory.getLogger(HlsPenaltyCalExecutor.class);
    /**
     * 日结类型为罚息 (PENALTY)
     */
    public static final String DAY_END_TYPE = "PENALTY";

    /**
     * 单利计算
     */
    public static final String SIMPLE_INTEREST = "SIMPLE_INTEREST";

    /**
     * 复利计算
     */
    public static final String COMPOUND_INTEREST = "COMPOUND_INTEREST";
    /**
     * 起租状态
     */
    public static final String INCEPT = "INCEPT";


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private HlsCusConContractMapper conContractMapper;

    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;

    @Autowired
    private HlsCusCshWriteOffMapper cshWriteOffMapper;

    @Autowired
    private ConContractCashflowMapper conContractCashflowMapper;

    @Autowired
    private ConOverduePenaltyRptLnMapper conOverduePenaltyRptLnMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private JeTrxCommonService jeTrxCommonService;
    @Autowired
    private HlsCusPrjQuotationMapper quotationMapper;

    @Autowired
    private FakeRequestService fakeRequestService;
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;
    @Autowired
    private HlsProductDefinitionParaMapper hlsProductDefinitionParaMapper;
    private static final String DayEndType = "PENALTY";

    @Override
    public String getDayEndType() {
        return "PENALTY";
    }



    @Override
    public void process(Map map) {
        Object contractId = map.get(HlsDayEndJob.PARAM_CONTRACT_ID);
        Date dayEndDate = (Date) map.get(HlsDayEndJob.PARAM_DAY_END_DATE);

        //测试用例
        /*contractId = "1000";
        dayEndDate = DateUtil.parseDate("2020-08-11");*/

        HlsCusConContract param = new HlsCusConContract();
        //罚息计算 从 当天晚上23点 改为了 第二天 凌晨2点 ，所以时间 需要减一天
        dayEndDate = DateUtil.offset(dayEndDate, DateField.DAY_OF_MONTH, -1);
        param.setDayEndDate(dayEndDate);

        if (contractId != null) {
            param.setContractId(Long.parseLong((String) contractId));
        }
        List<HlsCusConContract> cusConContracts = conContractMapper.selectOverContract2(param);

        for (HlsCusConContract cusConContract : cusConContracts) {
            HlsCusConContract conContract = conContractMapper.selectByPrimaryKey(cusConContract.getContractId());
            try {
                //调用罚息计算逻辑
                calculatePenalty(conContract, dayEndDate);
                //更新表外标志 逾期90天
//                updateOffBalanceSheetFlag(conContract,dayEndDate);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 罚息计算逻辑
     *
     * @param contract   需要计算罚息的合同
     * @param dayEndDate 逾期计算日期(默认为当前系统时间)
     */
    private void calculatePenalty(HlsCusConContract contract, Date dayEndDate) throws ParseException {
        Map<String, Object> param = new HashMap<>();
        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
        conContractCashflow.setContractId(contract.getContractId());
        conContractCashflow.setDayEndDate(dayEndDate);
        List<HlsCusConContractCashflow> conContractCashflowList = hlsCusConContractCashflowMapper.selectForOverDueDayEnd(conContractCashflow);

        //算头不算尾 加一天 （统一天数的计算逻辑）
        Date simpleDayEndDate = DateUtil.offset(dayEndDate, DateField.DAY_OF_MONTH, 1);
        for (HlsCusConContractCashflow cashflow : conContractCashflowList) {
            String writeOffFlag = cashflow.getWriteOffFlag();
            //更新现金流的逾期状态
            updateCashflowOverDueStatus(contract, cashflow, dayEndDate);
            if (SIMPLE_INTEREST.equals(contract.getPenaltyCalcMethod())) {
                updatePanaltyCashfow(contract, cashflow, simpleDayEndDate);
                //复利计算 兴业二开逻辑 逻辑中 自带算头不算尾 不需要加一天
            } else if (COMPOUND_INTEREST.equals(contract.getPenaltyCalcMethod())) {
                updateCompoundPanaltyCashfowNew(contract, cashflow, simpleDayEndDate);
            }else{
                updatePanaltyCashfow(contract, cashflow, simpleDayEndDate);
            }
        }
        updateConContract(contract);
    }

    /**
     * 更新现金流的逾期状态
     *
     * @param contract   合同
     * @param cashflow   现金流
     * @param dayEndDate 计算日
     */
    private void updateCashflowOverDueStatus(HlsCusConContract contract, HlsCusConContractCashflow cashflow, Date dayEndDate) {
        //逾期状态
        String overdueStatus = cashflow.getOverdueStatus();
        Date dueDate = cashflow.getDueDate();

        Double dueAmount = cashflow.getDueAmount();
        //Double receivedAmount = cashflow.getReceivedAmount();
        Double receivedAmount = cashflow.getWriteOffDueAmount();
        //本金
        Double principal = cashflow.getPrincipal();
        //Double receivedPrincipal = cashflow.getReceivedPrincipal();
        Double receivedPrincipal = cashflow.getWriteOffPrincipal();
        //利息
        Double interest = cashflow.getInterest();
        //Double receivedInterest = cashflow.getReceivedInterest();
        Double receivedInterest = cashflow.getWriteOffInterest();
        dayEndDate = DateUtil.offset(dayEndDate, DateField.DAY_OF_MONTH, 1);
        if ("Y".equals(overdueStatus)) {
            //已逾期
            if (dueAmount > nvl(receivedAmount, 0D)) {
                //未收完款项
                cashflow.setOverdueBookDate(dayEndDate);
                cashflow.setOverdueMaxDays(DateUtil.betweenDay(dayEndDate, dueDate, true));
                cashflow.setOverdueAmount(dueAmount - nvl(receivedAmount, 0D));
                cashflow.setOverduePrincipal(principal - nvl(receivedPrincipal, 0D));
                cashflow.setOverdueInterest(interest - nvl(receivedInterest, 0D));
            } else {
                //已收完款
                cashflow.setOverdueAmount(dueAmount - nvl(receivedAmount, 0D));
                cashflow.setOverduePrincipal(principal - nvl(receivedPrincipal, 0D));
                cashflow.setOverdueBookDate(cashflow.getFullWriteOffDate());
                cashflow.setOverdueMaxDays(DateUtil.betweenDay(cashflow.getFullWriteOffDate(), dueDate, true));
                cashflow.setOverdueInterest(interest - nvl(receivedInterest, 0D));
            }
        } else {
            //未逾期
            cashflow.setOverdueStatus("Y");
            cashflow.setOverdueMaxDays(DateUtil.betweenDay(dayEndDate, dueDate, true));
            cashflow.setOverdueBookDate(dayEndDate);
            cashflow.setOverdueAmount(dueAmount - nvl(receivedAmount, 0D));
            cashflow.setOverduePrincipal(principal - nvl(receivedPrincipal, 0D));
            cashflow.setOverdueInterest(interest - nvl(receivedInterest, 0D));
        }
        conContractCashflowMapper.updateByPrimaryKey(cashflow);
    }


    /**
     * 计算罚息 并插入日志表
     * con_contract_cashflow.penalty_process_status<> 'NORMAL' 跳过 (其他两种方式:SUSPEND 暂停计算/EXEMPT 豁免在罚息减免处考虑)
     * 已核销金额罚息 = 已核销金额*(核销日期-应还日期-宽限期)*罚息率 =write_off_amount*( write_off_date- due_date - grace_period )* penalty_rate
     * 备注: 已核销金额罚息累加(每次核销的已核销金额都会有两天的宽限期)
     * 未核销金额罚息=(应还金额-已还金额)*(sysdate-应还日期-宽限期)*罚息率 =(due_amount-received_amount)*(sysdate-due_date- grace_period)*penalty_rate
     *
     * @param contract   合同
     * @param cashflow   逾期计算日期(默认为当前系统时间)
     * @param dayEndDate 计算日期
     */
    private void updatePanaltyCashfow(HlsCusConContract contract, HlsCusConContractCashflow cashflow, Date dayEndDate) {
        if (cashflow == null) {
            return;
        }
        if (!"NORMAL".equals(cashflow.getPenaltyProcessStatus()) && !"N".equals(cashflow.getPenaltyProcessStatus())) {
            //con_contract_cashflow.penalty_process_status<> 'NORMAL' 跳过
            return;
        }
        Long cashflowId = cashflow.getCashflowId();
        Long contractId = contract.getContractId();
        Long cfItem = cashflow.getCfItem();
        Long cfType = cashflow.getCfType();

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(contract.getProjectId());
        prjProject =  prjProjectMapper.selectByPrimaryKey(prjProject);

        //现金流期数
        Long times = cashflow.getTimes();

        //根据 本条 CashFlow CashFlowId 和 DueDate查询核销表
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setCashflowId(cashflow.getCashflowId());
        cshWriteOff.setDueDate(cashflow.getDueDate());
        //逾期核销的记录
        List<HlsCusCshWriteOff> penaltyCshWriteOffList = cshWriteOffMapper.selectPenaltyWriteOff(cshWriteOff);
        //正常核销的记录
        List<HlsCusCshWriteOff> advancedCshWriteOffList = cshWriteOffMapper.selectAdvancedWriteOff(cshWriteOff);

        //豁免期天数
        HlsCusPrjQuotation prjQuotation=new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(contract.getQuotationId());
        prjQuotation=quotationMapper.selectByPrimaryKey(prjQuotation.getQuotationId());
        Long gracePeriod=nvl(hlsProductDefinitionParaMapper.getDefaulValue(prjQuotation.getPlanId(),"GRACE_PERIOD"),0d).longValue();
        logger.info("gracePeriod: " + gracePeriod);
        //罚息率
        Double penaltyRate=nvl(hlsProductDefinitionParaMapper.getDefaulValue(prjQuotation.getPlanId(),"PENALTY_RATE"),0d);
        logger.info("penaltyRate: " + penaltyRate);
        //应还金额
        Double dueAmount = cashflow.getDueAmount();
        logger.info("dueAmount: " + dueAmount);
        //应还日期
        Date dueDate = cashflow.getDueDate();
        logger.info("dueDate: " + dueDate);

        /*↓↓↓↓↓↓ 已核销金额罚息 计算开始 ↓↓↓↓↓↓*/
        //该现金流的已核销金额罚息
        Double received = 0.0D;
        //该现金流已核销金额
        Double writeOffDueAmount = 0.0D;

        for (HlsCusCshWriteOff cusCshWriteOff : penaltyCshWriteOffList) {
            //已核销金额
            Double writeOffWriteOffDueAmount = cusCshWriteOff.getWriteOffDueAmount();
            //核销日期
            //fix by Eugene Song  根据收款日期 而非核销日期
            Date writeOffDate = cusCshWriteOff.getPenaltyCalcDate();
            //逾期天数
            Long overdu = DateUtil.betweenDay(writeOffDate, dueDate, true);
            logger.info("overdu: " + overdu);
            //去除宽限期后的预期天数(核销日期-应还日期-宽限期)
            Long overDueDaysWithoutGracePeriod = overdu - gracePeriod;
            logger.info("overDueDaysWithoutGracePeriod: " + overDueDaysWithoutGracePeriod);
            //已核销金额罚息 = 已核销金额*(核销日期-应还日期-宽限期)*罚息率
            Double receiving = writeOffWriteOffDueAmount * penaltyRate * overDueDaysWithoutGracePeriod;
            logger.info("receiving: " + receiving);
            received += receiving;
            writeOffDueAmount += writeOffWriteOffDueAmount;


        }
        /* ↑↑↑↑↑↑ 已核销金额罚息 计算结束 ↑↑↑↑↑↑ */

        /* ↓↓↓↓↓↓ 未核销金额罚息 计算开始 ↓↓↓↓↓↓**/
        //剩余应还金额(未核销金额)(应还金额-该现金流已核销金额)
        if (advancedCshWriteOffList.size() > 0) {
            writeOffDueAmount = add(advancedCshWriteOffList.stream().collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount)), writeOffDueAmount, 2);
        }
        double unWirteOffDueAmount = dueAmount - writeOffDueAmount;
        //逾期天数
        Long overdu = DateUtil.betweenDay(dayEndDate, dueDate, true);
        //去除宽限期后的预期天数(核销日期-应还日期-宽限期)
        long overDueDaysWithoutGracePeriod = overdu - gracePeriod;
        if (overDueDaysWithoutGracePeriod < 0) {
            overDueDaysWithoutGracePeriod = 0;
        }

        //未核销金额罚息=(应还金额-已还金额)*(dayEndDate-应还日期-宽限期)*罚息率
        Double unreceived = unWirteOffDueAmount * penaltyRate * overDueDaysWithoutGracePeriod;
        logger.info("unreceived: " + unreceived);
        //罚息总额
        Double profileTotalAmount = unreceived + received;

        /* ↑↑↑↑↑↑ 未核销金额罚息 计算结束 ↑↑↑↑↑↑  */
        //查询该条现金流是否已经插入罚息
        HlsCusConContractCashflow cashFlowUpdateParam = new HlsCusConContractCashflow();
        cashFlowUpdateParam.setContractId(contractId);
        cashFlowUpdateParam.setCfItem(9L);
        cashFlowUpdateParam.setTimes(times);
        cashFlowUpdateParam.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
        cashFlowUpdateParam.setGeneratedSourceDocLineId(cashflow.getCashflowId());
        cashFlowUpdateParam.setGeneratedSource("DAY_END");
        cashFlowUpdateParam.setOverdueStatus("N");
        List<HlsCusConContractCashflow> cashFlow2Update = hlsCusConContractCashflowMapper.select(cashFlowUpdateParam);

        if (CollectionUtils.isEmpty(cashFlow2Update)) {
            if (profileTotalAmount.compareTo(0.0) > 0) {
                HlsCusConContractCashflow contractCashflowIns = new HlsCusConContractCashflow();
                contractCashflowIns.setContractId(cashflow.getContractId());
                contractCashflowIns.setCfType(9L);
                contractCashflowIns.setCfItem(9L);
                contractCashflowIns.setCfDirection("INFLOW");
                contractCashflowIns.setCfStatus("RELEASE");
                contractCashflowIns.setTimes(times);
                contractCashflowIns.setDueDate(dayEndDate);
                contractCashflowIns.setCalcDate(dayEndDate);
                contractCashflowIns.setFinIncomeDate(dayEndDate);
                contractCashflowIns.setDueAmount(profileTotalAmount);
                contractCashflowIns.setNetDueAmount(div(contractCashflowIns.getDueAmount(), add(1, contract.getVatRate()), 2));
                contractCashflowIns.setVatDueAmount(sub(contractCashflowIns.getDueAmount(), contractCashflowIns.getNetDueAmount()));
                contractCashflowIns.setGeneratedSource("DAY_END");
                contractCashflowIns.setWriteOffFlag("NOT");
                contractCashflowIns.setBillingStatus("NOT");
                contractCashflowIns.setOverdueStatus("N");
                contractCashflowIns.setPenaltyProcessStatus("N");
                contractCashflowIns.setGeneratedSource("DAY_END");
                contractCashflowIns.setDayEndDate(dayEndDate);
                contractCashflowIns.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
                contractCashflowIns.setGeneratedSourceDocLineId(cashflow.getCashflowId());
                hlsCusConContractCashflowMapper.insertSelective(contractCashflowIns);
            }
        } else {
            HlsCusConContractCashflow cashflow2Update = cashFlow2Update.get(0);
            cashflow2Update.setDueDate(dayEndDate);
            cashflow2Update.setCalcDate(dayEndDate);
            cashflow2Update.setFinIncomeDate(dayEndDate);
            cashflow2Update.setDueAmount(nvl(profileTotalAmount, 0D));
            cashflow2Update.setNetDueAmount(div(cashflow2Update.getDueAmount(), add(1, contract.getVatRate()), 2));
            cashflow2Update.setVatDueAmount(sub(cashflow2Update.getDueAmount(), cashflow2Update.getNetDueAmount()));
            cashflow2Update.setGeneratedSource("DAY_END");
            cashflow2Update.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
            cashflow2Update.setGeneratedSourceDocLineId(cashflow.getCashflowId());
            hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(cashflow2Update);
            //更新
            if (cashFlow2Update.size() > 1) {
                for (HlsCusConContractCashflow delete : cashFlow2Update) {
                    if (!delete.getCashflowId().equals(cashflow2Update.getCashflowId())) {
                        hlsCusConContractCashflowMapper.deleteByPrimaryKey(delete);
                    }
                }
            }

        }
    }


    /**
     * 计算罚息 并插入日志表 复利
     * con_contract_cashflow.penalty_process_status<> 'NORMAL' 跳过 (其他两种方式:SUSPEND 暂停计算/EXEMPT 豁免在罚息减免处考虑)
     * 已核销金额罚息 = 已核销金额*(核销日期-应还日期-宽限期)*罚息率 =write_off_amount*( write_off_date- due_date - grace_period )* penalty_rate
     * 备注: 已核销金额罚息累加(每次核销的已核销金额都会有两天的宽限期)
     * 未核销金额罚息=(应还金额-已还金额)*(sysdate-应还日期-宽限期)*罚息率 =(due_amount-received_amount)*(sysdate-due_date- grace_period)*penalty_rate
     *
     * @param contract   合同
     * @param cashflow   逾期计算日期(默认为当前系统时间)
     * @param dayEndDate 计算日期
     */
    private void updateCompoundPanaltyCashfowNew(HlsCusConContract contract, HlsCusConContractCashflow cashflow, Date dayEndDate) {
        if (cashflow == null) {
            return;
        }
        if (!"NORMAL".equals(cashflow.getPenaltyProcessStatus()) && !"N".equals(cashflow.getPenaltyProcessStatus())) {
            //con_contract_cashflow.penalty_process_status<> 'NORMAL' 跳过
            return;
        }
        Long cashflowId = cashflow.getCashflowId();
        Long contractId = contract.getContractId();
        Long cfItem = cashflow.getCfItem();
        Long cfType = cashflow.getCfType();

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(contract.getProjectId());
        prjProject =  prjProjectMapper.selectByPrimaryKey(prjProject);

        //现金流期数
        Long times = cashflow.getTimes();

        //根据 本条 CashFlow CashFlowId 和 DueDate查询核销表
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setCashflowId(cashflow.getCashflowId());
        cshWriteOff.setDueDate(cashflow.getDueDate());
        //逾期核销的记录
        List<HlsCusCshWriteOff> penaltyCshWriteOffList = cshWriteOffMapper.selectPenaltyWriteOff(cshWriteOff);
        //正常核销的记录
        List<HlsCusCshWriteOff> advancedCshWriteOffList = cshWriteOffMapper.selectAdvancedWriteOff(cshWriteOff);

        //豁免期天数
        Long gracePeriod = nvl(contract.getGracePeriod(), 0L);
        logger.info("gracePeriod: " + gracePeriod);
        //罚息率
//        Double penaltyRate = nvl(contract.getPenaltyRate(), 0D);
        Double penaltyRate = prjProject.getFalsifyInterestRate();
        logger.info("penaltyRate: " + penaltyRate);
        //应还金额
        Double dueAmount = cashflow.getDueAmount();
        logger.info("dueAmount: " + dueAmount);
        //应还日期
        Date dueDate = cashflow.getDueDate();
        logger.info("dueDate: " + dueDate);

        /*↓↓↓↓↓↓ 已核销金额罚息 计算开始 ↓↓↓↓↓↓*/
        //该现金流的已核销金额罚息
        Double received = 0.0D;
        //该现金流已核销金额
        Double writeOffDueAmount = 0.0D;

        for (HlsCusCshWriteOff cusCshWriteOff : penaltyCshWriteOffList) {
            //已核销金额
            Double writeOffWriteOffDueAmount = cusCshWriteOff.getWriteOffDueAmount();
            //核销日期
            //fix by Eugene Song  根据收款日期 而非核销日期
            Date writeOffDate = cusCshWriteOff.getPenaltyCalcDate();
            //逾期天数
            Long overdu = DateUtil.betweenDay(writeOffDate, dueDate, true);
            logger.info("overdu: " + overdu);
            //去除宽限期后的预期天数(核销日期-应还日期-宽限期)
            Long overDueDaysWithoutGracePeriod = overdu - gracePeriod;
            logger.info("overDueDaysWithoutGracePeriod: " + overDueDaysWithoutGracePeriod);
            //已核销金额罚息 = 已核销金额*(核销日期-应还日期-宽限期)*罚息率
            Double receiving = round(writeOffWriteOffDueAmount * Math.pow((1+penaltyRate),overDueDaysWithoutGracePeriod) - writeOffWriteOffDueAmount,2);
            logger.info("receiving: " + receiving);
            received += receiving;
            writeOffDueAmount += writeOffWriteOffDueAmount;


        }
        /* ↑↑↑↑↑↑ 已核销金额罚息 计算结束 ↑↑↑↑↑↑ */


        /* ↓↓↓↓↓↓ 未核销金额罚息 计算开始 ↓↓↓↓↓↓**/
        //剩余应还金额(未核销金额)(应还金额-该现金流已核销金额)
        if (advancedCshWriteOffList.size() > 0) {
            writeOffDueAmount = add(advancedCshWriteOffList.stream().collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount)), writeOffDueAmount, 2);
        }
        double unWirteOffDueAmount = dueAmount - writeOffDueAmount;
        //逾期天数
        Long overdu = DateUtil.betweenDay(dayEndDate, dueDate, true);
        //去除宽限期后的预期天数(核销日期-应还日期-宽限期)
        long overDueDaysWithoutGracePeriod = overdu - gracePeriod;
        if (overDueDaysWithoutGracePeriod < 0) {
            overDueDaysWithoutGracePeriod = 0;
        }

        //未核销金额罚息=(应还金额-已还金额)*(dayEndDate-应还日期-宽限期)*罚息率
        Double unreceived = round(unWirteOffDueAmount * Math.pow((1+penaltyRate),overDueDaysWithoutGracePeriod) - unWirteOffDueAmount,2);
        logger.info("unreceived: " + unreceived);
        //罚息总额
        Double profileTotalAmount = unreceived + received;

        /* ↑↑↑↑↑↑ 未核销金额罚息 计算结束 ↑↑↑↑↑↑  */

        //查询该条现金流是否已经插入罚息

        HlsCusConContractCashflow cashFlowUpdateParam = new HlsCusConContractCashflow();
        cashFlowUpdateParam.setContractId(contractId);
        cashFlowUpdateParam.setCfItem(9L);
        cashFlowUpdateParam.setTimes(times);
        cashFlowUpdateParam.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
        cashFlowUpdateParam.setGeneratedSourceDocLineId(cashflow.getCashflowId());
        cashFlowUpdateParam.setGeneratedSource("DAY_END");
        cashFlowUpdateParam.setOverdueStatus("N");
        List<HlsCusConContractCashflow> cashFlow2Update = hlsCusConContractCashflowMapper.select(cashFlowUpdateParam);

        if (CollectionUtils.isEmpty(cashFlow2Update)) {
            if (profileTotalAmount.compareTo(0.0) > 0) {
                HlsCusConContractCashflow contractCashflowIns = new HlsCusConContractCashflow();
                contractCashflowIns.setContractId(cashflow.getContractId());
                contractCashflowIns.setCfType(9L);
                contractCashflowIns.setCfItem(9L);
                contractCashflowIns.setCfDirection("INFLOW");
                contractCashflowIns.setCfStatus("RELEASE");
                contractCashflowIns.setTimes(times);
                contractCashflowIns.setDueDate(dayEndDate);
                contractCashflowIns.setFinIncomeDate(dayEndDate);
                contractCashflowIns.setDueAmount(profileTotalAmount);
                contractCashflowIns.setNetDueAmount(div(contractCashflowIns.getDueAmount(), add(1, cashflow.getTaxTypeRate()), 2));
                contractCashflowIns.setVatDueAmount(sub(contractCashflowIns.getDueAmount(), contractCashflowIns.getNetDueAmount()));
                contractCashflowIns.setGeneratedSource("DAY_END");
                contractCashflowIns.setWriteOffFlag("NOT");
                contractCashflowIns.setBillingStatus("NOT");
                contractCashflowIns.setOverdueStatus("N");
                contractCashflowIns.setPenaltyProcessStatus("N");
                contractCashflowIns.setGeneratedSource("DAY_END");
                contractCashflowIns.setDayEndDate(dayEndDate);
                contractCashflowIns.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
                contractCashflowIns.setGeneratedSourceDocLineId(cashflow.getCashflowId());
                hlsCusConContractCashflowMapper.insertSelective(contractCashflowIns);
            }
        } else {
            HlsCusConContractCashflow cashflow2Update = cashFlow2Update.get(0);
            cashflow2Update.setDueDate(dayEndDate);
            cashflow2Update.setFinIncomeDate(dayEndDate);
            cashflow2Update.setDueAmount(nvl(profileTotalAmount, 0D));
            cashflow2Update.setNetDueAmount(div(cashflow2Update.getDueAmount(), add(1, cashflow.getTaxTypeRate()), 2));
            cashflow2Update.setVatDueAmount(sub(cashflow2Update.getDueAmount(), cashflow2Update.getNetDueAmount()));
            cashflow2Update.setGeneratedSource("DAY_END");
            cashflow2Update.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
            cashflow2Update.setGeneratedSourceDocLineId(cashflow.getCashflowId());
            hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(cashflow2Update);
            //更新
            if (cashFlow2Update.size() > 1) {
                for (HlsCusConContractCashflow delete : cashFlow2Update) {
                    if (!delete.getCashflowId().equals(cashflow2Update.getCashflowId())) {
                        hlsCusConContractCashflowMapper.deleteByPrimaryKey(delete);
                    }
                }
            }

        }
    }


    /**
     * 更新合同表
     *
     * @param contract 合同
     */
    private void updateConContract(HlsCusConContract contract) {

        Long overdueMaxDays = conContractMapper.selectMaxOverDueDays(contract.getContractId());
        String status;
        if (nvl(overdueMaxDays, 0L) > 0D) {
            status = "Y";
        } else {
            status = "N";
        }
        contract.setOverdueMaxDays(overdueMaxDays + 1 );
        contract.setOverdueStatus(status);
        conContractMapper.updateByPrimaryKeySelective(contract);
    }

}
