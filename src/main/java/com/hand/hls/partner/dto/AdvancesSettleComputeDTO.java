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

    private String trialTime;//试算时间


    private Long payableAmount;//应付金额单位
    private Long deductAmount;//抵扣金额单位 分
    private List<Integer> termNos;//期次信息
    private List<Integer> deductNos;//抵扣期次
    private Long principal;//总本金单位 分
    private Long interest;//总利息 单位 分
    private Long penalty;//总罚息 单位 分
    private Long otherFee;//其他 单位 分

}
