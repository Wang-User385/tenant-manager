package com.hand.hls.gld.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.persistence.Transient;

import java.util.Date;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

/**
 * @author wujun
 * @version 1.0
 * @date 2020/2/7 18:54
 * @description copy from gd
 */
@ExtensionAttribute(disable=true)
@Table(name = "ct_document_fin_income")
public class HlsCusCtDocumentFinIncome extends BaseDTO {
    public static final String FIELD_FIN_INCOME_ID = "finIncomeId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_SOURCE_TYPE = "sourceType";
    public static final String FIELD_SOURCE_ID = "sourceId";
    public static final String FIELD_PERIOD_NAME = "periodName";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_DAYS = "days";
    public static final String FIELD_FINANCE_INCOME = "financeIncome";
    public static final String FIELD_CF_ITEM = "cfItem";
    public static final String FIELD_FINANCE_INCOME_INCLUD = "financeIncomeInclud";
    public static final String FIELD_FINANCE_INCOME_VAT = "financeIncomeVat";


    @Id
    @GeneratedValue
    private Long finIncomeId; //PK

    @NotNull
    private Long companyId; //公司ID

    @Length(max = 30)
    private String sourceType; //来源单据类型(CON_CONTRACT租赁合同/FCT_CONTRACT保理合同/LON_CONTRACT_WITHDRAW融资提款)

    private Long sourceId; //来源单据ID

    @Length(max = 30)
    private String periodName; //期间

    private Date startDate; //计提开始日

    private Date endDate; //计提结束鈤

    private Long days; //计提天数

    private Double financeIncome; //计提金额

    private Double financeIncomeVat;//当月计提税额

    private Double financeIncomeInclud;//当月含税计提总额

    private Long cfItem;// 现金流项目

    @Transient
    private Long financeIncomeId;
    @Transient
    private Long contractId;
    @Transient
    private Long gldCashflowId;
    @Transient
    private Long quotationId;
    @Transient
    private String postFlag;
    @Transient
    private Double vatIncome;
    @Transient
    private String periodNameTo;
    @Transient
    private String periodNameFrom;
    @Transient
    private String contractName;
    @Transient
    private String contractNumber;
    @Transient
    private String bpName;
    @Transient
    private String startEndDate;
    @Transient
    private String taxTypeRate;
    @Transient
    private String cfItemDesc;
    @Transient
    private String postFlagDesc;
    @Transient
    private Long rowNum;
    @Transient
    private String refV01;
    @Transient
    private String refV02;
    @Transient
    private String refV03;
    @Transient
    private String refV04;
    @Transient
    private String refV05;
    @Transient
    private String refV06;
    @Transient
    private String refV07;
    @Transient
    private String refV08;
    @Transient
    private String refV09;
    @Transient
    private String refV10;
    @Transient
    private String refV11;
    @Transient
    private String refV12;
    @Transient
    private String refV13;
    @Transient
    private String refV14;
    @Transient
    private String refV15;
    @Transient
    private Double refN01;
    @Transient
    private Double refN02;
    @Transient
    private Double refN03;
    @Transient
    private Double refN04;
    @Transient
    private Double refN05;
    @Transient
    private Double refN06;
    @Transient
    private Double refN07;
    @Transient
    private Double refN08;
    @Transient
    private Double refN09;
    @Transient
    private Double refN10;
    @Transient
    private Date refD01;
    @Transient
    private Date refD02;
    @Transient
    private Date refD03;
    @Transient
    private Date refD04;
    @Transient
    private Date refD05;
    @Transient
    private Date refD06;
    @Transient
    private Date refD07;
    @Transient
    private Date refD08;
    @Transient
    private Date refD09;
    @Transient
    private Date refD10;

    public Long getFinanceIncomeId() {
        return financeIncomeId;
    }

