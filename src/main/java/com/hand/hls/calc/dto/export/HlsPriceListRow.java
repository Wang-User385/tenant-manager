package com.hand.hls.calc.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author qiang.chen04@hand-china.com 2019/06/20 16:05
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HlsPriceListRow {

    private String priceList;

    private String description;

    private String sheets;

    private String enabledFlag;

    /**
     * 价目表按钮配置导出行
     */
    private List<HlsPriceListConfigBTRow> hlsPriceListConfigBTRows;

    /**
     * 价目表配置头 导出行
     */
    private List<HlsPriceListConfigHdRow> hlsPriceListConfigHdRows;


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

    public List<HlsPriceListConfigBTRow> getHlsPriceListConfigBTRows() {
        return hlsPriceListConfigBTRows;
    }

    public void setHlsPriceListConfigBTRows(List<HlsPriceListConfigBTRow> hlsPriceListConfigBTRows) {
        this.hlsPriceListConfigBTRows = hlsPriceListConfigBTRows;
    }

    public List<HlsPriceListConfigHdRow> getHlsPriceListConfigHdRows() {
        return hlsPriceListConfigHdRows;
    }

    public void setHlsPriceListConfigHdRows(List<HlsPriceListConfigHdRow> hlsPriceListConfigHdRows) {
        this.hlsPriceListConfigHdRows = hlsPriceListConfigHdRows;
    }
}
