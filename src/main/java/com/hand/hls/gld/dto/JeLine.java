package com.hand.hls.gld.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "gld_je_line")
@Getter
@Setter
public class JeLine extends BaseDTO {
    @Id
    @GeneratedValue
    private Long jeLineId; //凭证行ID

    private Long jeHeadId; //凭证头ID

    @NotEmpty
    private String documentCategory; //单据类别

    @NotEmpty
    private String documentType; //单据类型

    @NotEmpty
    private String businessType; //业务类型

    private Long companyId; //公司ID

    private String sourceType; //来源类型(syscode:GLD.JE_SOURCE_TYPE)(流水/手工)

    private Long sourceId; //来源ID(JE_TRX_DTL_ID/空)

    private Long jeTemplateId; //凭证模板ID

    private String jeTrx; //凭证事务

    private Long setOfBooksId; //帐套ID

    private Date jeDate; //记账日期

    private String periodName; //期间

    private Long accountId; //科目ID

    private Long costCenterId; //成本中心ID

    private String drCr; //借/贷（syscode：GLD.JE_DR_CR)

    private Double drAmount; //借方金额

    private Double crAmount; //贷方金额

    private Double drFunctionalAmount; //借方本币金额

    private Double crFunctionalAmount; //贷方本币金额

    private Double exchangeRate; //汇率

    private String currency;

    private String jeDescription; //行摘要

    @Column(name = "SEGMENT_1")
    private String segment1; //段1
    @Column(name = "SEGMENT_2")
    private String segment2; //段2
    @Column(name = "SEGMENT_3")
    private String segment3; //段3
    @Column(name = "SEGMENT_4")
    private String segment4; //段4
    @Column(name = "SEGMENT_5")
    private String segment5; //段5
    @Column(name = "SEGMENT_6")
    private String segment6; //段6
    @Column(name = "SEGMENT_7")
    private String segment7; //段7
    @Column(name = "SEGMENT_8")
    private String segment8; //段8
    @Column(name = "SEGMENT_9")
    private String segment9; //段9
    @Column(name = "SEGMENT_10")
    private String segment10; //段10

    private String jeStatus; //凭证状态

    private Long jeCreatedBy; //凭证制证人

    private Date jeCreationDate; //凭证制证时间

    private Long jeConfirmedBy; //凭证确认人

    private Date jeConfirmDate; //凭证确认时间

    private String glStatus; //总账状态

    private String glMsg; //总账消息

    private String reverseFlag;//反冲标志

    private Long reverseJeLineId;//反冲凭证行id

    @Transient
    private String companyFullName; //公司全称
    @Transient
    private String companyShortName; //公司名称
    @Transient
    private String jeTemplateName;//凭证模板名称
    @Transient
    private String jeTrxDescription;//凭证事务名称
    @Transient
    private String setOfBooksName;//账套名称
    @Transient
    private String accountCode;//科目代码
    @Transient
    private String accountName;//科目名称
    @Transient
    private String costCenterName;//成本中心名称
    @Transient
    private Date dateFrom;//日期从
    @Transient
    private Date dateTo;//日期到
    @Transient
    private List<String> paramJeTrx;//凭证事务list
    @Transient
    private List<String> paramStatus;//状态list
    @Transient
    private Double amount;//金额上限值
    @Transient
    private Double amountFrom;//金额从
    @Transient
    private Double amountTo;//金额到
    @Transient
    private String contractNumber;//合同编号
    @Transient
    private String contractName;//合同名称
    @Transient
    private String jeStatusDescription;//凭证状态名称
    @Transient
    private String documentCategoryDescription;//单据类别名称

    @Transient
    private String documentTypeDescription;//单据类型名称
    @Transient
    private String businessTypeDescription;//业务类型名称
    @Transient
    private String currencyName;//币种名称
    @Transient
    private Double amountValue;//金额值
    @Transient
    private String contractNumberFrom;//合同编号从
    @Transient
    private String contractNumberTo;//合同编号到
    @Transient
    private String periodFrom;//期间从
    @Transient
    private String periodTo;//期间到

    @Transient
    private String jeCreatedByDesc;
    @Transient
    private String jeConfirmedByDesc;

    @Transient
    private List<String> paramJeCreatedBy;

    @Transient
    private String bankInfo;
    @Transient
    private String bpName;
    @Transient
    private String employeeName;
    @Transient
    private String unitName;
    @Transient
    private String bankName;

    @Transient
    private List jeLineIdList;

    @Transient
    private String jeLineIdStr;
    @Transient
    private String accountIdN;
    @Transient
    private String drTotal;
    @Transient
    private String crTotal;
    @Transient
    private String drFunctionalAmountTotal;
    @Transient
    private String crFunctionalAmountTotal;

    @Transient
    private String cashflowItem;

    public String getJeLineIdStr() {
        return jeLineIdStr;
    }

    public void setJeLineIdStr(String jeLineIdStr) {
        this.jeLineIdStr = jeLineIdStr;
    }

