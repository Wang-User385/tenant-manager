package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OverdueRepurchaseRequestDTO extends BaseDTO {

    @NotNull
    private String orderNo;//订单编号
    @NotNull
    private String requestNo;//请求号
    @NotNull
    private Long buybackAmount;//回购金额单位 分
}
