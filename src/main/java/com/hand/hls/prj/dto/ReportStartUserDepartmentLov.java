package com.hand.hls.prj.dto;

import leaf.annotation.LovField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportStartUserDepartmentLov {

    @LovField(
        prompt = "部门名称",
        field = "unit_name",
        forQuery = true,
        forDisplay = true,
        displayWidth = 180
    )
    private String unitName;
    @LovField(
        prompt = "公司名称",
        field = "company_short_name",
        forQuery = true,
        forDisplay = true,
        displayWidth = 180
    )
    private String companyShortName;

    public ReportStartUserDepartmentLov() {
    }
}