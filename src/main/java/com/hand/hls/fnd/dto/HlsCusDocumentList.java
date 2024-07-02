package com.hand.hls.fnd.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description:附件清单
 * @Author: wty
 * @Date: Created in 0:15 2018/4/16
 */
@ExtensionAttribute(
        disable = true
)
@Table(
        name = "sys_document_list"
)
public class HlsCusDocumentList extends BaseDTO {

    @Id
    @GeneratedValue
    private Long documentId;

    private String documentType;

    private String documentCategory;

    private String enabledFlag;

    private String systemFlag;

    private String documentListName;

    private Long serialNumber ; // add by sqx 2017.12.30 序号

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getSystemFlag() {
        return systemFlag;
    }

    public void setSystemFlag(String systemFlag) {
        this.systemFlag = systemFlag;
    }

    public String getDocumentListName() {
        return documentListName;
    }

    public void setDocumentListName(String documentListName) {
        this.documentListName = documentListName;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }
}
