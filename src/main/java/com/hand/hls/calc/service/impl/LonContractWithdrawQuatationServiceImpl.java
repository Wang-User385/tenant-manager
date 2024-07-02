package com.hand.hls.calc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.calc.service.QuotationCommon;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractQuotation;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.mapper.HlsCusLonContractMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractRepaymentMapper;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fin.service.HlsCusLonContractQuotationService;
import com.hand.hls.fin.service.LonContractRepaymentService;
import com.hand.hls.fin.service.LonContractWithdrawService;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.service.IPrjQuotationCashflowService;
import com.hand.hls.prj.service.IPrjQuotationDetailsService;
import com.hand.hls.prj.service.IPrjQuotationService;
import com.hand.hls.sys.dto.SysDocumentHistoryBlob;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryBlobMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.utils.HlsCusConstant;
import hls.core.sys.mapper.SysCodeValueMapper;
import jodd.util.ArraysUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.rmi.NoSuchObjectException;
import java.util.*;

/**
 * 融资提款报价
 */

@Service
public class LonContractWithdrawQuatationServiceImpl implements QuotationCommon {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;
    @Autowired
    private IPrjQuotationCashflowService prjQuotationCashflowService;
    @Autowired
    private IPrjQuotationDetailsService prjQuotationDetailsService;
    @Autowired
    private SysDocumentHistoryMapper documentHistoryMapper;
    @Autowired
    private SysDocumentHistoryDetailMapper documentHistoryDetailMapper;
    @Autowired
    private SysDocumentHistoryBlobMapper documentHistoryBlobMapper;
    @Autowired
    private HlsPriceListConfigHdMapper configHdMapper;
    @Autowired
    private HlsPriceListConfigLnMapper configLnMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private SysDocumentHistoryDetailMapper sysDocumentHistoryDetailMapper;
    @Autowired
    private SysCodeValueMapper sysCodeValueMapper;
    @Autowired
    private HlsCfItemMapper cfItemMapper;
    @Autowired
    private IPrjQuotationService prjQuotationService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;

    @Autowired
    private HlsCusLonContractWithdrawMapper lonContractWithdrawMapper;

    @Autowired
    private HlsCusLonContractMapper lonContracWithdrawMapper;

    @Autowired
    private HlsCusLonContractRepaymentMapper lonContractRepaymentMapper;
    @Autowired
    private LonContractRepaymentService lonContractRepaymentService;
    @Autowired
    private LonContractWithdrawService lonContractWithdrawService;
    @Autowired
    HlsCusLonContractQuotationService lonContractQuotationService;

