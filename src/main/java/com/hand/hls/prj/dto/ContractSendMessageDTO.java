package com.hand.hls.prj.dto;

import lombok.Data;

/**
 * @description 签章合同推送消息队列DTO
 * @author nian.liu@hand-china.com
 * @date 2022/8/24
 */
@Data
public class ContractSendMessageDTO {

    /**
     * 合同序号
     */
    private Integer serialNumber;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 签章主体
     */
    private String signatureSubject;

    /**
     * 签章人
     */
    private String sealPerson;

    /**
     * 合同状态（待签/已签）
     */
    private String contractStatus;

    /**
     * 合同已签文件（base64编码）
     */
    private String documentSignedFile;

}