    public void setFinanceIncomeId(Long financeIncomeId) {
        this.financeIncomeId = financeIncomeId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getGldCashflowId() {
        return gldCashflowId;
    }

    public void setGldCashflowId(Long gldCashflowId) {
        this.gldCashflowId = gldCashflowId;
    }

    public Long getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(Long quotationId) {
        this.quotationId = quotationId;
    }

    public String getPostFlag() {
        return postFlag;
    }

    public void setPostFlag(String postFlag) {
        this.postFlag = postFlag;
    }

    public Double getVatIncome() {
        return vatIncome;
    }

    public void setVatIncome(Double vatIncome) {
        this.vatIncome = vatIncome;
    }

    public String getRefV01() {
        return refV01;
    }

    public void setRefV01(String refV01) {
        this.refV01 = refV01;
    }

    public String getRefV02() {
        return refV02;
    }

    public void setRefV02(String refV02) {
        this.refV02 = refV02;
    }

    public String getRefV03() {
        return refV03;
    }

    public void setRefV03(String refV03) {
        this.refV03 = refV03;
    }

    public String getRefV04() {
        return refV04;
    }

    public void setRefV04(String refV04) {
        this.refV04 = refV04;
    }

    public String getRefV05() {
        return refV05;
    }

    public void setRefV05(String refV05) {
        this.refV05 = refV05;
    }

    public String getRefV06() {
        return refV06;
    }

    public void setRefV06(String refV06) {
        this.refV06 = refV06;
    }

    public String getRefV07() {
        return refV07;
    }

    public void setRefV07(String refV07) {
        this.refV07 = refV07;
    }

    public String getRefV08() {
        return refV08;
    }

    public void setRefV08(String refV08) {
        this.refV08 = refV08;
    }

    public String getRefV09() {
        return refV09;
    }

    public void setRefV09(String refV09) {
        this.refV09 = refV09;
    }

    public String getRefV10() {
        return refV10;
    }

    public void setRefV10(String refV10) {
        this.refV10 = refV10;
    }

    public String getRefV11() {
        return refV11;
    }

    public void setRefV11(String refV11) {
        this.refV11 = refV11;
    }

    public String getRefV12() {
        return refV12;
    }

    public void setRefV12(String refV12) {
        this.refV12 = refV12;
    }

    public String getRefV13() {
        return refV13;
    }

    public void setRefV13(String refV13) {
        this.refV13 = refV13;
    }

    public String getRefV14() {
        return refV14;
    }

    public void setRefV14(String refV14) {
        this.refV14 = refV14;
    }

    public String getRefV15() {
        return refV15;
    }

    public void setRefV15(String refV15) {
        this.refV15 = refV15;
    }

    public Double getRefN01() {
        return refN01;
    }

    public void setRefN01(Double refN01) {
        this.refN01 = refN01;
    }

    public Double getRefN02() {
        return refN02;
    }

    public void setRefN02(Double refN02) {
        this.refN02 = refN02;
    }

    public Double getRefN03() {
        return refN03;
    }

    public void setRefN03(Double refN03) {
        this.refN03 = refN03;
    }

    public Double getRefN04() {
        return refN04;
    }

    public void setRefN04(Double refN04) {
        this.refN04 = refN04;
    }

    public Double getRefN05() {
        return refN05;
    }

    public void setRefN05(Double refN05) {
        this.refN05 = refN05;
    }

    public Double getRefN06() {
        return refN06;
    }

    public void setRefN06(Double refN06) {
        this.refN06 = refN06;
    }

    public Double getRefN07() {
        return refN07;
    }

    public void setRefN07(Double refN07) {
        this.refN07 = refN07;
    }

    public Double getRefN08() {
        return refN08;
    }

    public void setRefN08(Double refN08) {
        this.refN08 = refN08;
    }

    public Double getRefN09() {
        return refN09;
    }

    public void setRefN09(Double refN09) {
        this.refN09 = refN09;
    }

    public Double getRefN10() {
        return refN10;
    }

    public void setRefN10(Double refN10) {
        this.refN10 = refN10;
    }

    public Date getRefD01() {
        return refD01;
    }

    public void setRefD01(Date refD01) {
        this.refD01 = refD01;
    }

    public Date getRefD02() {
        return refD02;
    }

    public void setRefD02(Date refD02) {
        this.refD02 = refD02;
    }

    public Date getRefD03() {
        return refD03;
    }

    public void setRefD03(Date refD03) {
        this.refD03 = refD03;
    }

    public Date getRefD04() {
        return refD04;
    }

    public void setRefD04(Date refD04) {
        this.refD04 = refD04;
    }

    public Date getRefD05() {
        return refD05;
    }

    public void setRefD05(Date refD05) {
        this.refD05 = refD05;
    }

    public Date getRefD06() {
        return refD06;
    }

    public void setRefD06(Date refD06) {
        this.refD06 = refD06;
    }

    public Date getRefD07() {
        return refD07;
    }

    public void setRefD07(Date refD07) {
        this.refD07 = refD07;
    }

    public Date getRefD08() {
        return refD08;
    }

    public void setRefD08(Date refD08) {
        this.refD08 = refD08;
    }

    public Date getRefD09() {
        return refD09;
    }

    public void setRefD09(Date refD09) {
        this.refD09 = refD09;
    }

    public Date getRefD10() {
        return refD10;
    }

    public void setRefD10(Date refD10) {
        this.refD10 = refD10;
    }

    public String getPeriodNameTo() {
        return periodNameTo;
    }

    public void setPeriodNameTo(String periodNameTo) {
        this.periodNameTo = periodNameTo;
    }

    public String getPeriodNameFrom() {
        return periodNameFrom;
    }

    public void setPeriodNameFrom(String periodNameFrom) {
        this.periodNameFrom = periodNameFrom;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getStartEndDate() {
        return startEndDate;
    }

    public void setStartEndDate(String startEndDate) {
        this.startEndDate = startEndDate;
    }

    public String getTaxTypeRate() {
        return taxTypeRate;
    }

    public void setTaxTypeRate(String taxTypeRate) {
        this.taxTypeRate = taxTypeRate;
    }

    public String getCfItemDesc() {
        return cfItemDesc;
    }

    public void setCfItemDesc(String cfItemDesc) {
        this.cfItemDesc = cfItemDesc;
    }

    public String getPostFlagDesc() {
        return postFlagDesc;
    }

    public void setPostFlagDesc(String postFlagDesc) {
        this.postFlagDesc = postFlagDesc;
    }

    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
    }

    public void setFinIncomeId(Long finIncomeId){
        this.finIncomeId = finIncomeId;
    }

    public Long getFinIncomeId(){
        return finIncomeId;
    }

    public void setCompanyId(Long companyId){
        this.companyId = companyId;
    }

    public Long getCompanyId(){
        return companyId;
    }

    public void setSourceType(String sourceType){
        this.sourceType = sourceType;
    }

    public String getSourceType(){
        return sourceType;
    }

    public void setSourceId(Long sourceId){
        this.sourceId = sourceId;
    }

    public Long getSourceId(){
        return sourceId;
    }

    public void setPeriodName(String periodName){
        this.periodName = periodName;
    }

    public String getPeriodName(){
        return periodName;
    }

    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }

    public Date getStartDate(){
        return startDate;
    }

    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }

    public Date getEndDate(){
        return endDate;
    }

    public void setDays(Long days){
        this.days = days;
    }

    public Long getDays(){
        return days;
    }

    public void setFinanceIncome(Double financeIncome){
        this.financeIncome = financeIncome;
    }

    public Double getFinanceIncome(){
        return financeIncome;
    }

    public Double getFinanceIncomeVat() {
        return financeIncomeVat;
    }

    public void setFinanceIncomeVat(Double financeIncomeVat) {
        this.financeIncomeVat = financeIncomeVat;
    }

    public Double getFinanceIncomeInclud() {
        return financeIncomeInclud;
    }

    public void setFinanceIncomeInclud(Double financeIncomeInclud) {
        this.financeIncomeInclud = financeIncomeInclud;
    }

    public Long getCfItem() {
        return cfItem;
    }

    public void setCfItem(Long cfItem) {
        this.cfItem = cfItem;
    }
}
