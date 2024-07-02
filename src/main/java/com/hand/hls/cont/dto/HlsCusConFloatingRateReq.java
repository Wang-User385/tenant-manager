package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by zhangyu on 2018/5/29.
 */
@ExtensionAttribute(disable = true)
@Table(name = "con_floating_rate_req")
public class HlsCusConFloatingRateReq extends ConFloatingRateReq {
    public static final String FIELD_BASE_RATE_SET = "baseRateSet";

    @Length(max = 200)
    private String baseRateSet;

    @Transient
    private Date validFrom;

    @Transient
    private String documentTypeDesc;

    @Transient
    private String contractNumber;

    @Transient
    private String contractName;
    @Transient
    private String businessTypeN;
    @Transient
    private String documentTypeN;

    @Transient
    private String bpCode;

    @Transient
    private String bpName;
    @Transient
    private String statusN;

    @Transient
    private Long fltReqLnId;

    public String getBaseRateSet() {
        return baseRateSet;
    }

    public void setBaseRateSet(String baseRateSet) {
        this.baseRateSet = baseRateSet;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public String getDocumentTypeDesc() {
        return documentTypeDesc;
    }

    public void setDocumentTypeDesc(String documentTypeDesc) {
        this.documentTypeDesc = documentTypeDesc;
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

    public String getBpCode() {
        return bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    @Transient
    private String statusList;

    public String getStatusList() {
        return statusList;
    }

    public void setStatusList(String statusList) {
        this.statusList = statusList;
    }

    public String getBusinessTypeN() {
        return businessTypeN;
    }

    public void setBusinessTypeN(String businessTypeN) {
        this.businessTypeN = businessTypeN;
    }

    public String getDocumentTypeN() {
        return documentTypeN;
    }

    public void setDocumentTypeN(String documentTypeN) {
        this.documentTypeN = documentTypeN;
    }

    @Override
    public String getStatusN() {
        return statusN;
    }

    @Override
    public void setStatusN(String statusN) {
        this.statusN = statusN;
    }

    @Override
    public Long getFltReqLnId() {
        return fltReqLnId;
    }

    @Override
    public void setFltReqLnId(Long fltReqLnId) {
        this.fltReqLnId = fltReqLnId;
    }
}
