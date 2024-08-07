package com.hand.hls.prj.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "prj_quotation"
)
public class PrjQuotation extends BaseDTO {
    @Id
    @GeneratedValue
    private Long quotationId;
    @NotEmpty
    private String sourceDocumentCategory;
    @NotNull
    private Long sourceDocumentId;
    private String description;
    private String status;
    private String priceList;
    private Date leaseStartDate;
    private Double irr;
    private Long leaseTimes;
    private Long annualPayTimes;
    private Double leaseTerm;
    private Long payType;
    private String currency;
    private Long taxTypeId;
    private String vatFlag;
    private Double vatRate;
    private Double leaseItemAmount;
    private Double vatInput;
    private Long vatInputTaxTypeId;
    private Double vatInputTaxTypeRate;
    private Double downPaymentRatio;
    private Double downPayment;
    private Double netDownPayment;
    private Double vatDownPayment;
    private Double financeAmount;
    private Double netFinanceAmount;
    private Double vatFinanceAmount;
    private Double totalRental;
    private Double netTotalRental;
    private Double vatTotalRental;
    private Double totalInterest;
    private Double netTotalInterest;
    private Double vatTotalInterest;
    private Double leaseChargeRatio;
    private Double leaseCharge;
    private Double netLeaseCharge;
    private Double vatLeaseCharge;
    private Double leaseMgtFeeRatio;
    private Double leaseMgtFee;
    private Double netLeaseMgtFee;
    private Double vatLeaseMgtFee;
    private String leaseMgtFeeRule;
    private Double depositRatio;
    private Double deposit;
    private String depositDeduction;
    private Double residualRatio;
    private Double residualValue;
    private Double netResidualValue;
    private Double vatResidualValue;
    private Double balloonRatio;
    private Double balloon;
    private Double netBalloon;
    private Double vatBalloon;
    private String baseRateType;
    private Double baseRate;
    private Double intRate;
    private Double intRateImplicit;
    private String intRateType;
    private Double irrAfterTax;
    private Double pmt;
    private Double pmtFirst;
    private Double annualMeanRate;
    private String billingProfile;
    private String penaltyProfile;
    private String floatingRangeMethod;
    @Transient
    private String sheets;
    @Transient
    private String compressSheets;
    @Transient
    private Long projectId;
    @Transient
    private String priceListDesc;
    @Transient
    private String annualPayTimesDesc;
    @Transient
    private String intRateTypeDesc;
    //private Double loanReviewRatio;
    private Double surplusAmount;
    private Double gpsAmount;

    public PrjQuotation() {
    }


    public String getBillingProfile() {
        return this.billingProfile;
    }

    public void setBillingProfile(String billingProfile) {
        this.billingProfile = billingProfile;
    }

    public String getPenaltyProfile() {
        return this.penaltyProfile;
    }

    public void setPenaltyProfile(String penaltyProfile) {
        this.penaltyProfile = penaltyProfile;
    }

    public String getFloatingRangeMethod() {
        return this.floatingRangeMethod;
    }

    public void setFloatingRangeMethod(String floatingRangeMethod) {
        this.floatingRangeMethod = floatingRangeMethod;
    }

    public String getIntRateTypeDesc() {
        return this.intRateTypeDesc;
    }

    public void setIntRateTypeDesc(String intRateTypeDesc) {
        this.intRateTypeDesc = intRateTypeDesc;
    }

    public String getPriceListDesc() {
        return this.priceListDesc;
    }

    public void setPriceListDesc(String priceListDesc) {
        this.priceListDesc = priceListDesc;
    }

    public String getSheets() {
        return this.sheets;
    }

    public void setSheets(String sheets) {
        this.sheets = sheets;
    }

    public String getAnnualPayTimesDesc() {
        return this.annualPayTimesDesc;
    }

