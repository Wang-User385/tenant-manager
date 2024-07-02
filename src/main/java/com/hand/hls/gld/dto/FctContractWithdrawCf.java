package com.hand.hls.gld.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Date;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

/**
 * @author wujun
 * @version 1.0
 * @date 2020/2/7 15:24
 * @description copy from 光大项目
 */
@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fct_contract_withdraw_cf"
)
public class FctContractWithdrawCf extends BaseDTO {
    public static final String FIELD_WITHDRAW_ID = "withdrawId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_TIMES = "times";
    public static final String FIELD_CF_ITEM = "cfItem";
    public static final String FIELD_CF_TYPE = "cfType";
    public static final String FIELD_CF_DIRECTION = "cfDirection";
    public static final String FIELD_CF_STATUS = "cfStatus";
    public static final String FIELD_DUE_DATE = "dueDate";
    public static final String FIELD_DUE_AMOUNT = "dueAmount";
    public static final String FIELD_WRITE_OFF_FLAG = "writeOffFlag";
    public static final String FIELD_WRITE_OFF_AMOUNT = "writeOffAmount";
    public static final String FIELD_INTEREST_PERIOD_DAYS = "interestPeriodDays";
    public static final String FIELD_INTEREST_ACCRUAL_BALANCE = "interestAccrualBalance";
    @Id
    @GeneratedValue
    private Long withdrawCfId;
    private Long withdrawId;
    private Long contractId;
    private Long times;
    private Long cfItem;
    private Long cfType;
    @Length(
            max = 100
    )
    private String cfDirection;
    @Length(
            max = 100
    )
    private String cfStatus;
    private Date dueDate;
    private Double dueAmount;
    @Length(
            max = 30
    )
    private String writeOffFlag;
    private Double writeOffAmount;
    private Long interestPeriodDays;
    private Double interestAccrualBalance;
    private Long writeOffId;
    private Long generatedSourceDocId;
    private String generatedSource;
    private String overdueStatus;
    @Transient
    private String cfItemDesc;
    @Transient
    private Double intRate;
    @Transient
    private Double currentPeriodPrincipal;
    @Transient
    private Double currentPeriodInterest;
    @Transient
    private Date interestEndDate;
    @Transient
    private Date withdrawDate;
    @Transient
    private Date dayEndDate;

    public FctContractWithdrawCf() {
    }

    public Long getGeneratedSourceDocId() {
        return this.generatedSourceDocId;
    }

    public void setGeneratedSourceDocId(Long generatedSourceDocId) {
        this.generatedSourceDocId = generatedSourceDocId;
    }

    public String getGeneratedSource() {
        return this.generatedSource;
    }

    public void setGeneratedSource(String generatedSource) {
        this.generatedSource = generatedSource;
    }

    public String getOverdueStatus() {
        return this.overdueStatus;
    }

    public void setOverdueStatus(String overdueStatus) {
        this.overdueStatus = overdueStatus;
    }

    public Date getDayEndDate() {
        return this.dayEndDate;
    }

    public void setDayEndDate(Date dayEndDate) {
        this.dayEndDate = dayEndDate;
    }

    public Date getWithdrawDate() {
        return this.withdrawDate;
    }

    public void setWithdrawDate(Date withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public Date getInterestEndDate() {
        return this.interestEndDate;
    }

    public void setInterestEndDate(Date interestEndDate) {
        this.interestEndDate = interestEndDate;
    }

    public String getCfItemDesc() {
        return this.cfItemDesc;
    }

    public Long getWriteOffId() {
        return this.writeOffId;
    }

    public void setWriteOffId(Long writeOffId) {
        this.writeOffId = writeOffId;
    }

    public void setCfItemDesc(String cfItemDesc) {
        this.cfItemDesc = cfItemDesc;
    }

    public Double getIntRate() {
        return this.intRate;
    }

    public void setIntRate(Double intRate) {
        this.intRate = intRate;
    }

    public Double getCurrentPeriodPrincipal() {
        return this.currentPeriodPrincipal;
    }

    public void setCurrentPeriodPrincipal(Double currentPeriodPrincipal) {
        this.currentPeriodPrincipal = currentPeriodPrincipal;
    }

    public Double getCurrentPeriodInterest() {
        return this.currentPeriodInterest;
    }

    public void setCurrentPeriodInterest(Double currentPeriodInterest) {
        this.currentPeriodInterest = currentPeriodInterest;
    }

    public void setWithdrawId(Long withdrawId) {
        this.withdrawId = withdrawId;
    }

    public Long getWithdrawId() {
        return this.withdrawId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return this.contractId;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public Long getTimes() {
        return this.times;
    }

    public void setCfItem(Long cfItem) {
        this.cfItem = cfItem;
    }

    public Long getCfItem() {
        return this.cfItem;
    }

    public void setCfType(Long cfType) {
        this.cfType = cfType;
    }

    public Long getCfType() {
        return this.cfType;
    }

    public void setCfDirection(String cfDirection) {
        this.cfDirection = cfDirection;
    }

    public String getCfDirection() {
        return this.cfDirection;
    }

    public void setCfStatus(String cfStatus) {
        this.cfStatus = cfStatus;
    }

    public String getCfStatus() {
        return this.cfStatus;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueAmount(Double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public Double getDueAmount() {
        return this.dueAmount;
    }

    public void setWriteOffFlag(String writeOffFlag) {
        this.writeOffFlag = writeOffFlag;
    }

    public String getWriteOffFlag() {
        return this.writeOffFlag;
    }

    public void setWriteOffAmount(Double writeOffAmount) {
        this.writeOffAmount = writeOffAmount;
    }

    public Double getWriteOffAmount() {
        return this.writeOffAmount;
    }

    public void setInterestPeriodDays(Long interestPeriodDays) {
        this.interestPeriodDays = interestPeriodDays;
    }

    public Long getInterestPeriodDays() {
        return this.interestPeriodDays;
    }

    public void setInterestAccrualBalance(Double interestAccrualBalance) {
        this.interestAccrualBalance = interestAccrualBalance;
    }

    public Double getInterestAccrualBalance() {
        return this.interestAccrualBalance;
    }

    public Long getWithdrawCfId() {
        return this.withdrawCfId;
    }

    public void setWithdrawCfId(Long withdrawCfId) {
        this.withdrawCfId = withdrawCfId;
    }
}
