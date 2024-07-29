package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.validation.constraints.NotNull;

//租赁物相关信息

public class CarInfo extends BaseDTO {

    @NotNull(message = "品牌Code不能为空")
    private  String brandCode;//品牌Code

    @NotNull(message = "品牌名称不能为空")
    private  String brandName;//品牌名称

    @NotNull(message = "车系 Code不能为空")
    private  String seriesCode;//车系 Code

    @NotNull(message = "车系名称不能为空")
    private  String seriesName;//车系名称

    @NotNull(message = "车型Code不能为空")
    private  String modelCode;//车型Code

    @NotNull(message = "车型名称不能为空")
    private  String modelName;//车型名称

    private  String vin;//vin码/车架号

    @NotNull(message = "车辆颜色不能为空")
    private  String color;//车辆颜色

    private  String carProductionDate;//车辆出厂日期

    private  String engineNumber;//发动机号（选填）

    private  String mandatoryInsuranceAmount;//强制保险金额 (分)（选填）

    private  String commercialInsuranceType;//商业保险类型（选填）

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSeriesCode() {
        return seriesCode;
    }

    public void setSeriesCode(String seriesCode) {
        this.seriesCode = seriesCode;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCarProductionDate() {
        return carProductionDate;
    }

    public void setCarProductionDate(String carProductionDate) {
        this.carProductionDate = carProductionDate;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getMandatoryInsuranceAmount() {
        return mandatoryInsuranceAmount;
    }

    public void setMandatoryInsuranceAmount(String mandatoryInsuranceAmount) {
        this.mandatoryInsuranceAmount = mandatoryInsuranceAmount;
    }

    public String getCommercialInsuranceType() {
        return commercialInsuranceType;
    }

    public void setCommercialInsuranceType(String commercialInsuranceType) {
        this.commercialInsuranceType = commercialInsuranceType;
    }
}
