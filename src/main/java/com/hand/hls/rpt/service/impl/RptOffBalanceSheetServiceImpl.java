package com.hand.hls.rpt.service.impl;

import cn.hutool.core.date.DateUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.SysConfig;
import com.hand.hap.system.mapper.SysConfigMapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.mapper.GldFinanceIncomeDayMapper;
import com.hand.hls.rpt.dto.RptOffBalanceSheet;
import com.hand.hls.rpt.mapper.RptOffBalanceSheetMapper;
import com.hand.hls.rpt.service.RptOffBalanceSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hand.hls.sys.utils.OracleUtils.nvl;
import static com.hand.hls.utils.HlsCusMathUtil.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class RptOffBalanceSheetServiceImpl extends BaseServiceImpl<RptOffBalanceSheet> implements RptOffBalanceSheetService {
    @Autowired
    private RptOffBalanceSheetMapper mapper;
    @Autowired
    private GldFinanceIncomeDayMapper gldFinanceIncomeDayMapper;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsCusCshWriteOffMapper writeOffMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private HlsCusCshTransactionMapper cshTransactionMapper;


    /**
     * 按每1000个一组分割
     */
    private static final Integer MAX_NUMBER = 1000;

    private static Integer countStep(Integer size) {
        return (size + MAX_NUMBER - 1) / MAX_NUMBER;
    }


    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dfPeriodName = new SimpleDateFormat("yyyy-MM");

    @Override
    public List<RptOffBalanceSheet> createRptOffBalanceSheet(IRequest iRequest, Long contractId) throws ParseException {


        SysConfig sysConfig = sysConfigMapper.selectByCode("OFF_BALANCE_DAYS");
        //表外标志
        String offBalanceSheetFlag = "N";
        //默认90天
        Long configDays = Long.parseLong(sysConfig.getConfigValue());

        RptOffBalanceSheet report = new RptOffBalanceSheet();
        List<RptOffBalanceSheet> reportList = new ArrayList<>();

        report.setContractId(contractId);
        mapper.deleteByContractId(report);

        HlsCusConContract contract = contractMapper.selectByPrimaryKey(contractId);
        Double vatRate = contract.getVatRate();

        //收益分摊按天(所有数据)
        Example incomeDayExample = new Example(GldFinanceIncomeDay.class);
        incomeDayExample.createCriteria().andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList(1, 10));
        List<GldFinanceIncomeDay> incomeDayList = gldFinanceIncomeDayMapper.selectByExample(incomeDayExample);

        //收益分摊按天(截止于当天的数据)
        Example calcIncomeDayExample = new Example(GldFinanceIncomeDay.class);
        calcIncomeDayExample.createCriteria().andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList(1, 10)).andLessThanOrEqualTo("amortizationDate",df.parse(df.format(new Date())));
        List<GldFinanceIncomeDay> calcIncomeDayList = gldFinanceIncomeDayMapper.selectByExample(calcIncomeDayExample);


        //现金流
        Example cashflowExample = new Example(HlsCusConContractCashflow.class);
        cashflowExample.createCriteria().andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList(0, 1, 10)).andIsNotNull("dueDate");
        List<HlsCusConContractCashflow> cashflowList = cashflowMapper.selectByExample(cashflowExample);

        //核销
        Example writeOffExample = new Example(HlsCusCshWriteOff.class);
        writeOffExample.createCriteria().andEqualTo("contractId", contractId).andEqualTo("reversedFlag", "N").andIn("cfItem", Arrays.asList(1, 10));
        List<HlsCusCshWriteOff> cshWriteOffList = writeOffMapper.selectByExample(writeOffExample);
        //通过核销表 找到 现金事务表 ，取到收款日期
        for (HlsCusCshWriteOff cshWriteOff : cshWriteOffList) {
            HlsCusCshTransaction cshTransaction = cshTransactionMapper.selectByPrimaryKey(cshWriteOff.getCshTransactionId());
            cshWriteOff.setWriteOffDate(cshTransaction.getTransactionDate());
        }

        //所有的核销 包括反冲的记录 用于后续90天判断
        Example allWriteOffExample = new Example(HlsCusCshWriteOff.class);
        allWriteOffExample.createCriteria().andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList(1, 10));
        List<HlsCusCshWriteOff> allCshWriteOffList = writeOffMapper.selectByExample(allWriteOffExample);

        //所有需要判断的时点  应收日90天的日期+ 所有核销记录日期 包括核销反冲日期
        List<Date> caclDateList = new ArrayList<>();

        cashflowList.stream().filter(item -> "INFLOW".equals(item.getCfDirection())).forEach(item -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(item.getDueDate());
            calendar.add(Calendar.DATE, Integer.parseInt(configDays.toString()));
            caclDateList.add(calendar.getTime());
        });

        allCshWriteOffList.stream().forEach(item -> {
            caclDateList.add(item.getWriteOffDate());
        });


        if (calcIncomeDayList.size() > 0) {

            Date lastAmortizationDate = incomeDayList.stream().sorted(Comparator.comparing(GldFinanceIncomeDay::getAmortizationDate).reversed()).findFirst().get().getAmortizationDate();

            calcIncomeDayList = calcIncomeDayList.stream().sorted(Comparator.comparing(GldFinanceIncomeDay::getAmortizationDate)).collect(Collectors.toList());

            Double financeAmount = 0.0;
            Double actFinanceAmount = 0.0;
            Double totalAmortizationAmount = 0.0;
            Double allAmortizationAmount = round(incomeDayList.stream().collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount)), 2);
            Double totalFinanceAmount = 0.0;
            Double totalActFinanceAmount = 0.0;

            Calendar calendar = Calendar.getInstance();

            Long offBalanceSheetDay = 0L;

            Double originalObsAmount = null;

            for (GldFinanceIncomeDay financeIncomeDay : calcIncomeDayList) {
                RptOffBalanceSheet data = new RptOffBalanceSheet();

                data.setContractId(contractId);
                data.setCashflowId(financeIncomeDay.getCashflowId());
                //日摊销金额（计划）
                data.setAmortizationAmount(financeIncomeDay.getAmortizationAmount());
                //摊销日期
                data.setAmortizationDate(financeIncomeDay.getAmortizationDate());

                totalAmortizationAmount = add(totalAmortizationAmount, data.getAmortizationAmount(), 2);

                cashflowList.stream().filter(item -> financeIncomeDay.getAmortizationDate().equals(item.getDueDate())).forEach(item -> {
                    //期序
                    data.setTimes(item.getTimes());
                    //支付日期
                    data.setDueDate(item.getDueDate());
                    //租金
                    if (item.getCfItem() != 0L) {
                        data.setDueAmount(item.getDueAmount());
                        //利息
                        data.setPrincipal(item.getPrincipal());
                        //回收本金
                        data.setInterest(item.getInterest());
                    }
                });

                //实收租金
                Double writeOffDueAmount = round(nvl(cshWriteOffList.stream().
                        filter(item -> financeIncomeDay.getAmortizationDate().equals(item.getWriteOffDate())).
                        collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount)), 0.0), 2);
                data.setWriteOffDueAmount(writeOffDueAmount);
                //实收本金
                Double writeOffPrincipal = round(nvl(cshWriteOffList.stream().
                        filter(item -> financeIncomeDay.getAmortizationDate().equals(item.getWriteOffDate())).
                        collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffPrincipal)), 0.0), 2);
                data.setWriteOffPrincipal(writeOffPrincipal);
                //实收利息
                Double writeOffInterest = round(nvl(cshWriteOffList.stream().
                        filter(item -> financeIncomeDay.getAmortizationDate().equals(item.getWriteOffDate())).
                        collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffInterest)), 0.0), 2);
                data.setWriteOffInterest(writeOffInterest);

                //逾期90天判断 表内转表外
                Long count = caclDateList.stream().filter(item -> item.compareTo(financeIncomeDay.getAmortizationDate()) == 0).count();

                if (count > 0) {
                    offBalanceSheetDay = getOffBalanceSheetDay(contractId, financeIncomeDay.getAmortizationDate());
                }

                if (offBalanceSheetDay.compareTo(configDays) == -1 && "N".equals(offBalanceSheetFlag)) {
                    //日摊销金额（实际）
                    data.setActAmortizationAmount(data.getAmortizationAmount());
                    data.setOriginalObsAmount(null);
                } else if (offBalanceSheetDay.compareTo(configDays) == 0) {
                    Double totalWriteOffInterest = cshWriteOffList.stream().filter(item -> financeIncomeDay.getAmortizationDate().compareTo(item.getWriteOffDate()) == 1).collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffInterest));
                    Double netTotalWriteOffInterest = div(totalWriteOffInterest, add(1, vatRate), 2);
                    //日摊销金额（实际）
                    data.setActAmortizationAmount(sub(netTotalWriteOffInterest, totalFinanceAmount, 2));
                    //应收利息表外账总额
                    data.setOffBalanceSheetAmount(sub(sub(allAmortizationAmount, totalActFinanceAmount, 2), data.getActAmortizationAmount(), 2));
                    offBalanceSheetFlag = "Y";
                    offBalanceSheetDay = offBalanceSheetDay + 1;
                    originalObsAmount = data.getOffBalanceSheetAmount();
                    data.setOriginalObsAmount(originalObsAmount);

                } else if (offBalanceSheetDay.compareTo(configDays) == 1) {
                    //日摊销金额（实际）
                    if (writeOffInterest.compareTo(0.0) != 0) {
                        data.setActAmortizationAmount(div(writeOffInterest, add(1, vatRate), 2));
                    }
                    //应收利息表外账总额
                    data.setOffBalanceSheetAmount(sub(sub(allAmortizationAmount, totalActFinanceAmount, 2), nvl(data.getActAmortizationAmount(), 0.0), 2));
                    offBalanceSheetFlag = "Y";
                    offBalanceSheetDay = offBalanceSheetDay + 1;
                    data.setOriginalObsAmount(originalObsAmount);
                }
                //表外 转 表内
                else if (offBalanceSheetDay.compareTo(configDays) == -1 && "Y".equals(offBalanceSheetFlag)) {
                    data.setActAmortizationAmount(sub(totalAmortizationAmount, totalActFinanceAmount, 2));
                    data.setOffBalanceSheetAmount(0.0);
                    offBalanceSheetFlag = "N";
                    data.setOriginalObsAmount(null);
                }

                financeAmount = add(financeAmount, data.getAmortizationAmount(), 2);
                actFinanceAmount = add(actFinanceAmount, nvl(data.getActAmortizationAmount(), 0.0), 2);

                //判断是不是月末,同时月末不是最后一天，最后一天在下面一段逻辑处理（防止 最后一天 正好是 月末 ）
                calendar.setTime(financeIncomeDay.getAmortizationDate());
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

                if (calendar.getTime().equals(data.getAmortizationDate()) && !lastAmortizationDate.equals(data.getAmortizationDate())) {
                    //月摊销金额（计划）
                    data.setFinanceIncome(financeAmount);
                    data.setActFinanceIncome(actFinanceAmount);
                    //摊销月份
                    data.setPeriodName(dfPeriodName.format(data.getAmortizationDate()));
                    financeAmount = 0.0;
                    actFinanceAmount = 0.0;
                }

                //判断是不是最后一天
                if (lastAmortizationDate.equals(data.getAmortizationDate())) {
                    //月摊销金额（计划）
                    data.setFinanceIncome(financeAmount);
                    data.setActFinanceIncome(actFinanceAmount);
                    //摊销月份
                    data.setPeriodName(dfPeriodName.format(data.getAmortizationDate()));
                    financeAmount = 0.0;
                    actFinanceAmount = 0.0;
                }

                totalFinanceAmount = add(totalFinanceAmount, data.getAmortizationAmount(), 2);
                totalActFinanceAmount = add(totalActFinanceAmount, nvl(data.getActAmortizationAmount(), 0.0), 2);

                reportList.add(data);
            }
            //每1000条数据提交一次
            int limit = countStep(reportList.size());

            List<List<RptOffBalanceSheet>> infoSplitList = Stream.iterate(0, n -> n + 1).limit(limit).parallel().map(a -> reportList.stream().skip(a * MAX_NUMBER).limit(MAX_NUMBER).parallel().collect(Collectors.toList())).collect(Collectors.toList());
            for (List<RptOffBalanceSheet> list : infoSplitList) {
                mapper.insertOffBalanceSheetBatch(list);
            }
        }
        return reportList;
    }


    private Long getOffBalanceSheetDay(Long contractId, Date calcDate) throws ParseException {

        //计算日之前所有的核销记录
        Example writeOffExample = new Example(HlsCusCshWriteOff.class);
        writeOffExample.createCriteria().
                andEqualTo("contractId", contractId).
                andEqualTo("reversedFlag", "N").
                andIn("cfItem", Arrays.asList(1, 10));
        List<HlsCusCshWriteOff> cshWriteOffList = writeOffMapper.selectByExample(writeOffExample);

        //通过核销表 找到 现金事务表 ，取到收款日期
        cshWriteOffList = cshWriteOffList.stream().filter(item ->
                cshTransactionMapper.selectByPrimaryKey(item.getCshTransactionId()).getTransactionDate().compareTo(calcDate) < 1).collect(Collectors.toList());


        //计算日之前所有的现金流
        Example cashflowExample = new Example(HlsCusConContractCashflow.class);
        cashflowExample.createCriteria().andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList(1, 10)).andLessThanOrEqualTo("dueDate", calcDate);
        List<HlsCusConContractCashflow> cashflowList = cashflowMapper.selectByExample(cashflowExample);
        //按照应收日排序 找最早逾期记录
        cashflowList = cashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).collect(Collectors.toList());


        Long offBalanceSheetDay = 0L;
        if (cashflowList.size() > 0) {
            //查找计算日之前所有的逾期现金流
            for (HlsCusConContractCashflow cashflow : cashflowList) {
                Double writeOffAmount = round(cshWriteOffList.stream().
                        filter(item -> cashflow.getCashflowId().compareTo(nvl(item.getCashflowId(), 0L)) == 0).
                        collect(Collectors.summingDouble(HlsCusCshWriteOff::getWriteOffDueAmount)), 2);

                if (writeOffAmount.compareTo(cashflow.getDueAmount()) == -1) {
                    Date overdueDate = cashflow.getDueDate();
                    long between = DateUtil.betweenDay(overdueDate, calcDate, true);
                    return between;
                }
            }
        }
        return offBalanceSheetDay;
    }
}