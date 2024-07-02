package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(
        name = "con_contract_archive"
)
@ExtensionAttribute(
        disable = true
)
@Getter
@Setter
public class HlsCusConContractArchive extends ConContractArchive {

    /**
     * 档案编号
     */
    private String contractArchiveNumber;

    private Long confirmPerson;
    @Transient
    private String confirmPersonN;
    private Date confirmDate;
    @Transient
    private String statusN;
    /**
     * 合同编号
     */
    @Transient
    private String contractNumber;

    /**
     * 法务合同编号
     */
    @Transient
    private String legalContractNumber;

    /**
     * 合同名称
     */
    @Transient
    private String contractName;

    /**
     * 客户名称
     */
    @Transient
    private String bpName;

    /**
     * 项目经理
     */
    @Transient
    private String hostProjectManagerN;

    public HlsCusConContractArchive() {
    }

    public String getContractArchiveNumber() {
        return contractArchiveNumber;
    }

    public void setContractArchiveNumber(String contractArchiveNumber) {
        this.contractArchiveNumber = contractArchiveNumber;
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

    public String getBpName() {
        return bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getHostProjectManagerN() {
        return hostProjectManagerN;
    }

    public void setHostProjectManagerN(String hostProjectManagerN) {
        this.hostProjectManagerN = hostProjectManagerN;
    }

}