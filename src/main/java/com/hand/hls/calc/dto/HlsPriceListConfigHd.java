package com.hand.hls.calc.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@SuppressWarnings("serial")
@Table(name="hls_price_list_config_hd")
@ExtensionAttribute(disable=true)
public class HlsPriceListConfigHd extends BaseDTO {
	@Id
	@GeneratedValue
    private Long configHdId;

    private String priceList;

    private String tableName;

    private String tableType;

    private String multiLineFrom;

    private String multiLineTo;

    private String sheetName;

    private String sheetCode;

    private String configType;

    @Transient
    private String configTypeN;

    @Transient
    private String description;

    @Transient
    private List<HlsPriceListConfigLn> hlsPriceListConfigLns;

    public List<HlsPriceListConfigLn> getHlsPriceListConfigLnsH() {
        return hlsPriceListConfigLnsH;
    }

    public void setHlsPriceListConfigLnsH(List<HlsPriceListConfigLn> hlsPriceListConfigLnsH) {
        this.hlsPriceListConfigLnsH = hlsPriceListConfigLnsH;
    }

    @Transient
    private List<HlsPriceListConfigLn> hlsPriceListConfigLnsH;
    @Transient
    private String tableNameH;
    @Transient
    private String tableTypeH;
    @Transient
    private Long configHdIdH;

    public String getTableNameH() {
        return tableNameH;
    }

    public void setTableNameH(String tableNameH) {
        this.tableNameH = tableNameH;
    }

    public String getTableTypeH() {
        return tableTypeH;
    }

    public void setTableTypeH(String tableTypeH) {
        this.tableTypeH = tableTypeH;
    }

    public Long getConfigHdIdH() {
        return configHdIdH;
    }

    public void setConfigHdIdH(Long configHdIdH) {
        this.configHdIdH = configHdIdH;
    }

    public List<HlsPriceListConfigLn> getHlsPriceListConfigLns() {
		return hlsPriceListConfigLns;
	}

	public void setHlsPriceListConfigLns(List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
		this.hlsPriceListConfigLns = hlsPriceListConfigLns;
	}

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
        this.priceList = priceList == null ? null : priceList.trim();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType == null ? null : tableType.trim();
    }

    public String getMultiLineFrom() {
        return multiLineFrom;
    }

    public void setMultiLineFrom(String multiLineFrom) {
        this.multiLineFrom = multiLineFrom == null ? null : multiLineFrom.trim();
    }

    public String getMultiLineTo() {
        return multiLineTo;
    }

    public void setMultiLineTo(String multiLineTo) {
        this.multiLineTo = multiLineTo == null ? null : multiLineTo.trim();
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getConfigTypeN() {
        return configTypeN;
    }

    public void setConfigTypeN(String configTypeN) {
        this.configTypeN = configTypeN;
    }

    @Override
	public String toString() {
		return "HlsPriceListConfigHd [configHdId=" + configHdId + ", priceList=" + priceList + ", tableName="
				+ tableName + ", tableType=" + tableType + ", multiLineFrom=" + multiLineFrom + ", multiLineTo="
				+ multiLineTo + ", hlsPriceListConfigLns=" + hlsPriceListConfigLns + "]";
	}

}