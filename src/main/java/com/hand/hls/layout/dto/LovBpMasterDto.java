package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

/**
 * Created with IntelliJ IDEA.
 * User: jianfeng.fang
 * Date: 2020年03月07日
 */
public class LovBpMasterDto {

    @LovField(prompt = "客户ID", field = "bp_id")
    private String bpId;
    @LovField(prompt = "客户名称", field = "bp_name", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String bpName;
    @LovField(prompt = "客户编码", field = "bp_code")
    private String bpCode;
    @LovField(prompt = "客户类别", field = "bp_class", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String bpClass;
    @LovField(prompt = "客户类型", field = "bp_type", forQuery = true, forDisplay = true, displayWidth = 200, displayAlign = "left")
    private String bpType;
    @LovField(prompt = "关联区分", field = "associated_division")
    private String associatedDivision;


}
