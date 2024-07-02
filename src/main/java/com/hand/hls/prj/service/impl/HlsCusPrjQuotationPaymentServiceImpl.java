package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcConfigService;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.HlsCalcSaveService;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.HlsCusXirr;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
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
public class HlsCusPrjQuotationPaymentServiceImpl extends BaseServiceImpl<HlsCusPrjQuotation> implements HlsCusPrjQuotationPaymentService {
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

    private static final String PRJ_SOURCEDOCUMENT_CATEGORY = "PRJ_PROJECT";
    private static final String CON_SOURCEDOCUMENT_CATEGORY = "CON_CONTRACT";

    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;

    @Autowired
    private HlsCusPrjQuotationDetailsMapper hlsCusPrjQuotationDetailsMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsPriceListConfigHdMapper configHdMapper;
    @Autowired
    private HlsPriceListConfigLnMapper configLnMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;
    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;
    @Autowired
    HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;

    @Autowired
    private HlsCusConContractCashflowMapper contractCashflowMapper;
    @Autowired
    private IConContractCashflowService conContractCashflowService;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsCalcSaveService hlsCalcSaveService;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    private static final String BEFORE_RENT_STAGE_TYPE = "租前期";

    private static final Long BEFORE_RENT_CF = 10L;

    @Autowired
    private HlsCalcConfigService hlsCalcConfigService;

    @Override
    public void quotationReCalc(IRequest request, Long quotationId) throws Exception {

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        List<Map> mapList = new ArrayList<>();

        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation = prjQuotationMapper.selectByPrimaryKey(prjQuotation);
        Map quotationMap = (Map) prjQuotationMapper.queryQuotationInfoByQuotationId(prjQuotation).get(0);

        //传入计算字段

        //首次放款日期
        Map map18 = new HashMap();
        map18.put("field", "first_release_date");
        map18.put("value", df.format(quotationMap.get("first_release_date")));
        mapList.add(map18);

        //起租日期
        Map map21 = new HashMap();
        map21.put("field", "lease_start_date");
        map21.put("value", df.format(quotationMap.get("lease_start_date")));
        mapList.add(map21);


        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(quotationMap.get("price_list").toString());
        hlsCalcConfig = priceListMapper.selectByPrimaryKey(hlsCalcConfig);

        hlsCusPrjQuotationService.updateQuotaion(prjQuotation, mapList, hlsCalcConfig,true);

        hlsCusPrjQuotationService.updateXirr(request, prjQuotation);
    }

    /*
     * 更新对应的quotation表记录的信息
     * */
    @Override
    public void updateQuotaion(HlsCusPrjQuotation quotation, List<Map> maps, HlsCalcConfig hlsCalcConfig) throws Exception {

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
        if("CON_CONTRACT".equals(quotation.getSourceDocumentCategory())) {
            hlsCusPrjQuotation.setContractId(quotation.getSourceDocumentId());
        }
        hlsCusPrjQuotation.setSourceDocumentId(quotation.getSourceDocumentId());

   /*     HlsCalcConfig calcConfig = new HlsCalcConfig();
        calcConfig.setPriceList("TEST");
        calcConfig.setSheets(compressSheets);
        priceListMapper.updateByPrimaryKeySelective(calcConfig);*/

        hlsCalcSaveService.savePrjQuotation(RequestHelper.getCurrentRequest(true), hlsCusPrjQuotation);

      /*  if(CON_SOURCEDOCUMENT_CATEGORY.equalsIgnoreCase(hlsCusPrjQuotation.getSourceDocumentCategory() )){
            ConQuotationSubmit( RequestHelper.getCurrentRequest(true) , hlsCusPrjQuotation);
        }else if(PRJ_SOURCEDOCUMENT_CATEGORY.equalsIgnoreCase(hlsCusPrjQuotation.getSourceDocumentCategory())){
            PrjQuotationSubmit( RequestHelper.getCurrentRequest(true) , hlsCusPrjQuotation );
        }*/

    }


