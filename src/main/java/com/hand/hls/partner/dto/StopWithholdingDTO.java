package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

//暂停代扣

@Data
public class StopWithholdingDTO extends BaseDTO {

    @NotNull
    private String orderNo;//订单编号
    @NotNull
    private Integer termNo;//期次号

}
