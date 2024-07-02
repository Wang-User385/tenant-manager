package com.hand.hls.calc.dto.export;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * 价目表配置行
 * </p>
 *
 * @author qiang.chen04@hand-china.com 2019/06/21 13:49
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HlsPriceListConfigLnRow {
    private Long configLnId;
    private Long configHdId;
    private String columnName;
    private String columnCode;
    private String columnType;
    private String validateType;
    private String validateSql;
    private Long cashflowItem;
    private Long precisions;
    private String netColumnCode;
    private String vatColumnCode;
    private String formulaNoCheckFlag;
    private String prompt;
    private String hideColumnFlag;
    private String hideRowFlag;

    public Long getConfigLnId() {
        return configLnId;
    }

    public void setConfigLnId(Long configLnId) {
        this.configLnId = configLnId;
    }

    public Long getConfigHdId() {
        return configHdId;
    }

    public void setConfigHdId(Long configHdId) {
        this.configHdId = configHdId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    public String getValidateSql() {
        return validateSql;
    }

    public void setValidateSql(String validateSql) {
        this.validateSql = validateSql;
    }

    public Long getCashflowItem() {
        return cashflowItem;
    }

    public void setCashflowItem(Long cashflowItem) {
        this.cashflowItem = cashflowItem;
    }

    public Long getPrecisions() {
        return precisions;
    }

    public void setPrecisions(Long precisions) {
        this.precisions = precisions;
    }

    public String getNetColumnCode() {
        return netColumnCode;
    }

    public void setNetColumnCode(String netColumnCode) {
        this.netColumnCode = netColumnCode;
    }

    public String getVatColumnCode() {
        return vatColumnCode;
    }

    public void setVatColumnCode(String vatColumnCode) {
        this.vatColumnCode = vatColumnCode;
    }

    public String getFormulaNoCheckFlag() {
        return formulaNoCheckFlag;
    }

    public void setFormulaNoCheckFlag(String formulaNoCheckFlag) {
        this.formulaNoCheckFlag = formulaNoCheckFlag;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getHideColumnFlag() {
        return hideColumnFlag;
    }

    public void setHideColumnFlag(String hideColumnFlag) {
        this.hideColumnFlag = hideColumnFlag;
    }

    public String getHideRowFlag() {
        return hideRowFlag;
    }

    public void setHideRowFlag(String hideRowFlag) {
        this.hideRowFlag = hideRowFlag;
    }
}
