package com.hand.hls.partner.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AssetNeedSubstituteDto {

    private String orderNo; //订单号

    private String uniqueId; //唯一ID防止重复消费

    private Long termNo; //期次

    private String trialTime; //试算时间

    private Long principal;  //本金

    private Long interest;  //利息

    private Long penalty; //罚息

    private Long payableAmount; //应付金额

    private Long cashflowId;   //现金流ID
}
