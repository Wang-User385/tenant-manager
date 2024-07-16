package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;

//租赁物相关信息

@Data
public class CarInfo extends BaseDTO {

    @NotNull(message = "品牌名称不能为空")
    private  String brandName;//品牌名称

    @NotNull(message = "车系名称不能为空")
    private  String seriesName;//车系名称

    @NotNull(message = "车型名称不能为空")
    private  String modelName;//车型名称

    private  String vin;//vin码/车架号

    @NotNull(message = "车辆颜色不能为空")
    private  String color;//车辆颜色

    private  String carProductionDate;//车辆出厂日期

    private  String engineNumber;//发动机号（选填）

    private  Integer mandatoryInsuranceAmount;//强制保险金额 (分)（选填）

    private  String commercialInsuranceType;//商业保险类型（选填）
}
