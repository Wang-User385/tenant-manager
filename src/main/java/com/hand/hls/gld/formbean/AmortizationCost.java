package com.hand.hls.gld.formbean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/12
 * @description: 费用摊销
 */
@Getter
@Setter
public class AmortizationCost {
    public Date amortizationDate;
    public Double amortizationAmount;
    public Long cashflowId;
    public Double proportion;

}
