//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.Length;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "AST_PATROL"
)
public class HlsCusAstPatrol extends BaseDTO {
    @Id
    @GeneratedValue
    private Long patrolId;
    private String customerCode;
    private String patrolModeCode;
    private Date patrolDate;
    @Length(
            max = 2000
    )
    private String patrolText;
    @Length(
            max = 200
    )
    private String patrolPersons;
    @Transient
    private Date lastUpdateDate;
    @Transient
    private List<String> queryList;
    @Transient
    private List<String> typeQueryList;
    @Transient
    private String customerName;
    @Transient
    private String customerType;
    @Transient
    private String customerTypeCode;
    @Transient
    private String patrolYear;
    @Transient
    private String count;
    @Transient
    private String patrolModeDesc;
    @Transient
    private List<HlsCusPatrolAttachment> patrolAttachments;
    @Transient
    private Long patrolAttachmentId;
    @Transient
    private String patrolAttachmentCategory;
    @Transient
    private String documentName;
    @Transient
    private String description;
    @Transient
    private String fileNames;
    @Transient
    private String fileId;
    @Transient
    private String sourceKey;
    @Transient
    private String fileType;
    @Transient
    private String patrolModeTypeCode;
    @Transient
    private String desc;
    @Transient
    private String patrolModeCodeN;
    @Transient
    private String patrolModeCodeMeaning;
    @Transient
    private String projectNumber;
    @Transient
    private String projectName;

    public HlsCusAstPatrol() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumberN) {
        this.projectNumber = projectNumberN;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<HlsCusPatrolAttachment> getPatrolAttachments() {
        return this.patrolAttachments;
    }

    public void setPatrolAttachments(List<HlsCusPatrolAttachment> patrolAttachments) {
        this.patrolAttachments = patrolAttachments;
    }

    public String getPatrolModeDesc() {
        return this.patrolModeDesc;
    }

    public void setPatrolModeDesc(String patrolModeDesc) {
        this.patrolModeDesc = patrolModeDesc;
    }

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPatrolYear() {
        return this.patrolYear;
    }

    public void setPatrolYear(String patrolYear) {
        this.patrolYear = patrolYear;
    }

    public List<String> getTypeQueryList() {
        return this.typeQueryList;
    }

    public void setTypeQueryList(List<String> typeQueryList) {
        this.typeQueryList = typeQueryList;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<String> getQueryList() {
        return this.queryList;
    }

    public void setQueryList(List<String> queryList) {
        this.queryList = queryList;
    }

    public String getCustomerType() {
        return this.customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setPatrolId(Long patrolId) {
        this.patrolId = patrolId;
    }

    public Long getPatrolId() {
        return this.patrolId;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerCode() {
        return this.customerCode;
    }

    public void setPatrolModeCode(String patrolModeCode) {
        this.patrolModeCode = patrolModeCode;
    }

    public String getPatrolModeCode() {
        return this.patrolModeCode;
    }

    public Date getPatrolDate() {
        return this.patrolDate;
    }

    public void setPatrolDate(Date patrolDate) {
        this.patrolDate = patrolDate;
    }

    public void setPatrolText(String patrolText) {
        this.patrolText = patrolText;
    }

    public String getPatrolText() {
        return this.patrolText;
    }

    public void setPatrolPersons(String patrolPersons) {
        this.patrolPersons = patrolPersons;
    }

    public String getPatrolPersons() {
        return this.patrolPersons;
    }

    public Long getPatrolAttachmentId() {
        return this.patrolAttachmentId;
    }

    public void setPatrolAttachmentId(Long patrolAttachmentId) {
        this.patrolAttachmentId = patrolAttachmentId;
    }

    public String getPatrolAttachmentCategory() {
        return this.patrolAttachmentCategory;
    }

    public void setPatrolAttachmentCategory(String patrolAttachmentCategory) {
        this.patrolAttachmentCategory = patrolAttachmentCategory;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileNames() {
        return this.fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSourceKey() {
        return this.sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getCustomerTypeCode() {
        return this.customerTypeCode;
    }

    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    public String getPatrolModeTypeCode() {
        return this.patrolModeTypeCode;
    }

    public void setPatrolModeTypeCode(String patrolModeTypeCode) {
        this.patrolModeTypeCode = patrolModeTypeCode;
    }

    public String getPatrolModeCodeN() {
        return this.patrolModeCodeN;
    }

    public void setPatrolModeCodeN(String patrolModeCodeN) {
        this.patrolModeCodeN = patrolModeCodeN;
    }

    public String getPatrolModeCodeMeaning() {
        return this.patrolModeCodeMeaning;
    }

    public void setPatrolModeCodeMeaning(String patrolModeCodeMeaning) {
        this.patrolModeCodeMeaning = patrolModeCodeMeaning;
    }
}
