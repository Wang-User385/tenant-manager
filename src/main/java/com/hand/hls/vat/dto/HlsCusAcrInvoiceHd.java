package com.hand.hls.vat.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "acr_invoice_hd")
@Getter
@Setter
public class HlsCusAcrInvoiceHd extends AcrInvoiceHd {
    private String sourceDocumentType;   // 单据来源类型

    @Transient
    private String taxTypeCodeDescription; //适用税率描述

    @Transient
    private String invoiceTitle;  //发票抬头

    @Transient
    private Long quotationCashflowId; //现金流id

    @Transient
    private String time;//传入时间

    @Transient
    private String invoiceDateFlag;// 发票创建时间标志

    @Transient
    private Long[] invoiceKinds;  // 发票种类

    @Transient
    private String dateFlag;  // 收据时间标志

    @Transient
    private String receiptNameOrNumber;  // 合同编号、合同名称或承租人名称

    @Transient
    private Long itemFlag;  // 应收项目标志

    @Transient
    private String contractName;  // 合同名称

    @Transient
    private String contractNumber;  // 合同编号
    @Transient
    private Date dueDate;  // 应收日期

    @Transient
    private String projectDesc;  // 项目描述

    @Transient
    private Long timesFrom;  // 期数从

    @Transient
    private Long timesTo;  // 期数到

    @Transient
    private Date dueDateFrom;  // 应收日期从

    @Transient
    private Date dueDateTo;  // 应收日期到

    @Transient
    private Double iamountFrom;  // 金额从

    @Transient
    private Double iamountTo;   // 金额到

    @Transient
    private Double lnTotalAmount;   // 金额到

    @Transient
    private String dateFrom;   // 日期从

    @Transient
    private String dateTo;   // 日期到

    @Transient
    private Long invoiceLnId;
    @Transient
    private String productName;
    @Transient
    private String receiptStatusDesc;
    @Transient
    private Long quantity;

    @Transient

    private Double totalAmount;

    @Transient
    private Double taxTypeRate;

    @Transient
    private Double taxAmount;

    @Transient
    private Long invoiceRelationId;

    @Transient
    private Long sourceDocumentId;

    @Transient
    private String bpAddressPhoneNum;

    @Transient
    private String cfItem;

    @Transient
    private String cfItemN;

    @Transient
    private Double dueAmount;

    @Transient
    private Double receivedAmount;

    @Transient
    private String receivedPrincipal;
    @Transient
    private String receivedInterest;
    @Transient
    private String taxableServiceName;
    @Transient
    private Long times;
    @Transient
    private String cfType;
    @Transient
    private Double billingPrincipal;
    @Transient
    private Double billingInterest;
    @Transient
    private String companyName;
    @Transient
    private String sourceType;
    @Transient
    private String receiptDate;

    @Transient
    private double billingAmount;

    @Transient
    private String cfItemDesc;

    @Transient
    private String redInvoiceCode;

    @Transient
    private String redInvoiceNumber;

    @Transient
    private String uom;

    @Transient
    private double price;

    @Transient
    private String[] invoiceHdIdArr;  // 期数从

    @Transient
    private String invoiceStatusN;

    @Transient
    private String invoiceStatusDesc;

    @Transient
    private Date taxInvoiceDate;

    @Transient
    private String isCancelFlag;

    @Transient
    private String invoiceKindDesc;

    @Transient
    private String invoiceKindN;
    @Transient
    private Long receiptLnId;

    @Transient
    private String receiptStatus;

    @Transient
    private String documentResource;

    @Transient
    private String createdByN;

    @Transient
    private String confirmedByN;

    @Transient
    private String invoicePeriod;
    @Transient
    private String currencyN;

    @Transient
    private String writeOffFlagN;
    @Transient
    private String companyFullName;
    @Transient
    private String billingType;

    private String dzfpUrl;
    private String confirmStatus;
    @Transient
    private String confirmStatusN;

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public String getCompanyFullName() {
        return companyFullName;
    }

    public void setCompanyFullName(String companyFullName) {
        this.companyFullName = companyFullName;
    }

