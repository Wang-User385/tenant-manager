package com.hand.hls.vat.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ACR_INVOICE_RELATIONSHIP"
)
@Getter
@Setter
public class AcrInvoiceRelationship extends BaseDTO {
    @Id
    @GeneratedValue
    private Long invoiceRelationId;
    private Long invoiceLnId;
    private String sourceDocumentType;
    private Long sourceDocumentId;
    private Double billingAmount;
    private Double taxTypeRate;
    private String taxIncludedFlag;
    private Double taxAmount;
    private Double netAmount;
    private String description;
    private String invoiceKind;
    private Long bpId;

    public AcrInvoiceRelationship() {
    }

    public Long getInvoiceRelationId() {
        return this.invoiceRelationId;
    }

    public void setInvoiceRelationId(Long invoiceRelationId) {
        this.invoiceRelationId = invoiceRelationId;
    }

    public Long getInvoiceLnId() {
        return this.invoiceLnId;
    }

    public void setInvoiceLnId(Long invoiceLnId) {
        this.invoiceLnId = invoiceLnId;
    }

    public String getSourceDocumentType() {
        return this.sourceDocumentType;
    }

    public void setSourceDocumentType(String sourceDocumentType) {
        this.sourceDocumentType = sourceDocumentType == null ? null : sourceDocumentType.trim();
    }

    public Long getSourceDocumentId() {
        return this.sourceDocumentId;
    }

    public void setSourceDocumentId(Long sourceDocumentId) {
        this.sourceDocumentId = sourceDocumentId;
    }

    public Double getBillingAmount() {
        return this.billingAmount;
    }

    public void setBillingAmount(Double billingAmount) {
        this.billingAmount = billingAmount;
    }

    public Double getTaxTypeRate() {
        return this.taxTypeRate;
    }

    public void setTaxTypeRate(Double taxTypeRate) {
        this.taxTypeRate = taxTypeRate;
    }

    public String getTaxIncludedFlag() {
        return this.taxIncludedFlag;
    }

    public void setTaxIncludedFlag(String taxIncludedFlag) {
        this.taxIncludedFlag = taxIncludedFlag == null ? null : taxIncludedFlag.trim();
    }

    public Double getTaxAmount() {
        return this.taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getNetAmount() {
        return this.netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getInvoiceKind() {
        return this.invoiceKind;
    }

    public void setInvoiceKind(String invoiceKind) {
        this.invoiceKind = invoiceKind == null ? null : invoiceKind.trim();
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }
}
