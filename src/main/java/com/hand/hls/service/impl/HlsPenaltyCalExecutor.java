package com.hand.hls.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.SysConfig;
import com.hand.hap.system.mapper.SysConfigMapper;
import com.hand.hls.bp.job.HlsDayEndJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.ConContractCashflowMapper;
import com.hand.hls.cont.mapper.ConOverduePenaltyRptLnMapper;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.service.HlsDayEndExecutor;
import com.hand.hls.utils.DateUtils;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.utils.service.FakeRequestService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.*;

/**
 * <p>
 * 罚息计算执行器
 * </p>
 *
 * @author qiang.chen04@hand-china.com 2019/07/03 14:52
 * modify by Eugene Song 添加罚息复利计算
 */
@Component
@Transactional
public class HlsPenaltyCalExecutor implements HlsDayEndExecutor {

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

    @Override
    public String getDayEndType() {
        return DAY_END_TYPE;
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
        List<HlsCusConContract> cusConContracts = conContractMapper.selectOverContract(param);

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
            //if (!"FULL".equals(writeOffFlag)) {
            //计算罚息并更新
            if (SIMPLE_INTEREST.equals(contract.getPenaltyCalcMethod())) {
                updatePanaltyCashfow(contract, cashflow, simpleDayEndDate);
                //复利计算 兴业二开逻辑 逻辑中 自带算头不算尾 不需要加一天
            } else if (COMPOUND_INTEREST.equals(contract.getPenaltyCalcMethod())) {
//                updateCompoundPanaltyCashfow(contract, cashflow, dayEndDate);
                updateCompoundPanaltyCashfowNew(contract, cashflow, simpleDayEndDate);
            }

            //}
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

       /* //插入罚息生成记录表
        ConOverduePenaltyRptLn report = new ConOverduePenaltyRptLn();
        report.setCashflowId(cashflowId);
        report.setContractId(contractId);
        report.setCfItem(cfItem);
        report.setCfType(cfType);
        report.setTimes(cashflow.getTimes());
        report.setLineType("2");
        report.setDueAmount(cashflow.getDueAmount() - nvl(received, 0D));
        report.setDueDate(cashflow.getDueDate());
        report.setWriteOffAmount(null);
        report.setWriteOffDate(null);
        report.setPenaltyAmt(profileTotalAmount);
        report.setPenaltyDays(overDueDaysWithoutGracePeriod);
        conOverduePenaltyRptLnMapper.insert(report);*/
    }

