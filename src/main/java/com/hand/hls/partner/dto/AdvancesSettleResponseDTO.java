package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import java.util.List;

@Data
public class AdvancesSettleResponseDTO extends BaseDTO {

    private Long payableAmount;//应付金额 单位 分

    private Long deductAmount;//抵扣金额 单位 分
    private List<Integer> termNos;//期次信息

    private List<Integer> deductNos;//抵扣期次
    private Long principal;//总本金 单位 分
    private Long interest;//总利息 单位 分
    private Long penalty;//总罚息 单位 分
    private Long otherFee;//其他 单位 分
    private Long preSettleAmount;//提前结清金额 单位 分
}
