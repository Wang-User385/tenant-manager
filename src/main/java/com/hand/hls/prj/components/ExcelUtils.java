package com.hand.hls.prj.components;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigBT;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigBTMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.HlsCalcSaveService;
import com.hand.hls.calc.service.IPriceListIrrService;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsProductDefinition;
import com.hand.hls.fnd.dto.HlsProductDefinitionPara;
import com.hand.hls.fnd.mapper.HlsProductDefinitionMapper;
import com.hand.hls.fnd.mapper.HlsProductDefinitionParaMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.IPrjProjectService;
import com.hand.hls.prj.service.IPrjQuotationCashflowService;
import com.hand.hls.prj.service.IPrjQuotationDetailsService;
import com.hand.hls.prj.service.IPrjQuotationService;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.MathUtil;
import jodd.util.ArraysUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.utils.HlsConstantUtil.BaseController.Y;

@Component
public class ExcelUtils {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static Calendar excelStartDate = new GregorianCalendar(1900, 0, -1);
    @Autowired
    private HlsPriceListConfigHdMapper configHdMapper;
    @Autowired
    private HlsPriceListConfigLnMapper configLnMapper;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;
    @Autowired
    HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;
    @Autowired
    private IPrjQuotationService prjQuotationService;
    @Autowired
    private IPrjQuotationDetailsService prjQuotationDetailsService;

    @Autowired
    private IPrjQuotationCashflowService prjQuotationCashflowService;
    @Autowired
    private HlsCalcSaveService hlsCalcSaveService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsProductDefinitionParaMapper hlsProductDefinitionParaMapper;
    @Autowired
    private HlsPriceListConfigBTMapper hlsPriceListConfigBTMapper;
    @Autowired
    private HlsPriceListConfigHdMapper hlsPriceListConfigHdMapper;
    @Autowired
    private IPriceListIrrService priceListIrrService;
    @Autowired
    private HlsPriceListConfigLnMapper hlsPriceListConfigLnMapper;
    @Autowired
    private HlsProductDefinitionMapper hlsProductDefinitionMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    private static final String KEY_INDEX = "index";
    private static final String KEY_CELLS = "cells";
    private static final String KEY_ROWS = "rows";
    public static final String KEY_VALUE = "value";
    private static final String KEY_FORMULA = "formula";
    public static final String KEY_FIELD = "field";
    private static final String PRICE_TYPE_SINGLE = "SINGLE";
    private static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    private static final String KEY_FORMAT = "format";
//    private static final String[] DOUBLE_FORMATS = {"0%", "0.00"};
    private static final String[] DOUBLE_FORMATS = {"#,0", "0%", "0.00", "#,##0.00", "#,0.00", "0.00%"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy"};
    public static final String LEASE_START_DATE = "lease_start_date";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final String INCEPT_FIX_DAY = "incept_fix_day";


    @Autowired
    HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    /**
     * 根据项目信息更新报价信息
     */
    public void updateQutationByProject(Long quotationId, Long projectId, List<Map> initMap) throws Exception {
        List<Map> mapList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(initMap)) {
            mapList.addAll(initMap);
        }
        HlsCusPrjProject project = new HlsCusPrjProject();
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();

        project.setProjectId(projectId);
        project = hlsCusPrjProjectMapper.selectByPrimaryKey(project);

        quotation.setQuotationId(quotationId);
        quotation = prjQuotationMapper.selectByPrimaryKey(quotation);
        quotation.setProjectId(projectId);
        //狮桥需求 将产品编号放入价目表中
        HlsProductDefinition productDefinition = hlsProductDefinitionMapper.selectByPrimaryKey(quotation.getPlanId());
        //在产品进行变更或者审批中时（即未启用），对与产品有关的进件进行报价更新时报错
        if(productDefinition.getEnabledFlag().equals("N") || StringUtils.equals(productDefinition.getEnabledFlag(),"")){
            throw new HlsCusException("该产品"+productDefinition.getDefinitionCode()+" ["+productDefinition.getDefinitionName()+"] 未启用，请检查！");
        }
        Map podtnumMap = new HashMap();
        podtnumMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.HlsProductDefinition.ProductPara.PRODUC_NUMBER);
        podtnumMap.put(ExcelUtils.KEY_VALUE,productDefinition.getDefinitionCode());
        mapList.add(podtnumMap);
        Map result = prjQuotationMapper.selectExportVar(quotation);
        result.forEach((key, value) -> {
            Map map = new HashMap();
            map.put(ExcelUtils.KEY_FIELD, key);//ExcelUtils.LEASE_START_DATE
            map.put(ExcelUtils.KEY_VALUE, value);
            mapList.add(map);
        });


