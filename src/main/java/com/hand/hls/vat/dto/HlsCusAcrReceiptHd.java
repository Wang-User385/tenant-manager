package com.hand.hls.vat.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

@ExtensionAttribute(disable = true)
@Table(name = "acr_receipt_hd")
public class HlsCusAcrReceiptHd extends AcrReceiptHd {

    private String receiptType;

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }
}
