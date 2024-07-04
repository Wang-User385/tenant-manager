package di.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import java.util.List;
@Data
public class RepayMent extends BaseDTO {

    private String requestNo;//请求号
    private String orderNo;//订单编号
    private String repayType;//还款方式；DEDUCT - 代扣TRANSFER - 转付ACTIVE_REPAY - 主动还款
    private List<TermRepayDetailApplyDTO> termRepayDetailApplyDTOList;
}
