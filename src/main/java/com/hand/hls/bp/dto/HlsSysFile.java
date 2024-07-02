//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package com.hand.hls.bp.dto;
import com.hand.hap.system.dto.BaseDTO;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

public class HlsSysFile extends BaseDTO {
    @Id
    @GeneratedValue
    private Long fileId;
    private Long attachmentId;
    private String fileName;
    private String filePath;
    private BigDecimal fileSize;
    private String fileType;
    private Date uploadDate;
    @Transient
    private Long bpId;
    @Transient
    private String sourceType;
    @Transient
    private String sourceKey;
    @Transient
    private Long contractAttachmentId;
    @Transient
    private String conTemplate;
    @Transient
    private String documentName;
    @Transient
    private String bpName;
    @Transient
    private String bpTypeDesc;

    public HlsSysFile() {
    }

    public Long getContractAttachmentId() {
        return this.contractAttachmentId;
    }

    public void setContractAttachmentId(Long contractAttachmentId) {
        this.contractAttachmentId = contractAttachmentId;
    }

    public String getConTemplate() {
        return this.conTemplate;
    }

    public void setConTemplate(String conTemplate) {
        this.conTemplate = conTemplate;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getBpName() {
        return this.bpName;
    }

    public void setBpName(String bpName) {
        this.bpName = bpName;
    }

    public String getBpTypeDesc() {
        return this.bpTypeDesc;
    }

    public void setBpTypeDesc(String bpTypeDesc) {
        this.bpTypeDesc = bpTypeDesc;
    }

    public String getSourceKey() {
        return this.sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getAttachmentId() {
        return this.attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
    }

    public BigDecimal getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
    }

    public Date getUploadDate() {
        return this.uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
