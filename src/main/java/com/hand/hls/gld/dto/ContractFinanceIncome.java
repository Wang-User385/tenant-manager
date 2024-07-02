package com.hand.hls.gld.dto;

/**
 * Created by IntelliJ IDEA.
 * @author: WuJun
 * @Date: 2020/02/07
 * @Time: 11:48
 */

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import org.hibernate.validator.constraints.Length;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

import com.hand.hap.system.dto.BaseDTO;

@ExtensionAttribute(disable=true)
@Table(name = "GLD_CONTRACT_FINANCE_INCOME")
public class ContractFinanceIncome extends BaseDTO {

     public static final String FIELD_FINANCE_INCOME_ID = "financeIncomeId";
     public static final String FIELD_COMPANY_ID = "companyId";
     public static final String FIELD_CONTRACT_ID = "contractId";
     public static final String FIELD_GLD_CASHFLOW_ID = "gldCashflowId";
     public static final String FIELD_PERIOD_NAME = "periodName";
     public static final String FIELD_START_DATE = "startDate";
     public static final String FIELD_END_DATE = "endDate";
     public static final String FIELD_DAYS = "days";
     public static final String FIELD_FINANCE_INCOME = "financeIncome";
     public static final String FIELD_SOURCE_TYPE = "sourceType";
     public static final String FIELD_SOURCE_ID = "sourceId";
     public static final String FIELD_QUOTATION_ID = "quotationId";
     public static final String FIELD_POST_FLAG = "postFlag";
     public static final String FIELD_VAT_INCOME = "vatIncome";
     public static final String FIELD_OBJECT_VERSION_NUMBER = "objectVersionNumber";
     public static final String FIELD_REF_V01 = "refV01";
     public static final String FIELD_REF_V02 = "refV02";
     public static final String FIELD_REF_V03 = "refV03";
     public static final String FIELD_REF_V04 = "refV04";
     public static final String FIELD_REF_V05 = "refV05";
     public static final String FIELD_REF_V06 = "refV06";
     public static final String FIELD_REF_V07 = "refV07";
     public static final String FIELD_REF_V08 = "refV08";
     public static final String FIELD_REF_V09 = "refV09";
     public static final String FIELD_REF_V10 = "refV10";
     public static final String FIELD_REF_V11 = "refV11";
     public static final String FIELD_REF_V12 = "refV12";
     public static final String FIELD_REF_V13 = "refV13";
     public static final String FIELD_REF_V14 = "refV14";
     public static final String FIELD_REF_V15 = "refV15";
     public static final String FIELD_REF_N01 = "refN01";
     public static final String FIELD_REF_N02 = "refN02";
     public static final String FIELD_REF_N03 = "refN03";
     public static final String FIELD_REF_N04 = "refN04";
     public static final String FIELD_REF_N05 = "refN05";
     public static final String FIELD_REF_N06 = "refN06";
     public static final String FIELD_REF_N07 = "refN07";
     public static final String FIELD_REF_N08 = "refN08";
     public static final String FIELD_REF_N09 = "refN09";
     public static final String FIELD_REF_N10 = "refN10";
     public static final String FIELD_REF_D01 = "refD01";
     public static final String FIELD_REF_D02 = "refD02";
     public static final String FIELD_REF_D03 = "refD03";
     public static final String FIELD_REF_D04 = "refD04";
     public static final String FIELD_REF_D05 = "refD05";
     public static final String FIELD_REF_D06 = "refD06";
     public static final String FIELD_REF_D07 = "refD07";
     public static final String FIELD_REF_D08 = "refD08";
     public static final String FIELD_REF_D09 = "refD09";
     public static final String FIELD_REF_D10 = "refD10";
     public static final String FIELD_CF_ITEM = "cfItem";
     public static final String FIELD_FINANCE_INCOME_INCLUD = "financeIncomeInclud";
     public static final String FIELD_FINANCE_INCOME_VAT = "financeIncomeVat";

     @Id
     @GeneratedValue
     private Long financeIncomeId;

     private Long companyId;

     private Long contractId;

     private Long gldCashflowId;

     @Length(max = 100)
     private String periodName;

     private Date startDate;

     private Date endDate;

     private Long days;

     private Double financeIncome;

     @Length(max = 100)
     private String sourceType;

     private Long sourceId;

     private Long quotationId;

