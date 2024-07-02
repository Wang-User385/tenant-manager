package com.hand.hls.calc.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcConfigService;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * *author:wangwei 2017-4-7
 * kendoUI的spreadSheet复制
 * 当前只校验第一个sheet
 */
@Service
@Transactional
public class HlsCusCalcExcelImportUtilServiceImpl extends BaseServiceImpl<HlsCalcConfig> implements HlsCusCalcExcelImportUtilService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HlsPriceListConfigHdMapper hlsPriceListConfigHdMapper;
    @Autowired
    private HlsCalcConfigService hlsCalcConfigService;

    @Autowired
    private HlsPriceListConfigLnMapper hlsPriceListConfigLnMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCfItemMapper hlsCfItemMapper;
    //跟租金关联字段写死
    private static String[] rentalArray = {"interest", "principal", "net_interest", "vat_interest", "net_principal", "vat_principal", "net_due_amount", "vat_due_amount"};

    //将kendoSpreadsheet传过来的json数据里面的value转换成三维数组
    //第一维代表第几个sheet，第二维代表第几行，第三行代表第几列
    @Override
    public String[][][] parseExcelJsonToArray(IRequest requestContext, String jsonStr) throws IOException {
        //
        JsonNode rootNode = objectMapper.readTree(jsonStr);
        String[][][] excelArray = new String[rootNode.size()][][];
        if (rootNode.size() > 0) {
            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode sheetJsonNode = rootNode.get(i).path("rows");
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
                                throw new IllegalArgumentException("计算失败,请查看单元格" + ExcelCell + row + "！");
                            }

                        }

                    }
                    sheetArray[rowIndex] = cellArray;
                }
                excelArray[i] = sheetArray;
            }
        }
        return excelArray;
    }

    //将kendoSpreadsheet传过来的json数据里面的value转换成map,map包含excel三维数据，sheetname信息
    //第一维代表第几个sheet，第二维代表第几行，第三行代表第几列
    @Override
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
                                //logger.info("textValue : " +  textValue + "  行  " + rowIndex + "  列  " + cellIndex);
                            } else {
                                String ExcelCell = excelColIndexToStr(cellIndex + 1);
                                int row = rowIndex + 1;
                                //test by song
                                //throw new IllegalArgumentException("计算失败,sheet:"+rootNode.get(i).path("name").asText()+",请查看单元格" + ExcelCell + row + "！");
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

    //将kendoSpreadsheet传过来的json数据里面的公式转换成三维数组
    //第一维代表第几个sheet，第二维代表第几行，第三行代表第几列，
    //方便后期做price_list模板和提交数据的公式校验，防止公式被更改
    @Override
    public String[][][] parseExcelJsonToArrayFormat(IRequest requestContext, String jsonStr) throws IOException {
        //
        JsonNode rootNode = objectMapper.readTree(jsonStr);
        String[][][] excelArray = new String[rootNode.size()][][];
        if (rootNode.size() > 0) {
            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode sheetJsonNode = rootNode.get(i).path("rows");
                String[][] sheetArray = new String[getMaxExcelIndex(sheetJsonNode)][];

                for (int j = 0; j < sheetJsonNode.size(); j++) {
                    int rowIndex = sheetJsonNode.get(j).path("index").asInt();
                    JsonNode cellJsonNode = sheetJsonNode.get(j).path("cells");
                    //获取数组的最大值

                    String[] cellArray = new String[getMaxExcelIndex(cellJsonNode)];
                    for (int k = 0; k < cellJsonNode.size(); k++) {
                        int cellIndex = cellJsonNode.get(k).path("index").asInt();
                        if (cellJsonNode.get(k).path("formula").isMissingNode()) {
                            cellArray[cellIndex] = null;
                        } else {
                            String textValue = cellJsonNode.get(k).path("formula").asText().trim().toUpperCase();
                            cellArray[cellIndex] = textValue;
                        }

                    }
                    sheetArray[rowIndex] = cellArray;
                }
                excelArray[i] = sheetArray;
            }
        }
        return excelArray;
    }


    @Override
    public Map<String, String> getCalcHdMap(IRequest requestContext, String jsonStr, String priceList) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        Map result = parseExcelJsonToMap(requestContext, jsonStr);
        String[][][] excelArray = (String[][][]) result.get("excel");
        Map sheetInfo = (Map) result.get("sheetInfo");

        HlsPriceListConfigLn hlsPriceListConfigLn = new HlsPriceListConfigLn();
        hlsPriceListConfigLn.setPriceList(priceList);
        hlsPriceListConfigLn.setTableType("SINGLE");
        List<HlsPriceListConfigLn> hlsPriceListConfigLnList;
        //合同变更回写和调息回写，需要固定去取第三个sheet页数据，不取basics 和 input
        if("CON_CHANGE_PMT_JC".equalsIgnoreCase(priceList) || "CON_CHANGE_LP_JC".equalsIgnoreCase(priceList) || "CON_CHANGE_CASUAL_JC".equalsIgnoreCase(priceList)
        || "ADJUST_RATE_PMT_JC".equalsIgnoreCase(priceList) || "ADJUST_RATE_LP_JC".equalsIgnoreCase(priceList) || "ADJUST_RATE_CASUAL_JC".equalsIgnoreCase(priceList)){
            hlsPriceListConfigLnList = hlsPriceListConfigLnMapper.selectHlsPriceListConfiglineByPriceListReq(hlsPriceListConfigLn);
        }else{
            hlsPriceListConfigLnList = hlsPriceListConfigLnMapper.selectHlsPriceListConfiglineByPriceList(hlsPriceListConfigLn);
        }
        if (hlsPriceListConfigLnList.size() == 0) {
            throw new IllegalArgumentException("报价器头表不存在已定义的字段！");
        }
        for (int i = 0; i < hlsPriceListConfigLnList.size(); i++) {

            //根据sheetName获取当前sheet索引位置
            int sheetIndex = (int) sheetInfo.get(hlsPriceListConfigLnList.get(i).getSheetName());

            String columnCode = hlsPriceListConfigLnList.get(i).getColumnCode();
            //转为驼峰型
            String columnName = underline2Camel(hlsPriceListConfigLnList.get(i).getColumnName());
            //解析类似A1的行列，从三维数组中取出值
            String rowStr = getNumberFromExcelStr(columnCode, "C");
            int cell = Integer.parseInt(getNumberFromExcelStr(columnCode, "N"));
            int row = excelColStrToNum(rowStr);
            try {
                String value = excelArray[sheetIndex][cell - 1][row - 1];
                //有验证sql的转换值
                if (StringUtil.isNotEmpty(hlsPriceListConfigLnList.get(i).getValidateSql()) && StringUtil.isNotEmpty(hlsPriceListConfigLnList.get(i).getValidateType()) && hlsPriceListConfigLnList.get(i).getValidateType().equalsIgnoreCase("SQl")) {
                    try {
                        String valueCode = hlsPriceListConfigHdMapper.getCodeValueName(hlsPriceListConfigLnList.get(i).getValidateSql(), value);
                        map.put(columnName, valueCode);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("头表中" + columnName + "的验证sql定义错误！");
                    }
                } else {

                    //精度调整
                    if ("NUMBER".equals(hlsPriceListConfigLnList.get(i).getColumnType()) &&
                            hlsPriceListConfigLnList.get(i).getPrecisions() != null && value != null && !"".equals(value)) {
                        try {
                            map.put(columnName, roundByPrecision(value, Integer.parseInt(hlsPriceListConfigLnList.get(i).getPrecisions().toString())));
                        } catch (Exception e) {
                            throw new IllegalArgumentException("头表中" + columnName + "的数据类型转化错误！");
                        }

                    } else {
                        map.put(columnName, value);
                    }

                }


            } catch (ArrayIndexOutOfBoundsException e) {
                //没有定义对应的列
                throw new IllegalArgumentException("头表中" + columnName + "的单元格在模板中未定义！");
            }
        }
        return map;
    }

    /**
     * 将excel保存到头表中去
     * 将每个报价只能匹配一套表，同一个报价在多个阶段用到它，需要定义多遍，实非吾愿，功能设计如此
     *
     * @jsonStr: json字符串
     * @priceList:报价器
     * @models: models, dto对应的模块名如hls.core.prj.dto中的prj
     * 返回头表dto
     */
    @Override
    public Object getExcelToCalcHdTable(IRequest requestContext, String jsonStr, String priceList, String models) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map = getCalcHdMap(requestContext, jsonStr, priceList);
        HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
        hlsPriceListConfigHd.setPriceList(priceList);
        hlsPriceListConfigHd.setTableType("SINGLE");
        List<HlsPriceListConfigHd> HlsPriceListConfigHdSingleList = hlsPriceListConfigHdMapper.getConfigHdInfoByPriceList(hlsPriceListConfigHd);
        if (HlsPriceListConfigHdSingleList.size() == 0) {
            throw new IllegalArgumentException("报价器头表定义错误！");
        }
        //根据表名获取对应dto类名
        String tableName = underline2Camel(HlsPriceListConfigHdSingleList.get(0).getTableName());
        String objectName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
        //dto全路径
        String path = "com.hand.hls." + models + ".dto";
        Class onwClass = Class.forName(path + ".HlsCus" + objectName);
        Object o = onwClass.newInstance();
        // Long leaseTimes = new Double(Double.parseDouble(map.get("leaseTimes"))).longValue();
        //map.put("leaseTimes", leaseTimes.toString());
        hlsBeanRefUtilService.setFieldValue(o, map);
        return o;
    }

    /**
     * 将excel返回到行对象中
     *
     * @jsonStr: json字符串
     * @priceList:报价器
     * @sourceDocumentCategory:来源单据类型
     * @sourceDocumentId: 来源单据名称
     * @sourceDocumentId: models, dto对应的模块名如hls.core.prj.dto中的prj
     * 返回头表dto
     * leaseTimes:期数
     */
    @Override
    public List<Object> getExcelToCalcLnTable(IRequest requestContext, String jsonStr, String priceList, String models, int leaseTimes, String sourceDocumentCategory) throws Exception {
        List<Object> objectList = new ArrayList<>();

        //获取数组
        //String[][][] excelArray = parseExcelJsonToArray(requestContext, jsonStr);
        Map result = parseExcelJsonToMap(requestContext, jsonStr);
        String[][][] excelArray = (String[][][]) result.get("excel");
        Map sheetInfo = (Map) result.get("sheetInfo");

        //调息价目表判断
        if("CON_FLOATING_RATE_REQ".equalsIgnoreCase(sourceDocumentCategory)){
            if("GECALCULATOR_PMT_JC".equalsIgnoreCase(priceList) || "CON_CHANGE_PMT_JC".equalsIgnoreCase(priceList) ){
                priceList = "ADJUST_RATE_PMT_JC";
            }else if("GECALCULATOR_LP_JC".equalsIgnoreCase(priceList) || "CON_CHANGE_LP_JC".equalsIgnoreCase(priceList)){
                priceList = "ADJUST_RATE_LP_JC";
            }else if("GECALCULATOR_CASUAL_JC".equalsIgnoreCase(priceList) || "CON_CHANGE_CASUAL_JC".equalsIgnoreCase(priceList)){
                priceList = "ADJUST_RATE_CASUAL_JC";
            }
        }
        //dto全路径
        String path = "com.hand.hls." + models + ".dto";

        //获取行表
        HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
        hlsPriceListConfigHd.setPriceList(priceList);
        hlsPriceListConfigHd.setTableType("MULTI_LINE");
        hlsPriceListConfigHd.setConfigType("OUTPUT");
        List<HlsPriceListConfigHd> hlsPriceListConfigHdSingleList = hlsPriceListConfigHdMapper.getConfigHdInfoByPriceList(hlsPriceListConfigHd);
        if (hlsPriceListConfigHdSingleList.size() == 0) {
            throw new IllegalArgumentException("报价器行表定义错误！");
        }

        //获取头表字段定义
        HlsPriceListConfigLn configLn = new HlsPriceListConfigLn();
        configLn.setPriceList(priceList);
        configLn.setTableType("SINGLE");
        List<HlsPriceListConfigLn> hlsPriceListConfigLnListHdCloumn = hlsPriceListConfigLnMapper.selectHlsPriceListConfiglineByPriceList(configLn);


        String objectName = "";
        String cashflowIrrAfterTax = "0";
        String cashflowIrr = "0";;
        for(HlsPriceListConfigHd configHd:hlsPriceListConfigHdSingleList) {

            int multiLineFrom;
            int multiLineto;
            try {
                multiLineFrom = Integer.parseInt(configHd.getMultiLineFrom());
                multiLineto = Integer.parseInt(configHd.getMultiLineTo());
            } catch (Exception e) {
                throw new IllegalArgumentException("报价器行表多行范围从到定义错误！");
            }

            //获取行表字段定义
            HlsPriceListConfigLn hlsPriceListConfigLn = new HlsPriceListConfigLn();
            hlsPriceListConfigLn.setPriceList(priceList);
            hlsPriceListConfigLn.setTableType("MULTI_LINE");
            hlsPriceListConfigLn.setSheetName(configHd.getSheetName());
            List<HlsPriceListConfigLn> hlsPriceListConfigLnList = hlsPriceListConfigLnMapper.selectHlsPriceListConfiglineByPriceList(hlsPriceListConfigLn);


            //根据表名获取对应dto类名
            String tableName = underline2Camel(hlsPriceListConfigHdSingleList.get(0).getTableName());
            if (sourceDocumentCategory != null && sourceDocumentCategory.equals("CON_CONTRACT")) {
                tableName = "ConContractCashflow";

            }
            objectName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);


            int distance = multiLineto - multiLineFrom;
            int cellNum;
            for(int i = 0; i <= distance; i++){
                //取 第零期 cashflowIrrAfterTax cashflowIrr金额
                if(i < ((ArrayList) hlsPriceListConfigLnList).size() && hlsPriceListConfigLnList.get(i) != null && hlsPriceListConfigLnList.get(i).getColumnName().equals("CASHFLOW_IRR_AFTER_TAX")){
                    cellNum = excelColStrToNum(hlsPriceListConfigLnList.get(i).getColumnCode().toUpperCase());
                    cashflowIrrAfterTax =  excelArray[0][multiLineFrom-1][cellNum-1] ;
                }
                if(i < ((ArrayList) hlsPriceListConfigLnList).size() && hlsPriceListConfigLnList.get(i) != null && hlsPriceListConfigLnList.get(i).getColumnName().equals("CASHFLOW_IRR")){
                    cellNum = excelColStrToNum(hlsPriceListConfigLnList.get(i).getColumnCode().toUpperCase());
                    cashflowIrr = excelArray[0][multiLineFrom-1][cellNum-1] ;
                }

                //插入行表对应现金流
                for (int j = 0; j < hlsPriceListConfigLnList.size(); j++) {

                    //获取sheet索引
                    int sheetIndex = (int) sheetInfo.get(hlsPriceListConfigLnList.get(j).getSheetName());

                    //获取行表上的dueamount，含税不含税期数等字段
                    Map map = getCalcLineObject(excelArray, multiLineFrom, i, hlsPriceListConfigLnList.get(j), sheetIndex);

                    if (map.size() > 0) {

                        //将非创建现金流字段同步到行上
                        setNoCashflowCloToMap(map, excelArray, multiLineFrom, i, hlsPriceListConfigLnList, sheetIndex);
                        Class onwClass = Class.forName(path + ".HlsCus" + objectName);
                        Object o = onwClass.newInstance();
                        hlsBeanRefUtilService.setFieldValue(o, map);
                        objectList.add(o);
                    }
                }

            }

        }

        for(int i = 0; i < hlsPriceListConfigLnListHdCloumn.size(); i++){

            //获取头配置上面的现金流
            int sheetIndex = (int) sheetInfo.get(hlsPriceListConfigLnListHdCloumn.get(i).getSheetName());
            Map map = getCalcHeaderObeject(excelArray, hlsPriceListConfigLnListHdCloumn.get(i),sheetIndex);
            if (map.size() > 0) {
                map.put("cfStatus","HEAD_CASH_FLOW");
                Class onwClass = Class.forName(path + "." + objectName);
                Object o = onwClass.newInstance();
                map.put("cashflowIrr",cashflowIrr);
                map.put("cashflowIrrAfterTax",cashflowIrrAfterTax);
                hlsBeanRefUtilService.setFieldValue(o, map);
                objectList.add(o);
            }
        }

        return objectList;
    }

    //判断提交的excel和模板中的公式是否被修改
    @Override
    public Boolean excelFormatHasChange(IRequest requestContext, String priceList, String checkJsonStr) throws IOException {
        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(priceList);
        HlsCalcConfig hlsCalcConfigQuery = hlsCalcConfigService.selectByPrimaryKey(requestContext, hlsCalcConfig);
        Boolean checkBoolean = true;
        if (hlsCalcConfigQuery.getSheets() != null) {
            String templateJsonStr = hlsCalcConfigQuery.getSheets();
            String templateArray[][][] = parseExcelJsonToArrayFormat(requestContext, templateJsonStr);
            //获取跟校验相同维度的数组，如果为忽略公式校验则值为Y
            String noCheckFormualArray[][][] = getNoCheckFormualArray(priceList, templateArray);
            String checkArray[][][] = parseExcelJsonToArrayFormat(requestContext, checkJsonStr);

            for (int i = 0; i < templateArray[0].length; i++) {
                for (int j = 0; j < templateArray[0][i].length; j++) {
                    if (!"Y".equals(noCheckFormualArray[0][i][j])) {
                        String formula = templateArray[0][i][j] == null ? "空" : templateArray[0][i][j];
                        String formulaValue = checkArray[0][i][j] == null ? "空" : checkArray[0][i][j];
                        if (!formula.equals(formulaValue)) {
                            String ExcelCell = excelColIndexToStr(j + 1);
                            int row = i + 1;
                            throw new IllegalArgumentException("计算失败,单元格" + ExcelCell + row + "公式被修改,原公式" + formula + "被修改为" + formulaValue + "！");
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("价目表模板未定义！");
        }
        return checkBoolean;
    }

    //获取不校验公式的三维数组
    String[][][] getNoCheckFormualArray(String priceList, String[][][] temlateArray) {
        //数组维度复制
        String[][][] NoCheckFormualArray = new String[1][][];
        String[][] temp2 = new String[temlateArray[0].length][];

        for (int i = 0; i < temlateArray[0].length; i++) {
            String[] temp1 = new String[temlateArray[0][i].length];
            temp2[i] = temp1;
        }
        NoCheckFormualArray[0] = temp2;
        //获取忽略配置信息
        HlsPriceListConfigLn hlsPriceListConfigLnQuery = new HlsPriceListConfigLn();
        hlsPriceListConfigLnQuery.setPriceList(priceList);
        hlsPriceListConfigLnQuery.setTableType("SINGLE");
        hlsPriceListConfigLnQuery.setFormulaNoCheckFlag("Y");
        List<HlsPriceListConfigLn> noCheckFormualListHd = hlsPriceListConfigLnMapper.selectHlsPriceListConfiglineByPriceList(hlsPriceListConfigLnQuery);
        hlsPriceListConfigLnQuery.setTableType("MULTI_LINE");
        List<HlsPriceListConfigLn> noCheckFormualListLn = hlsPriceListConfigLnMapper.selectHlsPriceListConfiglineByPriceList(hlsPriceListConfigLnQuery);
        //获取行表
        HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
        hlsPriceListConfigHd.setPriceList(priceList);
        hlsPriceListConfigHd.setTableType("MULTI_LINE");
        List<HlsPriceListConfigHd> HlsPriceListConfigHdSingleList = hlsPriceListConfigHdMapper.getConfigHdInfoByPriceList(hlsPriceListConfigHd);
        if (HlsPriceListConfigHdSingleList.size() > 1 || HlsPriceListConfigHdSingleList.size() == 0) {
            throw new IllegalArgumentException("报价器行表定义错误！");
        }

        int multiLineFrom;
        int multiLineto;
        try {
            multiLineFrom = Integer.parseInt(HlsPriceListConfigHdSingleList.get(0).getMultiLineFrom());
            multiLineto = Integer.parseInt(HlsPriceListConfigHdSingleList.get(0).getMultiLineTo());
        } catch (Exception e) {
            throw new IllegalArgumentException("报价器行表多行范围从到定义错误！");
        }
        for (int i = 0; i < noCheckFormualListHd.size(); i++) {
            String formulaNoCheckFlag = noCheckFormualListHd.get(i).getFormulaNoCheckFlag() == null ? "N" : noCheckFormualListHd.get(i).getFormulaNoCheckFlag().toUpperCase();
            if ("Y".equals(formulaNoCheckFlag)) {
                String RowStr = getNumberFromExcelStr(noCheckFormualListHd.get(i).getColumnCode().toUpperCase(), "C");
                int Cell = Integer.parseInt(getNumberFromExcelStr(noCheckFormualListHd.get(i).getColumnCode().toUpperCase(), "N"));
                int Row = excelColStrToNum(RowStr);
                NoCheckFormualArray[0][Cell - 1][Row - 1] = "Y";
            }
        }
        for (int i = 0; i < noCheckFormualListLn.size(); i++) {
            String formulaNoCheckFlag = noCheckFormualListLn.get(i).getFormulaNoCheckFlag() == null ? "N" : noCheckFormualListLn.get(i).getFormulaNoCheckFlag().toUpperCase();
            if ("Y".equals(formulaNoCheckFlag)) {
                int CellsIndex = excelColStrToNum(noCheckFormualListLn.get(i).getColumnCode().toUpperCase());
                for (int j = multiLineFrom - 1; j < NoCheckFormualArray[0].length; j++) {
                    try {
                        NoCheckFormualArray[0][j][CellsIndex - 1] = "Y";
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        }
        return NoCheckFormualArray;
    }

    //获取头表字段配置的现金流
    Map getCalcHeaderObeject(String[][][] array, HlsPriceListConfigLn hlsPriceListConfigLn,int sheetIndex) {
        Long cashflowItem = hlsPriceListConfigLn.getCashflowItem();
        Map<String, String> map = new HashMap<>();
        if (cashflowItem != null) {
            //解析类似A1的行列，从三维数组中取出值
            String rowStr = getNumberFromExcelStr(hlsPriceListConfigLn.getColumnCode(), "C");
            int cell = Integer.parseInt(getNumberFromExcelStr(hlsPriceListConfigLn.getColumnCode(), "N"));
            int row = excelColStrToNum(rowStr);
            //net和vat
            String netColumnCode=hlsPriceListConfigLn.getNetColumnCode()==null?"":hlsPriceListConfigLn.getNetColumnCode().trim();
            String vatColumnCode=hlsPriceListConfigLn.getVatColumnCode()==null?"":hlsPriceListConfigLn.getVatColumnCode().trim();
            String netValue="";
            String vatValue="";
            //net
            if(!"".equals(netColumnCode)){
                String netRowStr = getNumberFromExcelStr(netColumnCode.toUpperCase(), "C");
                int netCell = Integer.parseInt(getNumberFromExcelStr(netColumnCode.toUpperCase(), "N"));
                int netRow = excelColStrToNum(netRowStr);
                try{
                    netValue = array[sheetIndex][netCell - 1][netRow - 1];
                }catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("头表中" + hlsPriceListConfigLn.getColumnName()  + "的不含税列单元格在模板中未定义！");
                }
            }
            //vat
            if(!"".equals(vatValue)){
                String vatRowStr = getNumberFromExcelStr(vatValue.toUpperCase(), "C");
                int vatCell = Integer.parseInt(getNumberFromExcelStr(vatValue.toUpperCase(), "N"));
                int vatRow = excelColStrToNum(vatRowStr);
                try{
                    vatValue = array[sheetIndex][vatCell - 1][vatRow - 1];
                }catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("头表中" + hlsPriceListConfigLn.getColumnName()  + "的税额列单元格在模板中未定义！");
                }
            }
            try {
                String value = array[sheetIndex][cell - 1][row - 1];
                setDefaultCashflowTomap(value,netValue,vatValue, map, cashflowItem,hlsPriceListConfigLn.getPrecisions());
            } catch (ArrayIndexOutOfBoundsException e) {
                //没有定义对应的列
                throw new IllegalArgumentException("头表中" + hlsPriceListConfigLn.getColumnName() + "的单元格在模板中未定义！");
            }
        }
        return map;
    }

    //获取行表上的dueamount，含税不含税期数等字段
    Map getCalcLineObject(String[][][] array, int multiLineFrom, int times,  HlsPriceListConfigLn hlsPriceListConfigLn,int sheetIndex) {
        Long cashflowItem = hlsPriceListConfigLn.getCashflowItem();
        Map<String, String> map = new HashMap<>();
        if (cashflowItem != null) {
            // 将行上a,b,c等字母转换为数字
            int cellsIndex = excelColStrToNum(hlsPriceListConfigLn.getColumnCode().toUpperCase());
            //表字段名转为驼峰型以对应dto
            String columnName = underline2Camel(hlsPriceListConfigLn.getColumnName());
            //获取net和vat字段
            String netColumnCode=hlsPriceListConfigLn.getNetColumnCode()==null?"":hlsPriceListConfigLn.getNetColumnCode().trim();
            String vatColumnCode=hlsPriceListConfigLn.getVatColumnCode()==null?"":hlsPriceListConfigLn.getVatColumnCode().trim();
            String netValue="";
            String vatValue="";
            //net
            if(!"".equals(netColumnCode)){
                int netCellsIndex = excelColStrToNum(netColumnCode.toUpperCase());
                try{
                    netValue = array[sheetIndex][times + multiLineFrom - 1][netCellsIndex - 1];
                }catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("行表中" + columnName + "的不含税列单元格在模板中未定义！");
                }
            }
            //vat
            if(!"".equals(vatColumnCode)){
                int vatCellsIndex = excelColStrToNum(vatColumnCode.toUpperCase());
                try{
                    vatValue = array[sheetIndex][times + multiLineFrom - 1][vatCellsIndex - 1];
                }catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("行表中" + columnName + "的税额列单元格在模板中未定义！");
                }

            }
            try {
                String value = array[sheetIndex][times + multiLineFrom - 1][cellsIndex - 1];
                setDefaultCashflowTomap(value,netValue,vatValue, map, cashflowItem,hlsPriceListConfigLn.getPrecisions());
            } catch (ArrayIndexOutOfBoundsException e) {
                //没有定义对应的列
                throw new IllegalArgumentException("行表中" + columnName + "的单元格在模板中未定义！");
            }
        }
        return map;
    }

    //将默认参数传递给map
    Map setDefaultCashflowTomap(String value,String netValue,String vatValue, Map map, Long cashflowItem,Long precision) {
        if (value != null&&value!="") {
            BigDecimal valueB = new BigDecimal(value);
            //不为0则插入
            if (valueB.compareTo(BigDecimal.ZERO) != 0) {
                //非租金
                HlsCashflowItem hlsCashflowItem = new HlsCashflowItem();
                hlsCashflowItem.setCfItem(cashflowItem.toString());
                List<HlsCashflowItem> hlsCashflowItemList = hlsCfItemMapper.select(hlsCashflowItem);
                if (hlsCashflowItemList.size() == 0 || hlsCashflowItemList.size() > 1) {
                    throw new IllegalArgumentException("现金流项目为" + cashflowItem + "定义错误！");
                }
                map.put("cfItem", hlsCashflowItemList.get(0).getCfItem());
                map.put("cfType", hlsCashflowItemList.get(0).getCfType());
                map.put("cfDirection", hlsCashflowItemList.get(0).getCfDirection());
                //map.put("times", Integer.toString(times));
                if(precision!=null){
                    map.put("dueAmountPrecision",precision.toString());
                    map.put("netDueAmountPrecision",precision.toString());
                    map.put("vatDueAmountPrecision",precision.toString());
                }else{
                    map.put("dueAmountPrecision","2");
                    map.put("netDueAmountPrecision","2");
                    map.put("vatDueAmountPrecision","2");
                }
                map.put("dueAmount", value);
                map.put("netDueAmount", netValue);
                map.put("vatDueAmount", vatValue);
            }
        }
        return map;
    }

    //获取行表中非创建现金流的列，并把它set到map中
    Map setNoCashflowCloToMap(Map map, String[][][] array, int multiLineFrom, int times, List<HlsPriceListConfigLn> hlsPriceListConfigLnList,int sheetIndex) {
        for (int i = 0; i < hlsPriceListConfigLnList.size(); i++) {
            //只有不创建现金流的字段才同步
            if (hlsPriceListConfigLnList.get(i).getCashflowItem() == null) {
                // 将行上a,b,c等字母转换为数字
                int cellsIndex = excelColStrToNum(hlsPriceListConfigLnList.get(i).getColumnCode().toUpperCase());
                //表字段名转为驼峰型以对应dto
                String columnName = underline2Camel(hlsPriceListConfigLnList.get(i).getColumnName());
                String value = array[sheetIndex][times + multiLineFrom - 1][cellsIndex - 1];
                if (columnName.equals("times") || columnName.equals("finIncomeDate") || columnName.equals("calcDate") || columnName.equals("dueDate")) {
                    if(value == null){
                        throw new IllegalArgumentException("第"+(sheetIndex+1)+"sheet,第"+(times + multiLineFrom)+"行"+columnName + "字段配置取值为空!");
                    }
                    value = value.replace(".0", "");

                }
                //非租金排除租金相关字段
                if (!map.get("cfItem").equals("1")) {
                    if (!Arrays.asList(rentalArray).contains(hlsPriceListConfigLnList.get(i).getColumnName().toLowerCase())) {
                        if (value != null) {
                            map.put(columnName, value);
                        }
                    }
                } else {
                    if (value != null) {
                        map.put(columnName, value);
                    }
                }
            }
        }
        return map;
    }

    //将excel中的A，B等装换成其对应的数字
    @Override
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

    @Override
    public List<Object> getExcelHdCashItemList(IRequest iRequest, String jsonStr, String priceList, String models) {
        return null;
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


    //获取excel中的数字,或者字母 type为“C”为子母
    @Override
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

}