    public List getJeLineIdList() {
        return jeLineIdList;
    }

    public void setJeLineIdList(List jeLineIdList) {
        this.jeLineIdList = jeLineIdList;
    }

    public List<String> getParamJeCreatedBy() {
        return paramJeCreatedBy;
    }

    public void setParamJeCreatedBy(List<String> paramJeCreatedBy) {
        this.paramJeCreatedBy = paramJeCreatedBy;
    }

    public String getJeCreatedByDesc() {
        return jeCreatedByDesc;
    }

    public void setJeCreatedByDesc(String jeCreatedByDesc) {
        this.jeCreatedByDesc = jeCreatedByDesc;
    }

    public String getJeConfirmedByDesc() {
        return jeConfirmedByDesc;
    }

    public void setJeConfirmedByDesc(String jeConfirmedByDesc) {
        this.jeConfirmedByDesc = jeConfirmedByDesc;
    }

    public String getReverseFlag() {
        return reverseFlag;
    }

    public void setReverseFlag(String reverseFlag) {
        this.reverseFlag = reverseFlag;
    }

    public Long getReverseJeLineId() {
        return reverseJeLineId;
    }

    public void setReverseJeLineId(Long reverseJeLineId) {
        this.reverseJeLineId = reverseJeLineId;
    }

    public Long getJeHeadId() {
        return jeHeadId;
    }

    public void setJeHeadId(Long jeHeadId) {
        this.jeHeadId = jeHeadId;
    }

    public Double getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(Double amountValue) {
        this.amountValue = amountValue;
    }

    public String getContractNumberFrom() {
        return contractNumberFrom;
    }

    public void setContractNumberFrom(String contractNumberFrom) {
        this.contractNumberFrom = contractNumberFrom;
    }

    public String getContractNumberTo() {
        return contractNumberTo;
    }

    public void setContractNumberTo(String contractNumberTo) {
        this.contractNumberTo = contractNumberTo;
    }

