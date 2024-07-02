package com.hand.hls.fin.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.Length;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "lon_credit_contract"
)
public class CreditContract extends BaseDTO {
    public static final String FIELD_CREDIT_CONTRACT_ID = "creditContractId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_DOCUMENT_TYPE = "documentType";
    public static final String FIELD_DOCUMENT_CATEGORY = "documentCategory";
    public static final String FIELD_BUSINESS_TYPE = "businessType";
    public static final String FIELD_CREDIT_CONTRACT_NUMBER = "creditContractNumber";
    public static final String FIELD_CREDIT_CONTRACT_NAME = "creditContractName";
    public static final String FIELD_CREDIT_CONTRACT_STATUS = "creditContractStatus";
    public static final String FIELD_CREDIT_LINE_ID = "creditLineId";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_CREDIT_BP_ID = "creditBpId";
    public static final String FIELD_DESCRIPTION = "description";
    @Id
    @GeneratedValue
    private Long creditContractId;
    private Long companyId;
    @Length(
            max = 100
    )
    private String documentType;
    @Length(
            max = 100
    )
    private String documentCategory;
    @Length(
            max = 100
    )
    private String businessType;
    @Length(
            max = 255
    )
    private String creditContractNumber;
    @Length(
            max = 500
    )
    private String creditContractName;
    @Length(
            max = 100
    )
    private String creditContractStatus;
    private Long creditLineId;
    @Length(
            max = 100
    )
    private String currency;
    private Long creditBpId;
    @Length(
            max = 500
    )
    private String description;
    @Transient
    private String bpName;
    @Transient
    private String creditType;
    @Transient
    private String creditCate;
    @Transient
    private String documentType2;
    @Transient
    private Double creditLineAmt;
    @Transient
    private Double creditExposureAmt;
    @Transient
    private Date validFrom;
    @Transient
    private Date validTo;
    @Transient
    private String bpCategory;
    @Transient
    private Double openToBuy;
    @Transient
    private String bpCode;
    @Transient
    private String bpClass;
    @Transient
    private String creditDescription;
    @Transient
    private String[] lonTypeStatus;
    @Transient
    private String[] creditTypeStatus;
    @Transient
    private Double[] creditLineAmtStatus;
    @Transient
    private String lonTypes;
    @Transient
    private String creditTypes;
    @Transient
    private String creditLineAmts;
    @Transient
    private Double maxAmt;
    @Transient
    private Double minAmt;
    @Transient
    private Long proposerUserId;
    @Transient
    private Long proposerEmployeeAssignId;
    @Transient
    private Long proposerEmployeeId;
    @Transient
    private String contractName;
    @Transient
    private Date dueDate;
    @Transient
    private String cfDirection;
    @Transient
    private Long withdrawId;
    @Transient
    private Double writeOffAmount;
    @Transient
    private Long month;
    @Transient
    private Long year;
    @Transient
    private String orgType;
    @Transient
    private Double usedAmt;
    @Transient
    private Double noUsedAmt;
    @Transient
    private Long bpId;
    @Transient
    private String creditLineName;
    @Transient
    private Double withdrawAmt;
    @Transient
    private Double repaymentAmt;
    @Transient
    private String currencyName;
    @Transient
    private int revolving;
    @Transient
    private int nonRevolving;
    @Transient
    private int once;
    @Transient
    private Double amMax;
    @Transient
    private String bpQueryName;
    @Transient
    private String contractQueryName;
    @Transient
    private String lonDes;
    @Transient
    private String creditDes;
    @Transient
    private Double leftAmount;
    @Transient
    private List<HlsCusCreditLineAttachment> creditLineAttachments;

    public CreditContract() {
    }

    public Double getLeftAmount() {
        return this.leftAmount;
    }

    public void setLeftAmount(Double leftAmount) {
        this.leftAmount = leftAmount;
    }

    public String getLonDes() {
        return this.lonDes;
    }

