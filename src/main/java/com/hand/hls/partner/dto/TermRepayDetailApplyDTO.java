package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

//期次还款信息

@Data
public class TermRepayDetailApplyDTO extends BaseDTO {


    @NotNull(message = "期次号不能为空")
    private Integer termNo;//期次号
    @NotNull(message = "实际还款本金不能为空")
    private Long repayPrincipal;//实际还款本金；单位 分
    @NotNull(message = "实际还款利息不能为空")
    private Long repayInterest;//实际还款利息；单位 分
    @NotNull(message = "实际还款罚息不能为空")
    private Long repayPenalty;//实际还款罚息；单位 分
    @NotNull(message = "实际还款总金额不能为空")
    private Long repayAmount;//实际还款总金额；单位 分
    private String transactionNo;//结算单号
    private String externalDeductNo;//代扣交易单号

}
