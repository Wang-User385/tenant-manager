//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "prj_quotation_cashflow"
)
@Getter
@Setter
public class PrjQuotationCashflow extends BaseDTO {
    @Id
    @GeneratedValue
    private Long quotationCashflowId;
    @NotNull
    private Long quotationId;
    private Long cfItem;
    private Long cfType;
    private String cfDirection;
    private String cfStatus;
    private Double times;
    private Date dueDate;
    private Date finIncomeDate;
    private Double dueAmount;
    private Double netDueAmount;
    private Double vatDueAmount;
    private Double principal;
    private Double netPrincipal;
    private Double vatPrincipal;
    private Double interest;
    private Double netInterest;
    private Double vatInterest;
    private Double outstandingRental;
    private Double outstandingPrincipal;
    private Double outstandingInterest;
    private Double interestAccrualBalance;
    private Double accumulatedUnpaidInterest;
    private Double vatRate;
    private Double cashflowIrr; //净现金流
    private Double cashflowIrrAfterTax; //净现金流（不含税）
    @Transient
    private String description;
    @Transient
    private Long projectId;

    private Long principalTimes;
    private Date principalDueDate;
    private Long interestTimes;
    private Date interestDueDate;

    public Double getCashflowIrr() {
        return cashflowIrr;
    }

    public void setCashflowIrr(Double cashflowIrr) {
        this.cashflowIrr = cashflowIrr;
    }

    public Double getCashflowIrrAfterTax() {
        return cashflowIrrAfterTax;
    }

    public void setCashflowIrrAfterTax(Double cashflowIrrAfterTax) {
        this.cashflowIrrAfterTax = cashflowIrrAfterTax;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getQuotationCashflowId() {
        return this.quotationCashflowId;
    }

    public void setQuotationCashflowId(Long quotationCashflowId) {
        this.quotationCashflowId = quotationCashflowId;
    }

    public Long getQuotationId() {
        return this.quotationId;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public Long getCfItem() {
        return this.cfItem;
    }

    public void setCfItem(Long cfItem) {
        this.cfItem = cfItem;
    }

    public Long getCfType() {
        return this.cfType;
    }

    public void setCfType(Long cfType) {
        this.cfType = cfType;
    }

    public String getCfDirection() {
        return this.cfDirection;
    }

    public void setCfDirection(String cfDirection) {
        this.cfDirection = cfDirection;
    }

    public String getCfStatus() {
        return this.cfStatus;
    }

    public void setCfStatus(String cfStatus) {
        this.cfStatus = cfStatus;
    }

    public Double getTimes() {
        return this.times;
    }

    public void setTimes(Double times) {
        this.times = times;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getFinIncomeDate() {
        return this.finIncomeDate;
    }

    public void setFinIncomeDate(Date finIncomeDate) {
        this.finIncomeDate = finIncomeDate;
    }

    public Double getDueAmount() {
        return this.dueAmount;
    }

    public void setDueAmount(Double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public Double getNetDueAmount() {
        return this.netDueAmount;
    }

    public void setNetDueAmount(Double netDueAmount) {
        this.netDueAmount = netDueAmount;
    }

    public Double getVatDueAmount() {
        return this.vatDueAmount;
    }

    public void setVatDueAmount(Double vatDueAmount) {
        this.vatDueAmount = vatDueAmount;
    }

    public Double getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Double principal) {
        this.principal = principal;
    }

    public Double getNetPrincipal() {
        return this.netPrincipal;
    }

    public void setNetPrincipal(Double netPrincipal) {
        this.netPrincipal = netPrincipal;
    }

    public Double getVatPrincipal() {
        return this.vatPrincipal;
    }

    public void setVatPrincipal(Double vatPrincipal) {
        this.vatPrincipal = vatPrincipal;
    }

    public Double getInterest() {
        return this.interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getNetInterest() {
        return this.netInterest;
    }

    public void setNetInterest(Double netInterest) {
        this.netInterest = netInterest;
    }

    public Double getVatInterest() {
        return this.vatInterest;
    }

    public void setVatInterest(Double vatInterest) {
        this.vatInterest = vatInterest;
    }

    public Double getOutstandingRental() {
        return this.outstandingRental;
    }

    public void setOutstandingRental(Double outstandingRental) {
        this.outstandingRental = outstandingRental;
    }

    public Double getOutstandingPrincipal() {
        return this.outstandingPrincipal;
    }

    public void setOutstandingPrincipal(Double outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public Double getOutstandingInterest() {
        return this.outstandingInterest;
    }

    public void setOutstandingInterest(Double outstandingInterest) {
        this.outstandingInterest = outstandingInterest;
    }

    public Double getInterestAccrualBalance() {
        return this.interestAccrualBalance;
    }

    public void setInterestAccrualBalance(Double interestAccrualBalance) {
        this.interestAccrualBalance = interestAccrualBalance;
    }

    public Double getAccumulatedUnpaidInterest() {
        return this.accumulatedUnpaidInterest;
    }

    public void setAccumulatedUnpaidInterest(Double accumulatedUnpaidInterest) {
        this.accumulatedUnpaidInterest = accumulatedUnpaidInterest;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getVatRate() {
        return this.vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }
}
