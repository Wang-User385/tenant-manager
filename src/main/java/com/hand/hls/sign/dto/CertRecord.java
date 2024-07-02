package com.hand.hls.sign.dto;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Data;

@Data
@ExtensionAttribute(disable=true)
@Table(name = "prj_cert_record")
public class CertRecord {

    @Transient
    private String signCheckStatusN;

    public String getSignCheckStatusN() {
        return signCheckStatusN;
    }

    public void setSignCheckStatusN(String signCheckStatusN) {
        this.signCheckStatusN = signCheckStatusN;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getBpId() {
        return bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    public String getAgentIdCardNo() {
        return agentIdCardNo;
    }

    public void setAgentIdCardNo(String agentIdCardNo) {
        this.agentIdCardNo = agentIdCardNo;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getLegalPhone() {
        return legalPhone;
    }

    public void setLegalPhone(String legalPhone) {
        this.legalPhone = legalPhone;
    }

    public String getLegalIdCardNo() {
        return legalIdCardNo;
    }

    public void setLegalIdCardNo(String legalIdCardNo) {
        this.legalIdCardNo = legalIdCardNo;
    }

    public String getLegalSignId() {
        return legalSignId;
    }

    public void setLegalSignId(String legalSignId) {
        this.legalSignId = legalSignId;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getLegalOpenTime() {
        return legalOpenTime;
    }

    public void setLegalOpenTime(String legalOpenTime) {
        this.legalOpenTime = legalOpenTime;
    }

    public String getSignEnabledFlag() {
        return signEnabledFlag;
    }

    public void setSignEnabledFlag(String signEnabledFlag) {
        this.signEnabledFlag = signEnabledFlag;
    }

    public String getSignCheckStatus() {
        return signCheckStatus;
    }

    public void setSignCheckStatus(String signCheckStatus) {
        this.signCheckStatus = signCheckStatus;
    }

    public String getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(String operateUserId) {
        this.operateUserId = operateUserId;
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

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getBpCategory() {
        return bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public String getBpCategoryN() {
        return bpCategoryN;
    }

    public void setBpCategoryN(String bpCategoryN) {
        this.bpCategoryN = bpCategoryN;
    }

    public String getAnxinsignCheckStatusN() {
        return anxinsignCheckStatusN;
    }

    public void setAnxinsignCheckStatusN(String anxinsignCheckStatusN) {
        this.anxinsignCheckStatusN = anxinsignCheckStatusN;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getBpAttachmentId() {
        return bpAttachmentId;
    }

    public void setBpAttachmentId(String bpAttachmentId) {
        this.bpAttachmentId = bpAttachmentId;
    }

    public String getOperateUserIdN() {
        return operateUserIdN;
    }

    public void setOperateUserIdN(String operateUserIdN) {
        this.operateUserIdN = operateUserIdN;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getCheckTimeFormat() {
        return checkTimeFormat;
    }

    public void setCheckTimeFormat(String checkTimeFormat) {
        this.checkTimeFormat = checkTimeFormat;
    }

    public String getOpenTimeFormat() {
        return openTimeFormat;
    }

    public void setOpenTimeFormat(String openTimeFormat) {
        this.openTimeFormat = openTimeFormat;
    }

    public String getLegalOpenTimeFormat() {
        return legalOpenTimeFormat;
    }

    public void setLegalOpenTimeFormat(String legalOpenTimeFormat) {
        this.legalOpenTimeFormat = legalOpenTimeFormat;
    }

    @Id
    @GeneratedValue
    private Long recordId;

    /**
     *商业伙伴ID
     */
    private Long bpId;
    /**
     *企业联系电话
     */
    private String companyPhone;
    /**
     *经办人姓名
     */
    private String agentName;
    /**
     *经办人手机号
     */
    private String agentPhone;
    /**
     *经办人身份证号
     */
    private String agentIdCardNo;
    /**
     *签约平台用户ID
     */
    private String signId;
    /**
     *法人姓名
     */
    private String legalName;
    /**
     *法人手机号
     */
    private String legalPhone;
    /**
     *法人身份证号
     */
    private String legalIdCardNo;
    /**
     *法人签约平台用户ID
     */
    private String legalSignId;
    /**
     *复核时间
     */
    private String checkTime;
    /**
     *经销商开户时间
     */
    private String openTime;
    /**
     *法人开户时间
     */
    private String legalOpenTime;
    /**
     *签约启用标识
     */
    private String signEnabledFlag;
    /**
     *复核状态
     */
    private String signCheckStatus;
    /**
     *操作用户ID
     */
    private String operateUserId;
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
    private String bpCode;

    @Transient
    private String bpClass;

    @Transient
    private String bpName;

    @Transient
    private String regno;

    @Transient
    private String bpCategory;

    @Transient
    private String bpCategoryN;

    @Transient
    private String anxinsignCheckStatusN;

    @Transient
    private String venderName;

    @Transient
    private String bpAttachmentId;

    @Transient
    private String operateUserIdN;

    @Transient
    private String unitName;

    @Transient
    private String checkTimeFormat;

    @Transient
    private String openTimeFormat;

    @Transient
    private String legalOpenTimeFormat;
}
