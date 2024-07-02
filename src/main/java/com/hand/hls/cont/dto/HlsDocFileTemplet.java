//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.cont.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "hls_doc_file_templet"
)
public class HlsDocFileTemplet extends BaseDTO {
    @Id
    @GeneratedValue
    private Long templetId;
    @NotEmpty
    private String templetCode;
    private String templetName;
    private String templetType;
    @NotEmpty
    private String usageCode;
    private String note;
    private String usageCategory;
    @Transient
    private String usageCategoryDesc;
    private String enabledFlag;
    @Transient
    private String queryCondition;
    private String usageClass;
    @Transient
    private String usageClassDesc;
    public HlsDocFileTemplet() {
    }

    public String getUsageClassDesc() {
        return usageClassDesc;
    }

    public void setUsageClassDesc(String usageClassDesc) {
        this.usageClassDesc = usageClassDesc;
    }

    public String getUsageClass() {
        return usageClass;
    }

    public void setUsageClass(String usageClass) {
        this.usageClass = usageClass;
    }

    public String getTempletType() {
        return this.templetType;
    }

    public void setTempletType(String templetType) {
        this.templetType = templetType;
    }

    public String getUsageCategory() {
        return this.usageCategory;
    }

    public void setUsageCategory(String usageCategory) {
        this.usageCategory = usageCategory;
    }

    public String getQueryCondition() {
        return this.queryCondition;
    }

    public String getUsageCategoryDesc() {
        return this.usageCategoryDesc;
    }

    public void setUsageCategoryDesc(String usageCategoryDesc) {
        this.usageCategoryDesc = usageCategoryDesc;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public String getTempletName() {
        return this.templetName;
    }

    public void setTempletName(String templetName) {
        this.templetName = templetName;
    }

    public String getTempletCode() {
        return this.templetCode;
    }

    public void setTempletCode(String templetCode) {
        this.templetCode = templetCode;
    }

    public Long getTempletId() {
        return this.templetId;
    }

    public void setTempletId(Long templetId) {
        this.templetId = templetId;
    }

    public String getUsageCode() {
        return this.usageCode;
    }

    public void setUsageCode(String usageCode) {
        this.usageCode = usageCode;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String toString() {
        return "HLSDocFileTemplet{templetId=" + this.templetId + ", templetCode='" + this.templetCode + '\'' + ", templetName='" + this.templetName + '\'' + ", usageCode='" + this.usageCode + '\'' + ", note='" + this.note + '\'' + ", enabledFlag='" + this.enabledFlag + '\'' + '}';
    }
}
