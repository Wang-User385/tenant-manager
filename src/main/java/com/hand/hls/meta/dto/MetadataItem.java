package com.hand.hls.meta.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "metadata_item")
public class MetadataItem extends BaseDTO {
    @Id
    @GeneratedValue(generator="UUID")
    @Column(name = "ITEM_ID")
    private String itemId;

    /**
     * metadata表ID
     */
    @Column(name = "METADATA_ID")
    private String metadataId;

    /**
     * 数据版本
     */
    @Column(name = "DATA_VERSION")
    private Long dataVersion;

    @Column(name = "OBJECT_VERSION_NUMBER")
    private Long objectVersionNumber;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "PROGRAM_ID")
    private Long programId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @Column(name = "LAST_UPDATE_DATE")
    private Date lastUpdateDate;

    @Column(name = "LAST_UPDATE_LOGIN")
    private Long lastUpdateLogin;

    /**
     * xml数据
     */
    @Column(name = "DATA")
    private String data;

    @Transient
    private Long ruleId;

    @Transient
    private String dataName;

    @Transient
    private Long ruleLineId;

    @Transient
    private String fieldType;

    @Transient
    private String dataFieldName;
    @Transient
    private String dataFieldNameDescription;

    public String getDataFieldNameDescription() {
        return dataFieldNameDescription;
    }

    public void setDataFieldNameDescription(String dataFieldNameDescription) {
        this.dataFieldNameDescription = dataFieldNameDescription;
    }

    public String getDataFieldName() {
        return dataFieldName;
    }

    public void setDataFieldName(String dataFieldName) {
        this.dataFieldName = dataFieldName;
    }


    public Long getRuleLineId() {
        return ruleLineId;
    }

    public void setRuleLineId(Long ruleLineId) {
        this.ruleLineId = ruleLineId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * 获取metadata表ID
     *
     * @return METADATA_ID - metadata表ID
     */
    public String getMetadataId() {
        return metadataId;
    }

    /**
     * 设置metadata表ID
     *
     * @param metadataId metadata表ID
     */
    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    /**
     * 获取数据版本
     *
     * @return DATA_VERSION - 数据版本
     */
    public Long getDataVersion() {
        return dataVersion;
    }

    /**
     * 设置数据版本
     *
     * @param dataVersion 数据版本
     */
    public void setDataVersion(Long dataVersion) {
        this.dataVersion = dataVersion;
    }

    /**
     * @return OBJECT_VERSION_NUMBER
     */
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    /**
     * @param objectVersionNumber
     */
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    /**
     * @return REQUEST_ID
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * @param requestId
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    /**
     * @return PROGRAM_ID
     */
    public Long getProgramId() {
        return programId;
    }

    /**
     * @param programId
     */
    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    /**
     * @return CREATED_BY
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     */
    public void setCreatedBy(Long createdBy) {
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
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * @param lastUpdatedBy
     */
    public void setLastUpdatedBy(Long lastUpdatedBy) {
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

    /**
     * @return LAST_UPDATE_LOGIN
     */
    public Long getLastUpdateLogin() {
        return lastUpdateLogin;
    }

    /**
     * @param lastUpdateLogin
     */
    public void setLastUpdateLogin(Long lastUpdateLogin) {
        this.lastUpdateLogin = lastUpdateLogin;
    }

    /**
     * 获取xml数据
     *
     * @return DATA - xml数据
     */
    public String getData() {
        return data;
    }

    /**
     * 设置xml数据
     *
     * @param data xml数据
     */
    public void setData(String data) {
        this.data = data;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}