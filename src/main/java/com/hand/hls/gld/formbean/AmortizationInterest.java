package com.hand.hls.gld.formbean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/7
 * @description: 利息摊销
 */
@Getter
@Setter
public class AmortizationInterest {
    //构建 必须的数据
    private List<String> dateList;
    private Map<Date,Long> dateCashflowId;
    private List<Double> preAmortizationAmountList;
    private List<Double> adjustmentAmountList;

    //构建 计算中间数据
    private Double taxRate;
    private Double intRate;
    private Long interestDays;
    private List<Double> outstandingPrincipalList;
    private Map<Date,Double> dueDateNetInterest;
    private Map<Date,Double> dueDateNetDueAmount;
    private Map<Date,Double> dueDateAdjustmentAmount;
    private Double intRateDay;
    private Double outstandingPrincipal;
    private Double netInterestTotal;


}
