package com.hand.hls.partner.dto;

import lombok.Data;

/**
 * 期次代扣结果通知：具体期次实体
 */
@Data
public class RepayPlanRepaidNotifyDto {
    private String termNo; //期次号
    private Double principal; //本金
    private Double interest; //利息
    private Double principalPenalty; //本金罚息
    private Double interestPenalty; //利息罚息
    private Double otherFee;  //其他费用
    private Double total; //总金额
    private String transactionNo; //结算单号
    private String externalDeductNo; //代扣交易单号
}
