package com.hand.hls.calc.dto;


import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Table(name="hls_price_list_config_ln")
@ExtensionAttribute(disable=true)
public class HlsPriceListConfigLn extends BaseDTO {

    public static final String NUMBER = "NUMBER";
    public static final String CHAR = "CHAR";
    public static final String DATE = "DATE";

	@Id
	@GeneratedValue
    private Long configLnId;

    private Long configHdId;
    @Condition(operator = LIKE)
    private String columnName;
    @Condition(operator = LIKE)
    private String columnCode;

    private String columnType;

    private String validateType;

    private String validateSql;

    private Long cashflowItem;
    @Transient
    private String inputMode;
    private Long        precisions;
    private String netColumnCode;
    private String vatColumnCode;
    private String formulaNoCheckFlag;
    private String prompt;
    private String   hideColumnFlag;
    private String  hideRowFlag;
    @Transient
    private String description;
    @Transient
    private String tableType;
    @Transient
    private String priceList;


    @Transient
    private int hideColumnIndex;
    @Transient
    private int hideRowIndex;
    @Transient
    private String sheetName;

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

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

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
        this.columnName = columnName == null ? null : columnName.trim();
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode == null ? null : columnCode.trim();
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType == null ? null : columnType.trim();
    }

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType == null ? null : validateType.trim();
    }

    public String getValidateSql() {
        return validateSql;
    }

    public void setValidateSql(String validateSql) {
        this.validateSql = validateSql == null ? null : validateSql.trim();
    }

    public Long getCashflowItem() {
        return cashflowItem;
    }

    public void setCashflowItem(Long cashflowItem) {
        this.cashflowItem = cashflowItem;
    }

	@Override
	public String toString() {
		return "HlsPriceListConfigLn [configLnId=" + configLnId + ", configHdId=" + configHdId + ", columnName="
				+ columnName + ", columnCode=" + columnCode + ", columnType=" + columnType + ", validateType="
				+ validateType + ", validateSql=" + validateSql + ", cashflowItem=" + cashflowItem + "]";
	}

    public String getInputMode() {
        return inputMode;
    }

    public void setInputMode(String inputMode) {
        this.inputMode = inputMode;
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

    public int getHideColumnIndex() {
        return hideColumnIndex;
    }

    public void setHideColumnIndex(int hideColumnIndex) {
        this.hideColumnIndex = hideColumnIndex;
    }

    public int getHideRowIndex() {
        return hideRowIndex;
    }

    public void setHideRowIndex(int hideRowIndex) {
        this.hideRowIndex = hideRowIndex;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}