package com.hand.hls.partner.dto;

import lombok.Data;

@Data
public class AssetNeedBuybackDto {
    private String orderNo; //订单编号

    private String uniqueId; //唯一ID防止重复消费

    private String termNos; //其次号

    private String trialTime; //试算时间

    private Long principal;  //本金

    private Long interest;  //利息

    private Long penalty; //罚息

    private Long otherFee; //其他费用

    private Long payableAmount; //应付金额

    private Long contractId; //合同ID


}