    public void setLonDes(String lonDes) {
        this.lonDes = lonDes;
    }

    public String getCreditDes() {
        return this.creditDes;
    }

    public void setCreditDes(String creditDes) {
        this.creditDes = creditDes;
    }

    public String getBpQueryName() {
        return this.bpQueryName;
    }

    public void setBpQueryName(String bpQueryName) {
        this.bpQueryName = bpQueryName;
    }

    public String getContractQueryName() {
        return this.contractQueryName;
    }

    public void setContractQueryName(String contractQueryName) {
        this.contractQueryName = contractQueryName;
    }

    public Double getAmMax() {
        return this.amMax;
    }

    public void setAmMax(Double amMax) {
        this.amMax = amMax;
    }

    public Double[] getCreditLineAmtStatus() {
        return this.creditLineAmtStatus;
    }

    public void setCreditLineAmtStatus(Double[] creditLineAmtStatus) {
        this.creditLineAmtStatus = creditLineAmtStatus;
    }

    public String getLonTypes() {
        return this.lonTypes;
    }

    public void setLonTypes(String lonTypes) {
        this.lonTypes = lonTypes;
    }

    public String getCreditTypes() {
        return this.creditTypes;
    }

    public void setCreditTypes(String creditTypes) {
        this.creditTypes = creditTypes;
    }

    public String getCreditLineAmts() {
        return this.creditLineAmts;
    }

    public void setCreditLineAmts(String creditLineAmts) {
        this.creditLineAmts = creditLineAmts;
    }

    public String[] getLonTypeStatus() {
        return this.lonTypeStatus;
    }

    public void setLonTypeStatus(String[] lonTypeStatus) {
        this.lonTypeStatus = lonTypeStatus;
    }

    public String[] getCreditTypeStatus() {
        return this.creditTypeStatus;
    }

    public void setCreditTypeStatus(String[] creditTypeStatus) {
        this.creditTypeStatus = creditTypeStatus;
    }

    public String getCreditDescription() {
        return this.creditDescription;
    }

    public void setCreditDescription(String creditDescription) {
        this.creditDescription = creditDescription;
    }

    public int getRevolving() {
        return this.revolving;
    }

    public void setRevolving(int revolving) {
        this.revolving = revolving;
    }

    public int getNonRevolving() {
        return this.nonRevolving;
    }

    public void setNonRevolving(int nonRevolving) {
        this.nonRevolving = nonRevolving;
    }

    public int getOnce() {
        return this.once;
    }

    public void setOnce(int once) {
        this.once = once;
    }

