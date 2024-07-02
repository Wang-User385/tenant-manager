package com.hand.hls.prj.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "zx_bp_orgbase")
@Data
public class ZxBpOrgbase {
    /**
     * pk
     */
    @Id
    @GeneratedValue
    @Column(name = "RECORD_ID")
    private Long recordId;

    /**
     * 客户号(bp_id)
     */
    @Column(name = "BP_ID")
    private Long bpId;

    /**
     * 管理行代码
     */
    @Column(name = "BANKCODE")
    private String bankcode;

    /**
     * 客户类型
     */
    @Column(name = "CUSTTYPE")
    private String custtype;

    /**
     * 机构信用代码
     */
    @Column(name = "ORGCREDITCODE")
    private String orgcreditcode;

    /**
     * 登记注册类型
     */
    @Column(name = "REGNOTYPE")
    private String regnotype;

    /**
     * 登记注册号码
     */
    @Column(name = "REGNO")
    private String regno;

    /**
     * 纳税人识别号（地税）
     */
    @Column(name = "LOCALTAXNO")
    private String localtaxno;

    /**
     * 开户许可证核准号
     */
    @Column(name = "ACCOUNTPERMITNO")
    private String accountpermitno;

    /**
     * 数据提取日期
     */
    @Column(name = "DATAEXTRADATE")
    private String dataextradate;

    /**
     * 预留字段
     */
    @Column(name = "RESERVEDFIELD")
    private String reservedfield;

    /**
     * 是否事业单位
     */
    @Column(name = "ISINSTITUTION")
    private String isinstitution;

    @Column(name = "CREATED_BY")
    private Integer createdBy;

    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private Integer lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    private Date lastUpdateDate;

    @Transient
    private String custtypeN;

    @Transient
    private String isinstitutionN;

    @Transient
    private String regnotypeN;

    public String getCusttypeN() {
        return custtypeN;
    }

    public void setCusttypeN(String custtypeN) {
        this.custtypeN = custtypeN;
    }

    public String getIsinstitutionN() {
        return isinstitutionN;
    }

    public void setIsinstitutionN(String isinstitutionN) {
        this.isinstitutionN = isinstitutionN;
    }

    public String getRegnotypeN() {
        return regnotypeN;
    }

    public void setRegnotypeN(String regnotypeN) {
        this.regnotypeN = regnotypeN;
    }

    /**
     * 获取pk
     *
     * @return RECORD_ID - pk
     */
    public Long getRecordId() {
        return recordId;
    }

    /**
     * 设置pk
     *
     * @param recordId pk
     */
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    /**
     * 获取客户号(bp_id)
     *
     * @return BP_ID - 客户号(bp_id)
     */
    public Long getBpId() {
        return bpId;
    }

    /**
     * 设置客户号(bp_id)
     *
     * @param bpId 客户号(bp_id)
     */
    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    /**
     * 获取管理行代码
     *
     * @return BANKCODE - 管理行代码
     */
    public String getBankcode() {
        return bankcode;
    }

    /**
     * 设置管理行代码
     *
     * @param bankcode 管理行代码
     */
    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    /**
     * 获取客户类型
     *
     * @return CUSTTYPE - 客户类型
     */
    public String getCusttype() {
        return custtype;
    }

    /**
     * 设置客户类型
     *
     * @param custtype 客户类型
     */
    public void setCusttype(String custtype) {
        this.custtype = custtype;
    }

    /**
     * 获取机构信用代码
     *
     * @return ORGCREDITCODE - 机构信用代码
     */
    public String getOrgcreditcode() {
        return orgcreditcode;
    }

    /**
     * 设置机构信用代码
     *
     * @param orgcreditcode 机构信用代码
     */
    public void setOrgcreditcode(String orgcreditcode) {
        this.orgcreditcode = orgcreditcode;
    }

    /**
     * 获取登记注册类型
     *
     * @return REGNOTYPE - 登记注册类型
     */
    public String getRegnotype() {
        return regnotype;
    }

    /**
     * 设置登记注册类型
     *
     * @param regnotype 登记注册类型
     */
    public void setRegnotype(String regnotype) {
        this.regnotype = regnotype;
    }

    /**
     * 获取登记注册号码
     *
     * @return REGNO - 登记注册号码
     */
    public String getRegno() {
        return regno;
    }

    /**
     * 设置登记注册号码
     *
     * @param regno 登记注册号码
     */
    public void setRegno(String regno) {
        this.regno = regno;
    }

    /**
     * 获取纳税人识别号（地税）
     *
     * @return LOCALTAXNO - 纳税人识别号（地税）
     */
    public String getLocaltaxno() {
        return localtaxno;
    }

    /**
     * 设置纳税人识别号（地税）
     *
     * @param localtaxno 纳税人识别号（地税）
     */
    public void setLocaltaxno(String localtaxno) {
        this.localtaxno = localtaxno;
    }

    /**
     * 获取开户许可证核准号
     *
     * @return ACCOUNTPERMITNO - 开户许可证核准号
     */
    public String getAccountpermitno() {
        return accountpermitno;
    }

    /**
     * 设置开户许可证核准号
     *
     * @param accountpermitno 开户许可证核准号
     */
    public void setAccountpermitno(String accountpermitno) {
        this.accountpermitno = accountpermitno;
    }

    /**
     * 获取数据提取日期
     *
     * @return DATAEXTRADATE - 数据提取日期
     */
    public String getDataextradate() {
        return dataextradate;
    }

    /**
     * 设置数据提取日期
     *
     * @param dataextradate 数据提取日期
     */
    public void setDataextradate(String dataextradate) {
        this.dataextradate = dataextradate;
    }

    /**
     * 获取预留字段
     *
     * @return RESERVEDFIELD - 预留字段
     */
    public String getReservedfield() {
        return reservedfield;
    }

    /**
     * 设置预留字段
     *
     * @param reservedfield 预留字段
     */
    public void setReservedfield(String reservedfield) {
        this.reservedfield = reservedfield;
    }

    /**
     * 获取是否事业单位
     *
     * @return ISINSTITUTION - 是否事业单位
     */
    public String getIsinstitution() {
        return isinstitution;
    }

    /**
     * 设置是否事业单位
     *
     * @param isinstitution 是否事业单位
     */
    public void setIsinstitution(String isinstitution) {
        this.isinstitution = isinstitution;
    }

    /**
     * @return CREATED_BY
     */
    public Integer getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     */
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return CREATION_DATE
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return LAST_UPDATED_BY
     */
    public Integer getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @param lastUpdatedBy
     */
    public void setLastUpdatedBy(Integer lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @return LAST_UPDATE_DATE
     */
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * @param lastUpdateDate
     */
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}