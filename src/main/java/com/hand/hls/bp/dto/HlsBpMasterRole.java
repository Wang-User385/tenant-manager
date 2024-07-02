//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
    disable = true
)
@Table(
    name = "hls_bp_master_role"
)
public class HlsBpMasterRole extends BaseDTO {
    @Id
    private Long bpId;
//    @Id
    private String bpType;
    private String bpCategory;
    private String enabledFlag;
    @Transient
    private String typeDescription;
    @Transient
    private String categoryDescription;

    public HlsBpMasterRole() {
    }

    public String getTypeDescription() {
        return this.typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getCategoryDescription() {
        return this.categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getBpType() {
        return this.bpType;
    }

    public void setBpType(String bpType) {
        this.bpType = bpType;
    }

    public String getBpCategory() {
        return this.bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory == null ? null : bpCategory.trim();
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag == null ? null : enabledFlag.trim();
    }
}