    public void setAnnualPayTimesDesc(String annualPayTimesDesc) {
        this.annualPayTimesDesc = annualPayTimesDesc;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public Long getQuotationId() {
        return this.quotationId;
    }

    public void setSourceDocumentCategory(String sourceDocumentCategory) {
        this.sourceDocumentCategory = sourceDocumentCategory;
    }

    public String getSourceDocumentCategory() {
        return this.sourceDocumentCategory;
    }

    public void setSourceDocumentId(Long sourceDocumentId) {
        this.sourceDocumentId = sourceDocumentId;
    }

    public Long getSourceDocumentId() {
        return this.sourceDocumentId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public String getPriceList() {
        return this.priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public Double getLeaseItemAmount() {
        return this.leaseItemAmount;
    }

    public void setLeaseItemAmount(Double leaseItemAmount) {
        this.leaseItemAmount = leaseItemAmount;
    }

    public Date getLeaseStartDate() {
        return this.leaseStartDate;
    }

    public void setLeaseStartDate(Date leaseStartDate) {
        this.leaseStartDate = leaseStartDate;
    }

    public Long getLeaseTimes() {
        return this.leaseTimes;
    }

    public void setLeaseTimes(Long leaseTimes) {
        this.leaseTimes = leaseTimes;
    }

    public Long getAnnualPayTimes() {
        return this.annualPayTimes;
    }

    public void setAnnualPayTimes(Long annualPayTimes) {
        this.annualPayTimes = annualPayTimes;
    }

    public Double getIrr() {
        return this.irr;
    }

    public void setIrr(Double irr) {
        this.irr = irr;
    }

    public Double getNetTotalRental() {
        return this.netTotalRental;
    }

    public void setNetTotalRental(Double netTotalRental) {
        this.netTotalRental = netTotalRental;
    }

    public Double getLeaseTerm() {
        return this.leaseTerm;
    }

    public void setLeaseTerm(Double leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public Long getPayType() {
        return this.payType;
    }

    public void setPayType(Long payType) {
        this.payType = payType;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getTaxTypeId() {
        return this.taxTypeId;
    }

    public void setTaxTypeId(Long taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public String getVatFlag() {
        return this.vatFlag;
    }

    public void setVatFlag(String vatFlag) {
        this.vatFlag = vatFlag;
    }

    public Double getVatRate() {
        return this.vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public Double getVatInput() {
        return this.vatInput;
    }

    public void setVatInput(Double vatInput) {
        this.vatInput = vatInput;
    }

    public Long getVatInputTaxTypeId() {
        return this.vatInputTaxTypeId;
    }

    public void setVatInputTaxTypeId(Long vatInputTaxTypeId) {
        this.vatInputTaxTypeId = vatInputTaxTypeId;
    }

    public Double getVatInputTaxTypeRate() {
        return this.vatInputTaxTypeRate;
    }

    public void setVatInputTaxTypeRate(Double vatInputTaxTypeRate) {
        this.vatInputTaxTypeRate = vatInputTaxTypeRate;
    }

    public Double getDownPaymentRatio() {
        return this.downPaymentRatio;
    }

    public void setDownPaymentRatio(Double downPaymentRatio) {
        this.downPaymentRatio = downPaymentRatio;
    }

    public Double getDownPayment() {
        return this.downPayment;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Double getNetDownPayment() {
        return this.netDownPayment;
    }

    public void setNetDownPayment(Double netDownPayment) {
        this.netDownPayment = netDownPayment;
    }

    public Double getVatDownPayment() {
        return this.vatDownPayment;
    }

    public void setVatDownPayment(Double vatDownPayment) {
        this.vatDownPayment = vatDownPayment;
    }

    public Double getFinanceAmount() {
        return this.financeAmount;
    }

    public void setFinanceAmount(Double financeAmount) {
        this.financeAmount = financeAmount;
    }

    public Double getNetFinanceAmount() {
        return this.netFinanceAmount;
    }

    public void setNetFinanceAmount(Double netFinanceAmount) {
        this.netFinanceAmount = netFinanceAmount;
    }

    public Double getVatFinanceAmount() {
        return this.vatFinanceAmount;
    }

    public void setVatFinanceAmount(Double vatFinanceAmount) {
        this.vatFinanceAmount = vatFinanceAmount;
    }

    public Double getTotalRental() {
        return this.totalRental;
    }

    public void setTotalRental(Double totalRental) {
        this.totalRental = totalRental;
    }

    public Double getVatTotalRental() {
        return this.vatTotalRental;
    }

    public void setVatTotalRental(Double vatTotalRental) {
        this.vatTotalRental = vatTotalRental;
    }

    public Double getTotalInterest() {
        return this.totalInterest;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getNetTotalInterest() {
        return this.netTotalInterest;
    }

    public void setNetTotalInterest(Double netTotalInterest) {
        this.netTotalInterest = netTotalInterest;
    }

    public Double getVatTotalInterest() {
        return this.vatTotalInterest;
    }

    public void setVatTotalInterest(Double vatTotalInterest) {
        this.vatTotalInterest = vatTotalInterest;
    }

    public Double getLeaseChargeRatio() {
        return this.leaseChargeRatio;
    }

    public void setLeaseChargeRatio(Double leaseChargeRatio) {
        this.leaseChargeRatio = leaseChargeRatio;
    }

    public Double getLeaseCharge() {
        return this.leaseCharge;
    }

    public void setLeaseCharge(Double leaseCharge) {
        this.leaseCharge = leaseCharge;
    }

    public Double getNetLeaseCharge() {
        return this.netLeaseCharge;
    }

    public void setNetLeaseCharge(Double netLeaseCharge) {
        this.netLeaseCharge = netLeaseCharge;
    }

    public Double getVatLeaseCharge() {
        return this.vatLeaseCharge;
    }

    public void setVatLeaseCharge(Double vatLeaseCharge) {
        this.vatLeaseCharge = vatLeaseCharge;
    }

    public Double getLeaseMgtFeeRatio() {
        return this.leaseMgtFeeRatio;
    }

    public void setLeaseMgtFeeRatio(Double leaseMgtFeeRatio) {
        this.leaseMgtFeeRatio = leaseMgtFeeRatio;
    }

    public Double getLeaseMgtFee() {
        return this.leaseMgtFee;
    }

    public void setLeaseMgtFee(Double leaseMgtFee) {
        this.leaseMgtFee = leaseMgtFee;
    }

    public Double getNetLeaseMgtFee() {
        return this.netLeaseMgtFee;
    }

    public void setNetLeaseMgtFee(Double netLeaseMgtFee) {
        this.netLeaseMgtFee = netLeaseMgtFee;
    }

    public Double getVatLeaseMgtFee() {
        return this.vatLeaseMgtFee;
    }

    public void setVatLeaseMgtFee(Double vatLeaseMgtFee) {
        this.vatLeaseMgtFee = vatLeaseMgtFee;
    }

    public String getLeaseMgtFeeRule() {
        return this.leaseMgtFeeRule;
    }

    public void setLeaseMgtFeeRule(String leaseMgtFeeRule) {
        this.leaseMgtFeeRule = leaseMgtFeeRule;
    }

    public Double getDepositRatio() {
        return this.depositRatio;
    }

    public void setDepositRatio(Double depositRatio) {
        this.depositRatio = depositRatio;
    }

    public Double getDeposit() {
        return this.deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public String getDepositDeduction() {
        return this.depositDeduction;
    }

    public void setDepositDeduction(String depositDeduction) {
        this.depositDeduction = depositDeduction;
    }

    public Double getResidualRatio() {
        return this.residualRatio;
    }

    public void setResidualRatio(Double residualRatio) {
        this.residualRatio = residualRatio;
    }

    public Double getResidualValue() {
        return this.residualValue;
    }

    public void setResidualValue(Double residualValue) {
        this.residualValue = residualValue;
    }

    public Double getNetResidualValue() {
        return this.netResidualValue;
    }

    public void setNetResidualValue(Double netResidualValue) {
        this.netResidualValue = netResidualValue;
    }

    public Double getVatResidualValue() {
        return this.vatResidualValue;
    }

    public void setVatResidualValue(Double vatResidualValue) {
        this.vatResidualValue = vatResidualValue;
    }

    public Double getBalloonRatio() {
        return this.balloonRatio;
    }

    public void setBalloonRatio(Double balloonRatio) {
        this.balloonRatio = balloonRatio;
    }

    public Double getBalloon() {
        return this.balloon;
    }

    public void setBalloon(Double balloon) {
        this.balloon = balloon;
    }

    public Double getNetBalloon() {
        return this.netBalloon;
    }

    public void setNetBalloon(Double netBalloon) {
        this.netBalloon = netBalloon;
    }

    public Double getVatBalloon() {
        return this.vatBalloon;
    }

    public void setVatBalloon(Double vatBalloon) {
        this.vatBalloon = vatBalloon;
    }

    public String getBaseRateType() {
        return this.baseRateType;
    }

    public void setBaseRateType(String baseRateType) {
        this.baseRateType = baseRateType;
    }

    public Double getBaseRate() {
        return this.baseRate;
    }

    public void setBaseRate(Double baseRate) {
        this.baseRate = baseRate;
    }

    public Double getIntRate() {
        return this.intRate;
    }

    public void setIntRate(Double intRate) {
        this.intRate = intRate;
    }

    public Double getIntRateImplicit() {
        return this.intRateImplicit;
    }

    public void setIntRateImplicit(Double intRateImplicit) {
        this.intRateImplicit = intRateImplicit;
    }

    public String getIntRateType() {
        return this.intRateType;
    }

    public void setIntRateType(String intRateType) {
        this.intRateType = intRateType;
    }

    public Double getIrrAfterTax() {
        return this.irrAfterTax;
    }

    public void setIrrAfterTax(Double irrAfterTax) {
        this.irrAfterTax = irrAfterTax;
    }

    public Double getPmt() {
        return this.pmt;
    }

    public void setPmt(Double pmt) {
        this.pmt = pmt;
    }

    public Double getPmtFirst() {
        return this.pmtFirst;
    }

    public void setPmtFirst(Double pmtFirst) {
        this.pmtFirst = pmtFirst;
    }

    public Double getAnnualMeanRate() {
        return this.annualMeanRate;
    }

    public void setAnnualMeanRate(Double annualMeanRate) {
        this.annualMeanRate = annualMeanRate;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCompressSheets() {
        return compressSheets;
    }

    public void setCompressSheets(String compressSheets) {
        this.compressSheets = compressSheets;
    }

    public Double getSurplusAmount() {
        return surplusAmount;
    }

    public void setSurplusAmount(Double surplusAmount) {
        this.surplusAmount = surplusAmount;
    }

    public Double getGpsAmount() {
        return gpsAmount;
    }

    public void setGpsAmount(Double gpsAmount) {
        this.gpsAmount = gpsAmount;
    }
}