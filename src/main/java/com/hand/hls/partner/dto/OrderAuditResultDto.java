package com.hand.hls.partner.dto;

import lombok.Data;

@Data
public class OrderAuditResultDto {

    private String orderNo;//订单编号

    private String uniqueId; //唯一ID防止重复消费

    private String scene; // PRE_RISK 人工风险审核LOAN_AUDIT 放款审核MORTGAGE_MATERIAL_AUDIT 抵押材料审核

    private String status; //PASS ：通过 REJECT ：拒绝  REVIEW : 驳回

    private String remark; //备注

    private String opinion; // 意见
}
