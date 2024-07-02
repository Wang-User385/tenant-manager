package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by wangyan on 2017/10/24.
 * Modify by zhangyu on 2018/5/30
 */
@ExtensionAttribute(disable = true)
@Table(name = "con_floating_rate_req_ln")
@Getter
@Setter
public class HlsCusConFloatingRateReqLn extends ConFloatingRateReqLn {
    public static final String FIELD_CHANGE_REQ_ID = "changeReqId";

    private Long changeReqId;

    public Long getChangeReqId() {
        return changeReqId;
    }

    public void setChangeReqId(Long changeReqId) {
        this.changeReqId = changeReqId;
    }

    @Transient
    private String documentTypeDesc;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    @Transient
    private Long documentId;

    public String getFloatingrangeMethodRemark() {
        return floatingrangeMethodRemark;
    }

    public void setFloatingrangeMethodRemark(String floatingrangeMethodRemark) {
        this.floatingrangeMethodRemark = floatingrangeMethodRemark;
    }

    @Transient
    private String floatingrangeMethodRemark;

    public Date getFloatingRateStartDate() {
        return floatingRateStartDate;
    }

    public void setFloatingRateStartDate(Date floatingRateStartDate) {
        this.floatingRateStartDate = floatingRateStartDate;
    }

    @Transient
    private Date floatingRateStartDate;

    public String getDocumentTypeDesc() {
        return documentTypeDesc;
    }

    public void setDocumentTypeDesc(String documentTypeDesc) {
        this.documentTypeDesc = documentTypeDesc;
    }

    @Transient
    private Double interestAdjAmountSum;

    @Transient
    private int contractCount;
    @Transient
    private String bpCode;


    public Double getInterestAdjAmountSum() {
        return interestAdjAmountSum;
    }

    public void setInterestAdjAmountSum(Double interestAdjAmountSum) {
        this.interestAdjAmountSum = interestAdjAmountSum;
    }

    public int getContractCount() {
        return contractCount;
    }

    public void setContractCount(int contractCount) {
        this.contractCount = contractCount;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    @Transient
    private String priceListN;

    @Transient
    private String description;

    @Transient
    private Double oldDueAmount;

    @Transient
    private Double newDueAmount;

    @Transient
    private Double oldPrincipal;

    @Transient
    private Double newPrincipal;

    @Transient
    private Double oldInterest;

    @Transient
    private Double newInterest;

    @Transient
    private Double oldOutstandingInterest;

    @Transient
    private Double newOutstandingInterest;

    private String adjustRatePrice;

    @Transient
    private String contractNumberCon;

    @Transient
    private String hostUnitId;
    @Transient
    private String unitName;
    @Transient
    private String companyName;
    //@Transient
    private Date lprLinkDate;
    //@Transient
    private Date nextAdjustmentDate;
    @Transient
    private String lprBaseDate;
    @Transient
    private String lprBaseDateN;
    @Transient
    private String lprFixedDate;
    @Transient
    private String lprAdjustmentTerm;
    @Transient
    private String lprAdjustmentPeriodN;
    @Transient
    private String intRateType;
    @Transient
    private String intRateTypeN;

    private String newBaseRateCollect;
    @Transient
    private String newBaseRateCollectN;

    @Transient
    private String floatType;

    @Transient
    private Double floatingWayRate;

    @Transient
    private Double baseRate;

    @Transient
    private Double intRate;

    @Transient
    private String baseRateTypeN;


    public String getPriceListN() {
        return priceListN;
    }

    public void setPriceListN(String priceListN) {
        this.priceListN = priceListN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Long quotationIdNew;

    public Long getQuotationIdNew() {
        return quotationIdNew;
    }

    public void setQuotationIdNew(Long quotationIdNew) {
        this.quotationIdNew = quotationIdNew;
    }

    public Double getOldDueAmount() {
        return oldDueAmount;
    }

    public void setOldDueAmount(Double oldDueAmount) {
        this.oldDueAmount = oldDueAmount;
    }

    public Double getNewDueAmount() {
        return newDueAmount;
    }

    public void setNewDueAmount(Double newDueAmount) {
        this.newDueAmount = newDueAmount;
    }

    public Double getOldPrincipal() {
        return oldPrincipal;
    }

    public void setOldPrincipal(Double oldPrincipal) {
        this.oldPrincipal = oldPrincipal;
    }

    public Double getNewPrincipal() {
        return newPrincipal;
    }

    public void setNewPrincipal(Double newPrincipal) {
        this.newPrincipal = newPrincipal;
    }

    public Double getOldInterest() {
        return oldInterest;
    }

    public void setOldInterest(Double oldInterest) {
        this.oldInterest = oldInterest;
    }

    public Double getNewInterest() {
        return newInterest;
    }

    public void setNewInterest(Double newInterest) {
        this.newInterest = newInterest;
    }

    public Double getOldOutstandingInterest() {
        return oldOutstandingInterest;
    }

    public void setOldOutstandingInterest(Double oldOutstandingInterest) {
        this.oldOutstandingInterest = oldOutstandingInterest;
    }

    public Double getNewOutstandingInterest() {
        return newOutstandingInterest;
    }

    public void setNewOutstandingInterest(Double newOutstandingInterest) {
        this.newOutstandingInterest = newOutstandingInterest;
    }
}