     @Length(max = 1)
     private String postFlag;

     private Double vatIncome;

     @Length(max = 4000)
     private String refV01;

     @Length(max = 4000)
     private String refV02;

     @Length(max = 4000)
     private String refV03;

     @Length(max = 4000)
     private String refV04;

     @Length(max = 4000)
     private String refV05;

     @Length(max = 4000)
     private String refV06;

     @Length(max = 4000)
     private String refV07;

     @Length(max = 4000)
     private String refV08;

     @Length(max = 4000)
     private String refV09;

     @Length(max = 4000)
     private String refV10;

     @Length(max = 4000)
     private String refV11;

     @Length(max = 4000)
     private String refV12;

     @Length(max = 4000)
     private String refV13;

     @Length(max = 4000)
     private String refV14;

     @Length(max = 4000)
     private String refV15;

     private Double refN01;

     private Double refN02;

     private Double refN03;

     private Double refN04;

     private Double refN05;

     private Double refN06;

     private Double refN07;

     private Double refN08;

     private Double refN09;

     private Double refN10;


     private Date refD01;

     @Length(max = 11)
     private String refD02;

     @Length(max = 11)
     private String refD03;

     @Length(max = 11)
     private String refD04;

     @Length(max = 11)
     private String refD05;

     @Length(max = 11)
     private String refD06;

     @Length(max = 11)
     private String refD07;

     @Length(max = 11)
     private String refD08;

     @Length(max = 11)
     private String refD09;

     @Length(max = 11)
     private String refD10;

     private Long cfItem;

     private Double financeIncomeInclud;

     private Double financeIncomeVat;


     @Transient
     private String contractNumber;

     @Transient
     private String contractName;

     @Transient
     private String bpName;

     @Transient
     private Double vatRate;

     @Transient
     private Double netCfItem10;
     @Transient
     private Double vatCfItem10;
     @Transient
     private Double dueCfItem10;

     @Transient
     private Double netCfItem1;
     @Transient
     private Double vatCfItem1;
     @Transient
     private Double dueCfItem1;

     @Transient
     private Double netCfItem3;
     @Transient
     private Double vatCfItem3;
     @Transient
     private Double dueCfItem3;

     @Transient
     private Double netCfItem65;
     @Transient
     private Double vatCfItem65;
     @Transient
     private Double dueCfItem65;

     @Transient
     private String periodNameFrom;

     @Transient
     private String periodNameTo;


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

     public String getBpName() {
          return bpName;
     }

     public void setBpName(String bpName) {
          this.bpName = bpName;
     }

     public Double getVatRate() {
          return vatRate;
     }

     public void setVatRate(Double vatRate) {
          this.vatRate = vatRate;
     }

     public Double getNetCfItem10() {
          return netCfItem10;
     }

     public void setNetCfItem10(Double netCfItem10) {
          this.netCfItem10 = netCfItem10;
     }

     public Double getVatCfItem10() {
          return vatCfItem10;
     }

     public void setVatCfItem10(Double vatCfItem10) {
          this.vatCfItem10 = vatCfItem10;
     }

     public Double getDueCfItem10() {
          return dueCfItem10;
     }

     public void setDueCfItem10(Double dueCfItem10) {
          this.dueCfItem10 = dueCfItem10;
     }

     public Double getNetCfItem1() {
          return netCfItem1;
     }

     public void setNetCfItem1(Double netCfItem1) {
          this.netCfItem1 = netCfItem1;
     }

     public Double getVatCfItem1() {
          return vatCfItem1;
     }

     public void setVatCfItem1(Double vatCfItem1) {
          this.vatCfItem1 = vatCfItem1;
     }

     public Double getDueCfItem1() {
          return dueCfItem1;
     }

     public void setDueCfItem1(Double dueCfItem1) {
          this.dueCfItem1 = dueCfItem1;
     }

     public Double getNetCfItem3() {
          return netCfItem3;
     }

     public void setNetCfItem3(Double netCfItem3) {
          this.netCfItem3 = netCfItem3;
     }

     public Double getVatCfItem3() {
          return vatCfItem3;
     }

     public void setVatCfItem3(Double vatCfItem3) {
          this.vatCfItem3 = vatCfItem3;
     }

     public Double getDueCfItem3() {
          return dueCfItem3;
     }