        //产品数据
        HlsProductDefinitionPara hlsProductDefinitionPara = new HlsProductDefinitionPara();
        hlsProductDefinitionPara.setQuotationId(quotationId);
        List<Map> list = hlsProductDefinitionParaMapper.selectHlsProductDefinitionParaByQuotation(hlsProductDefinitionPara);
        Map definitionMap = hlsProductDefinitionParaMapper.selectHlsProductDefinitionByQuotation(hlsProductDefinitionPara);
        //设置逾期宽限类型
        Map graceMap = new HashMap();
        graceMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.GRACE_FLAG_N);
        graceMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.GRACE_FLAG_N));
        mapList.add(graceMap);

        if((StringUtils.isNotEmpty(quotation.getRepayFlag())) && (StringUtils.isNotEmpty(quotation.getServiceFeeRepayFlag()) )){
            //是否返利
            Map replyMap = new HashMap();
            replyMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.REPAY_FLAG_N);
            replyMap.put(ExcelUtils.KEY_VALUE, hlsCusPrjQuotationMapper.queryYNFlag(quotation.getRepayFlag()));
            mapList.add(replyMap);
            //服务费是否返利/贴息SERVICE_FEE_REPAY_FLAG_N
            Map serviceFeeReplyMap = new HashMap();
            serviceFeeReplyMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.SERVICE_FEE_REPAY_FLAG_N);
            serviceFeeReplyMap.put(ExcelUtils.KEY_VALUE, hlsCusPrjQuotationMapper.queryYNFlag(quotation.getServiceFeeRepayFlag()));
            mapList.add(serviceFeeReplyMap);
        }else{
            //是否返利
            Map replyMap = new HashMap();
            replyMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.REPAY_FLAG_N);
            replyMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.REPAY_FLAG_N));
            mapList.add(replyMap);
            //服务费是否返利/贴息SERVICE_FEE_REPAY_FLAG_N
            Map serviceFeeReplyMap = new HashMap();
            serviceFeeReplyMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.SERVICE_FEE_REPAY_FLAG_N);
            serviceFeeReplyMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.SERVICE_FEE_REPAY_FLAG_N));
            mapList.add(serviceFeeReplyMap);
        }

        //单利/复利
        if(null != definitionMap.get(HlsConstantUtil.PrjProject.SIMPLE_COMPOUND_TYPE_N)){
            Map compundType = new HashMap();
            compundType.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.SIMPLE_COMPOUND_TYPE_N);
            compundType.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.SIMPLE_COMPOUND_TYPE_N));
            mapList.add(compundType);
        }
        //是否阶梯式报价
        Map quotationFlagMap = new HashMap();
        quotationFlagMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.STEP_QUOTATION_FLAG_N);
        quotationFlagMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.STEP_QUOTATION_FLAG_N));
        mapList.add(quotationFlagMap);
        //名义货价收取方式
        Map residualValueMap = new HashMap();
        residualValueMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.RESIDUAL_VALUE_RECEIVED_N);
        residualValueMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.RESIDUAL_VALUE_RECEIVED_N));
        mapList.add(residualValueMap);
        //返利/贴息模式
        Map repayModeFlagMap = new HashMap();
        repayModeFlagMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.REPAY_MODE_N);
        repayModeFlagMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.REPAY_MODE_N));
        mapList.add(repayModeFlagMap);
        //是否倒推租金
        if(definitionMap.get(HlsConstantUtil.PrjProject.INT_RATE_CALC_FLAG) != null){
            Map intRateCalcFlagMap = new HashMap();
            intRateCalcFlagMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.INT_RATE_CALC_FLAG);
            intRateCalcFlagMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.INT_RATE_CALC_FLAG));
            mapList.add(intRateCalcFlagMap);
        }

        //服务费贴息规则
        if(definitionMap.get(HlsConstantUtil.PrjProject.LEASE_CHARGE_DISCOUNT_RULE) != null) {
            Map leaseChargeDiscountRuleMap = new HashMap();
            leaseChargeDiscountRuleMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.LEASE_CHARGE_DISCOUNT_RULE);
            leaseChargeDiscountRuleMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.LEASE_CHARGE_DISCOUNT_RULE));
            mapList.add(leaseChargeDiscountRuleMap);
        }
        //(租金)贴息支付期数
        if(definitionMap.get(HlsConstantUtil.PrjProject.RENTAL_DISCOUNT_TIMES) != null) {
            Map rentalDiscountTimesMap = new HashMap();
            rentalDiscountTimesMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.RENTAL_DISCOUNT_TIMES);
            rentalDiscountTimesMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.RENTAL_DISCOUNT_TIMES));
            mapList.add(rentalDiscountTimesMap);
        }
        //(租金)贴息支付期数翻译
        if(definitionMap.get(HlsConstantUtil.PrjProject.RENTAL_DISCOUNT_TIMES_N) != null) {
            Map rentalDiscountTimesNameMap = new HashMap();
            rentalDiscountTimesNameMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.RENTAL_DISCOUNT_TIMES_N);
            rentalDiscountTimesNameMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.RENTAL_DISCOUNT_TIMES_N));
            mapList.add(rentalDiscountTimesNameMap);
        }
        //服务费贴息规则
        if(definitionMap.get(HlsConstantUtil.PrjProject.LEASE_CHARGE_DISCOUNT_RULE_N) != null) {
            Map leaseChargeDiscountRuleNameMap = new HashMap();
            leaseChargeDiscountRuleNameMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.LEASE_CHARGE_DISCOUNT_RULE_N);
            leaseChargeDiscountRuleNameMap.put(ExcelUtils.KEY_VALUE, definitionMap.get(HlsConstantUtil.PrjProject.LEASE_CHARGE_DISCOUNT_RULE_N));
            mapList.add(leaseChargeDiscountRuleNameMap);
        }
        //取产品默认值前先判断参数列表是否已经存在
        List<String> paraStringList = mapList.stream().map(paraMap -> paraMap.get(ExcelUtils.KEY_FIELD).toString()).
                collect(Collectors.toList());
        for (Map map : list) {
            Map eleMap = new HashMap();

            String paraName = map.get(HlsConstantUtil.PrjProject.PRODUCT_PARA).toString().toLowerCase();
            eleMap.put(ExcelUtils.KEY_FIELD, paraName);
            eleMap.put(ExcelUtils.KEY_VALUE, map.get(HlsConstantUtil.PrjProject.DEFAULT_VALUE));

            //返利的时候
            if (definitionMap.get(HlsConstantUtil.HlsProductDefinition.REPAY_FLAG) != null && Y.equalsIgnoreCase(definitionMap.get(HlsConstantUtil.HlsProductDefinition.REPAY_FLAG).toString())) {
                //设置租息率默认值
                if (HlsConstantUtil.HlsProductDefinition.ProductPara.INT_RATE.equalsIgnoreCase(map.get(HlsConstantUtil.PrjProject.PRODUCT_PARA).toString())) {
                    Map repayMap = new HashMap();
                    repayMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.HlsProductDefinition.INT_RATE_REPLY_TMP);
                    repayMap.put(HlsConstantUtil.BaseController.VALUE, map.get(HlsConstantUtil.PrjProject.DEFAULT_VALUE));
                    mapList.add(repayMap);
                }
                //服务费率默认值
                if (HlsConstantUtil.HlsProductDefinition.ProductPara.LEASE_CHARGE_RATIO.equalsIgnoreCase(map.get(HlsConstantUtil.PrjProject.PRODUCT_PARA).toString())) {
                    Map repayMap = new HashMap();
                    repayMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.HlsProductDefinition.LEASE_CHARGE_RATIO_REPLY_TMP);
                    repayMap.put(HlsConstantUtil.BaseController.VALUE, map.get(HlsConstantUtil.PrjProject.DEFAULT_VALUE));
                    mapList.add(repayMap);
                }
            }

            if (paraStringList.contains(paraName)) {
                continue;
            }

            mapList.add(eleMap);
        }

        //增加基准利率
        project.setLeaseTerm(String.valueOf(quotation.getLeaseTerm().longValue()));
        Map rateMap = prjProjectMapper.queryBaseRateInfo(project);
        if (rateMap != null) {
            rateMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.BASE_RATE);
            rateMap.put(ExcelUtils.KEY_VALUE, rateMap.get(HlsConstantUtil.PrjProject.BASE_RATE));
            mapList.add(rateMap);
        }
        //增加项目默认值
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        Map maps = prjProjectMapper.prjRpLModifyDetailQuery(hlsCusPrjProject);
        maps.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.FLOATING_RANGE_METHOD);
        maps.put(ExcelUtils.KEY_VALUE, maps.get(HlsConstantUtil.PrjProject.FLOATING_RANGE_METHOD_N));
        mapList.add(maps);
        //起租日
