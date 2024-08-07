package com.hand.hls.partner.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrderLoanResultDto {

    private String orderNo; // 订单编号

    private String uniqueId; //唯一ID防止重复消费

    private Date loanTime; //实际放款时间

    private Long loanAmount; //实际放款金额（分）

    private String status; // 放款结果

    private String type; //类型 APPLY_LOAN_AMOUNT 融资款 GPS_COST GPS费用

    private String remark; //备注
}
