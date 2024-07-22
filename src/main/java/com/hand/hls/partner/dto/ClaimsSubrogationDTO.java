package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

//代偿请求DTO

@Data
public class ClaimsSubrogationDTO extends BaseDTO {

    @NotNull(message = "请求号不能为空")
    private String requestNo;//请求号，全局唯一
    @NotNull(message = "订单编号不能为空")
    private String orderNo;//订单编号
    @NotNull(message = "期次不能为空")
    private Integer termNo;//期次
    @NotNull(message = "代偿金额不能为空")
    private Long substituteAmount;//代偿金额 单位 分
}