    public HlsCusPrjQuotation ConQuotationSubmit(IRequest iRequest, HlsCusPrjQuotation prjQuotation) throws Exception {
        //校验公式是否被修改
//        hlsCusCalcExcelImportUtilService.excelFormatHasChange(iRequest,prjQuotation.getPriceList(),prjQuotation.getSheets());
        String jsonStr = JSON.toJSONString(hlsCusCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, prjQuotation.getSheets(), prjQuotation.getPriceList(), "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);
        if (prjQuotationDto.getLeaseTimes() == null) {
            throw new IllegalArgumentException("期数未取到！");
        }
        prjQuotationDto.setPriceList(prjQuotation.getPriceList());
        prjQuotationDto.setSheets(prjQuotation.getSheets());
        prjQuotationDto.setCompressSheets(prjQuotation.getCompressSheets());
        prjQuotationDto.setSourceDocumentCategory(prjQuotation.getSourceDocumentCategory());
        prjQuotationDto.setSourceDocumentId(prjQuotation.getSourceDocumentId());
        prjQuotationDto.setQuotationId(prjQuotation.getQuotationId());
        prjQuotationDto.setDataClass("VIRTUAL_CON");


        //当前项目下的只有一个报价
        List<HlsCusPrjQuotation> prjQuotationList = prjQuotationMapper.prjQuotationDetailQuery(prjQuotationDto);
        if (prjQuotationList.size() == 1) {
            prjQuotationDto.setQuotationId(prjQuotationList.get(0).getQuotationId());
            prjQuotationDto.setSourceDocumentId(prjQuotationList.get(0).getSourceDocumentId());
        } else if (prjQuotationList.size() > 0 ) {
            throw new IllegalArgumentException("当前单据下存在多条报价信息，请清除多余数据！");
        }
        prjQuotationDto.setStatus("NEW");

        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest,prjQuotationDto);

        String  interestAmortizationMethod  = prjQuotationDto.getInterestAmortizationMethod();

        HlsCusPrjQuotationDetails prjQuotationDetails = new HlsCusPrjQuotationDetails();
        prjQuotationDetails.setQuotationId(prjQuotationDto.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = new ArrayList<>();
        detailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(prjQuotationDetails);
        if (detailsList.size() > 0) {
            detailsList.get(0).setSheets(prjQuotationDto.getCompressSheets());
            detailsList.get(0).set__status("update");
        } else {
            prjQuotationDetails.setSheets(prjQuotationDto.getCompressSheets());
            detailsList.add(prjQuotationDetails);
            detailsList.get(0).set__status("insert");
        }
        hlsCusPrjQuotationDetailsService.batchUpdate(iRequest, detailsList);

        //保存报价现金流表
        hlsCusPrjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);
       // saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);

        //先删除原来报价对应的现金流--加判断 ，若已经关联核销表，则不删除
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(prjQuotationDto.getSourceDocumentId());
        cashflow.setGeneratedSource("PRJ_QUOTATION");
        cashflow.setGeneratedSourceDocId(prjQuotationDto.getQuotationId());
        List<HlsCusConContractCashflow> cashflowList = contractCashflowMapper.select(cashflow);
        //判断是否被核销
        for(HlsCusConContractCashflow flow : cashflowList){
            String writeOffFlag =  contractCashflowMapper.queryCashflowByFlag(flow);
            if("NOT".equalsIgnoreCase(writeOffFlag)){
                contractCashflowMapper.deleteByCashflowId(flow);
            }

        }
//        conContractCashflowService.batchDelete(cashflowList);

        //将报价现金流更新合同现金流
        saveCashflowFromQuotationCashflow(iRequest, prjQuotationDto.getSourceDocumentId(), prjQuotationDto.getQuotationId(),interestAmortizationMethod);

        //更新合同表
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(prjQuotationDto.getSourceDocumentId());
        contract = hlsCusConContractMapper.selectByPrimaryKey(contract);

        contract.setTaxStructure(prjQuotationDto.getTaxStructure());
        contract.setTaxTypeCode(prjQuotationDto.getTaxTypeCode());
        contract.setLeaseItemAmount(prjQuotationDto.getLeaseItemAmount());
        contract.setFinanceAmount(prjQuotationDto.getFinanceAmount());
        hlsCusConContractMapper.updateByPrimaryKeySelective(contract);

        return prjQuotationDto;
    }

