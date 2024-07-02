package com.hand.hls.fnd.dto;

import javax.persistence.Transient;
import java.util.List;

/**
 * Created by qixiang.shao on 2017/11/16
 */
public class HlsCusEmployee extends HlsEmployee {

    @Transient
    private String employeeAssignId;

    @Transient
    private String isDesignated;//用于查询工作流页面指定的经办人类型

    @Transient
    private String positionCode;

    @Transient
    private String unitCode;

    @Transient
    private String codeString;

    @Transient
    private Long manageUnitId;

    @Transient
    private List<String> positionCodeList;

    @Transient
    private String userName;

    public String[] getEmployeeCodes() {
        return employeeCodes;
    }

    public void setEmployeeCodes(String[] employeeCodes) {
        this.employeeCodes = employeeCodes;
    }

    @Transient
    private String[] employeeCodes;
    public String getEmployeeAssignId() {
        return employeeAssignId;
    }

    public void setEmployeeAssignId(String employeeAssignId) {
        this.employeeAssignId = employeeAssignId;
    }

    public String getIsDesignated() {
        return isDesignated;
    }

    public void setIsDesignated(String isDesignated) {
        this.isDesignated = isDesignated;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getCodeString() {
        return codeString;
    }

    public void setCodeString(String codeString) {
        this.codeString = codeString;
    }

    public Long getManageUnitId() {
        return manageUnitId;
    }

    public void setManageUnitId(Long manageUnitId) {
        this.manageUnitId = manageUnitId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public List<String> getPositionCodeList() {
        return positionCodeList;
    }

    public void setPositionCodeList(List<String> positionCodeList) {
        this.positionCodeList = positionCodeList;
    }

    @Transient
    private Long allocationId;

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }
}