//        Map leaseStartDateMap = new HashMap();
//        leaseStartDateMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.LEASE_START_DATE);
//        leaseStartDateMap.put(ExcelUtils.KEY_VALUE, ExcelUtils.getDays(project.getLeaseStartDate(), ExcelUtils.excelStartDate.getTime()));
//
//        mapList.add(leaseStartDateMap);
        //租赁物总价款
        Map amountMap = new HashMap();
        HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
        hlsCusPrjProjectLeaseItem.setProjectId(projectId);
        List<HlsCusPrjProjectLeaseItem> leaseItems = hlsCusPrjProjectLeaseItemMapper.select(hlsCusPrjProjectLeaseItem);
        if (CollectionUtils.isEmpty(leaseItems)) {
            throw new HlsCusException(IPrjProjectService.LEASE_ITEM_NOT_FOUND);
        }
        Double sumAmount = 0D;
        for (HlsCusPrjProjectLeaseItem item : leaseItems) {
            sumAmount = MathUtil.add(sumAmount,item.getPrice());
        }
        amountMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.LEASE_ITEM_AMOUNT);
        amountMap.put(ExcelUtils.KEY_VALUE, sumAmount);
        mapList.add(amountMap);

        //管理费率
        Map managementFeeRatioMap = new HashMap();
        managementFeeRatioMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.MANAGEMENT_FEE_RATIO);
        managementFeeRatioMap.put(ExcelUtils.KEY_VALUE, quotation.getManagementFeeRatio());
        mapList.add(managementFeeRatioMap);

        //管理费
        Map managementFeeMap = new HashMap();
        managementFeeMap.put(ExcelUtils.KEY_FIELD, HlsConstantUtil.PrjProject.MANAGEMENT_FEE);
        managementFeeMap.put(ExcelUtils.KEY_VALUE, quotation.getManagementFee());
        mapList.add(managementFeeMap);

        updateQuotaion(quotationId, mapList);
    }


    /*
    * 更新对应的quotation表记录的信息
    * */
    public void updateQuotaion(Long quotationId, List<Map> maps) throws Exception {
        Map<String, Object> data = new HashMap<>();

        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setQuotationId(quotationId);
        quotation = prjQuotationMapper.selectByPrimaryKey(quotation);

        //获取价目表类型
        String priceList = quotation.getPriceList();

        if (quotation.getSheets() == null) {
            HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
            hlsCalcConfig.setPriceList(priceList);
            hlsCalcConfig = priceListMapper.selectByPrimaryKey(hlsCalcConfig);
            quotation.setSheets(hlsCalcConfig.getSheets());
        }

        String stringSheets = GzipUtil.atob(quotation.getSheets());
        String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
        String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");

        //将价目表转成JSONArray
        JSONArray array = JSONArray.parseArray(jsonSheets);
        JSONObject sheetObject = array.getJSONObject(0);
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        //将sheetObject写入sheet
        readSheet(wb, sheet, sheetObject);

        //将map数据转成JSONArray
        JSONArray transArray = getTransObject(maps);

        HlsPriceListConfigHd hlsPriceListConfigHd = new HlsPriceListConfigHd();
        hlsPriceListConfigHd.setPriceList(priceList);
        List<HlsPriceListConfigHd> hlsPriceListConfigHdS = hlsPriceListConfigHdMapper.select(hlsPriceListConfigHd);


        //如果有配置单变量求解按钮 执行相关计算逻辑
//        if(CollectionUtils.isNotEmpty(hlsPriceListConfigHdS)){
//            transArray = calculateBts(quotationId,maps, priceList, null, transArray, hlsPriceListConfigHdS);
//        }

        List<HlsPriceListConfigLn> hlsPriceListConfigLns = getPriceListConfigLn(priceList, PRICE_TYPE_SINGLE).getHlsPriceListConfigLns();
        setCellFormat(transArray, hlsPriceListConfigLns);

        updateSheet(transArray, data, sheet, priceList);
        writeBack(wb, sheet, sheetObject, priceList);


        HlsCusPrjQuotation hlsCusPrjQuotation = JSONObject.parseObject(JSON.toJSONString(data), HlsCusPrjQuotation.class);
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation.setSheets(JSON.toJSONString(array));
        hlsCusPrjQuotation.setPriceList(quotation.getPriceList());
        hlsCusPrjQuotation.setSourceDocumentCategory(quotation.getSourceDocumentCategory());
        hlsCusPrjQuotation.setSourceDocumentId(quotation.getSourceDocumentId());
        hlsCalcSaveService.savePrjQuotation(RequestHelper.getCurrentRequest(true), hlsCusPrjQuotation);
    }

    private JSONArray calculateBts(Long quotationId, List<Map> maps, String priceList, JSONObject sheetObject, JSONArray transArray, List<HlsPriceListConfigHd> hlsPriceListConfigHdS) throws HlsCusException {
//        List<String> columnNameList= new ArrayList<>(16);
//        for (HlsPriceListConfigBT hlsPriceListConfigBT : hlsPriceListConfigBTS) {
//            //根据计算规则判断是否进行单变量求解计算
//            String calculateRule = hlsPriceListConfigBT.getCalculateRule();
//            if(StringUtils.isEmpty(calculateRule) || "NEVER".equals(calculateRule)){
//                continue;
//            }
//            Double targetIrr = null;
//            if("RATE_CALC_FLAG_TURE".equals(calculateRule)){
//                boolean returnFlag = true;
//                for (int i = 0; transArray != null && i < transArray.size(); i++) {
//                    JSONObject row = transArray.getJSONObject(i);
//                    String fieldName = row.getString(KEY_FIELD);
//                    Object value = row.get(KEY_VALUE);
//                    //当配置计算规则为RATE_CALC_FLAG_TURE，只有倒推租金为是的时候进行单变量求解
//                    if(HlsConstantUtil.PrjProject.INT_RATE_CALC_FLAG.equals(fieldName) && "Y".equals(value)){
//                        returnFlag = false;
//                    }
//
//                    List<Map> list = hlsProductDefinitionParaMapper.selectHlsProductDefinitionParaIrr(quotationId);
//                    if(CollectionUtils.isNotEmpty(list)){
//                        Map map = list.get(0);
//                        if(map.get(HlsConstantUtil.PrjProject.DEFAULT_VALUE) != null){
//                            Map irrMap = new HashMap();
//                            irrMap.put(ExcelUtils.KEY_FIELD, "target_irr");
//                            irrMap.put(ExcelUtils.KEY_VALUE, map.get(HlsConstantUtil.PrjProject.DEFAULT_VALUE));
//                            maps.add(irrMap);
//                            targetIrr = Double.parseDouble(map.get(HlsConstantUtil.PrjProject.DEFAULT_VALUE).toString());
//                        }
//                    }
//                }
///*                Map irrrateMap = new HashMap();
//                irrrateMap.put(ExcelUtils.KEY_FIELD, "");
//                irrrateMap.put(ExcelUtils.KEY_VALUE, rate);
//                maps.add(irrrateMap);*/
//
//
//                if(returnFlag){
//                    continue;
//                }
//            }
//
//            HlsPriceListConfigLn hlsPriceListConfigLn = hlsPriceListConfigLnMapper.queryConfigLnByPriceListAndColumnCode(priceList,
//                    hlsPriceListConfigBT.getVariableColumnCode());
//            String columnName = hlsPriceListConfigLn.getColumnName();
//            if(columnNameList.contains(columnName)){
//                continue;
//            }
//            columnNameList.add(columnName);
//            Double rate = (Double) priceListIrrService.goalSeek(sheetObject, transArray, hlsPriceListConfigBT,targetIrr);
//            Map rateMap = new HashMap();
//            rateMap.put(ExcelUtils.KEY_FIELD, columnName);
//            rateMap.put(ExcelUtils.KEY_VALUE, rate);
//            maps.add(rateMap);
//        }
//        transArray = getTransObject(maps);
//        return transArray;
//    }
//
//    public String getSetDefaultSheet(String  sheets,List<Map> maps,String priceList){
//
//        Map<String, Object> data = new HashMap<>();
//        JSONArray array = JSONArray.parseArray(sheets);
//        JSONObject sheetObject = array.getJSONObject(0);
//        XSSFWorkbook wb = new XSSFWorkbook();
//        XSSFSheet sheet = wb.createSheet();
//        //将sheetObject写入sheet
//        readSheet(wb, sheet, sheetObject);
//
//        //将map数据转成JSONArray
//        JSONArray transArray = getTransObject(maps);
//
//        List<HlsPriceListConfigLn> hlsPriceListConfigLns = getPriceListConfigLn(priceList, PRICE_TYPE_SINGLE).getHlsPriceListConfigLns();
//        setCellFormat(transArray, hlsPriceListConfigLns);
//
//        updateSheet(transArray, data, sheet, priceList);
//        writeBack(wb, sheet, sheetObject, priceList);
//        return JSON.toJSONString(array);
        return null;
    }

    /**
     * 替换报价器sheets参数并保存
     *
     * @param iRequest
     * @param quotationId 报价ID
     * @param maps        替换参数
     * @throws Exception
     */
    public void updateQuotaionSheets(IRequest iRequest, Long quotationId, List<Map> maps) throws Exception {
        Map<String, Object> data = new HashMap<>();

        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(quotationId);
        hlsCusPrjQuotation = prjQuotationService.selectByPrimaryKey(iRequest, hlsCusPrjQuotation);

        //获取价目表类型
        String priceList = hlsCusPrjQuotation.getPriceList();
        String sheets = hlsCusPrjQuotation.getSheets();

        if (sheets == null) {
            HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
            hlsCalcConfig.setPriceList(priceList);
            hlsCalcConfig = priceListMapper.selectByPrimaryKey(hlsCalcConfig);
            hlsCusPrjQuotation.setSheets(hlsCalcConfig.getSheets());
        }

        //将价目表转成JSONArray
        JSONArray array = JSONArray.parseArray(sheets);
        JSONObject sheetObject = array.getJSONObject(0);
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        //将sheetObject写入sheet
        readSheet(wb, sheet, sheetObject);

        //将map数据转成JSONArray
        JSONArray transArray = getTransObject(maps);
        List<HlsPriceListConfigLn> hlsPriceListConfigLns = getPriceListConfigLn(priceList, PRICE_TYPE_SINGLE).getHlsPriceListConfigLns();
        setCellFormat(transArray, hlsPriceListConfigLns);

        updateSheet(transArray, data, sheet, priceList);
        writeBack(wb, sheet, sheetObject, priceList);

        //更新sheets
        hlsCusPrjQuotation.setSheets(JSON.toJSONString(array));
        hlsCusPrjQuotation = prjQuotationService.updateByPrimaryKeySelective(iRequest, hlsCusPrjQuotation);

        //获取sheets关联大字段表
        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
        hlsCusPrjQuotationDetails.setQuotationId(hlsCusPrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList;
        detailsList = prjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
        if (detailsList.size() > 0) {
            detailsList.get(0).setSheets(hlsCusPrjQuotation.getSheets());
            detailsList.get(0).set__status("update");
        } else {
            hlsCusPrjQuotationDetails.setSheets(hlsCusPrjQuotation.getSheets());
            detailsList.add(hlsCusPrjQuotationDetails);
            detailsList.get(0).set__status("insert");
        }
        prjQuotationDetailsService.batchUpdate(iRequest, detailsList);
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
    private void updateSheet(JSONArray cells, Map<String, Object> data, XSSFSheet sheet, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLn(priceList);
        Map<String, HlsPriceListConfigLn> lnMap = new HashMap<>();
        for (HlsPriceListConfigLn ln : lns) {
            lnMap.put(ln.getColumnName().toLowerCase(), ln);
        }
        for (int i = 0; cells != null && i < cells.size(); i++) {
            JSONObject row = cells.getJSONObject(i);
            String fieldName = row.getString(KEY_FIELD);
            Object value = row.get(KEY_VALUE);
            HlsPriceListConfigLn ln = lnMap.get(fieldName.toLowerCase());
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

    private List<HlsPriceListConfigLn> getPriceListConfigLn(String priceList) {
        return getPriceListConfigLn(priceList, PRICE_TYPE_SINGLE).getHlsPriceListConfigLns();
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
                value= 26 * (value + 1);
            }
            cellIndex += value;
        }
        cellPosition.setRowIndex(Integer.valueOf(rowString) - 1);
        cellPosition.setCellIndex(cellIndex);
        return cellPosition;
    }

    private void setCellValue(JSONObject dataObject, Object value, XSSFCell cell) {
        if ( Objects.isNull(value) || StringUtils.isEmpty(value.toString())) {
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

    public static String toDate(int days) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = new GregorianCalendar(1900, 0, -1);

        Date d = c.getTime();
        Date _d = DateUtils.addDays(d, days); //42605是距离1900年1月1日的天数
        return simpleDateFormat.format(_d);
    }


    private void writeBack(XSSFWorkbook wb, XSSFSheet sheet, JSONObject sheetObject, String priceList) {
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        List<HlsPriceListConfigLn> singleLines = getPriceListConfigLn(priceList);
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, PRICE_TYPE_MULTI_LINE);

        int jsonRowIndex = 0;
        JSONArray rowsObject = sheetObject.getJSONArray(KEY_ROWS);
        for (int i = 0; i < singleLines.size(); i++) {
            HlsPriceListConfigLn singleLine = singleLines.get(i);
            String columnCode = singleLine.getColumnCode();
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
                cell = evaluator.evaluateInCell(cell);
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
            JSONObject rowObject = getRowObject(rowsObject, i);
            if (rowObject == null) {
                continue;
            }
            JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
            if (cellsObject == null) {
                continue;
            }
            for (int j = 0; j < cellsObject.size(); j++) {
                XSSFCell cell = sheet.getRow(i).getCell(cellsObject.getJSONObject(j).getIntValue(KEY_INDEX));
                if (cell != null && cell.getCellTypeEnum() == CellType.FORMULA) {
                    CellValue cellValue = null;
                    try {
                        cellValue = evaluator.evaluate(cell);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Object rawValue = getRawValue(cellValue);
                    cellsObject.getJSONObject(j).put(KEY_VALUE, rawValue);
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

    public HlsCusPrjQuotation PrjQuotationSubmit(IRequest iRequest, HlsCusPrjQuotation HlsCusPrjQuotation) throws Exception {
        //校验公式是否被修改
//        hlsCalcExcelImportUtilService.excelFormatHasChange(iRequest,HlsCusPrjQuotation.getPriceList(),HlsCusPrjQuotation.getSheets());
        String jsonStr = JSON.toJSONString(hlsCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, HlsCusPrjQuotation.getSheets(), HlsCusPrjQuotation.getPriceList(), "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, com.hand.hls.prj.dto.HlsCusPrjQuotation.class);
        //  HlsCusPrjQuotation prjQuotationDto=(HlsCusPrjQuotation)hlsCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest,HlsCusPrjQuotation.getSheets(),HlsCusPrjQuotation.getPriceList(),"prj");
        if (prjQuotationDto.getLeaseTimes() == null) {
            throw new IllegalArgumentException("期数未取到！");
        }
        prjQuotationDto.setEnabledFlag(HlsCusPrjQuotation.getEnabledFlag());
        prjQuotationDto.setPriceList(HlsCusPrjQuotation.getPriceList());
        prjQuotationDto.setSheets(HlsCusPrjQuotation.getSheets());
        prjQuotationDto.setQuotationId(HlsCusPrjQuotation.getQuotationId());
        prjQuotationDto.setSourceDocumentCategory(HlsCusPrjQuotation.getSourceDocumentCategory());
        prjQuotationDto.setQuotationType(HlsCusPrjQuotation.getQuotationType());
        prjQuotationDto.setSourceDocumentId(HlsCusPrjQuotation.getSourceDocumentId());
        prjQuotationDto.setStatus(HlsCusPrjQuotation.getStatus());
//        prjQuotationDto.setQuotationCoding(HlsCusPrjQuotation.getQuotationCoding());
        prjQuotationDto.setDescription(HlsCusPrjQuotation.getDescription());

        prjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

        //获取项目项下的报价
        com.hand.hls.prj.dto.HlsCusPrjQuotation savePrjQuotation = new HlsCusPrjQuotation();
        savePrjQuotation.setQuotationId(prjQuotationDto.getQuotationId());
        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
        hlsCusPrjQuotationDetails.setQuotationId(savePrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList;
        detailsList = prjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
        if (detailsList.size() > 0) {
            detailsList.get(0).setSheets(prjQuotationDto.getSheets());
            detailsList.get(0).set__status("update");
        } else {
            hlsCusPrjQuotationDetails.setSheets(prjQuotationDto.getSheets());
            detailsList.add(hlsCusPrjQuotationDetails);
            detailsList.get(0).set__status("insert");
        }
        prjQuotationDetailsService.batchUpdate(iRequest, detailsList);

        //保存现金流表
        prjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);

        /*//将报价现金流更新合同现金流
        conContractCashflowService.saveCashflowFromQuotationCashflow(iRequest, prjQuotationDto.getSourceDocumentId(), prjQuotationDto.getQuotationId());*/

        return prjQuotationDto;
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
}
