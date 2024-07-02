package com.hand.hls.vat.dto;


import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "con_contract_bank_account"
)
@Getter
@Setter
public class AcrInvoiceBill extends BaseDTO {

    @Id
    @GeneratedValue
    private Long bankAccountId;
    private Long contractId;
    private String bankAccountNum;
    private String bankAccountCode;
    private String bankAccountName;
    private String invoiceKind;
    private String billingWay;
    private String productName;
    private Long bpId;
    private String bpTaxRegistryNum;
    private String bpAddressPhoneNum;
    private String bpBankAccount;
    private String taxTypeRate;


    @Transient
    private String contractNumber;

    @Transient
    private String contractName;

    @Transient
    private String tenantIdN;

    @Transient
    private String contractAmount;

    @Transient
    private String hostProjectManager;

    @Transient
    private String hostProjectManagerN;

    @Transient
    private String invoiceKindN;

    @Transient
    private String billingWayN;

    @Transient
    private String bpIdN;

    @Transient
    private String taxTypeRateN;

    @Transient
    private Long cashflowId;

    @Transient
    private Double dueAmount;

    @Transient
    private Long companyId;

    @Transient
    private String billingStatus;

    @Transient
    private String bpName;

    @Transient
    private String taxpayerType;

    @Transient
    private String writeOffFlag;

    @Transient
    private Date dueDate;

    @Transient
    private String contractStatus;

    @Transient
    private String documentCategory;

    @Transient
    private String documentType;

    @Transient
    private String businessType;

    @Transient
    private String currency;

    @Transient
    private String billingMethod;

    @Transient
    private Double billingAmount;

    @Transient
    private Double totalAmount;

    @Transient
    private Long projectId;

    @Transient
    private String legalContractNumber;

    public AcrInvoiceBill() {
    }


}
