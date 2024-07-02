package com.hand.hls.sys.dto;

import leaf.annotation.LovField;


public class LovFndEmployee {
    @LovField(
            prompt = "员工Id",
            field = "employee_id"
    )
    private String employeeId;
    @LovField(
            prompt = "员工代码",
            field = "employee_code"
    )
    private String employeeCode;
    @LovField(
            prompt = "员工名称",
            field = "name",
            forQuery = true,
            forDisplay = true,
            displayWidth = 250
    )
    private String name;
    @LovField(
            prompt = "",
            field = "company_id"
    )
    private String companyId;
    @LovField(
            prompt = "",
            field = "company_name"
    )
    private String companyName;
    @LovField(
            prompt = "",
            field = "unit_id"
    )
    private String unitId;
    @LovField(
            prompt = "部门名称",
            field = "unit_name",
            forQuery = true,
            forDisplay = true,
            displayWidth = 250
    )
    private String unitName;
    @LovField(
            prompt = "",
            field = "manager_name"
    )
    private String managerName;
    @LovField(
            prompt = "",
            field = "employee_assign_id"
    )
    private String employeeAssignId;

    public LovFndEmployee() {
    }
}
