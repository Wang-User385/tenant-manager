package com.hand.hls.prj.dto;

import java.util.Date;
import lombok.Data;

/**
 * @description 个人实名认证推送消息队列DTO
 * @author nian.liu@hand-china.com
 * @date 2022/8/12
 */
@Data
public class CompanyAuthMessageDTO {

    /**
     * 系统编码+系统唯一性标识流水号
     */
    private String uniqueIdentification;

    /**
     * 单据编号
     */
    private String orderNum;

    /**
     * 认证人员姓名
     */
    private String personName;

    /**
     * 业务系统名称
     */
    private String businessType;

    /**
     * 证件类型（只支持身份证，传 01）
     */
    private String documentType;

    /**
     * 认证人员的身份证号
     */
    private String idNumber;

    /**
     * 认证结果状态(成功：SUCCESS，失败：ERROR)
     */
    private String resultStatus;

    /**
     * 认证结果描述
     */
    private String resultDesc;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业证件类型（营业执照）
     */
    private String companyDocumentType;

    /**
     * 企业证件编码（统一社会信用码）
     */
    private String documentNumber;

    /**
     * 认证时间
     */
    private Date certificationTime;

    /**
     * 企业数字证书编号
     */
    private String sealCode;

    /**
     * 法人数字证书编号
     */
    private String legalSealCode;

}