    public String getInvoiceStatusDesc() {
        return invoiceStatusDesc;
    }

    public void setInvoiceStatusDesc(String invoiceStatusDesc) {
        this.invoiceStatusDesc = invoiceStatusDesc;
    }

    @Override
    public String getBpAddressPhoneNum() {
        return bpAddressPhoneNum;
    }

    @Override
    public void setBpAddressPhoneNum(String bpAddressPhoneNum) {
        this.bpAddressPhoneNum = bpAddressPhoneNum;
    }


    @Transient
    private String bpAddressPhoneNumber;

    public String getBpAddressPhoneNumber() {
        return bpAddressPhoneNumber;
    }

    public void setBpAddressPhoneNumber(String bpAddressPhoneNumber) {
        this.bpAddressPhoneNumber = bpAddressPhoneNumber;
    }

    @Transient
    private String contractNameOrNumber;   // 合同名称或者编号

    private String invoiceCode;//发票号码

    public Long getInvoiceLnId() {
        return invoiceLnId;
    }

    public void setInvoiceLnId(Long invoiceLnId) {
        this.invoiceLnId = invoiceLnId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public Double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public Double getTaxAmount() {
        return taxAmount;
    }

    @Override
    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Long getInvoiceRelationId() {
        return invoiceRelationId;
    }

    public void setInvoiceRelationId(Long invoiceRelationId) {
        this.invoiceRelationId = invoiceRelationId;
    }


    public String getSourceDocumentType() {
        return sourceDocumentType;
    }

    public void setSourceDocumentType(String sourceDocumentType) {
        this.sourceDocumentType = sourceDocumentType;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public Double getIamountFrom() {
        return iamountFrom;
    }

    public void setIamountFrom(Double iamountFrom) {
        this.iamountFrom = iamountFrom;
    }

    public Double getIamountTo() {
        return iamountTo;
    }

    public void setIamountTo(Double iamountTo) {
        this.iamountTo = iamountTo;
    }

    public Double getLnTotalAmount() {
        return lnTotalAmount;
    }

    public void setLnTotalAmount(Double lnTotalAmount) {
        this.lnTotalAmount = lnTotalAmount;
    }

    public String getContractNameOrNumber() {
        return contractNameOrNumber;
    }

    public void setContractNameOrNumber(String contractNameOrNumber) {
        this.contractNameOrNumber = contractNameOrNumber;
    }

    public Date getDueDateFrom() {
        return dueDateFrom;
    }

    public void setDueDateFrom(Date dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }

    public Date getDueDateTo() {
        return dueDateTo;
    }

    public void setDueDateTo(Date dueDateTo) {
        this.dueDateTo = dueDateTo;
    }

    public Long getTimesFrom() {
        return timesFrom;
    }

    public void setTimesFrom(Long timesFrom) {
        this.timesFrom = timesFrom;
    }

    public Long getTimesTo() {
        return timesTo;
    }

    public void setTimesTo(Long timesTo) {
        timesTo = timesTo;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDateFlag() {
        return dateFlag;
    }

    public void setDateFlag(String dateFlag) {
        this.dateFlag = dateFlag;
    }

    public String getReceiptNameOrNumber() {
        return receiptNameOrNumber;
    }

    public void setReceiptNameOrNumber(String receiptNameOrNumber) {
        this.receiptNameOrNumber = receiptNameOrNumber;
    }

    public Long getItemFlag() {
        return itemFlag;
    }

    public void setItemFlag(Long itemFlag) {
        this.itemFlag = itemFlag;
    }

    public Long[] getInvoiceKinds() {
        return invoiceKinds;
    }

    public void setInvoiceKinds(Long[] invoiceKinds) {
        this.invoiceKinds = invoiceKinds;
    }

    public String getInvoiceDateFlag() {
        return invoiceDateFlag;
    }

    public void setInvoiceDateFlag(String invoiceDateFlag) {
        this.invoiceDateFlag = invoiceDateFlag;
    }

    public Long getQuotationCashflowId() {
        return quotationCashflowId;
    }

    public void setQuotationCashflowId(Long quotationCashflowId) {
        this.quotationCashflowId = quotationCashflowId;
    }

    public String getTaxTypeCodeDescription() {
        return taxTypeCodeDescription;
    }

    public void setTaxTypeCodeDescription(String taxTypeCodeDescription) {
        this.taxTypeCodeDescription = taxTypeCodeDescription;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Transient
    private String invoiceDateFormat;

    @Transient
    private String specification;
    @Transient
    private String contractContentNumber;

    @Transient
    private String taxTypeNum;

    @Transient
    private String bpCode;

    @Transient
    private String none1;
    @Transient
    private String none2;
    @Transient
    private String none3;
    @Transient
    private String none4;
    @Transient
    private String none5;
    @Transient
    private String none6;
    @Transient
    private String none7;
    @Transient
    private String none8;
    @Transient
    private String none9;
    @Transient
    private String none10;
    @Transient
    private String none11;
    @Transient
    private String none12;
    @Transient
    private String none13;
    @Transient
    private String none14;
    @Transient
    private String none15;
    @Transient
    private String none16;
    @Transient
    private String none17;
    @Transient
    private String none18;
    @Transient
    private String none19;
    @Transient
    private String none20;
    @Transient
    private String none21;
    @Transient
    private String none22;
    @Transient
    private String none23;
    @Transient
    private String none24;
    @Transient
    private String none25;
    @Transient
    private String none26;
    @Transient
    private String none27;

    public String getInvoiceDateFormat() {
        return invoiceDateFormat;
    }

    public void setInvoiceDateFormat(String invoiceDateFormat) {
        this.invoiceDateFormat = invoiceDateFormat;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getTaxTypeNum() {
        return taxTypeNum;
    }

    public void setTaxTypeNum(String taxTypeNum) {
        this.taxTypeNum = taxTypeNum;
    }

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getNone1() {
        return none1;
    }

    public void setNone1(String none1) {
        this.none1 = none1;
    }

    public String getNone2() {
        return none2;
    }

    public void setNone2(String none2) {
        this.none2 = none2;
    }

    public String getNone3() {
        return none3;
    }

    public void setNone3(String none3) {
        this.none3 = none3;
    }

    public String getNone4() {
        return none4;
    }

    public void setNone4(String none4) {
        this.none4 = none4;
    }

    public String getNone5() {
        return none5;
    }

    public void setNone5(String none5) {
        this.none5 = none5;
    }

    public String getNone6() {
        return none6;
    }

    public void setNone6(String none6) {
        this.none6 = none6;
    }

    public String getNone7() {
        return none7;
    }

    public void setNone7(String none7) {
        this.none7 = none7;
    }

    public String getNone8() {
        return none8;
    }

    public void setNone8(String none8) {
        this.none8 = none8;
    }

    public String getNone9() {
        return none9;
    }

    public void setNone9(String none9) {
        this.none9 = none9;
    }

    public String getNone10() {
        return none10;
    }

    public void setNone10(String none10) {
        this.none10 = none10;
    }

    public String getNone11() {
        return none11;
    }

    public void setNone11(String none11) {
        this.none11 = none11;
    }

    public String getNone12() {
        return none12;
    }

    public void setNone12(String none12) {
        this.none12 = none12;
    }

    public String getNone13() {
        return none13;
    }

    public void setNone13(String none13) {
        this.none13 = none13;
    }

    public String getNone14() {
        return none14;
    }

    public void setNone14(String none14) {
        this.none14 = none14;
    }

    public String getNone15() {
        return none15;
    }

    public void setNone15(String none15) {
        this.none15 = none15;
    }

    public String getNone16() {
        return none16;
    }

    public void setNone16(String none16) {
        this.none16 = none16;
    }

    public String getNone17() {
        return none17;
    }

    public void setNone17(String none17) {
        this.none17 = none17;
    }

    public String getNone18() {
        return none18;
    }

    public void setNone18(String none18) {
        this.none18 = none18;
    }

    public String getNone19() {
        return none19;
    }

    public void setNone19(String none19) {
        this.none19 = none19;
    }

    public String getNone20() {
        return none20;
    }

    public void setNone20(String none20) {
        this.none20 = none20;
    }

    public String getNone21() {
        return none21;
    }

    public void setNone21(String none21) {
        this.none21 = none21;
    }

    public String getNone22() {
        return none22;
    }

    public void setNone22(String none22) {
        this.none22 = none22;
    }

    public String getNone23() {
        return none23;
    }

    public void setNone23(String none23) {
        this.none23 = none23;
    }

    public String getNone24() {
        return none24;
    }

    public void setNone24(String none24) {
        this.none24 = none24;
    }

    public String getNone25() {
        return none25;
    }

    public void setNone25(String none25) {
        this.none25 = none25;
    }

    public String getNone26() {
        return none26;
    }

    public void setNone26(String none26) {
        this.none26 = none26;
    }

    public String getNone27() {
        return none27;
    }

    public void setNone27(String none27) {
        this.none27 = none27;
    }

    public String getRedInvoiceCode() {
        return redInvoiceCode;
    }

    public void setRedInvoiceCode(String redInvoiceCode) {
        this.redInvoiceCode = redInvoiceCode;
    }

    public String getIsCancelFlag() {
        return isCancelFlag;
    }

    public void setIsCancelFlag(String isCancelFlag) {
        this.isCancelFlag = isCancelFlag;
    }

    public Long getReceiptLnId() {
        return receiptLnId;
    }

    public void setReceiptLnId(Long receiptLnId) {
        this.receiptLnId = receiptLnId;
    }

    public Date getTaxInvoiceDate() {
        return taxInvoiceDate;
    }

    public void setTaxInvoiceDate(Date taxInvoiceDate) {
        this.taxInvoiceDate = taxInvoiceDate;
    }

    public String[] getInvoiceHdIdArr() {
        return invoiceHdIdArr;
    }

    public void setInvoiceHdIdArr(String[] invoiceHdIdArr) {
        this.invoiceHdIdArr = invoiceHdIdArr;
    }

    public String getDocumentResource() {
        return documentResource;
    }

    public void setDocumentResource(String documentResource) {
        this.documentResource = documentResource;
    }

    public String getContractContentNumber() {
        return contractContentNumber;
    }

    public void setContractContentNumber(String contractContentNumber) {
        this.contractContentNumber = contractContentNumber;
    }

    public String getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public String getReceiptStatusDesc() {
        return receiptStatusDesc;
    }

    public void setReceiptStatusDesc(String receiptStatusDesc) {
        this.receiptStatusDesc = receiptStatusDesc;
    }

    @Transient
    private Long cashflowId;

    @Override
    public Long getCashflowId() {
        return cashflowId;
    }

    @Override
    public void setCashflowId(Long cashflowId) {
        this.cashflowId = cashflowId;
    }


    @Transient
    private String bpNameTenant;

    @Transient
    private String billingMethodN;

    @Transient
    private String bpIdN;
    @Transient
    private String taxpayerType;

    @Transient
    private String billingWay;

    @Transient
    private String billingWayN;

    @Transient
    private String taxpayerTypeN;

    private Date handoverDate;

    private String handoverPerson;

    @Transient
    private String handoverPersonN;

    private String handoverStatus;

    @Transient
    private String handoverStatusN;

    @Transient
    private String hostPartment;

    @Transient
    private String hostManager;

    private String reviewPerson;

    @Transient
    private String reviewPersonN;

    public String getWriteOffFlagN() {
        return writeOffFlagN;
    }

    public void setWriteOffFlagN(String writeOffFlagN) {
        this.writeOffFlagN = writeOffFlagN;
    }

    @Transient
    private Double totalAmountFrom;
    @Transient
    private Double totalAmountTo;
    @Transient
    private Long projectId;

    @Transient
    private String billingStatus;

    @Transient
    private String writeOffFlag;

    @Transient
    private String contractStatus;
}