    public HlsCusPrjQuotation PrjQuotationSubmit(IRequest iRequest, HlsCusPrjQuotation prjQuotation) throws Exception {
        //校验公式是否被修改
//        hlsCusCalcExcelImportUtilService.excelFormatHasChange(iRequest,prjQuotation.getPriceList(),prjQuotation.getSheets());
        String jsonStr = JSON.toJSONString(hlsCusCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, prjQuotation.getSheets(), prjQuotation.getPriceList(), "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);
        if (prjQuotationDto.getLeaseTimes() == null) {
            throw new IllegalArgumentException("期数未取到！");
        }
        prjQuotationDto.setPriceList(prjQuotation.getPriceList());


        prjQuotationDto.setSheets(prjQuotation.getSheets());
        prjQuotationDto.setCompressSheets(prjQuotation.getCompressSheets());
        prjQuotationDto.setSourceDocumentCategory(prjQuotation.getSourceDocumentCategory());
        prjQuotationDto.setSourceDocumentId(prjQuotation.getSourceDocumentId());
        prjQuotationDto.setQuotationId(prjQuotation.getQuotationId());
        prjQuotationDto.setDataClass("PRJ_PROJECT_INVEST");


        //当前项目下的只有一个报价
        List<HlsCusPrjQuotation> prjQuotationList = prjQuotationMapper.prjQuotationDetailQuery(prjQuotationDto);
        if (prjQuotationList.size() == 1) {
            prjQuotationDto.setQuotationId(prjQuotationList.get(0).getQuotationId());
            prjQuotationDto.setSourceDocumentId(prjQuotationList.get(0).getSourceDocumentId());
        } else if (prjQuotationList.size() > 0 ) {
            throw new IllegalArgumentException("当前单据下存在多条报价信息，请清除多余数据！");
        }
        prjQuotationDto.setStatus("NEW");


        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest,prjQuotationDto);

        //反写项目表字段lease_item_amount
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(prjQuotationDto.getSourceDocumentId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);

        hlsCusPrjProject.setLeaseItemAmount(prjQuotationDto.getLeaseItemAmount());
        hlsCusPrjProject.setTaxStructure(prjQuotationDto.getTaxStructure());
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest,hlsCusPrjProject);

