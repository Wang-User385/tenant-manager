package com.hand.hls.wsdl.dto;

import java.io.Serializable;

/**
 * <p>保险信息
 *
 * @author ferry ferry_sy@163.com
 * created by 2019/12/02 15:25
 */

public class Insurance implements Serializable {
    /**
     * 保险类型
     */
    private String insuranceType;

    /**
     * 保险日期从
     */
    private String insuranceDateFrom;

    /**
     * 保险日期到
     */
    private String insuranceDateTo;

    /**
     * 保险公司
     */
    private String insuranceCompany;

    /**
     * 保险编号
     */
    private String insuranceNumber;

    /**
     * 投保金额
     */
    private Double insuranceAmount;

    /**
     * 第一受益人
     */
    private String firstBeneficiary;

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsuranceDateFrom() {
        return insuranceDateFrom;
    }

    public void setInsuranceDateFrom(String insuranceDateFrom) {
        this.insuranceDateFrom = insuranceDateFrom;
    }

    public String getInsuranceDateTo() {
        return insuranceDateTo;
    }

    public void setInsuranceDateTo(String insuranceDateTo) {
        this.insuranceDateTo = insuranceDateTo;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public Double getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(Double insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public String getFirstBeneficiary() {
        return firstBeneficiary;
    }

    public void setFirstBeneficiary(String firstBeneficiary) {
        this.firstBeneficiary = firstBeneficiary;
    }
}
