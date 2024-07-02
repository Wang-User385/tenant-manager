package com.hand.hls.vat.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "acp_invoice_ln"
)
@Getter
@Setter
public class HlsCusAcpInvoiceLn extends AcpInvoiceLn {

    public HlsCusAcpInvoiceLn() {
    }
    private String documentNumber;

    private Long invoiceHdId;
    private String bpName;
    private String bpTaxRegistryNum;
    @Transient
    private Long headerId;
    @Transient
    private String attributes_1;
    @Transient
    private String attributes_2;
    @Transient
    private String attributes_3;
    @Transient
    private String attributes_4;
    @Transient
    private String attributes_5;
    @Transient
    private String attributes_6;
    @Transient
    private String attributes_7;
    @Transient
    private String attributes_8;
    @Transient
    private String attributes_9;
    @Transient
    private String attributes_10;
    @Transient
    private String attributes_11;
    @Transient
    private String attributes_12;
    @Transient
    private String attributes_13;
    @Transient
    private String attributes_14;
    @Transient
    private Long readLine;
    @Transient
    private Long lineNumber;
    @Transient
    private Long attachCount;
    @Transient
    private String taxTypeRateN;

    @Transient
    private Double contractAmount;

}