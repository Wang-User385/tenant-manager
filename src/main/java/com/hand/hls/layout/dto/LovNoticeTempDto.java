package com.hand.hls.layout.dto;


import leaf.annotation.LovField;

public class LovNoticeTempDto {


    @LovField(prompt = "模板代码", field = "templet_code", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String templetCode;

    @LovField(prompt = "模板名称", field = "templet_name", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String templetName;

}