    public String getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
    }

    public String getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(String periodTo) {
        this.periodTo = periodTo;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getDocumentCategoryDescription() {
        return documentCategoryDescription;
    }

    public void setDocumentCategoryDescription(String documentCategoryDescription) {
        this.documentCategoryDescription = documentCategoryDescription;
    }

    public String getDocumentTypeDescription() {
        return documentTypeDescription;
    }

    public void setDocumentTypeDescription(String documentTypeDescription) {
        this.documentTypeDescription = documentTypeDescription;
    }

    public String getBusinessTypeDescription() {
        return businessTypeDescription;
    }

    public void setBusinessTypeDescription(String businessTypeDescription) {
        this.businessTypeDescription = businessTypeDescription;
    }

    public String getJeStatusDescription() {
        return jeStatusDescription;
    }

    public void setJeStatusDescription(String jeStatusDescription) {
        this.jeStatusDescription = jeStatusDescription;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getCompanyFullName() {
        return companyFullName;
    }

    public void setCompanyFullName(String companyFullName) {
        this.companyFullName = companyFullName;
    }

    public Double getAmountFrom() {
        return amountFrom;
    }

    public void setAmountFrom(Double amountFrom) {
        this.amountFrom = amountFrom;
    }

    public Double getAmountTo() {
        return amountTo;
    }

    public void setAmountTo(Double amountTo) {
        this.amountTo = amountTo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCompanyShortName() {
        return companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = companyShortName;
    }

    public String getJeTemplateName() {
        return jeTemplateName;
    }

    public void setJeTemplateName(String jeTemplateName) {
        this.jeTemplateName = jeTemplateName;
    }

    public String getJeTrxDescription() {
        return jeTrxDescription;
    }

    public void setJeTrxDescription(String jeTrxDescription) {
        this.jeTrxDescription = jeTrxDescription;
    }

    public String getSetOfBooksName() {
        return setOfBooksName;
    }

    public void setSetOfBooksName(String setOfBooksName) {
        this.setOfBooksName = setOfBooksName;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public List<String> getParamJeTrx() {
        return paramJeTrx;
    }

    public void setParamJeTrx(List<String> paramJeTrx) {
        this.paramJeTrx = paramJeTrx;
    }

    public List<String> getParamStatus() {
        return paramStatus;
    }

    public void setParamStatus(List<String> paramStatus) {
        this.paramStatus = paramStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setJeLineId(Long jeLineId) {
        this.jeLineId = jeLineId;
    }

    public Long getJeLineId() {
        return jeLineId;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setJeTemplateId(Long jeTemplateId) {
        this.jeTemplateId = jeTemplateId;
    }

    public Long getJeTemplateId() {
        return jeTemplateId;
    }

    public void setJeTrx(String jeTrx) {
        this.jeTrx = jeTrx;
    }

    public String getJeTrx() {
        return jeTrx;
    }

    public void setSetOfBooksId(Long setOfBooksId) {
        this.setOfBooksId = setOfBooksId;
    }

    public Long getSetOfBooksId() {
        return setOfBooksId;
    }

    public void setJeDate(Date jeDate) {
        this.jeDate = jeDate;
    }

    public Date getJeDate() {
        return jeDate;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setCostCenterId(Long costCenterId) {
        this.costCenterId = costCenterId;
    }

    public Long getCostCenterId() {
        return costCenterId;
    }

    public void setDrCr(String drCr) {
        this.drCr = drCr;
    }

    public String getDrCr() {
        return drCr;
    }

    public void setDrAmount(Double drAmount) {
        this.drAmount = drAmount;
    }

    public Double getDrAmount() {
        return drAmount;
    }

    public void setCrAmount(Double crAmount) {
        this.crAmount = crAmount;
    }

    public Double getCrAmount() {
        return crAmount;
    }

    public void setDrFunctionalAmount(Double drFunctionalAmount) {
        this.drFunctionalAmount = drFunctionalAmount;
    }

    public Double getDrFunctionalAmount() {
        return drFunctionalAmount;
    }

    public void setCrFunctionalAmount(Double crFunctionalAmount) {
        this.crFunctionalAmount = crFunctionalAmount;
    }

    public Double getCrFunctionalAmount() {
        return crFunctionalAmount;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setJeDescription(String jeDescription) {
        this.jeDescription = jeDescription;
    }

    public String getJeDescription() {
        return jeDescription;
    }

    public void setSegment1(String segment1) {
        this.segment1 = segment1;
    }

    public String getSegment1() {
        return segment1;
    }

    public void setSegment2(String segment2) {
        this.segment2 = segment2;
    }

    public String getSegment2() {
        return segment2;
    }

    public void setSegment3(String segment3) {
        this.segment3 = segment3;
    }

    public String getSegment3() {
        return segment3;
    }

    public void setSegment4(String segment4) {
        this.segment4 = segment4;
    }

    public String getSegment4() {
        return segment4;
    }

    public void setSegment5(String segment5) {
        this.segment5 = segment5;
    }

    public String getSegment5() {
        return segment5;
    }

    public void setSegment6(String segment6) {
        this.segment6 = segment6;
    }

    public String getSegment6() {
        return segment6;
    }

    public void setSegment7(String segment7) {
        this.segment7 = segment7;
    }

    public String getSegment7() {
        return segment7;
    }

    public void setSegment8(String segment8) {
        this.segment8 = segment8;
    }

    public String getSegment8() {
        return segment8;
    }

    public void setSegment9(String segment9) {
        this.segment9 = segment9;
    }

    public String getSegment9() {
        return segment9;
    }

    public void setSegment10(String segment10) {
        this.segment10 = segment10;
    }

    public String getSegment10() {
        return segment10;
    }

    public void setJeStatus(String jeStatus) {
        this.jeStatus = jeStatus;
    }

    public String getJeStatus() {
        return jeStatus;
    }

    public void setJeCreatedBy(Long jeCreatedBy) {
        this.jeCreatedBy = jeCreatedBy;
    }

    public Long getJeCreatedBy() {
        return jeCreatedBy;
    }

    public void setJeCreationDate(Date jeCreationDate) {
        this.jeCreationDate = jeCreationDate;
    }

    public Date getJeCreationDate() {
        return jeCreationDate;
    }

    public void setJeConfirmedBy(Long jeConfirmedBy) {
        this.jeConfirmedBy = jeConfirmedBy;
    }

    public Long getJeConfirmedBy() {
        return jeConfirmedBy;
    }

    public void setJeConfirmDate(Date jeConfirmDate) {
        this.jeConfirmDate = jeConfirmDate;
    }

    public Date getJeConfirmDate() {
        return jeConfirmDate;
    }

    public void setGlStatus(String glStatus) {
        this.glStatus = glStatus;
    }

    public String getGlStatus() {
        return glStatus;
    }

    public void setGlMsg(String glMsg) {
        this.glMsg = glMsg;
    }

    public String getGlMsg() {
        return glMsg;
    }

    public String getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(String bankInfo) {
        this.bankInfo = bankInfo;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountIdN() {
        return accountIdN;
    }

    public void setAccountIdN(String accountIdN) {
        this.accountIdN = accountIdN;
    }

    public String getDrTotal() {
        return drTotal;
    }

    public void setDrTotal(String drTotal) {
        this.drTotal = drTotal;
    }

    public String getCrTotal() {
        return crTotal;
    }

    public void setCrTotal(String crTotal) {
        this.crTotal = crTotal;
    }

    public String getDrFunctionalAmountTotal() {
        return drFunctionalAmountTotal;
    }

    public void setDrFunctionalAmountTotal(String drFunctionalAmountTotal) {
        this.drFunctionalAmountTotal = drFunctionalAmountTotal;
    }

    public String getCrFunctionalAmountTotal() {
        return crFunctionalAmountTotal;
    }

    public void setCrFunctionalAmountTotal(String crFunctionalAmountTotal) {
        this.crFunctionalAmountTotal = crFunctionalAmountTotal;
    }
}
