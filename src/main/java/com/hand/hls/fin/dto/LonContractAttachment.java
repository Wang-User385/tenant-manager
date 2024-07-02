package com.hand.hls.fin.dto;


import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "lon_contract_attachment"
)
public class LonContractAttachment extends BaseDTO {
    public static final String FIELD_CONTRACT_ATTACHMENT_ID = "contractAttachmentId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_SOURCE_TYPE = "sourceType";
    public static final String FIELD_SOURCE_ID = "sourceId";
    public static final String FIELD_BP_CATEGORY = "bpCategory";
    public static final String FIELD_BP_ID = "bpId";
    public static final String FIELD_DOCUMENT_NAME = "documentName";
    public static final String FIELD_DESCRIPTION = "description";
    @Id
    @GeneratedValue
    private Long contractAttachmentId;
    @NotNull
    private Long contractId;
    @Length(
            max = 100
    )
    private String sourceType;
    private Long sourceId;
    @Length(
            max = 100
    )
    private String bpCategory;
    private Long bpId;
    @Length(
            max = 2000
    )
    private String documentName;
    @Length(
            max = 2000
    )
    private String description;
    @Transient
    private String fileNames;

    public LonContractAttachment() {
    }

    public String getFileNames() {
        return this.fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public void setContractAttachmentId(Long contractAttachmentId) {
        this.contractAttachmentId = contractAttachmentId;
    }

    public Long getContractAttachmentId() {
        return this.contractAttachmentId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getContractId() {
        return this.contractId;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getSourceId() {
        return this.sourceId;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public String getBpCategory() {
        return this.bpCategory;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}

