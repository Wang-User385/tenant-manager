//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.vat.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "acr_invoice_hd_temp"
)
public class AcrInvoiceHdTemp extends BaseDTO {
    public static final String FIELD_INVOICE_HD_TEMP_ID = "invoiceHdTempId";
    public static final String FIELD_CASHFLOW_ID = "cashflowId";
    public static final String FIELD_BILLING_WAY = "billingWay";
    public static final String FIELD_INVOICE_DATE = "invoiceDate";
    public static final String FIELD_BP_ID = "bpId";
    @Id
    private String invoiceHdTempId;
    @NotNull
    @Id
    private Long cashflowId;
    @Length(
            max = 10
    )
    private String billingWay;
    private Date invoiceDate;
    private Long bpId;
    private String description;
    private String billingType;

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public AcrInvoiceHdTemp() {
    }

    public void setInvoiceHdTempId(String invoiceHdTempId) {
        this.invoiceHdTempId = invoiceHdTempId;
    }

    public String getInvoiceHdTempId() {
        return this.invoiceHdTempId;
    }

    public void setCashflowId(Long cashflowId) {
        this.cashflowId = cashflowId;
    }

    public Long getCashflowId() {
        return this.cashflowId;
    }

    public void setBillingWay(String billingWay) {
        this.billingWay = billingWay;
    }

    public String getBillingWay() {
        return this.billingWay;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getInvoiceDate() {
        return this.invoiceDate;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
