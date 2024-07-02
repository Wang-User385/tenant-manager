package com.hand.hls.sign.dto;


import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

/**
 * description
 *
 * @author shigure 2022/11/28 15:37
 */
@ExtensionAttribute(disable=true)
@Data
@Table(name = "prj_sign_contract")
public class SignContract extends BaseDTO {
    @Id
    @GeneratedValue
    private Long contractId;

    private Long signId;

    private Integer serialNumber;

    private String contractCode;

    private String contractName;

    private String signatureSubject;

    private String sealPerson;

    private String contractStatus;

    private String contractSignType;

    private Long signedAttachmentId;

    private Long signedAttachmentId2;

    private Long signedAttachmentId3;

    @Transient
    private String documentSignedFile;

}
