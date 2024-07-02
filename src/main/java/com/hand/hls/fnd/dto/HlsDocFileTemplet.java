package com.hand.hls.fnd.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@ExtensionAttribute(disable=true)
@Table(name="hls_doc_file_templet")
public class HlsDocFileTemplet extends BaseDTO {

    @Id
    @GeneratedValue
    private Long templetId;
    @NotEmpty
    private String templetCode;
    private String templetName;
    private String templetType;
    private String templetDesc;
    @NotEmpty
    private String usageCode;
    private String note;
    private String usageCategory;
    private String enabledFlag;
    @Transient
    private String queryCondition;
    @Transient
    private String TempletNameStr;

    @Transient
    private String EBFIFandEBFIL;

    public String getTempletType() {
        return templetType;
    }

    public void setTempletType(String templetType) {
        this.templetType = templetType;
    }

    public String getUsageCategory() {
        return usageCategory;
    }

    public void setUsageCategory(String usageCategory) {
        this.usageCategory = usageCategory;
    }

    public String getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public String getTempletName() {
        return templetName;
    }

    public void setTempletName(String templetName) {
        this.templetName = templetName;
    }

    public String getTempletCode() {
        return templetCode;
    }

    public void setTempletCode(String templetCode) {
        this.templetCode = templetCode;
    }

    public Long getTempletId() {
        return templetId;
    }

    public void setTempletId(Long templetId) {
        this.templetId = templetId;
    }

    public String getTempletDesc() {
        return templetDesc;
    }

    public void setTempletDesc(String templetDesc) {
        this.templetDesc = templetDesc;
    }

    public String getUsageCode() {
        return usageCode;
    }

    public void setUsageCode(String usageCode) {
        this.usageCode = usageCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEBFIFandEBFIL() {
        return EBFIFandEBFIL;
    }

    public void setEBFIFandEBFIL(String EBFIFandEBFIL) {
        this.EBFIFandEBFIL = EBFIFandEBFIL;
    }

    public void setTempletNameStr(String templetNameStr) {
        TempletNameStr = templetNameStr;
    }

    public String getTempletNameStr() {
        return TempletNameStr;
    }

    @Override
    public String toString() {
        return "HLSDocFileTemplet{" +
                "templetId=" + templetId +
                ", templetCode='" + templetCode + '\'' +
                ", templetName='" + templetName + '\'' +
                ", usageCode='" + usageCode + '\'' +
                ", note='" + note + '\'' +
                ", enabledFlag='" + enabledFlag + '\'' +
                '}';
    }
}
