package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene Song
 * Date: 2020年03月20日
 */
public class LovHlsBpMasterAddressDto {

    @LovField(prompt = "收件地址", field = "address_detail", forQuery = true, forDisplay = true, displayWidth = 450, displayAlign = "left")
    private String addressDetail;
    @LovField(prompt = "邮编", field = "zipcode", forQuery = true, forDisplay = true, displayWidth = 50, displayAlign = "left")
    private String zipcode;

}
