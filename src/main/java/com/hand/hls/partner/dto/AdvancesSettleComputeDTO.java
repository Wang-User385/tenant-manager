package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

//提前结清

@Data
public class AdvancesSettleComputeDTO extends BaseDTO {

    @NotBlank(message = "订单编号不能为空")
    private String orderNo;//订单编号
    @NotBlank(message = "请求号不能为空")
    private String requestNo;//请求号

    private String trialTime;//试算时间
    @NotBlank(message = "应付金额不能为空")
    private Long payableAmount;//应付金额 单位 分

    private Long deductAmount;//抵扣金额 单位 分
    @NotNull(message = "期次信息不能为空")
    @Valid
    private List<Integer> termNos;//期次信息

    private List<Integer> deductNos;//抵扣期次
    @NotNull(message = "总本金不能为空")
    private Long principal;//总本金 单位 分
    @NotNull(message = "总利息不能为空")
    private Long interest;//总利息 单位 分
    @NotNull(message = "总罚息不能为空")
    private Long penalty;//总罚息 单位 分
    @NotNull(message = "其他不能为空")
    private Long otherFee;//其他 单位 分
    @NotNull(message = "提前结清金额不能为空")
    private Long preSettleAmount;//提前结清金额 单位 分
}
