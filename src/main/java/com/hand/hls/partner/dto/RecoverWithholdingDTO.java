package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RecoverWithholdingDTO extends BaseDTO {


    @NotNull(message = "订单编号不能为空")
    private String orderNo;//订单编号
    @NotNull(message = "期次号不能为空")
    private Integer termNo;//期次号
}