        HlsCusPrjQuotationDetails prjQuotationDetails = new HlsCusPrjQuotationDetails();
        prjQuotationDetails.setQuotationId(prjQuotationDto.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = new ArrayList<>();
        detailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(prjQuotationDetails);
        if (detailsList.size() > 0) {
            detailsList.get(0).setSheets(prjQuotationDto.getCompressSheets());
            detailsList.get(0).set__status("update");
        } else {
            prjQuotationDetails.setSheets(prjQuotationDto.getCompressSheets());
            detailsList.add(prjQuotationDetails);
            detailsList.get(0).set__status("insert");
        }
        hlsCusPrjQuotationDetailsService.batchUpdate(iRequest, detailsList);
        //保存现金流表
//        hlsCusPrjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);
        saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);
        return prjQuotationDto;
    }

    @Override
    public void updateXirr(IRequest request, HlsCusPrjQuotation prjQuotation) throws IllegalArgumentException {
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());

        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList1 = hlsCusPrjQuotationCashflowService.select(request, hlsCusPrjQuotationCashflow, 1, 9999999);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.selectIrrQuotation(hlsCusPrjQuotationCashflow);
        if (prjQuotationCashflowList.size() == 0) {
            return;
        }
        Double deposit = prjQuotation.getDeposit() == null ? 0D : prjQuotation.getDeposit();
        Double residualValue = prjQuotation.getResidualValue() == null ? 0D : prjQuotation.getResidualValue();
        if (deposit.compareTo(0D) == 1) {
            if (HlsCusConstant.PRJ_MARGIN_PAYMENT_METHOD.PERIOD_FINAL_DEDUCTIBLE.equalsIgnoreCase(prjQuotation.getDepositReturnMethod())) {
                prjQuotationCashflowList = marginDeductionIrr(prjQuotationCashflowList, residualValue);
            }
        }
        double[] dueAmountList = new double[prjQuotationCashflowList.size()];
        double[] netDueAmountList = new double[prjQuotationCashflowList.size()];

        Date[] dueAmountDateList = new Date[prjQuotationCashflowList.size()];
        Date[] netDueAmountDateList = new Date[prjQuotationCashflowList.size()];

        for (int i = 0; i < prjQuotationCashflowList.size(); i++) {
            if ("OUTFLOW".equalsIgnoreCase(prjQuotationCashflowList.get(i).getCfDirection())) {
                dueAmountList[i] = -prjQuotationCashflowList.get(i).getDueAmount();
                dueAmountDateList[i] = prjQuotationCashflowList.get(i).getDueDate();

                netDueAmountList[i] = -prjQuotationCashflowList.get(i).getDueAmount();
                netDueAmountDateList[i] = prjQuotationCashflowList.get(i).getDueDate();
            } else {
                dueAmountList[i] = prjQuotationCashflowList.get(i).getDueAmount();
                dueAmountDateList[i] = prjQuotationCashflowList.get(i).getDueDate();

                netDueAmountList[i] = prjQuotationCashflowList.get(i).getNetDueAmount();
                netDueAmountDateList[i] = prjQuotationCashflowList.get(i).getDueDate();
            }
        }
        //名义irr(复利参考值)
        Double referenceValueIrr = hlsCusPrjQuotationCashflowService.Newtons_method(0.1, dueAmountList, dueAmountDateList);

        if ("NAN".equals(referenceValueIrr)) {
            throw new IllegalArgumentException("名义irr(复利参考值)计算失败，请联系管理员");
        }

        //含税xirr
        Double xirr = HlsCusXirr.Newtons_method(0.1, dueAmountList, dueAmountDateList);

        //不含税xirr
        Double netXirr = HlsCusXirr.Newtons_method(0.1, netDueAmountList, netDueAmountDateList);

        //名义irr
        Double irr = (double) Math.round((Math.pow(referenceValueIrr + 1.0, 1.0 / 365.0) - 1.0) * 365.0 * Math.pow(10, 6)) / Math.pow(10, 6);
        //更新名义irr和xirr
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(prjQuotation.getQuotationId());
        hlsCusPrjQuotation = self().selectByPrimaryKey(request, hlsCusPrjQuotation);
        hlsCusPrjQuotation.setIrr(irr);
        if(!Double.isNaN(xirr) && !Double.isInfinite(xirr)) {
            hlsCusPrjQuotation.setXirr(xirr);
        }
        if(!Double.isNaN(netXirr) && !Double.isInfinite(netXirr)) {
            hlsCusPrjQuotation.setXirrNet(netXirr);
        }
        self().updateByPrimaryKeySelective(request, hlsCusPrjQuotation);
    }



    void saveCashflowFromQuotationCashflow(IRequest iRequest, Long contractId, Long quotationId,String interestAmortizationMethod) {
        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = hlsCusConContractMapper.selectByPrimaryKey(c);
        List<HlsCusConContractCashflow> conContractCashflowList = new ArrayList<>();
        HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflowParameter.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
        if (prjQuotationCashflowList.size() > 0) {
            c.setFirstPayDate(prjQuotationCashflowList.get(0).getDueDate());
            c.setLeaseEndDate(prjQuotationCashflowList.get(0).getDueDate());
        }
        for (int i = 0; i < prjQuotationCashflowList.size(); i++) {
            HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
            if (prjQuotationCashflowList.get(i).getDueDate() != null) {
                //新加判断 报价现金流的dueDate可能为空
                if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getFirstPayDate().getTime()) < 0) {
                    c.setFirstPayDate(prjQuotationCashflowList.get(i).getDueDate());
                }
                if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getLeaseEndDate().getTime()) > 0) {
                    c.setLeaseEndDate(prjQuotationCashflowList.get(i).getDueDate());
                }
            }

            BeanRefUtils.beanToBean(prjQuotationCashflowList.get(i), conContractCashflow, hlsBeanRefUtilService);

            // prjQuotationCashflow times double -> conContractCashflow times long
            conContractCashflow.setTimes(prjQuotationCashflowList.get(i).getTimes().longValue());
            conContractCashflow.setContractId(contractId);
            conContractCashflow.setCfStatus("RELEASE");
            conContractCashflow.setWriteOffFlag("NOT");
            conContractCashflow.setBillingStatus("NOT");
            conContractCashflow.setOverdueStatus("N");
            conContractCashflow.setPenaltyProcessStatus("N");
            conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
            conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
            conContractCashflow.setGeneratedSourceDocId(quotationId);
            conContractCashflow.setGeneratedSourceDocLineId(prjQuotationCashflowList.get(i).getQuotationCashflowId());

            if(conContractCashflow.getCfItem() == 1L){
                conContractCashflow.setAmortizationMethod(interestAmortizationMethod);
            }

            //根据  due_amount ,contract_id , cf_item ,times ,判断 已经存在的则不插入，进行更新
            List<HlsCusConContractCashflow> contractCashflows =  contractCashflowMapper.queryOldCashflow(conContractCashflow);
            if(contractCashflows.size() > 0){
                for(HlsCusConContractCashflow cash : contractCashflows ){
                    conContractCashflow.setCashflowId(cash.getCashflowId());
                    conContractCashflow.set__status("update");
                }

            }else{
                conContractCashflow.set__status("insert");
            }
