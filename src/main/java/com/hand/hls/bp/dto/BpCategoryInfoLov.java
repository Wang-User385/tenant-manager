//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.bp.dto;

import leaf.annotation.LovField;

import javax.persistence.Transient;

public class BpCategoryInfoLov {
    @LovField(
            prompt = "HLS.BP_CATEGORY",
            field = "bp_category",
            forQuery = true,
            forDisplay = true,
            displayWidth = 100
    )
    private String bp_category;
    @LovField(
            prompt = "类别描述",
            field = "category_desc",
            forQuery = true,
            forDisplay = true,
            displayWidth = 150
    )
    private String category_desc;

    @LovField(
            prompt = "HLS.BP_TYPE",
            field = "bp_type",
            forQuery = true,
            forDisplay = true,
            displayWidth = 100
    )
    private String bp_type;


    @LovField(
            prompt = "类型描述",
            field = "type_desc",
            forQuery = true,
            forDisplay = true,
            displayWidth = 150
    )
    private String type_desc;

    @Transient
    private String bpCategory;


    public BpCategoryInfoLov() {
    }

    public String getBpCategory() {
        return this.bpCategory;
    }

    public void setBpCategory(String bpCategory) {
        this.bpCategory = bpCategory;
    }

    public String getBp_type() {
        return this.bp_type;
    }

    public void setBp_type(String bp_type) {
        this.bp_type = bp_type;
    }

    public String getType_desc() {
        return this.type_desc;
    }

    public void setType_desc(String type_desc) {
        this.type_desc = type_desc;
    }

    public String getBp_category() {

        return this.bp_category;
    }

    public void setBp_category(String bp_category) {
        this.bp_category = bp_category;
    }

    public String getCategory_desc() {
        return this.category_desc;
    }

    public void setCategory_desc(String category_desc) {
        this.category_desc = category_desc;
    }
}
