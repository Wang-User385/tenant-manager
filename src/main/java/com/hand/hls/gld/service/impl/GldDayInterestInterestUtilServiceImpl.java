package com.hand.hls.gld.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.dto.GldFinanceIncomeDayPeriod;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.formbean.AmortizationInterest;
import com.hand.hls.gld.mapper.ContractFinanceIncomeMapper;
import com.hand.hls.gld.mapper.GldFinanceIncomeDayMapper;
import com.hand.hls.gld.mapper.HlsCusContractFinanceIncomeMapper;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldDayInterestUtilService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.utils.HlsCusCheckNull;
import com.hand.hls.utils.HlsCusDateMethodUtil;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.HlsCusXirr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hand.hls.utils.HlsCusMathUtil.add;
import static com.hand.hls.utils.HlsCusMathUtil.sub;
import static com.hand.hls.utils.HlsCusMathUtil.mul;
import static com.hand.hls.utils.HlsCusMathUtil.div;
import static com.hand.hls.utils.HlsCusMathUtil.round;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/9
 * @description: 计算工具
 */
@Service
public class GldDayInterestInterestUtilServiceImpl implements IGldDayInterestUtilService {

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dfPeriodName = new SimpleDateFormat("yyyy-MM");
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final Long INTEREST_DAYS_360 = 360L;
    public static final Long INTEREST_DAYS_365 = 365L;


    public static final String LEASE = "LEASE";
    public static final String LEASEBACK = "LEASEBACK";
    public static final String SOURCE_DOCUMENT_CATEGORY = "CON_CONTRACT";

    /**
     * 按每1000个一组分割
     */
    private static final Integer MAX_NUMBER = 1000;


    private static Integer countStep(Integer size) {
        return (size + MAX_NUMBER - 1) / MAX_NUMBER;
    }


    @Autowired
    private IGldFinanceIncomeDayService gldFinanceIncomeDayService;
    @Autowired
    private GldFinanceIncomeDayMapper gldFinanceIncomeDayMapper;
    @Autowired
    private IContractFinanceIncomeService gldContractFinanceIncomeService;
    @Autowired
    private ContractFinanceIncomeMapper gldContractFinanceIncomeMapper;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsCusPrjQuotationMapper quotationMapper;


    /**
     * @Title: dateBetween
     * @Discription: 获取日期间隔  START = 2020/1/1 END = 2020/1/5   dateList = 2020/1/1 2020/1/2 2020/1/3 2020/1/4 2020/1/5
     * @Param: [start, end]
     * @Return: java.util.List<java.lang.String>
     */
    @Override
    public List<String> dateBetween(String start, String end) {
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
     * @Title: getDays
     * @Discription: 获取日期间隔天数
     * @Param: [startDate, endDate]
     * @Return: java.lang.Long
     */
    @Override
    public Long getDays(Date startDate, Date endDate) {
        return ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
    }

    /**
     * @Title: getDays
     * @Discription: 获取日期间隔天数 添加 算头不算尾标志
     * @Param: [startDate, endDate,isHeadNotTail]
     * @Return: java.lang.Long
     */
    @Override
    public Long getDays(Date startDate, Date endDate, Boolean isHeadNotTail) {
        if (isHeadNotTail) {
            return ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant()) + 1;
        }
        return getDays(startDate, endDate);
    }

