package com.hand.hls.hls.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "HLS_MARKETING_REPORT"
)
public class HlsCusHlsMarketingReport extends HlsMarketingReport {
    public HlsCusHlsMarketingReport() {
    }
    @Transient
    private String tenant;
    @Transient
    private String otherBpName;
    @Transient
    private String unitIdN;
    @Transient
    private String failureDate;
    @Transient
    private String applyLeasingProductsN;
    @Transient
    private Long unitId;
    @Transient
    private String creditFlagN;
    @Transient
    private String bpName;


    @Transient
    private String hostProjectManagerN;
    @Transient
    private String hostUnitName;
    @Transient
    private String assistProjectManagerN;
    @Transient
    private String assistUnitName;
    @Transient
    private String fallBankN;
    @Transient
    private String whetherGreenProjectN;
    @Transient
    private String statusN;
    @Transient
    private String projectManagerN;

    @Transient
    private String marketingChannelN;
    @Transient
    private String xyBranchBankN;

    private String fromProject;
    @Transient
    private String fromProjectN;
    private Long changeReqId;
    private String exclusiveIndustry;
    private String currency;
    @Transient
    private String exclusiveIndustryN;
    @Transient
    private String currencyN;
    private Long hostUnitId;
    private Long assistUnitId;

    private String revenueSharingBase;

    @Transient
    private String revenueSharingBaseN;

    private String revenueSharingBaseBc;

    private Double hostDepartmentProportionR;

    private Double assistDepartmentProportionR;
    @Transient
    private Date createdDate;
    @Transient
    private String companyIdN;
    @Transient
    private String insureFlagN;
    @Transient
    private String guaranteeMethodN;
    @Transient
    private String payMethodN;
    private String marketingReportName;
    private String enginAllFlag;
    private String machineDamageFlag;

    @Transient
    private String sourcePkValue;
    @Transient
    private String templetCode;
    private String contractTextStatus;
    private String docxFlag;

    public String getProjectManagerN() {
        return projectManagerN;
    }

    public void setProjectManagerN(String projectManagerN) {
        this.projectManagerN = projectManagerN;
    }

    public String getContractTextStatus() {
        return contractTextStatus;
    }

    public void setContractTextStatus(String contractTextStatus) {
        this.contractTextStatus = contractTextStatus;
    }

    public String getDocxFlag() {
        return docxFlag;
    }

    public void setDocxFlag(String docxFlag) {
        this.docxFlag = docxFlag;
    }

    public String getSourcePkValue() {
        return sourcePkValue;
    }

    public void setSourcePkValue(String sourcePkValue) {
        this.sourcePkValue = sourcePkValue;
    }

    public String getTempletCode() {
        return templetCode;
    }

    public void setTempletCode(String templetCode) {
        this.templetCode = templetCode;
    }

    public String getMachineDamageFlag() {
        return machineDamageFlag;
    }

    public void setMachineDamageFlag(String machineDamageFlag) {
        this.machineDamageFlag = machineDamageFlag;
    }

    public String getEnginAllFlag() {
        return enginAllFlag;
    }

    public void setEnginAllFlag(String enginAllFlag) {
        this.enginAllFlag = enginAllFlag;
    }

    public String getMarketingReportName() {
        return marketingReportName;
    }

    public void setMarketingReportName(String marketingReportName) {
        this.marketingReportName = marketingReportName;
    }

    public String getCompanyIdN() {
        return companyIdN;
    }

    public void setCompanyIdN(String companyIdN) {
        this.companyIdN = companyIdN;
    }

    public String getInsureFlagN() {
        return insureFlagN;
    }

    public void setInsureFlagN(String insureFlagN) {
        this.insureFlagN = insureFlagN;
    }

    public String getGuaranteeMethodN() {
        return guaranteeMethodN;
    }

    public void setGuaranteeMethodN(String guaranteeMethodN) {
        this.guaranteeMethodN = guaranteeMethodN;
    }

    public String getPayMethodN() {
        return payMethodN;
    }

