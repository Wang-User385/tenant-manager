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
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ast_five_class_target"
)
public class AstFiveClassTarget extends BaseDTO {
    public static final String FIELD_FIVE_CLASS_TARGET = "fiveClassTarget";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_USAGE_TYPE = "usageType";
    public static final String FIELD_TARGET_TYPE = "targetType";
    public static final String FIELD_TARGET_OBJECT = "targetObject";
    public static final String FIELD_TARGET_UOM = "targetUom";
    public static final String FIELD_INCLUDE_TARGET_VALUE = "includeTargetValue";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    @Id
    @NotEmpty
    @Length(
            max = 30
    )
    private String fiveClassTarget;
    @Length(
            max = 255
    )
    private String name;
    @Length(
            max = 2000
    )
    private String description;
    @Length(
            max = 30
    )
    private String usageType;
    @Length(
            max = 30
    )
    private String targetType;
    @Length(
            max = 30
    )
    private String targetObject;
    @Length(
            max = 30
    )
    private String targetUom;
    @Length(
            max = 30
    )
    private String includeTargetValue;
    @Length(
            max = 1
    )
    private String enabledFlag;
    @Transient
    private String targetUomN;

    public AstFiveClassTarget() {
    }

    private String fiveClassTargetName;

    public String getFiveClassTargetName() {
        return fiveClassTargetName;
    }

    public void setFiveClassTargetName(String fiveClassTargetName) {
        this.fiveClassTargetName = fiveClassTargetName;
    }

    public String getTargetUomN() {
        return this.targetUomN;
    }

    public void setTargetUomN(String targetUomN) {
        this.targetUomN = targetUomN;
    }

    public void setFiveClassTarget(String fiveClassTarget) {
        this.fiveClassTarget = fiveClassTarget;
    }

    public String getFiveClassTarget() {
        return this.fiveClassTarget;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public String getUsageType() {
        return this.usageType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetType() {
        return this.targetType;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public String getTargetObject() {
        return this.targetObject;
    }

    public void setTargetUom(String targetUom) {
        this.targetUom = targetUom;
    }

    public String getTargetUom() {
        return this.targetUom;
    }

    public void setIncludeTargetValue(String includeTargetValue) {
        this.includeTargetValue = includeTargetValue;
    }

    public String getIncludeTargetValue() {
        return this.includeTargetValue;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }
}
