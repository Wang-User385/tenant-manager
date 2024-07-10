package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

//代偿请求DTO

@Data
public class ClaimsSubrogationDTO extends BaseDTO {

    @NotBlank(message = "请求号不能为空")
    private String requestNo;//请求号，全局唯一
    @NotBlank(message = "请求号不能为空")
    private String orderNo;//订单编号
    @NotNull(message = "请求号不能为空")
    private Integer termNo;//期次
    @NotNull(message = "请求号不能为空")
    private Long substituteAmount;//代偿金额 单位 分
}
