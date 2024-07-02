package com.hand.hls.hls.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.hls.service.HlsQuotationCalcService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/6/16
 * @description: 报价计算工具 利用excel
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsQuotationCalcServiceImpl extends BaseServiceImpl<HlsCusPrjQuotation> implements HlsQuotationCalcService {


    public Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public HlsPriceListConfigHdMapper configHdMapper;
    @Autowired
    public HlsPriceListConfigLnMapper configLnMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationDetailsService prjQuotationDetailsService;
    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsCusPrjQuotationDetailsMapper hlsCusPrjQuotationDetailsMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;


    public static final String sourceDocumentCategory = "CONTRACT";
    public static final String KEY_INDEX = "index";
    public static final String KEY_CELLS = "cells";
    public static final String KEY_ROWS = "rows";
    public static final String KEY_VALUE = "value";
    public static final String KEY_FORMULA = "formula";
    public static final String KEY_FIELD = "field";
    public static final String PRICE_TYPE_SINGLE = "SINGLE";
    public static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    public static final String KEY_FORMAT = "format";
    private static final String[] DOUBLE_FORMATS = {"0%", "0.00"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy", "yyyy/m/d"};
    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    class CellPosition {
        public int rowIndex = -1;
        public int cellIndex = -1;

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

    /**
     * @Title: getPriceListConfigLns
     * @Discription: 获取报价行配置
     * @Param: [priceList, type]
     * @Return: java.util.List<com.hand.hls.calc.dto.HlsPriceListConfigLn>
     */
    private List<HlsPriceListConfigLn> getPriceListConfigLns(String priceList, String type) {
        HlsPriceListConfigLn ln = new HlsPriceListConfigLn();
        ln.setTableType(type);
        ln.setPriceList(priceList);
        List<HlsPriceListConfigLn> listConfigLns = configLnMapper.selectHlsPriceListConfiglineByPriceList(ln);
        return listConfigLns;
    }

    /**
     * @Title: getPriceListConfigHd
     * @Discription: 获取报价头行配置
     * @Param: [priceList, type]
     * @Return: java.util.List<com.hand.hls.calc.dto.HlsPriceListConfigHd>
     */
    private List<HlsPriceListConfigHd> getPriceListConfigHd(String priceList, String type) {
        HlsPriceListConfigHd hd = new HlsPriceListConfigHd();
        hd.setPriceList(priceList);
        hd.setTableType(type);
        List<HlsPriceListConfigHd> headers = configHdMapper.select(hd);
        if (CollectionUtils.isEmpty(headers)) {
            return null;
        }
        for (HlsPriceListConfigHd configHd : headers) {
            HlsPriceListConfigLn ln = new HlsPriceListConfigLn();

            ln.setConfigHdId(configHd.getConfigHdId());
            configHd.setHlsPriceListConfigLns(configLnMapper.selectHlsPriceListConfiglineByPriceList(ln));
        }
        return headers;
    }

    /**
     * @Title: getPriceListConfigLn
     * @Discription: 获取指定sheet 行配置
     * @Param: [priceList, sheetName]
     * @Return: java.util.List<com.hand.hls.calc.dto.HlsPriceListConfigLn>
     */
    @Override
    public List<HlsPriceListConfigLn> getPriceListConfigLn(String priceList, String sheetName) {
        return getPriceListConfigLn(priceList, PRICE_TYPE_SINGLE, sheetName).getHlsPriceListConfigLns();
    }

    /**
     * @Title: getPriceListConfigLn
     * @Discription: 获取指定sheet 行配置
     * @Param: [priceList, type, sheetName]
     * @Return: com.hand.hls.calc.dto.HlsPriceListConfigHd
     */
    @Override
    public HlsPriceListConfigHd getPriceListConfigLn(String priceList, String type, String sheetName) {
        HlsPriceListConfigHd hd = new HlsPriceListConfigHd();
        hd.setPriceList(priceList);
        hd.setTableType(type);
        hd.setSheetName(sheetName);
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

    /**
     * @Title: setCellValue
     * @Discription: 给单元格赋值
     * @Param: [dataObject, value, cell]
     * @Return: void
     */
    private void setCellValue(JSONObject dataObject, Object value, XSSFCell cell) {
        if (value == null || StringUtils.isEmpty(value.toString()) && ArraysUtil.contains(DOUBLE_FORMATS, dataObject.getString(KEY_FORMAT))) {
            cell.setCellValue(0D);
            return;
        }
        if (StringUtils.isEmpty(value.toString())) {
            cell.setCellValue("");
            return;
        }
        if (ArraysUtil.contains(DOUBLE_FORMATS, dataObject.getString(KEY_FORMAT))) {
            try {
                cell.setCellValue(Double.parseDouble(value.toString()));
            } catch (Exception e) {
                cell.setCellValue(value.toString());
            }
        } else if (ArraysUtil.contains(DATE_FORMATS, dataObject.getString(KEY_FORMAT))) {
            if (NumberUtils.isNumber(value.toString())) {
                cell.setCellValue(HlsBeanRefUtilServiceImpl.parseDate(String.valueOf(Math.round(Double.parseDouble(value.toString())))));
            } else {
                if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else {
                    cell.setCellValue(HlsBeanRefUtilServiceImpl.parseDate(value.toString()));
                }
            }
        } else {
            if (NumberUtils.isNumber(value.toString())) {
                cell.setCellValue(Double.valueOf(value.toString()));
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    /**
     * @Title: parsePosition
     * @Discription: 解析单元格位置
     * @Param: [position]
     * @Return: com.hand.hls.hls.service.impl.HlsQuotationCalcServiceImpl.CellPosition
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
                value = (value + 1) * 26;
            }
            cellIndex += value;
        }
        cellPosition.setRowIndex(Integer.valueOf(rowString) - 1);
        cellPosition.setCellIndex(cellIndex);
        return cellPosition;
    }


    /**
     * @Title: getRawValue
     * @Discription: 给单元值 格式化
     * @Param: [cell]
     * @Return: java.lang.Object
     */
    private Object getRawValue(XSSFCell cell) {
        Object rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = cell.getNumericCellValue();
                break;
            case STRING:
                rawValue = cell.getStringCellValue();
                break;
            case ERROR:
                //test by song
                //throw new RuntimeException("变更后报价单元格计算错误" + cell.getReference());
            default:
                break;
        }
        return rawValue;
    }

    /**
     * @Title: getRawValue
     * @Discription: 给单元值 格式化
     * @Param: [cell]
     * @Return: java.lang.Object
     */
    public Object getRawValue(CellValue cell) {
        Object rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = cell.getNumberValue();
                break;
            case STRING:
                rawValue = cell.getStringValue();
                break;
            case ERROR:
                throw new RuntimeException("变更后报价单元格计算错误");
            default:
                break;
        }
        return rawValue;
    }


    /**
     * @Title: setCellFormat
     * @Discription: 给单元格 设置 excel 格式
     * @Param: [transArray, hlsPriceListConfigLns]
     * @Return: void
     */
    @Override
    public void setCellFormat(JSONArray transArray, List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
        for (int i = 0; i < transArray.size(); i++) {
            JSONObject jsonObject = transArray.getJSONObject(i);
            setCellFormat(jsonObject, hlsPriceListConfigLns);
        }
    }

    /**
     * @Title: setCellFormat
     * @Discription: 给单元格 设置 excel 格式
     * @Param: [object, hlsPriceListConfigLns]
     * @Return: void
     */
    @Override
    public void setCellFormat(JSONObject object, List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
        for (HlsPriceListConfigLn item : hlsPriceListConfigLns) {
            if (item.getColumnName().equalsIgnoreCase(object.getString(KEY_FIELD))) {
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

    /**
     * @Title: readSheets
     * @Discription: 读取全部sheet
     * @Param: [wb, array]
     * @Return: void
     */
    @Override
    public void readSheets(XSSFWorkbook wb, JSONArray array) {
        readSheets(wb, array, false);
    }

    /**
     * @Title: readSheets
     * @Discription: 读取全部sheet
     * @Param: [wb, array, valueOnly]
     * @Return: void
     */
    @Override
    public void readSheets(XSSFWorkbook wb, JSONArray array, boolean valueOnly) {
        for (int k = 0; k < array.size(); k++) {

            JSONObject jsonObject = array.getJSONObject(k);
            XSSFSheet sheet = wb.createSheet(jsonObject.getString("name"));

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
                    }
                }
            }
        }
    }

    /**
     * @Title: readSheet
     * @Discription: 读取单个sheet
     * @Param: [wb, sheet, jsonObject]
     * @Return: void
     */
    @Override
    public void readSheet(XSSFWorkbook wb, XSSFSheet sheet, JSONObject jsonObject) {
        readSheet(wb, sheet, jsonObject, false);
    }

    /**
     * @Title: readSheet
     * @Discription: 读取单个sheet
     * @Param: [wb, sheet, jsonObject, valueOnly]
     * @Return: void
     */
    @Override
    public void readSheet(XSSFWorkbook wb, XSSFSheet sheet, JSONObject jsonObject, boolean valueOnly) {
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
                    setCellValue(cell, value, rowCell);
                }
            }
        }
    }

    /**
     * @Title: extractHeadDataFromSheet
     * @Discription: 通过sheet 提取头数据
     * @Param: [sheets, priceList, sheetName]
     * @Return: com.alibaba.fastjson.JSONArray
     */
    @Override
    public JSONArray extractHeadDataFromSheet(String sheets, String priceList, String sheetName) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLn(priceList, sheetName);
        JSONArray array = new JSONArray();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        JSONObject sheetJson = (JSONObject) JSONArray.parseArray(sheets).stream().filter(item -> ((JSONObject) item).getString("name").equals(sheetName)).findFirst().get();
        readSheet(wb, sheet, sheetJson, true);
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

    /**
     * @Title: extractLineDataFromSheet
     * @Discription: 通过sheet 提取行数据
     * @Param: [sheets, priceList, sheetName]
     * @Return: com.alibaba.fastjson.JSONArray
     */
    @Override
    public JSONArray extractLineDataFromSheet(String sheets, String priceList, String sheetName) {
        JSONArray lines = new JSONArray();
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, PRICE_TYPE_MULTI_LINE, sheetName);
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
                            case ERROR:
                                throw new RuntimeException("单元格计算有误：" + columnCode + i);
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
                        logger.warn("Can not get value of type: {}", cell.getCellType());
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


    /**
     * @Title: updateSheet
     * @Discription: 更新单个sheet 头
     * @Param: [cells, sheet, priceList, sheetName]
     * @Return: void
     */
    @Override
    public void updateSheet(JSONArray cells, XSSFSheet sheet, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLn(priceList, sheet.getSheetName());

        setCellFormat(cells, lns);

        Map<String, HlsPriceListConfigLn> lnMap = new HashMap<>();
        for (HlsPriceListConfigLn ln : lns) {
            lnMap.put(ln.getColumnName().toLowerCase(), ln);
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

    /**
     * @Title: updateSheetLines
     * @Discription: 更新单个sheet 行
     * @Param: [lines, sheet, priceList, sheetName]
     * @Return: void
     */
    @Override
    public void updateSheetLines(JSONArray lines, XSSFSheet sheet, String priceList) {
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, PRICE_TYPE_MULTI_LINE, sheet.getSheetName());
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
        configLns.forEach(t -> lineMap.put(t.getColumnName().toLowerCase(), t.getColumnCode()));
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
                Object value = dataObject.get(KEY_VALUE);
                String code = lineMap.get(field);
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


    JSONObject getJsonObjectBySheetName(String sheetName, JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            if (sheetName.equals(jsonArray.getJSONObject(i).getString("name"))) {
                return jsonArray.getJSONObject(i);
            }
        }
        return new JSONObject();
    }

    /**
     * @Title: writeBack
     * @Discription: 回写sheet
     * @Param: [wb, array, priceList]
     * @Return: void
     */
    @Override
    public void writeBack(XSSFWorkbook wb, JSONArray array, String priceList) {
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        List<HlsPriceListConfigLn> singleLines = getPriceListConfigLns(priceList, PRICE_TYPE_SINGLE);
        List<HlsPriceListConfigHd> hdList = getPriceListConfigHd(priceList, PRICE_TYPE_MULTI_LINE);

        int jsonRowIndex = 0;

        for (int i = 0; i < singleLines.size(); i++) {
            HlsPriceListConfigLn singleLine = singleLines.get(i);
            String columnCode = singleLine.getColumnCode();
            XSSFSheet sheet = wb.getSheet(singleLine.getSheetName());
            JSONObject sheetObject = getJsonObjectBySheetName(singleLine.getSheetName(), array);
            JSONArray rowsObject = sheetObject.getJSONArray(KEY_ROWS);

            CellPosition cellPosition = parsePosition(columnCode);
            XSSFRow row = sheet.getRow(cellPosition.getRowIndex());
            if (row == null) {
                logger.warn("Found empty row at {}", cellPosition.getRowIndex());
                continue;
            }
            XSSFCell cell = row.getCell(cellPosition.getCellIndex());
            if (cell == null) {
                logger.warn("Found empty cell at {}", columnCode);
                continue;
            }
            if (cell.getCellTypeEnum() == CellType.FORMULA) {
                try {
                    cell = evaluator.evaluateInCell(cell);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            }
            Object rawValue = getRawValue(cell);
            /*logger.info("row : " + cellPosition.getRowIndex());
            logger.info("cell : " + cellPosition.getCellIndex());
            logger.info("rawValue :  " + rawValue.toString());*/

            JSONObject rowObject = getRowObject(rowsObject, cellPosition);
            if (rowObject == null) {
                logger.warn("Found empty rowObject at {}", cellPosition.getRowIndex());
                continue;
            }
            JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
            if (cellsObject == null) {
                logger.warn("Found empty cellsObject at {}", cellPosition.getRowIndex());
                continue;
            }
            boolean foundCell = false;
            for (int cellIndex = 0; cellIndex < cellsObject.size(); cellIndex++) {
                JSONObject cellObject = cellsObject.getJSONObject(cellIndex);
                int intValue = cellObject.getIntValue(KEY_INDEX);
                if (intValue == cellPosition.getCellIndex()) {
                    foundCell = true;
                    cellObject.put(KEY_VALUE, rawValue);
                    break;
                }
            }
            if (!foundCell) {
//                没有找到对应的cell，创建一个
                JSONObject cellObject = new JSONObject();
                cellObject.put(KEY_VALUE, rawValue);
                cellObject.put(KEY_INDEX, cellPosition.getCellIndex());
                cellsObject.add(cellObject);
            }
        }

        for (HlsPriceListConfigHd hd : hdList) {
            String multiLineFrom = hd.getMultiLineFrom();
            String multiLineTo = hd.getMultiLineTo();
            List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();
            if (StringUtils.isAnyEmpty(multiLineFrom, multiLineTo)) {
                logger.warn("Found empty multiLineFrom[{}] or multiLineTo[{}]", multiLineFrom, multiLineTo);
                continue;
            }
            if (CollectionUtils.isEmpty(configLns)) {
                logger.warn("Found empty HlsPriceListConfigLn of multi-line", multiLineFrom, multiLineTo);
                continue;
            }
            Integer from = Integer.valueOf(multiLineFrom);
            Integer to = Integer.valueOf(multiLineTo);
            for (Integer i = from; i <= to; i++) {
                boolean writeData = false;
                for (HlsPriceListConfigLn ln : configLns) {
                    String columnCode = ln.getColumnCode();
                    CellPosition cellPosition = parsePosition(columnCode + i);
                    XSSFSheet sheet = wb.getSheet(ln.getSheetName());
                    JSONObject sheetObject = getJsonObjectBySheetName(ln.getSheetName(), array);
                    JSONArray rowsObject = sheetObject.getJSONArray(KEY_ROWS);

                    XSSFRow row = sheet.getRow(cellPosition.getRowIndex());
                    if (row == null) {
                        logger.warn("Found empty row at {}", cellPosition.getRowIndex());
                        continue;
                    }
                    XSSFCell cell = row.getCell(cellPosition.getCellIndex());
                    if (cell == null) {
                        logger.warn("Found empty cell at {}", columnCode + i);
                        continue;
                    }
                    Object rawValue = getRawValue(cell);


                    JSONObject rowObject = getRowObject(rowsObject, cellPosition);
                    if (rowObject == null) {
                        logger.warn("Found empty rowObject at {}", cellPosition.getRowIndex());
                        continue;
                    }
                    JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
                    if (cellsObject == null) {
                        logger.warn("Found empty cellsObject at {}", cellPosition.getRowIndex());
                        continue;
                    }
                    boolean foundCell = false;
                    for (int cellIndex = 0; cellIndex < cellsObject.size(); cellIndex++) {
                        JSONObject cellObject = cellsObject.getJSONObject(cellIndex);
                        int intValue = cellObject.getIntValue(KEY_INDEX);
                        if (intValue == cellPosition.getCellIndex()) {
                            foundCell = true;
                            writeData = true;
                            cellObject.put(KEY_VALUE, rawValue);
                            break;
                        }
                    }
                    if (!foundCell) {
//                没有找到对应的cell，创建一个
                        JSONObject cellObject = new JSONObject();
                        cellObject.put(KEY_VALUE, rawValue);
                        cellObject.put(KEY_INDEX, cellPosition.getCellIndex());
                        cellsObject.add(cellObject);
                        writeData = true;
                    }
                }
                if (!writeData) {
//               FIXME: 一整行都没有写入数据，考虑一下后面都为空行
                    break;
                }
            }

            evaluator.clearAllCachedResultValues();
            for (int i = 0; i < Integer.valueOf(multiLineTo); i++) {

                for (int k = 0; k < array.size(); k++) {
                    JSONObject sheetObject = array.getJSONObject(k);
                    JSONArray rowsObject = sheetObject.getJSONArray(KEY_ROWS);
                    JSONObject rowObject = getRowObject(rowsObject, i);
                    if (rowObject == null) {
                        continue;
                    }
                    JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
                    if (cellsObject == null) {
                        continue;
                    }
                    XSSFSheet sheet = wb.getSheet(sheetObject.getString("name"));
                    for (int j = 0; j < cellsObject.size(); j++) {
                        XSSFCell cell = sheet.getRow(i).getCell(cellsObject.getJSONObject(j).getIntValue(KEY_INDEX));
                        if (cell != null && cell.getCellTypeEnum().equals(CellType.FORMULA)) {
                            try {
                                CellValue cellValue = evaluator.evaluate(cell);
                                Object rawValue = getRawValue(cellValue);
                                cellsObject.getJSONObject(j).put(KEY_VALUE, rawValue);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }


    public static String encodeURIComponent(String input) {
        if (null == input || "".equals(input.trim())) {
            return input;
        }

        int l = input.length();
        StringBuilder o = new StringBuilder(l * 3);
        try {
            for (int i = 0; i < l; i++) {
                String e = input.substring(i, i + 1);
                if (ALLOWED_CHARS.indexOf(e) == -1) {
                    byte[] b = e.getBytes("utf-8");
                    o.append(getHex(b));
                    continue;
                }
                o.append(e);
            }
            return o.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return input;
    }

    private static String getHex(byte buf[]) {
        StringBuilder o = new StringBuilder(buf.length * 3);
        for (int i = 0; i < buf.length; i++) {
            int n = (int) buf[i] & 0xff;
            o.append("%");
            if (n < 0x10) {
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }

    /**
     * @Title: unzipSheet
     * @Discription: excel sheet 解压
     * @Param: [compressSheet]
     * @Return: java.lang.String
     */
    @Override
    public String unzipSheet(String compressSheet) throws UnsupportedEncodingException {
        String stringSheets = GzipUtil.atob(compressSheet);
        String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
        String jsonSheets = URLDecoder.decode(unzipSheets, "utf-8");
        return jsonSheets;
    }


    /**
     * @Title: getCompressSheets
     * @Discription: excel sheet 压缩
     * @Param: [array]
     * @Return: java.lang.String
     */
    @Override
    public String getCompressSheets(String array) throws UnsupportedEncodingException {
        String sheetsArray = encodeURIComponent((array));
        String zipSheets = new String(GzipUtil.compress(sheetsArray), "iso-8859-1");
        String compressSheets = GzipUtil.btoa(zipSheets);
        return compressSheets;
    }


    /**
     * @Title: extractHeadDataFromObject
     * @Discription: 通过查询数据对象，提取头数据
     * @Param: [object, priceList, sheetName]
     * @Return: com.alibaba.fastjson.JSONArray
     */
    @Override
    public JSONArray extractHeadDataFromObject(Object object, String priceList, String sheetName) {

        JSONObject headData = JSONObject.parseObject(JSONObject.toJSON(object).toString());
        JSONArray modifiedCells = new JSONArray();
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, HlsQuotationCalcServiceImpl.PRICE_TYPE_SINGLE, sheetName);
        List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();
        for (HlsPriceListConfigLn configLn : configLns) {
            JSONObject modifiedCell = new JSONObject();
            modifiedCell.put(KEY_FIELD, configLn.getColumnName().toLowerCase());
            Set<String> headDataSet = headData.keySet();
            Long count = headDataSet.stream().filter(item -> StringUtil.underlineToCamelhump(configLn.getColumnName().toLowerCase()).equalsIgnoreCase(item)).count();
            if (count > 0) {
                Object value = headData.get(StringUtil.underlineToCamelhump(configLn.getColumnName().toLowerCase()));
                if ("DATE".equals(configLn.getColumnType())) {
                    Date date = new Date(Long.parseLong(value.toString()));
                    modifiedCell.put(KEY_VALUE, df.format(date));
                } else {
                    modifiedCell.put(KEY_VALUE, value);
                }
                modifiedCells.add(modifiedCell);
            }
        }
        return modifiedCells;
    }


    /**
     * @Title: extractLineDataFromObject
     * @Discription: 通过查询数据对象，提取行数据
     * @Param: [object, priceList, sheetName]
     * @Return: com.alibaba.fastjson.JSONArray
     */
    @Override
    public JSONArray extractLineDataFromObject(Object object, String priceList, String sheetName) {

        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, HlsQuotationCalcServiceImpl.PRICE_TYPE_MULTI_LINE, sheetName);
        List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();

        JSONArray lines = new JSONArray();
        List list = (ArrayList) object;
        for (int i = 0; i < list.size(); i++) {

            JSONObject lineData = JSONObject.parseObject(JSONObject.toJSON(list.get(i)).toString());
            JSONObject line = new JSONObject();
            JSONArray dataArray = new JSONArray();

            line.put("time", i);
            line.put("data", dataArray);

            for (HlsPriceListConfigLn configLn : configLns) {

                JSONObject data = new JSONObject();
                String columnName = configLn.getColumnName().toLowerCase();
                data.put(KEY_FIELD, columnName);

                Object value = lineData.get(StringUtil.underlineToCamelhump(columnName.toLowerCase()));

                if ("DATE".equals(configLn.getColumnType())) {
                    Date date = new Date(Long.parseLong(value.toString()));
                    data.put(KEY_VALUE, df.format(date));
                } else {
                    data.put(KEY_VALUE, value);
                }
                dataArray.add(data);

            }
            lines.add(line);
        }
        return lines;
    }


    /**
     * @Title: updateQuotationFromSheet
     * @Discription: 回写 prj_quotation 相关表
     * @Param: [iRequest, quotationId, priceList, array]
     * @Return: void
     */
    @Override
    public void updateQuotationFromSheet(IRequest iRequest, Long quotationId, String priceList, JSONArray array) throws Exception {
        //回写prj_quotation
        HlsCusPrjQuotation prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotationId);
        String jsonStr = JSON.toJSONString(hlsCusCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, JSON.toJSONString(array), priceList, "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);

        String sheets = JSON.toJSONString(array);
        String compressSheets = getCompressSheets(sheets);

        prjQuotationDto.setPriceList(priceList);
        prjQuotationDto.setSheets(sheets);
        prjQuotationDto.setCompressSheets(compressSheets);
        prjQuotationDto.setSourceDocumentCategory(prjQuotation.getSourceDocumentCategory());
        prjQuotationDto.setSourceDocumentId(prjQuotation.getSourceDocumentId());
        prjQuotationDto.setQuotationId(prjQuotation.getQuotationId());
        prjQuotationDto.setDataClass("CONTRACT_PLAN");
        prjQuotationDto.setLeaseTimes(prjQuotation.getAltLeaseTimes());
        prjQuotationDto.setStatus("NEW");
        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

        //回写prj_quotation_details
        HlsCusPrjQuotationDetails prjQuotationDetail = new HlsCusPrjQuotationDetails();
        prjQuotationDetail.setQuotationId(quotationId);
        List<HlsCusPrjQuotationDetails> prjQuotationDetailList = hlsCusPrjQuotationDetailsMapper.select(prjQuotationDetail);
        prjQuotationDetail = prjQuotationDetailList.get(0);
        prjQuotationDetail.setSheets(compressSheets);
        prjQuotationDetailsService.updateByPrimaryKeySelective(iRequest, prjQuotationDetail);


        HlsCalcConfig calcConfig = new HlsCalcConfig();
        calcConfig.setPriceList("TEST");
        calcConfig.setSheets(compressSheets);
        priceListMapper.updateByPrimaryKeySelective(calcConfig);

        //回写prj_quotation_cashflow
        prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotationId);
        prjQuotation.setSheets(sheets);
        prjQuotation.setCompressSheets(compressSheets);
        hlsCusPrjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotation);
    }


    private JSONArray getTransObject(List<Map> maps) {
        JSONArray jsonArray = new JSONArray();
        for (Map map : maps) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_FIELD, map.get(KEY_FIELD));
            jsonObject.put(KEY_VALUE, map.get(KEY_VALUE));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JSONArray extractHeadDataFromMap(List<Map> maps, String priceList, String sheetName) {
        //将map数据转成JSONArray
        JSONArray transArray = getTransObject(maps);
        HlsPriceListConfigHd priceListConfigHd = getPriceListConfigLn(priceList, HlsQuotationCalcServiceImpl.PRICE_TYPE_MULTI_LINE, sheetName);
        List<HlsPriceListConfigLn> hlsPriceListConfigLns = priceListConfigHd.getHlsPriceListConfigLns();
        setCellFormat(transArray, hlsPriceListConfigLns);
        return transArray;
    }

}
