package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

//融资方案相关信息

@Data
public class FinanceInfo extends BaseDTO {

    @NotNull(message = "期数不能为空")
    private Integer termCount;//期数

    @NotNull(message = "月租不能为空")
    private  Integer monthPayment;//月租 (分)

    @NotNull(message = "利率不能为空")
    private Long rate;//利率

    @NotNull(message = "首付款不能为空")
    private  Integer firstPayment;//首付款 （分）

     @NotNull(message = "车辆指导价不能为空")
    private  Integer carGuidePrice;//车辆指导价 (分)

     @NotNull(message = "车辆售价不能为空")
    private  Integer carSalePrice;//车辆售价 (分)

     @NotNull(message = "申请融资额不能为空")
    private  Integer applyLoanAmount;//申请融资额 (分)

     @NotNull(message = "剩余车辆价款不能为空")
    private  Integer carRestPrice;//剩余车辆价款 (分)

     @NotNull(message = "加融项金额不能为空")
    private  Integer plusFinanceAmount;//加融项金额 (分)

    @NotNull(message = "起息日不能为空")
    private  String startRentDate;//起息日

}
