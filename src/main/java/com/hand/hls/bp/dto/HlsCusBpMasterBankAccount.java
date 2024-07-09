package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hls.prj.dto.HlsBpMasterBankAccount;

import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(disable = true)
@Table(name = "hls_bp_master_bank_account")
public class HlsCusBpMasterBankAccount extends HlsBpMasterBankAccount {

    private String bankAccountType;

    private String remarks;

    private String description;
    private String contactPerson;
    private String contactNum;

    @Transient
    private String bpName;

    @Transient
    private Integer projectId;

    @Transient
    private String bankAccountTypeN;
    private String iban;
    private String bankCode;
    private String swiftCode;
    private String primaryFlag;

    //BANK_ADDRESS
    private String bankAddress;

    public String getPrimaryFlag() {
        return primaryFlag;
    }

    public void setPrimaryFlag(String primaryFlag) {
        this.primaryFlag = primaryFlag;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Override
    public String getSwiftCode() {
        return swiftCode;
    }

    @Override
    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public String getBankAccountTypeN() {
        return bankAccountTypeN;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBankAccountTypeN(String bankAccountTypeN) {
        this.bankAccountTypeN = bankAccountTypeN;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }
}