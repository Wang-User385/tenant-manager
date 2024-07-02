package com.hand.hls.hls.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationLnMapper;
import com.hand.hls.hls.service.HlsDurationCalcService;
import com.hand.hls.hls.service.HlsDurationCompareService;
import com.hand.hls.hls.service.HlsDurationLnService;
import com.hand.hls.hls.service.HlsQuotationCalcService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.prj.service.IPrjQuotationCashflowService;
import com.hand.hls.sys.utils.OracleUtils;
import com.hand.hls.utils.HlsCusMathUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hls.hls.service.impl.FundingPlanServiceImpl.DateToLocaleDate;
import static com.hand.hls.hls.service.impl.FundingPlanServiceImpl.LocalDateToDate;
import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationCalcServiceImpl extends BaseServiceImpl<HlsDurationHd> implements HlsDurationCalcService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HlsDurationLnMapper hlsDurationLnMapper;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private HlsCalcConfigMapper priceListMapper;

    @Autowired
    private HlsQuotationCalcService hlsQuotationCalcService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private HlsCusPrjQuotationDetailsService prjQuotationDetailsService;

    @Autowired
    private HlsCusPrjQuotationDetailsMapper prjQuotationDetailsMapper;

    @Autowired
    private IPrjQuotationCashflowService prjQuotationCashflowService;

    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsDurationLnService hlsDurationLnService;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsDurationCompareService hlsDurataionCompareService;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;


    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    static final String INPUT_SHEET = "INPUT";

    private void etUpdateQuotation(IRequest iRequest, HlsDurationLn ln) throws Exception {

        String etPriceList = ln.getEtPriceList();
        Long etQuotationId = ln.getEtQuotationId();

        //变更报价
        HlsCalcConfig config = priceListMapper.selectByPrimaryKey(etPriceList);
        String sheets = hlsQuotationCalcService.unzipSheet(config.getSheets());
        JSONArray array = JSONArray.parseArray(sheets);
        XSSFWorkbook wb = new XSSFWorkbook();
        hlsQuotationCalcService.readSheets(wb, array);


        //构造头需要赋值的字段
        HlsDurationLn durationLn = hlsDurationLnMapper.hlsDurationLnEtCalc(ln).get(0);

        JSONArray modifiedHd = hlsQuotationCalcService.extractHeadDataFromObject(durationLn, ln.getEtPriceList(), INPUT_SHEET);

        //构造行需要赋值的字段(取现金流)
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setContractId(ln.getContractId());
        contractCashflow.setCfStatus("RELEASE");
//        List<HlsCusConContractCashflow> contractCashflowList = cashflowMapper.select(contractCashflow);
        //通过sql查询将其他现金流并入
        List<HlsCusConContractCashflow> contractCashflowList = cashflowMapper.queryAllReq(contractCashflow);
        //罚息现金流
        Double unreceivedFineAmt = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(9L) == 0 && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount)).doubleValue(), 0.0), 2);
        //已收租金
        Double receivedAmount = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedAmount)).doubleValue(), 0.0), 2);

        //只需要租金现金流
        contractCashflowList = contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 || item.getCfItem().compareTo(10L) == 0 || item.getCfItem().compareTo(0L) == 0).
                sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes)).
                collect(Collectors.toList());

        JSONArray modifiedLines = hlsQuotationCalcService.extractLineDataFromObject(contractCashflowList, ln.getEtPriceList(), INPUT_SHEET);

        //将变更字段赋值到sheet
        hlsQuotationCalcService.updateSheet(modifiedHd, wb.getSheet(INPUT_SHEET), etPriceList);
        hlsQuotationCalcService.updateSheetLines(modifiedLines, wb.getSheet(INPUT_SHEET), etPriceList);
        hlsQuotationCalcService.writeBack(wb, array, etPriceList);

        //回写prj_quotation 相关表
        hlsQuotationCalcService.updateQuotationFromSheet(iRequest, etQuotationId, etPriceList, array);

        //回写hls_duration_ln
        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setQuotationId(etQuotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflow);

        //更新stage_type 字段
        prjQuotationCashflowList.stream().forEach(item -> {
            item.setStageType("租赁期");
            prjQuotationCashflowMapper.updateByPrimaryKeySelective(item);
        });

        //变更后总租金
        Double totalRental = HlsCusMathUtil.round(nvl(prjQuotationCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 || item.getCfItem().compareTo(10L) == 0).
                collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount)).doubleValue(), 0.0), 2);


        /*Double etPreAmount = prjQuotationCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(110L) == 0).
                collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount));
        ln.setEtPreAmount(etPreAmount);*/
        ln.setEtAmount(HlsCusMathUtil.sub(nvl(ln.getEtPreAmount(), 0.0), nvl(ln.getEtExemptAmount(), 0.0)));
        ln.setUnreceivedFineAmt(unreceivedFineAmt);
        ln.setRentalResidualAmount(HlsCusMathUtil.sub(totalRental, receivedAmount, 2));
        hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);

        //插入对比表 hls_duration_compare
        hlsDurataionCompareService.createCompare(iRequest, ln);
    }


    private void prepaymentUpdateQuotation(IRequest iRequest, HlsDurationLn ln) throws Exception {
        String prepaymentPriceList = ln.getPrepaymentPriceList();
        Long prepaymentQuotationId = ln.getPrepaymentQuotationId();

        //变更报价
        HlsCalcConfig config = priceListMapper.selectByPrimaryKey(prepaymentPriceList);
        String sheets = hlsQuotationCalcService.unzipSheet(config.getSheets());
        JSONArray array = JSONArray.parseArray(sheets);
        XSSFWorkbook wb = new XSSFWorkbook();
        hlsQuotationCalcService.readSheets(wb, array);

        //--------------- 提前还款的报价 在原来报价的基础上做了 修改 需要把原来报价的要素都 复制过来
        HlsCusPrjQuotation prjQuotation = prjQuotationMapper.selectByPrimaryKey(prepaymentQuotationId);
        List<Map> calcInfo = getCalcInfo(prjQuotation, ln);
        JSONArray jsonArray = hlsQuotationCalcService.extractHeadDataFromMap(calcInfo, prepaymentPriceList, INPUT_SHEET);
        hlsQuotationCalcService.updateSheet(jsonArray, wb.getSheet(INPUT_SHEET), prepaymentPriceList);
        //----------------


        //构造头需要赋值的字段（提前还款相关字段）
        HlsDurationLn durationLn = hlsDurationLnMapper.hlsDurationLnPrepaymentCalc(ln).get(0);
        JSONArray modifiedHd = hlsQuotationCalcService.extractHeadDataFromObject(durationLn, ln.getPrepaymentPriceList(), INPUT_SHEET);


        //构造行需要赋值的字段(取现金流)
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setContractId(ln.getContractId());
        contractCashflow.setCfStatus("RELEASE");
        List<HlsCusConContractCashflow> contractCashflowList = cashflowMapper.select(contractCashflow);

        //罚息现金流
        Double unreceivedFineAmt = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(9L) == 0 && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount)).doubleValue(), 0.0), 2);
        //已收租金
        Double receivedAmount = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedAmount)).doubleValue(), 0.0), 2);

        //只需要租金现金流
        contractCashflowList = contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 || item.getCfItem().compareTo(10L) == 0).
                sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes)).
                collect(Collectors.toList());


        JSONArray modifiedLines = hlsQuotationCalcService.extractLineDataFromObject(contractCashflowList, ln.getPrepaymentPriceList(), INPUT_SHEET);

        //将变更字段赋值到sheet
        hlsQuotationCalcService.updateSheet(modifiedHd, wb.getSheet(INPUT_SHEET), prepaymentPriceList);
        hlsQuotationCalcService.updateSheetLines(modifiedLines, wb.getSheet(INPUT_SHEET), prepaymentPriceList);
        hlsQuotationCalcService.writeBack(wb, array, prepaymentPriceList);

        //回写prj_quotation 相关表
        hlsQuotationCalcService.updateQuotationFromSheet(iRequest, prepaymentQuotationId, prepaymentPriceList, array);

        //回写hls_duration_ln
        HlsCusPrjQuotation prepaymentPrjQuotation = prjQuotationMapper.selectByPrimaryKey(prepaymentQuotationId);
        ln.setPrepaymentPreAmount(prepaymentPrjQuotation.getPrepaymentPreAmount());
        ln.setPrepaymentAmount(prepaymentPrjQuotation.getPrepaymentAmount());
        ln.setPrepaymentExemptAmount(prepaymentPrjQuotation.getPrepaymentExemptAmount());
        ln.setAltLprRefDay(prepaymentPrjQuotation.getAltLprRefDay());
        ln.setAltBaseRate(prepaymentPrjQuotation.getAltBaseRate());

        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setQuotationId(prepaymentQuotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflow);
        //变更后总租金
        Double totalRental = HlsCusMathUtil.round(nvl(prjQuotationCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 || item.getCfItem().compareTo(10L) == 0).
                collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount)).doubleValue(), 0.0), 2);

        ln.setUnreceivedFineAmt(unreceivedFineAmt);
        ln.setRentalResidualAmount(HlsCusMathUtil.sub(totalRental, receivedAmount, 2));


        hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);
        //插入对比表 hls_duration_compare
        hlsDurataionCompareService.createCompare(iRequest, ln);

    }
    /*金融条款变更（兴业）== 业务变更（君成）*/
    private void changeUpdateQuotation(IRequest iRequest, HlsDurationLn ln) throws Exception {
        String changePriceList = ln.getChangePriceList();
        Long changeQuotationId = ln.getChangeQuotationId();

        //变更报价
        HlsCalcConfig config = priceListMapper.selectByPrimaryKey(changePriceList);
        String sheets = hlsQuotationCalcService.unzipSheet(config.getSheets());
        JSONArray array = JSONArray.parseArray(sheets);
        XSSFWorkbook wb = new XSSFWorkbook();
        hlsQuotationCalcService.readSheets(wb, array);

        //--------------- 金融条款变更的报价 在原来报价的基础上做了 修改 需要把原来报价的要素都 复制过来
        HlsCusPrjQuotation prjQuotation = prjQuotationMapper.selectByPrimaryKey(changeQuotationId);
        List<Map> calcInfo = getCalcInfo(prjQuotation, ln);
        JSONArray jsonArray = hlsQuotationCalcService.extractHeadDataFromMap(calcInfo, changePriceList, INPUT_SHEET);
        hlsQuotationCalcService.updateSheet(jsonArray, wb.getSheet(INPUT_SHEET), changePriceList);
        //----------------


        //构造头需要赋值的字段（金融条款相关字段）
        HlsDurationLn durationLn = hlsDurationLnMapper.hlsDurationLnChangeCalcNew(ln).get(0);
        JSONArray modifiedHd = hlsQuotationCalcService.extractHeadDataFromObject(durationLn, ln.getChangePriceList(), INPUT_SHEET);


        //构造行需要赋值的字段(取现金流)
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setContractId(ln.getContractId());
        contractCashflow.setCfStatus("RELEASE");
//        List<HlsCusConContractCashflow> contractCashflowList = cashflowMapper.select(contractCashflow);
        //通过sql查询将其他现金流并入
        List<HlsCusConContractCashflow> contractCashflowList = cashflowMapper.queryAllReq(contractCashflow);

        //罚息现金流
        Double unreceivedFineAmt = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(9L) == 0 && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount)).doubleValue(), 0.0), 2);
        //已收租金
        Double receivedAmount = HlsCusMathUtil.round(nvl(contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedAmount)).doubleValue(), 0.0), 2);
        //只需要租金现金流
        contractCashflowList = contractCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0  || item.getCfItem().compareTo(0L) == 0).
                sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes)).
                collect(Collectors.toList());

        JSONArray modifiedLines = hlsQuotationCalcService.extractLineDataFromObject(contractCashflowList, ln.getChangePriceList(), INPUT_SHEET);

        //将变更字段赋值到sheet
        hlsQuotationCalcService.updateSheet(modifiedHd, wb.getSheet(INPUT_SHEET), changePriceList);
        hlsQuotationCalcService.updateSheetLines(modifiedLines, wb.getSheet(INPUT_SHEET), changePriceList);
        hlsQuotationCalcService.writeBack(wb, array, changePriceList);

        //回写prj_quotation 相关表
        hlsQuotationCalcService.updateQuotationFromSheet(iRequest, changeQuotationId, changePriceList, array);

        //回写hls_duration_ln
        HlsCusPrjQuotation changePrjQuotation = prjQuotationMapper.selectByPrimaryKey(changeQuotationId);
        ln.setAltLprRefDay(changePrjQuotation.getAltLprRefDay());
        ln.setAltBaseRate(changePrjQuotation.getAltBaseRate());
        ln.setPrinAtCd(changePrjQuotation.getPrinAtCd());
        ln.setUnreceivedFineAmt(changePrjQuotation.getUnreceivedFineAmt());
        ln.setResidualPrinAtCd(changePrjQuotation.getResidualPrinAtCd());
        ln.setMonthAfterAlt(changePrjQuotation.getMonthAfterAlt());
        ln.setFineReorganizedFlag(changePrjQuotation.getFineReorganizedFlag());
        ln.setAltRentingFrequency(changePrjQuotation.getAltRentingFrequency());
        ln.setAltRentingFreCustom(changePrjQuotation.getAltRentingFreCustom());
        ln.setAltRentalRoundingType(changePrjQuotation.getAltRentalRoundingType());
        ln.setAltPayType(changePrjQuotation.getAltPayType());
        ln.setAltRentalPayDate(changePrjQuotation.getAltRentalPayDate());
        ln.setAltLeaseTimes(changePrjQuotation.getAltLeaseTimes());
        ln.setAltIntRate(changePrjQuotation.getAltIntRate());
        ln.setAltFloatingWayRate(changePrjQuotation.getAltFloatingWayRate());
        ln.setAltCalcDateType(changePrjQuotation.getAltCalcDateType());
        ln.setAltGraceLeaseTerm(changePrjQuotation.getAltGraceLeaseTerm());

        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setQuotationId(changeQuotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflow);
        //变更后总租金
        Double totalRental = HlsCusMathUtil.round(nvl(prjQuotationCashflowList.stream().
                filter(item -> item.getCfItem().compareTo(1L) == 0 || item.getCfItem().compareTo(10L) == 0).
                collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount)).doubleValue(), 0.0), 2);

        ln.setUnreceivedFineAmt(unreceivedFineAmt);
        ln.setRentalResidualAmount(HlsCusMathUtil.sub(totalRental, receivedAmount, 2));

        hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);
        //插入对比表 hls_duration_compare
        hlsDurataionCompareService.createCompare(iRequest, ln);

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

    private List<Map> getCalcInfo(HlsCusPrjQuotation prjQuotation, HlsDurationLn ln) {

        Map quotationMap = (Map) prjQuotationMapper.queryQuotationInfoByQuotationIdMarketing(prjQuotation).get(0);
        List<Map> mapList = new ArrayList<>();
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

        //报价方式
        Map map3 = new HashMap();
        map3.put("field", "price_list_type_n");
        map3.put("value", stringDataTran(quotationMap.get("price_list_type_n")));
        mapList.add(map3);

        //租赁业务类型
        Map map5 = new HashMap();
        map5.put("field", "business_type_n");
        map5.put("value", stringDataTran(quotationMap.get("business_type_n")));
        mapList.add(map5);

        //币种
        Map map8 = new HashMap();
        map8.put("field", "currency_n");
        map8.put("value", stringDataTran(quotationMap.get("currency_n")));
        mapList.add(map8);

        //汇率
        Map map11 = new HashMap();
        map11.put("field", "exchange_rate");
        map11.put("value", doubleDataTran(quotationMap.get("exchange_rate")));
        mapList.add(map11);

        //融资金额
        Map map12 = new HashMap();
        map12.put("field", "finance_amount");
        map12.put("value", doubleDataTran(quotationMap.get("finance_amount")));
        mapList.add(map12);

        //首付款
        Map map13 = new HashMap();
        map13.put("field", "first_pay");
        map13.put("value", doubleDataTran(quotationMap.get("first_pay")));
        mapList.add(map13);

        //首期租金
        Map map14 = new HashMap();
        map14.put("field", "down_payment");
        map14.put("value", doubleDataTran(quotationMap.get("down_payment")));
        mapList.add(map14);


        //首次放款日期
        if (quotationMap.get("first_release_date") != null) {
            Map map18 = new HashMap();
            map18.put("field", "first_release_date");
            map18.put("value", df.format(quotationMap.get("first_release_date")));
            mapList.add(map18);
        }

        //租赁期限
        Map map19 = new HashMap();
        map19.put("field", "lease_term");
        map19.put("value", doubleDataTran(quotationMap.get("lease_term")));
        mapList.add(map19);

        //租金支付频率（计算用）（月）
        Map map20 = new HashMap();
        map20.put("field", "renting_frequency_n");
        map20.put("value", stringDataTran(quotationMap.get("renting_frequency_n")));
        mapList.add(map20);

        //起租日期
        if (quotationMap.get("lease_start_date") != null) {
            Map map21 = new HashMap();
            map21.put("field", "lease_start_date");
            map21.put("value", df.format(quotationMap.get("lease_start_date")));
            mapList.add(map21);
        }

        //租赁期届满日
        if (quotationMap.get("lease_end_date") != null) {
            Map map22 = new HashMap();
            map22.put("field", "lease_end_date_old");
            map22.put("value", df.format(quotationMap.get("lease_end_date")));
            mapList.add(map22);
        }

        //是否含有租前期
        Map map26 = new HashMap();
        map26.put("field", "pre_lease_flag_n");
        map26.put("value", stringDataTran(quotationMap.get("pre_lease_flag_n")));
        mapList.add(map26);

        //租前期期限（月）
        Map map27 = new HashMap();
        map27.put("field", "pre_lease_term");
        map27.put("value", doubleDataTran(quotationMap.get("pre_lease_term")));
        mapList.add(map27);

        //是否含有宽限期
        Map map28 = new HashMap();
        map28.put("field", "grace_flag_n");
        map28.put("value", stringDataTran(quotationMap.get("grace_flag_n")));
        mapList.add(map28);

        //宽限期期限（月）
        Map map29 = new HashMap();
        map29.put("field", "grace_lease_term");
        map29.put("value", doubleDataTran(quotationMap.get("grace_lease_term")));
        mapList.add(map29);

        //计算日类型
        Map map33 = new HashMap();
        map33.put("field", "calc_date_type_n");
        map33.put("value", stringDataTran(quotationMap.get("calc_date_type_n")));
        mapList.add(map33);

        //租金支付日（计算用）
        Map map34 = new HashMap();
        map34.put("field", "rental_pay_date");
        map34.put("value", quotationMap.get("rental_pay_date"));
        mapList.add(map34);

        //租金支付方式
        Map map35 = new HashMap();
        map35.put("field", "pay_type_n");
        map35.put("value", quotationMap.get("pay_type_n"));
        mapList.add(map35);

        //利率类型
        Map map36 = new HashMap();
        map36.put("field", "int_rate_type_n");
        map36.put("value", stringDataTran(quotationMap.get("int_rate_type_n")));
        mapList.add(map36);

        /*//利率浮动类型
        Map map37 = new HashMap();
        map37.put("field", "float_type");
        map37.put("value", stringDataTran(quotationMap.get("float_type")));
        mapList.add(map37);*/

        //利率日期参考系
        Map map38 = new HashMap();
        map38.put("field", "lpr_ref_day_n");
        map38.put("value", stringDataTran(quotationMap.get("lpr_ref_day_n")));
        mapList.add(map38);

        //LPR指定日期
        if (quotationMap.get("lpr_name_date") != null) {
            Map map39 = new HashMap();
            map39.put("field", "lpr_name_date");
            map39.put("value", quotationMap.get("lpr_name_date"));
            mapList.add(map39);
        }

        //基准利率标准
        Map map40 = new HashMap();
        map40.put("field", "base_rate_type_n");
        map40.put("value", stringDataTran(quotationMap.get("base_rate_type_n")));
        mapList.add(map40);

        //基准利率值
        Map map41 = new HashMap();
        map41.put("field", "base_rate_old");
        map41.put("value", doubleDataTran(quotationMap.get("base_rate")));
        mapList.add(map41);

        //浮动值（BP）
        Map map42 = new HashMap();
        map42.put("field", "floating_way_rate");
        map42.put("value", doubleDataTran(quotationMap.get("floating_way_rate")));
        mapList.add(map42);

        //年利率(%)
        Map map43 = new HashMap();
        map43.put("field", "int_rate_old");
        map43.put("value", doubleDataTran(quotationMap.get("int_rate")));
        mapList.add(map43);

        //是否含税利率
        Map map44 = new HashMap();
        map44.put("field", "vatable_rate_flag_n");
        map44.put("value", stringDataTran(quotationMap.get("vatable_rate_flag_n")));
        mapList.add(map44);

        //是否含税本金
        Map map45 = new HashMap();
        map45.put("field", "vatable_prin_flag_n");
        map45.put("value", stringDataTran(quotationMap.get("vatable_prin_flag_n")));
        mapList.add(map45);

        //税务认定结构
        Map map46 = new HashMap();
        map46.put("field", "tax_structure_n");
        map46.put("value", stringDataTran(quotationMap.get("tax_structure_n")));
        mapList.add(map46);

        //税种
        Map map47 = new HashMap();
        map47.put("field", "tax_type_code");
        map47.put("value", stringDataTran(quotationMap.get("tax_type_code")));
        mapList.add(map47);

        //调息基准日期
        Map map48 = new HashMap();
        map48.put("field", "lpr_base_date_n");
        map48.put("value", stringDataTran(quotationMap.get("lpr_base_date_n")));
        mapList.add(map48);

        //调息指定日期
        Map map49 = new HashMap();
        map49.put("field", "lpr_fixed_date");
        map49.put("value", stringDataTran(quotationMap.get("lpr_fixed_date")));
        mapList.add(map49);

        //调息期限
        Map map50 = new HashMap();
        map50.put("field", "lpr_adjustment_term");
        map50.put("value", stringDataTran(quotationMap.get("lpr_adjustment_term")));
        mapList.add(map50);

        //调息期次
        Map map51 = new HashMap();
        map51.put("field", "lpr_adjustment_period_n");
        map51.put("value", stringDataTran(quotationMap.get("lpr_adjustment_period_n")));
        mapList.add(map51);

        //资产管理费类型
        Map map52 = new HashMap();
        map52.put("field", "asset_mgt_fee_type_n");
        map52.put("value", stringDataTran(quotationMap.get("asset_mgt_fee_type_n")));
        mapList.add(map52);

        //利息摊销方法
        Map map53 = new HashMap();
        map53.put("field", "interest_amortization_method_n");
        map53.put("value", stringDataTran(quotationMap.get("interest_amortization_method_n")));
        mapList.add(map53);

        //租金取整
        Map map54 = new HashMap();
        map54.put("field", "rental_rounding_type_n");
        map54.put("value", stringDataTran(quotationMap.get("rental_rounding_type_n")));
        mapList.add(map54);

        //留购价
        Map map55 = new HashMap();
        map55.put("field", "residual_value_old");
        map55.put("value", doubleDataTran(quotationMap.get("residual_value")));
        mapList.add(map55);

        //支付期数
        Map map56 = new HashMap();
        map56.put("field", "lease_times_old");
        map56.put("value", doubleDataTran(quotationMap.get("lease_times")));
        mapList.add(map56);


        Map map57 = new HashMap();
        map57.put("field", "interest_year_days");
        map57.put("value", doubleDataTran(quotationMap.get("interest_year_days")));
        mapList.add(map57);


        Map map58 = new HashMap();
        map58.put("field", "vat_rate");
        map58.put("value", doubleDataTran(quotationMap.get("vat_rate")));
        mapList.add(map58);

        //提前还款 申请减免金额
        Map map59 = new HashMap();
        map59.put("field", "prepayment_exempt_amount");
        map59.put("value", doubleDataTran(quotationMap.get("prepayment_exempt_amount")));
        mapList.add(map59);


        Map map60 = new HashMap();
        map60.put("field", "et_pre_amount");
        map60.put("value", doubleDataTran(ln.getEtPreAmount()));
        mapList.add(map60);

        Map map61 = new HashMap();
        map61.put("field", "prepayment_pre_amount");
        map61.put("value", doubleDataTran(ln.getPrepaymentPreAmount()));
        mapList.add(map61);

        Map map62 = new HashMap();
        map62.put("field", "lease_item_amount");
        map62.put("value", doubleDataTran(quotationMap.get("lease_item_amount")));
        mapList.add(map62);
        //首付款比例
        Map map63 = new HashMap();
        map63.put("field", "down_payment_ratio");
        map63.put("value", doubleDataTran(quotationMap.get("down_payment_ratio")));
        mapList.add(map63);
        //保证金
        Map map64 = new HashMap();
        map64.put("field", "deposit");
        map64.put("value", doubleDataTran(quotationMap.get("deposit")));
        mapList.add(map64);
        //保证金比例
        Map map65 = new HashMap();
        map65.put("field", "deposit_ratio");
        map65.put("value", doubleDataTran(quotationMap.get("deposit_ratio")));
        mapList.add(map65);
        //手续费
        if(quotationMap.get("lease_charge") != null) {
            Map map66 = new HashMap();
            map66.put("field", "lease_charge");
            map66.put("value", doubleDataTran(quotationMap.get("lease_charge")));
            mapList.add(map66);
        }
        //手续费比例
        if(quotationMap.get("lease_charge_ratio") != null) {
            Map map67 = new HashMap();
            map67.put("field", "lease_charge_ratio");
            map67.put("value", doubleDataTran(quotationMap.get("lease_charge_ratio")));
            mapList.add(map67);
        }
        //管理费
        if(quotationMap.get("lease_mgt_fee") != null) {
            Map map68 = new HashMap();
            map68.put("field", "lease_mgt_fee");
            map68.put("value", doubleDataTran(quotationMap.get("lease_mgt_fee")));
            mapList.add(map68);
        }
        //管理费比例
        if(quotationMap.get("lease_mgt_fee_ratio") != null) {
            Map map69 = new HashMap();
            map69.put("field", "lease_mgt_fee_ratio");
            map69.put("value", doubleDataTran(quotationMap.get("lease_mgt_fee_ratio")));
            mapList.add(map69);
        }
        Map map70 = new HashMap();
        map70.put("field", "irr");
        map70.put("value", doubleDataTran(quotationMap.get("irr")));
        mapList.add(map70);

        Map map71 = new HashMap();
        map71.put("field", "xirr");
        map71.put("value", doubleDataTran(quotationMap.get("xirr")));
        mapList.add(map71);
        //期末留购价
        Map map72 = new HashMap();
        map72.put("field", "residual_value");
        map72.put("value", doubleDataTran(quotationMap.get("residual_value")));
        mapList.add(map72);

        Map map73 = new HashMap();
        map73.put("field", "deposit_deduction");
        map73.put("value", stringDataTran(quotationMap.get("deposit_deduction")));
        mapList.add(map73);

        Map map74 = new HashMap();
        map74.put("field", "deposit_deduction_n");
        map74.put("value", stringDataTran(quotationMap.get("deposit_deduction_n")));
        mapList.add(map74);
        //基准利率值
        Map map75 = new HashMap();
        map75.put("field", "base_rate");
        map75.put("value", doubleDataTran(quotationMap.get("base_rate")));
        mapList.add(map75);



        return mapList;
    }

    @Override
    public List<HlsDurationLn> calculate(IRequest iRequest, Long lnId, JSONObject param) throws Exception {
        List<HlsDurationLn> lnList = new ArrayList<>();
        HlsDurationLn ln = hlsDurationLnMapper.selectByPrimaryKey(lnId);

        String sourceType = ln.getSourceType();

        if ("ET".equals(sourceType)) {
            etUpdateQuotation(iRequest, ln);
        } else if ("PREPAYMENT".equals(sourceType)) {
            prepaymentUpdateQuotation(iRequest, ln);
        } else if ("FINANCIAL_TERMS".equals(sourceType)) {
            changeUpdateQuotation(iRequest, ln);
        }

        return lnList;
    }


    @Override
    public HlsDurationLn createQuotation(IRequest iRequest, Long lnId) {
        //插入prj_quotation 表
        HlsDurationLn ln = hlsDurationLnMapper.selectByPrimaryKey(lnId);
        String sourceType = ln.getSourceType();
        String priceList = "";
        Long quotationId = null;
        if ("ET".equals(sourceType)) {
            priceList = ln.getEtPriceList();
            quotationId = ln.getEtQuotationId();
        } else if ("PREPAYMENT".equals(sourceType)) {
            priceList = ln.getPrepaymentPriceList();
            quotationId = ln.getPrepaymentQuotationId();
        } else if ("FINANCIAL_TERMS".equals(sourceType)) {
            priceList = ln.getChangePriceList();
            quotationId = ln.getChangeQuotationId();
        }

        HlsCusConContract conContract = hlsCusConContractMapper.selectByPrimaryKey(ln.getContractId());
        HlsCusPrjQuotation prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(conContract.getQuotationId());

        if (quotationId == null) {
            prjQuotation.setQuotationId(null);
            prjQuotation.setPriceList(priceList);
            prjQuotation.setSourceDocumentCategory("CON_CONTRACT_CHANGE");
            hlsCusPrjQuotationService.insertSelective(iRequest, prjQuotation);
        } else {
            prjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotationId);
//            prjQuotation.setQuotationId(quotationId);
            prjQuotation.setPriceList(priceList);
            prjQuotation.setSourceDocumentCategory("CON_CONTRACT_CHANGE");
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotation);
        }

        //先删除 再重新插入
        HlsCusPrjQuotationDetails prjQuotationDetails = new HlsCusPrjQuotationDetails();
        prjQuotationDetails.setQuotationId(prjQuotation.getQuotationId());
        prjQuotationDetailsService.batchDelete(prjQuotationDetailsMapper.select(prjQuotationDetails));
        //将空的报价 复制到 prj_quotation_detail表
        HlsCalcConfig config = priceListMapper.selectByPrimaryKey(priceList);
        prjQuotationDetails.setSheets(config.getSheets());
        prjQuotationDetailsService.insertSelective(iRequest, prjQuotationDetails);


        //先删除 再重新插入
        HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
        quotationCashflow.setQuotationId(prjQuotation.getQuotationId());
        prjQuotationCashflowService.batchDelete(prjQuotationCashflowMapper.select(quotationCashflow));

        //将现金流 复制到 prj_quotation_cashflow表
        HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
        contractCashflow.setContractId(conContract.getContractId());
        contractCashflow.setCfStatus("RELEASE");
        List<HlsCusConContractCashflow> cashflowList = cashflowMapper.select(contractCashflow);

        for (HlsCusConContractCashflow cashflow : cashflowList) {
            HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
            BeanRefUtils.beanToBean(cashflow, prjQuotationCashflow, hlsBeanRefUtilService);
            prjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());
            prjQuotationCashflowService.insertSelective(iRequest, prjQuotationCashflow);
        }

        //如果有承兑汇票 需要更新source_cashflow_id
        //修改支付表cf_item=90与91的关联id
        HlsCusPrjQuotationCashflow paynote = new HlsCusPrjQuotationCashflow();
        paynote.setQuotationId(prjQuotation.getQuotationId());
        paynote.setCfItem(90L);
        List<HlsCusPrjQuotationCashflow> paynoteList = prjQuotationCashflowMapper.select(paynote);
        for (HlsCusPrjQuotationCashflow paynoteCashflow : paynoteList) {
            HlsCusPrjQuotationCashflow paynoteFree = new HlsCusPrjQuotationCashflow();
            paynoteFree.setQuotationId(prjQuotation.getQuotationId());
            paynoteFree.setCfItem(91L);
            if (paynoteCashflow.getPaynoteTerm() != null) {
                Date date = paynoteCashflow.getDueDate();//取时间
                LocalDate localDateFrom = DateToLocaleDate(date);
                date = LocalDateToDate(localDateFrom.minusDays(paynoteCashflow.getPaynoteTerm()));
                paynoteFree.setDueDate(date);
            } else {
                paynoteFree.setDueDate(paynoteCashflow.getDueDate());
            }

            List<HlsCusPrjQuotationCashflow> paynoteFreeList = prjQuotationCashflowMapper.select(paynoteFree);
            paynoteFreeList.stream().forEach(item -> {
                item.setSourceCashflowId(paynoteCashflow.getQuotationCashflowId());
                prjQuotationCashflowService.updateByPrimaryKeySelective(iRequest, item);
            });
        }

        //回写quotation_id
        if ("ET".equals(sourceType)) {
            ln.setEtQuotationId(prjQuotation.getQuotationId());
            //总融资额
            if ("TOTAL_FINANCE_AMOUNT".equals(ln.getEtType())) {
                ln.setEtPreAmount(HlsCusMathUtil.mul(conContract.getFinanceAmount(), ln.getEtRatio()));
            }
            //剩余未偿还租金
            else if ("REMAIN_OUTSTANF_RENT".equals(ln.getEtType())) {
                Double totalDueAmount = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount));
                Double totalReceivedAmount = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedAmount));
                ln.setEtPreAmount(HlsCusMathUtil.mul(HlsCusMathUtil.sub(totalDueAmount, totalReceivedAmount), ln.getEtRatio()));
            }
            //剩余未偿还本金
            else if ("REMAIN_OUTSTANF_PRINCIPLE".equals(ln.getEtType())) {
                Double totalPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getPrincipal));
                Double totalReceivedPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedPrincipal));
                ln.setEtPreAmount(HlsCusMathUtil.mul(HlsCusMathUtil.sub(totalPrincipal, totalReceivedPrincipal), ln.getEtRatio()));
            }
            //未到期租赁成本
            else if ("UNEXPIRED_LEASE_COSTS".equals(ln.getEtType())) {
                Double totalPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE") && item.getCalcDate().compareTo(ln.getEtDate()) >= 0).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getPrincipal));
                Double totalReceivedPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE") && item.getCalcDate().compareTo(ln.getEtDate()) >= 0).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedPrincipal));
                ln.setEtPreAmount(HlsCusMathUtil.mul(HlsCusMathUtil.sub(totalPrincipal, totalReceivedPrincipal), ln.getEtRatio()));
            }
            ln.setEtAmount(HlsCusMathUtil.sub(nvl(ln.getEtPreAmount(), 0.0), nvl(ln.getEtExemptAmount(), 0.0), 2));
        } else if ("PREPAYMENT".equals(sourceType)) {
            ln.setPrepaymentQuotationId(prjQuotation.getQuotationId());

            //总融资额
            if ("TOTAL_FINANCE_AMOUNT".equals(ln.getPrepaymentType())) {
                ln.setPrepaymentPreAmount(HlsCusMathUtil.mul(conContract.getFinanceAmount(), ln.getPrepaymentRatio()));
            }
            //剩余未偿还租金
            else if ("REMAIN_OUTSTANF_RENT".equals(ln.getPrepaymentType())) {
                Double totalDueAmount = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount));
                Double totalReceivedAmount = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedAmount));
                ln.setPrepaymentPreAmount(HlsCusMathUtil.mul(HlsCusMathUtil.sub(totalDueAmount, totalReceivedAmount), ln.getPrepaymentRatio()));
            }
            //剩余未偿还本金
            else if ("REMAIN_OUTSTANF_PRINCIPLE".equals(ln.getPrepaymentType())) {
                Double totalPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getPrincipal));
                Double totalReceivedPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE")).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedPrincipal));
                ln.setPrepaymentPreAmount(HlsCusMathUtil.mul(HlsCusMathUtil.sub(totalPrincipal, totalReceivedPrincipal), ln.getPrepaymentRatio()));
                //未到期租赁成本
            } else if ("UNEXPIRED_LEASE_COSTS".equals(ln.getPrepaymentType())) {
                Double totalPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE") && item.getCalcDate().compareTo(ln.getPrepaymentDate()) >= 0).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getPrincipal));
                Double totalReceivedPrincipal = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE") && item.getCalcDate().compareTo(ln.getPrepaymentDate()) >= 0).
                        collect(Collectors.summingDouble(HlsCusConContractCashflow::getReceivedPrincipal));
                ln.setPrepaymentPreAmount(HlsCusMathUtil.mul(HlsCusMathUtil.sub(totalPrincipal, totalReceivedPrincipal), ln.getPrepaymentRatio()));
            }
            //提前偿还的租金
            else if ("PREPAID_RENT".equals(ln.getPrepaymentType())) {
                //提前还款日上一期的现金流
                HlsCusConContractCashflow cashflow = cashflowList.stream().
                        filter(item -> item.getCfItem().equals(1L) && item.getCfStatus().equals("RELEASE") && item.getCalcDate().
                                compareTo(ln.getPrepaymentDate()) <= 0).sorted(Comparator.comparing(HlsCusConContractCashflow::getTimes).reversed()).findFirst().get();
                long betweenDay = DateUtil.betweenDay(ln.getPrepaymentDate(), cashflow.getCalcDate(), true);
                double rental = HlsCusMathUtil.
                        add(HlsCusMathUtil.mul(
                                HlsCusMathUtil.mul(cashflow.getOutstandingPrincipal(), nvl(prjQuotation.getIntRate(), 0.0) / 360), betweenDay, 2), ln.getPrinAtCd());
                ln.setPrepaymentPreAmount(HlsCusMathUtil.mul(rental, ln.getPrepaymentRatio()));
            }
            ln.setPrepaymentAmount(HlsCusMathUtil.sub(nvl(ln.getPrepaymentPreAmount(), 0.0), nvl(ln.getPrepaymentExemptAmount(), 0.0), 2));
        } else if ("FINANCIAL_TERMS".equals(sourceType)) {
            ln.setChangeQuotationId(prjQuotation.getQuotationId());
        }

        hlsDurationLnService.updateByPrimaryKeySelective(iRequest, ln);
        return ln;
    }
}
