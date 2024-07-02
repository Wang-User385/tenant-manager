package com.hand.hls.meta.dto;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "metadata")
public class Metadata extends BaseDTO {
    public static final String DATA_TYPE_PAGE = "PAGE";
    public static final String DATA_TYPE_TABLE = "TABLE";
    public static final String DATA_TYPE_RELATION = "RELATION";
    public static final String STATUS_CHECKOUT = "CHECKOUT";
    public static final String STATUS_COMMITTED = "COMMITTED";

    @Id
    @GeneratedValue(generator="UUID")
    @Column(name = "ID")
    private String metaId;

    /**
     * 名称
     */
    @Condition(operator = LIKE)
    @Column(name = "NAME")
    private String name;

    /**
     * 描述
     */
    @Condition(operator = LIKE)
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * Xml类型
     */
    @Column(name = "DATA_TYPE")
    private String dataType;

    /**
     * 状态
     */
    @Column(name = "STATUS")
    private String status;

    /**
     * 被谁锁定(用户名)
     */
    @Column(name = "LOCKED_BY")
    private String lockedBy;

    /**
     * 数据ID
     */
    @Column(name = "DATA_ID")
    private String dataId;

    /**
     * 当前变更ID
     */
    @Column(name = "CHANGE_ID")
    private String changeId;

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

    @Transient
    private MetadataItem change;

    public MetadataItem getChange() {
        return change;
    }

    public void setChange(MetadataItem change) {
        this.change = change;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    /**
     * 获取名称
     *
     * @return NAME - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取描述
     *
     * @return DESCRIPTION - 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取Xml类型
     *
     * @return DATA_TYPE - Xml类型
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置Xml类型
     *
     * @param dataType Xml类型
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取状态
     *
     * @return STATUS - 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取被谁锁定(用户名)
     *
     * @return LOCKED_BY - 被谁锁定(用户名)
     */
    public String getLockedBy() {
        return lockedBy;
    }

    /**
     * 设置被谁锁定(用户名)
     *
     * @param lockedBy 被谁锁定(用户名)
     */
    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    /**
     * 获取数据ID
     *
     * @return DATA_ID - 数据ID
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * 设置数据ID
     *
     * @param dataId 数据ID
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * 获取当前变更ID
     *
     * @return CHANGE_ID - 当前变更ID
     */
    public String getChangeId() {
        return changeId;
    }

    /**
     * 设置当前变更ID
     *
     * @param changeId 当前变更ID
     */
    public void setChangeId(String changeId) {
        this.changeId = changeId;
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
}