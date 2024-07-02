package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.formbean.AmortizationCost;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/13
 * @description: 费用分摊工具
 */
public interface IGldDayCostUtilService {

    /**
     * @Title: firstLoanDate
     * @Discription: 首次放款日 用来判断分摊的开始时间
     * @Param: [contractId]
     * @Return: java.util.Date
     */
    Date firstLoanDate(Long contractId);

    /**
     * @Title: leaseEndDate
     * @Discription: 最后一期收款日
     * @Param: [contractId]
     * @Return: java.util.Date
     */
    Date leaseEndDate(Long contractId) ;
    /**
     * @Title: getAllCashflow
     * @Discription: 查询合同下该费用所有现金流 需要筛选 摊销方式 然后 合并
     * @Param: [contractId, cfItem, shareType]
     * @Return: java.util.List<com.hand.hls.cont.dto.HlsCusConContractCashflow>
     */
    List<HlsCusConContractCashflow> getAllCashflow(Long contractId, Long cfItem, String shareType);
    /**
     * @Title: getAllWriteOff
     * @Discription: 获取所有的核销记录，并获取收款时间
     * @Param: [cashflowId]
     * @Return: java.util.List<com.hand.hls.csh.dto.HlsCusCshWriteOff>
     */
    List<HlsCusCshWriteOff>getAllWriteOff(Long cashflowId);
    /**
     * @Title: getAmortizationIncomeDay
     * @Discription: 筛选所有需要计算的分摊日记录
     * @Param: [contract]
     * @Return: java.util.List<com.hand.hls.gld.dto.GldFinanceIncomeDay>
     */
    List<GldFinanceIncomeDay> getAmortizationIncomeDay(HlsCusConContract contract);
    /**
     * @Title: surplusInterestSumMap
     * @Discription: 收取时，剩余“利息摊销金额”合计
     * @Param: [gldFinanceIncomeDayList, allWriteOff]
     * @Return: java.util.Map<java.util.Date, java.lang.Double>
     */
    Map<Date, Double> surplusInterestSumMap(List<GldFinanceIncomeDay> gldFinanceIncomeDayList,  List<HlsCusCshWriteOff> allWriteOff);

    /**
     * @Title: calcAmortizationCost
     * @Discription: 计算费用摊销(费用利息占比法)
     * @Param: [gldFinanceIncomeDayList, allWriteOff, surplusInterestSumMap, taxTypeRate, cashflow]
     * @Return: java.util.List<com.hand.hls.gld.formbean.AmortizationCost>
     */
    List<AmortizationCost> calcAmortizationCost(List<GldFinanceIncomeDay> gldFinanceIncomeDayList, List<HlsCusCshWriteOff> allWriteOff, Map<Date, Double> surplusInterestSumMap,Double taxTypeRate,HlsCusConContractCashflow cashflow) throws ParseException;

    /**
     * @Title: calcAmortizationCost
     * @Discription: 计算费用摊销(费用直线法)
     * @Param: [contractId, cashflowList]
     * @Return: java.util.List<com.hand.hls.gld.formbean.AmortizationCost>
     */
    List<AmortizationCost>calcAmortizationCost(Long contractId, List<HlsCusConContractCashflow> cashflowList) throws ParseException;

    /**
     * @Title: insertGldFinanceIncomeDay
     * @Discription: 插入 gld_finance_income_day
     * @Param: [iRequest, dateList, contractId, gldFinanceIncomeDayCalc]
     * @Return: java.util.List<com.hand.hls.gld.dto.GldFinanceIncomeDay>
     */
    List<GldFinanceIncomeDay> insertGldFinanceIncomeDay(IRequest iRequest, List<AmortizationCost> amortizationCostList, Long contractId, Long cfItem) throws ParseException;

}
