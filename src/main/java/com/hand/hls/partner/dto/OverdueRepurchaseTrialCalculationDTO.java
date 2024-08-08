package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

//逾期回购试算

@Data
public class OverdueRepurchaseTrialCalculationDTO{


    @NotNull(message = "订单编号不能为空")
    private String orderNo;//订单编号

    private String trialTime;//预计回购日期

    private Long payableAmount;//应付金额单位
    private Long deductAmount;//抵扣金额单位 分
    private List<Integer> termNos;//期次信息
    private List<Integer> deductNos;//抵扣期次
    private Long principal;//总本金单位 分
    private Long interest;//总利息 单位 分
    private Long penalty;//总罚息 单位 分
    private Long otherFee;//其他 单位 分

}
