package com.hand.hls.fin.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "LON_CONTRACT_INVOICE")
@Getter
@Setter
public class HlsCusLonContractInvoice extends BaseDTO {

    @Id
    @GeneratedValue
    private Long invoiceId;

    private String invoiceCode;
    private String invoiceNumber;
    private String invoiceDescription;
    private String invoiceFlag;
    private String projectCashFlow;
    private String invoiceUrl;
    private Long contractId;
    private String contractNumber;
    private Long batchId;
    private String batchNumber;

    @Transient
    private String invoiceFlagN;

    @Transient
    private String fileName;
    @Transient
    private Long fileSize;
    @Transient
    private Date uploadDate;
    //上传人
    @Transient
    private String uploadPerson;
    @Transient
    private String sourceType;
    @Transient
    private Long attachmentId;
    //是否是最新的
    @Transient
    private String updateStatus;
    @Transient
    private String filePath;
    @Transient
    private String categoryPath;
    @Transient
    private String fileSuffix;
}
