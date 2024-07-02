package com.hand.hls.wsdl.dto;

import java.io.Serializable;
import java.util.List;

/**
 * <p>租赁物信息
 *
 * @author ferry ferry_sy@163.com
 * created by 2019/12/02 16:03
 */

public class LeaseItem implements Serializable {
    /**
     * 租赁物ID
     */
    private String partnersLeaseItemId;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 车型
     */
    private String vehicleType;

    /**
     * 车系
     */
    private String series;

    /**
     * 车牌号
     */
    private String licensePlateNumber;

    /**
     * 车架号
     */
    private String frameNumber;

    /**
     * 发动机号
     */
    private String engineNumber;

    /**
     * 是否挂车
     */
    private String trailerFlag;

    /**
     * 保险信息
     */
    private List<Insurance> insurances;

    public String getPartnersLeaseItemId() {
        return partnersLeaseItemId;
    }

    public void setPartnersLeaseItemId(String partnersLeaseItemId) {
        this.partnersLeaseItemId = partnersLeaseItemId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(String frameNumber) {
        this.frameNumber = frameNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public List<Insurance> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<Insurance> insurances) {
        this.insurances = insurances;
    }

    public String getTrailerFlag() {
        return trailerFlag;
    }

    public void setTrailerFlag(String trailerFlag) {
        this.trailerFlag = trailerFlag;
    }
}
