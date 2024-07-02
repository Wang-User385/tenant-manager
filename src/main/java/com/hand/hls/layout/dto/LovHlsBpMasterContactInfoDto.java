package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

/**
 * Created with IntelliJ IDEA.
 * User: Eugene Song
 * Date: 2020年03月20日
 */
public class LovHlsBpMasterContactInfoDto {

    @LovField(prompt = "收件人姓名", field = "contact_person", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String contactPerson;
    @LovField(prompt = "收件人电话", field = "cell_phone", forQuery = true, forDisplay = true, displayWidth = 150, displayAlign = "left")
    private String cellPhone;


}
