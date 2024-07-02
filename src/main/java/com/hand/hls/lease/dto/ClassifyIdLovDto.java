package com.hand.hls.lease.dto;

import leaf.annotation.LovField;

/**
 * @author Marshal
 * @date 2019-01-15 11:08
 * @description 规则引擎定义LOV dto
 */
public class ClassifyIdLovDto {

    public String getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(String classifyId) {
        this.classifyId = classifyId;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getManufacturerIdN() {
        return manufacturerIdN;
    }

    public void setManufacturerIdN(String manufacturerIdN) {
        this.manufacturerIdN = manufacturerIdN;
    }

    @LovField(prompt = "分类ID", field = "classify_id", forQuery = false, forDisplay = false,displayWidth=160)
    private String classifyId;
    @LovField(prompt = "属性值", field = "attribute_value", forQuery = true, forDisplay = true,displayWidth=160)
    private String attributeValue;
    @LovField(prompt = "生产厂商", field = "manufacturer_id_n",forQuery = true,forDisplay = true,displayWidth=160)
    private String manufacturerIdN;

}
