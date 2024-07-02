//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.sys.dto;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
    disable = true
)
@Table(
    name = "sys_user"
)
public class SysUser extends BaseDTO {
    @Id
    @GeneratedValue
    private Long userId;
    private Long userType;
    @Condition(
        operator = "LIKE"
    )
    private String userName;
    @Condition(
        operator = "LIKE"
    )
    private String description;
    private String passwordEncrypted;
    private String email;
    private String phone;
    private Date startActiveDate;
    private Date endActiveDate;
    private String status;
    @Transient
    private String password;
    private String frozenFlag;
    private Date frozenDate;
    private String passwordLifespanType;
    private Long passwordLifespanDays;
    private Long passwordLifespanTimes;
    private Date lastLoginDate;
    private Date lastPasswordUpdateDate;
    @Transient
    private String companyFullName;
    @Transient
    private String positionName;
    @Transient
    private String unitName;
    @Transient
    private Long relationId;
    @Transient
    private Long relationUserId;
    @Transient
    private String userDescrip;
    @Transient
    private String employeeCode;
    @Transient
    private String queryCondition;
    @Transient
    @Children
    List<SysUserAllocation> sysUserAllocation;
    @Transient
    private Long allocationId;

    @Transient
    private Long[] positionIdList;

    @Transient
    private Long[] unitIdList;

    public SysUser() {
    }

    public Long[] getUnitIdList() {
        return unitIdList;
    }

    public void setUnitIdList(Long[] unitIdList) {
        this.unitIdList = unitIdList;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public Long[] getPositionIdList() {
        return positionIdList;
    }

    public void setPositionIdList(Long[] positionIdList) {
        this.positionIdList = positionIdList;
    }

    public String getEmployeeCode() {
        return this.employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getUserDescrip() {
        return this.userDescrip;
    }

    public void setUserDescrip(String userDescrip) {
        this.userDescrip = userDescrip;
    }

    public Long getRelationUserId() {
        return this.relationUserId;
    }

    public void setRelationUserId(Long relationUserId) {
        this.relationUserId = relationUserId;
    }

    public Long getRelationId() {
        return this.relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getCompanyFullName() {
        return this.companyFullName;
    }

    public void setCompanyFullName(String companyFullName) {
        this.companyFullName = companyFullName;
    }

    public String getPositionName() {
        return this.positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getQueryCondition() {
        return this.queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition == null ? null : queryCondition.trim();
    }

    public List<SysUserAllocation> getSysUserAllocation() {
        return this.sysUserAllocation;
    }

    public void setSysUserAllocation(List<SysUserAllocation> sysUserAllocation) {
        this.sysUserAllocation = sysUserAllocation;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPasswordEncrypted() {
        return this.passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted == null ? null : passwordEncrypted.trim();
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Date getStartActiveDate() {
        return this.startActiveDate;
    }

    public void setStartActiveDate(Date startActiveDate) {
        this.startActiveDate = startActiveDate;
    }

    public Date getEndActiveDate() {
        return this.endActiveDate;
    }

    public void setEndActiveDate(Date endActiveDate) {
        this.endActiveDate = endActiveDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getFrozenFlag() {
        return this.frozenFlag;
    }

    public void setFrozenFlag(String frozenFlag) {
        this.frozenFlag = frozenFlag == null ? null : frozenFlag.trim();
    }

    public String getPasswordLifespanType() {
        return this.passwordLifespanType;
    }

    public void setPasswordLifespanType(String passwordLifespanType) {
        this.passwordLifespanType = passwordLifespanType == null ? null : passwordLifespanType.trim();
    }

    public Long getPasswordLifespanDays() {
        return this.passwordLifespanDays;
    }

    public void setPasswordLifespanDays(Long passwordLifespanDays) {
        this.passwordLifespanDays = passwordLifespanDays;
    }

    public Long getPasswordLifespanTimes() {
        return this.passwordLifespanTimes;
    }

    public void setPasswordLifespanTimes(Long passwordLifespanTimes) {
        this.passwordLifespanTimes = passwordLifespanTimes;
    }

    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastPasswordUpdateDate() {
        return this.lastPasswordUpdateDate;
    }

    public void setLastPasswordUpdateDate(Date lastPasswordUpdateDate) {
        this.lastPasswordUpdateDate = lastPasswordUpdateDate;
    }

    public Long getUserType() {
        return this.userType;
    }

    public void setUserType(Long userType) {
        this.userType = userType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFrozenDate() {
        return this.frozenDate;
    }

    public void setFrozenDate(Date frozenDate) {
        this.frozenDate = frozenDate;
    }
}
