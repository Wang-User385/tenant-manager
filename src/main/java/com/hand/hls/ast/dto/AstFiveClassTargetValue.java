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
        name = "AST_FIVE_CLASS_TARGET_VALUE"
)
public class AstFiveClassTargetValue extends BaseDTO {
    public static final String FIELD_FIVE_CLASS_TARGET = "fiveClassTarget";
    public static final String FIELD_TARGET_VALUE = "targetValue";
    public static final String FIELD_VALUE_NAME = "valueName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    @Id
    @GeneratedValue
    private Long targetValueId;

   // @NotEmpty
    @Length(
            max = 30
    )
    private String fiveClassTarget;
    //@NotEmpty
    @Length(
            max = 255
    )
    private String targetValue;
    @Length(
            max = 255
    )
    private String valueName;
    @Length(
            max = 2000
    )
    private String description;
    @Length(
            max = 1
    )
    private String enabledFlag;

    public AstFiveClassTargetValue() {
    }

    private String fiveClassTargetName;

    public Long getTargetValueId() {
        return targetValueId;
    }

    public void setTargetValueId(Long targetValueId) {
        this.targetValueId = targetValueId;
    }

    public String getFiveClassTargetName() {
        return fiveClassTargetName;
    }

    public void setFiveClassTargetName(String fiveClassTargetName) {
        this.fiveClassTargetName = fiveClassTargetName;
    }

    public String getFiveClassTarget() {
        return this.fiveClassTarget;
    }

    public void setFiveClassTarget(String fiveClassTarget) {
        this.fiveClassTarget = fiveClassTarget;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public String getTargetValue() {
        return this.targetValue;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getValueName() {
        return this.valueName;
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
