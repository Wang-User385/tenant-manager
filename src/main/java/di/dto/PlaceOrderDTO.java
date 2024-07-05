package di.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

//下单

@Data
public class PlaceOrderDTO extends BaseDTO {



    private String name;
    private String idCardNo;
    private String mobile;
    private String productCode;
    private String idissue;
    private String idexp;
    private String outBizNo;
}
