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

}
