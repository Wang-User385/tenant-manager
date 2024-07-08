package di.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

//关单

@Data
public class CloseOrderDTO extends BaseDTO {

    private String orderNo;
    private String reason;
}
