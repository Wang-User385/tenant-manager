package com.hand.hls.vat.dto;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "acp_invoice_hd"
)
@Getter
@Setter
public class AcpInvoiceHd extends BaseDTO {
    @Id
    @GeneratedValue
    private Long invoiceHdId;
    private Long companyId;
    private String documentNumber;
    private String documentType;
    private String documentCategory;
    private String businessType;
    private String billingMethod;
    private String invoiceKind;
    private Long bpId;
    private String bpName;
    private String bpTaxRegistryNum;
    private String bpAddressPhoneNum;
    private String bpBankAccount;
    private String description;
    private double totalAmount;
    private double netAmount;
    private double taxAmount;
    private String currency;
    private Date invoiceDate;
    private String invoiceNumber;
    private String invoiceStatus;
    private String reversedFlag;
    private Date reverseDate;
    private Long sourceInvoiceHeaderId;
    private Date confirmedDate;
    private Long confirmedBy;
    private Date postedDate;
    private Long postedBy;
    @Transient
    private Long contractId;
    @Transient
    private Long cashflowId;
    @Transient
    private double writeOffDueAmount;
    @Transient
    private double writeOffInterest;
    @Transient
    private double writeOffPrincipal;
    @Transient
    private double totalInterest;
    @Transient
    private String cashflowIdStr;
    @Transient
    private Long ranks;
    @Transient
    private double specialNum;
    @Transient
    private double normalNum;
    @Transient
    private String companyFullName;
    @Transient
    private String contractName;
    @Transient
    private String contractNumber;
    @Transient
    private String invoiceKindDesc;
    @Transient
    private String cfItemDesc;
    @Transient
    private Long cfItem;
    @Transient
    @Children
    List<HlsCusAcrInvoiceLn> acrInvoiceLnList;
    @Transient
    private Double diffAmount;
    @Transient
    private Long times;
    @Transient
    private Double billingAmount;
    @Transient
    private String diff;
    @Transient
    private String flag;
    @Transient
    private Long count;
    @Transient
    private String cashflowItems;
    @Transient
    private Double amountFrom;
    @Transient
    private Double amountTo;
    @Transient
    private String contractInfo;
    @Transient
    private Long timesFrom;
    @Transient
    private Long timesTo;
    @Transient
    private int groupByFlag;
    @Transient
    private List<Long> queryList;
    @Transient
    private double totalPrincipal;

    public AcpInvoiceHd() {
    }

    public double getSpecialNum() {
        return this.specialNum;
    }

    public void setSpecialNum(double specialNum) {
        this.specialNum = specialNum;
    }

    public double getNormalNum() {
        return this.normalNum;
    }

    public void setNormalNum(double normalNum) {
        this.normalNum = normalNum;
    }

    public List<HlsCusAcrInvoiceLn> getAcrInvoiceLnList() {
        return this.acrInvoiceLnList;
    }

    public String getCompanyFullName() {
        return this.companyFullName;
    }

    public void setAcrInvoiceLnList(List<HlsCusAcrInvoiceLn> acrInvoiceLnList) {
        this.acrInvoiceLnList = acrInvoiceLnList;
    }

    public String getInvoiceKindDesc() {
        return this.invoiceKindDesc;
    }

    public void setInvoiceKindDesc(String invoiceKindDesc) {
        this.invoiceKindDesc = invoiceKindDesc;
    }

    public String getCfItemDesc() {
        return this.cfItemDesc;
    }

    public void setCfItemDesc(String cfItemDesc) {
        this.cfItemDesc = cfItemDesc;
    }

    public Long getCfItem() {
        return this.cfItem;
    }

    public void setCfItem(Long cfItem) {
        this.cfItem = cfItem;
    }

    public Long getRanks() {
        return this.ranks;
    }

    public void setRanks(Long ranks) {
        this.ranks = ranks;
    }

    public void setCompanyFullName(String companyFullName) {
        this.companyFullName = companyFullName;
    }