    private static final String SOURCE_DOCUMENT_CATEGORY = "LON_CONTRACT_WITHDRAW";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CELLS = "cells";
    private static final String KEY_ROWS = "rows";
    private static final String KEY_VALUE = "value";
    private static final String KEY_FORMULA = "formula";
    private static final String KEY_FIELD = "field";
    private static final String PRICE_TYPE_SINGLE = "SINGLE";
    private static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    private static final String KEY_FORMAT = "format";
    private static final String[] DOUBLE_FORMATS = {"0%", "0.00"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy"};


    private static final String MONTH = "MONTH";
    private static final String QUARTER = "QUARTER";
    private static final String HALF_A_YEAR = "HALF_A_YEAR";
    private static final String YEAR = "YEAR";
    private static final String UNEQUAL_AMOUNT = "UNEQUAL_AMOUNT";


    private static final Long MONTH_VALUE = 12L;
    private static final Long QUARTER_VALUE = 4L;
    private static final Long HALF_A_YEAR_VALUE = 2L;
    private static final Long YEAR_VALUE = 1L;


    @Override
    public String getSourceDocumentCategory() {
        return SOURCE_DOCUMENT_CATEGORY;
    }

    private void setCellFormat(JSONArray transArray, List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
        for (int i = 0; i < transArray.size(); i++) {
            JSONObject jsonObject = transArray.getJSONObject(i);
            setCellFormat(jsonObject, hlsPriceListConfigLns);
        }
    }

    private void setCellFormat(JSONObject object, List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
        for (HlsPriceListConfigLn item : hlsPriceListConfigLns) {
            if (item.getColumnName().toLowerCase().equals(object.getString(KEY_FIELD))) {
                switch (item.getColumnType()) {
                    case HlsPriceListConfigLn.NUMBER:
                        object.put(KEY_FORMAT, DOUBLE_FORMATS[1]);
                        break;
                    case HlsPriceListConfigLn.DATE:
                        object.put(KEY_FORMAT, DATE_FORMATS[0]);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private List<JSONObject> getOldContractCashFlows(IRequest iRequest, Long documentId, String
            documentCategory, Long quotationId) throws NoSuchObjectException {
        List<JSONObject> contractCashflows = new ArrayList<>();
        sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "con_contract_cashflow")
                .forEach(item -> {
                    if (quotationId.equals(item.getJSONObject("data").getLong("generated_source_doc_id"))) {
                        contractCashflows.add(item);
                    }
                });
        return contractCashflows;
    }

    private int getTimesColumn(List<HlsPriceListConfigLn> hlsPriceListConfigLnList) {
        int timesColumn = -1;
        for (HlsPriceListConfigLn item : hlsPriceListConfigLnList) {
            if ("times".equals(item.getColumnName())) {
                timesColumn = hlsCalcExcelImportUtilService.excelColStrToNum(item.getColumnCode().toUpperCase());
            }
        }
        return timesColumn;
    }


    private JSONArray extractLineDataFromSheet(String sheets, String priceList) {
        JSONArray lines = new JSONArray();
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, PRICE_TYPE_MULTI_LINE);
        String multiLineFrom = hd.getMultiLineFrom();
        String multiLineTo = hd.getMultiLineTo();
        List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();
        if (StringUtils.isAnyEmpty(multiLineFrom, multiLineTo)) {
            logger.warn("Found empty multiLineFrom[{}] or multiLineTo[{}]", multiLineFrom, multiLineTo);
            return lines;
        }
        if (CollectionUtils.isEmpty(configLns)) {
            logger.warn("Found empty HlsPriceListConfigLn of multi-line", multiLineFrom, multiLineTo);
            return lines;
        }
        Integer from = Integer.valueOf(multiLineFrom);
        Integer to = Integer.valueOf(multiLineTo);
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        readSheet(wb, sheet, JSONArray.parseArray(sheets).getJSONObject(0));
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        ROW_LOOP:
        for (Integer i = from; i <= to; i++) {
            boolean hasData = false;
            JSONObject line = new JSONObject();
            line.put("time", i - from);
            JSONArray dataArray = new JSONArray();
            line.put("data", dataArray);
            for (HlsPriceListConfigLn configLn : configLns) {
                String columnName = configLn.getColumnName();
                String columnCode = configLn.getColumnCode();
                CellPosition cellPosition = parsePosition(columnCode + i);
                XSSFRow row = sheet.getRow(cellPosition.getRowIndex());
                if (row == null) {
                    continue ROW_LOOP;
                }
                XSSFCell cell = row.getCell(cellPosition.getCellIndex());
                if (cell == null) {
                    continue;
                }
                Object value = null;
                switch (cell.getCellTypeEnum()) {
                    case FORMULA:
                        CellValue cellValue = evaluator.evaluate(cell);
                        CellType cellTypeEnum = cellValue.getCellTypeEnum();
                        switch (cellTypeEnum) {
                            case NUMERIC:
                                value = cellValue.getNumberValue();
                                break;
                            case STRING:
                                value = cellValue.getStringValue();
                                break;
                            case BOOLEAN:
                                value = cellValue.getBooleanValue();
                                break;
                            default:
                                logger.warn("Can not get value of type: {}", cellTypeEnum);
                        }
                        break;
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        value = cell.getNumericCellValue();
                        break;
                    default:
                        logger.warn("Can not get value of type: {}", cell.getCellTypeEnum());
                }
                if (value == null) {
                    continue;
                }
                hasData = true;
                JSONObject data = new JSONObject();
                data.put(KEY_FIELD, columnName);
                data.put(KEY_VALUE, value);
                dataArray.add(data);
            }
            if (!hasData) {
                break;
            } else {
                lines.add(line);
            }
        }

        return lines;
    }

    @SuppressWarnings("AlibabaSwitchStatement")
    private JSONArray extractHeadDataFromSheet(String sheets, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLn(priceList);
        JSONArray array = new JSONArray();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        readSheet(wb, sheet, JSONArray.parseArray(sheets).getJSONObject(0), true);
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        for (HlsPriceListConfigLn ln : lns) {
            JSONObject jsonObject = new JSONObject();
            String columnCode = ln.getColumnCode();
            String columnName = ln.getColumnName();

            CellPosition cellPosition = parsePosition(columnCode);
            XSSFRow row = sheet.getRow(cellPosition.getRowIndex());
            XSSFCell cell = row == null ? null : row.getCell(cellPosition.getCellIndex());

            if (cell != null) {
                Object value = null;
                switch (cell.getCellTypeEnum()) {
                    case FORMULA:
                        CellValue evaluate = evaluator.evaluate(cell);
                        switch (evaluate.getCellTypeEnum()) {
                            case NUMERIC:
                                value = evaluate.getNumberValue();
                                break;
                            case STRING:
                                value = evaluate.getStringValue();
                                break;

                        }
                        break;
                    default:
                        value = getRawValue(cell);
                }
                jsonObject.put("value", value);
                jsonObject.put("field", columnName);
                array.add(jsonObject);
            } else {
                logger.warn("Cell [{}] has no value.", columnCode);
            }

        }
        setCellFormat(array, lns);
        return array;
    }

    private List<HlsPriceListConfigLn> getPriceListConfigLn(String priceList) {
        return getPriceListConfigLn(priceList, PRICE_TYPE_SINGLE).getHlsPriceListConfigLns();
    }

    private HlsPriceListConfigHd getPriceListConfigLn(String priceList, String type) {
        HlsPriceListConfigHd hd = new HlsPriceListConfigHd();
        hd.setPriceList(priceList);
        hd.setTableType(type);
        List<HlsPriceListConfigHd> headers = configHdMapper.select(hd);
        if (CollectionUtils.isEmpty(headers) || headers.size() != 1) {
            return hd;
        }
        hd = headers.get(0);
        HlsPriceListConfigLn ln = new HlsPriceListConfigLn();
        ln.setConfigHdId(hd.getConfigHdId());
        hd.setHlsPriceListConfigLns(configLnMapper.select(ln));
        return hd;
    }

    private void updateQuotationHistoryData(Long historyDetailId, String ccrPriceList, JSONArray array) {
        if (StringUtils.isNotEmpty(ccrPriceList)) {
            SysDocumentHistoryDetail detail = documentHistoryDetailMapper.selectByPrimaryKey(historyDetailId);
            String historyData = detail.getHistoryData();
            JSONObject jsonObject = JSON.parseObject(historyData);
            jsonObject.put("price_list", ccrPriceList);
            jsonObject.put("_status", "update");
            detail.setHistoryData(jsonObject.toJSONString());
            documentHistoryDetailMapper.updateByPrimaryKeySelective(detail);
        }
        SysDocumentHistoryBlob sysDocumentHistoryBlob = new SysDocumentHistoryBlob();
        sysDocumentHistoryBlob.setHistoryDetailId(historyDetailId);
        sysDocumentHistoryBlob.setFieldName("sheets");
        sysDocumentHistoryBlob = documentHistoryBlobMapper.selectOne(sysDocumentHistoryBlob);
        sysDocumentHistoryBlob.setFieldValue(JSON.toJSONString(array));
        documentHistoryBlobMapper.updateByPrimaryKeySelective(sysDocumentHistoryBlob);
    }

    private String getRawValue(XSSFCell cell) {
        String rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
                rawValue = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return rawValue;
    }

    private String getRawValue(CellValue cell) {
        String rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = String.valueOf(cell.getNumberValue());
                break;
            case STRING:
                rawValue = cell.getStringValue();
                break;
            default:
                break;
        }
        return rawValue;
    }

    private JSONObject getRowObject(JSONArray rowsObject, CellPosition cellPosition) {
        JSONObject rowObject = null;
        for (int j = 0; j < rowsObject.size(); j++) {
            if (rowsObject.getJSONObject(j).getIntValue(KEY_INDEX) == cellPosition.getRowIndex()) {
                rowObject = rowsObject.getJSONObject(j);
            }
        }
        return rowObject;
    }

    private JSONObject getRowObject(JSONArray rowsObject, int rowIndex) {
        JSONObject rowObject = null;
        for (int j = 0; j < rowsObject.size(); j++) {
            if (rowsObject.getJSONObject(j).getIntValue(KEY_INDEX) == rowIndex) {
                rowObject = rowsObject.getJSONObject(j);
            }
        }
        return rowObject;
    }


    private JSONArray getTransObject(String modCells) {
        return JSONArray.parseArray(modCells);
    }

    private String getSheetDataFromHistory(Long historyDetailId) {
        SysDocumentHistoryBlob sysDocumentHistoryBlob = new SysDocumentHistoryBlob();
        sysDocumentHistoryBlob.setHistoryDetailId(historyDetailId);
        sysDocumentHistoryBlob.setFieldName("sheets");
        List<SysDocumentHistoryBlob> list = documentHistoryBlobMapper.select(sysDocumentHistoryBlob);
        return CollectionUtils.isEmpty(list) ? null : list.get(0).getFieldValue();
    }

    private String getPriceList(Map<String, Object> data) {
        return data == null ? null : data.containsKey("price_list") ? String.valueOf(data.get("price_list")) : null;
    }

    private SysDocumentHistoryDetail queryDetail(Long historyId, Long quotationId) {
        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(historyId);
        detail.setTableName("PRJ_QUOTATION");
        detail.setTablePkValue(String.valueOf(quotationId));
        List<SysDocumentHistoryDetail> list = documentHistoryDetailMapper.select(detail);
        if (CollectionUtils.isEmpty(list) || list.size() > 1) {
            logger.warn("Can not find the unique PRJ_QUOTATION document history detail record with history id[{}].", historyId);
            return null;
        }
        return list.get(0);

    }

    private JSONObject getData(SysDocumentHistoryDetail detail) {
        if (detail == null) {
            return new JSONObject();
        }
        String historyData = detail.getHistoryData();
        if (StringUtils.isEmpty(historyData)) {
            return new JSONObject();
        }
        return JSON.parseObject(historyData);
    }

    private void modifySheet(JSONArray cells, Map<String, Object> data, XSSFWorkbook wb, XSSFSheet
            sheet, JSONObject jsonObject) {
        for (int i = 0; cells != null && i < cells.size(); i++) {
            JSONObject row = cells.getJSONObject(i);
            String position = row.getString("position");
            String fieldName = row.getString("field");
            String force = row.getString("force");
            String value = row.getString("value");
            CellPosition cellPosition = parsePosition(position);
            int rowIndex = cellPosition.getRowIndex();
            int cellIndex = cellPosition.getCellIndex();
            XSSFRow sheetRow = sheet.getRow(rowIndex);
            XSSFCell rowCell = sheetRow.getCell(cellIndex);
            if (rowCell != null) {
                if (value != null) {
                    rowCell.setCellValue(value);
                } else if (rowCell.getCellTypeEnum() != CellType.FORMULA) {
                    rowCell.setCellValue(String.valueOf(data.get(fieldName)));
                }
            } else {
                logger.info("request cell do not exist:" + position + "-" + fieldName);
            }
        }
    }

    private void updateSheet(JSONArray cells, Map<String, Object> data, XSSFSheet sheet, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLn(priceList);
        Map<String, HlsPriceListConfigLn> lnMap = new HashMap<>();
        for (HlsPriceListConfigLn ln : lns) {
            lnMap.put(ln.getColumnName(), ln);
        }
        for (int i = 0; cells != null && i < cells.size(); i++) {
            JSONObject row = cells.getJSONObject(i);
            String fieldName = row.getString(KEY_FIELD);
            Object value = row.get(KEY_VALUE);
            HlsPriceListConfigLn ln = lnMap.get(fieldName);
            if (ln == null) {
                logger.warn("Can not get target HlsPriceListConfigLn by field: {}", fieldName);
                continue;
            }
            CellPosition cellPosition = parsePosition(ln.getColumnCode());
            XSSFRow sheetRow = sheet.getRow(cellPosition.getRowIndex());
            if (sheetRow == null) {
                sheetRow = sheet.createRow(cellPosition.getRowIndex());
            }
            XSSFCell rowCell = sheetRow.getCell(cellPosition.getCellIndex());
            if (rowCell == null) {
                rowCell = sheetRow.createCell(cellPosition.getCellIndex());
                logger.info("request cell do not exist:" + ln.getColumnCode() + "-" + fieldName);
            }
            setCellValue(row, value, rowCell);
        }
    }

    private void updateSheetLines(JSONArray lines, Map<String, Object> data, XSSFSheet sheet, String priceList) {
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, PRICE_TYPE_MULTI_LINE);
        String multiLineFrom = hd.getMultiLineFrom();
        List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();
        if (StringUtils.isEmpty(multiLineFrom)) {
            logger.warn("Found empty multiLineFrom", multiLineFrom);
            return;
        }
        if (CollectionUtils.isEmpty(configLns)) {
            logger.warn("Found empty HlsPriceListConfigLn of multi-line");
            return;
        }
        Map<String, String> lineMap = new HashMap<>();
        configLns.forEach(t -> lineMap.put(t.getColumnName(), t.getColumnCode()));
        Integer from = Integer.valueOf(multiLineFrom);

        for (int i = 0; i < lines.size(); i++) {
            JSONObject line = lines.getJSONObject(i);
            int time = line.getIntValue("time");
            JSONArray lineData = line.getJSONArray("data");
            int rownum = time + from;
            XSSFRow row = sheet.getRow(rownum - 1);
            if (row == null) {
                row = sheet.createRow(rownum - 1);
            }
            for (int j = 0; j < lineData.size(); j++) {
                JSONObject dataObject = lineData.getJSONObject(j);
                setCellFormat(dataObject, configLns);
                String field = dataObject.getString(KEY_FIELD);
                String value = dataObject.getString(KEY_VALUE);
                String code = lineMap.get("dcr_" + field);
                if (code == null) {
                    continue;
                }
                CellPosition cellPosition = parsePosition(code + rownum);
                XSSFCell cell = row.getCell(cellPosition.getCellIndex());
                if (cell == null) {
                    cell = row.createCell(cellPosition.getCellIndex());
                }
                setCellValue(dataObject, value, cell);
            }
        }

    }

    private void setCellValue(JSONObject dataObject, Object value, XSSFCell cell) {
        if (StringUtils.isEmpty(value.toString())) {
            cell.setCellValue("");
            return;
        }
        if (ArraysUtil.contains(DOUBLE_FORMATS, dataObject.getString(KEY_FORMAT))) {
            cell.setCellValue(Double.parseDouble(value.toString()));
        } else if (ArraysUtil.contains(DATE_FORMATS, dataObject.getString(KEY_FORMAT))) {
            if (NumberUtils.isNumber(value.toString())) {
                cell.setCellValue(HlsBeanRefUtilServiceImpl.parseDate(String.valueOf(Math.round(Double.parseDouble(value.toString())))));
            } else {
                cell.setCellValue(HlsBeanRefUtilServiceImpl.parseDate(value.toString()));
            }
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 行下标从0开始
     * 列下标从0开始
     *
     * @param position
     * @return
     */
    private CellPosition parsePosition(String position) {
        if (StringUtils.isEmpty(position)) {
            throw new RuntimeException("Empty cell position string.");
        }
        String cellString = StringUtils.replaceChars(position, "1234567890", null);
        if (StringUtils.isEmpty(cellString) || !StringUtils.isAlpha(cellString)) {
            throw new RuntimeException("Illegal cellIndex string: " + position);
        }
        String rowString = position.substring(cellString.length());
        if (StringUtils.isEmpty(rowString) || !StringUtils.isNumeric(rowString)) {
            throw new RuntimeException("Illegal rowIndex string: " + position);
        }

        CellPosition cellPosition = new CellPosition();
        char[] chars = cellString.toUpperCase(Locale.CHINA).toCharArray();
        int cellIndex = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            int value = chars[i] - 'A';
            for (int j = 0; j < chars.length - 1 - i; j++) {
                value *= 26;
            }
            cellIndex += value;
        }
        cellPosition.setRowIndex(Integer.valueOf(rowString) - 1);
        cellPosition.setCellIndex(cellIndex);
        return cellPosition;
    }

    private void readSheet(XSSFWorkbook wb, XSSFSheet sheet, JSONObject jsonObject) {
        readSheet(wb, sheet, jsonObject, false);
    }

    private void readSheet(XSSFWorkbook wb, XSSFSheet sheet, JSONObject jsonObject, boolean valueOnly) {
        JSONArray rows = jsonObject.getJSONArray(KEY_ROWS);
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            int rowIndex = row.getIntValue("index");
            XSSFRow sheetRow = sheet.createRow(rowIndex);
            JSONArray cells = row.getJSONArray(KEY_CELLS);
            for (int cellIndex = 0; cells != null && cellIndex < cells.size(); cellIndex++) {
                JSONObject cell = cells.getJSONObject(cellIndex);
                Object value = cell.get(KEY_VALUE);
                int index = cell.getIntValue(KEY_INDEX);
                if (index < 0) {
                    continue;
                }
                String formula = cell.getString(KEY_FORMULA);
                if (formula != null && !valueOnly) {
                    XSSFCell rowCell = sheetRow.createCell(index);
                    if (formula.indexOf("#REF!") == -1) {
                        rowCell.setCellFormula(formula);
                    }
                } else if (value != null) {
                    XSSFCell rowCell = sheetRow.createCell(index);
                    setCellValue(row, value, rowCell);
//                    rowCell.setCellValue(value);

                }
            }
        }
    }

    class CellPosition {
        private int rowIndex = -1;
        private int cellIndex = -1;

        public int getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public int getCellIndex() {
            return cellIndex;
        }

        public void setCellIndex(int cellIndex) {
            this.cellIndex = cellIndex;
        }
    }

    public List<HlsCusLonContractRepayment> saveRepayemntFromQuotationCashflow(IRequest iRequest, Long withdrawId, Long quotationId, Long newQuotationId) {
        List<HlsCusLonContractRepayment> lonContractRepaymentList = new ArrayList<>();
        HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflowParameter.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);

        HlsCusLonContractWithdraw withdraw = lonContractWithdrawMapper.selectByPrimaryKey(withdrawId);
        HlsCusLonContract lonContract = new HlsCusLonContract();
        lonContract.setContractId(withdraw.getContractId());
        lonContract = lonContracWithdrawMapper.select(lonContract).get(0);


        for (int i = 0; i < prjQuotationCashflowList.size(); i++) {

            HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();

            HlsCusPrjQuotationCashflow model = prjQuotationCashflowList.get(i);
            HlsCusPrjQuotation data = new HlsCusPrjQuotation();
            data.setQuotationId(model.getQuotationId());
            data = prjQuotationService.selectByPrimaryKey(iRequest, data);
            BeanRefUtils.beanToBean(model, lonContractRepayment, hlsBeanRefUtilService);
            lonContractRepayment.setPlannedDueDate(model.getDueDate());
            lonContractRepayment.setPlannedCalcDate(model.getDueDate());
            lonContractRepayment.setPlannedDueAmount(model.getDueAmount());
            //lonContractRepayment.setTimes(prjQuotationCashflowList.get(i).getTimes().longValue());
            lonContractRepayment.setWithdrawId(withdrawId);
            lonContractRepayment.setContractId(withdraw.getContractId());
            lonContractRepayment.setWriteOffFlag("NOT");
            lonContractRepayment.setCurrency(data.getCurrency());
            lonContractRepayment.setInterestAccrualBalance(prjQuotationCashflowList.get(i).getOutstandingPrincipal());
            HlsCusLonContractRepayment lonContractRepaymentQ = new HlsCusLonContractRepayment();
            //lonContractRepaymentQ.setTimes(lonContractRepayment.getTimes());
            if(lonContractRepayment.getCfItem() == 301) {
                lonContractRepaymentQ.setTimes(model.getPrincipalTimes());
            }
            if(lonContractRepayment.getCfItem() == 302) {
                lonContractRepaymentQ.setTimes(model.getInterestTimes());
            }
            lonContractRepaymentQ.setWithdrawId(lonContractRepayment.getWithdrawId());
            lonContractRepaymentQ.setCfItem(lonContractRepayment.getCfItem());
            List<HlsCusLonContractRepayment> l = lonContractRepaymentService.select(iRequest, lonContractRepaymentQ, 1, 1);
            if (l.size() > 0) {
                l.get(0).setDueDate(lonContractRepayment.getDueDate());
                l.get(0).setDueAmount(lonContractRepayment.getDueAmount());
                l.get(0).setInterestAccrualBalance(prjQuotationCashflowList.get(i).getOutstandingPrincipal());
                if("Finance_OnePay_YH".equals(data.getPriceList()) && lonContractRepayment.getCfItem() == 301){
                    lonContractRepayment.setTimes(data.getLeaseTimes());
                }
                lonContractRepaymentService.updateByPrimaryKeySelective(iRequest, l.get(0));
            } else {
                //301,302,213,214,211,212,303,304,206,203,201
                if (lonContractRepayment.getCfItem() == 213 || lonContractRepayment.getCfItem() == 214
                        || lonContractRepayment.getCfItem() == 211 || lonContractRepayment.getCfItem() == 212 || lonContractRepayment.getCfItem() == 303 || lonContractRepayment.getCfItem() == 304
                        || lonContractRepayment.getCfItem() == 206 || lonContractRepayment.getCfItem() == 203 || lonContractRepayment.getCfItem() == 201
                ) {
                    lonContractRepayment.setPaymentMethod("TT");
                }
                if(lonContractRepayment.getCfItem() == 301){
                    lonContractRepayment.setDueDate(model.getPrincipalDueDate());
                    lonContractRepayment.setPlannedDueDate(model.getPrincipalDueDate());
                    lonContractRepayment.setPlannedCalcDate(model.getPrincipalDueDate());
                    lonContractRepayment.setTimes(model.getPrincipalTimes());
                }
                if(lonContractRepayment.getCfItem() == 302){
                    lonContractRepayment.setDueDate(model.getInterestDueDate());
                    lonContractRepayment.setPlannedDueDate(model.getInterestDueDate());
                    lonContractRepayment.setPlannedCalcDate(model.getInterestDueDate());
                    lonContractRepayment.setTimes(model.getInterestTimes());
                }
                if("Finance_OnePay_YH".equals(data.getPriceList()) && lonContractRepayment.getCfItem() == 301){
                    lonContractRepayment.setTimes(data.getLeaseTimes());
                }
                lonContractRepaymentService.insertSelective(iRequest, lonContractRepayment);
            }

            lonContractRepaymentList.add(lonContractRepayment);
        }
        return lonContractRepaymentList;

    }

