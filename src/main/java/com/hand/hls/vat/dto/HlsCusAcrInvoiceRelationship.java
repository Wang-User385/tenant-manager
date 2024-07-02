package com.hand.hls.vat.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ACR_INVOICE_RELATIONSHIP"
)
public class HlsCusAcrInvoiceRelationship extends AcrInvoiceRelationship {
    public HlsCusAcrInvoiceRelationship() {
    }
}