    /**
     * @Title: lastMonthDate
     * @Discription: 获取 一个月最后一天
     * @Param: [periodName]
     * @Return: java.util.Date
     */
    @Override
    public Date lastMonthDate(String periodName) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dfPeriodName.parse(periodName));
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return calendar.getTime();
    }

    /**
     * @Title: firstMonthDate
     * @Discription: 获取 一个月第一天
     * @Param: [periodName]
     * @Return: java.util.Date
     */
    @Override
    public Date firstMonthDate(String periodName) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dfPeriodName.parse(periodName));
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * @Title: addDate
     * @Discription: 日期加减天数
     * @Param: [date, days]
     * @Return: java.util.Date
     */
    @Override
    public Date addDate(Date date, Long days) throws ParseException {
        LocalDate localDate = LocalDate.parse(df.format(date), dtf);
        localDate = localDate.plusDays(days);
        return df.parse(dtf.format(localDate));
    }

    /**
     * @Title: getAllCashflow
     * @Discription: 查询出所有与利息相关的现金流
     * @Param: [contractId, shareType]
     * @Return: java.util.List<com.hand.hls.cont.dto.HlsCusConContractCashflow>
     */
    @Override
    public List<HlsCusConContractCashflow> getAllCashflow(Long contractId, String shareType) {
        List<HlsCusConContractCashflow> cashflowList = new ArrayList<>();

        Example interestExample = new Example(HlsCusConContractCashflow.class);
        Example.Criteria interestCriteria = interestExample.createCriteria();
        //查询出所有与利息相关的现金流
        interestCriteria.andEqualTo("contractId", contractId).
                andIn("cfItem", Arrays.asList("1", "10")).
                andEqualTo("cfStatus", "RELEASE").
                andEqualTo("amortizationMethod", shareType);
        List<HlsCusConContractCashflow> interestCashflowList = cashflowMapper.selectByExample(interestExample);

        Example leaseItemExample = new Example(HlsCusConContractCashflow.class);
        Example.Criteria leaseItemCriteria = leaseItemExample.createCriteria();
        leaseItemCriteria.andEqualTo("contractId", contractId).
                andEqualTo("cfItem", "0").
                andEqualTo("cfStatus", "RELEASE");
        List<HlsCusConContractCashflow> leaseItemCashflowList = cashflowMapper.selectByExample(leaseItemExample);

        cashflowList.addAll(interestCashflowList);
        cashflowList.addAll(leaseItemCashflowList);
        return cashflowList;
    }


    /**
     * @Title: splitLeaseAccountDate
     * @Discription: //财务起租日需要拆分 成两段
     * @Param: [dayList, leaseAccountDate, startDate, endDate, periodName, dayPeriodList]
     * @Return: void
     */
    public void splitLeaseAccountDate(List<GldFinanceIncomeDay> dayList, Date leaseAccountDate, Date startDate, Date endDate, String periodName, List<GldFinanceIncomeDayPeriod> dayPeriodList) throws ParseException {


        Date preLeaseAccountDate = addDate(leaseAccountDate, -1L);

        GldFinanceIncomeDayPeriod preDayPeriod = new GldFinanceIncomeDayPeriod();


        preDayPeriod.setStartDate(startDate);
        preDayPeriod.setEndDate(preLeaseAccountDate);
        Long days = getDays(preDayPeriod.getStartDate(), preDayPeriod.getEndDate(), true);
        preDayPeriod.setDays(days);
        preDayPeriod.setPeriodName(periodName);
        preDayPeriod.setFinanceIncome(dayList.stream().filter(item -> item.getAmortizationDate().compareTo(preLeaseAccountDate) <= 0).collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount)));
        dayPeriodList.add(preDayPeriod);

        GldFinanceIncomeDayPeriod sufDayPeriod = new GldFinanceIncomeDayPeriod();

        sufDayPeriod.setStartDate(leaseAccountDate);
        sufDayPeriod.setEndDate(endDate);
        days = getDays(sufDayPeriod.getStartDate(), sufDayPeriod.getEndDate(), true);
        sufDayPeriod.setDays(days);
        sufDayPeriod.setPeriodName(periodName);
        sufDayPeriod.setFinanceIncome(dayList.stream().filter(item -> item.getAmortizationDate().compareTo(leaseAccountDate) >= 0).collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount)));
        dayPeriodList.add(sufDayPeriod);
    }

    /**
     * @Title: getPeriodStartDateAndEndDate
     * @Discription: 用于gld_contract_finance_income 中的 StartDate 和 EndDate
     * @Param: [dayList, leaseAccountDate, cfItem]
     * @Return: java.util.List<com.hand.hls.gld.dto.GldFinanceIncomeDayPeriod>
     */
    public List<GldFinanceIncomeDayPeriod> getPeriodStartDateAndEndDate(List<GldFinanceIncomeDay> dayList, Date leaseAccountDate, Long cfItem) throws ParseException {
        //判断日期是否跨月
        List<GldFinanceIncomeDayPeriod> dayPeriodList = new ArrayList<>();
        Date preLeaseAccountDate = addDate(leaseAccountDate, -1L);
        int size = dayList.stream().collect(Collectors.groupingBy(GldFinanceIncomeDay::getPeriodName)).size();
        //不跨月

        if (size == 1) {
            GldFinanceIncomeDayPeriod dayPeriod = new GldFinanceIncomeDayPeriod();
            dayPeriod.setStartDate(dayList.get(0).getAmortizationDate());
            dayPeriod.setEndDate(dayList.get(dayList.size() - 1).getAmortizationDate());

            //财务起租日需要拆分 成两段
            if (leaseAccountDate.compareTo(dayPeriod.getStartDate()) > 0 && leaseAccountDate.compareTo(dayPeriod.getEndDate()) < 0 && cfItem == 1L) {
                splitLeaseAccountDate(dayList, leaseAccountDate, dayPeriod.getStartDate(), dayPeriod.getEndDate(), dayList.get(0).getPeriodName(), dayPeriodList);
            } else {
                Long days = getDays(dayPeriod.getStartDate(), dayPeriod.getEndDate(), true);
                dayPeriod.setDays(days);
                dayPeriod.setPeriodName(dayList.get(0).getPeriodName());
                dayPeriod.setFinanceIncome(dayList.stream().collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount)));
                dayPeriodList.add(dayPeriod);
            }
        } else {
            List<String> periodNameList = new ArrayList<>();
            for (String periodName : dayList.stream().collect(Collectors.groupingBy(GldFinanceIncomeDay::getPeriodName)).keySet()) {
                periodNameList.add(periodName);
            }
            //排序
            periodNameList = periodNameList.stream().sorted().collect(Collectors.toList());
            //日期区间 dayList 的头尾日期 ，中间每个月的  第一天 和 最后一天
            for (int i = 0; i < periodNameList.size(); i++) {
                if (i == 0) {

                    GldFinanceIncomeDayPeriod dayPeriod = new GldFinanceIncomeDayPeriod();
                    //第一天
                    dayPeriod.setStartDate(dayList.get(0).getAmortizationDate());
                    //当月最后一天
                    dayPeriod.setEndDate(lastMonthDate(periodNameList.get(0)));
                    if (leaseAccountDate.compareTo(dayPeriod.getStartDate()) > 0 && leaseAccountDate.compareTo(dayPeriod.getEndDate()) < 0 && cfItem == 1L) {
                        splitLeaseAccountDate(dayList, leaseAccountDate, dayPeriod.getStartDate(), dayPeriod.getEndDate(), periodNameList.get(0), dayPeriodList);
                    } else {
                        dayPeriod.setDays(getDays(dayPeriod.getStartDate(), dayPeriod.getEndDate(), true));
                        dayPeriod.setPeriodName(periodNameList.get(0));
                        dayPeriod.setFinanceIncome(
                                round(dayList.stream().
                                        filter(item -> item.getAmortizationDate().compareTo(dayPeriod.getStartDate()) > -1 && item.getAmortizationDate().compareTo(dayPeriod.getEndDate()) < 1).
                                        collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount)), 2));
                        dayPeriodList.add(dayPeriod);
                    }
                } else {
                    //如果 最后一天 小于当月最后一天
                    if (dayList.get(dayList.size() - 1).getAmortizationDate().compareTo(lastMonthDate(periodNameList.get(i))) == -1) {
                        GldFinanceIncomeDayPeriod dayPeriod = new GldFinanceIncomeDayPeriod();
                        //当月第一天
                        dayPeriod.setStartDate(firstMonthDate(periodNameList.get(i)));
                        //最后一天
                        dayPeriod.setEndDate(dayList.get(dayList.size() - 1).getAmortizationDate());
                        if (leaseAccountDate.compareTo(dayPeriod.getStartDate()) > 0 && leaseAccountDate.compareTo(dayPeriod.getEndDate()) < 0 && cfItem == 1L) {
                            splitLeaseAccountDate(dayList, leaseAccountDate, dayPeriod.getStartDate(), dayPeriod.getEndDate(), periodNameList.get(0), dayPeriodList);
                        } else {
                            dayPeriod.setDays(getDays(dayPeriod.getStartDate(), dayPeriod.getEndDate(), true));
                            dayPeriod.setPeriodName(periodNameList.get(i));
                            dayPeriod.setFinanceIncome(
                                    round(dayList.stream().
                                            filter(item -> item.getAmortizationDate().compareTo(dayPeriod.getStartDate()) > -1 && item.getAmortizationDate().compareTo(dayPeriod.getEndDate()) < 1).
                                            collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount)), 2));
                            dayPeriodList.add(dayPeriod);
                        }
                    } else {
                        GldFinanceIncomeDayPeriod dayPeriod = new GldFinanceIncomeDayPeriod();
                        //当月第一天
                        dayPeriod.setStartDate(firstMonthDate(periodNameList.get(i)));
                        //当月最后一天
                        dayPeriod.setEndDate(lastMonthDate(periodNameList.get(i)));
                        if (leaseAccountDate.compareTo(dayPeriod.getStartDate()) > 0 && leaseAccountDate.compareTo(dayPeriod.getEndDate()) < 0 && cfItem == 1L) {
                            splitLeaseAccountDate(dayList, leaseAccountDate, dayPeriod.getStartDate(), dayPeriod.getEndDate(), periodNameList.get(i), dayPeriodList);
                        } else {
                            dayPeriod.setDays(getDays(dayPeriod.getStartDate(), dayPeriod.getEndDate(), true));
                            dayPeriod.setPeriodName(periodNameList.get(i));
                            dayPeriod.setFinanceIncome(
                                    round(dayList.stream().
                                            filter(item -> item.getAmortizationDate().compareTo(dayPeriod.getStartDate()) > -1 && item.getAmortizationDate().compareTo(dayPeriod.getEndDate()) < 1).
                                            collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount)), 2));
                            dayPeriodList.add(dayPeriod);
                        }
                    }
                }
            }
        }

        return dayPeriodList;
    }


    /**
     * @Title: insertGldFinanceIncomeDay
     * @Discription: 插入 gld_finance_income_day
     * @Param: [iRequest, dateList, contractId, gldFinanceIncomeDayCalc]
     * @Return: java.util.List<com.hand.hls.gld.dto.GldFinanceIncomeDay>
     */
    @Override
    public List<GldFinanceIncomeDay> insertGldFinanceIncomeDay(IRequest iRequest, Long contractId, AmortizationInterest amortizationInterest, String shareType) throws ParseException {
        //插值
        //先删除 再重新 插入
        /*Example exampleIncome = new Example(GldFinanceIncomeDay.class);
        Example.Criteria criteriaIncome = exampleIncome.createCriteria();
        criteriaIncome.andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList("1", "10"));
        Optional.ofNullable(gldFinanceIncomeDayMapper.selectByExample(exampleIncome)).
                ifPresent(item -> gldFinanceIncomeDayService.batchDelete(item));*/

        //优化效率
        GldFinanceIncomeDay incomeDay = new GldFinanceIncomeDay();
        incomeDay.setContractId(contractId);
        gldFinanceIncomeDayMapper.deleteGldFinanceIncomeDayInterest(incomeDay);


        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setSourceDocumentCategory(SOURCE_DOCUMENT_CATEGORY);
        prjQuotation.setSourceDocumentId(contractId);
        List<HlsCusPrjQuotation> prjQuotationList = quotationMapper.select(prjQuotation);
        //财务起租日
        Date leaseAccountDate = prjQuotationList.get(0).getLeaseAccountDate();

        List<String> dateList = amortizationInterest.getDateList();

        List<GldFinanceIncomeDay> gldFinanceIncomeDayList = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            Date amortizationDate = df.parse(dateList.get(i));
            GldFinanceIncomeDay gldFinanceIncomeDay = new GldFinanceIncomeDay();
            gldFinanceIncomeDay.setCompanyId(iRequest.getCompanyId());
            gldFinanceIncomeDay.setContractId(contractId);
            Long cashflowId = amortizationInterest.getDateCashflowId().get(amortizationDate);
            gldFinanceIncomeDay.setCfItem(cashflowMapper.selectByPrimaryKey(cashflowId).getCfItem());
            gldFinanceIncomeDay.setCashflowId(cashflowId);
            gldFinanceIncomeDay.setPeriodName(dfPeriodName.format(amortizationDate));
            gldFinanceIncomeDay.setAmortizationDate(amortizationDate);
            gldFinanceIncomeDay.setPreAmortizationAmount(Optional.ofNullable(amortizationInterest.getPreAmortizationAmountList().get(i)).orElse(0.0));
            gldFinanceIncomeDay.setAdjustmentAmount(0.0);
            if (GldFinInterestStraightServiceImpl.SHARE_TYPE.equals(shareType)) {
                gldFinanceIncomeDay.setAdjustmentAmount(Optional.ofNullable(amortizationInterest.getAdjustmentAmountList().get(i)).orElse(0.0));
            }
            gldFinanceIncomeDay.setAmortizationAmount(add(gldFinanceIncomeDay.getPreAmortizationAmount(), gldFinanceIncomeDay.getAdjustmentAmount(), 2));

            //如果cf_item = 10 或者1 ， AMORTIZATION_DATE < 财务起租日 ，gld_finance_income_day.pre_lease_interest_flag 更新为Y
            if (amortizationDate.compareTo(leaseAccountDate) == -1 && (gldFinanceIncomeDay.getCfItem() == 1 || gldFinanceIncomeDay.getCfItem() == 10)) {
                gldFinanceIncomeDay.setPreLeaseInterestFlag("Y");
            }

            //优化效率 改为批量提交
            //gldFinanceIncomeDay = gldFinanceIncomeDayService.insertSelective(iRequest, gldFinanceIncomeDay);
            gldFinanceIncomeDayList.add(gldFinanceIncomeDay);
        }

        //每1000条数据提交一次
        int limit = countStep(gldFinanceIncomeDayList.size());

        List<List<GldFinanceIncomeDay>> infoSplitList = Stream.iterate(0, n -> n + 1).limit(limit).parallel().map(a -> gldFinanceIncomeDayList.stream().skip(a * MAX_NUMBER).limit(MAX_NUMBER).parallel().collect(Collectors.toList())).collect(Collectors.toList());
        for (List<GldFinanceIncomeDay> list : infoSplitList) {
            gldFinanceIncomeDayMapper.insertGldFinanceIncomeDayBatch(list);
        }


        return gldFinanceIncomeDayList;
    }

    /**
     * @Title: insertGldContractFinanceIncome
     * @Discription: 插入 gld_contract_finance_income
     * @Param: [iRequest, contractId, gldFinanceIncomeDayList, cfItem]
     * @Return: void
     */
    @Override
    public void insertGldContractFinanceIncome(IRequest iRequest, Long contractId, List<GldFinanceIncomeDay> gldFinanceIncomeDayList, Long cfItem) throws ParseException {

        //按照现金流分组
        List<GldFinanceIncomeDay> dayList = new ArrayList<>();
        List<GldFinanceIncomeDayPeriod> periodList = new ArrayList<>();
        //删除掉未确认的数据
        /*Example exampleIncome = new Example(HlsCusContractFinanceIncome.class);
        Example.Criteria criteriaIncome = exampleIncome.createCriteria();
        if (cfItem == 1) {
            criteriaIncome.andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList("1", "10")).andEqualTo("postFlag", "N");
        } else {
            criteriaIncome.andEqualTo("contractId", contractId).andEqualTo("cfItem", cfItem).andEqualTo("postFlag", "N");
        }
        Optional.ofNullable(gldContractFinanceIncomeMapper.selectByExample(exampleIncome)).
                ifPresent(item -> gldContractFinanceIncomeService.batchDelete(item));*/

        //需要记录分摊前数据  当现金流发生变化，已经确认的部分 会产生差额 需要将差额 补充到 最近一期没有确认的部分
        HlsCusContractFinanceIncome beforeIncome = new HlsCusContractFinanceIncome();
        beforeIncome.setContractId(contractId);
        List<HlsCusContractFinanceIncome> beforeIncomeList = gldContractFinanceIncomeMapper.select(beforeIncome);

        Map<Long, Date> beforeStartDateMap = new HashMap<>();
        Map<Long, Double> beforeFinanceIncomeMap = new HashMap<>();

        if (beforeIncomeList.size() > 0) {
            //已确认部分 金额合计 按照现金流类型分组
            beforeFinanceIncomeMap = beforeIncomeList.stream().filter(item -> item.getPostFlag().equals("Y")).
                    collect(Collectors.groupingBy(HlsCusContractFinanceIncome::getCfItem, Collectors.summingDouble(HlsCusContractFinanceIncome::getFinanceIncome)));

            Set<Long> beforeCfItems = beforeIncomeList.stream().filter(item -> item.getPostFlag().equals("Y")).
                    collect(Collectors.groupingBy(HlsCusContractFinanceIncome::getCfItem)).keySet();

            //找到除去 已确认的部分，未确认部分的最小日期，用来插入差异值
            for (Long beforeCfItem : beforeCfItems) {
                Date startDate = beforeIncomeList.stream().filter(item -> beforeCfItem.compareTo(item.getCfItem()) == 0 && item.getPostFlag().equals("N")).
                        min(Comparator.comparing(HlsCusContractFinanceIncome::getStartDate)).get().getStartDate();
                beforeStartDateMap.put(beforeCfItem, startDate);
            }
        }

        //优化效率
        HlsCusContractFinanceIncome contractFinanceIncome = new HlsCusContractFinanceIncome();
        contractFinanceIncome.setContractId(contractId);
        contractFinanceIncome.setPostFlag("N");
        contractFinanceIncome.setCfItem(cfItem);
        if (cfItem == 1) {
            gldContractFinanceIncomeMapper.deleteGldContractFinanceIncomeInterest(contractFinanceIncome);
        } else {
            gldContractFinanceIncomeMapper.deleteGldContractFinanceIncomeCost(contractFinanceIncome);
        }


        List<HlsCusContractFinanceIncome> contractFinanceIncomeList = new ArrayList<>();

        HlsCusConContract conContract = contractMapper.selectByPrimaryKey(contractId);
        HlsCusPrjQuotation prjQuotation = quotationMapper.selectByPrimaryKey(conContract.getQuotationId());

        for (Long cashflowId : gldFinanceIncomeDayList.stream().collect(Collectors.groupingBy(GldFinanceIncomeDay::getCashflowId)).keySet()) {
            //根据月份 和 应收日期 分段插入
            dayList = gldFinanceIncomeDayList.stream().filter(item -> item.getCashflowId().compareTo(cashflowId) == 0).sorted(Comparator.comparing(GldFinanceIncomeDay::getAmortizationDate)).collect(Collectors.toList());

            periodList = getPeriodStartDateAndEndDate(dayList, prjQuotation.getLeaseAccountDate(), cfItem);

            for (GldFinanceIncomeDayPeriod period : periodList) {
                HlsCusContractFinanceIncome gldContractFinanceIncome = new HlsCusContractFinanceIncome();
                gldContractFinanceIncome.setCompanyId(iRequest.getCompanyId());
                gldContractFinanceIncome.setContractId(contractId);
                gldContractFinanceIncome.setGldCashflowId(cashflowId);
                gldContractFinanceIncome.setCfItem(cashflowMapper.selectByPrimaryKey(cashflowId).getCfItem());
                gldContractFinanceIncome.setPeriodName(period.getPeriodName());
                gldContractFinanceIncome.setStartDate(period.getStartDate());
                gldContractFinanceIncome.setEndDate(period.getEndDate());
                gldContractFinanceIncome.setDays(period.getDays());
                gldContractFinanceIncome.setFinanceIncome(period.getFinanceIncome());
                gldContractFinanceIncome.setSourceType("CON_CONTRACT");
                gldContractFinanceIncome.setSourceId(contractId);
                gldContractFinanceIncome.setPostFlag("N");
                //优化效率
                //gldContractFinanceIncomeService.insertSelective(iRequest, gldContractFinanceIncome);
                contractFinanceIncomeList.add(gldContractFinanceIncome);
            }
        }
        //算头不算尾
        //最后一条记录的天数 要减一
        HlsCusContractFinanceIncome lastFinanceIncome = contractFinanceIncomeList.stream().sorted(Comparator.comparing(HlsCusContractFinanceIncome::getEndDate).reversed()).findFirst().get();

        contractFinanceIncomeList.stream().sorted(Comparator.comparing(HlsCusContractFinanceIncome::getEndDate).reversed()).findFirst().get().setDays(lastFinanceIncome.getDays() - 1);

        //每1000条数据提交一次
        int limit = countStep(contractFinanceIncomeList.size());

        List<List<HlsCusContractFinanceIncome>> infoSplitList = Stream.iterate(0, n -> n + 1).limit(limit).parallel().map(a -> contractFinanceIncomeList.stream().skip(a * MAX_NUMBER).limit(MAX_NUMBER).parallel().collect(Collectors.toList())).collect(Collectors.toList());
        for (List<HlsCusContractFinanceIncome> list : infoSplitList) {
            gldContractFinanceIncomeMapper.insertGldContractFinanceIncomeBatch(list);
        }

        //

        //删除掉重复的部分
        Example deleteExample = new Example(HlsCusContractFinanceIncome.class);
        if (cfItem == 1) {
            deleteExample.createCriteria().andEqualTo("contractId", contractId).andIn("cfItem", Arrays.asList("1", "10")).andEqualTo("postFlag", "Y");
        } else {
            deleteExample.createCriteria().andEqualTo("contractId", contractId).andEqualTo("cfItem", cfItem).andEqualTo("postFlag", "Y");
        }
        List<HlsCusContractFinanceIncome> gldDeleteIncomeList = gldContractFinanceIncomeMapper.selectByExample(deleteExample);

        List<HlsCusContractFinanceIncome> afterIncomeList = new ArrayList<>();


        for (Date startDate : gldDeleteIncomeList.stream().collect(Collectors.groupingBy(HlsCusContractFinanceIncome::getStartDate)).keySet()) {
            HlsCusContractFinanceIncome gldContractFinanceIncome = new HlsCusContractFinanceIncome();
            gldContractFinanceIncome.setContractId(contractId);
            gldContractFinanceIncome.setStartDate(startDate);
            gldContractFinanceIncome.setPostFlag("N");
            List<HlsCusContractFinanceIncome> deleteList = gldContractFinanceIncomeMapper.select(gldContractFinanceIncome);

            afterIncomeList.addAll(deleteList);
            //Optional.ofNullable(deleteList).ifPresent(item -> gldContractFinanceIncomeService.batchDelete(item));
        }

        Optional.ofNullable(afterIncomeList).ifPresent(item -> gldContractFinanceIncomeService.batchDelete(item));

        if (afterIncomeList.size() > 0) {
            Map<Long, Double> afterFinanceIncomeMap = afterIncomeList.stream().
                    collect(Collectors.groupingBy(HlsCusContractFinanceIncome::getCfItem, Collectors.summingDouble(HlsCusContractFinanceIncome::getFinanceIncome)));

            for (Long afterCfItem : afterFinanceIncomeMap.keySet()) {
                if (beforeFinanceIncomeMap.get(afterCfItem) != null) {
                    Double amount = sub(afterFinanceIncomeMap.get(afterCfItem), beforeFinanceIncomeMap.get(afterCfItem), 2);
                    Date date = beforeStartDateMap.get(afterCfItem);

                    HlsCusContractFinanceIncome afterFinanceIncome = new HlsCusContractFinanceIncome();
                    afterFinanceIncome.setContractId(contractId);
                    afterFinanceIncome.setCfItem(afterCfItem);
                    afterFinanceIncome.setStartDate(date);
                    List<HlsCusContractFinanceIncome> afterFinanceIncomeList = gldContractFinanceIncomeMapper.select(afterFinanceIncome);

                    afterFinanceIncome = afterFinanceIncomeList.get(0);
                    afterFinanceIncome.setFinanceIncomeId(null);
                    afterFinanceIncome.setFinanceIncome(amount);
                    gldContractFinanceIncomeMapper.insertSelective(afterFinanceIncome);
                }
            }
        }


    }

    /**
     * @Title: setDueDateNetInterest
     * @Discription: 设置 排除掉放款现金流的  所有应收日的利息，如果没有利息取应收金额(用于尾差计算)
     * @Param: [cashflowList, leaseStartDate, gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    public AmortizationInterest setDueDateNetInterest(List<HlsCusConContractCashflow> cashflowList, Date leaseStartDate, AmortizationInterest amortizationInterest) {
        Map<Date, Double> dueDateNetInterest = new HashMap<>();
        dueDateNetInterest.put(leaseStartDate, 0.0);
        cashflowList.stream().filter(item -> item.getCfItem() != 0).forEach(item -> {
            if (HlsCusCheckNull.isNull(item.getInterest())) {
                dueDateNetInterest.put(item.getDueDate(), item.getNetDueAmount());
            } else {
                dueDateNetInterest.put(item.getDueDate(), item.getNetInterest());
            }
        });
        amortizationInterest.setDueDateNetInterest(dueDateNetInterest);
        return amortizationInterest;
    }

    /**
     * @Title: setDateCashflowId
     * @Discription: 设置 日期 与 现金流 的关系
     * @Param: [cashflowList, dateList, lastCalcCashflow, gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    public AmortizationInterest setDateCashflowId(List<HlsCusConContractCashflow> cashflowList, List<String> dateList,
                                                  HlsCusConContractCashflow lastCalcCashflow,
                                                  AmortizationInterest amortizationInterest) throws ParseException {

        Map<Date, Long> dateCashflowId = new HashMap<>();
        for (String date : dateList) {
            cashflowList.stream().filter(item -> item.getCfItem() != 0).forEach(item -> {
                try {
                    if (item.getDueDate().equals(df.parse(date))) {
                        //放款日和起租日 同一天
                        if (dateList.indexOf(date) == 0) {
                            dateCashflowId.put(df.parse(dateList.get(dateList.indexOf(date))), item.getCashflowId());
                        } else {
                            dateCashflowId.put(df.parse(dateList.get(dateList.indexOf(date) - 1)), item.getCashflowId());
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
            //最后一天
            if (lastCalcCashflow.getDueDate().equals(df.parse(date))) {
                dateCashflowId.put(lastCalcCashflow.getDueDate(), lastCalcCashflow.getCashflowId());
            }

        }

        //倒叙排列
        List<String> dateListReverse = dateList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        Long cashflowId = null;
        for (String date : dateListReverse) {
            if (dateCashflowId.get(df.parse(date)) != null) {
                cashflowId = dateCashflowId.get(df.parse(date));
            } else {
                dateCashflowId.put(df.parse(date), cashflowId);
            }
        }

        amortizationInterest.setDateCashflowId(dateCashflowId);
        return amortizationInterest;
    }

    /**
     * @Title: getAmortizationCashflow
     * @Discription: 获取需要计算的现金流 给 amortizationInterest 赋值
     * @Param: [cashflowList, gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    @Override
    public AmortizationInterest getAmortizationCashflow(List<HlsCusConContractCashflow> cashflowList, AmortizationInterest amortizationInterest) throws ParseException {


        HlsCusConContractCashflow firstCalcCashflow =
                cashflowList.stream().filter(item -> item.getCfItem().compareTo(0L) == 0).sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).findFirst().get();
        //最早一期放款日
        Date leaseStartDate = firstCalcCashflow.getDueDate();
        //设置第一条 剩余本金
        Double outstandingPrincipal = firstCalcCashflow.getOutstandingPrincipal();
        List<Double> outstandingPrincipalList = new ArrayList<>(1);
        outstandingPrincipalList.add(outstandingPrincipal);
        amortizationInterest.setOutstandingPrincipalList(outstandingPrincipalList);

        HlsCusConContractCashflow lastCalcCashflow = cashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate).reversed()).findFirst().get();
        //最后一期收款日
        Date leaseEndDate = lastCalcCashflow.getDueDate();
        //获取这一段时间的所有的现金流
        cashflowList = cashflowList.stream().filter(item -> item.getDueDate().compareTo(leaseStartDate) >= 0 && item.getDueDate().compareTo(leaseEndDate) < 1).sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).collect(Collectors.toList());
        //设置摊销日期
        List<String> dateList = dateBetween(df.format(leaseStartDate), df.format(leaseEndDate));

        //设置 所有的日期间隔
        amortizationInterest.setDateList(dateList);

        //设置 排除掉放款现金流的  所有应收日的利息，如果没有利息取应收金额(用于尾差计算)
        amortizationInterest = setDueDateNetInterest(cashflowList, leaseStartDate, amortizationInterest);

        //设置 每一期利息调整金额
        amortizationInterest = setDueDateAdjustmentAmountMap(cashflowList, leaseStartDate, amortizationInterest);

        //设置 日期与现金流 的关系 (用于给每一条记录 赋值现金流id)
        amortizationInterest = setDateCashflowId(cashflowList, dateList,
                lastCalcCashflow, amortizationInterest);

        return amortizationInterest;
    }


    /**
     * @Title: calcPreAmortizationAmount
     * @Discription: 计算 日摊销金额（调整前）
     * @Param: [gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    @Override
    public AmortizationInterest calcPreAmortizationAmount(AmortizationInterest amortizationInterest) throws ParseException {

        List<String> dateList = amortizationInterest.getDateList();
        List<Double> outstandingPrincipalList = amortizationInterest.getOutstandingPrincipalList();
        List<Double> preAmortizationAmountList = new ArrayList<>();
        Long interestDays = amortizationInterest.getInterestDays();
        Double intRate = amortizationInterest.getIntRate();
        Double taxRate = amortizationInterest.getTaxRate();
        Map<Date, Double> dueDateNetInterest = amortizationInterest.getDueDateNetInterest();
        List<Date> dueDateList = new ArrayList<>();

        for (Date dueDate : dueDateNetInterest.keySet()) {
            dueDateList.add(dueDate);
        }

        dueDateList = dueDateList.stream().sorted().collect(Collectors.toList());


        for (int i = 0; i < dateList.size(); i++) {
            Optional.ofNullable(amortizationInterest.getInterestDays()).orElse(INTEREST_DAYS_360);
            Double preAmortizationAmount = div(div(mul(outstandingPrincipalList.get(i), intRate), interestDays), add(1, taxRate), 2);
            preAmortizationAmountList.add(i, preAmortizationAmount);

            //计算尾差
            Double tail = 0.0;
            Double interest = dueDateNetInterest.get(df.parse(dateList.get(i)));

            if (!HlsCusCheckNull.isNull(interest) && interest.compareTo(0.0) != 0) {

                int index = dueDateList.indexOf(df.parse(dateList.get(i)));

                Date startDate = dueDateList.get(index - 1);
                Date endDate = dueDateList.get(index);

                int startIndex = dateList.indexOf(df.format(startDate));
                int endIndex = dateList.indexOf(df.format(endDate));
                Double amount = 0.0;
                for (int j = startIndex; j < endIndex; j++) {
                    amount = add(amount, preAmortizationAmountList.get(j));
                }
                tail = sub(interest, amount);

                preAmortizationAmountList.set(i - 1, add(preAmortizationAmountList.get(i - 1), tail));
            }
        }
        amortizationInterest.setPreAmortizationAmountList(preAmortizationAmountList);


        return amortizationInterest;
    }

    /**
     * @Title: setDueDateAdjustmentAmount
     * @Discription: 设置 每一期的利息调整金额
     * @Param: [cashflowList, amortizationInterest]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    public AmortizationInterest setDueDateAdjustmentAmountMap(List<HlsCusConContractCashflow> cashflowList, Date leaseStartDate, AmortizationInterest amortizationInterest) {
        Map<Date, Double> dueDateAdjustmentAmountMap = new HashMap<>();
        dueDateAdjustmentAmountMap.put(leaseStartDate, 0.0);
        //利息或者租前息
        cashflowList.stream().filter(item -> item.getCfItem() == 1L || item.getCfItem() == 10L).forEach(item -> {
            if (!HlsCusCheckNull.isNull(item.getInterestAdjustAmount())) {
                dueDateAdjustmentAmountMap.put(item.getDueDate(), item.getInterestAdjustAmount());
            } else {
                dueDateAdjustmentAmountMap.put(item.getDueDate(), 0.0);
            }
        });
        amortizationInterest.setDueDateAdjustmentAmount(dueDateAdjustmentAmountMap);
        return amortizationInterest;
    }

    /**
     * @Title: calcAdjustmentAmount
     * @Discription: 计算 日调整额 直线法
     * @Param: [amortizationInterest]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    @Override
    public AmortizationInterest calcAdjustmentAmount(AmortizationInterest amortizationInterest) throws ParseException {

        List<String> dateList = amortizationInterest.getDateList();
        List<Double> ajustmentAmountList = new ArrayList<>();
        Double taxRate = amortizationInterest.getTaxRate();
        Map<Date, Double> dueDateAdjustmentAmountMap = amortizationInterest.getDueDateAdjustmentAmount();

        List<Date> dueDateList = new ArrayList<>();
        for (Date dueDate : dueDateAdjustmentAmountMap.keySet()) {
            dueDateList.add(dueDate);
        }
        dueDateList = dueDateList.stream().sorted().collect(Collectors.toList());

        if (dueDateList.size() > 0) {
            for (int i = 0; i < dueDateList.size() - 1; i++) {
                Double adjustmentAmount = dueDateAdjustmentAmountMap.get(dueDateList.get(i + 1));
                Double netAdjustmentAmount = div(adjustmentAmount, add(1, taxRate), 2);

                Date startDate = dueDateList.get(i);
                Date endDate = dueDateList.get(i + 1);

                int startIndex = dateList.indexOf(df.format(startDate));
                int endIndex = dateList.indexOf(df.format(endDate));
                Double sumAmount = 0.0;
                for (int j = startIndex; j < endIndex; j++) {
                    Double amount = div(netAdjustmentAmount, getDays(startDate, endDate), 2);
                    sumAmount = add(sumAmount, amount);
                    ajustmentAmountList.add(amount);

                    //计算尾差
                    Double tail = 0.0;
                    if (j == endIndex - 1) {
                        tail = sub(netAdjustmentAmount, sumAmount);
                        ajustmentAmountList.set(j, add(ajustmentAmountList.get(j), tail));
                    }
                }
            }
            //最后一天 为0
            ajustmentAmountList.add(0.0);
            amortizationInterest.setAdjustmentAmountList(ajustmentAmountList);
        }
        return amortizationInterest;
    }


    /**
     * @Title: setOutstandingPrincipalList
     * @Discription: 获取每一天的剩余本金
     * @Param: [cashflowList, gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    @Override
    public AmortizationInterest getOutstandingPrincipalList(List<HlsCusConContractCashflow> cashflowList, AmortizationInterest amortizationInterest) throws ParseException {
        List<Double> outstandingPrincipalList = amortizationInterest.getOutstandingPrincipalList();
        List<String> dateList = amortizationInterest.getDateList();

        for (int i = 1; i < dateList.size(); i++) {
            //每一天的摊销日
            Date amortizationDate = df.parse(dateList.get(i));
            //判断 核销日 与 现金流应收日 相同的一天
            Long count = cashflowList.stream().filter(item -> item.getDueDate().equals(amortizationDate)).count();
            if (count > 0) {
                //如果相同 取现金流上的剩余本金字段
                Double amount = cashflowList.stream().filter(item -> item.getDueDate().equals(amortizationDate)).findFirst().get().getOutstandingPrincipal();
                if (HlsCusCheckNull.isNull(amount)) {
                    //如果金额是空，取前一天的剩余本金
                    outstandingPrincipalList.add(i, outstandingPrincipalList.get(i - 1));
                } else {
                    outstandingPrincipalList.add(i, amount);
                }
            } else {
                outstandingPrincipalList.add(i, outstandingPrincipalList.get(i - 1));
            }
        }
        amortizationInterest.setOutstandingPrincipalList(outstandingPrincipalList);
        return amortizationInterest;
    }


    /**
     * @Title: getAmortizationInterest
     * @Discription: 构造AmortizationInterest 方便后续插入业务表
     * @Param: [contractId, shareType]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    @Override
    public AmortizationInterest getAmortizationInterest(Long contractId, String shareType) throws ParseException {
        AmortizationInterest amortizationInterest = new AmortizationInterest();
        HlsCusConContract contract = contractMapper.selectByPrimaryKey(contractId);

        List<HlsCusConContractCashflow> cashflowList = getAllCashflow(contract.getContractId(), shareType);

        Double netInterestTotal = cashflowList.stream().filter(item -> item.getCfItem() == 1L || item.getCfItem() == 10L).
                collect(Collectors.summingDouble(HlsCusConContractCashflow::getNetInterest));

        amortizationInterest.setNetInterestTotal(netInterestTotal);

        amortizationInterest = getAmortizationCashflow(cashflowList, amortizationInterest);

        amortizationInterest = getOutstandingPrincipalList(cashflowList, amortizationInterest);

        amortizationInterest.setIntRate(contract.getIntRate());

        amortizationInterest.setInterestDays(GldDayInterestInterestUtilServiceImpl.INTEREST_DAYS_360);

        //光大环境搬迁过来时，他们就是通过vatRate 表示 合同税率
        amortizationInterest.setTaxRate(contract.getVatRate());

        amortizationInterest = calcPreAmortizationAmount(amortizationInterest);

        amortizationInterest = calcAdjustmentAmount(amortizationInterest);

        return amortizationInterest;
    }

    /**
     * @Title: getIntRateDay
     * @Discription: 通过xirr 反推日利率
     * @Param: [contractId]
     * @Return: java.lang.Double
     */
    @Override
    public AmortizationInterest getIntRateDay(Long contractId, String shareType) {
        AmortizationInterest amortizationInterest = new AmortizationInterest();

        //构造xirr 现金流
        HlsCusConContract contract = contractMapper.selectByPrimaryKey(contractId);
        //HlsCusPrjQuotation prjQuotation = quotationMapper.selectByPrimaryKey(contract.getQuotationId());

        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(contractId);
        HlsCusPrjQuotation prjQuotation = quotationMapper.queryQuotationByConId(hlsCusPrjQuotation);


        String businessType = prjQuotation.getBusinessType();
        Double vatRate = contract.getVatRate();

        //（废弃）获取 起租日及各期租金日 起租日现金流=-起租时点未回收成本，其他各期现金流=不含税本金+不含税利息
        //修改上述描述逻辑 不取起租日及各期租金日 改为 放款日及各期租金日
        Example outstandingPrincipalExample = new Example(HlsCusConContractCashflow.class);
        outstandingPrincipalExample.createCriteria().
                andEqualTo("contractId", contract.getContractId()).
                andEqualTo("cfItem", "0").
                andEqualTo("cfStatus", "RELEASE");

        List<HlsCusConContractCashflow> outstandingPrincipalCashflowList = cashflowMapper.selectByExample(outstandingPrincipalExample);

        Example rentalExample = new Example(HlsCusConContractCashflow.class);
        rentalExample.createCriteria().
                andEqualTo("contractId", contract.getContractId()).
                andIn("cfItem", Arrays.asList("1", "10")).
                andEqualTo("amortizationMethod", shareType).
                andEqualTo("cfStatus", "RELEASE");

        List<HlsCusConContractCashflow> rentalCashflowList = cashflowMapper.selectByExample(rentalExample);
        Double netInterestTotal = rentalCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getNetInterest));

        //按日期排序
        rentalCashflowList = rentalCashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).collect(Collectors.toList());


        Date[] dateArrays = new Date[rentalCashflowList.size() + outstandingPrincipalCashflowList.size()];
        double[] amountArrays = new double[rentalCashflowList.size() + outstandingPrincipalCashflowList.size()];

        //获取直租回租 如果直租 取的剩余本金金额 需要去税 如果是回租 则不需要 （不含税剩余本金）
        outstandingPrincipalCashflowList.stream().forEach(item -> {
            if (LEASE.equals(businessType)) {
                item.setOutstandingPrincipal(div(item.getOutstandingPrincipal(), (1 + vatRate), 2));
            }
        });

        Double outstandingPrincipal = outstandingPrincipalCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getOutstandingPrincipal));
        //dateArrays[0] = outstandingPrincipalCashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).findFirst().get().getDueDate();
        //amountArrays[0] = -1 * outstandingPrincipal;

        int index = 0;
        for (HlsCusConContractCashflow outPrincipal : outstandingPrincipalCashflowList) {
            dateArrays[index] = outPrincipal.getDueDate();
            amountArrays[index] = -1 * outPrincipal.getOutstandingPrincipal();
            index++;
        }

        for (HlsCusConContractCashflow rental : rentalCashflowList) {
            dateArrays[index] = rental.getDueDate();
            amountArrays[index] = rental.getNetDueAmount();
            index++;
        }

        //不含税xirr
        Double xirr = HlsCusXirr.Newtons_method(0.1, amountArrays, dateArrays);

        //根据不含税xirr 反推出 XIRR对应日利率
        double intRateDay = sub(Math.pow((1 + xirr), (div(1, INTEREST_DAYS_365, 10))), 1, 10);

        amortizationInterest.setIntRateDay(intRateDay);
        amortizationInterest.setOutstandingPrincipal(outstandingPrincipal);
        amortizationInterest.setNetInterestTotal(netInterestTotal);
        return amortizationInterest;
    }


    /**
     * @Title: calcPreAmortizationAmount
     * @Discription: 计算分摊利息
     * @Param: [contractId, amortizationInterest]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    public AmortizationInterest calcPreAmortizationAmount(Long contractId, AmortizationInterest amortizationInterest) throws ParseException {
        List<String> dateList = amortizationInterest.getDateList();
        List<Double> preAmortizationAmountList = new ArrayList<>();
        Map<Date, Double> dueDateNetDueAmount = amortizationInterest.getDueDateNetDueAmount();
        Double amortizationCostAmt = 0.0;
        Double amortizationAmount = 0.0;
        Double amortizationAmountTotal = 0.0;
        for (int i = 0; i < dateList.size(); i++) {
            //调整后摊余成本
            //起租日
            if (i == 0) {
                amortizationCostAmt = amortizationInterest.getOutstandingPrincipal();
                amortizationAmount = mul(amortizationCostAmt, amortizationInterest.getIntRateDay(), 2);
            }
            //租赁期限到期日倒数第二天计提利息需进行轧差
            else if (i == dateList.size() - 2) {
                amortizationAmount = sub(amortizationInterest.getNetInterestTotal(), amortizationAmountTotal, 2);
                //算头不算尾 所以最后一天 应该是0
            } else if (i == dateList.size() - 1) {
                amortizationAmount = 0.0;
            } else {
                Double netDueAmount = dueDateNetDueAmount.get(df.parse(dateList.get(i))) == null ? 0.0 : dueDateNetDueAmount.get(df.parse(dateList.get(i)));
                amortizationCostAmt = sub(add(amortizationCostAmt, amortizationAmount), netDueAmount, 2);
                amortizationAmount = mul(amortizationCostAmt, amortizationInterest.getIntRateDay(), 2);
            }
            amortizationAmountTotal = add(amortizationAmountTotal, amortizationAmount, 2);
            preAmortizationAmountList.add(i, amortizationAmount);
        }
        amortizationInterest.setPreAmortizationAmountList(preAmortizationAmountList);
        return amortizationInterest;
    }

    /**
     * @Title: setDueDateNetDueAmount
     * @Discription: 设置 排除掉放款现金流的  所有应收日的不含税租金
     * @Param: [cashflowList, leaseStartDate, gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    public AmortizationInterest setDueDateNetDueAmount(List<HlsCusConContractCashflow> cashflowList, AmortizationInterest amortizationInterest) {
        Map<Date, Double> dueDateNetDueAmount = new HashMap<>();
        cashflowList.stream().filter(item -> item.getCfItem() != 0).forEach(item -> {
            dueDateNetDueAmount.put(item.getDueDate(), item.getNetDueAmount());
        });
        amortizationInterest.setDueDateNetDueAmount(dueDateNetDueAmount);
        return amortizationInterest;
    }

    /**
     * @Title: getAmortizationCashflow
     * @Discription: 获取需要计算的现金流 给 amortizationInterest 赋值
     * @Param: [contractId, amortizationInterest, shareType]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    @Override
    public AmortizationInterest getAmortizationCashflow(Long contractId, AmortizationInterest amortizationInterest, String shareType) throws ParseException {
        HlsCusConContract contract = contractMapper.selectByPrimaryKey(contractId);
        List<HlsCusConContractCashflow> cashflowList = getAllCashflow(contract.getContractId(), shareType);

        HlsCusConContractCashflow firstCalcCashflow =
                cashflowList.stream().filter(item -> item.getCfItem().compareTo(0L) == 0).sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).findFirst().get();

        //最早一期放款日
        Date leaseStartDate = firstCalcCashflow.getDueDate();

        //设置摊销日期
        List<String> dateList = dateBetween(df.format(leaseStartDate), df.format(contract.getLeaseEndDate()));
        //设置 所有的日期间隔
        amortizationInterest.setDateList(dateList);


        HlsCusConContractCashflow lastCalcCashflow = cashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate).reversed()).findFirst().get();

        //设置 日期与现金流 的关系 (用于给每一条记录 赋值现金流id)
        amortizationInterest = setDateCashflowId(cashflowList, dateList,
                lastCalcCashflow, amortizationInterest);

        //设置 排除掉放款现金流的  所有应收日的不含税租金
        amortizationInterest = setDueDateNetDueAmount(cashflowList, amortizationInterest);

        //计算分摊利息
        amortizationInterest = calcPreAmortizationAmount(contractId, amortizationInterest);

        return amortizationInterest;
    }
}