    public String getContractName() {
        return this.contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractNumber() {
        return this.contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getFlag() {
        return this.flag;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getCashflowItems() {
        return this.cashflowItems;
    }

    public void setCashflowItems(String cashflowItems) {
        this.cashflowItems = cashflowItems;
    }

    public Double getAmountFrom() {
        return this.amountFrom;
    }

    public void setAmountFrom(Double amountFrom) {
        this.amountFrom = amountFrom;
    }

    public Double getAmountTo() {
        return this.amountTo;
    }

    public void setAmountTo(Double amountTo) {
        this.amountTo = amountTo;
    }

    public String getContractInfo() {
        return this.contractInfo;
    }

    public void setContractInfo(String contractInfo) {
        this.contractInfo = contractInfo;
    }

    public Long getTimesFrom() {
        return this.timesFrom;
    }

    public void setTimesFrom(Long timesFrom) {
        this.timesFrom = timesFrom;
    }

    public Long getTimesTo() {
        return this.timesTo;
    }

    public void setTimesTo(Long timesTo) {
        this.timesTo = timesTo;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDiff() {
        return this.diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public Double getDiffAmount() {
        return this.diffAmount;
    }

    public void setDiffAmount(Double diffAmount) {
        this.diffAmount = diffAmount;
    }

    public Long getTimes() {
        return this.times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public Double getBillingAmount() {
        return this.billingAmount;
    }

    public void setBillingAmount(Double billingAmount) {
        this.billingAmount = billingAmount;
    }

    public String getCashflowIdStr() {
        return this.cashflowIdStr;
    }

    public void setCashflowIdStr(String cashflowIdStr) {
        this.cashflowIdStr = cashflowIdStr;
    }

    public int getGroupByFlag() {
        return this.groupByFlag;
    }

    public void setGroupByFlag(int groupByFlag) {
        this.groupByFlag = groupByFlag;
    }

    public List<Long> getQueryList() {
        return this.queryList;
    }

    public void setQueryList(List<Long> queryList) {
        this.queryList = queryList;
    }

    public Long getContractId() {
        return this.contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getCashflowId() {
        return this.cashflowId;
    }

    public void setCashflowId(Long cashflowId) {
        this.cashflowId = cashflowId;
    }

    public double getWriteOffDueAmount() {
        return this.writeOffDueAmount;
    }

    public void setWriteOffDueAmount(double writeOffDueAmount) {
        this.writeOffDueAmount = writeOffDueAmount;
    }

    public double getWriteOffInterest() {
        return this.writeOffInterest;
    }

    public void setWriteOffInterest(double writeOffInterest) {
        this.writeOffInterest = writeOffInterest;
    }

    public double getWriteOffPrincipal() {
        return this.writeOffPrincipal;
    }

    public void setWriteOffPrincipal(double writeOffPrincipal) {
        this.writeOffPrincipal = writeOffPrincipal;
    }

    public double getTotalInterest() {
        return this.totalInterest;
    }

    public void setTotalInterest(double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public double getTotalPrincipal() {
        return this.totalPrincipal;
    }

    public void setTotalPrincipal(double totalPrincipal) {
        this.totalPrincipal = totalPrincipal;
    }

    public Long getInvoiceHdId() {
        return this.invoiceHdId;
    }

    public void setInvoiceHdId(Long invoiceHdId) {
        this.invoiceHdId = invoiceHdId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber == null ? null : documentNumber.trim();
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType == null ? null : documentType.trim();
    }

    public String getDocumentCategory() {
        return this.documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory == null ? null : documentCategory.trim();
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType == null ? null : businessType.trim();
    }

    public String getBillingMethod() {
        return this.billingMethod;
    }

    public void setBillingMethod(String billingMethod) {
        this.billingMethod = billingMethod == null ? null : billingMethod.trim();
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

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName == null ? null : bpName.trim();
    }

    public String getBpTaxRegistryNum() {
        return this.bpTaxRegistryNum;
    }

    public void setBpTaxRegistryNum(String bpTaxRegistryNum) {
        this.bpTaxRegistryNum = bpTaxRegistryNum == null ? null : bpTaxRegistryNum.trim();
    }

    public String getBpAddressPhoneNum() {
        return this.bpAddressPhoneNum;
    }

    public void setBpAddressPhoneNum(String bpAddressPhoneNum) {
        this.bpAddressPhoneNum = bpAddressPhoneNum == null ? null : bpAddressPhoneNum.trim();
    }

    public String getBpBankAccount() {
        return this.bpBankAccount;
    }

    public void setBpBankAccount(String bpBankAccount) {
        this.bpBankAccount = bpBankAccount == null ? null : bpBankAccount.trim();
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getNetAmount() {
        return this.netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public double getTaxAmount() {
        return this.taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public Date getInvoiceDate() {
        return this.invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber == null ? null : invoiceNumber.trim();
    }

    public String getInvoiceStatus() {
        return this.invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus == null ? null : invoiceStatus.trim();
    }

    public String getReversedFlag() {
        return this.reversedFlag;
    }

    public void setReversedFlag(String reversedFlag) {
        this.reversedFlag = reversedFlag == null ? null : reversedFlag.trim();
    }

    public Date getReverseDate() {
        return this.reverseDate;
    }

    public void setReverseDate(Date reverseDate) {
        this.reverseDate = reverseDate;
    }

    public Long getSourceInvoiceHeaderId() {
        return this.sourceInvoiceHeaderId;
    }

    public void setSourceInvoiceHeaderId(Long sourceInvoiceHeaderId) {
        this.sourceInvoiceHeaderId = sourceInvoiceHeaderId;
    }

    public Long getConfirmedBy() {
        return this.confirmedBy;
    }

    public void setConfirmedBy(Long confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public Date getConfirmedDate() {
        return this.confirmedDate;
    }

    public void setConfirmedDate(Date confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public Date getPostedDate() {
        return this.postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Long getPostedBy() {
        return this.postedBy;
    }

    public void setPostedBy(Long postedBy) {
        this.postedBy = postedBy;
    }
}

