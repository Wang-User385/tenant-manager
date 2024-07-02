package com.hand.hls.lease.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@ExtensionAttribute(disable=true)
@Table(name = "yx_lease_item_manufacturer")
public class YxLeaseItemManufacturer {

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public Long getBpId() {
        return bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getBpIdN() {
        return bpIdN;
    }

    public void setBpIdN(String bpIdN) {
        this.bpIdN = bpIdN;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserIdN() {
        return createUserIdN;
    }

    public void setCreateUserIdN(String createUserIdN) {
        this.createUserIdN = createUserIdN;
    }

    @Id
    @GeneratedValue
    private Long manufacturerId;
    /**
     *商业伙伴id
     */
    private Long bpId;
    /**
     *生产厂商名称
     */
    private String manufacturerName;
    /**
     *启用标识
     */
    private String enabledFlag;
    /**
     *
     */
    private Date creationDate;
    /**
     *
     */
    private Long createdBy;
    /**
     *
     */
    private Date lastUpdateDate;
    /**
     *
     */
    private Long lastUpdatedBy;

    @Transient
    private String bpIdN;

    @Transient
    private String createDate;

    @Transient
    private Long createUserId;

    @Transient
    private String createUserIdN;
}
