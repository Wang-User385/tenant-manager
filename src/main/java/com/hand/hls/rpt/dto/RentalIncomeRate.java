package com.hand.hls.rpt.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name="RPT_RENTAL_INCOME_RATE")
public class RentalIncomeRate
        extends BaseDTO
{
    public static final String FIELD_RECORD_ID = "recordId";
    public static final String FIELD_CASHFLOW_ID = "cashflowId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_BP_ID = "bpId";
    public static final String FIELD_BP_NAME = "bpName";
    public static final String FIELD_CF_ITEM = "cfItem";
    public static final String FIELD_ORGANIZATION_NAME = "organizationName";
    public static final String FIELD_CONTRACT_NUMBER = "contractNumber";
    public static final String FIELD_DUE_DATE = "dueDate";
    public static final String FIELD_LAST_RECEIVED_DATE = "lastReceivedDate";
    public static final String FIELD_CURRENCY_NAME = "currencyName";
    public static final String FIELD_INT_RATE = "intRate";
    public static final String FIELD_DUE_AMOUNT = "dueAmount";
    public static final String FIELD_RECEIVED_AMOUNT = "receivedAmount";
    public static final String FIELD_OVERDUE_AMOUNT = "overdueAmount";
    public static final String FIELD_BUSINESS_UNIT = "businessUnit";
    public static final String FIELD_PLAN_JUDGE = "planJudge";
    public static final String FIELD_SOURCES_FLAG = "sourcesFlag";
    public static final String FIELD_TIMES = "times";
    @Id
    @GeneratedValue
    private Long recordId;
    private Long cashflowId;
    private Long contractId;
    private Long bpId;
    @Length(max=2000)
    private String bpName;
    private Long cfItem;
    @Length(max=2000)
    private String organizationName;
    @Length(max=2000)
    private String contractNumber;
    private Date dueDate;
    private Date lastReceivedDate;
    @Length(max=2000)
    private String currencyName;
    private Double intRate;
    private Double dueAmount;
    private Double receivedAmount;
    private Double overdueAmount;
    @Length(max=2000)
    private String businessUnit;
    @Length(max=2000)
    private String planJudge;
    @Length(max=200)
    private String sourcesFlag;
    @Length(max=200)
    private Long times;
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
    private String attributes_15;

    @Transient
    private Date dueDateFrom;
    @Transient
    private Date dueDateTo;
    @Transient
    private Double dueAmountFrom;
    @Transient
    private Double dueAmountTo;
    @Transient
    private Double outstandingAmountFrom;
    @Transient
    private Double outstandingAmountTo;
    @Transient
    private Double receivedRate;
    @Transient
    private String cashflowItemDesc;
    @Transient
    private Double outstandingAmount;
    @Transient
    private String sourcesFlagN;
    @Transient
    private Long headerId;

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getRecordId()
    {
        return this.recordId;
    }

    public void setCashflowId(Long cashflowId)
    {
        this.cashflowId = cashflowId;
    }

    public Long getCashflowId()
    {
        return this.cashflowId;
    }

    public void setContractId(Long contractId)
    {
        this.contractId = contractId;
    }

    public Long getContractId()
    {
        return this.contractId;
    }

    public void setBpId(Long bpId)
    {
        this.bpId = bpId;
    }

    public Long getBpId()
    {
        return this.bpId;
    }

    public void setBpName(String bpName)
    {
        this.bpName = bpName;
    }

    public String getBpName()
    {
        return this.bpName;
    }

    public void setCfItem(Long cfItem)
    {
        this.cfItem = cfItem;
    }

    public Long getCfItem()
    {
        return this.cfItem;
    }

    public void setOrganizationName(String organizationName)
    {
        this.organizationName = organizationName;
    }

    public String getOrganizationName()
    {
        return this.organizationName;
    }

    public void setContractNumber(String contractNumber)
    {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber()
    {
        return this.contractNumber;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Date getDueDate()
    {
        return this.dueDate;
    }

    public void setLastReceivedDate(Date lastReceivedDate)
    {
        this.lastReceivedDate = lastReceivedDate;
    }

    public Date getLastReceivedDate()
    {
        return this.lastReceivedDate;
    }

    public void setCurrencyName(String currencyName)
    {
        this.currencyName = currencyName;
    }

    public String getCurrencyName()
    {
        return this.currencyName;
    }

    public void setIntRate(Double intRate)
    {
        this.intRate = intRate;
    }

    public Double getIntRate()
    {
        return this.intRate;
    }

    public void setDueAmount(Double dueAmount)
    {
        this.dueAmount = dueAmount;
    }

    public Double getDueAmount()
    {
        return this.dueAmount;
    }

    public void setReceivedAmount(Double receivedAmount)
    {
        this.receivedAmount = receivedAmount;
    }

    public Double getReceivedAmount()
    {
        return this.receivedAmount;
    }

    public void setOverdueAmount(Double overdueAmount)
    {
        this.overdueAmount = overdueAmount;
    }

    public Double getOverdueAmount()
    {
        return this.overdueAmount;
    }

    public void setBusinessUnit(String businessUnit)
    {
        this.businessUnit = businessUnit;
    }

    public String getBusinessUnit()
    {
        return this.businessUnit;
    }

    public void setPlanJudge(String planJudge)
    {
        this.planJudge = planJudge;
    }

    public String getPlanJudge()
    {
        return this.planJudge;
    }

    public void setSourcesFlag(String sourcesFlag)
    {
        this.sourcesFlag = sourcesFlag;
    }

    public String getSourcesFlag()
    {
        return this.sourcesFlag;
    }

    public void setTimes(Long times)
    {
        this.times = times;
    }

    public Long getTimes()
    {
        return this.times;
    }

    public void setDueDateFrom(Date dueDateFrom)
    {
        this.dueDateFrom = dueDateFrom;
    }

    public Date getDueDateFrom()
    {
        return this.dueDateFrom;
    }

    public void setDueDateTo(Date dueDateTo)
    {
        this.dueDateTo = dueDateTo;
    }

    public Date getDueDateTo()
    {
        return this.dueDateTo;
    }

    public void setDueAmountFrom(Double dueAmountFrom)
    {
        this.dueAmountFrom = dueAmountFrom;
    }

    public Double getDueAmountFrom()
    {
        return this.dueAmountFrom;
    }

    public void setDueAmountTo(Double dueAmountTo)
    {
        this.dueAmountTo = dueAmountTo;
    }

    public Double getDueAmountTo()
    {
        return this.dueAmountTo;
    }

    public void setOutstandingAmountFrom(Double outstandingAmountFrom)
    {
        this.outstandingAmountFrom = outstandingAmountFrom;
    }

    public Double getOutstandingAmountFrom()
    {
        return this.outstandingAmountFrom;
    }

    public void settOutstandingAmountTo(Double outstandingAmountTo)
    {
        this.outstandingAmountTo = outstandingAmountTo;
    }

    public Double getOutstandingAmountTo()
    {
        return this.outstandingAmountTo;
    }

    public void settReceivedRate(Double receivedRate)
    {
        this.receivedRate = receivedRate;
    }

    public Double getReceivedRate()
    {
        return this.receivedRate;
    }

    public void setCashflowItemDesc(String cashflowItemDesc)
    {
        this.cashflowItemDesc = cashflowItemDesc;
    }

    public String getCashflowItemDesc()
    {
        return this.cashflowItemDesc;
    }

    public void settOutstandingAmount(Double cashflowItemDesc)
    {
        this.outstandingAmount = this.outstandingAmount;
    }

    public Double getOutstandingAmount()
    {
        return this.outstandingAmount;
    }

    public void settSourcesFlagN(String sourcesFlagN)
    {
        this.sourcesFlagN = sourcesFlagN;
    }

    public String getSourcesFlagN()
    {
        return this.sourcesFlagN;
    }

    public void settHeaderId(Long headerId)
    {
        this.headerId = headerId;
    }

    public Long getHeaderId()
    {
        return this.headerId;
    }

    public void settAttributes_1(String attributes_1)
    {
        this.attributes_1 = attributes_1;
    }

    public String getAttributes_1()
    {
        return this.attributes_1;
    }

    public void settAttributes_2(String attributes_2)
    {
        this.attributes_2 = attributes_2;
    }

    public String getAttributes_2()
    {
        return this.attributes_2;
    }

    public void settAttributes_3(String attributes_3)
    {
        this.attributes_3 = attributes_3;
    }

    public String getAttributes_3()
    {
        return this.attributes_3;
    }

    public void settAttributes_4(String attributes_4)
    {
        this.attributes_4 = attributes_4;
    }

    public String getAttributes_4()
    {
        return this.attributes_4;
    }

    public void settAttributes_5(String attributes_5)
    {
        this.attributes_5 = attributes_5;
    }

    public String getAttributes_5()
    {
        return this.attributes_5;
    }

    public void settAttributes_6(String attributes_6)
    {
        this.attributes_6 = attributes_6;
    }

    public String getAttributes_6()
    {
        return this.attributes_6;
    }

    public void settAttributes_7(String attributes_7)
    {
        this.attributes_7 = attributes_7;
    }

    public String getAttributes_7()
    {
        return this.attributes_7;
    }

    public void settAttributes_8(String attributes_8)
    {
        this.attributes_8 = attributes_8;
    }

    public String getAttributes_8()
    {
        return this.attributes_8;
    }

    public void settAttributes_9(String attributes_9)
    {
        this.attributes_9 = attributes_9;
    }

    public String getAttributes_9()
    {
        return this.attributes_9;
    }

    public void settAttributes_10(String attributes_10)
    {
        this.attributes_10 = attributes_10;
    }

    public String getAttributes_10()
    {
        return this.attributes_10;
    }

    public void settAttributes_11(String attributes_11)
    {
        this.attributes_11 = attributes_11;
    }

    public String getAttributes_11()
    {
        return this.attributes_11;
    }

    public void settAttributes_12(String attributes_12)
    {
        this.attributes_12 = attributes_12;
    }

    public String getAttributes_12()
    {
        return this.attributes_12;
    }

    public void settAttributes_13(String attributes_13)
    {
        this.attributes_13 = attributes_13;
    }

    public String getAttributes_13()
    {
        return this.attributes_13;
    }

    public void settAttributes_14(String attributes_14)
    {
        this.attributes_14 = attributes_14;
    }

    public String getAttributes_14()
    {
        return this.attributes_14;
    }

    public void settAttributes_15(String attributes_15)
    {
        this.attributes_15 = attributes_15;
    }
    public String getAttributes_15()
    {
        return this.attributes_15;
    }
}
