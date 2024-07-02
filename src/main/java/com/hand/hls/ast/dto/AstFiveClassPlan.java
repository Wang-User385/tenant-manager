//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.ast.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "ast_five_class_plan"
)
public class AstFiveClassPlan extends BaseDTO {
    public static final String FIELD_FIVE_CLASS_PLAN = "fiveClassPlan";
    public static final String FIELD_USAGE_TYPE = "usageType";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_CALCULATE_OBJECT = "calculateObject";
    public static final String FIELD_UPDATE_SYSTEM_METHOD = "updateSystemMethod";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    @Id
    private String fiveClassPlan;
    @NotEmpty
    @Length(
            max = 30
    )
    private String usageType;
    @Length(
            max = 2000
    )
    private String description;
    @Length(
            max = 30
    )
    private String calculateObject;
    @Length(
            max = 30
    )
    private String updateSystemMethod;
    @Length(
            max = 1
    )
    private String enabledFlag;

    public AstFiveClassPlan() {
    }

    public void setFiveClassPlan(String fiveClassPlan) {
        this.fiveClassPlan = fiveClassPlan;
    }

    public String getFiveClassPlan() {
        return this.fiveClassPlan;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public String getUsageType() {
        return this.usageType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCalculateObject(String calculateObject) {
        this.calculateObject = calculateObject;
    }

    public String getCalculateObject() {
        return this.calculateObject;
    }

    public void setUpdateSystemMethod(String updateSystemMethod) {
        this.updateSystemMethod = updateSystemMethod;
    }

    public String getUpdateSystemMethod() {
        return this.updateSystemMethod;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }
}
