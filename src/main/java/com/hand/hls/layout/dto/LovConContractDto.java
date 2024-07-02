package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

import java.util.Date;


public class LovConContractDto {

    @LovField(prompt = "客户名称", field = "bp_name",forQuery = true,forDisplay = true,displayWidth = 100)
    private String bpName;
    @LovField(prompt = "合同编号", field = "contract_number", forDisplay = true, forQuery = true, displayWidth = 120, displayAlign = "left")
    private String contractNumber;
    @LovField(prompt = "期数", forQuery = true, field = "times", forDisplay = true, displayWidth = 60)
    private Long times;
    @LovField(prompt = "现金流项目", field = "cf_project", forDisplay = true, forQuery = true, displayWidth = 80)
    private String cfProject;


}
