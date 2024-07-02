package com.hand.hls.sign.dto;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

@Data
@ExtensionAttribute(disable=true)
@Table(name = "prj_sign_verify")
public class SignVerify extends BaseDTO {
    @Id
    @GeneratedValue
    private Long verifyId;

    /**
     *唯一性标识
     */
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
    /**
     *复核时间
     */
    private Date checkTime;
    /**
     *经销商开户时间
     */
    private Date openTime;
    /**
     *法人开户时间
     */
    private Date legalOpenTime;
    /**
     *签约启用标识
     */
    private String signEnabledFlag;
    /**
     *复核状态
     */
    private String signCheckStatus;
    /**
     *操作用户ID
     */
    private Long operateUserId;
    /**
     *商业伙伴ID
     */
    private Long bpId;

    /**
     * 认证结果
     */
    private String verifyResult;

    /**
     * 企业数字证书编号
     */
    private String sealCode;

    /**
     * 法人数字证书编号
     */
    private String legalSealCode;
    /**
     * 认证URL
     */
    private String verifyUrl;

    @Transient
    private String bpCode;

    @Transient
    private String bpName;

    @Transient
    private String bpClass;

    @Transient
    private String registerCertNum;

    @Transient
    private String bpCategory;

    @Transient
    private String bpCategoryN;

    @Transient
    private String bpType;

    @Transient
    private String bpTypeN;

    @Transient
    private String bpAttachmentId;

    @Transient
    private String checkTimeFormat;

    @Transient
    private String checkTimeFrom;

    @Transient
    private String checkTimeTo;

    @Transient
    private String openTimeFormat;

    @Transient
    private String openTimeForm;

    @Transient
    private String openTimeTo;

    @Transient
    private String legalOpenTimeFormat;

    @Transient
    private String legalOpenTimeForm;

    @Transient
    private String legalOpenTimeTo;

    @Transient
    private String venderName;

    @Transient
    private String operateUserIdN;

    @Transient
    private String signEnabledFlagN;

    @Transient
    private String unitName;

    @Transient
    private String signCheckStatusN;
}
