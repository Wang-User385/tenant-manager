package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcSaveService;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.partner.service.IPrjQuotationCalcService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import jodd.util.ArraysUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class PrjQuotationCalcServiceImpl extends BaseServiceImpl<HlsCusPrjQuotation> implements IPrjQuotationCalcService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String KEY_INDEX = "index";
    private static final String KEY_CELLS = "cells";
    private static final String KEY_ROWS = "rows";
    public static final String KEY_VALUE = "value";
    private static final String KEY_FORMULA = "formula";
    public static final String KEY_FIELD = "field";
    private static final String PRICE_TYPE_SINGLE = "SINGLE";
    private static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    private static final String KEY_FORMAT = "format";
    private static final String[] DOUBLE_FORMATS = {"0%", "0.00"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy"};
    public static final String LEASE_START_DATE = "lease_start_date";
    public static final Long DUE_AMOUNT_CF_ITEM = 1L;
    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationDetailsMapper hlsCusPrjQuotationDetailsMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;
    @Autowired
    private HlsPriceListConfigLnMapper configLnMapper;
    @Autowired
    private HlsPriceListConfigHdMapper configHdMapper;
    @Autowired
    private HlsCalcSaveService hlsCalcSaveService;

    private Double doubleDataTran(Object var) {
        double result;
        if (var == null) {
            result = 0D;
        } else {
            result = Double.valueOf(var.toString());
        }
        return result;
    }
    private String stringDataTran(Object var) {
        String result;
        if (var == null) {
            result = "";
        } else {
            result = var.toString();
        }
        return result;
    }
    private String DateDataTran(Object var){
        String result;
        if (var == null) {
            result = "";
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            result = df.format(var);
        }
        return result;
    }

    private void readSheets(XSSFWorkbook wb, JSONArray array) {
        readSheets(wb, array, false);
    }
    private void readSheets(XSSFWorkbook wb, JSONArray array, boolean valueOnly) {
        for(int k = 0; k < array.size(); k++) {
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
    private void setCellValue(JSONObject dataObject, Object value, XSSFCell cell) {
        if(value == null){
            logger.info("value is null "+dataObject.toJSONString());
        }else {
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
                if (value instanceof Integer) {
                    cell.setCellValue(Integer.valueOf(value.toString()));
                } else if (value instanceof BigDecimal) {
                    cell.setCellValue(Double.valueOf(value.toString()));
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }
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

    private List<HlsPriceListConfigLn> getPriceListConfigLns(String priceList, String type){
        HlsPriceListConfigLn ln = new HlsPriceListConfigLn();
        ln.setTableType(type);
        ln.setPriceList(priceList);
        List<HlsPriceListConfigLn> listConfigLns = configLnMapper.selectHlsPriceListConfiglineByPriceList(ln);
        return listConfigLns;
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
                    case "NUMBER":
                        object.put(KEY_FORMAT, DOUBLE_FORMATS[1]);
                        break;
                    case "DATE":
                        object.put(KEY_FORMAT, DATE_FORMATS[0]);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void updateSheet(JSONArray cells, Map<String, Object> data, XSSFWorkbook wb, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLns(priceList,PRICE_TYPE_SINGLE);
        Map<String, HlsPriceListConfigLn> lnMap = new HashMap<>();
        for (HlsPriceListConfigLn ln : lns) {
            lnMap.put(ln.getColumnName(), ln);
        }
        for (int i = 0; cells != null && i < cells.size(); i++) {
            JSONObject row = cells.getJSONObject(i);
            String fieldName = row.getString(KEY_FIELD);
            Object value = row.get(KEY_VALUE);
            HlsPriceListConfigLn ln = lnMap.get(fieldName.toLowerCase());
            if(ln == null){
                ln = lnMap.get(fieldName.toUpperCase());
            }
            if (ln == null) {
                logger.warn("Can not get target HlsPriceListConfigLn by field: {}", fieldName);
                continue;
            }
            //根据sheet名称获取当前sheet对象
            XSSFSheet sheet = wb.getSheet(ln.getSheetName());

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

    private void writeBack(XSSFWorkbook wb, JSONArray array, String priceList) {
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        List<HlsPriceListConfigLn> singleLines = getPriceListConfigLns(priceList,PRICE_TYPE_SINGLE);
        List<HlsPriceListConfigHd> hdList = getPriceListConfigHd(priceList, PRICE_TYPE_MULTI_LINE);

        int jsonRowIndex = 0;

        for (int i = 0; i < singleLines.size(); i++) {
            HlsPriceListConfigLn singleLine = singleLines.get(i);
            String columnCode = singleLine.getColumnCode();
            XSSFSheet sheet = wb.getSheet(singleLine.getSheetName());
            JSONObject sheetObject = getJsonObjectBySheetName(singleLine.getSheetName(),array);
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
                }catch (Exception e){
                    logger.info(e.getMessage());
                }
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

        for(HlsPriceListConfigHd hd:hdList) {
            String multiLineFrom = hd.getMultiLineFrom();
            String multiLineTo = hd.getMultiLineTo();
            List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();
            if (StringUtils.isAnyEmpty(multiLineFrom, multiLineTo)) {
                logger.warn("Found empty multiLineFrom[{}] or multiLineTo[{}]", multiLineFrom, multiLineTo);
                return;
            }
            if (CollectionUtils.isEmpty(configLns)) {
                logger.warn("Found empty HlsPriceListConfigLn of multi-line", multiLineFrom, multiLineTo);
                return;
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
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                            try {
                                CellValue cellValue = evaluator.evaluate(cell);
                                Object rawValue = getRawValue(cellValue);
                                cellsObject.getJSONObject(j).put(KEY_VALUE, rawValue);
                            }catch (Exception e){
                                logger.error(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }
    private List<HlsPriceListConfigHd> getPriceListConfigHd(String priceList, String type) {
        HlsPriceListConfigHd hd = new HlsPriceListConfigHd();
        hd.setPriceList(priceList);
        hd.setTableType(type);
        List<HlsPriceListConfigHd> headers = configHdMapper.select(hd);
        if (CollectionUtils.isEmpty(headers)) {
            return null;
        }
        for(HlsPriceListConfigHd configHd:headers){
            HlsPriceListConfigLn ln = new HlsPriceListConfigLn();

            ln.setConfigHdId(configHd.getConfigHdId());
            configHd.setHlsPriceListConfigLns(configLnMapper.selectHlsPriceListConfiglineByPriceList(ln));
        }
        return headers;
    }
    private JSONObject getJsonObjectBySheetName(String sheetName,JSONArray jsonArray){
        for(int i = 0; i < jsonArray.size(); i++){
            if(sheetName.equals(jsonArray.getJSONObject(i).getString("name"))){
                return jsonArray.getJSONObject(i);
            }
        }
        return new JSONObject();
    }
    private Object getRawValue(XSSFCell cell) {
        Object rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = cell.getNumericCellValue();
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

    private static String encodeURIComponent(String input){
        if (null == input || "".equals(input.trim())){
            return input;
        }

        int l = input.length();
        StringBuilder o = new StringBuilder(l * 3);
        try{
            for (int i = 0; i < l; i++ ){
                String e = input.substring(i, i + 1);
                if (ALLOWED_CHARS.indexOf(e) == -1){
                    byte[] b = e.getBytes("utf-8");
                    o.append(getHex(b));
                    continue;
                }
                o.append(e);
            }
            return o.toString();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return input;
    }
    private static String getHex(byte buf[]){
        StringBuilder o = new StringBuilder(buf.length * 3);
        for (int i = 0; i < buf.length; i++ ){
            int n = (int)buf[i] & 0xff;
            o.append("%");
            if (n < 0x10){
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }

    @Override
    public void prjQuotationCalc(Long quotationId, IRequest iRequest) throws Exception{
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation = prjQuotationMapper.selectByPrimaryKey(prjQuotation);

        //step1如果价目表发生了变化，则需要删除
        if(!"N".equals(prjQuotation.getOldPriceList())){
            if (!prjQuotation.getPriceList().equals(prjQuotation.getOldPriceList())) {
                hlsCusPrjQuotationDetailsMapper.deleteDetailsById(prjQuotation);
            }
        }

        //step2 构造需要替换的参数值
        Map quotationMap = (Map) prjQuotationMapper.queryQuotationInfoByQuotationIdMarketing(prjQuotation).get(0);
        List<Map> mapList = new ArrayList<>();
        //年利率
        Map map1 = new HashMap();
        map1.put("field", "int_rate");
        map1.put("value", doubleDataTran(quotationMap.get("int_rate"))/100);
        mapList.add(map1);
        //租赁期数
        Map map2 = new HashMap();
        map2.put("field", "lease_times");
        map2.put("value", doubleDataTran(quotationMap.get("lease_times")));
        mapList.add(map2);
        //起息日
        Map map3 = new HashMap();
        map3.put("field", "lease_start_date");
        map3.put("value", DateDataTran(quotationMap.get("lease_start_date")));
        mapList.add(map3);
        //申请融资额
        Map map4 = new HashMap();
        map4.put("field", "finance_amount");
        map4.put("value", doubleDataTran(quotationMap.get("finance_amount")));
        mapList.add(map4);

        //step3 判断是否有detail数据，如果没有则取报价模板的sheet
        String priceList = prjQuotation.getPriceList();
        String sourceSheet;
        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(priceList);
        hlsCalcConfig = priceListMapper.selectByPrimaryKey(hlsCalcConfig);
        HlsCusPrjQuotationDetails details = new HlsCusPrjQuotationDetails();
        details.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = hlsCusPrjQuotationDetailsMapper.select(details);
        if(CollectionUtils.isNotEmpty(detailsList) && detailsList.size() == 1){
            sourceSheet = detailsList.get(0).getSheets();
        }else{
            sourceSheet = hlsCalcConfig.getSheets();
        }

        //step4 解压压缩过的sheets,将价目表转成JSONArray
        String stringSheets = GzipUtil.atob(sourceSheet);
        String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
        String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");
        JSONArray array = JSONArray.parseArray(jsonSheets);
        XSSFWorkbook wb = new XSSFWorkbook();
        readSheets(wb, array);

        //step5 将参数mapList数据转成JSONArray,并设置单元格格式,数据更新到sheet里面,回写
        Map<String, Object> data = new HashMap<>();
        JSONArray transArray = getTransObject(mapList);
        List<HlsPriceListConfigLn> hlsPriceListConfigLns = getPriceListConfigLns(priceList, PRICE_TYPE_SINGLE);
        setCellFormat(transArray, hlsPriceListConfigLns);
        updateSheet(transArray, data, wb, priceList);
        writeBack(wb, array, priceList);

        //step6 设置hlsCusPrjQuotation对象，调用报价计算逻辑
        HlsCusPrjQuotation hlsCusPrjQuotation = JSONObject.parseObject(JSON.toJSONString(data), HlsCusPrjQuotation.class);
        hlsCusPrjQuotation.setQuotationId(prjQuotation.getQuotationId());
        String sheetsArray = encodeURIComponent((JSON.toJSONString(array)));
        String zipSheets = new String(GzipUtil.compress(sheetsArray),"iso-8859-1");
        String compressSheets = GzipUtil.btoa(zipSheets);
        hlsCusPrjQuotation.setSheets(JSON.toJSONString(array));
        hlsCusPrjQuotation.setCompressSheets(compressSheets);
        hlsCusPrjQuotation.setPriceList(prjQuotation.getPriceList());
        hlsCusPrjQuotation.setSourceDocumentCategory(prjQuotation.getSourceDocumentCategory());
        hlsCusPrjQuotation.setLeaseTimes(prjQuotation.getLeaseTimes());
        hlsCusPrjQuotation.setVatRate(prjQuotation.getVatRate());
        hlsCusPrjQuotation.setDataClass(prjQuotation.getDataClass());
        hlsCusPrjQuotation.setSourceDocumentId(prjQuotation.getSourceDocumentId());
        hlsCusPrjQuotation.setBusinessType(prjQuotation.getBusinessType());
        hlsCalcSaveService.savePrjQuotation(iRequest, hlsCusPrjQuotation);

        //step7 更新old_price_list字段
        HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
        cusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(prjQuotation);
        cusPrjQuotation.setOldPriceList(prjQuotation.getPriceList());
        prjQuotationMapper.updateByPrimaryKeySelective(cusPrjQuotation);
    }
}
