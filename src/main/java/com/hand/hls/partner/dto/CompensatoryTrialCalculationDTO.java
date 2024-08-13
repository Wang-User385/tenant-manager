package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

//代偿试算

@Data
public class CompensatoryTrialCalculationDTO{


    @NotBlank(message = "订单编号不能为空")
    private String orderNo;//订单编号
    @NotNull(message = "期次不能为空")
    private Integer termNo;//期次
    //@NotBlank(message = "试算时间不能为空")
    private String trialTime;//试算时间
    @NotNull(message = "本金不能为空")
    private Long principal;//本金 单位 分
    @NotNull(message = "利息不能为空")
    private Long interest;//利息 单位 分
    @NotNull(message = "罚息不能为空")
    private Long penalty;//罚息 单位 分
    @NotNull(message = "应付金额不能为空")
    private Long payableAmount;//应付金额 单位 分
}
