package com.hand.hls.fin.dto;

/**
 *  created by zhangyu on 20180703
 **/

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(disable = true)
@Table(name = "ct_abs_contract")
public class HlsCusCtAbsContract extends BaseDTO {

    public static final String FIELD_ABS_CON_ID = "absConId";
    public static final String FIELD_LON_CONTRACT_ID = "lonContractId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_TIMES_FROM = "timesFrom";
    public static final String FIELD_TIMES_TO = "timesTo";
    public static final String FIELD_UNRECEIVED_PRINCIPAL = "unreceivedPrincipal";
    public static final String FIELD_UNRECEIVED_INTEREST = "unreceivedInterest";


    @Id
    @GeneratedValue
    private Long absConId; //pk

    private Long lonContractId; //融资合同ID

    private Long contractId; //资产包租赁合同ID

    private Long timesFrom; //期数从

    private Long timesTo; //期数到

    private Double unreceivedPrincipal;//未收本金之和

    private Double unreceivedInterest;//未收利息之和

    @Transient
    private String contractNumber;

    @Transient
    private String contractName;

    @Transient
    private String bpName;

    @Transient
    private Double financeAmount;

    public Long getAbsConId() {
        return absConId;
    }

    public void setAbsConId(Long absConId) {
        this.absConId = absConId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getLonContractId() {
        return lonContractId;
    }

    public void setLonContractId(Long lonContractId) {
        this.lonContractId = lonContractId;
    }

    public Long getTimesFrom() {
        return timesFrom;
    }

    public void setTimesFrom(Long timesFrom) {
        this.timesFrom = timesFrom;
    }

    public Long getTimesTo() {
        return timesTo;
    }

    public void setTimesTo(Long timesTo) {
        this.timesTo = timesTo;
    }

    public Double getUnreceivedPrincipal() {
        return unreceivedPrincipal;
    }

    public void setUnreceivedPrincipal(Double unreceivedPrincipal) {
        this.unreceivedPrincipal = unreceivedPrincipal;
    }

    public Double getUnreceivedInterest() {
        return unreceivedInterest;
    }

    public void setUnreceivedInterest(Double unreceivedInterest) {
        this.unreceivedInterest = unreceivedInterest;
    }

    public Double getFinanceAmount() {
        return financeAmount;
    }

    public void setFinanceAmount(Double financeAmount) {
        this.financeAmount = financeAmount;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
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
}
