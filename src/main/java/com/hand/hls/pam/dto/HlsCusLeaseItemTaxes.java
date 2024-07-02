package com.hand.hls.pam.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/16 - 10:38
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ExtensionAttribute(disable = true)
@Table(name = "hls_lease_item_taxes")
public class HlsCusLeaseItemTaxes extends BaseDTO {
    @Id
    @GeneratedValue
    private Long leaseTaxesId;
    private Long leaseItemId;


    private String ifPayedRelatedTax;

    private String taxName;

    private Double taxRate;

    private Double invoiceAmount;

    private String vatDeductionVoucher;
    private String voucherDeductionPrice;
    private String voucherCode;
    private String documentNumber;
    private String ifIncludedInPrincipal;
    private String description;

    @Transient
    private String ifPayedRelatedTaxN;

    @Transient
    private String ifIncludedInPrincipalN;

    @Transient
    private String taxNameN;

    @Transient
    private String vatDeductionVoucherN;

    @Transient
    private String voucherDeductionPriceN;



}
