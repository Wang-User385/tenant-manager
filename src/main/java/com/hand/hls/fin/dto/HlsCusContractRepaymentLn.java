package com.hand.hls.fin.dto;

/**
 * ferry
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "lon_contract_repayment_ln")
public class HlsCusContractRepaymentLn extends BaseDTO {

    public static final String FIELD_LN_ID = "lnId";
    public static final String FIELD_WITHDRAW_ID = "withdrawId";
    public static final String FIELD_REPAYMENT_ID = "repaymentId";
    public static final String FIELD_BANK_ACCOUNT_ID = "bankAccountId";
    public static final String FIELD_BANK_BRANCH_NAME = "bankBranchName";
    public static final String FIELD_BANK_ACCOUNT_NUM = "bankAccountNum";
    public static final String FIELD_BANK_ACCOUNT_NAME = "bankAccountName";
    public static final String FIELD_DUE_AMOUNT = "dueAmount";
    public static final String DEFAULT_FLAG = "defaultFlag";

    @Id
    @GeneratedValue
    private Long lnId; //融资还款账户主键

    @NotNull
    private Long withdrawId; //融资提款id

    @NotNull
    private Long repaymentId; //融资还款id

    @NotNull
    private Long bankAccountId; //回款账户主键

    @Length(max = 200)
    private String bankBranchName; //回款账户支行名称

    @Length(max = 200)
    private String bankAccountNum; //回款账户

    @Length(max = 200)
    private String bankAccountName; //回款账户名称

    private Date dueDate;  // 还款日期

    private Double dueAmount; //还款金额

    /**
     * 人民币还款金额
     */
    private Double  cnyDueAmount;


    @Length(max = 10)
    private String defaultFlag;  // 默认还款账户

    @Transient
    private Long times;  // 期数

    @Transient
    private String cfItemDesc;  // 现金流项目

    @Transient
    private String prevDueAmount;  // 预还款金额

    @Transient
    private Double cnyPlannedDueAmount; //人民币预还款金额

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        this.times = times;
    }

    public String getCfItemDesc() {
        return cfItemDesc;
    }

    public void setCfItemDesc(String cfItemDesc) {
        this.cfItemDesc = cfItemDesc;
    }

    public String getPrevDueAmount() {
        return prevDueAmount;
    }

    public void setPrevDueAmount(String prevDueAmount) {
        this.prevDueAmount = prevDueAmount;
    }

    public void setLnId(Long lnId){
     this.lnId = lnId;
    }

    public Long getLnId(){
     return lnId;
    }

    public void setWithdrawId(Long withdrawId){
     this.withdrawId = withdrawId;
    }

    public Long getWithdrawId(){
     return withdrawId;
    }

    public void setRepaymentId(Long repaymentId){
     this.repaymentId = repaymentId;
    }

    public Long getRepaymentId(){
     return repaymentId;
    }

    public void setBankAccountId(Long bankAccountId){
     this.bankAccountId = bankAccountId;
    }

    public Long getBankAccountId(){
     return bankAccountId;
    }

    public void setBankBranchName(String bankBranchName){
     this.bankBranchName = bankBranchName;
    }

    public String getBankBranchName(){
     return bankBranchName;
    }

    public void setBankAccountNum(String bankAccountNum){
     this.bankAccountNum = bankAccountNum;
    }

    public String getBankAccountNum(){
     return bankAccountNum;
    }

    public void setBankAccountName(String bankAccountName){
     this.bankAccountName = bankAccountName;
    }

    public String getBankAccountName(){
     return bankAccountName;
    }

    public void setDueAmount(Double dueAmount){
     this.dueAmount = dueAmount;
    }

    public Double getDueAmount(){
     return dueAmount;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Double getCnyPlannedDueAmount() {
        return cnyPlannedDueAmount;
    }

    public void setCnyPlannedDueAmount(Double cnyPlannedDueAmount) {
        this.cnyPlannedDueAmount = cnyPlannedDueAmount;
    }

    public Double getCnyDueAmount() {
        return cnyDueAmount;
    }

    public void setCnyDueAmount(Double cnyDueAmount) {
        this.cnyDueAmount = cnyDueAmount;
    }
}
