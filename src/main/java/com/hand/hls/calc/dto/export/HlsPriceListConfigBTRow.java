package com.hand.hls.calc.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * 价目表按钮配置 导出行
 * </p>
 *
 * @author qiang.chen04@hand-china.com 2019/07/09 10:31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HlsPriceListConfigBTRow {
    private Long configBtId; //配置按钮ID

    private String priceList; //价目表ID

    private Long configHdId; //配置头ID

    private String buttonType; //按钮类型

    private String buttonName; //按钮名称

    private String targetColumnCode; //目标单元格

    private Double targetColumnValue; //目标值

    private String variableColumnCode; //可变单元格

    private Double guessValue; //猜测值

    public Long getConfigBtId() {
        return configBtId;
    }

    public void setConfigBtId(Long configBtId) {
        this.configBtId = configBtId;
    }

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public Long getConfigHdId() {
        return configHdId;
    }

    public void setConfigHdId(Long configHdId) {
        this.configHdId = configHdId;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getTargetColumnCode() {
        return targetColumnCode;
    }

    public void setTargetColumnCode(String targetColumnCode) {
        this.targetColumnCode = targetColumnCode;
    }

    public Double getTargetColumnValue() {
        return targetColumnValue;
    }

    public void setTargetColumnValue(Double targetColumnValue) {
        this.targetColumnValue = targetColumnValue;
    }

    public String getVariableColumnCode() {
        return variableColumnCode;
    }

    public void setVariableColumnCode(String variableColumnCode) {
        this.variableColumnCode = variableColumnCode;
    }

    public Double getGuessValue() {
        return guessValue;
    }

    public void setGuessValue(Double guessValue) {
        this.guessValue = guessValue;
    }
}
