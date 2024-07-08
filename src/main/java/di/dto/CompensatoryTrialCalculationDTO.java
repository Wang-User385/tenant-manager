package di.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

//代偿试算

@Data
public class CompensatoryTrialCalculationDTO extends BaseDTO {



    private String orderNo;//订单编号
    private Integer termNo;//期次
    private String trialTime;//试算时间
    private Long principal;//本金单位 分
    private Long interest;//利息单位 分
    private Long penalty;//罚息单位 分
    private Long payableAmount;//应付金额单位 分
}
