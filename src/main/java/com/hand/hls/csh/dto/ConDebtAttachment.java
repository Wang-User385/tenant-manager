package com.hand.hls.csh.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "con_debt_attachment")
@Data
public class ConDebtAttachment extends BaseDTO {
    public static final String FIELD_DEBT_ATTCAHMENT_ID = "debtAttachmentId";
    public static final String FIELD_CONTRACT_ID = "contractId";
    public static final String FIELD_DEBT_ATTACHMENT_CATEGORY = "debtAttachmentCategory";
    public static final String FIELD_DOCUMENT_NAME = "documentName";
    public static final String FIELD_DESCRIPTION = "description";


    @Id
    @GeneratedValue
    private Long debtAttachmentId;

    private Long contractId;

    @NotEmpty
    @Length(max = 255)
    private String debtAttachmentCategory;

    @Length(max = 255)
    private String documentName; //文件名称

    @Length(max = 255)
    private String description; //备注

    @Transient
    private String fileName;

    @Transient
    private String bpType;

    @Transient
    private String yesNo;
    @Transient
    private String valueCode;
    @Transient
    private String valueName;
    @Transient
    private String uploadPerson;
    @Transient
    private Date uploadDate;

    @Transient
    private String filePath;
    @Transient
    private String attachmentId;



}
