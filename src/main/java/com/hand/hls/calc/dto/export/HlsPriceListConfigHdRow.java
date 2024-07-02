package com.hand.hls.calc.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * <p>
 * 价目表配置头
 * </p>
 *
 * @author qiang.chen04@hand-china.com 2019/07/09 10:34
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HlsPriceListConfigHdRow {

    private Long configHdId;

    private String priceList;

    private String tableName;

    private String tableType;

    private String multiLineFrom;

    private String multiLineTo;

    private String sheetName;

    private String sheetCode;

    private String configType;

    private List<HlsPriceListConfigLnRow> hlsPriceListConfigLnRows;

    public Long getConfigHdId() {
        return configHdId;
    }

    public void setConfigHdId(Long configHdId) {
        this.configHdId = configHdId;
    }

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getMultiLineFrom() {
        return multiLineFrom;
    }

    public void setMultiLineFrom(String multiLineFrom) {
        this.multiLineFrom = multiLineFrom;
    }

    public String getMultiLineTo() {
        return multiLineTo;
    }

    public void setMultiLineTo(String multiLineTo) {
        this.multiLineTo = multiLineTo;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSheetCode() {
        return sheetCode;
    }

    public void setSheetCode(String sheetCode) {
        this.sheetCode = sheetCode;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public List<HlsPriceListConfigLnRow> getHlsPriceListConfigLnRows() {
        return hlsPriceListConfigLnRows;
    }

    public void setHlsPriceListConfigLnRows(List<HlsPriceListConfigLnRow> hlsPriceListConfigLnRows) {
        this.hlsPriceListConfigLnRows = hlsPriceListConfigLnRows;
    }
}
