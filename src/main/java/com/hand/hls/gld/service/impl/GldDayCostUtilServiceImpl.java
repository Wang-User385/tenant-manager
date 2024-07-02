package com.hand.hls.gld.service.impl;

import cn.hutool.core.date.DateUtil;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.formbean.AmortizationCost;
import com.hand.hls.gld.mapper.GldFinanceIncomeDayMapper;
import com.hand.hls.gld.service.IGldDayCostUtilService;
import com.hand.hls.gld.service.IGldDayInterestUtilService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hand.hls.utils.HlsCusMathUtil.*;
import static com.hand.hls.sys.utils.OracleUtils.nvl;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/13
 * @description:
 */
@Service
public class GldDayCostUtilServiceImpl implements IGldDayCostUtilService {

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dfPeriodName = new SimpleDateFormat("yyyy-MM");


    /**
     * 按每1000个一组分割
     */
    private static final Integer MAX_NUMBER = 1000;


    private static Integer countStep(Integer size) {
        return (size + MAX_NUMBER - 1) / MAX_NUMBER;
    }

    @Autowired
    private GldFinanceIncomeDayMapper gldFinanceIncomeDayMapper;
    @Autowired
    private IGldFinanceIncomeDayService gldFinanceIncomeDayService;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    private IGldDayInterestUtilService utilService;
    @Autowired
    private HlsCusCshTransactionMapper cshTransactionMapper;

