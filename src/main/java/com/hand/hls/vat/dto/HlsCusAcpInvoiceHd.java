package com.hand.hls.vat.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "acp_invoice_hd"
)
public class HlsCusAcpInvoiceHd extends AcpInvoiceHd {

    private String invoiceCode;

    private String postedType;


    public String getPostedType() {
        return postedType;
    }

    public void setPostedType(String postedType) {
        this.postedType = postedType;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public HlsCusAcpInvoiceHd() {
    }
}
