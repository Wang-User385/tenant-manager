package com.hand.hls.fin.service.impl;

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
import com.hand.hls.fin.dto.HlsCusLonContractQuotation;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.mapper.HlsCusLonContractWithdrawMapper;
import com.hand.hls.fin.service.HlsCusLonContractQuotationService;

import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import jodd.util.ArraysUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
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
public class HlsCusLonContractQuotationServiceImpl extends BaseServiceImpl<HlsCusLonContractQuotation> implements HlsCusLonContractQuotationService {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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

    private static final String sourceDocumentCategory = "LON_CONTRACT_WITHDRAW";

    private static final String QUOTATION_TYPE = "MAJOR";

    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";
    private static final String APP_CALCULATE = "APP_CALCULATE";

    @Autowired
    private HlsCalcConfigMapper hlsCalcConfigMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationService prjQuotationService;
    @Autowired
    HlsCusLonContractWithdrawMapper lonContractWithdrawMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;
    @Autowired
    private HlsCusPrjQuotationDetailsMapper hlsCusPrjQuotationDetailsMapper;
    @Autowired
    private HlsPriceListConfigLnMapper configLnMapper;
    @Autowired
    private HlsPriceListConfigHdMapper configHdMapper;
    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;
    @Autowired
    private HlsCalcSaveService hlsCalcSaveService;

