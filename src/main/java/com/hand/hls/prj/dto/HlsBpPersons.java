package com.hand.hls.prj.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ExtensionAttribute(
        disable = true
)
@Table(
        name = "fnd_employee"
)
public class HlsBpPersons extends BaseDTO {
    @Id
    @Column(
            name = "EMPLOYEE_ID"
    )
    private Long employeeId;
    @Column(
            name = "companyId"
    )
    private long companyId;
    @Column(
            name = "EMPLOYEE_CODE"
    )
    private String employeeCode;
    @Column(
            name = "NAME"
    )
    private String name;
    @Transient
    private String project_name;
    @Transient
    private String project_number;
    @Transient
    private String project_id;
    @Transient
    private String bp_id;

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_number() {
        return project_number;
    }

    public void setProject_number(String project_number) {
        this.project_number = project_number;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getBp_id() {
        return bp_id;
    }

    public void setBp_id(String bp_id) {
        this.bp_id = bp_id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public long getcompanyId() {
        return companyId;
    }

    public void setcompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
