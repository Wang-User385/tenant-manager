package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.formbean.AmortizationInterest;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/9
 * @description: 利息分摊工具
 */
public interface IGldDayInterestUtilService {
    /**
     * @Title: dateBetween
     * @Discription: 获取日期间隔  START = 2020/1/1 END = 2020/1/5   dateList = 2020/1/1 2020/1/2 2020/1/3 2020/1/4 2020/1/5
     * @Param: [start, end]
     * @Return: java.util.List<java.lang.String>
     */
    List<String> dateBetween(String start, String end);

    /**
     * @Title: getDays
     * @Discription: 获取日期间隔天数
     * @Param: [startDate, endDate]
     * @Return: java.lang.Long
     */
    Long getDays(Date startDate, Date endDate);


    /**
     * @Title: getDays
     * @Discription: 获取日期间隔天数 添加 算头不算尾标志
     * @Param: [startDate, endDate,isHeadNotTail]
     * @Return: java.lang.Long
     */
    Long getDays(Date startDate, Date endDate, Boolean isHeadNotTail);


    /**
     * @Title: lastMonthDate
     * @Discription: 获取 一个月最后一天
     * @Param: [periodName]
     * @Return: java.util.Date
     */
    Date lastMonthDate(String periodName) throws ParseException;

    /**
     * @Title: firstMonthDate
     * @Discription: 获取 一个月第一天
     * @Param: [periodName]
     * @Return: java.util.Date
     */
    Date firstMonthDate(String periodName) throws ParseException;

    /**
     * @Title: addDate
     * @Discription: 日期加减天数
     * @Param: [date, days]
     * @Return: java.util.Date
     */
    Date addDate(Date date,Long days) throws ParseException;

    /**
     * @Title: getAllCashflow
     * @Discription:  查询出所有与利息相关的现金流
     * @Param: [contractId, shareType]
     * @Return: java.util.List<com.hand.hls.cont.dto.HlsCusConContractCashflow>
     */
    List<HlsCusConContractCashflow> getAllCashflow(Long contractId, String shareType);

    /**
     * @Title: insertGldFinanceIncomeDay
     * @Discription: 插入 gld_finance_income_day
     * @Param: [iRequest, contractId, gldFinanceIncomeDayCalc]
     * @Return: java.util.List<com.hand.hls.gld.dto.GldFinanceIncomeDay>
     */
    List<GldFinanceIncomeDay> insertGldFinanceIncomeDay(IRequest iRequest,Long contractId, AmortizationInterest amortizationInterest, String shareType) throws ParseException;
    /**
     * @Title: insertGldContractFinanceIncome
     * @Discription: 插入 GLD_CONTRACT_FINANCE_INCOME
     * @Param: [iRequest, contractId, gldFinanceIncomeDayList, cfItem]
     * @Return: void
     */
    void insertGldContractFinanceIncome(IRequest iRequest, Long contractId, List<GldFinanceIncomeDay> gldFinanceIncomeDayList,Long cfItem) throws ParseException;

    /**
     * @Title: getAmortizationCashflow
     * @Discription: 获取需要计算的现金流 给 amortizationInterest 赋值
     * @Param: [cashflowList, gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    AmortizationInterest getAmortizationCashflow(List<HlsCusConContractCashflow> cashflowList, AmortizationInterest amortizationInterest) throws ParseException;

    /**
     * @Title: setOutstandingPrincipalList
     * @Discription: 获取每一天的剩余本金
     * @Param: [cashflowList, gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    AmortizationInterest getOutstandingPrincipalList(List<HlsCusConContractCashflow> cashflowList, AmortizationInterest amortizationInterest) throws ParseException;

    /**
     * @Title: calcPreAmortizationAmount
     * @Discription: 计算 日摊销金额（调整前）
     * @Param: [gldFinanceIncomeDayCalc]
     * @Return: com.hand.hls.gld.dto.GldFinanceIncomeDayCalc
     */
    AmortizationInterest calcPreAmortizationAmount(AmortizationInterest amortizationInterest) throws ParseException;


    /**
     * @Title: calcAdjustmentAmount
     * @Discription: 计算 日调整额
     * @Param: [amortizationInterest]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    AmortizationInterest calcAdjustmentAmount(AmortizationInterest amortizationInterest) throws ParseException;

    /**
     * @Title: getAmortizationInterest
     * @Discription: 构造AmortizationInterest 方便后续插入业务表
     * @Param: [iRequest, contractId, shareType]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    AmortizationInterest getAmortizationInterest(Long contractId, String shareType) throws ParseException;

    /**
     * @Title: getIntRateDay
     * @Discription: 通过xirr 反推日利率
     * @Param: [contractId]
     * @Return: java.lang.Double
     */
    AmortizationInterest getIntRateDay(Long contractId,String shareType);


    /**
     * @Title: getAmortizationCashflow
     * @Discription:  获取需要计算的现金流 给 amortizationInterest 赋值
     * @Param: [contractId, amortizationInterest, shareType]
     * @Return: com.hand.hls.gld.formbean.AmortizationInterest
     */
    AmortizationInterest getAmortizationCashflow(Long contractId, AmortizationInterest amortizationInterest, String shareType) throws ParseException;
}
