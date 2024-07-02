package com.hand.hls.ast.dto;

import leaf.annotation.LovField;
import lombok.Data;

@Data
public class LovAssetClassDto {
    @LovField(prompt = "bp_id", field ="bp_id", forQuery =false, forDisplay =false)
    private String bpId;

    @LovField(prompt = "厂商/合作方名称", field = "bp_name", forQuery =true, forDisplay =true, displayWidth =150)
    private String bpName;

    @LovField(prompt = "类型", field ="bp_category", forQuery =true, forDisplay =true, displayWidth =150)
    private String bpCategory;
}
