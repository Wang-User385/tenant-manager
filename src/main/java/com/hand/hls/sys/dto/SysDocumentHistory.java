package com.hand.hls.sys.dto;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(
        name = "sys_document_history"
)
public class SysDocumentHistory implements Serializable {
    public static final String FIELD_DOCUMENT_ID = "documentId";
    public static final String FIELD_DOCUMENT_CATEGORY = "documentCategory";
    @Column(
            name = "history_id"
    )
    @Id
    @GeneratedValue
    private Long historyId;
    @Transient
    @Column(
            name = "history_detail_id"
    )
    private Long historyDetailId;
    @Column(
            name = "document_id"
    )
    private Long documentId;
    @Column(
            name = "document_category"
    )
    private String documentCategory;

    @Column(
            name = "version"
    )
    private Long version;
    @Column(
            name = "OBJECT_VERSION_NUMBER"
    )
    private Long objectVersionNumber;
    @Column(
            name = "REQUEST_ID"
    )
    private Long requestId;
    @Column(
            name = "PROGRAM_ID"
    )
    private Long programId;
    @Column(
            name = "CREATED_BY"
    )
    private Long createdBy;
    @Column(
            name = "CREATION_DATE"
    )
    private Date creationDate;
    @Column(
            name = "LAST_UPDATED_BY"
    )
    private Long lastUpdatedBy;
    @Column(
            name = "LAST_UPDATE_DATE"
    )
    private Date lastUpdateDate;
    @Column(
            name = "LAST_UPDATE_LOGIN"
    )
    private Long lastUpdateLogin;
    private static final long serialVersionUID = 1L;

    public SysDocumentHistory() {
    }

    public Long getHistoryId() {
        return this.historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getHistoryDetailId() {
        return this.historyDetailId;
    }

    public void setHistoryDetailId(Long historyDetailId) {
        this.historyDetailId = historyDetailId;
    }

    public Long getDocumentId() {
        return this.documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentCategory() {
        return this.documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getObjectVersionNumber() {
        return this.objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getRequestId() {
        return this.requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getProgramId() {
        return this.programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getLastUpdateLogin() {
        return this.lastUpdateLogin;
    }

    public void setLastUpdateLogin(Long lastUpdateLogin) {
        this.lastUpdateLogin = lastUpdateLogin;
    }
}
