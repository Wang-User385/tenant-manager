//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ast_five_class_rule"
)
public class AstFiveClassRule extends BaseDTO {
    public static final String FIELD_FIVE_CLASS_RULE_ID = "fiveClassRuleId";
    public static final String FIELD_USAGE_TYPE = "usageType";
    public static final String FIELD_FIVE_CLASS_RULE = "fiveClassRule";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    @Id
    @GeneratedValue
    private Long fiveClassRuleId;
    @NotEmpty
    @Length(
            max = 30
    )
    private String usageType;
    @Length(
            max = 30
    )
    private String fiveClassRule;
    @Length(
            max = 2000
    )
    private String description;
    @Length(
            max = 1
    )
    private String enabledFlag;

    public AstFiveClassRule() {
    }

    public void setFiveClassRuleId(Long fiveClassRuleId) {
        this.fiveClassRuleId = fiveClassRuleId;
    }

    public Long getFiveClassRuleId() {
        return this.fiveClassRuleId;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public String getUsageType() {
        return this.usageType;
    }

    public void setFiveClassRule(String fiveClassRule) {
        this.fiveClassRule = fiveClassRule;
    }

    public String getFiveClassRule() {
        return this.fiveClassRule;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }
}
