//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(
        name = "hls_bp_master_relation"
)
public class HlsBpMasterRelation extends BaseDTO {
    @Id
    private Long bpRelationId;
    private Long relationBpId;
    private Long bpId;
    private String bpRelationType;
    private String admitStatus;
    private String relationType;
    private Long relatedBpId;
    private String enabledFlag;
    private String relationCategory;
    private Date termOfAgreementFrom;
    private Date termOfAgreementTo;
    private String reportLink;
    private String reportStatus;
    private Long reportQueryTimes;
    private String reportToken;

    @Transient
    private String description;
    @Transient
    private String bpCode;

    @Transient
    private String bpName;
    @Transient
    private String personType;
    @Transient
    private String bpClass;
    @Transient
    private String bpRelationTypeN;
    @Transient
    private String bpCodeN;
    @Transient
    private String admitStatusN;
    @Transient
    private String relationTypeN;
    @Transient
    private String relatedBpIdN;


    public HlsBpMasterRelation() {
    }

    public Long getBpRelationId() {
        return bpRelationId;
    }

    public void setBpRelationId(Long bpRelationId) {
        this.bpRelationId = bpRelationId;
    }
    public String getBpClass() {
        return this.bpClass;
    }

    public void setBpClass(String bpClass) {
        this.bpClass = bpClass;
    }

    public String getPersonType() {
        return this.personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBpCode() {
        return this.bpCode;
    }

    public void setBpCode(String bpCode) {
        this.bpCode = bpCode;
    }

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpRelationType() {
        return this.bpRelationType;
    }

    public void setBpRelationType(String bpRelationType) {
        this.bpRelationType = bpRelationType == null ? null : bpRelationType.trim();
    }

    public Long getRelationBpId() {
        return this.relationBpId;
    }

    public void setRelationBpId(Long relationBpId) {
        this.relationBpId = relationBpId;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getBpRelationTypeN() {
        return bpRelationTypeN;
    }

    public void setBpRelationTypeN(String bpRelationTypeN) {
        this.bpRelationTypeN = bpRelationTypeN;
    }

    public String getBpCodeN() {
        return bpCodeN;
    }

    public void setBpCodeN(String bpCodeN) {
        this.bpCodeN = bpCodeN;
    }

    public String getAdmitStatus() {
        return admitStatus;
    }

    public void setAdmitStatus(String admitStatus) {
        this.admitStatus = admitStatus;
    }

    public String getAdmitStatusN() {
        return admitStatusN;
    }

    public void setAdmitStatusN(String admitStatusN) {
        this.admitStatusN = admitStatusN;
    }

    public String getRelationTypeN() {
        return relationTypeN;
    }

    public void setRelationTypeN(String relationTypeN) {
        this.relationTypeN = relationTypeN;
    }

    public String getRelatedBpIdN() {
        return relatedBpIdN;
    }

    public void setRelatedBpIdN(String relatedBpIdN) {
        this.relatedBpIdN = relatedBpIdN;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Long getRelatedBpId() {
        return relatedBpId;
    }

    public void setRelatedBpId(Long relatedBpId) {
        this.relatedBpId = relatedBpId;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getRelationCategory() {
        return relationCategory;
    }

    public void setRelationCategory(String relationCategory) {
        this.relationCategory = relationCategory;
    }

    public Date getTermOfAgreementFrom() {
        return termOfAgreementFrom;
    }

    public void setTermOfAgreementFrom(Date termOfAgreementFrom) {
        this.termOfAgreementFrom = termOfAgreementFrom;
    }

    public Date getTermOfAgreementTo() {
        return termOfAgreementTo;
    }

    public void setTermOfAgreementTo(Date termOfAgreementTo) {
        this.termOfAgreementTo = termOfAgreementTo;
    }

    public String getReportLink() {
        return reportLink;
    }

    public void setReportLink(String reportLink) {
        this.reportLink = reportLink;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Long getReportQueryTimes() {
        return reportQueryTimes;
    }

    public void setReportQueryTimes(Long reportQueryTimes) {
        this.reportQueryTimes = reportQueryTimes;
    }

    public String getReportToken() {
        return reportToken;
    }

    public void setReportToken(String reportToken) {
        this.reportToken = reportToken;
    }
}
