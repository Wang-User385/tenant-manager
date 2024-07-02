package com.hand.hls.prj.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * description
 *
 * @author shigure 2022/12/23 11:27
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignatureVerifyDTO {
    /**
     *唯一性标识
     */
    @JSONField(name = "uniqueIdentification")
    private String uniqueId;
    /**
     *单据编号
     */
    private String orderNum;
    /**
     *业务类型
     */
    private String businessType;
    /**
     *业务场景
     */
    private String businessScene;
    /**
     *企业名称
     */
    private String companyName;
    /**
     *企业证件类型
     */
    private String companyDocumentType;
    /**
     *企业证件类型
     */
    private String documentNumber;
    /**
     *法人姓名
     */
    private String personName;
    /**
     *法人证件类型
     */
    private String documentType;
    /**
     *法人证件号
     */
    private String idNumber;
    /**
     *手机号
     */
    private String phoneNumber;
    /**
     *接收人手机号
     */
    private String receivePhoneNumber;

    private String codeFlag;

    private String faceFlag;

    private String digitalCertificateFlag;

    private String elementType;

    private String elementVerifyCount;

    private String faceCount;

    private String companyVerifyCount;

    private String companyElementVerifyCount;

    private String legalCertificateFlag;
}
