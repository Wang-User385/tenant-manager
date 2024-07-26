package com.hand.hls.partner.dto;

import lombok.Data;

/**
 * 期次代扣结果通知：具体期次实体
 */
@Data
public class RepayPlanRepaidNotifyDto {
    private String termNo; //期次号
    private Long principal; //本金
    private Long interest; //利息
    private Long penalty; //罚息
    private Long otherFee;  //其他费用
    private Long total; //总金额
    private String transactionNo; //结算单号
    private String externalDeductNo; //代扣交易单号
}
