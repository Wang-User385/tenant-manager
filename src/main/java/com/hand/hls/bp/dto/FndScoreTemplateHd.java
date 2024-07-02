//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fnd_score_template_hd"
)
public class FndScoreTemplateHd extends BaseDTO {
    @Id
    @GeneratedValue
    private Long scoreTemplateHdId;
    private Long companyId;
    private String scoreTemplateHdCode;
    private String scoreTemplateHdName;
    private Long scoreTemplateTypeId;
    @Transient
    private String scoreTemplateTypeName;
    private String calcMethod;
    private String enabledFlag;
    private String financialFlag;

    public String getFinancialFlag() {
        return financialFlag;
    }

    public void setFinancialFlag(String financialFlag) {
        this.financialFlag = financialFlag;
    }

    public FndScoreTemplateHd() {
    }

    public String getScoreTemplateTypeName() {
        return this.scoreTemplateTypeName;
    }

    public void setScoreTemplateTypeName(String scoreTemplateTypeName) {
        this.scoreTemplateTypeName = scoreTemplateTypeName;
    }

    public void setScoreTemplateHdId(Long scoreTemplateHdId) {
        this.scoreTemplateHdId = scoreTemplateHdId;
    }

    public Long getScoreTemplateHdId() {
        return this.scoreTemplateHdId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setScoreTemplateHdCode(String scoreTemplateHdCode) {
        this.scoreTemplateHdCode = scoreTemplateHdCode;
    }

    public String getScoreTemplateHdCode() {
        return this.scoreTemplateHdCode;
    }

    public void setScoreTemplateHdName(String scoreTemplateHdName) {
        this.scoreTemplateHdName = scoreTemplateHdName;
    }

    public String getScoreTemplateHdName() {
        return this.scoreTemplateHdName;
    }

    public void setScoreTemplateTypeId(Long scoreTemplateTypeId) {
        this.scoreTemplateTypeId = scoreTemplateTypeId;
    }

    public Long getScoreTemplateTypeId() {
        return this.scoreTemplateTypeId;
    }

    public void setCalcMethod(String calcMethod) {
        this.calcMethod = calcMethod;
    }

    public String getCalcMethod() {
        return this.calcMethod;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }
}
