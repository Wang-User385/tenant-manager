package com.hand.hls.partner.dto;

import lombok.Data;

@Data
public class AssetNeedBuybackDto {
    private String orderNo; //订单编号

    private String uniqueId; //唯一ID防止重复消费

    private String termNos; //其次号

    private String trialTime; //试算时间

    private Double principal;  //本金

    private Double interest;  //利息

    private Double principalPenalty; //本金罚息

    private Double interestPenalty; //利息罚息

    private Double otherFee; //其他费用

    private Double payableAmount; //应付金额


}
