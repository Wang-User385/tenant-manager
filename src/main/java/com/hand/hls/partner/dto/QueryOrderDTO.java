package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class QueryOrderDTO extends BaseDTO {

    @NotBlank(message = "订单编号不能为空")
    private String orderNo;//订单编号
}
