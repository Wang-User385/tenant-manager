package com.hand.hls.fin.dto;


import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "lon_contract_attachment")
@Getter
@Setter
public class HlsCusLonContractAttachment extends LonContractAttachment {

    public static final String FIELD_ATTACHMENT_STATUS = "attachmentStatus";
    public static final String FIELD_ATTACHMENT_TYPE = "attachmentType";
    public static final String FIELD_IS_FINAL_PRINT = "isFinalPrint";
    public static final String FIELD_STAMP_NAME = "stampName";
    public static final String FIELD_COPIES = "copies";
    public static final String FIELD_ORG_STAMP_NAME = "orgStampName";
    public static final String FIELD_NP_STAMP_NAME = "npStampName";
    public static final String FIELD_TEMPLATE_ID = "templateId";

    @Length(max = 30)
    private String attachmentStatus;

    @Length(max = 30)
    private String attachmentType;

    private String isFinalPrint;

    private String stampName;

    private String copies;

    private String orgStampName;

    private String npStampName;

    private Long templateId;


    private String attachmentCode;

    private Long  orderNumber;

    private Long withdrawId;

    public String getAttachmentStatus() {
        return attachmentStatus;
    }

    public void setAttachmentStatus(String attachmentStatus) {
        this.attachmentStatus = attachmentStatus;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getIsFinalPrint() {
        return isFinalPrint;
    }

    public void setIsFinalPrint(String isFinalPrint) {
        this.isFinalPrint = isFinalPrint;
    }

    public String getStampName() {
        return stampName;
    }

    public void setStampName(String stampName) {
        this.stampName = stampName;
    }

    public String getCopies() {
        return copies;
    }

    public void setCopies(String copies) {
        this.copies = copies;
    }

    public String getOrgStampName() {
        return orgStampName;
    }

    public void setOrgStampName(String orgStampName) {
        this.orgStampName = orgStampName;
    }

    public String getNpStampName() {
        return npStampName;
    }

    public void setNpStampName(String npStampName) {
        this.npStampName = npStampName;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    @Transient
    private String fileName;
    @Transient
    private Long fileSize;
    @Transient
    private Date uploadDate;
    //上传人
    @Transient
    private String uploadPerson;
    @Transient
    private String sourceType;
    @Transient
    private Long attachmentId;
    //是否是最新的
    @Transient
    private String updateStatus;

    @Transient
    private String fileNames;
    @Transient
    private Long fileId;
    @Transient
    private String sourceKey;
    @Transient
    private String fileType;

    @Transient
    private String filePath;

    @Transient
    private String categoryPath;

    @Transient
    private String fileSuffix;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadPerson() {
        return uploadPerson;
    }

    public void setUploadPerson(String uploadPerson) {
        this.uploadPerson = uploadPerson;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getAttachmentCode() {
        return attachmentCode;
    }

    public void setAttachmentCode(String attachmentCode) {
        this.attachmentCode = attachmentCode;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }
}
