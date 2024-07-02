package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

public class LovCshUnitDto {

    @LovField(prompt = "部门ID", field = "unit_id")
    private String unitId;
    @LovField(prompt = "部门代码", field = "unit_code", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String unitCode;
    @LovField(prompt = "部门名称", field = "unit_name", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String unitName;

}
