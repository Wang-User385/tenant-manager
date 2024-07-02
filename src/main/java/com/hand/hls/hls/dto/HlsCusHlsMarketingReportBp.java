package com.hand.hls.hls.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "HLS_MARKETING_REPORT_BP"
)
public class HlsCusHlsMarketingReportBp extends HlsMarketingReportBp {
    public HlsCusHlsMarketingReportBp() {
    }

    @Transient
    private String bpIdN;
    @Transient
    private String bpCode;
    @Transient
    private String bpTypeN;
    @Transient
    private String bpClass;
    @Transient
    private String membershipGroup;
    @Transient
    private String whetherExclusiveIndustry;
    @Transient
    private String whetherTotalCredit;
    @Transient
    private String whetherStockCustomer;
    @Transient
    private String industryCategories;
    @Transient
    private String economicInduClassifyN;
    @Transient
    private String economicInduClassSecN;
    @Transient
    private String economicInduClassThrN;
    private String bpType;
    @Transient
    private String groupMembership;
    @Transient
    private String countryN;
    @Transient
    private String provinceN;
    @Transient
    private String cityN;
    @Transient
    private String districtN;
    private String status;

    @Transient
    private String provincialCapital;
    @Transient
    private String economicZoneN;
    @Transient
    private String listedCompanyN;
    @Transient
    private String listingCode;
    @Transient
    private String bpFinancialTypeN;

    private String bpCatagory;

    public String getBpCatagory() {
        return bpCatagory;
    }

    public void setBpCatagory(String bpCatagory) {
        this.bpCatagory = bpCatagory;
    }

    public String getBpClass() {
        return bpClass;
    }

    public void setBpClass(String bpClass) {
        this.bpClass = bpClass;
    }

    public String getProvincialCapital() {
        return provincialCapital;
    }

    public void setProvincialCapital(String provincialCapital) {
        this.provincialCapital = provincialCapital;
    }

    public String getEconomicZoneN() {
        return economicZoneN;
    }

    public void setEconomicZoneN(String economicZoneN) {
        this.economicZoneN = economicZoneN;
    }

    public String getListedCompanyN() {
        return listedCompanyN;
    }

    public void setListedCompanyN(String listedCompanyN) {
        this.listedCompanyN = listedCompanyN;
    }

    public String getListingCode() {
        return listingCode;
    }

    public void setListingCode(String listingCode) {
        this.listingCode = listingCode;
    }

    public String getBpFinancialTypeN() {
        return bpFinancialTypeN;
    }

    public void setBpFinancialTypeN(String bpFinancialTypeN) {
        this.bpFinancialTypeN = bpFinancialTypeN;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpType() {
        return bpType;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public String getBpIdN() {
        return bpIdN;
    }

    public void setBpIdN(String bpIdN) {
        this.bpIdN = bpIdN;
    }

    public String getBpTypeN() {
        return bpTypeN;
    }

    public void setBpTypeN(String bpTypeN) {
        this.bpTypeN = bpTypeN;
    }

    public String getMembershipGroup() {
        return membershipGroup;
    }

    public void setMembershipGroup(String membershipGroup) {
        this.membershipGroup = membershipGroup;
    }

    public String getWhetherExclusiveIndustry() {
        return whetherExclusiveIndustry;
    }

    public void setWhetherExclusiveIndustry(String whetherExclusiveIndustry) {
        this.whetherExclusiveIndustry = whetherExclusiveIndustry;
    }

    public String getWhetherTotalCredit() {
        return whetherTotalCredit;
    }

    public void setWhetherTotalCredit(String whetherTotalCredit) {
        this.whetherTotalCredit = whetherTotalCredit;
    }

    public String getWhetherStockCustomer() {
        return whetherStockCustomer;
    }

    public void setWhetherStockCustomer(String whetherStockCustomer) {
        this.whetherStockCustomer = whetherStockCustomer;
    }

    public String getIndustryCategories() {
        return industryCategories;
    }

    public void setIndustryCategories(String industryCategories) {
        this.industryCategories = industryCategories;
    }

    public String getEconomicInduClassifyN() {
        return economicInduClassifyN;
    }

    public void setEconomicInduClassifyN(String economicInduClassifyN) {
        this.economicInduClassifyN = economicInduClassifyN;
    }

    public String getEconomicInduClassSecN() {
        return economicInduClassSecN;
    }

    public void setEconomicInduClassSecN(String economicInduClassSecN) {
        this.economicInduClassSecN = economicInduClassSecN;
    }

    public String getEconomicInduClassThrN() {
        return economicInduClassThrN;
    }

    public void setEconomicInduClassThrN(String economicInduClassThrN) {
        this.economicInduClassThrN = economicInduClassThrN;
    }

    public String getGroupMembership() {
        return groupMembership;
    }

    public void setGroupMembership(String groupMembership) {
        this.groupMembership = groupMembership;
    }

    public String getCountryN() {
        return countryN;
    }

    public void setCountryN(String countryN) {
        this.countryN = countryN;
    }

    public String getProvinceN() {
        return provinceN;
    }

    public void setProvinceN(String provinceN) {
        this.provinceN = provinceN;
    }

    public String getCityN() {
        return cityN;
    }

    public void setCityN(String cityN) {
        this.cityN = cityN;
    }

    public String getDistrictN() {
        return districtN;
    }

    public void setDistrictN(String districtN) {
        this.districtN = districtN;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
