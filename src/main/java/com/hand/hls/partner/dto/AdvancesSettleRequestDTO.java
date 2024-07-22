package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdvancesSettleRequestDTO extends BaseDTO {

    @NotNull(message = "订单编号不能为空")
    private String orderNo;//订单编号
    @NotNull(message = "请求号不能为空")
    private String requestNo;//请求号
    @NotNull(message = "提前结清金额不能为空")
    private Long payableAmount;//提前结清金额单位 分
}
