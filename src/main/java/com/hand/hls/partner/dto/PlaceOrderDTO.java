package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


//下单

@Data
public class PlaceOrderDTO extends BaseDTO {

    @NotBlank(message = "客户姓名不能为空")
    private String name;
    @NotBlank(message = "客户身份证号不能为空")
    private String idCardNo;
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @NotBlank(message = "产品码不能为空")
    private String productCode;
    @NotBlank(message = "证件签发日期不能为空")
    private String idissue;
    @NotBlank(message = "证件到期日期为空")
    private String idexp;
    @NotBlank(message = "合作机构单号不能为空")
    private String outBizNo;
}