     public void setDueCfItem3(Double dueCfItem3) {
          this.dueCfItem3 = dueCfItem3;
     }

     public Double getNetCfItem65() {
          return netCfItem65;
     }

     public void setNetCfItem65(Double netCfItem65) {
          this.netCfItem65 = netCfItem65;
     }

     public Double getVatCfItem65() {
          return vatCfItem65;
     }

     public void setVatCfItem65(Double vatCfItem65) {
          this.vatCfItem65 = vatCfItem65;
     }

     public Double getDueCfItem65() {
          return dueCfItem65;
     }

     public void setDueCfItem65(Double dueCfItem65) {
          this.dueCfItem65 = dueCfItem65;
     }

     public Long getFinanceIncomeId() {
          return financeIncomeId;
     }

     public void setFinanceIncomeId(Long financeIncomeId) {
          this.financeIncomeId = financeIncomeId;
     }

     public Long getCompanyId() {
          return companyId;
     }

     public void setCompanyId(Long companyId) {
          this.companyId = companyId;
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

     public String getPeriodName() {
          return periodName;
     }

     public void setPeriodName(String periodName) {
          this.periodName = periodName;
     }

     public Date getStartDate() {
          return startDate;
     }

     public void setStartDate(Date startDate) {
          this.startDate = startDate;
     }

     public Date getEndDate() {
          return endDate;
     }

     public void setEndDate(Date endDate) {
          this.endDate = endDate;
     }

     public Long getDays() {
          return days;
     }

     public void setDays(Long days) {
          this.days = days;
     }

     public Double getFinanceIncome() {
          return financeIncome;
     }

     public void setFinanceIncome(Double financeIncome) {
          this.financeIncome = financeIncome;
     }

     public String getSourceType() {
          return sourceType;
     }

     public void setSourceType(String sourceType) {
          this.sourceType = sourceType;
     }

     public Long getSourceId() {
          return sourceId;
     }

     public void setSourceId(Long sourceId) {
          this.sourceId = sourceId;
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

     public String getRefD02() {
          return refD02;
     }

     public void setRefD02(String refD02) {
          this.refD02 = refD02;
     }

     public String getRefD03() {
          return refD03;
     }

     public void setRefD03(String refD03) {
          this.refD03 = refD03;
     }

     public String getRefD04() {
          return refD04;
     }

     public void setRefD04(String refD04) {
          this.refD04 = refD04;
     }

     public String getRefD05() {
          return refD05;
     }

     public void setRefD05(String refD05) {
          this.refD05 = refD05;
     }

     public String getRefD06() {
          return refD06;
     }

     public void setRefD06(String refD06) {
          this.refD06 = refD06;
     }

     public String getRefD07() {
          return refD07;
     }

     public void setRefD07(String refD07) {
          this.refD07 = refD07;
     }

     public String getRefD08() {
          return refD08;
     }

     public void setRefD08(String refD08) {
          this.refD08 = refD08;
     }

     public String getRefD09() {
          return refD09;
     }

     public void setRefD09(String refD09) {
          this.refD09 = refD09;
     }

     public String getRefD10() {
          return refD10;
     }

     public void setRefD10(String refD10) {
          this.refD10 = refD10;
     }

     public Double getFinanceIncomeInclud() {
          return financeIncomeInclud;
     }

     public void setFinanceIncomeInclud(Double financeIncomeInclud) {
          this.financeIncomeInclud = financeIncomeInclud;
     }

     public Double getFinanceIncomeVat() {
          return financeIncomeVat;
     }

     public void setFinanceIncomeVat(Double financeIncomeVat) {
          this.financeIncomeVat = financeIncomeVat;
     }

     public Long getCfItem() {
          return cfItem;
     }

     public void setCfItem(Long cfItem) {
          this.cfItem = cfItem;
     }

     public Date getRefD01() {
          return refD01;
     }

     public void setRefD01(Date refD01) {
          this.refD01 = refD01;
     }

     public String getPeriodNameFrom() {
          return periodNameFrom;
     }

     public void setPeriodNameFrom(String periodNameFrom) {
          this.periodNameFrom = periodNameFrom;
     }

     public String getPeriodNameTo() {
          return periodNameTo;
     }

     public void setPeriodNameTo(String periodNameTo) {
          this.periodNameTo = periodNameTo;
     }
}