    public void setPayMethodN(String payMethodN) {
        this.payMethodN = payMethodN;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreditFlagN() {
        return creditFlagN;
    }

    public void setCreditFlagN(String creditFlagN) {
        this.creditFlagN = creditFlagN;
    }

    public Long getHostUnitId() {
        return hostUnitId;
    }

    public void setHostUnitId(Long hostUnitId) {
        this.hostUnitId = hostUnitId;
    }
    public Long getChangeReqId() {
        return changeReqId;
    }

    public void setChangeReqId(Long changeReqId) {
        this.changeReqId = changeReqId;
    }

    public String getExclusiveIndustry() {
        return exclusiveIndustry;
    }

    public void setExclusiveIndustry(String exclusiveIndustry) {
        this.exclusiveIndustry = exclusiveIndustry;
    }

    public String getExclusiveIndustryN() {
        return exclusiveIndustryN;
    }

    public void setExclusiveIndustryN(String exclusiveIndustryN) {
        this.exclusiveIndustryN = exclusiveIndustryN;
    }

    public String getFromProject() {
        return fromProject;
    }

    public void setFromProject(String fromProject) {
        this.fromProject = fromProject;
    }

    public String getFromProjectN() {
        return fromProjectN;
    }

    public void setFromProjectN(String fromProjectN) {
        this.fromProjectN = fromProjectN;
    }

    public String getStatusN() {
        return statusN;
    }

    public void setStatusN(String statusN) {
        this.statusN = statusN;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getOtherBpName() {
        return otherBpName;
    }

    public void setOtherBpName(String otherBpName) {
        this.otherBpName = otherBpName;
    }

    public String getUnitIdN() {
        return unitIdN;
    }

    public void setUnitIdN(String unitIdN) {
        this.unitIdN = unitIdN;
    }

    public String getFailureDate() {
        return failureDate;
    }

    public void setFailureDate(String failureDate) {
        this.failureDate = failureDate;
    }

    public String getHostProjectManagerN() {
        return hostProjectManagerN;
    }

    public void setHostProjectManagerN(String hostProjectManagerN) {
        this.hostProjectManagerN = hostProjectManagerN;
    }

    public String getAssistProjectManagerN() {
        return assistProjectManagerN;
    }

    public void setAssistProjectManagerN(String assistProjectManagerN) {
        this.assistProjectManagerN = assistProjectManagerN;
    }

    public String getAssistUnitName() {
        return assistUnitName;
    }

    public void setAssistUnitName(String assistUnitName) {
        this.assistUnitName = assistUnitName;
    }

    public String getFallBankN() {
        return fallBankN;
    }

    public void setFallBankN(String fallBankN) {
        this.fallBankN = fallBankN;
    }

    public String getWhetherGreenProjectN() {
        return whetherGreenProjectN;
    }

    public void setWhetherGreenProjectN(String whetherGreenProjectN) {
        this.whetherGreenProjectN = whetherGreenProjectN;
    }

    public String getHostUnitName() {
        return hostUnitName;
    }

    public void setHostUnitName(String hostUnitName) {
        this.hostUnitName = hostUnitName;
    }

    public String getMarketingChannelN() {
        return marketingChannelN;
    }

    public void setMarketingChannelN(String marketingChannelN) {
        this.marketingChannelN = marketingChannelN;
    }

    public String getXyBranchBankN() {
        return xyBranchBankN;
    }

    public void setXyBranchBankN(String xyBranchBankN) {
        this.xyBranchBankN = xyBranchBankN;
    }

    public String getApplyLeasingProductsN() {
        return applyLeasingProductsN;
    }

    public void setApplyLeasingProductsN(String applyLeasingProductsN) {
        this.applyLeasingProductsN = applyLeasingProductsN;
    }

    @Override
    public Long getUnitId() {
        return unitId;
    }

    @Override
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getAssistUnitId() {
        return assistUnitId;
    }

    public void setAssistUnitId(Long assistUnitId) {
        this.assistUnitId = assistUnitId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyN() {
        return currencyN;
    }

    public void setCurrencyN(String currencyN) {
        this.currencyN = currencyN;
    }

    public String getRevenueSharingBase() {
        return revenueSharingBase;
    }

    public void setRevenueSharingBase(String revenueSharingBase) {
        this.revenueSharingBase = revenueSharingBase;
    }

    public String getRevenueSharingBaseN() {
        return revenueSharingBaseN;
    }

    public void setRevenueSharingBaseN(String revenueSharingBaseN) {
        this.revenueSharingBaseN = revenueSharingBaseN;
    }

    public String getRevenueSharingBaseBc() {
        return revenueSharingBaseBc;
    }

    public void setRevenueSharingBaseBc(String revenueSharingBaseBc) {
        this.revenueSharingBaseBc = revenueSharingBaseBc;
    }

    public Double getHostDepartmentProportionR() {
        return hostDepartmentProportionR;
    }

    public void setHostDepartmentProportionR(Double hostDepartmentProportionR) {
        this.hostDepartmentProportionR = hostDepartmentProportionR;
    }

    public Double getAssistDepartmentProportionR() {
        return assistDepartmentProportionR;
    }

    public void setAssistDepartmentProportionR(Double assistDepartmentProportionR) {
        this.assistDepartmentProportionR = assistDepartmentProportionR;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }
}
