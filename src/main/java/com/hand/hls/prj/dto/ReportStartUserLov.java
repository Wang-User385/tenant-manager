package com.hand.hls.prj.dto;

import leaf.annotation.LovField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportStartUserLov {


    @LovField(
        prompt = "描述",
        field = "description",
        forDisplay = true,
        forQuery = true,
        displayWidth = 180
    )
    private String description;
    @LovField(
        prompt = "姓名",
        field = "user_name",
        forQuery = true,
        forDisplay = true,
        displayWidth = 180
    )
    private String userName;

    public ReportStartUserLov() {
    }
}