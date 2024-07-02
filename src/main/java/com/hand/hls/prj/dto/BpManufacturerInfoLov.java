//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.prj.dto;

import leaf.annotation.LovField;
import lombok.Data;

@Data
public class BpManufacturerInfoLov {
    @LovField(
            prompt = "商业伙伴类别",
            field = "bp_category",
            forQuery = true,
            forDisplay = true,
            displayWidth = 200
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
