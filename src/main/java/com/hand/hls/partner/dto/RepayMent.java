package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

//期次还款

@Data
public class RepayMent extends BaseDTO {

    @NotBlank(message = "请求号不能为空")
    private String requestNo;//请求号
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;//订单编号
    @NotBlank(message = "还款方式不能为空")
    private String repayType;//还款方式；DEDUCT - 代扣TRANSFER - 转付ACTIVE_REPAY - 主动还款

    @NotNull(message = "详细信息不能为空")
    @Valid
    private List<TermRepayDetailApplyDTO> termRepayDetailApplyDTOList;
}
