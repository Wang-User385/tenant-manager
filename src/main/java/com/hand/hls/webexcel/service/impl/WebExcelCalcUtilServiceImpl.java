package com.hand.hls.webexcel.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.hls.dto.HlsWebExcel;
import com.hand.hls.hls.dto.HlsWebExcelConfigHd;
import com.hand.hls.hls.dto.HlsWebExcelConfigLn;
import com.hand.hls.hls.mapper.HlsWebExcelConfigHdMapper;
import com.hand.hls.hls.mapper.HlsWebExcelConfigLnMapper;
import com.hand.hls.webexcel.service.IWebExcelCalcUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebExcelCalcUtilServiceImpl implements IWebExcelCalcUtilService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HlsWebExcelConfigLnMapper configLnMapper;

    @Autowired
    private HlsWebExcelConfigHdMapper configHdMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    private Logger logger = LoggerFactory.getLogger(getClass());


    private static final String NOT_LOOP = "NOT_LOOP";
    private static final String LEFT_TO_RIGHT = "LEFT_TO_RIGHT";
    private static final String TOP_TO_BOTTOM = "TOP_TO_BOTTOM";

    @Override
    public List<Map> getExcelSingleListByHdInfo(IRequest iRequest, String jsonStr, Long excelId,Map dtoMap,String tableType) throws Exception {
        List<Map> list = new ArrayList<>();

        HlsWebExcelConfigHd hlsWebExcelConfigHd = new HlsWebExcelConfigHd();
        hlsWebExcelConfigHd.setExcelId(excelId);
        hlsWebExcelConfigHd.setTableType(tableType);
        List<HlsWebExcelConfigHd> hlsWebExcelConfigHds = configHdMapper.getConfigHdInfoByExcel(hlsWebExcelConfigHd);

        for(HlsWebExcelConfigHd excelConfigHd:hlsWebExcelConfigHds) {

            Map result = new HashMap();
            List<Map<String, String>> mapList = getCalcHdMap(iRequest, jsonStr, excelConfigHd);
            List<Object> objectList = new ArrayList<>();
            for(Map mapInfo:mapList){
                //dto全路径
                String path = String.valueOf(dtoMap.get(excelConfigHd.getTableName()));
                Class onwClass = Class.forName(path);
                Object obj = onwClass.newInstance();
                hlsBeanRefUtilService.setFieldValue(obj, mapInfo);
                objectList.add(obj);
            }
            result.put(excelConfigHd.getSheetName(), objectList);

            list.add(result);
        }
        return list;
    }


    public List<Map<String, String>> getCalcHdMap(IRequest iRequest, String jsonStr, HlsWebExcelConfigHd configHd) throws Exception {
        List<Map<String, String>> list = new ArrayList<>();

        Map result = parseExcelJsonToMap(iRequest, jsonStr);
        String[][][] excelArray = (String[][][]) result.get("excel");
        Map sheetInfo = (Map) result.get("sheetInfo");

        HlsWebExcelConfigLn hlsWebExcelConfigLn = new HlsWebExcelConfigLn();
        hlsWebExcelConfigLn.setConfigHdId(configHd.getConfigHdId());
        List<HlsWebExcelConfigLn> hlsWebExcelConfigLns = configLnMapper.selectHlsWebExcelConfiglineByHdId(hlsWebExcelConfigLn);

        //根据sheetName获取当前sheet索引位置
        int sheetIndex = (int) sheetInfo.get(configHd.getSheetName());

        //判断是否循环取值,NOT_LOOP不循环，LEFT_TO_RIGHT横向循环取值
        if(NOT_LOOP.equals(configHd.getLoopType())){
            Map map = getSheetColInfo(hlsWebExcelConfigLns,excelArray,sheetIndex,0,0);
            list.add(map);
        }else if(LEFT_TO_RIGHT.equals(configHd.getLoopType())){

            int multiLineFrom = excelColStrToNum(configHd.getMultiLineFrom());
            int multiLineTo = excelColStrToNum(configHd.getMultiLineTo());
            int distance = multiLineTo - multiLineFrom;
            for(int i = 0; i <= distance; i++){

                Map map = getSheetColInfo(hlsWebExcelConfigLns,excelArray,sheetIndex,i,0);
                list.add(map);
            }

        }else if(TOP_TO_BOTTOM.equals(configHd.getLoopType())){
            int multiLineFrom = Integer.valueOf(configHd.getMultiLineFrom());
            int multiLineTo = Integer.valueOf(configHd.getMultiLineTo());
            int distance = multiLineTo - multiLineFrom;
            for(int i = 0; i <= distance; i++){

                Map map = getSheetColInfo(hlsWebExcelConfigLns,excelArray,sheetIndex,0,i);
                list.add(map);
            }
        }

        return list;
    }

    Map getSheetColInfo(List<HlsWebExcelConfigLn> lnList,String[][][] excelArray,int sheetIndex,int colOffset, int rowOffset){
        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < lnList.size(); i++) {

            String columnCode = lnList.get(i).getColumnCode();
            //转为驼峰型
            String columnName = underline2Camel(lnList.get(i).getColumnName());
            //解析类似A1的行列，从三维数组中取出值
            String rowStr = getNumberFromExcelStr(columnCode, "C");
            int cell = Integer.parseInt(getNumberFromExcelStr(columnCode, "N"));
            int row = excelColStrToNum(rowStr);
            try {
                String value = excelArray[sheetIndex][cell + rowOffset - 1][row + colOffset - 1];

                //精度调整
                if ("NUMBER".equals(lnList.get(i).getColumnType()) &&
                        lnList.get(i).getPrecisions() != null && value != null && !"".equals(value)) {
                    try {
                        map.put(columnName, roundByPrecision(value, Integer.parseInt(lnList.get(i).getPrecisions().toString())));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("头表中" + columnName + "的数据类型转化错误！");
                    }

                } else {
                    map.put(columnName, value);
                }


            } catch (ArrayIndexOutOfBoundsException e) {
                //没有定义对应的列
                throw new IllegalArgumentException("头表中" + columnName + "的单元格在模板中未定义！");
            }
        }
        return map;
    }

    //将kendoSpreadsheet传过来的json数据里面的value转换成map,map包含excel三维数据，sheetname信息
    //第一维代表第几个sheet，第二维代表第几行，第三行代表第几列
    public Map parseExcelJsonToMap(IRequest requestContext, String jsonStr) throws IOException {
        //
        JsonNode rootNode = objectMapper.readTree(jsonStr);
        String[][][] excelArray = new String[rootNode.size()][][];
        Map result = new HashMap();
        Map map = new HashMap();
        if (rootNode.size() > 0) {
            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode sheetJsonNode = rootNode.get(i).path("rows");
                map.put(rootNode.get(i).path("name").asText(),i);
                String[][] sheetArray = new String[getMaxExcelIndex(sheetJsonNode)][];


                for (int j = 0; j < sheetJsonNode.size(); j++) {
                    int rowIndex = sheetJsonNode.get(j).path("index").asInt();

                    JsonNode cellJsonNode = sheetJsonNode.get(j).path("cells");
                    //获取数组的最大值

                    String[] cellArray = new String[getMaxExcelIndex(cellJsonNode)];
                    for (int k = 0; k < cellJsonNode.size(); k++) {
                        int cellIndex = cellJsonNode.get(k).path("index").asInt();
                        if (cellJsonNode.get(k).path("value").isMissingNode()) {
                            cellArray[cellIndex] = null;
                        } else {
                            //类似出现#VALUES！则表明计算失败，结构如下{"code":"VALUE"}
                            if (cellJsonNode.get(k).path("value").findPath("code").isMissingNode()) {
                                String textValue = cellJsonNode.get(k).path("value").asText();
                                cellArray[cellIndex] = textValue;
                            } else {
                                String ExcelCell = excelColIndexToStr(cellIndex + 1);
                                int row = rowIndex + 1;
                                logger.error("计算失败！sheet：{}，单元格：{}",i+1,ExcelCell+ "" +row);
                            }

                        }

                    }
                    sheetArray[rowIndex] = cellArray;
                }
                excelArray[i] = sheetArray;
            }
        }
        result.put("sheetInfo",map);
        result.put("excel",excelArray);
        return result;
    }

    //下划线转驼峰型
    public String underline2Camel(String underline) {
        Pattern pattern = Pattern.compile("[_]\\w");
        String camel = underline.toLowerCase();
        Matcher matcher = pattern.matcher(camel);
        while (matcher.find()) {
            String w = matcher.group().trim();
            camel = camel.replace(w, w.toUpperCase().replace("_", ""));
        }
        return camel;
    }

    //round函数
    String roundByPrecision(String doubleStr, int precision) {

        BigDecimal bg = new BigDecimal(doubleStr);
        Double f1 = bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1.toString();
    }

    //获取excel中的数字,或者字母 type为“C”为子母
    public String getNumberFromExcelStr(String str, String type) {
        str = str.trim();
        String strNumber = "";
        String StrD = "";
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (i == 0) {
                    if (!((str.charAt(i) >= 97 && str.charAt(i) <= 122) || (str.charAt(i) >= 65 && str.charAt(i) <= 90))) {
                        throw new IllegalArgumentException("表达式" + str + "中第一位必须为字母！");
                    }
                }
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    strNumber += str.charAt(i);
                } else if ((str.charAt(i) >= 97 && str.charAt(i) <= 122) || (str.charAt(i) >= 65 && str.charAt(i) <= 90)) {
                    StrD += str.charAt(i);
                } else {
                    throw new IllegalArgumentException("表达式中" + str + "只能存在字母及数字！");
                }

            }
        }
        //判断连续
        if (str.indexOf(strNumber) < 0 || str.indexOf(StrD) < 0 || strNumber == "" || StrD == "") {
            throw new IllegalArgumentException("表达式" + str + "格式错误，格式示例：A1！");
        }
        if (type.equals("C")) {
            return StrD.toUpperCase();
        } else {
            return strNumber;
        }
    }

    //将excel中的A，B等装换成其对应的数字
    public int excelColStrToNum(String colStr) {
        int num = 0;
        int result = 0;
        int length = colStr.length();
        for (int i = 0; i < length; i++) {
            char ch = colStr.charAt(length - i - 1);
            num = (int) (ch - 'A' + 1);
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }

    int getMaxExcelIndex(JsonNode jsonNode) {
        int index = 0;
        for (int k = 0; k < jsonNode.size(); k++) {
            int cellIndex = jsonNode.get(k).path("index").asInt();
            if (cellIndex > index) {
                index = cellIndex;
            }
        }
        index = index + 1;
        return index;
    }

    //将数字转化成excel中的A,B.....
    public String excelColIndexToStr(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }

}