    public List<HlsCusLonContractRepayment> saveRepayemntFromQuotationCashflow(IRequest iRequest, Long withdrawId, Long quotationId) {

        // 自定义查询sql
        Example example = new Example(HlsCusLonContractRepayment.class);
        Example.Criteria criteria = example.createCriteria();
        List<String> conds = new ArrayList<>(3);
        conds.add("20");
        conds.add("301");
        conds.add("302");
        conds.add("213");
        conds.add("214");
        conds.add("211");
        conds.add("212");
        conds.add("303");
        conds.add("304");

        conds.add("206");
        conds.add("203");

        criteria.andEqualTo("withdrawId", withdrawId).andIn("cfItem", conds).andEqualTo("writeOffFlag", "NOT");

        // 查询需要删除的repayment记录,对于211,212,303,304现金流来说，如果有bpid，那就说明客户自己改动过，就别删，如果没有bpid那么就全部都删掉
        List<HlsCusLonContractRepayment> repaymentList = lonContractRepaymentMapper.selectByExample(example);
        lonContractRepaymentService.batchDelete(repaymentList);

        repaymentList = saveRepayemntFromQuotationCashflow(iRequest, withdrawId, quotationId, quotationId);
        return repaymentList;
    }

    public void saveWithdrawQuotationFromQuotation(IRequest iRequest, Long quotationId){
        HlsCusPrjQuotation quotation=new HlsCusPrjQuotation();
        quotation.setQuotationId(quotationId);
        quotation=prjQuotationService.selectByPrimaryKey(iRequest,quotation);

        HlsCusLonContractQuotation withdrawQuotation =new HlsCusLonContractQuotation();
        withdrawQuotation.setWithdrawId(quotation.getSourceDocumentId());
        HlsCusLonContractQuotation withdrawQuotation1 =new HlsCusLonContractQuotation();
        withdrawQuotation1=lonContractQuotationService.select(iRequest,withdrawQuotation,1,9999).get(0);

        withdrawQuotation.setQuotationId(withdrawQuotation1.getQuotationId());
        withdrawQuotation.setLoanAmount(quotation.getFinanceAmount());
        withdrawQuotation.setStartActiveDate(quotation.getLeaseStartDate());
        withdrawQuotation.setEndActiveDate(quotation.getLeaseEndDate());
        withdrawQuotation.setLoanTimes(quotation.getLeaseTimes());
        withdrawQuotation.setLoanRate(quotation.getIntRate());
        withdrawQuotation.setIntRate(quotation.getIntRate());
        withdrawQuotation.setPlanInterestPaymentDate(quotation.getPlanInterestPaymentDate());
        withdrawQuotation.setPlanPrincipalPaymentDate(quotation.getPlanPrincipalPaymentDate());
        withdrawQuotation.setInterestCycle(quotation.getRentingFrequency());
        withdrawQuotation.setPrincipalCycle(quotation.getRentingFrequency());
        withdrawQuotation.setCalcInterestYearDays(quotation.getInterestYearDays().toString());
        withdrawQuotation.setBaseRate(quotation.getBaseRate());
        withdrawQuotation.setXirr(quotation.getXirr());
        withdrawQuotation.setExchangeRate(quotation.getExchangeRate());
        withdrawQuotation.setCurrency(quotation.getCurrency());
        withdrawQuotation.setTaxRate(quotation.getVatRate());
        withdrawQuotation.setInterestPaymentDate(quotation.getInterestPaymentDate());
        withdrawQuotation.setPrincipalPaymentDate(quotation.getPrincipalPaymentDate());
        withdrawQuotation.setLoanTerm(quotation.getLeaseTerm());
        lonContractQuotationService.updateByPrimaryKeySelective(iRequest,withdrawQuotation);


        //将报价信息更新到lon_contract_withdraw
        HlsCusLonContractWithdraw lonContractWithdraw = new HlsCusLonContractWithdraw();
        //lonContractWithdraw = lonContractWithdrawMapper.selectByPrimaryKey(quotation.getSourceDocumentId());
        lonContractWithdraw.setWithdrawId(quotation.getSourceDocumentId());
        lonContractWithdraw.setDueDate(quotation.getLeaseStartDate());
        lonContractWithdraw.setWithdrawEndDate(quotation.getLeaseEndDate());
        lonContractWithdraw.setInitialWithdrawEndDate(quotation.getLeaseEndDate());
        lonContractWithdraw.setDueAmount(quotation.getFinanceAmount());
        lonContractWithdraw.setLoanTimes(quotation.getLeaseTimes());
        lonContractWithdraw.setWithdrawRate(quotation.getIntRate());
        lonContractWithdraw.setIntRate(quotation.getIntRate());
        lonContractWithdraw.setCalcInterestYearDays(quotation.getInterestYearDays().toString());
        lonContractWithdraw.setBaseRate(quotation.getBaseRate());
        lonContractWithdraw.setDeposit(quotation.getDeposit());
        lonContractWithdraw.setXirr(quotation.getXirr());
        lonContractWithdraw.setExchangeRate(quotation.getExchangeRate());
        lonContractWithdraw.setCurrency(quotation.getCurrency());
        lonContractWithdrawService.updateByPrimaryKeySelective(iRequest, lonContractWithdraw);
    }


