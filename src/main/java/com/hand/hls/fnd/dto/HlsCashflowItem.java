package com.hand.hls.fnd.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by zyx on 23/04/09.
 */
@SuppressWarnings("serial")
@ExtensionAttribute(disable=true)
@Table(name = "hls_cashflow_item")
public class HlsCashflowItem extends BaseDTO implements Serializable{
    @Id
    private String cfItem;
    @Id
    private String cfType;

    private String finIncomeProfile;

    private String billingDesc;

    private String calcPenalty;


    private String description;

    private String cfDirection;

    private String enabledFlag;

    private String systemFlag;

    @Transient
    private String queryCondition;

    public String getCfItem() {
        return cfItem;
    }

    public void setCfItem(String cfItem) {
        this.cfItem = cfItem;
    }

    public String getCfType() {
        return cfType;
    }

    public void setCfType(String cfType) {
        this.cfType = cfType;
    }

    public String getFinIncomeProfile() {
        return finIncomeProfile;
    }

    public void setFinIncomeProfile(String finIncomeProfile) {
        this.finIncomeProfile = finIncomeProfile;
    }

    public String getBillingDesc() {
        return billingDesc;
    }

    public void setBillingDesc(String billingDesc) {
        this.billingDesc = billingDesc;
    }

    public String getCalcPenalty() {
        return calcPenalty;
    }

    public void setCalcPenalty(String calcPenalty) {
        this.calcPenalty = calcPenalty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCfDirection() {
        return cfDirection;
    }

    public void setCfDirection(String cfDirection) {
        this.cfDirection = cfDirection;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getSystemFlag() {
        return systemFlag;
    }

    public void setSystemFlag(String systemFlag) {
        this.systemFlag = systemFlag;
    }

    public String getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }
}