    public String getCurrencyName() {
        return this.currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Double getWithdrawAmt() {
        return this.withdrawAmt;
    }

    public void setWithdrawAmt(Double withdrawAmt) {
        this.withdrawAmt = withdrawAmt;
    }

    public Double getRepaymentAmt() {
        return this.repaymentAmt;
    }

    public void setRepaymentAmt(Double repaymentAmt) {
        this.repaymentAmt = repaymentAmt;
    }

    public String getCreditLineName() {
        return this.creditLineName;
    }

    public void setCreditLineName(String creditLineName) {
        this.creditLineName = creditLineName;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Double getUsedAmt() {
        return this.usedAmt;
    }

    public void setUsedAmt(Double usedAmt) {
        this.usedAmt = usedAmt;
    }

    public Double getNoUsedAmt() {
        return this.noUsedAmt;
    }

    public void setNoUsedAmt(Double noUsedAmt) {
        this.noUsedAmt = noUsedAmt;
    }

    public String getOrgType() {
        return this.orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Long getMonth() {
        return this.month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getYear() {
        return this.year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getContractName() {
        return this.contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCfDirection() {
        return this.cfDirection;
    }

    public void setCfDirection(String cfDirection) {
        this.cfDirection = cfDirection;
    }

    public Long getWithdrawId() {
        return this.withdrawId;
    }

    public void setWithdrawId(Long withdrawId) {
        this.withdrawId = withdrawId;
    }

    public Double getWriteOffAmount() {
        return this.writeOffAmount;
    }

    public void setWriteOffAmount(Double writeOffAmount) {
        this.writeOffAmount = writeOffAmount;
    }

    public static String getFieldDocumentCategory() {
        return "documentCategory";
    }

    public Long getProposerUserId() {
        return this.proposerUserId;
    }

    public void setProposerUserId(Long proposerUserId) {
        this.proposerUserId = proposerUserId;
    }

    public Long getProposerEmployeeAssignId() {
        return this.proposerEmployeeAssignId;
    }

    public void setProposerEmployeeAssignId(Long proposerEmployeeAssignId) {
        this.proposerEmployeeAssignId = proposerEmployeeAssignId;
    }

    public Long getProposerEmployeeId() {
        return this.proposerEmployeeId;
    }

    public void setProposerEmployeeId(Long proposerEmployeeId) {
        this.proposerEmployeeId = proposerEmployeeId;
    }

    public static String getFieldDocumentType() {
        return "documentType";
    }

    public Double getMaxAmt() {
        return this.maxAmt;
    }

    public void setMaxAmt(Double maxAmt) {
        this.maxAmt = maxAmt;
    }

    public Double getMinAmt() {
        return this.minAmt;
    }

    public void setMinAmt(Double minAmt) {
        this.minAmt = minAmt;
    }

    public String getBpCode() {
        return this.bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpClass() {
        return this.bpClass;
    }

    public void setBpClass(String bpClass) {
        this.bpClass = bpClass;
    }

    public static String getFieldCompanyId() {
        return "companyId";
    }

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getCreditType() {
        return this.creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getCreditCate() {
        return this.creditCate;
    }

    public void setCreditCate(String creditCate) {
        this.creditCate = creditCate;
    }

    public Double getOpenToBuy() {
        return this.openToBuy;
    }

    public void setOpenToBuy(Double openToBuy) {
        this.openToBuy = openToBuy;
    }

    public String getBpCategory() {
        return this.bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public static String getFieldCreditContractId() {
        return "creditContractId";
    }

    public List<HlsCusCreditLineAttachment> getCreditLineAttachments() {
        return this.creditLineAttachments;
    }

    public String getDocumentType2() {
        return this.documentType2;
    }

    public void setDocumentType2(String documentType2) {
        this.documentType2 = documentType2;
    }

    public Double getCreditLineAmt() {
        return this.creditLineAmt;
    }

    public void setCreditLineAmt(Double creditLineAmt) {
        this.creditLineAmt = creditLineAmt;
    }

    public Double getCreditExposureAmt() {
        return this.creditExposureAmt;
    }

    public void setCreditExposureAmt(Double creditExposureAmt) {
        this.creditExposureAmt = creditExposureAmt;
    }

    public Date getValidFrom() {
        return this.validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return this.validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public void setCreditContractId(Long creditContractId) {
        this.creditContractId = creditContractId;
    }

    public Long getCreditContractId() {
        return this.creditContractId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getDocumentCategory() {
        return this.documentCategory;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setCreditContractNumber(String creditContractNumber) {
        this.creditContractNumber = creditContractNumber;
    }

    public String getCreditContractNumber() {
        return this.creditContractNumber;
    }

    public void setCreditContractName(String creditContractName) {
        this.creditContractName = creditContractName;
    }

    public String getCreditContractName() {
        return this.creditContractName;
    }

    public void setCreditContractStatus(String creditContractStatus) {
        this.creditContractStatus = creditContractStatus;
    }

    public String getCreditContractStatus() {
        return this.creditContractStatus;
    }

    public void setCreditLineId(Long creditLineId) {
        this.creditLineId = creditLineId;
    }

    public Long getCreditLineId() {
        return this.creditLineId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCreditBpId(Long creditBpId) {
        this.creditBpId = creditBpId;
    }

    public Long getCreditBpId() {
        return this.creditBpId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
