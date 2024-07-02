//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ast_five_class_code"
)
public class AstFiveClassCode extends BaseDTO {
    public static final String FIELD_FIVE_CLASS_CODE = "fiveClassCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_FIVE_CLASS_PLAN = "fiveClassPlan";
    public static final String FIELD_PRIORITY = "priority";
    public static final String FIELD_FIVE_CLASS_RULE_ID = "fiveClassRuleId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    @Id
    @NotEmpty
    private String fiveClassCode;
    @Length(
            max = 2000
    )
    private String description;
    @NotEmpty
    @Length(
            max = 30
    )
    private String fiveClassPlan;
    @NotNull
    private Long priority;
    private Long fiveClassRuleId;
    @Length(
            max = 1
    )
    private String enabledFlag;
    @Transient
    private String usageType;
    private Double breakRate;

    public AstFiveClassCode() {
    }

    public String getUsageType() {
        return this.usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public void setFiveClassCode(String fiveClassCode) {
        this.fiveClassCode = fiveClassCode;
    }

    public String getFiveClassCode() {
        return this.fiveClassCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setFiveClassPlan(String fiveClassPlan) {
        this.fiveClassPlan = fiveClassPlan;
    }

    public String getFiveClassPlan() {
        return this.fiveClassPlan;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Long getPriority() {
        return this.priority;
    }

    public void setFiveClassRuleId(Long fiveClassRuleId) {
        this.fiveClassRuleId = fiveClassRuleId;
    }

    public Long getFiveClassRuleId() {
        return this.fiveClassRuleId;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public Double getBreakRate() {
        return breakRate;
    }

    public void setBreakRate(Double breakRate) {
        this.breakRate = breakRate;
    }
}
