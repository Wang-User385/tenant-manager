package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

//代扣状态查询

@Data
public class QueryWithholdingStateDTO extends BaseDTO {

    @NotNull(message = "订单编号不能为空")
    private String orderNo;//订单编号

    private Integer termNox0;//期次号

    private Boolean deductStatusx0;//是否代扣中

}
