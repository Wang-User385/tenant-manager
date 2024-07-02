package com.hand.hls.gld.dto;

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
 * @description:
 */
@Getter
@Setter
public class GldFinanceIncomeDayPeriod {
    private Date startDate;
    private Date endDate;
    private String periodName;
    private Long days;
    private Double financeIncome;
}
