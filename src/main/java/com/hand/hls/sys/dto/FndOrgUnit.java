//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.sys.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fnd_org_unit"
)
public class FndOrgUnit extends BaseDTO {
    public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_UNIT_CODE = "unitCode";
    public static final String FIELD_UNIT_NAME = "unitName";
    public static final String FIELD_UNIT_TYPE = "unitType";
    public static final String FIELD_PARENT_UNIT_ID = "parentUnitId";
    public static final String FIELD_CHIEF_POSITION_ID = "chiefPositionId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_COMPANY_ID = "companyId";
    private Long companyId;
    @Id
    @GeneratedValue
    private Long unitId;
    @NotEmpty
    @Length(
            max = 100
    )
    private String unitCode;
    @Length(
            max = 2000
    )
    private String unitName;
    @Length(
            max = 100
    )
    private String unitType;
    private Long parentUnitId;
    private Long chiefPositionId;
    @Length(
            max = 1
    )
    private String enabledFlag;
    @Transient
    private String parentName;
    @Transient
    private String companyName;
    @Transient
    private String unitTypeName;
    @Transient
    private Long mgrEmployeeId;
    @Transient
    private String mgrEmployeeName;

    private String easCode;

    public FndOrgUnit() {
    }

    public String getEasCode() {
        return easCode;
    }

    public void setEasCode(String easCode) {
        this.easCode = easCode;
    }

    public String getParentName() {
        return this.parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return this.unitId;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitCode() {
        return this.unitCode;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnitType() {
        return this.unitType;
    }

    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }

    public Long getParentUnitId() {
        return this.parentUnitId;
    }

    public void setChiefPositionId(Long chiefPositionId) {
        this.chiefPositionId = chiefPositionId;
    }

    public Long getChiefPositionId() {
        return this.chiefPositionId;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEnabledFlag() {
        return this.enabledFlag;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUnitTypeName() {
        return this.unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public Long getMgrEmployeeId() {
        return this.mgrEmployeeId;
    }

    public void setMgrEmployeeId(Long mgrEmployeeId) {
        this.mgrEmployeeId = mgrEmployeeId;
    }

    public String getMgrEmployeeName() {
        return this.mgrEmployeeName;
    }

    public void setMgrEmployeeName(String mgrEmployeeName) {
        this.mgrEmployeeName = mgrEmployeeName;
    }
}
