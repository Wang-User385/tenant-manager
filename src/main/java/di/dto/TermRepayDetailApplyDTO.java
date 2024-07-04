package di.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

@Data
public class TermRepayDetailApplyDTO extends BaseDTO {


    private Integer termNo;//期次号
    private Long repayPrincipal;//实际还款本金；单位 分
    private Long repayInterest;//实际还款利息；单位 分
    private Long repayPrincipalPenalty;//实际还款本金罚息；单位 分
    private Long repayInterestPenalty;//实际还款利息罚息；单位 分
    private Long repayAmount;//实际还款总金额；单位 分
    private String transactionNo;//结算单号
    private String externalDeductNo;//代扣交易单号

}
