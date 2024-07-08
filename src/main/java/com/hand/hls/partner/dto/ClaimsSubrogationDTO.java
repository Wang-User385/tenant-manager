package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

//代偿请求DTO

@Data
public class ClaimsSubrogationDTO extends BaseDTO {


    private String requestNo;//请求号，全局唯一
    private String orderNo;//订单编号
    private Integer termNo;//期次
    private Long substituteAmount;//代偿金额 单位 分
}
