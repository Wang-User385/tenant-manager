package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

public class LovUserBpDto {
    @LovField(prompt = "商业伙伴编码", field = "bp_code", forDisplay = true, forQuery = true, displayWidth = 150, displayAlign = "left")
    private String bpCode;
    @LovField(prompt = "商业伙伴名称", field = "bp_name", forDisplay = true, forQuery = true, displayWidth = 200, displayAlign = "left")
    private String bpName;
    @LovField(prompt = "商业伙伴类型", field = "bp_category", forDisplay = true, forQuery = true, displayWidth = 200, displayAlign = "left")
    private String bpCategory;
}
