package com.hand.hls.layout.dto;

import leaf.annotation.LovField;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/1 14:54
 */
public class LovConCashflowItemLov {

    @LovField(prompt = "现金流项目", forQuery = true, field = "cf_item", forDisplay = true, displayWidth = 120)
    private Long cfItem;
    @LovField(prompt = "现金流名称", field = "cf_project", forDisplay = true, forQuery = true, displayWidth = 150)
    private String cfProject;

}
