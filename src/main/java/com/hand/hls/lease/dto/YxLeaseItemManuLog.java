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
@Table(name = "yx_lease_item_manu_log")
public class YxLeaseItemManuLog {

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

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

    public Date getLogCreationDate() {
        return logCreationDate;
    }

    public void setLogCreationDate(Date logCreationDate) {
        this.logCreationDate = logCreationDate;
    }

    public Long getLogCreatedBy() {
        return logCreatedBy;
    }

    public void setLogCreatedBy(Long logCreatedBy) {
        this.logCreatedBy = logCreatedBy;
    }

    public Date getLogLastUpdateDate() {
        return logLastUpdateDate;
    }

    public void setLogLastUpdateDate(Date logLastUpdateDate) {
        this.logLastUpdateDate = logLastUpdateDate;
    }

    public Long getLogLastUpdatedBy() {
        return logLastUpdatedBy;
    }

    public void setLogLastUpdatedBy(Long logLastUpdatedBy) {
        this.logLastUpdatedBy = logLastUpdatedBy;
    }

    public String getChangeInfo() {
        return changeInfo;
    }

    public void setChangeInfo(String changeInfo) {
        this.changeInfo = changeInfo;
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
    private Long logId;

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
    /**
     *
     */
    private Date logCreationDate;
    /**
     *
     */
    private Long logCreatedBy;
    /**
     *
     */
    private Date logLastUpdateDate;
    /**
     *
     */
    private Long logLastUpdatedBy;
    /**
     *
     */
    private String changeInfo;

    @Transient
    private String bpIdN;

    @Transient
    private String createDate;

    @Transient
    private Long createUserId;

    @Transient
    private String createUserIdN;
}
