package com.hand.hls.calc.dto;

import leaf.annotation.LovField;

/**
 * @author 3835
 * @description 产品报价列表LOV
 * @date 2019/7/3
 */
public class LovHlsCalcConfig {
    @LovField(prompt = "价目表代码", field = "price_list", forQuery = true, forDisplay = true)
    private String priceList;
    @LovField(prompt = "价目表名称", field = "description", forQuery = true, forDisplay = true)
    private String description;
    private String sheets;
    private String enabledFlag;

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSheets() {
        return sheets;
    }

    public void setSheets(String sheets) {
        this.sheets = sheets;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

}
