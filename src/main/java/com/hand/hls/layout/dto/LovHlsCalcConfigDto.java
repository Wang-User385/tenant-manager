package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

/**
 * @author Qian Yuanfeng
 * @date 2020/6/24 - 9:58
 */
public class LovHlsCalcConfigDto {


    @LovField(prompt = "模板代码", field = "price_list", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String priceList;

    @LovField(prompt = "模板名称", field = "description", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String description;

}
