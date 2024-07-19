package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

//业务申请

@Data
public class BusinessApplicationDTO extends BaseDTO {

    @NotNull(message = "订单编号不能为空")
    private String orderNo;//订单编号
    @NotNull(message = "需要执行的动作不能为空")
    private String action;//需要执行的动作

    private ExtendInfo extendInfo;//扩展参数
}
