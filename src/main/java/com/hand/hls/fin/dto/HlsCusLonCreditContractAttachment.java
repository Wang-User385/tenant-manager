package com.hand.hls.fin.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "lon_credit_attachment")
public class HlsCusLonCreditContractAttachment extends BaseDTO {

    public static final String FIELD_CREDIT_ATTACHMENT_ID = "creditAttachmentId";
    public static final String FIELD_CREDIT_CONTRACT_ID = "creditContractId";
    public static final String FIELD_CREDIT_ATTACHMENT_CATEGORY = "creditAttachmentCategory";
    public static final String FIELD_DOCUMENT_NAME = "documentName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CREDIT_SOURCE_TYPE = "creditSourceType";
    public static final String FIELD_SOURCE_ID = "sourceId";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name="sequence", sequenceName="LON_CREDIT_CON_ATTACHMENT_s", initialValue=1, allocationSize=1)
    private Long creditAttachmentId; //授信额度合同附件ID

    @NotNull
    private Long creditContractId; //授信额度合同ID

    @NotEmpty
    @Length(max = 200)
    private String creditAttachmentCategory; //授信额度合同附件类别

    @Length(max = 2000)
    private String documentName; //文档名称

    @Length(max = 2000)
    private String description; //说明备注

    @Length(max = 255)
    private String creditSourceType; //附件类型

    private Long sourceId;

    /**
     * 附件编码
     */
    private String attachmentCode;

    private Long  orderNumber;

    public String getAttachmentCode() {
        return attachmentCode;
    }

    public void setAttachmentCode(String attachmentCode) {
        this.attachmentCode = attachmentCode;
    }

    public String getCreditSourceType() {
        return creditSourceType;
    }

    public void setCreditSourceType(String creditSourceType) {
        this.creditSourceType = creditSourceType;
    }

    public void setCreditAttachmentId(Long creditAttachmentId) {
        this.creditAttachmentId = creditAttachmentId;
    }

    public Long getCreditAttachmentId() {
        return creditAttachmentId;
    }

    public void setCreditContractId(Long creditContractId) {
        this.creditContractId = creditContractId;
    }

    public Long getCreditContractId() {
        return creditContractId;
    }

    public void setCreditAttachmentCategory(String creditAttachmentCategory) {
        this.creditAttachmentCategory = creditAttachmentCategory;
    }

    public String getCreditAttachmentCategory() {
        return creditAttachmentCategory;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }
}
