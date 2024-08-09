package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

//融资方案相关信息

@Data
public class FinanceInfo extends BaseDTO {

    @NotNull(message = "期数不能为空")
    @NotBlank(message = "期数不能为空")
    private String termCount;//期数

    @NotNull(message = "月租不能为空")
    @NotBlank(message = "月租不能为空")
    private  String monthPayment;//月租 (分)

    @NotNull(message = "利率不能为空")
    @NotBlank(message = "利率不能为空")
    private String rate;//利率

    @NotNull(message = "首付款不能为空")
    @NotBlank(message = "首付款不能为空")
    private  String firstPayment;//首付款 （分）

     @NotNull(message = "车辆指导价不能为空")
     @NotBlank(message = "车辆指导价不能为空")
    private  String carGuidePrice;//车辆指导价 (分)

     @NotNull(message = "车辆售价不能为空")
     @NotBlank(message = "车辆售价不能为空")
    private  String carSalePrice;//车辆售价 (分)

     @NotNull(message = "申请融资额不能为空")
     @NotBlank(message = "申请融资额不能为空")
    private  String applyLoanAmount;//申请融资额 (分)

     @NotNull(message = "剩余车辆价款不能为空")
     @NotBlank(message = "剩余车辆价款不能为空")
    private  String carRestPrice;//剩余车辆价款 (分)

     @NotNull(message = "加融项金额不能为空")
     @NotBlank(message = "加融项金额不能为空")
    private  String plusFinanceAmount;//加融项金额 (分)

    @NotNull(message = "起息日不能为空")
    @NotBlank(message = "起息日不能为空")
    private  String startRentDate;//起息日

}
