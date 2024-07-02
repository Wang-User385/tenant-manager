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

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fnd_score_template_type"
)
public class FndScoreTempletType extends BaseDTO {
    public static final String FIELD_SCORE_TEMPLATE_TYPE_ID = "scoreTemplateTypeId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_SCORE_TEMPLATE_TYPE_CODE = "scoreTemplateTypeCode";
    public static final String FIELD_SCORE_TEMPLATE_TYPE_NAME = "scoreTemplateTypeName";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    @Id
    @GeneratedValue
    private Long scoreTemplateTypeId;
    private Long companyId;
    private String scoreTemplateTypeCode;
    private String scoreTemplateTypeName;
    private String enabledFlag;

    public FndScoreTempletType() {
    }

    public void setScoreTemplateTypeId(Long scoreTemplateTypeId) {
        this.scoreTemplateTypeId = scoreTemplateTypeId;
    }

    public Long getScoreTemplateTypeId() {
        return this.scoreTemplateTypeId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setScoreTemplateTypeCode(String scoreTemplateTypeCode) {
        this.scoreTemplateTypeCode = scoreTemplateTypeCode;
    }

    public String getScoreTemplateTypeCode() {
        return this.scoreTemplateTypeCode;
    }

    public void setScoreTemplateTypeName(String scoreTemplateTypeName) {
        this.scoreTemplateTypeName = scoreTemplateTypeName;
    }

    public String getScoreTemplateTypeName() {
        return this.scoreTemplateTypeName;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }
}
