package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hls.prj.dto.HlsBpMasterAddress;

import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(disable = true)
@Table(name = "hls_bp_master_address")
public class HlsCusBpMasterAddress extends HlsBpMasterAddress {
    private String isMailInvoiceAddress;
    private String region;

    @Transient
    private String provinceDesc;
    @Transient
    private String cityDesc;
    @Transient
    private String districtDesc;

    @Transient
    private String contactInfo;
    @Transient
    private String addressInfo;
    @Transient
    private String regionN;

    private String remark;

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    public String getIsMailInvoiceAddress() {
        return isMailInvoiceAddress;
    }

    public void setIsMailInvoiceAddress(String isMailInvoiceAddress) {
        this.isMailInvoiceAddress = isMailInvoiceAddress;
    }

    public String getProvinceDesc() {
        return provinceDesc;
    }

    public void setProvinceDesc(String provinceDesc) {
        this.provinceDesc = provinceDesc;
    }

    public String getCityDesc() {
        return cityDesc;
    }

    public void setCityDesc(String cityDesc) {
        this.cityDesc = cityDesc;
    }

    public String getDistrictDesc() {
        return districtDesc;
    }

    public void setDistrictDesc(String districtDesc) {
        this.districtDesc = districtDesc;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionN() {
        return regionN;
    }

    public void setRegionN(String regionN) {
        this.regionN = regionN;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}