//            conContractCashflowService.insertSelective(iRequest, conContractCashflow);
            conContractCashflowList.add(conContractCashflow);
        }

        conContractCashflowService.batchUpdate(iRequest, conContractCashflowList);

        hlsCusConContractMapper.updateByPrimaryKey(c);
    }

    public List<HlsCusPrjQuotationCashflow> saveCalc2PrjQuotationCashflow(IRequest requestContext, HlsCusPrjQuotation prjQuotation) throws Exception {
        String modles = "prj";
        if (prjQuotation.getSourceDocumentCategory() != null && prjQuotation.getSourceDocumentCategory().equals("CON_CONTRACT")) {
            modles = "cont";
        }

        //读取行配置的现金流
        List<Object> ObjectList = hlsCusCalcExcelImportUtilService.getExcelToCalcLnTable(requestContext, prjQuotation.getSheets(), prjQuotation.getPriceList(), modles, Integer.parseInt(prjQuotation.getLeaseTimes().toString()), prjQuotation.getSourceDocumentCategory());


        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = new ArrayList<>();

        for (int i = 0; i < ObjectList.size(); i++) {
            String s = JSON.toJSONString(ObjectList.get(i));
            HlsCusPrjQuotationCashflow prjQuotationCashflow = JSON.parseObject(s, HlsCusPrjQuotationCashflow.class);
            prjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());
            prjQuotationCashflow.setDueDate(prjQuotationCashflow.getDueDate());
            prjQuotationCashflow.setCalcDate(prjQuotationCashflow.getCalcDate());
            prjQuotationCashflow.setFinIncomeDate(prjQuotationCashflow.getCalcDate());

            //设置租前期现金的cftype，cfitem
            if(BEFORE_RENT_STAGE_TYPE.equals(prjQuotationCashflow.getStageType())){
                prjQuotationCashflow.setCfType(BEFORE_RENT_CF);
                prjQuotationCashflow.setCfItem(BEFORE_RENT_CF);
            }

            //新增
            prjQuotationCashflow.set__status(DTOStatus.ADD);

            //头配置现金流特殊处理（设备款）
            if(prjQuotationCashflow.getCfStatus() != null && "HEAD_CASH_FLOW".equals(prjQuotationCashflow.getCfStatus())){
                prjQuotationCashflow.setDueDate(prjQuotation.getFirstReleaseDate());
                prjQuotationCashflow.setTimes(0L);
                prjQuotationCashflow.setFinIncomeDate(prjQuotation.getFirstReleaseDate());
                prjQuotationCashflow.setOutstandingPrincipal(prjQuotation.getLeaseItemAmount());
                prjQuotationCashflow.setCalcDate(prjQuotation.getFirstReleaseDate());


                //直租计算税额
                if("LEASE".equals(prjQuotation.getBusinessType())) {
                    if (prjQuotation.getVatRate() == null) {
                        throw new HlsCusException("税率不能为空!");
                    }
                    Double netDueAmount = CalculateUtil.div(prjQuotation.getLeaseItemAmount(), (CalculateUtil.add(1D, prjQuotation.getVatRate())), 2);
                    Double vatDueAmount = CalculateUtil.sub(prjQuotation.getLeaseItemAmount(), netDueAmount);
                    prjQuotationCashflow.setNetDueAmount(netDueAmount);
                    prjQuotationCashflow.setVatDueAmount(vatDueAmount);
                }
            }
            //下达
            prjQuotationCashflow.setCfStatus("RELEASE");
            prjQuotationCashflowList.add(prjQuotationCashflow);
        }



        //删除这个quotation项下现金流(只删除在价目表配置中存在的现金流)
        prjQuotationCashflowMapper.deleteRentByQuotationId(prjQuotation);
        for (HlsCusPrjQuotationCashflow dt : prjQuotationCashflowList) {
            dt.set__status(DTOStatus.ADD);
            dt.setQuotationId(prjQuotation.getQuotationId());
        }
        hlsCusPrjQuotationCashflowService.batchUpdate(requestContext, prjQuotationCashflowList);


        //单次放款设备款单独处理
        autoCreateLeaseItemAmount(requestContext,prjQuotation);

        if(prjQuotation.getHolidayAdjust() != null && "Y".equals(prjQuotation.getHolidayAdjust())) {
            cashSkipWorkday(requestContext, prjQuotationCashflowList);
        }

        //插入留购金
        insertResidualValue(requestContext,prjQuotation);

        return prjQuotationCashflowList;
    }

    void autoCreateLeaseItemAmount(IRequest iRequest,HlsCusPrjQuotation quotation) throws com.hand.hls.exception.HlsCusException {

        if(quotation.getQuotationId() == null){
            throw new HlsCusException("参数获取失败,请联系管理员!");
        }

        quotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest,quotation);

        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(quotation.getPriceList());
        hlsCalcConfig = hlsCalcConfigService.selectByPrimaryKey(iRequest,hlsCalcConfig);

        String paymentType = hlsCalcConfig.getPaymentType();

        if(!org.springframework.util.StringUtils.isEmpty(paymentType)){

            //单次放款才插入或者更新设备款
            if("SINGE_PAYMENT".equals(paymentType)){

                //查询设备款是否存在
                HlsCusPrjQuotationCashflow quotationCashflow = new HlsCusPrjQuotationCashflow();
                quotationCashflow.setQuotationId(quotation.getQuotationId());
                quotationCashflow.setCfType(0L);
                quotationCashflow.setCfItem(0L);
                List<HlsCusPrjQuotationCashflow> cashflowList = prjQuotationCashflowMapper.select(quotationCashflow);

                if(org.apache.commons.collections4.CollectionUtils.isEmpty(cashflowList) || cashflowList.size() == 0){

                    //插入一条设备款
                    quotationCashflow.setDueDate(quotation.getFirstReleaseDate());
                    quotationCashflow.setTimes(0L);
                    quotationCashflow.setFinIncomeDate(quotation.getFirstReleaseDate());
                    quotationCashflow.setCalcDate(quotation.getFirstReleaseDate());
                    quotationCashflow.setCfStatus("RELEASE");
                    //直租计算税额
                    if("LEASE".equals(quotation.getBusinessType())) {
                        if (quotation.getVatRate() == null) {
                            throw new HlsCusException("税率不能为空!");
                        }
                        Double netDueAmount = CalculateUtil.div(quotation.getLeaseItemAmount(), (CalculateUtil.add(1D, quotation.getVatRate())), 2);
                        Double vatDueAmount = CalculateUtil.sub(quotation.getLeaseItemAmount(), netDueAmount);
                        quotationCashflow.setNetDueAmount(netDueAmount);
                        quotationCashflow.setVatDueAmount(vatDueAmount);
                    }else {
                        quotationCashflow.setDueAmount(quotation.getLeaseItemAmount());
                        quotationCashflow.setNetDueAmount(quotation.getLeaseItemAmount());
                    }
                    quotationCashflow.setOutstandingPrincipal(quotation.getLeaseItemAmount());
                    hlsCusPrjQuotationCashflowService.insert(iRequest,quotationCashflow);
                }else{

                    //更新设备款
                    quotationCashflow = cashflowList.get(0);
                    quotationCashflow.setDueDate(quotation.getFirstReleaseDate());
                    quotationCashflow.setTimes(0L);
                    if("LEASE".equals(quotation.getBusinessType())) {
                        if (quotation.getVatRate() == null) {
                            throw new HlsCusException("税率不能为空!");
                        }
                        Double netDueAmount = CalculateUtil.div(quotation.getLeaseItemAmount(), (CalculateUtil.add(1D, quotation.getVatRate())), 2);
                        Double vatDueAmount = CalculateUtil.sub(quotation.getLeaseItemAmount(), netDueAmount);
                        quotationCashflow.setNetDueAmount(netDueAmount);
                        quotationCashflow.setVatDueAmount(vatDueAmount);
                    }else {
                        quotationCashflow.setDueAmount(quotation.getLeaseItemAmount());
                        quotationCashflow.setNetDueAmount(quotation.getLeaseItemAmount());
                    }
                    quotationCashflow.setOutstandingPrincipal(quotation.getLeaseItemAmount());
                    hlsCusPrjQuotationCashflowService.updateByPrimaryKeySelective(iRequest,quotationCashflow);

                }

            }
        }

    }


    void insertResidualValue(IRequest iRequest,HlsCusPrjQuotation prjQuotation) throws HlsCusException {

        HlsCusPrjQuotationCashflow cashflow = new HlsCusPrjQuotationCashflow();

        prjQuotation = hlsCusPrjQuotationService.selectByPrimaryKey(iRequest,prjQuotation);

        //先删除留购金
        prjQuotationCashflowMapper.deleteResidualCashflow(prjQuotation);

        //获取税率
        Double vatRate = prjQuotation.getVatRate();

        //获取留购金
        Double residualValue = prjQuotation.getResidualValue();
        cashflow.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflow = prjQuotationCashflowMapper.selectMaxDueDateByQuotationId(cashflow);

        if(prjQuotationCashflow == null){
            throw new HlsCusException("租金生成失败，请检查数据!");
        }

        //计算税额
        if (vatRate != null && residualValue != null) {
            Double netDueAmount = CalculateUtil.div(residualValue, (CalculateUtil.add(1D, vatRate)), 2);
            Double vatDueAmount = CalculateUtil.sub(residualValue, netDueAmount);
            cashflow.setTimes(prjQuotation.getLeaseTimes());
            cashflow.setDueAmount(residualValue);
            if(prjQuotationCashflow != null) {
                cashflow.setDueDate(prjQuotationCashflow.get(0).getDueDate());
            }
            cashflow.setDueDate(prjQuotationCashflow.get(0).getDueDate());
            cashflow.setCfItem(8L);
            cashflow.setCfType(8L);
            cashflow.setNetDueAmount(netDueAmount);
            cashflow.setVatDueAmount(vatDueAmount);
            cashflow.setTaxTypeRate(vatRate);
            hlsCusPrjQuotationCashflowService.insert(iRequest,cashflow);
        }

    }

    public void cashSkipWorkday(IRequest requestContext, List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList) {
        //跳过工作日
        for (HlsCusPrjQuotationCashflow cashflow : hlsCusPrjQuotationCashflowList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cashflow.getDueDate());
            if (cashflow.getTimes() != 0L) {
                //如果支付日期为周六，那么让当前日期-1天
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    cashflow.setDueDate(calendar.getTime());
                }
                //如果是周日，那么让当前日期-2天
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, -2);
                    cashflow.setDueDate(calendar.getTime());
                }
                //更新到数据库中
                hlsCusPrjQuotationCashflowService.updateByPrimaryKeySelective(requestContext, cashflow);
            }
        }
    }

    /**
     * Description:
     *
     * @param input
     * @return
     * @see
     */
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

    private List<HlsPriceListConfigLn> getPriceListConfigLns(String priceList, String type){
        HlsPriceListConfigLn ln = new HlsPriceListConfigLn();

        ln.setTableType(type);
        ln.setPriceList(priceList);
        List<HlsPriceListConfigLn> listConfigLns = configLnMapper.selectHlsPriceListConfiglineByPriceList(ln);
        return listConfigLns;
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


    //获取还款频率
    public Long getRentingFrequency(String rentingFrequency) {
        Long resultNumber = 0L;
        if ("YEAR".equals(rentingFrequency)) {
            resultNumber = 12L;
        } else if ("HALF_A_YEAR".equals(rentingFrequency)) {
            resultNumber = 6L;
        } else if ("QUARTER".equals(rentingFrequency)) {
            resultNumber = 3L;
        } else if ("MONTH".equals(rentingFrequency)) {
            resultNumber = 1L;
        } else if ("DOUBLE_MONTH".equals(rentingFrequency)) {
            resultNumber = 2L;
        }
        return resultNumber;
    }

    public Boolean createNewPrjQuotation(IRequest iRequest,HlsCusPrjQuotation hlsCusPrjQuotation,Long projectId){
        //插入prj_quotation
        Long quotationId = hlsCusPrjQuotation.getQuotationId();
        HlsCusPrjQuotation newQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
        newQuotation.setQuotationId(null);
        newQuotation.setSourceDocumentId(projectId);
        newQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        newQuotation.setDataClass("PRJ_PROJECT_INVEST");
        newQuotation.setSourceQuotationId(quotationId);
        newQuotation.setCalcName(hlsCusPrjQuotation.getCalcName());
        newQuotation.setOtherPrice(hlsCusPrjQuotation.getOtherPrice());
        newQuotation.setSubjectMatterIntrodution(hlsCusPrjQuotation.getSubjectMatterIntrodution());
        prjQuotationMapper.insertSelective(newQuotation);
        //查询QuotationDetails并插入
        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
        hlsCusPrjQuotationDetails.setQuotationId(quotationId);
        List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
        for(HlsCusPrjQuotationDetails dt:hlsCusPrjQuotationDetailsList){
            //更改QuotationId()
            dt.setQuotationId(newQuotation.getQuotationId());
            hlsCusPrjQuotationDetailsService.insertSelective(iRequest,dt);
        }
        //查询出现金流并插入
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowService.queryQuotationCashFlowById(hlsCusPrjQuotation);
        for(HlsCusPrjQuotationCashflow dt:hlsCusPrjQuotationCashflowList){
            //更改QuotationId()
            dt.setQuotationId(newQuotation.getQuotationId());
            hlsCusPrjQuotationCashflowService.insertSelective(iRequest,dt);
        }
        return true;
    }



    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    public String getQuatationContractSerialNumber(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) {
        String num = hlsCusPrjProjectMapper.getQuatationContractMaxNumber(hlsCusPrjProject);
        if (StringUtils.isBlank(num)) {
            return "01";
        } else {
            int var = Integer.parseInt(num) + 1;
            num = ("" + (var + 100)).substring(("" + (var + 100)).length() - 2);
            return num;
        }
    }

    private List marginDeductionIrr(List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList, Double residualValue) {
        Double lastTimeSumAmount = prjQuotationCashflowList.get(prjQuotationCashflowList.size() - 1).getDueAmount();
        if (lastTimeSumAmount < residualValue) {
            prjQuotationCashflowList.get(prjQuotationCashflowList.size() - 1).setDueAmount(0D);
            //进行保证金抵扣
            for (int i = prjQuotationCashflowList.size() - 2; i >= 0; i--) {
                lastTimeSumAmount = lastTimeSumAmount + prjQuotationCashflowList.get(i).getDueAmount();
                if (lastTimeSumAmount < residualValue) {
                    prjQuotationCashflowList.get(i).setDueAmount(0D);
                } else {
                    prjQuotationCashflowList.get(i).setDueAmount(lastTimeSumAmount);
                    break;
                }
            }
        }
        return prjQuotationCashflowList;
    }


}
