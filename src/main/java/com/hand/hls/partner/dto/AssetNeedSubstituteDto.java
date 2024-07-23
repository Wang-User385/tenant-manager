package com.hand.hls.partner.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AssetNeedSubstituteDto {

    private String orderNo; //订单号

    private String uniqueId; //唯一ID防止重复消费

    private Long termNo; //期次

    private String trialTime; //试算时间

    private Double principal;  //本金

    private Double interest;  //利息

    private Double penalty; //罚息

    private Double payableAmount; //应付金额

    private Long cashflowId;   //现金流ID
}