    @Override
    public HlsCusPrjQuotation PrjQuotationSubmit(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation) throws Exception {

        //校验公式是否被修改
        String jsonStr = JSON.toJSONString(hlsCusCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, hlsCusPrjQuotation.getSheets(), hlsCusPrjQuotation.getPriceList(), "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);
        if (prjQuotationDto.getLeaseTimes() == null) {
            throw new IllegalArgumentException("期数未取到！");
        }
        if (prjQuotationDto.getPriceList() == "Finance_OnePay_YH" && prjQuotationDto.getLeaseEndDate().compareTo(prjQuotationDto.getPlanInterestPaymentDate())<0) {
            throw new IllegalArgumentException("融资到期一次性还本首期付息日需小于等于到期日！");
        }
        prjQuotationDto.setPriceList(hlsCusPrjQuotation.getPriceList());
        prjQuotationDto.setSheets(hlsCusPrjQuotation.getSheets());
        prjQuotationDto.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        prjQuotationDto.setSourceDocumentCategory(hlsCusPrjQuotation.getSourceDocumentCategory());
        prjQuotationDto.setQuotationType(hlsCusPrjQuotation.getQuotationType());
        prjQuotationDto.setSourceDocumentId(hlsCusPrjQuotation.getSourceDocumentId());
        prjQuotationDto.setStatus(hlsCusPrjQuotation.getStatus());
        prjQuotationDto.setDescription(hlsCusPrjQuotation.getDescription());

        prjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

        //获取项目项下的报价
        HlsCusPrjQuotation savePrjQuotation = new HlsCusPrjQuotation();
        savePrjQuotation.setQuotationId(prjQuotationDto.getQuotationId());
        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
        hlsCusPrjQuotationDetails.setQuotationId(savePrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList;
        detailsList = prjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);

        if (detailsList.size() > 0) {
            //detailsList.get(0).setSheets(hlsCusPrjQuotation.getSheets());
            detailsList.get(0).setSheets(hlsCusPrjQuotation.getCompressSheets());
            detailsList.get(0).set__status("update");
        } else {
            hlsCusPrjQuotationDetails.setSheets(hlsCusPrjQuotation.getCompressSheets());
            detailsList.add(hlsCusPrjQuotationDetails);
            detailsList.get(0).set__status("insert");
        }
        prjQuotationDetailsService.batchUpdate(iRequest, detailsList);
        //保存现金流表
        prjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);

        prjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

        //将报价现金流更新到还款表
        saveRepayemntFromQuotationCashflow(iRequest, prjQuotationDto.getSourceDocumentId(), prjQuotationDto.getQuotationId());

        //将报价现金流更新到提款报价表 HlsCusLonContractQuotation  HlsCusLonContractWithdraw
        saveWithdrawQuotationFromQuotation(iRequest, prjQuotationDto.getQuotationId());

        return prjQuotationDto;
    }

}
