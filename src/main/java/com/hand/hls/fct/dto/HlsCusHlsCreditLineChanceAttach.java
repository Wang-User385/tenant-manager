package com.hand.hls.fct.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author Qian Yuanfeng
 * @date 2020/5/5 - 16:39
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ExtensionAttribute(disable = true)
@Table(name = "hls_credit_line_attach")
public class HlsCusHlsCreditLineChanceAttach extends BaseDTO {
    @Id
    @GeneratedValue
    private Long chanceAttachmentId;

    private Long chanceId;

    private Long sourceId;

    private String documentName;

    private String description;

    @Transient
    private String  fileName;

    @Transient
    private String filePath;

    @Transient
    private String  attachmentId;
    @Transient
    private Date uploadDate;
    @Transient
    private String uploadPerson;

    private String status;

    private String attachmentCategory;
    private String sourceType;

    private Long createdBy;

    @Transient
    private String createdByN;

    @Transient
    private String infoName;

    @Transient
    private Date  creationDate;

    @Transient
    private Long fileNum;


}
