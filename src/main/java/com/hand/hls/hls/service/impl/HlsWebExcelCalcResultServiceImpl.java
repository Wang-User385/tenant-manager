package com.hand.hls.hls.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.hls.dto.HlsWebExcel;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;
import com.hand.hls.hls.mapper.HlsWebExcelCalcResultMapper;
import com.hand.hls.hls.mapper.HlsWebExcelConfigHdMapper;
import com.hand.hls.hls.mapper.HlsWebExcelConfigLnMapper;
import com.hand.hls.hls.mapper.HlsWebExcelMapper;
import com.hand.hls.hls.service.IHlsWebExcelCalcResultService;
import com.hand.hls.webexcel.service.IWebExcelCalcService;
import hls.core.utils.exception.HlsCusException;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsWebExcelCalcResultServiceImpl extends BaseServiceImpl<HlsWebExcelCalcResult> implements AppContextInitListener,IHlsWebExcelCalcResultService{


    public static final String KEY_INDEX = "index";
    public static final String KEY_CELLS = "cells";
    public static final String KEY_ROWS = "rows";
    public static final String KEY_VALUE = "value";
    public static final String KEY_FORMULA = "formula";
    public static final String KEY_FIELD = "field";
    public static final String PRICE_TYPE_SINGLE = "SINGLE";
    public static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    public static final String KEY_FORMAT = "format";
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private static final String[] DOUBLE_FORMATS = {"0%", "0.00"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy", "yyyy/m/d"};
    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

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


    @Autowired
    private HlsWebExcelCalcResultMapper hlsWebExcelCalcResultMapper;

    @Autowired
    private HlsWebExcelMapper hlsWebExcelMapper;

    @Autowired
    public HlsWebExcelConfigHdMapper configHdMapper;
    @Autowired
    public HlsWebExcelConfigLnMapper configLnMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, IWebExcelCalcService> excelInterfaceImplMap = new HashMap<>();

    @Override
    public void contextInitialized(ApplicationContext appCtx) {
        Map<String, IWebExcelCalcService> map = appCtx.getBeansOfType(IWebExcelCalcService.class);

        map.forEach((k, v) -> {
            excelInterfaceImplMap.put(v.getSourceDocumentCategory(), v);
        });

    }

    @Override
    public String queryWebExcelCalcResult(IRequest iRequest, HlsWebExcelCalcResult result) {

        if(result.getResultId() != null){
            result = self().selectByPrimaryKey(iRequest,result);
            return result.getSheets();
        }else if(result.getExcelId() != null){
            HlsWebExcel hlsWebExcel = new HlsWebExcel();
            hlsWebExcel.setExcelId(result.getExcelId());
            hlsWebExcel = hlsWebExcelMapper.selectByPrimaryKey(hlsWebExcel);
            return hlsWebExcel.getSheets();
        }
        return null;
    }


    @Override
    public HlsWebExcelCalcResult calcExcel(IRequest iRequest, HlsWebExcelCalcResult calcResult) throws HlsCusException {

        HlsWebExcelCalcResult hlsWebExcelCalcResult = new HlsWebExcelCalcResult();
        IWebExcelCalcService webExcelCalcImpl = excelInterfaceImplMap.get(calcResult.getSourceDocumentCategory());

        if(webExcelCalcImpl == null){
            logger.error("未找到类别为 {},{} 的实现类",calcResult.getSourceDocumentCategory(),"IWebExcelCalcService");
            throw new HlsCusException("未找到对应WebExcel的单据类别，请联系管理员!");
        }else{
            hlsWebExcelCalcResult = webExcelCalcImpl.calcExcel(iRequest,calcResult);
            hlsWebExcelCalcResult.setExcelId(calcResult.getExcelId());


            hlsWebExcelCalcResult.setSheets(calcResult.getCompressSheets());

            if(calcResult.getResultId() == null) {
                if(hlsWebExcelCalcResult.getSourceDocumentId() == null || hlsWebExcelCalcResult.getSourceDocumentCategory() == null){
                    throw new HlsCusException("未找到对应WebExcel的单据类别，请联系管理员!");
                }

                self().insert(iRequest, hlsWebExcelCalcResult);
            }else{
                hlsWebExcelCalcResult.setResultId(calcResult.getResultId());
                self().updateByPrimaryKeySelective(iRequest,hlsWebExcelCalcResult);
            }
        }
        return hlsWebExcelCalcResult;
    }

    /**
     * @Title: extractLineDataFromObject
     * @Discription: 通过查询数据对象，提取行数据
     * @Param: [object, priceList, sheetName]
     * @Return: com.alibaba.fastjson.JSONArray
     */
    @Override
    public JSONArray extractLineDataFromObject(Object object, String sheetName,Long excelId) {

        HlsWebExcelConfigHd hd = getPriceListConfigLn(PRICE_TYPE_MULTI_LINE, sheetName,excelId);
        List<HlsWebExcelConfigLn> configLns = hd.getHlsWebExcelConfigLnList();

        JSONArray lines = new JSONArray();
        List list = (ArrayList) object;
        for (int i = 0; i < list.size(); i++) {

            JSONObject lineData = JSONObject.parseObject(JSONObject.toJSON(list.get(i)).toString());
            JSONObject line = new JSONObject();
            JSONArray dataArray = new JSONArray();

            line.put("time", i);
            line.put("data", dataArray);

            for (HlsWebExcelConfigLn configLn : configLns) {

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
     * @Title: getPriceListConfigLn
     * @Discription: 获取指定sheet 行配置
     * @Param: [priceList, type, sheetName]
     * @Return: com.hand.hls.calc.dto.HlsPriceListConfigHd
     */
    @Override
    public HlsWebExcelConfigHd getPriceListConfigLn(String type, String sheetName,Long excelId) {
        HlsWebExcelConfigHd hd = new HlsWebExcelConfigHd();
        hd.setExcelId(excelId);
        hd.setTableType(type);
        hd.setSheetName(sheetName);
        List<HlsWebExcelConfigHd> headers = configHdMapper.select(hd);
        if (CollectionUtils.isEmpty(headers) || headers.size() != 1) {
            return hd;
        }
        hd = headers.get(0);
        HlsWebExcelConfigLn ln = new HlsWebExcelConfigLn();
        ln.setConfigHdId(hd.getConfigHdId());
        hd.setHlsWebExcelConfigLnList(configLnMapper.select(ln));
        return hd;
    }

    /**
     * @Title: updateSheetLines
     * @Discription: 更新单个sheet 行
     * @Param: [lines, sheet, priceList, sheetName]
     * @Return: void
     */
    @Override
    public void updateSheetLines(JSONArray lines, XSSFSheet sheet, Long excelId) {
        HlsWebExcelConfigHd hd = getPriceListConfigLn(PRICE_TYPE_MULTI_LINE, sheet.getSheetName(),excelId);
        String multiLineFrom = hd.getMultiLineFrom();
        List<HlsWebExcelConfigLn> configLns = hd.getHlsWebExcelConfigLnList();
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

    /**
     * @Title: setCellFormat
     * @Discription: 给单元格 设置 excel 格式
     * @Param: [object, hlsPriceListConfigLns]
     * @Return: void
     */
    @Override
    public void setCellFormat(JSONObject object, List<HlsWebExcelConfigLn> hlsWebExcelConfigLns) {
        for (HlsWebExcelConfigLn item : hlsWebExcelConfigLns) {
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
     * @Title: writeBack
     * @Discription: 回写sheet
     * @Param: [wb, array, priceList]
     * @Return: void
     */
    @Override
    public void writeBack(XSSFWorkbook wb, JSONArray array, Long excelId) {
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        List<HlsWebExcelConfigLn> singleLines = getPriceListConfigLns(excelId, PRICE_TYPE_SINGLE);
        List<HlsWebExcelConfigHd> hdList = getPriceListConfigHd(excelId, PRICE_TYPE_MULTI_LINE);

        int jsonRowIndex = 0;

        for (int i = 0; i < singleLines.size(); i++) {
            HlsWebExcelConfigLn singleLine = singleLines.get(i);
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

        for (HlsWebExcelConfigHd hd : hdList) {
            String multiLineFrom = hd.getMultiLineFrom();
            String multiLineTo = hd.getMultiLineTo();
            List<HlsWebExcelConfigLn> configLns = hd.getHlsWebExcelConfigLnList();
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
                for (HlsWebExcelConfigLn ln : configLns) {
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
                        if (cell != null ) {//&& cell.getCellTypeEnum().equals(CellType.FORMULA)
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


    /**
     * @Title: getPriceListConfigLns
     * @Discription: 获取报价行配置
     * @Param: [priceList, type]
     * @Return: java.util.List<com.hand.hls.calc.dto.HlsPriceListConfigLn>
     */
    private List<HlsWebExcelConfigLn> getPriceListConfigLns(Long excelId, String type) {
        HlsWebExcelConfigLn ln = new HlsWebExcelConfigLn();
        ln.setTableType(type);
        ln.setExcelId(excelId);
        List<HlsWebExcelConfigLn> listConfigLns = configLnMapper.selectHlsWebExcelConfiglineByExcelCode(ln);
        return listConfigLns;
    }

    /**
     * @Title: getPriceListConfigHd
     * @Discription: 获取报价头行配置
     * @Param: [priceList, type]
     * @Return: java.util.List<com.hand.hls.calc.dto.HlsPriceListConfigHd>
     */
    private List<HlsWebExcelConfigHd> getPriceListConfigHd(Long excelId, String type) {
        HlsWebExcelConfigHd hd = new HlsWebExcelConfigHd();
        hd.setExcelId(excelId);
        hd.setTableType(type);
        List<HlsWebExcelConfigHd> headers = configHdMapper.select(hd);
        if (CollectionUtils.isEmpty(headers)) {
            return null;
        }
        for (HlsWebExcelConfigHd configHd : headers) {
            HlsWebExcelConfigLn ln = new HlsWebExcelConfigLn();

            ln.setConfigHdId(configHd.getConfigHdId());
            ln.setExcelId(excelId);
            configHd.setHlsWebExcelConfigLnList(configLnMapper.selectHlsWebExcelConfiglineByExcelCode(ln));
        }
        return headers;
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

}
