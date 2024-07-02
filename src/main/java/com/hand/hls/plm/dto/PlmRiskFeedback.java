package com.hand.hls.plm.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "plm_risk_feedback")
public class PlmRiskFeedback extends BaseDTO {
    public static final String FEEDBACK_ID = "feedback_id";
    public static final String RISK_WARNING_ID = "risk_warning_id";
    public static final String PLM_ATTACHMENT_ID = "plm_attachment_id";
    public static final String FEEDBACK_DATE = "feedback_date";
    public static final String RISK_LEVEL = "risk_level";
    public static final String RISK_INFO = "risk_info";
    public static final String FEEDBACK_INFO = "feedback_info";
    public static final String FEEDBACK_OPINION = "feedback_opinion";

    @Id
    @GeneratedValue
    private Long feedbackId;

    @NotNull
    private Long riskWarningId;
    private Long plmAttachmentId;
    private Date feedbackDate;

    @Length(max = 100)
    private String riskLevel;

    @Length(max = 100)
    private String riskInfo;

    @Length(max = 2000)
    private String feedbackInfo;

    @Length(max = 2000)
    private String feedbackOpinion;

    /**
     * 来源plm_attachment
     */
    @Transient
    private String documentName;
    @Transient
    private String plmSourceType;
    @Transient
    private String fileName;
    @Transient
    private Long plmId;
    @Transient
    private String description;
    @Transient
    private String plmAttachmentCategory;
    @Transient
    private String sourceType;
    @Transient
    private String plmType;
    @Transient
    private String changeIq;

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Long getRiskWarningId() {
        return riskWarningId;
    }

    public void setRiskWarningId(Long riskWarningId) {
        this.riskWarningId = riskWarningId;
    }

    public Long getPlmAttachmentId() {
        return plmAttachmentId;
    }

    public void setPlmAttachmentId(Long plmAttachmentId) {
        this.plmAttachmentId = plmAttachmentId;
    }

    public Date getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(Date feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskInfo() {
        return riskInfo;
    }

    public void setRiskInfo(String riskInfo) {
        this.riskInfo = riskInfo;
    }

    public String getFeedbackInfo() {
        return feedbackInfo;
    }

    public void setFeedbackInfo(String feedbackInfo) {
        this.feedbackInfo = feedbackInfo;
    }

    public String getFeedbackOpinion() {
        return feedbackOpinion;
    }

    public void setFeedbackOpinion(String feedbackOpinion) {
        this.feedbackOpinion = feedbackOpinion;
    }


    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getPlmSourceType() {
        return plmSourceType;
    }

    public void setPlmSourceType(String plmSourceType) {
        this.plmSourceType = plmSourceType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getPlmId() {
        return plmId;
    }

    public void setPlmId(Long plmId) {
        this.plmId = plmId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlmAttachmentCategory() {
        return plmAttachmentCategory;
    }

    public void setPlmAttachmentCategory(String plmAttachmentCategory) {
        this.plmAttachmentCategory = plmAttachmentCategory;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getPlmType() {
        return plmType;
    }

    public void setPlmType(String plmType) {
        this.plmType = plmType;
    }

    public String getChangeIq() {
        return changeIq;
    }

    public void setChangeIq(String changeIq) {
        this.changeIq = changeIq;
    }
}