    public static String encodeURIComponent(String input)
    {
        if (null == input || "".equals(input.trim()))
        {
            return input;
        }

        int l = input.length();
        StringBuilder o = new StringBuilder(l * 3);
        try
        {
            for (int i = 0; i < l; i++ )
            {
                String e = input.substring(i, i + 1);
                if (ALLOWED_CHARS.indexOf(e) == -1)
                {
                    byte[] b = e.getBytes("utf-8");
                    o.append(getHex(b));
                    continue;
                }
                o.append(e);
            }
            return o.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return input;
    }

    private static String getHex(byte buf[])
    {
        StringBuilder o = new StringBuilder(buf.length * 3);
        for (int i = 0; i < buf.length; i++ )
        {
            int n = (int)buf[i] & 0xff;
            o.append("%");
            if (n < 0x10)
            {
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }

    JSONArray getTransObject(List<Map> maps) {
        JSONArray jsonArray = new JSONArray();
        for (Map map : maps) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_FIELD, map.get(KEY_FIELD));
            jsonObject.put(KEY_VALUE, map.get(KEY_VALUE));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /*
     * 将cells里面的数据更新到sheet里面
     * */
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

    public static String toDate(int days) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = new GregorianCalendar(1900, 0, -1);

        Date d = c.getTime();
        Date _d = DateUtils.addDays(d, days); //42605是距离1900年1月1日的天数
        return simpleDateFormat.format(_d);
    }

    JSONObject getJsonObjectBySheetName(String sheetName,JSONArray jsonArray){
        for(int i = 0; i < jsonArray.size(); i++){
            if(sheetName.equals(jsonArray.getJSONObject(i).getString("name"))){
                return jsonArray.getJSONObject(i);
            }
        }
        return new JSONObject();
    }

    public void writeBack(XSSFWorkbook wb, JSONArray array, String priceList) {
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


    public static int getDays(Date end, Date start) {
        Calendar aCalendar = Calendar.getInstance();
        Calendar bCalendar = Calendar.getInstance();
        aCalendar.setTime(end);
        bCalendar.setTime(start);
        int days = 0;
        while (aCalendar.before(bCalendar)) {
            days++;
            aCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (days == 0) {
            aCalendar.setTime(start);
            bCalendar.setTime(end);
            while (aCalendar.before(bCalendar)) {
                days++;
                aCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        return days;
    }

    /**
     * 获取两个日期相差的月数
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }
    public Double doubleDataTran(Object var) {
        double result;
        if (var == null) {
            result = 0D;
        } else {
            result = Double.valueOf(var.toString());
        }
        return result;
    }

    public Double doubleDataTran(Double var) {
        double result;
        if (var == null) {
            result = 0D;
        } else {
            result = var;
        }
        return result;
    }

    public String stringDataTran(Object var) {
        String result;
        if (var == null) {
            result = "";
        } else {
            result = var.toString();
        }
        return result;
    }

    void checkQuotation(HlsCusPrjQuotation quotation) throws HlsCusException {
        //校验基准利率值BASE_RATE
        Double baseRate = quotation.getBaseRate();
        if(baseRate == null){
            throw new HlsCusException("基准利率值不能为空！");
        }
    }

    private List<HlsPriceListConfigLn> getPriceListConfigLns(String priceList, String type){
        HlsPriceListConfigLn ln = new HlsPriceListConfigLn();

        ln.setTableType(type);
        ln.setPriceList(priceList);
        List<HlsPriceListConfigLn> listConfigLns = configLnMapper.selectHlsPriceListConfiglineByPriceList(ln);
        return listConfigLns;
    }
    @Override
    public List<HlsCusPrjQuotation> createCalcByWithdraw(IRequest iRequest, Long withdrawId, String priceList) throws Exception {
        HlsCusLonContractWithdraw lonContractWithdraw = lonContractWithdrawMapper.selectByPrimaryKey(withdrawId);
        HlsCusLonContractQuotation withdrawQuotation=new HlsCusLonContractQuotation();
        withdrawQuotation.setWithdrawId(withdrawId);
        withdrawQuotation=self().select(iRequest,withdrawQuotation,1,9999).get(0);

        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(lonContractWithdraw.getPriceList());
        List<HlsCalcConfig> hlsCalcConfigList = hlsCalcConfigMapper.select(hlsCalcConfig);
        if (hlsCalcConfigList.size() == 0) {
            throw new ResMessageException("找不到对应的报价！");
        }

        hlsCalcConfig = hlsCalcConfigList.get(0);
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setSourceDocumentCategory(HlsCusLonContractWithdraw.DOCUMENT_CATEGORY);
        quotation.setSourceDocumentId(withdrawId);
        List<HlsCusPrjQuotation> quotationList = hlsCusPrjQuotationMapper.select(quotation);
        if(quotationList.size() >0){
            quotationList.get(0).setPriceList(priceList);
        }
        quotation.setLeaseItemAmount(withdrawQuotation.getLoanAmount());
        quotation.setFinanceAmount(withdrawQuotation.getLoanAmount());
        quotation.setIntRateType(withdrawQuotation.getIntRateType());
        quotation.setFloatingWayRate(withdrawQuotation.getFloatingWayRange());
        quotation.setBaseRate(withdrawQuotation.getBaseRate());
        quotation.setBaseRateType(withdrawQuotation.getBaseRateType());
        quotation.setIntRate(withdrawQuotation.getIntRate());
        quotation.setRentingFrequency(withdrawQuotation.getInterestCycle());
        quotation.setPlanInterestPaymentDate(withdrawQuotation.getPlanInterestPaymentDate());
        quotation.setPlanPrincipalPaymentDate(withdrawQuotation.getPlanPrincipalPaymentDate());
        quotation.setLeaseStartDate(withdrawQuotation.getStartActiveDate());
        quotation.setLeaseEndDate(withdrawQuotation.getEndActiveDate());
        quotation.setLeaseTimes(withdrawQuotation.getLoanTimes());
        quotation.setVatRate(withdrawQuotation.getTaxRate());
        quotation.setInterestPaymentDate(withdrawQuotation.getInterestPaymentDate());
        quotation.setPrincipalPaymentDate(withdrawQuotation.getPrincipalPaymentDate());
        quotation.setLeaseTerm(withdrawQuotation.getLoanTerm());
        quotation.setInterestYearDays(Long.parseLong(withdrawQuotation.getCalcInterestYearDays()));
        quotation.setPriceList(priceList);

        if (quotationList.size() == 0) {
            quotation.setSourceDocumentId(withdrawId);
            quotation.setSourceDocumentCategory(sourceDocumentCategory);
            quotation.setQuotationType(QUOTATION_TYPE);
            quotation.setStatus(HlsCusConstant.WFL.APPROVED);
            quotation.setSheets(hlsCalcConfig.getSheets());
            quotation = prjQuotationService.insertSelective(iRequest, quotation);
        } else {
            quotation.setQuotationId(quotationList.get(0).getQuotationId());
            if(quotationList.get(0).getOldPriceList() !=null && !quotationList.get(0).getOldPriceList().equals("N")){
                if (!quotation.getPriceList().equals(quotationList.get(0).getOldPriceList())) {
                    //更换报价后删除details信息
                    hlsCusPrjQuotationDetailsMapper.deleteDetailsById(quotationList.get(0));
                }
            }
            prjQuotationService.updateByPrimaryKeySelective(iRequest, quotation);
        }

        HlsCusPrjQuotation cusQuotation = new HlsCusPrjQuotation();
        cusQuotation.setSourceDocumentCategory(HlsCusLonContractWithdraw.DOCUMENT_CATEGORY);
        cusQuotation.setSourceDocumentId(withdrawId);
        cusQuotation = hlsCusPrjQuotationMapper.select(cusQuotation).get(0);
        quotationReCalc(iRequest,cusQuotation.getQuotationId(),false);

        List<HlsCusPrjQuotation> list = new ArrayList<>();
        list.add(cusQuotation);
        return list;
    }

    public void quotationReCalc(IRequest request, Long quotationId,Boolean calcFlag) throws Exception {

        List<Map> mapList = new ArrayList<>();

        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(prjQuotation);

        //校验参数
        if(calcFlag) {
            checkQuotation(prjQuotation);
        }
        Map quotationMap = (Map) hlsCusPrjQuotationMapper.queryQuotationInfoByQuotationIdLoan(prjQuotation).get(0);

        //传入计算字段
        //报价名称
        Map map = new HashMap();
        map.put("field", "description");
        map.put("value", stringDataTran(quotationMap.get("description")));
        mapList.add(map);

        //QUOTATION_NUMBER
        Map map1 = new HashMap();
        map1.put("field", "quotation_number");
        map1.put("value", stringDataTran(quotationMap.get("quotation_number")));
        mapList.add(map1);

        //报价方案
        Map map2 = new HashMap();
        map2.put("field", "price_list_n");
        map2.put("value", stringDataTran(quotationMap.get("price_list_n")));
        mapList.add(map2);

        Map map3 = new HashMap();
        map3.put("field", "price_list");
        map3.put("value", stringDataTran(quotationMap.get("price_list")));
        mapList.add(map3);

        //租赁业务类型
        Map map4 = new HashMap();
        map4.put("field", "business_type");
        map4.put("value", stringDataTran(quotationMap.get("business_type")));
        mapList.add(map4);

        Map map5 = new HashMap();
        map5.put("field", "business_type_n");
        map5.put("value", stringDataTran(quotationMap.get("business_type_n")));
        mapList.add(map5);

        //币种
        Map map6 = new HashMap();
        map6.put("field", "currency_n");
        map6.put("value", stringDataTran(quotationMap.get("currency_n")));
        mapList.add(map6);

        //币种
        Map map7 = new HashMap();
        map7.put("field", "currency");
        map7.put("value", stringDataTran(quotationMap.get("currency_n")));
        mapList.add(map7);

        //融资金额
        Map map8 = new HashMap();
        map8.put("field", "finance_amount");
        map8.put("value", doubleDataTran(quotationMap.get("finance_amount")));
        mapList.add(map8);

        //支付期数
        Map map9 = new HashMap();
        map9.put("field", "lease_times");
        map9.put("value", doubleDataTran(quotationMap.get("lease_times")));
        mapList.add(map9);

        // 租赁物价款
        Map map10 = new HashMap();
        map10.put("field", "lease_item_amount");
        map10.put("value", doubleDataTran(quotationMap.get("lease_item_amount")));
        mapList.add(map10);

        //租赁期限
        Map map11 = new HashMap();
        map11.put("field", "lease_term");
        map11.put("value", doubleDataTran(quotationMap.get("lease_term")));
        mapList.add(map11);

        //预计放款日
        if(quotationMap.get("lease_start_date") != null) {
            Map map12 = new HashMap();
            map12.put("field", "lease_start_date");
            map12.put("value", df.format(quotationMap.get("lease_start_date")));
            mapList.add(map12);
        }

        //租赁期届满日
        if(quotationMap.get("lease_end_date") != null) {
            Map map13 = new HashMap();
            map13.put("field", "lease_end_date");
            map13.put("value", df.format(quotationMap.get("lease_end_date")));
            mapList.add(map13);
        }

        //基准利率标准
        Map map14 = new HashMap();
        map14.put("field", "base_rate_type_n");
        map14.put("value", stringDataTran(quotationMap.get("base_rate_type_n")));
        mapList.add(map14);

        //基准利率标准
        Map map15 = new HashMap();
        map15.put("field", "base_rate_type");
        map15.put("value", stringDataTran(quotationMap.get("base_rate_type")));
        mapList.add(map15);

        //基准利率值
        Map map16 = new HashMap();
        map16.put("field", "base_rate");
        map16.put("value", doubleDataTran(quotationMap.get("base_rate")));
        mapList.add(map16);

        Map map17 = new HashMap();
        map17.put("field", "int_rate_type");
        map17.put("value", stringDataTran(quotationMap.get("int_rate_type")));
        mapList.add(map17);

        //利率类型
        Map map18 = new HashMap();
        map18.put("field", "int_rate_type_n");
        map18.put("value", stringDataTran(quotationMap.get("int_rate_type_n")));
        mapList.add(map18);

        //年利率(%)
        Map map19 = new HashMap();
        map19.put("field", "int_rate");
        map19.put("value", doubleDataTran(quotationMap.get("int_rate")));
        mapList.add(map19);

        //利率浮动类型
        Map map20 = new HashMap();
        map20.put("field", "float_type");
        map20.put("value", stringDataTran(quotationMap.get("float_type")));
        mapList.add(map20);

        //利率浮动类型
        Map map21 = new HashMap();
        map21.put("field", "float_type_n");
        map21.put("value", stringDataTran(quotationMap.get("float_type_n")));
        mapList.add(map21);

        //浮动值（BP）
        Map map22 = new HashMap();
        map22.put("field", "floating_way_rate");
        map22.put("value", doubleDataTran(quotationMap.get("floating_way_rate")));
        mapList.add(map22);

        //首次付息日
        if(prjQuotation.getPlanInterestPaymentDate() != null) {
            Map map23 = new HashMap();
            map23.put("field", "plan_interest_payment_date");
            map23.put("value", df.format(prjQuotation.getPlanInterestPaymentDate()));
            mapList.add(map23);
        }

        //首次还本日
        if(prjQuotation.getPlanPrincipalPaymentDate() != null) {
            Map map24 = new HashMap();
            map24.put("field", "plan_principal_payment_date");
            map24.put("value", df.format(prjQuotation.getPlanPrincipalPaymentDate()));
            mapList.add(map24);
        }

        //还款频率
        Map map25 = new HashMap();
        map25.put("field", "renting_frequency");
        map25.put("value", doubleDataTran(quotationMap.get("renting_frequency")));
        mapList.add(map25);

        //还款频率
        Map map26 = new HashMap();
        map26.put("field", "renting_frequency_n");
        map26.put("value", stringDataTran(quotationMap.get("renting_frequency_n")));
        mapList.add(map26);

        //增值税率
        Map map27 = new HashMap();
        map27.put("field", "vat_rate");
        map27.put("value", doubleDataTran(prjQuotation.getVatRate()));
        mapList.add(map27);

        //付息日
        if(prjQuotation.getInterestPaymentDate() != null) {
            Map map28 = new HashMap();
            map28.put("field", "interest_payment_date");
            map28.put("value", doubleDataTran(prjQuotation.getInterestPaymentDate()));
            mapList.add(map28);
        }

        //还本日
        if(prjQuotation.getPrincipalPaymentDate() != null) {
            Map map29 = new HashMap();
            map29.put("field", "principal_payment_date");
            map29.put("value", doubleDataTran(prjQuotation.getPrincipalPaymentDate()));
            mapList.add(map29);
        }

        //年计息天数
        if(prjQuotation.getInterestYearDays() != null) {
            Map map30 = new HashMap();
            map30.put("field", "interest_year_days");
            map30.put("value", doubleDataTran(prjQuotation.getInterestYearDays()));
            mapList.add(map30);
        }

        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(quotationMap.get("price_list").toString());
        hlsCalcConfig = priceListMapper.selectByPrimaryKey(hlsCalcConfig);

        updateQuotaion(prjQuotation, mapList, hlsCalcConfig,calcFlag);

        //更新项目原报价字段
        HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
        cusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(prjQuotation);
        cusPrjQuotation.setOldPriceList(prjQuotation.getPriceList());
        hlsCusPrjQuotationMapper.updateByPrimaryKey(cusPrjQuotation);
    }

    public void updateQuotaion(HlsCusPrjQuotation quotation, List<Map> maps, HlsCalcConfig hlsCalcConfig,Boolean calcFlag) throws Exception {

        Map<String, Object> data = new HashMap<>();
        String sourceSheet;

        //获取价目表类型
        String priceList = quotation.getPriceList();

        //这里判断一下，如果当前报价存在details，则取报价表里面的sheets,否则取价目表配置的
        HlsCusPrjQuotationDetails details = new HlsCusPrjQuotationDetails();
        details.setQuotationId(quotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = hlsCusPrjQuotationDetailsMapper.select(details);

        if(CollectionUtils.isNotEmpty(detailsList) && detailsList.size() == 1){
            sourceSheet = detailsList.get(0).getSheets();
        }else{
            sourceSheet = hlsCalcConfig.getSheets();
        }

        //解压压缩过的sheets
        String stringSheets = GzipUtil.atob(sourceSheet);
        String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
        String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");

        //将价目表转成JSONArray
        JSONArray array = JSONArray.parseArray(jsonSheets);
        XSSFWorkbook wb = new XSSFWorkbook();
        readSheets(wb, array);

        //将map数据转成JSONArray
        JSONArray transArray = getTransObject(maps);

        List<HlsPriceListConfigLn> hlsPriceListConfigLns = getPriceListConfigLns(priceList, PRICE_TYPE_SINGLE);
        setCellFormat(transArray, hlsPriceListConfigLns);

        updateSheet(transArray, data, wb, priceList);
        writeBack(wb, array, priceList);

        HlsCusPrjQuotation hlsCusPrjQuotation = JSONObject.parseObject(JSON.toJSONString(data), HlsCusPrjQuotation.class);
        hlsCusPrjQuotation.setQuotationId(quotation.getQuotationId());

        String sheetsArray = encodeURIComponent((JSON.toJSONString(array)));
        String zipSheets = new String(GzipUtil.compress(sheetsArray),"iso-8859-1");
        String compressSheets = GzipUtil.btoa(zipSheets);

        hlsCusPrjQuotation.setSheets(JSON.toJSONString(array));
        hlsCusPrjQuotation.setCompressSheets(compressSheets);

        hlsCusPrjQuotation.setPriceList(quotation.getPriceList());
        hlsCusPrjQuotation.setSourceDocumentCategory(quotation.getSourceDocumentCategory());
        hlsCusPrjQuotation.setLeaseTimes(quotation.getLeaseTimes());
        hlsCusPrjQuotation.setVatRate(quotation.getVatRate());
        hlsCusPrjQuotation.setDataClass(quotation.getDataClass());
        hlsCusPrjQuotation.setSourceDocumentId(quotation.getSourceDocumentId());

        HlsCalcConfig calcConfig = new HlsCalcConfig();
        calcConfig.setPriceList("TEST");
        calcConfig.setSheets(compressSheets);
        priceListMapper.updateByPrimaryKeySelective(calcConfig);

        if(calcFlag) {
            hlsCalcSaveService.savePrjQuotation(RequestHelper.getCurrentRequest(true), hlsCusPrjQuotation);
        }else{
            if(quotation.getXirr() == null){
                HlsCusPrjQuotationDetails quotationDetails = new HlsCusPrjQuotationDetails();
                if(CollectionUtils.isEmpty(detailsList)) {

                    quotationDetails.setQuotationId(quotation.getQuotationId());
                    quotationDetails.setSheets(compressSheets);
                    hlsCusPrjQuotationDetailsMapper.insertSelective(quotationDetails);
                }else{
                    quotationDetails = detailsList.get(0);
                    quotationDetails.setSheets(compressSheets);
                    hlsCusPrjQuotationDetailsMapper.updateByPrimaryKeySelective(quotationDetails);
                }
            }
        }
    }

    @Override
    public void saveCalcFront(IRequest iRequest, HlsCusPrjQuotation quotation, String sheets) throws Exception {
        List<Map> mapList = new ArrayList<>();
        HlsCusPrjQuotationDetails details = new HlsCusPrjQuotationDetails();
        details.setQuotationId(quotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = hlsCusPrjQuotationDetailsMapper.select(details);
        if(CollectionUtils.isEmpty(detailsList)){
            details.setSheets(sheets);
            hlsCusPrjQuotationDetailsService.insertSelective(iRequest,details);
        }else if(detailsList.size() == 1){
            details = detailsList.get(0);
            details.setSheets(sheets);
            hlsCusPrjQuotationDetailsService.updateByPrimaryKeySelective(iRequest,details);
        }else{
            throw new HlsCusException("数据异常!");
        }
        HlsCalcConfig calcConfig = new HlsCalcConfig();
        calcConfig.setSheets(sheets);
        updateQuotaion(quotation,mapList,calcConfig,true);
    }
}