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
@Table(name = "yx_lease_item_classify")
public class YxLeaseItemClassify {


    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyCode() {
        return classifyCode;
    }

    public void setClassifyCode(String classifyCode) {
        this.classifyCode = classifyCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public Long getSuperiorClassifyId() {
        return superiorClassifyId;
    }

    public void setSuperiorClassifyId(Long superiorClassifyId) {
        this.superiorClassifyId = superiorClassifyId;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getAdvancedEquipmentFlag() {
        return advancedEquipmentFlag;
    }

    public void setAdvancedEquipmentFlag(String advancedEquipmentFlag) {
        this.advancedEquipmentFlag = advancedEquipmentFlag;
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

    public String getLabelN() {
        return labelN;
    }

    public void setLabelN(String labelN) {
        this.labelN = labelN;
    }

    public String getAttributeN() {
        return attributeN;
    }

    public void setAttributeN(String attributeN) {
        this.attributeN = attributeN;
    }

    public String getSuperiorClassifyIdN() {
        return superiorClassifyIdN;
    }

    public void setSuperiorClassifyIdN(String superiorClassifyIdN) {
        this.superiorClassifyIdN = superiorClassifyIdN;
    }

    public String getManufacturerIdN() {
        return manufacturerIdN;
    }

    public void setManufacturerIdN(String manufacturerIdN) {
        this.manufacturerIdN = manufacturerIdN;
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

    public String getExpandFlag() {
        return expandFlag;
    }

    public void setExpandFlag(String expandFlag) {
        this.expandFlag = expandFlag;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    @Id
    @GeneratedValue
    private Long classifyId;

    /**
     *租赁物分类编码
     */
    private String classifyCode;
    /**
     *标签
     */
    private String label;
    /**
     *属性
     */
    private String attribute;
    /**
     *属性值
     */
    private String attributeValue;
    /**
     *关联上级分类
     */
    private Long superiorClassifyId;
    /**
     *关联生产厂商
     */
    private Long manufacturerId;
    /**
     *高端装备标识
     */
    private String advancedEquipmentFlag;
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
    private String labelN;

    @Transient
    private String attributeN;

    @Transient
    private String superiorClassifyIdN;

    @Transient
    private String manufacturerIdN;

    @Transient
    private String createDate;

    @Transient
    private Long createUserId;

    @Transient
    private String createUserIdN;

    @Transient
    private String expandFlag;

    @Transient
    private String seqNum;

    private Integer durableYears;

    private String residualsRate;
}
