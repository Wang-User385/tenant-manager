package com.hand.hls.prj.dto;

import leaf.annotation.LovField;
import lombok.Data;

@Data
public class BpDealerInfoLov {
    @LovField(
            prompt = "商业伙伴类别",
            field = "bp_category",
            forQuery = true,
            forDisplay = true,
            displayWidth = 125
    )
    private String bpCategory;
    @LovField(
            prompt = "商业伙伴名称",
            field = "bp_name",
            forQuery = true,
            forDisplay = true,
            displayWidth = 200
    )
    private String bpName;

}