    /**
     * @Title: getWriteOffAmount
     * @Discription: 截止当前日 已核销金额
     * @Param: [cashflow, dayEndDate]
     * @Return: java.lang.Double
     */
    private Double getWriteOffAmount(HlsCusConContractCashflow cashflow, Date dayEndDate) {
        Double dueAmount = cashflow.getDueAmount();
        //截止当前日 已核销金额
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setCashflowId(cashflow.getCashflowId());
        cshWriteOff.setDueDate(dayEndDate);
        List<HlsCusCshWriteOff> cshWriteOffList = this.cshWriteOffMapper.selectAdvancedWriteOff(cshWriteOff);
        Double writeOffDueAmountTotal = 0.0;
        if (!cshWriteOffList.isEmpty()) {
            writeOffDueAmountTotal = round(cshWriteOffList.stream().collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount)), 2);
        }
        return writeOffDueAmountTotal;
    }



    /**
     * @Title: getWriteOffAmountDay
     * @Discription: 获取当日核销金额合计
     * @Param: [cashflow, dayEndDate]
     * @Return: java.lang.Double
     */
    private Double getWriteOffAmountDay(HlsCusConContractCashflow cashflow, Date dayEndDate) {
        Double dueAmount = cashflow.getDueAmount();
        //罚息计算日当天 所有的该笔现金流核销记录
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setCashflowId(cashflow.getCashflowId());
        cshWriteOff.setPenaltyCalcDate(dayEndDate);
        cshWriteOff.setReversedFlag("N");
        List<HlsCusCshWriteOff> cshWriteOffList = this.cshWriteOffMapper.selectPenaltyWriteOffDay(cshWriteOff);
        Double writeOffDueAmountTotal = 0.0;
        if (!cshWriteOffList.isEmpty()) {
            //同一天发生多笔核销  金额需要相加
            writeOffDueAmountTotal = cshWriteOffList.stream().collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount));
        }
        return writeOffDueAmountTotal;
    }


    /**
     * @Title: getPenaltyAmount
     * @Discription: 当日产生罚息
     * @Param: [penaltyCalcStandard, penaltyRate]
     * @Return: java.lang.Double
     */
    private Double getPenaltyAmount(Double penaltyCalcStandard, Double penaltyRate) {
        return mul(penaltyCalcStandard, penaltyRate, 2);
    }


    /**
     * @Title: getWriteOffPenaltyAmount
     * @Discription: 当日核销的罚息金额
     * @Param: [penaltyAmountSum, cashflow, dayEndDate]
     * @Return: java.lang.Double
     */
    private Double getWriteOffPenaltyAmount(HlsCusConContractCashflow cashflow, Date dayEndDate) {
        //截止于罚息计算日 所有的罚息现金流核销记录
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setContractId(cashflow.getContractId());
        cshWriteOff.setCfItem(9L);
        cshWriteOff.setTimes(cashflow.getTimes());
        cshWriteOff.setPenaltyCalcDate(dayEndDate);
        cshWriteOff.setReversedFlag("N");
        List<HlsCusCshWriteOff> cshWriteOffList = this.cshWriteOffMapper.selectPenaltyWriteOffDay(cshWriteOff);
        Double writeOffDueAmountTotal = 0.0;
        if (!cshWriteOffList.isEmpty()) {
            //同一天发生多笔核销  金额需要相加
            writeOffDueAmountTotal = cshWriteOffList.stream().collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount));
        }
        return writeOffDueAmountTotal;
    }

    private void updateCompoundPanaltyCashfow(HlsCusConContract contract, HlsCusConContractCashflow cashflow, Date dayEndDate) {
//        Double penaltyRate = contract.getPenaltyRate();
        Double dueAmount = cashflow.getDueAmount();
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(contract.getProjectId());
        prjProject =  prjProjectMapper.selectByPrimaryKey(prjProject);
        //罚息利率取虚拟合同头表维护值
        Double penaltyRate = prjProject.getFalsifyInterestRate();
        //截止当前核销金额
        Double writeOffAmount = getWriteOffAmount(cashflow, dayEndDate);
        logger.info("writeOffAmount: " + writeOffAmount);
        //当日尚欠租金
        Double residueAmount = sub(dueAmount, writeOffAmount, 2);
        logger.info("residueAmount: " + residueAmount);
        //当日复利罚息调整值
        Double penaltyBalance = nvl(cashflow.getPenaltyBalance(), 0.0);
        logger.info("penaltyBalance: " + penaltyBalance);
        //当日罚息计算基准
        Double penaltyCalcStandard = add(residueAmount, penaltyBalance);
        logger.info("penaltyCalcStandard: " + penaltyCalcStandard);
        //当日产生罚息
        Double penaltyAmount = getPenaltyAmount(penaltyCalcStandard, penaltyRate);
        logger.info("penaltyAmount: " + penaltyAmount);

        //累计尚欠罚息
        Double penaltyAmountSum = 0.0;
        HlsCusConContractCashflow penaltyCashflow = new HlsCusConContractCashflow();
        penaltyCashflow.setContractId(contract.getContractId());
        penaltyCashflow.setCfItem(9L);
        penaltyCashflow.setGeneratedSourceDocLineId(cashflow.getCashflowId());
        List<HlsCusConContractCashflow> penaltyCashflowList = hlsCusConContractCashflowMapper.select(penaltyCashflow);
        if (penaltyCashflowList.size() > 0) {
            penaltyAmountSum = penaltyCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount));
        }
        //当日罚息核销金额
        Double writeOffPenaltyAmount = getWriteOffPenaltyAmount(cashflow, dayEndDate);
        logger.info("writeOffPenaltyAmount: " + writeOffPenaltyAmount);

        //应收日 到 下个月 1号 之前 这段时间 当日复利罚息调整值 为0
        Date nextBeginOfMonth = DateUtil.beginOfMonth(cashflow.getDueDate()).offset(DateField.MONTH, 1);
        logger.info("nextBeginOfMonth: " + nextBeginOfMonth);
        logger.info("dayEndDate: " + dayEndDate);

        if (dayEndDate.compareTo(nextBeginOfMonth) < 0) {
            penaltyBalance = 0.0;
        } else {
            //之后的 每个月 1号 计算一次 复利罚息调整值，每天可以根据当日罚息核销情况调整
            //下个月1号的 当日复利罚息调整值 等于上个月 最后一天的累计尚欠罚息
            if (penaltyBalance.compareTo(writeOffPenaltyAmount) < 1) {
                penaltyBalance = 0.0;
            } else {
                penaltyBalance = sub(penaltyBalance, writeOffPenaltyAmount, 2);
            }
            logger.info("penaltyBalance: " + penaltyBalance);
        }

        penaltyAmountSum = add(penaltyAmountSum, sub(penaltyAmount, writeOffPenaltyAmount), 2);

        createPenaltyCasfhlow(cashflow, dayEndDate, penaltyAmountSum, prjProject.getVatRate());

        HlsCusConContractCashflow contractCashflow = hlsCusConContractCashflowMapper.selectByPrimaryKey(cashflow.getCashflowId());
        contractCashflow.setPenaltyBalance(penaltyBalance);
        hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(contractCashflow);
    }


    private void createPenaltyCasfhlow(HlsCusConContractCashflow cashflow, Date dayEndDate, Double penaltyAmountSum, Double taxRate) {
        HlsCusConContractCashflow cashFlowUpdateParam = new HlsCusConContractCashflow();
        cashFlowUpdateParam.setContractId(cashflow.getContractId());
        cashFlowUpdateParam.setCfItem(9L);
        cashFlowUpdateParam.setTimes(cashflow.getTimes());
        cashFlowUpdateParam.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
        cashFlowUpdateParam.setGeneratedSourceDocLineId(cashflow.getCashflowId());
        cashFlowUpdateParam.setGeneratedSource("DAY_END");
        cashFlowUpdateParam.setOverdueStatus("N");
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setQuotationId(cashflow.getQuotationId());
        quotation = quotationMapper.selectByPrimaryKey(quotation);
        List<HlsCusConContractCashflow> cashFlow2Update = hlsCusConContractCashflowMapper.select(cashFlowUpdateParam);

        if (CollectionUtils.isEmpty(cashFlow2Update)) {
            if (penaltyAmountSum.compareTo(0.0) > 0) {
                HlsCusConContractCashflow contractCashflowIns = new HlsCusConContractCashflow();
                contractCashflowIns.setContractId(cashflow.getContractId());
                contractCashflowIns.setCfType(9L);
                contractCashflowIns.setCfItem(9L);
                contractCashflowIns.setCfDirection("INFLOW");
                contractCashflowIns.setCfStatus("RELEASE");
                contractCashflowIns.setTimes(cashflow.getTimes());
                contractCashflowIns.setDueDate(dayEndDate);
                contractCashflowIns.setFinIncomeDate(dayEndDate);
                contractCashflowIns.setDueAmount(nvl(penaltyAmountSum, 0.0));
                contractCashflowIns.setNetDueAmount(div(contractCashflowIns.getDueAmount(), add(1, nvl(taxRate,quotation.getVatRate())), 2));
                contractCashflowIns.setVatDueAmount(sub(contractCashflowIns.getDueAmount(), contractCashflowIns.getNetDueAmount()));

                contractCashflowIns.setGeneratedSource("DAY_END");
                contractCashflowIns.setWriteOffFlag("NOT");
                contractCashflowIns.setBillingStatus("NOT");
                contractCashflowIns.setOverdueStatus("N");
                contractCashflowIns.setPenaltyProcessStatus("N");
                contractCashflowIns.setGeneratedSource("DAY_END");
                contractCashflowIns.setGeneratedSourceDocId(cashflow.getGeneratedSourceDocId());
                contractCashflowIns.setGeneratedSourceDocLineId(cashflow.getCashflowId());
                hlsCusConContractCashflowMapper.insertSelective(contractCashflowIns);
            }
        } else {
            HlsCusConContractCashflow cashflow2Update = cashFlow2Update.get(0);
            cashflow2Update.setDueDate(dayEndDate);
            cashflow2Update.setFinIncomeDate(dayEndDate);
            cashflow2Update.setDueAmount(nvl(penaltyAmountSum, 0.0));
            cashflow2Update.setNetDueAmount(div(cashflow2Update.getDueAmount(), add(1, nvl(taxRate,cashflow.getTaxTypeRate())), 2));
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
     * @Title: dateBetween
     * @Discription: 获取日期间隔  START = 2020/1/1 END = 2020/1/5   dateList = 2020/1/1 2020/1/2 2020/1/3 2020/1/4 2020/1/5
     * @Param: [start, end]
     * @Return: java.util.List<java.lang.String>
     */
    private List<String> dateBetween(String start, String end) {
        List<String> dateList = new ArrayList<>();

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        long distance = ChronoUnit.DAYS.between(startDate, endDate);
        if (distance < 1) {
            return dateList;
        }
        Stream.iterate(startDate, d -> {
            return d.plusDays(1);
        }).limit(distance + 1).forEach(f -> {
            dateList.add(f.toString());
        });
        return dateList;
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


    private void updateOffBalanceSheetFlag(HlsCusConContract contract,Date dayEndDate) throws ParseException, ResMessageException {


        SysConfig sysConfig = sysConfigMapper.selectByCode("OFF_BALANCE_DAYS");

        Example example = new Example(HlsCusConContractCashflow.class);
        example.createCriteria().
                andEqualTo("contractId", contract.getContractId()).
                andIn("cfItem", Arrays.asList(1, 10)).
                andEqualTo("cfStatus", "RELEASE").
                andNotEqualTo("writeOffFlag", "FULL");

        List<HlsCusConContractCashflow> cashflowList = conContractCashflowMapper.selectByExample(example);


        String offBalanceSheetFlag = "N";
        if (cashflowList.size() > 0) {
            HlsCusConContractCashflow overdueCashflow = cashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).findFirst().orElse(null);
            Date overdueDate = overdueCashflow.getDueDate();
            long between = DateUtil.betweenDay(overdueDate,dayEndDate , true);
            if (between >= Long.parseLong(sysConfig.getConfigValue())) {
                offBalanceSheetFlag = "Y";
                //生成凭证
                AbstractJeTrxService jeTrxService = jeTrxCommonService.map.get("OFF_BALANCE_TRANSFER");
                Map params = new HashMap<>();
                params.put("jeTrxId", contract.getContractId());
                params.put("companyId", contract.getCompanyId());
                params.put("contractId", contract.getContractId());
                params.put("jeDate", df.parse(df.format(new Date())));
                params.put("sourceDoc", "CON_CONTRACT");
                IRequest iRequest = fakeRequestService.createFakeRequest();
                jeTrxService.process(iRequest, params);
            }
        }
        contract.setOffBalanceSheetFlag(offBalanceSheetFlag);
        conContractMapper.updateByPrimaryKeySelective(contract);
    }



    /**
     * @Title: calcCompoundPanaltyCashfow
     * @Discription: 罚息试算 复利
     * @Param: [contract, cashflow, dayEndDate]
     * @Return: void
     */
    private void calcCompoundPanaltyCashfow(HlsCusConContract contract, HlsCusConContractCashflow cashflow, Date dayEndDate) {
        LocalDate dueDate = DateUtils.format(cashflow.getDueDate());
        LocalDate dayEndDateLocal = DateUtils.format(dayEndDate);
        Double penaltyRate = contract.getPenaltyRate();
        Double dueAmount = cashflow.getDueAmount();
        //获取日期间隔
        List<String> dateList = dateBetween(dueDate.format(formatter), dayEndDateLocal.format(formatter));
        //当日复利罚息调整值
        Double penaltyBalance = 0.0;
        //当日罚息计算基准
        Double penaltyCalcStandard = 0.0;
        //当日产生罚息
        Double penaltyAmount = 0.0;
        //当日累计尚欠罚息
        Double penaltyAmountSum = 0.0;
        //每个月1号 前一天的 当日复利罚息调整值 （方便后续计算）
        Double penaltyBalanceBeginOfMonth = 0.0;
        //当日核销金额
        Double writeOffAmount = 0.0;
        //累计核销金额
        Double writeOffAmountTotal = 0.0;
        //当日罚息核销金额
        Double writeOffPenaltyAmount = 0.0;
        //一个月内 累计核销金额  （方便后续计算）
        Double writeOffPenaltyAmountTotalInMonth = 0.0;
        for (String dateStr : dateList) {
            Date date = DateUtils.format(dateStr);
            //应收日 到 下个月 1号 之前 这段时间 当日复利罚息调整值 为0
            Date nextBeginOfMonth = DateUtil.beginOfMonth(cashflow.getDueDate()).offset(DateField.MONTH, 1);
            logger.info("nextBeginOfMonth: " + nextBeginOfMonth);
            logger.info("dateStr: " + dateStr);
            if (date.compareTo(nextBeginOfMonth) < 0) {

                writeOffAmount = getWriteOffAmount(cashflow, date);
                writeOffAmountTotal = add(writeOffAmount, writeOffAmountTotal);
                penaltyCalcStandard = add(sub(dueAmount, writeOffAmountTotal), 0.0);
                logger.info("penaltyCalcStandard: " + penaltyCalcStandard);
                penaltyAmount = getPenaltyAmount(penaltyCalcStandard, penaltyRate);
                logger.info("penaltyAmount: " + penaltyAmount);
                writeOffPenaltyAmount = getWriteOffPenaltyAmount(cashflow, date);
                logger.info("writeOffPenaltyAmount: " + writeOffPenaltyAmount);
                penaltyAmountSum = sub(add(penaltyAmount, penaltyAmountSum), writeOffPenaltyAmount);
                logger.info("penaltyAmountSum: " + penaltyAmountSum);
            } else {
                //之后的 每个月 1号 计算一次 复利罚息调整值，每天可以根据当日罚息核销情况调整
                //下个月1号的 当日复利罚息调整值 等于上个月 最后一天的累计尚欠罚息
                if (DateUtils.format(date).getDayOfMonth() == 1) {
                    penaltyBalanceBeginOfMonth = penaltyAmountSum;
                    //每个月1号 重置这个金额 （当日复利罚息调整值 取的是月初1号前一天的值，所以这个金额 需要每月重置一次）
                    writeOffPenaltyAmountTotalInMonth = 0.0;
                }
                logger.info("penaltyBalanceBeginOfMonth: " + penaltyBalanceBeginOfMonth);
                writeOffPenaltyAmount = getWriteOffPenaltyAmount(cashflow, date);
                logger.info("writeOffPenaltyAmount: " + writeOffPenaltyAmount);
                writeOffPenaltyAmountTotalInMonth = add(writeOffPenaltyAmount, writeOffPenaltyAmountTotalInMonth);

                if (penaltyBalanceBeginOfMonth.compareTo(writeOffPenaltyAmountTotalInMonth) < 1) {
                    penaltyBalance = 0.0;
                } else {
                    penaltyBalance = sub(penaltyBalanceBeginOfMonth, writeOffPenaltyAmountTotalInMonth);
                }
                logger.info("penaltyBalance: " + penaltyBalance);
                writeOffAmount = getWriteOffAmount(cashflow, date);
                logger.info("writeOffAmount: " + writeOffAmount);
                writeOffAmountTotal = add(writeOffAmount, writeOffAmountTotal);
                logger.info("writeOffAmountTotal: " + writeOffAmountTotal);
                penaltyCalcStandard = add(sub(dueAmount, writeOffAmountTotal), penaltyBalance);
                logger.info("penaltyCalcStandard: " + penaltyCalcStandard);
                penaltyAmount = getPenaltyAmount(penaltyCalcStandard, penaltyRate);
                logger.info("penaltyAmount: " + penaltyAmount);
                penaltyAmountSum = sub(add(penaltyAmount, penaltyAmountSum), writeOffPenaltyAmount);
                logger.info("penaltyAmountSum: " + penaltyAmountSum);
            }
        }
        //TODO 插入试算表
        //createPenaltyCasfhlow(cashflow, dayEndDate, penaltyAmountSum, contract.getVatRate());
    }

}