    /**
     * @Title: firstLoanDate
     * @Discription: 首次放款日 用来判断分摊的开始时间
     * @Param: [contractId]
     * @Return: java.util.Date
     */
    @Override
    public Date firstLoanDate(Long contractId) {
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(contractId);
        cashflow.setCfStatus("RELEASE");
        List<HlsCusConContractCashflow> cashflowList = cashflowMapper.select(cashflow);
        HlsCusConContractCashflow firstCalcCashflow =
                cashflowList.stream().filter(item -> item.getCfItem().compareTo(0L) == 0).sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate)).findFirst().get();
        //最早一期放款日
        Date firstLoanDate = firstCalcCashflow.getDueDate();
        return firstLoanDate;
    }

    /**
     * @Title: leaseEndDate
     * @Discription: 最后一期收款日
     * @Param: [contractId]
     * @Return: java.util.Date
     */
    @Override
    public Date leaseEndDate(Long contractId) {
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(contractId);
        cashflow.setCfStatus("RELEASE");
        List<HlsCusConContractCashflow> cashflowList = cashflowMapper.select(cashflow);
        HlsCusConContractCashflow lastCalcCashflow = cashflowList.stream().sorted(Comparator.comparing(HlsCusConContractCashflow::getDueDate).reversed()).findFirst().get();
        //最后一期收款日
        Date leaseEndDate = lastCalcCashflow.getDueDate();
        return leaseEndDate;
    }

    /**
     * @Title: getAllCashflow
     * @Discription: 查询合同下该费用所有现金流 需要筛选 摊销方式 然后 合并
     * @Param: [contractId, cfItem, shareType]
     * @Return: java.util.List<com.hand.hls.cont.dto.HlsCusConContractCashflow>
     */
    @Override
    public List<HlsCusConContractCashflow> getAllCashflow(Long contractId, Long cfItem, String shareType) {
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(contractId);
        cashflow.setCfItem(cfItem);
        cashflow.setAmortizationMethod(shareType);
        cashflow.setCfStatus("RELEASE");
        List<HlsCusConContractCashflow> cashflowList = cashflowMapper.select(cashflow);
        return cashflowList;
    }


    /**
     * @Title: getAllWriteOff
     * @Discription: 获取所有的核销记录，并获取收款时间
     * @Param: [cashflowId]
     * @Return: java.util.List<com.hand.hls.csh.dto.HlsCusCshWriteOff>
     */
    @Override
    public List<HlsCusCshWriteOff> getAllWriteOff(Long cashflowId) {
        HlsCusCshWriteOff cshWriteOff = new HlsCusCshWriteOff();
        cshWriteOff.setCashflowId(cashflowId);
        cshWriteOff.setReversedFlag("N");
        List<HlsCusCshWriteOff> cshWriteOffList = cshWriteOffMapper.select(cshWriteOff);
        for (HlsCusCshWriteOff writeOff : cshWriteOffList) {
            HlsCusCshTransaction cshTransaction = cshTransactionMapper.selectByPrimaryKey(writeOff.getCshTransactionId());
            writeOff.setTransactionDate(cshTransaction.getTransactionDate());
        }
        return cshWriteOffList;
    }

    /**
     * @Title: getAmortizationIncomeDay
     * @Discription: 筛选所有需要计算的分摊日记录
     * @Param: [contract]
     * @Return: java.util.List<com.hand.hls.gld.dto.GldFinanceIncomeDay>
     */
    @Override
    public List<GldFinanceIncomeDay> getAmortizationIncomeDay(HlsCusConContract contract) {

        GldFinanceIncomeDay rentalGldFinanceIncomeDay = new GldFinanceIncomeDay();
        rentalGldFinanceIncomeDay.setContractId(contract.getContractId());
        rentalGldFinanceIncomeDay.setCfItem(1L);
        List<GldFinanceIncomeDay> rentalGldFinanceIncomeDayList = gldFinanceIncomeDayMapper.select(rentalGldFinanceIncomeDay);

        GldFinanceIncomeDay advanceGldFinanceIncomeDay = new GldFinanceIncomeDay();
        advanceGldFinanceIncomeDay.setContractId(contract.getContractId());
        advanceGldFinanceIncomeDay.setCfItem(10L);
        List<GldFinanceIncomeDay> advanceGldFinanceIncomeDayList = gldFinanceIncomeDayMapper.select(advanceGldFinanceIncomeDay);

        List<GldFinanceIncomeDay> gldFinanceIncomeDayList = new ArrayList<>();
        gldFinanceIncomeDayList.addAll(rentalGldFinanceIncomeDayList);
        gldFinanceIncomeDayList.addAll(advanceGldFinanceIncomeDayList);
        //算头不算尾
        GldFinanceIncomeDay deleteDay = gldFinanceIncomeDayList.stream().sorted(Comparator.comparing(GldFinanceIncomeDay::getAmortizationDate).reversed()).findFirst().get();
        gldFinanceIncomeDayList = gldFinanceIncomeDayList.stream().
                filter(item -> !item.getAmortizationDate().equals(deleteDay.getAmortizationDate())).
                sorted(Comparator.comparing(GldFinanceIncomeDay::getAmortizationDate)).collect(Collectors.toList());
        return gldFinanceIncomeDayList;
    }

    /**
     * @Title: surplusInterestSumMap
     * @Discription: 收取时，剩余“利息摊销金额”合计
     * @Param: [gldFinanceIncomeDayList, allWriteOff]
     * @Return: java.util.Map<java.util.Date, java.lang.Double>
     */
    @Override
    public Map<Date, Double> surplusInterestSumMap(List<GldFinanceIncomeDay> gldFinanceIncomeDayList, List<HlsCusCshWriteOff> allWriteOff) {
        Map<Date, Double> surplusInterestSumMap = new HashMap<>();
        for (HlsCusCshWriteOff cshWriteOff : allWriteOff) {
            Double surplusInterestSum =
                    gldFinanceIncomeDayList.stream().
                            filter(item -> item.getAmortizationDate().compareTo(cshWriteOff.getCalcDate()) > -1).
                            collect(Collectors.summingDouble(GldFinanceIncomeDay::getAmortizationAmount));
            surplusInterestSumMap.put(cshWriteOff.getCalcDate(), surplusInterestSum);
        }
        return surplusInterestSumMap;
    }

    /**
     * @Title: calcAmortizationCost
     * @Discription: 计算费用摊销(费用利息占比法)
     * @Param: [gldFinanceIncomeDayList, allWriteOff, surplusInterestSumMap, taxTypeRate]
     * @Return: java.util.List<com.hand.hls.gld.formbean.AmortizationCost>
     */
    @Override
    public List<AmortizationCost> calcAmortizationCost(List<GldFinanceIncomeDay> gldFinanceIncomeDayList, List<HlsCusCshWriteOff> allWriteOff, Map<Date, Double> surplusInterestSumMap, Double taxTypeRate, HlsCusConContractCashflow cashflow) throws ParseException {

        List<AmortizationCost> amortizationCostList = new ArrayList<>();


        Double sumAmount = 0.0;
        Double amortizedAmountSum = 0.0;
        Double proportionSum = 0.0;
        for (int j = 0; j < gldFinanceIncomeDayList.size(); j++) {

            GldFinanceIncomeDay incomeDay = gldFinanceIncomeDayList.get(j);
            Long cashflowId = cashflow.getCashflowId();
            AmortizationCost amortizationCost = new AmortizationCost();
            amortizationCost.setAmortizationDate(incomeDay.getAmortizationDate());
            amortizationCost.setCashflowId(cashflowId);
            amortizationCost.setAmortizationDate(incomeDay.getAmortizationDate());
            Double netDueAmountSum = 0.0;
            for (int i = 0; i < allWriteOff.size(); i++) {
                Double netDueAmount = div(allWriteOff.get(i).getWriteOffDueAmount(), (1 + taxTypeRate), 2);
                netDueAmountSum = add(netDueAmountSum, netDueAmount);
                if (allWriteOff.size() == 1) {
                    //收取时，剩余“利息摊销金额”合计
                    Double surplusInterestSum = surplusInterestSumMap.get(allWriteOff.get(i).getCalcDate());
                    amortizationCost.setProportion(div(incomeDay.getAmortizationAmount(), surplusInterestSum, 8));
                    amortizationCost.setAmortizationAmount(mul(amortizationCost.getProportion(), netDueAmount, 2));
                    sumAmount = add(sumAmount, amortizationCost.getAmortizationAmount());
                    if (j == gldFinanceIncomeDayList.size() - 1) {
                        amortizationCost.setAmortizationAmount(add(amortizationCost.getAmortizationAmount(), sub(netDueAmount, sumAmount, 2)));
                    }
                    amortizationCostList.add(amortizationCost);
                } else {
                    Date startDate = allWriteOff.get(i).getCalcDate();
                    Date endDate;
                    if (i == allWriteOff.size() - 1) {
                        endDate = gldFinanceIncomeDayList.get(gldFinanceIncomeDayList.size() - 1).getAmortizationDate();
                    } else {
                        endDate = utilService.addDate(allWriteOff.get(i + 1).getCalcDate(), -1L);
                    }
                    if (gldFinanceIncomeDayList.get(j).getAmortizationDate().compareTo(startDate) >= 0 &&
                            gldFinanceIncomeDayList.get(j).getAmortizationDate().compareTo(endDate) <= 0) {
                        //收取时，剩余“利息摊销金额”合计
                        Double surplusInterestSum = surplusInterestSumMap.get(allWriteOff.get(i).getCalcDate());
                        if (i == 0) {
                            System.out.println("i :" + i);
                            System.out.println("Date :" + amortizationCost.getAmortizationDate());
                            System.out.println("Amount :" + incomeDay.getAmortizationAmount());
                            System.out.println("SurplusInterestSum :" + surplusInterestSum);
                            amortizationCost.setProportion(div(incomeDay.getAmortizationAmount(), surplusInterestSum, 8));
                            System.out.println("Proportion :" + amortizationCost.getProportion());

                            amortizationCost.setAmortizationAmount(mul(amortizationCost.getProportion(), netDueAmount, 2));
                            System.out.println("AmortizationAmount :" + amortizationCost.getAmortizationAmount());

                            sumAmount = add(sumAmount, amortizationCost.getAmortizationAmount());
                            if (j == gldFinanceIncomeDayList.size() - 1) {
                                amortizationCost.setAmortizationAmount(add(amortizationCost.getAmortizationAmount(), sub(netDueAmount, sumAmount, 2)));
                            }
                            amortizationCostList.add(amortizationCost);
                        } else {
                            Date startDateCalc = allWriteOff.get(i - 1).getCalcDate();
                            Date endDateCalc = utilService.addDate(allWriteOff.get(i).getCalcDate(), -1L);

                            proportionSum = round(amortizationCostList.stream().
                                    filter(item -> item.getAmortizationDate().compareTo(startDateCalc) >= 0 && item.getAmortizationDate().compareTo(endDateCalc) <= 0).
                                    collect(Collectors.summingDouble(AmortizationCost::getProportion)), 8);

                            amortizedAmountSum = round(amortizationCostList.stream().
                                    filter(item -> item.getAmortizationDate().compareTo(startDateCalc) >= 0 && item.getAmortizationDate().compareTo(endDateCalc) <= 0).
                                    collect(Collectors.summingDouble(AmortizationCost::getAmortizationAmount)), 2);
                            System.out.println("i :" + i);
                            System.out.println("Date :" + amortizationCost.getAmortizationDate());
                            System.out.println("Amount :" + incomeDay.getAmortizationAmount());
                            System.out.println("SurplusInterestSum :" + surplusInterestSum);
                            System.out.println("ProportionSum :" + proportionSum);

                            amortizationCost.setProportion(mul(div(incomeDay.getAmortizationAmount(), surplusInterestSum), sub(1, proportionSum), 8));
                            System.out.println("Proportion :" + amortizationCost.getProportion());

                            amortizationCost.setAmortizationAmount(
                                    mul(div(sub(netDueAmountSum, amortizedAmountSum),
                                            sub(1, proportionSum)),
                                            amortizationCost.getProportion(), 2));
                            System.out.println("AmortizationAmount :" + amortizationCost.getAmortizationAmount());

                            sumAmount = add(sumAmount, amortizationCost.getAmortizationAmount());
                            if (j == gldFinanceIncomeDayList.size() - 1) {

                                Double amountAll = amortizationCostList.stream().
                                        filter(item -> item.getCashflowId().compareTo(cashflowId) == 0).
                                        collect(Collectors.summingDouble(AmortizationCost::getAmortizationAmount));

                                amortizationCost.setAmortizationAmount(sub(netDueAmountSum, amountAll, 2));
                            }
                            amortizationCostList.add(amortizationCost);
                        }

                    }
                }
            }
        }
        return amortizationCostList;
    }

    /**
     * @Title: calcAmortizationCost
     * @Discription: 计算费用摊销(费用直线法)
     * @Param: [contractId, cashflowList]
     * @Return: java.util.List<com.hand.hls.gld.formbean.AmortizationCost>
     */
    @Override
    public List<AmortizationCost> calcAmortizationCost(Long
                                                               contractId, List<HlsCusConContractCashflow> cashflowList) throws ParseException {

        //获取每一笔现金流需要分摊的总天数
        Date firstLoanDate = firstLoanDate(contractId);
        Date endDate = leaseEndDate(contractId);
        //算头不算尾
        endDate = utilService.addDate(endDate, -1L);

        List<AmortizationCost> amortizationCostList = new ArrayList<>();

        for (HlsCusConContractCashflow cashflow : cashflowList) {
            Date startDate = cashflow.getDueDate().compareTo(firstLoanDate) == -1 ? firstLoanDate : cashflow.getDueDate();
            //被摊销的总天数
            Long days = utilService.getDays(startDate, endDate) + 1;
            //日摊销金额
            Double amortizationAmount = div(cashflow.getNetDueAmount(), days, 2);
            //获取日期间隔
            List<String> dateList = utilService.dateBetween(df.format(startDate), df.format(endDate));

            Double amountSum = 0.0;
            for (String date : dateList) {

                AmortizationCost amortizationCost = new AmortizationCost();
                amortizationCost.setAmortizationAmount(amortizationAmount);
                amortizationCost.setCashflowId(cashflow.getCashflowId());
                amortizationCost.setAmortizationDate(df.parse(date));
                //最后一天 金额倒减
                if (date.equals(df.format(endDate))) {
                    amortizationCost.setAmortizationAmount(sub(cashflow.getNetDueAmount(), amountSum, 2));
                }
                amountSum = add(amountSum, amortizationAmount);
                amortizationCostList.add(amortizationCost);
            }
        }
        return amortizationCostList;
    }

    /**
     * @Title: insertGldFinanceIncomeDay
     * @Discription: 插入 gld_finance_income_day
     * @Param: [iRequest, dateList, contractId, gldFinanceIncomeDayCalc]
     * @Return: java.util.List<com.hand.hls.gld.dto.GldFinanceIncomeDay>
     */
    @Override
    public List<GldFinanceIncomeDay> insertGldFinanceIncomeDay(IRequest
                                                                       iRequest, List<AmortizationCost> amortizationCostList, Long contractId, Long cfItem) throws ParseException {
        //插值
        //先删除 再重新 插入
        /*GldFinanceIncomeDay incomeDay = new GldFinanceIncomeDay();
        incomeDay.setCfItem(contractId);
        incomeDay.setCfItem(cfItem);
        Optional.ofNullable(gldFinanceIncomeDayMapper.select(incomeDay)).ifPresent(item -> gldFinanceIncomeDayService.batchDelete(item));*/
        //优化效率
        GldFinanceIncomeDay incomeDay = new GldFinanceIncomeDay();
        incomeDay.setContractId(contractId);
        incomeDay.setCfItem(cfItem);
        gldFinanceIncomeDayMapper.deleteGldFinanceIncomeDayCost(incomeDay);


        List<GldFinanceIncomeDay> gldFinanceIncomeDayList = new ArrayList<>();
        for (int i = 0; i < amortizationCostList.size(); i++) {

            AmortizationCost amortizationCost = amortizationCostList.get(i);
            GldFinanceIncomeDay gldFinanceIncomeDay = new GldFinanceIncomeDay();
            gldFinanceIncomeDay.setCompanyId(iRequest.getCompanyId());
            gldFinanceIncomeDay.setContractId(contractId);
            gldFinanceIncomeDay.setCfItem(cfItem);
            gldFinanceIncomeDay.setCashflowId(amortizationCostList.get(i).getCashflowId());
            gldFinanceIncomeDay.setPeriodName(dfPeriodName.format(amortizationCost.getAmortizationDate()));
            gldFinanceIncomeDay.setAmortizationDate(amortizationCost.getAmortizationDate());
            gldFinanceIncomeDay.setPreAmortizationAmount(amortizationCost.getAmortizationAmount());
            gldFinanceIncomeDay.setAdjustmentAmount(0.0);
            gldFinanceIncomeDay.setAmortizationAmount(add(gldFinanceIncomeDay.getPreAmortizationAmount(), gldFinanceIncomeDay.getAdjustmentAmount()));
            //优化效率
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
}
