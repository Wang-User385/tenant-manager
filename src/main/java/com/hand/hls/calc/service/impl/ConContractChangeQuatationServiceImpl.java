package com.hand.hls.calc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.QuotationCommon;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.dto.*;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractChangeReqMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.YhFactoryDelayTermMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.service.impl.ConContractServiceImpl;
import com.hand.hls.cont.utils.PMTUtil;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.dto.FndSysCodes;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.fnd.mapper.FndSysCodesMapper;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.dto.PrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.mapper.PrjQuotationMapper;
import com.hand.hls.prj.service.IPrjQuotationCashflowService;
import com.hand.hls.prj.service.IPrjQuotationDetailsService;
import com.hand.hls.prj.service.IPrjQuotationService;
import com.hand.hls.sys.dto.SysDocumentHistory;
import com.hand.hls.sys.dto.SysDocumentHistoryBlob;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryBlobMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.utils.DateCalculate;
import com.hand.hls.utils.DateUtils;
import com.hand.hls.utils.JsonUtils;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.vat.dto.HlsInvoiceProfileDtl;
import com.hand.hls.vat.mapper.HlsInvoiceProfileDtlMapper;
import hls.core.sys.mapper.SysCodeValueMapper;
import jodd.util.ArraysUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.formula.functions.FinanceLib;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.rmi.NoSuchObjectException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConContractChangeQuatationServiceImpl implements QuotationCommon {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;
    @Autowired
    private IPrjQuotationCashflowService prjQuotationCashflowService;

    @Autowired
    private IPrjQuotationDetailsService prjQuotationDetailsService;

    @Autowired
    private IConContractCashflowService conContractCashflowService;

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
    private PrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private HlsCusConContractCashflowMapper contractCashflowMapper;

    @Autowired
    private SysDocumentHistoryDetailMapper sysDocumentHistoryDetailMapper;

    @Autowired
    private SysCodeValueMapper sysCodeValueMapper;
    @Autowired
    private HlsCfItemMapper cfItemMapper;
    @Autowired
    private HlsPriceListConfigHdMapper hlsPriceListConfigHdMapper;

    @Autowired
    private HlsPriceListConfigLnMapper hlsPriceListConfigLnMapper;
    @Autowired
    private IPrjQuotationService prjQuotationService;
    @Autowired
    private HlsCusConContractChangeReqMapper contractChangeReqMapper;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private YhFactoryDelayTermMapper yhFactoryDelayTermMapper;
    @Autowired
    private HlsInvoiceProfileDtlMapper invoiceProfileDtlMapper;
    @Autowired
    private FndSysCodesMapper taxTypeCodesMapper;

    //保证金抵扣租金
    private static final String DEPOSIT_PERIOD_FINAL_DEDUCTIBLE = "PERIOD_FINAL_DEDUCTIBLE";
    //保证金退还
    private static final String DEPOSIT_PERIOD_FINAL_RETURN = "PERIOD_FINAL_RETURN";


    private static final String sourceDocumentCategory = "CONTRACT_CHANGE";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CELLS = "cells";
    private static final String KEY_ROWS = "rows";
    private static final String KEY_VALUE = "value";
    private static final String KEY_FORMULA = "formula";
    private static final String KEY_FIELD = "field";
    public static final String PRICE_TYPE_SINGLE = "SINGLE";
    public static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    private static final String KEY_FORMAT = "format";
    private static final String[] DOUBLE_FORMATS = {"#,0", "0%", "0.00", "#,##0.00", "#,0.00", "0.00%"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy", "yyyy/m/d"};
    public static final String DOCUMENT_CATEGORY_CONTRACT_CHANGE = "CONTRACT_CHANGE";
    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

    @Override
    public String getSourceDocumentCategory() {
        return sourceDocumentCategory;
    }

    public static String encodeURIComponent(String input) {
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
    private static String getHex(byte buf[]){
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

    public HlsCusPrjQuotation PrjQuotationSubmit(IRequest iRequest, HlsCusPrjQuotation HlsCusPrjQuotation, boolean isDocumentHistory) throws Exception {
        if (!isDocumentHistory) {
            return PrjQuotationSubmit(iRequest, HlsCusPrjQuotation);
        } else {
            //校验公式是否被修改
            String jsonStr = JSON.toJSONString(hlsCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, HlsCusPrjQuotation.getSheets(), HlsCusPrjQuotation.getPriceList(), "prj"));
            HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);
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
            //prjQuotationDto.setQuotationCoding(HlsCusPrjQuotation.getQuotationCoding());
            prjQuotationDto.setDescription(HlsCusPrjQuotation.getDescription());

            prjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

            //删除原来的合同id
            HlsCusConContractCashflow contractCashflow = new HlsCusConContractCashflow();
            contractCashflow.setGeneratedSource(ConContractCashflow.SOURCE_PRJ_QUOTATION);
            contractCashflow.setGeneratedSourceDocId(HlsCusPrjQuotation.getQuotationId());
            List<HlsCusConContractCashflow> contractCashflows = contractCashflowMapper.select(contractCashflow);
            for (HlsCusConContractCashflow cashflow : contractCashflows) {
                contractCashflowMapper.deleteByPrimaryKey(cashflow);
            }
//            contractCashflows.forEach(item -> contractCashflowMapper.deleteByPrimaryKey(item.getCashflowId()));


            List<Object> objectList = hlsCalcExcelImportUtilService.getExcelToCalcLnTable(iRequest, prjQuotationDto.getSheets(), prjQuotationDto.getPriceList(), "CONT", Integer.parseInt(prjQuotationDto.getLeaseTimes().toString()), prjQuotationDto.getSourceDocumentCategory());
            for (Object o : objectList) {
                HlsCusConContractCashflow cashflow = JSON.parseObject(JSON.toJSONString(o), HlsCusConContractCashflow.class);
                cashflow.setGeneratedSource(ConContractCashflow.SOURCE_PRJ_QUOTATION);
                cashflow.setGeneratedSourceDocId(prjQuotationDto.getQuotationId());
                cashflow.setContractId(prjQuotationDto.getSourceDocumentId());
                cashflow.setWriteOffFlag("NOT");
                cashflow.setBillingStatus("NOT");
                cashflow.setOverdueStatus("N");
                cashflow.setCfStatus("RELEASE");
                cashflow.setPenaltyProcessStatus("N");
                cashflow.setCalcDate(cashflow.getDueDate());
                conContractCashflowService.insertSelective(iRequest, cashflow);
            }

            return prjQuotationDto;

        }
    }

    @Override
    public HlsCusPrjQuotation PrjQuotationSubmit(IRequest iRequest, HlsCusPrjQuotation HlsCusPrjQuotation) throws Exception {
        String jsonStr = JSON.toJSONString(hlsCalcExcelImportUtilService.getExcelToCalcHdTable(iRequest, HlsCusPrjQuotation.getSheets(), HlsCusPrjQuotation.getPriceList(), "prj"));
        HlsCusPrjQuotation prjQuotationDto = JSON.parseObject(jsonStr, HlsCusPrjQuotation.class);
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
        //prjQuotationDto.setQuotationCoding(HlsCusPrjQuotation.getQuotationCoding());
        prjQuotationDto.setDescription(HlsCusPrjQuotation.getDescription());

        prjQuotationService.updateByPrimaryKeySelective(iRequest, prjQuotationDto);

        //获取项目项下的报价
        HlsCusPrjQuotation savePrjQuotation = new HlsCusPrjQuotation();
        savePrjQuotation.setQuotationId(prjQuotationDto.getQuotationId());
        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
        hlsCusPrjQuotationDetails.setQuotationId(savePrjQuotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList;
        detailsList = prjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
        if (detailsList.size() > 0) {
            detailsList.get(0).setSheets(HlsCusPrjQuotation.getCompressSheets());
            detailsList.get(0).set__status("update");
        } else {
            hlsCusPrjQuotationDetails.setSheets(HlsCusPrjQuotation.getCompressSheets());
            detailsList.add(hlsCusPrjQuotationDetails);
            detailsList.get(0).set__status("insert");
        }
        prjQuotationDetailsService.batchUpdate(iRequest, detailsList);

        //保存现金流表
        prjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);

        //将报价现金流更新合同现金流
        conContractCashflowService.saveCashflowFromQuotationCashflow(iRequest, prjQuotationDto.getSourceDocumentId(), prjQuotationDto.getQuotationId());

        return prjQuotationDto;
    }
    public JSONObject calculateNewCashflowHeadParam(Long documentId,Long quotationId){
        JSONObject headParam = new JSONObject();
        HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(quotationId);
        HlsCusConContractChangeReq hlsCusConContractChangeReq = contractChangeReqMapper.selectByPrimaryKey(documentId);
        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractChangeReq.getContractId());
        //租金是否返利/贴息
        headParam.put("repay_flag",hlsCusPrjQuotation.getRepayFlag());
        //利率
        headParam.put("int_rate",hlsCusPrjQuotation.getIntRate());
        //内含利率
        //headParam.put("int_rate_reply",hlsCusPrjQuotation.getIntRateReply());
        //税率
        headParam.put("vat_rate",hlsCusPrjQuotation.getVatRate());
        //内含税率
        //headParam.put("vat_rate_repay",hlsCusPrjQuotation.getVatRateRepay());
        //宽限类型-特殊方案
        headParam.put("grace_type_special",hlsCusPrjQuotation.getGraceTypeSpecial());
        //宽限总期数
        //Long graceTimes = hlsCusConContractMapper.queryGraceTimesByContractId(hlsCusConContract.getContractId());
       // headParam.put("grace_times",graceTimes);
        //期利率
        //Double periodIntRate = hlsCusConContractMapper.queryPeriodIntRateByContractId(hlsCusConContract.getContractId());
        //headParam.put("period_int_rate",MathUtil.round(periodIntRate,6));
        //内含期利率
        //Double periodIntRateReply = hlsCusConContractMapper.queryPeriodIntRateReplyByContractId(hlsCusConContract.getContractId());
        //headParam.put("period_int_rate_reply",MathUtil.round(periodIntRateReply,6));
        //业务类型
        headParam.put("business_type",hlsCusConContract.getBusinessType());
        //新增：保证金
        headParam.put("deposit",hlsCusPrjQuotation.getDeposit());
        //新增：保证金处理方式
        headParam.put("deposit_deduction",hlsCusPrjQuotation.getDepositDeduction());
        //贴息支付期数
        //headParam.put("rental_discount_times",hlsCusPrjQuotation.getRentalDiscountTimes());
        //支付频率
        headParam.put("annual_pay_times",hlsCusPrjQuotation.getAnnualPayTimes());

        //资产余值
        headParam.put("assets_surplus_value",hlsCusPrjQuotation.getAssetsSurplusValue());
        return headParam;
    }

    //生成变更服务费现金流
    public HlsCusConContractCashflow calculateNewCashflowServiceFee(HlsCusConContractCashflow rentCashflow,JSONObject configJson,JSONObject headParam,Long documentId,Long quotationId){
        HlsCusConContractCashflow serviceFeeCashflow = new HlsCusConContractCashflow();
        BeanUtil.copyProperties(rentCashflow,serviceFeeCashflow);
        Double vatRate = headParam.getDouble("vat_rate");
        Double dueAmount = configJson.getDouble("ccrFee");
        Double vatDueAmount = MathUtil.div(MathUtil.mul(dueAmount,vatRate,13),1+vatRate,2);
        Double netDueAmount = MathUtil.sub(dueAmount,vatDueAmount,2);
        serviceFeeCashflow.setCashflowId(null);
        serviceFeeCashflow.setCfItem(70L);
        serviceFeeCashflow.setCfType(65L);
        serviceFeeCashflow.setCfDirection("INFLOW");
        serviceFeeCashflow.setDueAmount(dueAmount);
        serviceFeeCashflow.setNetDueAmount(netDueAmount);
        serviceFeeCashflow.setVatDueAmount(vatDueAmount);
        serviceFeeCashflow.setPrincipal(0D);
        serviceFeeCashflow.setNetPrincipal(0D);
        serviceFeeCashflow.setVatPrincipal(0D);
        serviceFeeCashflow.setInterest(0D);
        serviceFeeCashflow.setNetInterest(0D);
        serviceFeeCashflow.setVatInterest(0D);
        serviceFeeCashflow.setOutstandingPrincipal(MathUtil.add(rentCashflow.getOutstandingPrincipal(),rentCashflow.getPrincipal(),2));
        //serviceFeeCashflow.setChangeFlag("Y");
        return serviceFeeCashflow;
    }

    //生成租金返利/贴息现金流
    public HlsCusConContractCashflow calculateNewCashflowRepay(HlsCusConContractCashflow rentCashflow,JSONObject configJson,JSONObject headParam,Long documentId,Long quotationId){
        HlsCusConContractCashflow repayCashflow = new HlsCusConContractCashflow();
        BeanUtil.copyProperties(rentCashflow,repayCashflow);
        Double dueAmount = 0D;
        Double netDueAmount = 0D;
        Double vatDueAmount = 0D;
        Double vatRate = headParam.getDouble("vat_rate");
        Double vatRateRepay = headParam.getDouble("vat_rate_repay");
        //利息-内含利息
        Double repayAmount = Math.abs(MathUtil.sub(rentCashflow.getInterest(),rentCashflow.getReplyFeeInterest(),2));
        //利率>内含利率，生成返利现金流,否则生成贴息现金流
        BigDecimal intRateD = new BigDecimal(headParam.getString("int_rate"));
        BigDecimal intRateReplyD = new BigDecimal(headParam.getString("int_rate_reply"));
        if(intRateD.compareTo(intRateReplyD) > 0){
            //ROUND(利息-内含利息-((利息-内含利息)*税率/(1+税率) - (利息-内含利息)*内含税率/(1+内含税率)),2)
            dueAmount = MathUtil.sub(repayAmount,MathUtil.sub(MathUtil.div(MathUtil.mul(repayAmount,vatRate,13),1+vatRate,13),MathUtil.div(MathUtil.mul(repayAmount,vatRateRepay,13),1+vatRateRepay,13),13),2);
            vatDueAmount = MathUtil.div(MathUtil.mul(dueAmount,vatRateRepay,13),1+vatRateRepay,2);
            netDueAmount = MathUtil.sub(dueAmount,vatDueAmount,2);
            repayCashflow.setCashflowId(null);
            repayCashflow.setCfItem(60L);
            repayCashflow.setCfType(60L);
            repayCashflow.setCfDirection("OUTFLOW");
            repayCashflow.setDueAmount(dueAmount);
            repayCashflow.setNetDueAmount(netDueAmount);
            repayCashflow.setVatDueAmount(vatDueAmount);
            repayCashflow.setPrincipal(0D);
            repayCashflow.setNetPrincipal(0D);
            repayCashflow.setVatPrincipal(0D);
            repayCashflow.setInterest(0D);
            repayCashflow.setNetInterest(0D);
            repayCashflow.setVatInterest(0D);
            //repayCashflow.setChangeFlag("Y");
        }else{
            //ROUND(利息-内含利息,2)
            dueAmount = repayAmount;
            vatDueAmount = MathUtil.div(MathUtil.mul(dueAmount,vatRateRepay,13),1+vatRateRepay,2);
            netDueAmount = MathUtil.sub(dueAmount,vatDueAmount,2);
            repayCashflow.setCashflowId(null);
            repayCashflow.setCfItem(61L);
            repayCashflow.setCfType(60L);
            repayCashflow.setDueAmount(dueAmount);
            repayCashflow.setNetDueAmount(netDueAmount);
            repayCashflow.setVatDueAmount(vatDueAmount);
            repayCashflow.setPrincipal(0D);
            repayCashflow.setNetPrincipal(0D);
            repayCashflow.setVatPrincipal(0D);
            repayCashflow.setInterest(0D);
            repayCashflow.setNetInterest(0D);
            repayCashflow.setVatInterest(0D);
            //repayCashflow.setChangeFlag("Y");
        }
        return repayCashflow;
    }

    //计算付息延期-期限延长
    public List<HlsCusConContractCashflow> calculateNewCashflowDelay(List<HlsCusConContractCashflow> oldCashflows,JSONObject configJson,Long documentId,Long quotationId) throws Exception{
        List<HlsCusConContractCashflow> newCashflows = new ArrayList<>();
        //获取报价头信息
        JSONObject headParam = calculateNewCashflowHeadParam(documentId,quotationId);
        Comparator<HlsCusConContractCashflow> byTimesAsc = Comparator.comparing(HlsCusConContractCashflow::getTimes);
        Comparator<HlsCusConContractCashflow> byCfItemAsc = Comparator.comparing(HlsCusConContractCashflow::getCfItem);
        Comparator<HlsCusConContractCashflow> finalComparator = byTimesAsc.thenComparing(byCfItemAsc);
        List<HlsCusConContractCashflow> oldCashflowsSorted = oldCashflows.stream().sorted(finalComparator).collect(Collectors.toList());
        Double outstandingPrincipal = 0D;
        Double dueAmountReply = 0D;
        Double replyFeeInterest = 0D;
        Double principalReply = 0D;
        Double outstandingPrincipalReply = 0D;
        for(HlsCusConContractCashflow oldCashflow:oldCashflowsSorted){
            //变更起始期前现金流保持不变
            if(oldCashflow.getTimes() < configJson.getLong("ccrStartTimes")){
                HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                BeanUtil.copyProperties(oldCashflow,newCashflow);
                newCashflows.add(newCashflow);
            }else if(oldCashflow.getTimes() == configJson.getLong("ccrStartTimes")){
                if(oldCashflow.getCfItem() == 1L){
                    outstandingPrincipal = MathUtil.add(oldCashflow.getOutstandingPrincipal(),oldCashflow.getPrincipal(),2);
                    dueAmountReply = oldCashflow.getReplyFeeInterest();
                    replyFeeInterest = oldCashflow.getReplyFeeInterest();
                    principalReply = 0D;
                    outstandingPrincipalReply = MathUtil.add(oldCashflow.getOutstandingPrincipalReply(),oldCashflow.getPrincipalReply(),2);
                    //生成对应延期现金流
                    for(int i = 0;i < configJson.getLong("ccrDelayTimes");i++){
                        //生成延期租金现金流
                        HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                        BeanUtil.copyProperties(oldCashflow,newCashflow);
                        Long times = oldCashflow.getTimes() + i;
                        newCashflow.setCashflowId(null);
                        newCashflow.setTimes(times);
                        Date dueDate = newCashflow.getDueDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dueDate);
                        calendar.add(Calendar.MONTH,i*12/headParam.getInteger("annual_pay_times"));
                        dueDate = calendar.getTime();
                        newCashflow.setDueDate(dueDate);
                        newCashflow.setCalcDate(dueDate);
                        newCashflow.setDueAmount(oldCashflow.getInterest());
                        newCashflow.setNetDueAmount(oldCashflow.getNetInterest());
                        newCashflow.setVatDueAmount(oldCashflow.getVatInterest());
                        newCashflow.setPrincipal(0D);
                        newCashflow.setNetPrincipal(0D);
                        newCashflow.setVatPrincipal(0D);
                        newCashflow.setInterest(oldCashflow.getInterest());
                        newCashflow.setNetInterest(oldCashflow.getNetInterest());
                        newCashflow.setVatInterest(oldCashflow.getVatInterest());
                        newCashflow.setOutstandingPrincipal(outstandingPrincipal);
                        newCashflow.setDueAmountReply(dueAmountReply);
                        newCashflow.setPrincipalReply(principalReply);
                        newCashflow.setReplyFeeInterest(replyFeeInterest);
                        newCashflow.setOutstandingPrincipalReply(outstandingPrincipalReply);
                        newCashflow.setChangeFlag("Y");
                        newCashflows.add(newCashflow);
                        //租金返利/贴息标志为Y，利率大于内含利率时，生成返利现金流，利率小于内含利率，且贴息支付期数为每一期时，生成贴息现金流
//                        BigDecimal intRateD = new BigDecimal(headParam.getString("int_rate"));
//                        BigDecimal intRateReplyD = new BigDecimal(headParam.getString("int_rate_reply"));
//                        if("Y".equals(headParam.getString("repay_flag"))&&(intRateD.compareTo(intRateReplyD) > 0)||((intRateD.compareTo(intRateReplyD) < 0)&&"EACH".equals(headParam.getString("rental_discount_times")))){
//                            HlsCusConContractCashflow repayCashflow = new HlsCusConContractCashflow();
//                            repayCashflow = calculateNewCashflowRepay(newCashflow,configJson,headParam,documentId,quotationId);
//                            newCashflows.add(repayCashflow);
//                        }
                    }
                    //变更起始期租金现金流向后平移
                    HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                    BeanUtil.copyProperties(oldCashflow,newCashflow);
                    Long times = oldCashflow.getTimes() + configJson.getLong("ccrDelayTimes");
                    newCashflow.setTimes(times);
                    Date dueDate = newCashflow.getDueDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dueDate);
                    calendar.add(Calendar.MONTH,configJson.getInteger("ccrDelayTimes")*12/headParam.getInteger("annual_pay_times"));
                    dueDate = calendar.getTime();
                    newCashflow.setDueDate(dueDate);
                    newCashflow.setCalcDate(dueDate);
                    newCashflow.setChangeFlag("Y");
                    newCashflows.add(newCashflow);
//                    //租金返利/贴息标志为Y，利率大于内含利率时，生成返利现金流，利率小于内含利率，且贴息支付期数为每一期时，生成贴息现金流
//                    BigDecimal intRateD = new BigDecimal(headParam.getString("int_rate"));
//                    BigDecimal intRateReplyD = new BigDecimal(headParam.getString("int_rate_reply"));
//                    if("Y".equals(headParam.getString("repay_flag"))&&(intRateD.compareTo(intRateReplyD) > 0)||((intRateD.compareTo(intRateReplyD) < 0)&&"EACH".equals(headParam.getString("rental_discount_times")))){
//                        HlsCusConContractCashflow repayCashflow = new HlsCusConContractCashflow();
//                        repayCashflow = calculateNewCashflowRepay(newCashflow,configJson,headParam,documentId,quotationId);
//                        newCashflows.add(repayCashflow);
//                    }
                    //生成变更服务费现金流
                    BigDecimal ccrFeeD = new BigDecimal(configJson.getDouble("ccrFee"));
                    BigDecimal zeroD = new BigDecimal(0D);
                    if(ccrFeeD.compareTo(zeroD) != 0){
                        HlsCusConContractCashflow serviceFeeCashflow = new HlsCusConContractCashflow();
                        serviceFeeCashflow = calculateNewCashflowServiceFee(oldCashflow,configJson,headParam,documentId,quotationId);
                        newCashflows.add(serviceFeeCashflow);
                    }

                }else if(oldCashflow.getCfItem() == 61L||oldCashflow.getCfItem() == 60L){
                    //生成对应延期现金流
                    for(int i = 0;i < configJson.getLong("ccrDelayTimes");i++){
                        //生成延期租金现金流
                        HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                        BeanUtil.copyProperties(oldCashflow,newCashflow);
                        Long times = oldCashflow.getTimes() + i;
                        newCashflow.setCashflowId(null);
                        newCashflow.setTimes(times);
                        Date dueDate = newCashflow.getDueDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(dueDate);
                        calendar.add(Calendar.MONTH,i*12/headParam.getInteger("annual_pay_times"));
                        dueDate = calendar.getTime();
                        newCashflow.setDueDate(dueDate);
                        newCashflow.setCalcDate(dueDate);
                        newCashflow.setOutstandingPrincipal(outstandingPrincipal);
                        newCashflow.setDueAmountReply(dueAmountReply);
                        newCashflow.setPrincipalReply(principalReply);
                        newCashflow.setReplyFeeInterest(replyFeeInterest);
                        newCashflow.setOutstandingPrincipalReply(outstandingPrincipalReply);
                        newCashflow.setChangeFlag("Y");
                        newCashflows.add(newCashflow);
                    }
                    //变更起始期返利贴息现金流向后平移
                    HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                    BeanUtil.copyProperties(oldCashflow,newCashflow);
                    Long times = oldCashflow.getTimes() + configJson.getLong("ccrDelayTimes");
                    newCashflow.setTimes(times);
                    Date dueDate = newCashflow.getDueDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dueDate);
                    calendar.add(Calendar.MONTH,configJson.getInteger("ccrDelayTimes")*12/headParam.getInteger("annual_pay_times"));
                    dueDate = calendar.getTime();
                    newCashflow.setDueDate(dueDate);
                    newCashflow.setCalcDate(dueDate);
                    newCashflow.setChangeFlag("Y");
                    newCashflows.add(newCashflow);
                }else{
                    //变更起始非租金现金流向后平移
                    HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                    BeanUtil.copyProperties(oldCashflow,newCashflow);
                    Long times = oldCashflow.getTimes() + configJson.getLong("ccrDelayTimes");
                    newCashflow.setTimes(times);
                    Date dueDate = newCashflow.getDueDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dueDate);
                    calendar.add(Calendar.MONTH,configJson.getInteger("ccrDelayTimes")*12/headParam.getInteger("annual_pay_times"));
                    dueDate = calendar.getTime();
                    newCashflow.setDueDate(dueDate);
                    newCashflow.setCalcDate(dueDate);
                    newCashflow.setChangeFlag("Y");
                    newCashflows.add(newCashflow);
                }
            }else{
                //变更起始期后租金和名义货价现金流向后平移
                if(oldCashflow.getCfItem() == 1L){
                    HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                    BeanUtil.copyProperties(oldCashflow,newCashflow);
                    Long times = oldCashflow.getTimes() + configJson.getLong("ccrDelayTimes");
                    newCashflow.setTimes(times);
                    Date dueDate = newCashflow.getDueDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dueDate);
                    calendar.add(Calendar.MONTH,configJson.getLong("ccrDelayTimes").intValue()*12/headParam.getInteger("annual_pay_times"));
                    dueDate = calendar.getTime();
                    newCashflow.setDueDate(dueDate);
                    newCashflow.setCalcDate(dueDate);
                    newCashflow.setChangeFlag("Y");
                    newCashflows.add(newCashflow);
                    //租金返利/贴息标志为Y，利率大于内含利率时，生成返利现金流，利率小于内含利率，且贴息支付期数为每一期时，生成贴息现金流
//                    BigDecimal intRateD = new BigDecimal(headParam.getString("int_rate"));
//                    BigDecimal intRateReplyD = new BigDecimal(headParam.getString("int_rate_reply"));
//                    if("Y".equals(headParam.getString("repay_flag"))&&(intRateD.compareTo(intRateReplyD) > 0)||((intRateD.compareTo(intRateReplyD) < 0)&&"EACH".equals(headParam.getString("rental_discount_times")))){
//                        HlsCusConContractCashflow repayCashflow = new HlsCusConContractCashflow();
//                        repayCashflow = calculateNewCashflowRepay(newCashflow,configJson,headParam,documentId,quotationId);
//                        newCashflows.add(repayCashflow);
//                    }
                }else{
                    HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
                    BeanUtil.copyProperties(oldCashflow,newCashflow);
                    Long times = oldCashflow.getTimes() + configJson.getLong("ccrDelayTimes");
                    newCashflow.setTimes(times);
                    Date dueDate = newCashflow.getDueDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dueDate);
                    calendar.add(Calendar.MONTH,configJson.getLong("ccrDelayTimes").intValue()*12/headParam.getInteger("annual_pay_times"));
                    dueDate = calendar.getTime();
                    newCashflow.setDueDate(dueDate);
                    newCashflow.setCalcDate(dueDate);
                    newCashflow.setChangeFlag("Y");
                    newCashflows.add(newCashflow);
                }
            }
        }
        return newCashflows;
    }

    //计算本金分摊-期限不变(等额租金后付，等额租金后付（复利），等额租金后付（宽限期延|均摊）)
    public List<HlsCusConContractCashflow> calculateNewCashflowEqualRental(List<HlsCusConContractCashflow> oldCashflows,String oldPriceList,String ccrPriceList,JSONObject configJson,Long documentId,Long quotationId) throws Exception{
        List<HlsCusConContractCashflow> newCashflows = new ArrayList<>();
        //提前还本金额
        Double changePrincipal = configJson.getDouble("changePrincipal");
        //提前还本业务标识
        String partialPrepaymentFlag = configJson.getString("partialPrepaymentFlag");
        //获取报价头信息
        JSONObject headParam = calculateNewCashflowHeadParam(documentId,quotationId);
        //变更起始期数
        Long ccrStartTimes = configJson.getLong("ccrStartTimes");
        //变更期数
        Long ccrDelayTimes = configJson.getLong("ccrDelayTimes");
        //变更后总期数
        Long ccrTimes = configJson.getLong("ccrTimes");
        //变更结束期数
        Long ccrEndTimes = ccrStartTimes + ccrDelayTimes - 1L;
        //需要重算的期数
        Long changeTimes = ccrTimes - ccrStartTimes + 1L;
        //Long 变更结束后剩余期数
        Long leftTimes = ccrTimes - ccrEndTimes;
        //宽限总期数
        Long graceTimes = headParam.getLong("grace_times");
        //期利率
        Double periodIntRate = headParam.getDouble("period_int_rate");
        //内含期利率
        Double periodIntRateReply = headParam.getDouble("period_int_rate_reply");

        //新增字段:年利率
        Double intRate = headParam.getDouble("int_rate");
        //新增字段:提前还本日
        Date changeStartDate = configJson.getDate("changeStartDate");
        //新增字段：还款频率
        Long annualDayTimes = headParam.getLong("annual_pay_times");
        //新增字段：手续费
        Double leaseCharge = configJson.getDouble("leaseCharge");
        //新增字段：保证金
        Double deposit = headParam.getDouble("deposit");
        //新增字段：保证金处理方式
        String depositDeduction = headParam.getString("deposit_deduction");

        Double assetsSurplusValue = headParam.getDoubleValue("assets_surplus_value");

        //税率
        Double vatRate = headParam.getDouble("vat_rate");
        //业务类型
        String businessType = headParam.getString("business_type");
        //获取第0期剩余本金
        Double outstandingPrincipal0 = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
        //获取变更起始期前一期现金流剩余本金
        //获取变更起始期前一期现金流剩余内含本金
        Double ccrOutstandingPrincipal = 0D;
        Double ccrOutstandingPrincipalReply = 0D;

        //新增：获取变更起始期前一期租金支付日
        Date dueDate = null;

        List<HlsCusConContractCashflow> ccrOutstandingCashflows = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(ccrOutstandingCashflows)){
            ccrOutstandingPrincipal = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
            ccrOutstandingPrincipalReply = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getOutstandingPrincipalReply();
            dueDate = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getDueDate();
        }else{
            ccrOutstandingPrincipal = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
            ccrOutstandingPrincipalReply = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList()).get(0).getOutstandingPrincipalReply();
            dueDate = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList()).get(0).getDueDate();
        }


        if (ccrOutstandingPrincipalReply == null){
            ccrOutstandingPrincipalReply = 0D;
        }

        //变更起始期前现金流
        List<HlsCusConContractCashflow> noChangeCashflows = oldCashflows.stream().filter(item->item.getTimes() < configJson.getLong("ccrStartTimes")).collect(Collectors.toList());
        //变更起始期后现金流
        List<HlsCusConContractCashflow> changeCashflows = oldCashflows.stream().filter(item->item.getTimes() >= configJson.getLong("ccrStartTimes")).collect(Collectors.toList());

        //变更起始期前现金流保持不变
        for(HlsCusConContractCashflow oldCashflow:noChangeCashflows){
            HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
            BeanUtil.copyProperties(oldCashflow,newCashflow);
            newCashflows.add(newCashflow);
        }


        //变更后每期租金
        Double rentAmount = 0D;
        //变更后每期内含租金
        Double rentAmountReply = 0D;

        //新增：最后一期计算日
        Date lastCalcDate = null;


        //提前还本业务：1.变更起始期：提前还本金额 2.最后一期：上一期剩余本金 3.中间期数：pmt租金 - 当期利息
        if ("Y".equals(partialPrepaymentFlag)){
            Double calPrincipal = ccrOutstandingPrincipal - changePrincipal;
            Long calTimes = ccrTimes - ccrStartTimes;
            Double calIntRate = intRate/annualDayTimes;
            //变更后每期租金
            rentAmount = MathUtil.round( -FinanceLib.pmt(calIntRate,calTimes,calPrincipal,assetsSurplusValue,false),2);
        }else {
            //变更后每期租金
            rentAmount = MathUtil.round(PMTUtil.pmt(periodIntRate,leftTimes,ccrOutstandingPrincipal,8),2);
            //变更后每期内含租金
            rentAmountReply = MathUtil.round(PMTUtil.pmt(periodIntRateReply,leftTimes,ccrOutstandingPrincipalReply,8),2);
        }


        //上一期剩余本金
        Double lastOutstandingPrincipal = ccrOutstandingPrincipal;
        //上一期内含剩余本金
        Double lastOutstandingPrincipalReply = ccrOutstandingPrincipalReply;

        //生成新的租金现金流，手续费现金流
        for(int i = 0;i < changeTimes;i++){
            //当前期数
            Long currentTime = ccrStartTimes + i;
            HlsCusConContractCashflow oldCashflow = changeCashflows.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);
            HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
            BeanUtil.copyProperties(oldCashflow,newCashflow);


            Double interest = 0D;
            Double dueAmount = 0D;
            Double principal= 0D;
            Double outstandingPrincipal = 0D;
            Double interestReply = 0D;
            Double dueAmountReply = 0D;
            Double principalReply = 0D;
            Double outstandingPrincipalReply = 0D;
            Double netDueAmount = 0D;
            Double vatDueAmount = 0D;
            Double netPrincipal = 0D;
            Double vatPrincipal = 0D;
            Double netInterest = 0D;
            Double vatInterest = 0D;

            //计算日
            Date calcDate = null;

            //--------------------------------------------------------
            // --start---新增：重写计算过程
            //--------------------------------------------------------
            if(currentTime == ccrStartTimes){
                //1.变更当期
                // 利息计算：上一期剩余本金 * 年利率/360 * DAY(提前还本日-上一期租金支付日）
                int days = DateCalculate.daysBetween(dueDate,changeStartDate);
                Double currentIntRate = MathUtil.mul(MathUtil.div(intRate,360D,8),days,6);
                interest = MathUtil.mul(lastOutstandingPrincipal, currentIntRate,2);
                //本金：提前还本金额
                principal = changePrincipal;
                //剩余本金：剩余本金 - 提前还本金额
                outstandingPrincipal = ccrOutstandingPrincipal - changePrincipal;
                //租金
                dueAmount = MathUtil.add(interest,principal,2);

                //更新上一期剩余本金
                lastOutstandingPrincipal = outstandingPrincipal;

                //增值税/营业税额：当期租金/(1 + 税率) * 税率
                Double vatAmount = MathUtil.mul(MathUtil.div(dueAmount,MathUtil.add(1D,vatRate,2),6), vatRate, 2);

                //本金(不含税)
                if("LEASE".equals(businessType)){
                    netPrincipal = MathUtil.div(principal,MathUtil.add(1D,vatRate,2),2);
                }else{
                    netPrincipal = principal;
                }
                //本金(税额)
                vatPrincipal = MathUtil.sub(principal,netPrincipal,2);

                //利息（不含税）
                netInterest = MathUtil.div(interest,MathUtil.add(1D,vatRate,2),2);
                //利息（税额）
                vatInterest = MathUtil.sub(interest,netInterest,2);
                //租金(不含税)
                netDueAmount = MathUtil.add(netPrincipal,netInterest,2);
                //租金(税额)
                vatDueAmount = MathUtil.add(vatPrincipal,vatInterest, 2);
            }else if(currentTime < ccrTimes){
                //2.非变更当期且不是最后一期
                // 利息计算：上一期剩余本金 * 年利率/年还租次数
                Double currentIntRate = MathUtil.div(intRate,annualDayTimes,6);
                interest = MathUtil.mul(lastOutstandingPrincipal, currentIntRate,2);
                //本金
                principal = MathUtil.sub(rentAmount, interest, 2);


                //剩余本金：上一期剩余本金 - 当期本金
                outstandingPrincipal = MathUtil.sub(lastOutstandingPrincipal, principal,2);
                //租金
                dueAmount = MathUtil.add(interest, principal,2);

                //更新上一期剩余本金
                lastOutstandingPrincipal = outstandingPrincipal;

                //增值税/营业税额：当期租金/(1 + 税率) * 税率
                Double vatAmount = MathUtil.mul(MathUtil.div(dueAmount,MathUtil.add(1D,vatRate,2),6), vatRate, 2);

                //本金(不含税)
                if("LEASE".equals(businessType)){
                    netPrincipal = MathUtil.div(principal,MathUtil.add(1D,vatRate,2),2);
                }else{
                    netPrincipal = principal;
                }
                //本金(税额)
                vatPrincipal = MathUtil.sub(principal,netPrincipal,2);

                //利息（不含税）
                netInterest = MathUtil.div(interest,MathUtil.add(1D,vatRate,2),2);
                //利息（税额）
                vatInterest = MathUtil.sub(interest,netInterest,2);
                //租金(不含税)
                netDueAmount = MathUtil.add(netPrincipal,netInterest,2);
                //租金(税额)
                vatDueAmount = MathUtil.add(vatPrincipal,vatInterest, 2);

            }else {
                //3.非变更当期且最后一期
                // 利息计算：上一期剩余本金 * 年利率/年还租次数
                Double currentIntRate = MathUtil.div(intRate,annualDayTimes,6);
                interest = MathUtil.mul(lastOutstandingPrincipal, currentIntRate,2);
                //本金
                principal = lastOutstandingPrincipal;

                //剩余本金：上一期剩余本金 - 当期本金
                outstandingPrincipal = MathUtil.sub(lastOutstandingPrincipal, principal,2);
                //租金
                dueAmount = MathUtil.add(interest, principal,2);

                //更新上一期剩余本金
                lastOutstandingPrincipal = outstandingPrincipal;

                //增值税/营业税额：当期租金/(1 + 税率) * 税率
                Double vatAmount = MathUtil.mul(MathUtil.div(dueAmount,MathUtil.add(1D,vatRate,2),6), vatRate, 2);

                //本金(不含税)
                if("LEASE".equals(businessType)){
                    netPrincipal = MathUtil.div(principal,MathUtil.add(1D,vatRate,2),2);
                }else{
                    netPrincipal = principal;
                }
                //本金(税额)
                vatPrincipal = MathUtil.sub(principal,netPrincipal,2);

                //利息（不含税）
                netInterest = MathUtil.div(interest,MathUtil.add(1D,vatRate,2),2);
                //利息（税额）
                vatInterest = MathUtil.sub(interest,netInterest,2);
                //租金(不含税)
                netDueAmount = MathUtil.add(netPrincipal,netInterest,2);
                //租金(税额)
                vatDueAmount = MathUtil.add(vatPrincipal,vatInterest, 2);

            }
            //--------------------------------------------------------
            // --end----新增：重写计算过程
            //--------------------------------------------------------


            //计算日获取
            if(currentTime == ccrStartTimes){
                logger.info("changeStartDate:{}",changeStartDate);
                newCashflow.setCalcDate(changeStartDate);
                newCashflow.setFinIncomeDate(changeStartDate);
                newCashflow.setDueDate(changeStartDate);
            }else{
                Double monthsToAdd = MathUtil.mul(MathUtil.div(12D, annualDayTimes.doubleValue(),0),i,0);
                calcDate = DateUtils.plusMonths(changeStartDate, monthsToAdd.longValue());
                logger.info("calcDate:{}",calcDate);
                newCashflow.setCalcDate(calcDate);
                newCashflow.setFinIncomeDate(calcDate);
                newCashflow.setDueDate(calcDate);
                lastCalcDate = calcDate;
            }


            newCashflow.setCashflowId(null);
            newCashflow.setTimes(currentTime);
            newCashflow.setDueAmount(dueAmount);
            newCashflow.setNetDueAmount(netDueAmount);
            newCashflow.setVatDueAmount(vatDueAmount);
            newCashflow.setPrincipal(principal);
            newCashflow.setNetPrincipal(netPrincipal);
            newCashflow.setVatPrincipal(vatPrincipal);
            newCashflow.setInterest(interest);
            newCashflow.setNetInterest(netInterest);
            newCashflow.setVatInterest(vatInterest);
            newCashflow.setOutstandingPrincipal(outstandingPrincipal);
            newCashflow.setDueAmountReply(dueAmountReply);
            newCashflow.setPrincipalReply(principalReply);
            newCashflow.setReplyFeeInterest(interestReply);
            newCashflow.setOutstandingPrincipalReply(outstandingPrincipalReply);
            //TODO 2023-04-04  净现金流 先临时让其等于应收金额
            newCashflow.setCashflowIrr(dueAmount);
            newCashflow.setChangeFlag("Y");
            newCashflows.add(newCashflow);


            if(currentTime == ccrStartTimes){
                //生成手续费现金流
                HlsCusConContractCashflow leaseChargeCashflow = calculateNewCashflowLeaseCharge(newCashflow, leaseCharge);
                newCashflows.add(leaseChargeCashflow);
            }

        }

        //2023-04-11 保证金现金流处理
        //变更后的所有租金之和
        Double totalChangeDueAmount = newCashflows.stream().filter(item->item.getCfItem().equals(1L)).mapToDouble(HlsCusConContractCashflow::getDueAmount).sum();
        //保证金 - 变更后的所有租金之和 ，用于判断满足两种情况的哪一种
        Double totalSurplusDeposit = MathUtil.sub(deposit, totalChangeDueAmount);
        List<HlsCusConContractCashflow> depositCashflows = oldCashflows.stream().filter(item->item.getTimes().equals(ccrTimes)&&item.getCfItem().equals(52L)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(depositCashflows)){
            HlsCusConContractCashflow depositCashflow = depositCashflows.get(0);
            if (depositCashflow != null){
                if (DEPOSIT_PERIOD_FINAL_RETURN.equals(depositDeduction)){
                    //保证金退还：直接改日期即可
                    depositCashflow.setDueDate(lastCalcDate);
                    depositCashflow.setFinIncomeDate(lastCalcDate);
                    depositCashflow.setCalcDate(lastCalcDate);
                    newCashflows.add(depositCashflow);
                }else if (DEPOSIT_PERIOD_FINAL_DEDUCTIBLE.equals(depositDeduction)){


                    //保证金抵扣：从最后一期开始分摊，若保证金覆盖完最后一期租金仍有剩余，则向上对前一期租金进行分摊，直到分摊完；
                    //  两种情况：1.可以分摊完   2.分摊到变更起始期，剩余保证金仍大于当期租金，则把剩余保证金加到最后一期上

                    //剩余保证金:初始值等于保证金
                    Double surplusDeposit = deposit;
                    if (totalSurplusDeposit > 0){
                        //2.分摊到变更起始期，剩余保证金仍大于当期租金，则把剩余保证金加到最后一期上
                        //剩余保证金重新赋值：初始保证金 - 多出来的
                        surplusDeposit =  MathUtil.sub(deposit, totalSurplusDeposit);
                        for (int j = Math.toIntExact(ccrTimes); j >= ccrStartTimes; j--) {
                            Long currentTime = (long) j;
                            HlsCusConContractCashflow currentRentCashflow = newCashflows.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);

                            Double currentDueAmount = currentRentCashflow.getDueAmount();

                            HlsCusConContractCashflow newDepositCashflow = new HlsCusConContractCashflow();
                            BeanUtil.copyProperties(depositCashflow,newDepositCashflow);
                            newDepositCashflow.setCashflowId(null);
                            newDepositCashflow.setTimes(currentRentCashflow.getTimes());
                            newDepositCashflow.setCalcDate(currentRentCashflow.getCalcDate());
                            newDepositCashflow.setFinIncomeDate(currentRentCashflow.getFinIncomeDate());
                            newDepositCashflow.setDueDate(currentRentCashflow.getDueDate());
                            newDepositCashflow.setOutstandingAmount(currentRentCashflow.getOutstandingAmount());
                            newDepositCashflow.setCashflowIrr(currentRentCashflow.getCashflowIrr());


                            if (surplusDeposit > currentDueAmount){
                                surplusDeposit = MathUtil.sub(surplusDeposit, currentDueAmount);
                                if (currentTime == ccrTimes){
                                    //最后一期加上剩余的保证金
                                    newDepositCashflow.setDueAmount(MathUtil.add(currentDueAmount,totalSurplusDeposit));
                                }else {
                                    newDepositCashflow.setDueAmount(currentDueAmount);
                                }
                                newCashflows.add(newDepositCashflow);
                            }else {
                                newDepositCashflow.setDueAmount(surplusDeposit);
                                newCashflows.add(newDepositCashflow);
                            }
                        }

                    }else {
                        //1.可以分摊完
                        for (int j = Math.toIntExact(ccrTimes); j >= ccrStartTimes; j--) {
                            Long currentTime = (long) j;
                            HlsCusConContractCashflow currentRentCashflow = newCashflows.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);

                            Double currentDueAmount = currentRentCashflow.getDueAmount();

                            HlsCusConContractCashflow newDepositCashflow = new HlsCusConContractCashflow();
                            BeanUtil.copyProperties(depositCashflow,newDepositCashflow);
                            newDepositCashflow.setCashflowId(null);
                            newDepositCashflow.setTimes(currentRentCashflow.getTimes());
                            newDepositCashflow.setCalcDate(currentRentCashflow.getCalcDate());
                            newDepositCashflow.setFinIncomeDate(currentRentCashflow.getFinIncomeDate());
                            newDepositCashflow.setDueDate(currentRentCashflow.getDueDate());
                            newDepositCashflow.setOutstandingAmount(currentRentCashflow.getOutstandingAmount());
                            newDepositCashflow.setCashflowIrr(currentRentCashflow.getCashflowIrr());

                            if (surplusDeposit > currentDueAmount){
                                surplusDeposit = MathUtil.sub(surplusDeposit, currentDueAmount);
                                newDepositCashflow.setDueAmount(currentDueAmount);
                                newCashflows.add(newDepositCashflow);

                            }else {
                                newDepositCashflow.setDueAmount(surplusDeposit);
                                newCashflows.add(newDepositCashflow);
                                break;
                            }
                        }
                    }

                }
            }
        }

        //保留原名义货价现金流
        List<HlsCusConContractCashflow> priceCashflows = oldCashflows.stream().filter(item->item.getTimes().equals(ccrTimes)&&item.getCfItem().equals(8L)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(priceCashflows)){
            HlsCusConContractCashflow priceCashflow = priceCashflows.get(0);
            priceCashflow.setCalcDate(lastCalcDate);
            priceCashflow.setFinIncomeDate(lastCalcDate);
            priceCashflow.setDueDate(lastCalcDate);
            newCashflows.add(priceCashflow);
        }

        return newCashflows;
    }

    //计算本金分摊-期限不变(等额本金后付，等额本金后付（复利），等额本金后付（宽限期延息|均摊）)
    public List<HlsCusConContractCashflow> calculateNewCashflowEqualPrincipal(List<HlsCusConContractCashflow> oldCashflows,String oldPriceList,String ccrPriceList,JSONObject configJson,Long documentId,Long quotationId) throws Exception{
        List<HlsCusConContractCashflow> newCashflows = new ArrayList<>();
        //提前还本金额
        Double changePrincipal = configJson.getDouble("changePrincipal");
        //提前还本业务标识
        String partialPrepaymentFlag = configJson.getString("partialPrepaymentFlag");
        //获取报价头信息
        JSONObject headParam = calculateNewCashflowHeadParam(documentId,quotationId);
        //变更起始期数
        Long ccrStartTimes = configJson.getLong("ccrStartTimes");
        //变更期数
        Long ccrDelayTimes = configJson.getLong("ccrDelayTimes");
        //变更后总期数
        Long ccrTimes = configJson.getLong("ccrTimes");
        //变更结束期数
        Long ccrEndTimes = ccrStartTimes + ccrDelayTimes - 1L;
        //需要重算的期数
        Long changeTimes = ccrTimes - ccrStartTimes + 1L;
        //Long 变更结束后剩余期数
        Long leftTimes = ccrTimes - ccrEndTimes;
        //宽限总期数
        Long graceTimes = headParam.getLong("grace_times");
        //期利率
        Double periodIntRate = headParam.getDouble("period_int_rate");
        //内含期利率
        Double periodIntRateReply = headParam.getDouble("period_int_rate_reply");

        //新增字段:年利率
        Double intRate = headParam.getDouble("int_rate");
        //新增字段:提前还本日
        Date changeStartDate = configJson.getDate("changeStartDate");
        //新增字段：还款频率
        Long annualDayTimes = headParam.getLong("annual_pay_times");
        //新增字段：手续费
        Double leaseCharge = configJson.getDouble("leaseCharge");
        //新增字段：保证金
        Double deposit = headParam.getDouble("deposit");
        //新增字段：保证金处理方式
        String depositDeduction = headParam.getString("deposit_deduction");

        //税率
        Double vatRate = headParam.getDouble("vat_rate");
        //业务类型
        String businessType = headParam.getString("business_type");
        //获取第0期剩余本金
        Double outstandingPrincipal0 = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
        //获取变更起始期前一期现金流剩余本金
        //获取变更起始期前一期现金流剩余内含本金
        Double ccrOutstandingPrincipal = 0D;
        Double ccrOutstandingPrincipalReply = 0D;
        //新增：获取变更起始期前一期租金支付日
        Date dueDate = null;
        List<HlsCusConContractCashflow> ccrOutstandingCashflows = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(ccrOutstandingCashflows)){
            ccrOutstandingPrincipal = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
            ccrOutstandingPrincipalReply = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getOutstandingPrincipalReply();
            dueDate = oldCashflows.stream().filter(item->item.getTimes().equals(0L)&&item.getCfItem().equals(0L)).collect(Collectors.toList()).get(0).getDueDate();
        }else{
            ccrOutstandingPrincipal = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList()).get(0).getOutstandingPrincipal();
            ccrOutstandingPrincipalReply = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList()).get(0).getOutstandingPrincipalReply();
            dueDate = oldCashflows.stream().filter(item->item.getTimes().equals(ccrStartTimes - 1L)&&(item.getCfItem().equals(1L)||item.getCfItem().equals(0L))).collect(Collectors.toList()).get(0).getDueDate();

        }

        if (ccrOutstandingPrincipalReply == null){
            ccrOutstandingPrincipalReply = 0D;
        }

        //变更起始期前现金流
        List<HlsCusConContractCashflow> noChangeCashflows = oldCashflows.stream().filter(item->item.getTimes() < configJson.getLong("ccrStartTimes")).collect(Collectors.toList());
        //变更起始期后现金流
        List<HlsCusConContractCashflow> changeCashflows = oldCashflows.stream().filter(item->item.getTimes() >= configJson.getLong("ccrStartTimes")).collect(Collectors.toList());

        //变更起始期前现金流保持不变
        for(HlsCusConContractCashflow oldCashflow:noChangeCashflows){
            HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
            BeanUtil.copyProperties(oldCashflow,newCashflow);
            newCashflows.add(newCashflow);
        }

        //变更后每期本金
        Double principalAmount = 0D;
        //变更后每期内含租金
        Double principalAmountReply = 0D;

        //上一期剩余本金
        Double lastOutstandingPrincipal = ccrOutstandingPrincipal;
        //上一期内含剩余本金
        Double lastOutstandingPrincipalReply = ccrOutstandingPrincipalReply;

        //提前还本业务：除开提前还本当期外，剩余的期数计算本金：(剩余本金 - 提前还本金额)/(总租赁期数-还本起始期数)
        if ("Y".equals(partialPrepaymentFlag)){
            Double calPrincipal = ccrOutstandingPrincipal - changePrincipal;
            Long calTimes = ccrTimes - ccrStartTimes;
            //变更后每期本金
            principalAmount = MathUtil.div(calPrincipal,calTimes.doubleValue(),2);
        }else {
            //变更后每期本金
            principalAmount = MathUtil.div(ccrOutstandingPrincipal,leftTimes.doubleValue(),2);
            //变更后每期内含租金
            principalAmountReply =  MathUtil.div(ccrOutstandingPrincipalReply,leftTimes.doubleValue(),2);
        }

        //新增：最后一期计算日
        Date lastCalcDate = null;

        //生成新的租金现金流，手续费现金流
        for(int i = 0;i < changeTimes;i++){
            //当前期数
            Long currentTime = ccrStartTimes + i;
            HlsCusConContractCashflow oldCashflow = changeCashflows.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);
            HlsCusConContractCashflow newCashflow = new HlsCusConContractCashflow();
            BeanUtil.copyProperties(oldCashflow,newCashflow);
            Double graceAmount = 0D;
            Double graceAmountReply = 0D;


            Double interest = 0D;
            Double dueAmount = 0D;
            Double principal= 0D;
            Double outstandingPrincipal = 0D;
            Double interestReply = 0D;
            Double dueAmountReply = 0D;
            Double principalReply = 0D;
            Double outstandingPrincipalReply = 0D;
            Double netDueAmount = 0D;
            Double vatDueAmount = 0D;
            Double netPrincipal = 0D;
            Double vatPrincipal = 0D;
            Double netInterest = 0D;
            Double vatInterest = 0D;

            //计算日
            Date calcDate = null;

            //--------------------------------------------------------
            // --start---新增：重写计算过程
            //--------------------------------------------------------
            if(currentTime == ccrStartTimes){
                //1.变更当期
                // 利息计算：上一期剩余本金 * 年利率/360 * DAY(提前还本日-上一期租金支付日）
                int days = DateCalculate.daysBetween(dueDate,changeStartDate);
                Double currentIntRate = MathUtil.mul(MathUtil.div(intRate,360D,8),days,6);
                interest = MathUtil.mul(lastOutstandingPrincipal, currentIntRate,2);
                //本金：提前还本金额
                principal = changePrincipal;
                //剩余本金：剩余本金 - 提前还本金额
                outstandingPrincipal = ccrOutstandingPrincipal - changePrincipal;
                //租金
                dueAmount = MathUtil.add(interest,principal,2);

                //更新上一期剩余本金
                lastOutstandingPrincipal = outstandingPrincipal;

                //增值税/营业税额：当期租金/(1 + 税率) * 税率
                Double vatAmount = MathUtil.mul(MathUtil.div(dueAmount,MathUtil.add(1D,vatRate,2),6), vatRate, 2);

                //本金(不含税)
                if("LEASE".equals(businessType)){
                    netPrincipal = MathUtil.div(principal,MathUtil.add(1D,vatRate,2),2);
                }else{
                    netPrincipal = principal;
                }
                //本金(税额)
                vatPrincipal = MathUtil.sub(principal,netPrincipal,2);

                //利息（不含税）
                netInterest = MathUtil.div(interest,MathUtil.add(1D,vatRate,2),2);
                //利息（税额）
                vatInterest = MathUtil.sub(interest,netInterest,2);
                //租金(不含税)
                netDueAmount = MathUtil.add(netPrincipal,netInterest,2);
                //租金(税额)
                vatDueAmount = MathUtil.add(vatPrincipal,vatInterest, 2);

            }else if(currentTime < ccrTimes){

                //2.非变更当期且不是最后一期
                // 利息计算：上一期剩余本金 * 年利率/年还租次数
                Double currentIntRate = MathUtil.div(intRate,annualDayTimes,6);
                interest = MathUtil.mul(lastOutstandingPrincipal, currentIntRate,2);
                //本金
                principal = principalAmount;


                //剩余本金：上一期剩余本金 - 当期本金
                outstandingPrincipal = MathUtil.sub(lastOutstandingPrincipal,principal,2);
                //租金
                dueAmount = MathUtil.add(interest,principal,2);

                //更新上一期剩余本金
                lastOutstandingPrincipal = outstandingPrincipal;

                //增值税/营业税额：当期租金/(1 + 税率) * 税率
                Double vatAmount = MathUtil.mul(MathUtil.div(dueAmount,MathUtil.add(1D,vatRate,2),6), vatRate, 2);

                //本金(不含税)
                if("LEASE".equals(businessType)){
                    netPrincipal = MathUtil.div(principal,MathUtil.add(1D,vatRate,2),2);
                }else{
                    netPrincipal = principal;
                }
                //本金(税额)
                vatPrincipal = MathUtil.sub(principal,netPrincipal,2);

                //利息（不含税）
                netInterest = MathUtil.div(interest,MathUtil.add(1D,vatRate,2),2);
                //利息（税额）
                vatInterest = MathUtil.sub(interest,netInterest,2);
                //租金(不含税)
                netDueAmount = MathUtil.add(netPrincipal,netInterest,2);
                //租金(税额)
                vatDueAmount = MathUtil.add(vatPrincipal,vatInterest, 2);

            }else {
                //3.非变更当期且最后一期
                // 利息计算：上一期剩余本金 * 年利率/年还租次数
                Double currentIntRate = MathUtil.div(intRate,annualDayTimes,6);
                interest = MathUtil.mul(lastOutstandingPrincipal, currentIntRate,2);
                //本金
                principal = lastOutstandingPrincipal;

                //剩余本金：上一期剩余本金 - 当期本金
                outstandingPrincipal = MathUtil.sub(lastOutstandingPrincipal,principal,2);
                //租金
                dueAmount = MathUtil.add(interest,principal,2);

                //更新上一期剩余本金
                lastOutstandingPrincipal = outstandingPrincipal;

                //增值税/营业税额：当期租金/(1 + 税率) * 税率
                Double vatAmount = MathUtil.mul(MathUtil.div(dueAmount,MathUtil.add(1D,vatRate,2),6), vatRate, 2);

                //本金(不含税)
                if("LEASE".equals(businessType)){
                    netPrincipal = MathUtil.div(principal,MathUtil.add(1D,vatRate,2),2);
                }else{
                    netPrincipal = principal;
                }
                //本金(税额)
                vatPrincipal = MathUtil.sub(principal,netPrincipal,2);

                //利息（不含税）
                netInterest = MathUtil.div(interest,MathUtil.add(1D,vatRate,2),2);
                //利息（税额）
                vatInterest = MathUtil.sub(interest,netInterest,2);
                //租金(不含税)
                netDueAmount = MathUtil.add(netPrincipal,netInterest,2);
                //租金(税额)
                vatDueAmount = MathUtil.add(vatPrincipal,vatInterest, 2);

            }
            //--------------------------------------------------------
            // --end----新增：重写计算过程
            //--------------------------------------------------------



//            leftGraceAmount = leftGraceAmount - graceAmount;
//            leftGraceAmountReply = leftGraceAmountReply - graceAmountReply;

            //计算日获取
            if(currentTime == ccrStartTimes){
                logger.info("changeStartDate: {}",changeStartDate);
                newCashflow.setCalcDate(changeStartDate);
                newCashflow.setFinIncomeDate(changeStartDate);
                newCashflow.setDueDate(changeStartDate);
            }else{
                Double monthsToAdd = MathUtil.mul(MathUtil.div(12D, annualDayTimes.doubleValue(),0),i,0);
                calcDate = DateUtils.plusMonths(changeStartDate, monthsToAdd.longValue());
                logger.info("calcDate:{} ",calcDate);
                newCashflow.setCalcDate(calcDate);
                newCashflow.setFinIncomeDate(calcDate);
                newCashflow.setDueDate(calcDate);
                //获取最后一期的日期，用于更新最后一期的名义货价现金流
                lastCalcDate = calcDate;
            }

            newCashflow.setCashflowId(null);
            newCashflow.setTimes(currentTime);
            newCashflow.setDueAmount(dueAmount);
            newCashflow.setNetDueAmount(netDueAmount);
            newCashflow.setVatDueAmount(vatDueAmount);
            newCashflow.setPrincipal(principal);
            newCashflow.setNetPrincipal(netPrincipal);
            newCashflow.setVatPrincipal(vatPrincipal);
            newCashflow.setInterest(interest);
            newCashflow.setNetInterest(netInterest);
            newCashflow.setVatInterest(vatInterest);
            newCashflow.setOutstandingPrincipal(outstandingPrincipal);
            newCashflow.setDueAmountReply(dueAmountReply);
            newCashflow.setPrincipalReply(principalReply);
            newCashflow.setReplyFeeInterest(interestReply);
            newCashflow.setOutstandingPrincipalReply(outstandingPrincipalReply);
            //TODO 2023-04-04  净现金流 先临时让其等于应收金额
            newCashflow.setCashflowIrr(dueAmount);
            newCashflow.setChangeFlag("Y");
            newCashflows.add(newCashflow);

            if(currentTime == ccrStartTimes){
                //生成手续费现金流
                HlsCusConContractCashflow leaseChargeCashflow = calculateNewCashflowLeaseCharge(newCashflow, leaseCharge);
                 newCashflows.add(leaseChargeCashflow);
            }
        }

        //2023-04-11 保证金现金流处理
        //变更后的所有租金之和
        Double totalChangeDueAmount = newCashflows.stream().filter(item->item.getCfItem().equals(1L)).mapToDouble(HlsCusConContractCashflow::getDueAmount).sum();
        //保证金 - 变更后的所有租金之和 ，用于判断满足两种情况的哪一种
        Double totalSurplusDeposit = MathUtil.sub(deposit, totalChangeDueAmount);
        List<HlsCusConContractCashflow> depositCashflows = oldCashflows.stream().filter(item->item.getTimes().equals(ccrTimes)&&item.getCfItem().equals(52L)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(depositCashflows)){
            HlsCusConContractCashflow depositCashflow =  depositCashflows.get(0);
            if (depositCashflow != null){
                if (DEPOSIT_PERIOD_FINAL_RETURN.equals(depositDeduction)){
                    //保证金退还：直接改日期即可
                    depositCashflow.setDueDate(lastCalcDate);
                    depositCashflow.setFinIncomeDate(lastCalcDate);
                    depositCashflow.setCalcDate(lastCalcDate);
                    newCashflows.add(depositCashflow);
                }else if (DEPOSIT_PERIOD_FINAL_DEDUCTIBLE.equals(depositDeduction)){


                    //保证金抵扣：从最后一期开始分摊，若保证金覆盖完最后一期租金仍有剩余，则向上对前一期租金进行分摊，直到分摊完；
                    //  两种情况：1.可以分摊完   2.分摊到变更起始期，剩余保证金仍大于当期租金，则把剩余保证金加到最后一期上

                    //剩余保证金:初始值等于保证金
                    Double surplusDeposit = deposit;
                    if (totalSurplusDeposit > 0){
                        //2.分摊到变更起始期，剩余保证金仍大于当期租金，则把剩余保证金加到最后一期上
                        //剩余保证金重新赋值：初始保证金 - 多出来的
                        surplusDeposit =  MathUtil.sub(deposit, totalSurplusDeposit);
                        for (int j = Math.toIntExact(ccrTimes); j >= ccrStartTimes; j--) {
                            Long currentTime = (long) j;
                            HlsCusConContractCashflow currentRentCashflow = newCashflows.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);

                            Double currentDueAmount = currentRentCashflow.getDueAmount();

                            HlsCusConContractCashflow newDepositCashflow = new HlsCusConContractCashflow();
                            BeanUtil.copyProperties(depositCashflow,newDepositCashflow);
                            newDepositCashflow.setCashflowId(null);
                            newDepositCashflow.setTimes(currentRentCashflow.getTimes());
                            newDepositCashflow.setCalcDate(currentRentCashflow.getCalcDate());
                            newDepositCashflow.setFinIncomeDate(currentRentCashflow.getFinIncomeDate());
                            newDepositCashflow.setDueDate(currentRentCashflow.getDueDate());
                            newDepositCashflow.setOutstandingAmount(currentRentCashflow.getOutstandingAmount());
                            newDepositCashflow.setCashflowIrr(currentRentCashflow.getCashflowIrr());


                            if (surplusDeposit > currentDueAmount){
                                surplusDeposit = MathUtil.sub(surplusDeposit, currentDueAmount);
                                if (currentTime == ccrTimes){
                                    //最后一期加上剩余的保证金
                                    newDepositCashflow.setDueAmount(MathUtil.add(currentDueAmount,totalSurplusDeposit));
                                }else {
                                    newDepositCashflow.setDueAmount(currentDueAmount);
                                }
                                newCashflows.add(newDepositCashflow);
                            }else {
                                newDepositCashflow.setDueAmount(surplusDeposit);
                                newCashflows.add(newDepositCashflow);
                            }
                        }

                    }else {
                        //1.可以分摊完
                        for (int j = Math.toIntExact(ccrTimes); j >= ccrStartTimes; j--) {
                            Long currentTime = (long) j;
                            HlsCusConContractCashflow currentRentCashflow = newCashflows.stream().filter(item->item.getTimes().equals(currentTime)&&item.getCfItem().equals(1L)).collect(Collectors.toList()).get(0);

                            Double currentDueAmount = currentRentCashflow.getDueAmount();

                            HlsCusConContractCashflow newDepositCashflow = new HlsCusConContractCashflow();
                            BeanUtil.copyProperties(depositCashflow,newDepositCashflow);
                            newDepositCashflow.setCashflowId(null);
                            newDepositCashflow.setTimes(currentRentCashflow.getTimes());
                            newDepositCashflow.setCalcDate(currentRentCashflow.getCalcDate());
                            newDepositCashflow.setFinIncomeDate(currentRentCashflow.getFinIncomeDate());
                            newDepositCashflow.setDueDate(currentRentCashflow.getDueDate());
                            newDepositCashflow.setOutstandingAmount(currentRentCashflow.getOutstandingAmount());
                            newDepositCashflow.setCashflowIrr(currentRentCashflow.getCashflowIrr());

                            if (surplusDeposit > currentDueAmount){
                                surplusDeposit = MathUtil.sub(surplusDeposit, currentDueAmount);
                                newDepositCashflow.setDueAmount(currentDueAmount);
                                newCashflows.add(newDepositCashflow);

                            }else {
                                newDepositCashflow.setDueAmount(surplusDeposit);
                                newCashflows.add(newDepositCashflow);
                                break;
                            }
                        }
                    }

                }
            }
        }


        //保留原名义货价现金流
        List<HlsCusConContractCashflow> priceCashflows = oldCashflows.stream().filter(item->item.getTimes().equals(ccrTimes)&&item.getCfItem().equals(8L)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(priceCashflows)){
            HlsCusConContractCashflow priceCashflow = priceCashflows.get(0);
            priceCashflow.setDueDate(lastCalcDate);
            priceCashflow.setCalcDate(lastCalcDate);
            priceCashflow.setFinIncomeDate(lastCalcDate);
            newCashflows.add(priceCashflow);
        }

        return newCashflows;
    }

    //生成手续费现金流
    private HlsCusConContractCashflow calculateNewCashflowLeaseCharge(HlsCusConContractCashflow rentCashflow, Double leaseCharge){
        HlsCusConContractCashflow leaseChargeCashflow = new HlsCusConContractCashflow();
        BeanUtil.copyProperties(rentCashflow,leaseChargeCashflow);

        //获取手续费税率
        Double vatRate = getLeaseChargeVatRate(rentCashflow.getContractId());

        Double vatDueAmount = MathUtil.div(MathUtil.mul(leaseCharge,vatRate,13),1+vatRate,2);
        Double netDueAmount = MathUtil.sub(leaseCharge,vatDueAmount,2);
        leaseChargeCashflow.setCashflowId(null);
        leaseChargeCashflow.setCfItem(3L);
        leaseChargeCashflow.setCfType(3L);
        leaseChargeCashflow.setCfDirection("INFLOW");
        leaseChargeCashflow.setDueAmount(leaseCharge);
        leaseChargeCashflow.setNetDueAmount(netDueAmount);
        leaseChargeCashflow.setVatDueAmount(vatDueAmount);
        leaseChargeCashflow.setPrincipal(0D);
        leaseChargeCashflow.setNetPrincipal(0D);
        leaseChargeCashflow.setVatPrincipal(0D);
        leaseChargeCashflow.setInterest(0D);
        leaseChargeCashflow.setNetInterest(0D);
        leaseChargeCashflow.setVatInterest(0D);
        leaseChargeCashflow.setOutstandingPrincipal(rentCashflow.getOutstandingPrincipal());
        return leaseChargeCashflow;
    }


    //根据合同ID获取手续费税率
    public Double getLeaseChargeVatRate(Long contractId) {
        HlsCusConContract cusConContract = new HlsCusConContract();
        cusConContract.setContractId(contractId);
        cusConContract = hlsCusConContractMapper.selectByPrimaryKey(cusConContract);
        String invoiceProfile = cusConContract.getBusinessType() + "_" + cusConContract.getLeaseItemProperty() + "S";
        HlsInvoiceProfileDtl invoiceProfileDtl = new HlsInvoiceProfileDtl();
        invoiceProfileDtl.setCfItem(3L);
        invoiceProfileDtl.setInvoiceProfile(invoiceProfile);
        invoiceProfileDtl.setEnabledFlag("Y");
        invoiceProfileDtl = invoiceProfileDtlMapper.selectOne(invoiceProfileDtl);
        FndSysCodes taxTypeCode = new FndSysCodes();
        taxTypeCode.setTaxTypeCode(invoiceProfileDtl.getTaxTypeCode());
        taxTypeCode.setEnabledFlag("Y");
        taxTypeCode = taxTypeCodesMapper.selectOne(taxTypeCode);
        return taxTypeCode.getTaxTypeRate();
    }

    //计算本金分摊-期限不变
    public List<HlsCusConContractCashflow> calculateNewCashflowEqual(List<HlsCusConContractCashflow> oldCashflows,String oldPriceList,String ccrPriceList,JSONObject configJson,Long documentId,Long quotationId) throws Exception{
        List<HlsCusConContractCashflow> newCashflows = new ArrayList<>();
        if("GECALCULATOR_PMT_YH".equals(oldPriceList)){
            newCashflows = calculateNewCashflowEqualRental(oldCashflows,oldPriceList,ccrPriceList,configJson,documentId,quotationId);
        }else if("GECALCULATOR_LP_YH".equals(oldPriceList)){
            newCashflows = calculateNewCashflowEqualPrincipal(oldCashflows,oldPriceList,ccrPriceList,configJson,documentId,quotationId);
        }else{
            throw new HlsCusException("暂不支持该报价发起本金分摊-期限不变");
        }
        return newCashflows;
    }

    public void updateQuotation(Long historyId, String modifiedCells, String ccrPriceList, Long quotationId, boolean autoSave) throws Exception {
        SysDocumentHistoryDetail detail = queryDetail(historyId, quotationId);
        if (detail == null) {
            throw new RuntimeException("Can not get a unique SysDocumentHistoryDetail record");
        }
        JSONObject data = getData(detail);
        String sourceSheet;
        String sheets;
        String priceList;
        if (StringUtils.isNotEmpty(ccrPriceList)) {
            //变更报价
            HlsCalcConfig config = priceListMapper.selectByPrimaryKey(ccrPriceList);
            sourceSheet = config.getSheets();
            priceList = ccrPriceList;
            //解压压缩过的sheets
            String stringSheets = GzipUtil.atob(sourceSheet);
            String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
            sheets = URLDecoder.decode(unzipSheets,"utf-8");
        } else {
            sourceSheet = getSheetDataFromHistory(detail.getHistoryDetailId());
            priceList = getPriceList(data);
            sheets = sourceSheet;
        }
        if (StringUtils.isEmpty(sheets)) {
            throw new RuntimeException("Can not get sheets from DocumentHistoryDetail");
        }
        if (StringUtils.isEmpty(priceList)) {
            throw new RuntimeException("Can not get priceList from DocumentHistoryDetail");
        }

        //将价目表转成JSONArray
        JSONArray array = JSONArray.parseArray(sheets);
        JSONObject sheetObject = array.getJSONObject(0);
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        //将sheetObject写入sheet
        //readSheets(wb, array);
        readSheet(wb, sheet, sheetObject);

        if (StringUtils.isNotEmpty(ccrPriceList)) {
            String oldPriceList = getPriceList(data);
            if (StringUtils.isEmpty(oldPriceList)) {
                throw new RuntimeException("Can not get priceList from DocumentHistoryDetail");
            }
            String oldSheets = getSheetDataFromHistory(detail.getHistoryDetailId());
            JSONArray oldModifiedCells = extractHeadDataFromSheet(oldSheets, oldPriceList);

            //updateSheet(oldModifiedCells, data, wb, ccrPriceList);
            updateSheet(oldModifiedCells, data, sheet, ccrPriceList);
            JSONArray oldLines = extractLineDataFromSheet(oldSheets, oldPriceList);
            //updateSheetLines(oldLines, data, wb, ccrPriceList);
            updateSheetLines(oldLines, data, sheet, ccrPriceList);
        }

        JSONArray transArray = getTransObject(modifiedCells);
        List<HlsPriceListConfigLn> hlsPriceListConfigLns = getPriceListConfigLn(priceList, PRICE_TYPE_SINGLE).getHlsPriceListConfigLns();
        setCellFormat(transArray, hlsPriceListConfigLns);

        updateSheet(transArray, data, sheet, priceList);
        writeBack(wb, sheet, sheetObject, priceList);
        //updateSheet(transArray, data, wb, priceList);
        //writeBack(wb, array, priceList);
        // 更新回原纪录detail以及blob上
        updateQuotationHistoryData(detail.getHistoryDetailId(), priceList, array);

        if (autoSave) {
            String sheetsArray = encodeURIComponent((JSON.toJSONString(array)));
            String zipSheets = new String(GzipUtil.compress(sheetsArray),"iso-8859-1");
            String compressSheets = GzipUtil.btoa(zipSheets);

            HlsCusPrjQuotation quotation = JSONObject.parseObject(JSON.toJSONString(data), HlsCusPrjQuotation.class);
            quotation.setSheets(JSON.toJSONString(array));
            quotation.setCompressSheets(compressSheets);
            this.PrjQuotationSubmit(RequestHelper.getCurrentRequest(true), quotation);
        }
    }

    public void setCellFormat(JSONArray transArray, List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
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

    public void updateDocumentHistoryCashflow(IRequest iRequest, Long documentId, String documentCategory, Long quotationId) throws Exception {

        JSONObject prjQuotation = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "prj_quotation").get(0).getJSONObject("data");

        JSONObject changeReq = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "con_contract_change_req").get(0).getJSONObject("data");
        JSONObject contract = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "con_contract").get(0).getJSONObject("data");
        //        Long ccrStartTimes = changeReq.getLong("ccr_start_times") == null ? 0L : changeReq.getLong("ccr_start_times");

        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentCategory(documentCategory);
        history.setDocumentId(documentId);
        Long historyId = documentHistoryMapper.selectDocumentHistory(documentCategory, documentId, null).getHistoryId();

        List<JSONObject> contractCashflows = getOldContractCashFlows(iRequest, documentId, documentCategory, quotationId);

        String priceList = prjQuotation.getString("price_list");
        JSONArray sheets = prjQuotation.getJSONArray("sheets");
//        JSONArray rows = sheets.getJSONObject(0).getJSONArray("rows");

        Map<String, String> map = hlsCalcExcelImportUtilService.getCalcHdMap(iRequest, sheets.toJSONString(), priceList);

        //开始变更的期数
        Long ccrStartTimes = map.get("ccrStartTimes") == null ? 0L : Math.round(Double.parseDouble(map.get("ccrStartTimes")));
        //变更后总期数
        //Double leaseTimes = map.get("leaseTimes") == null || map.get("annualPayTimes") == null ? prjQuotation.getDouble("lease_times") : MathUtil.mul(Double.valueOf(map.get("leaseTimes")), Double.valueOf(map.get("annualPayTimes")));
        Double leaseTimes = Double.valueOf(map.get("leaseTimes"));

        Double ccrTimes = map.get("ccrLeaseTimes") == null ? leaseTimes : Double.valueOf(map.get("ccrLeaseTimes"));

        SysDocumentHistory changeHistory = documentHistoryMapper.selectDocumentHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, null);
        SysDocumentHistoryDetail sysDocumentHistoryDetail = new SysDocumentHistoryDetail();
        sysDocumentHistoryDetail.setHistoryId(changeHistory.getHistoryId());
        sysDocumentHistoryDetail.setTableName("con_contract_change_req");
        sysDocumentHistoryDetail.setTablePkValue(String.valueOf(documentId));
        List<SysDocumentHistoryDetail> detailList = sysDocumentHistoryDetailMapper.select(sysDocumentHistoryDetail);
        sysDocumentHistoryDetail = detailList.get(0);

        String historyData = sysDocumentHistoryDetail.getHistoryData();
        JSONObject historyJsonObject = JSON.parseObject(historyData);

        HlsCusConContractChangeReq hlsCusConContractChangeReq = contractChangeReqMapper.selectByPrimaryKey(documentId);
        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractChangeReq.getContractId());
        HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusConContract.getQuotationId());
        Double changeTerm = MathUtil.div(MathUtil.mul(ccrTimes,12L),hlsCusPrjQuotation.getAnnualPayTimes(),0);

        if(changeReq.getString("change_type").equals("DELAY")){
            HlsCusConContract conContract = hlsCusConContractMapper.selectByPrimaryKey(contract.get("contract_id"));
            YhFactoryDelayTerm yhFactoryDelayTerm = new YhFactoryDelayTerm();
            List<YhFactoryDelayTerm> yhFactoryDelayTermList1 = yhFactoryDelayTermMapper.yhFactoryDelayTermQuery(null,conContract.getFactoryId(),null,null);
            if(yhFactoryDelayTermList1.size() == 0L){
                List<YhFactoryDelayTerm> yhFactoryDelayTermList2 = yhFactoryDelayTermMapper.yhFactoryDelayTermQuery(conContract.getManufacturerId(),null,null,null);
                if(yhFactoryDelayTermList2.size() > 0L){
                    yhFactoryDelayTerm = yhFactoryDelayTermList2.get(0);
                }
            }else{
                yhFactoryDelayTerm = yhFactoryDelayTermList1.get(0);
            }

            if(yhFactoryDelayTerm.getTermId() != null){
                if(yhFactoryDelayTerm.getMaxTerm() != null&&changeTerm.longValue() > yhFactoryDelayTerm.getMaxTerm()){
                    throw new HlsCusException("延期后合同期限（月）必须小于或等于合同主机厂最长期限（月）！");
                }
            }
        }

        historyJsonObject.put("change_times", ccrTimes.longValue());
        historyJsonObject.put("cange_term", changeTerm.longValue());

        sysDocumentHistoryDetail.setHistoryData(historyJsonObject.toJSONString());
        sysDocumentHistoryDetailMapper.updateByPrimaryKeySelective(sysDocumentHistoryDetail);

        hlsCusConContractChangeReq.setChangeTimes(ccrTimes.longValue());
        hlsCusConContractChangeReq.setChangeTerm(changeTerm.longValue());
        contractChangeReqMapper.updateByPrimaryKey(hlsCusConContractChangeReq);

        List<Object> list = hlsCalcExcelImportUtilService.getExcelToCalcLnTable(iRequest, sheets.toJSONString(), priceList, "cont", ccrTimes.intValue(), "CONTRACT");

        for (JSONObject item : contractCashflows) {
            Long id = item.getLong("id");
            JSONObject data = item.getJSONObject("data");
            if (data.getLong("times") >= ccrStartTimes) {
                //            if (data.getLong("times") >= 0) {
                if ("insert".equals(data.getString("_status"))) {
                    sysDocumentHistoryDetailMapper.deleteByPrimaryKey(id);
                } else if (!"delete".equals(data.getString("_status"))) {
                    data.put("_status", "delete");
                    sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, documentCategory, id, data.toJSONString());
                }
            }
        }

        List<Map> cfDirections = sysCodeValueMapper.queryCodeDetails("HLS.CASHFLOW_DIRECTION");
        List<HlsCashflowItem> hlsCashflowItems = cfItemMapper.selectAll();

        for (Object o : list) {

            JSONObject jsonObject = JSON.parseObject(JsonUtils.toSnakeJsonString(o));

            //插入现金流
            if (jsonObject.getLong("times") >= ccrStartTimes) {
//            if (jsonObject.getLong("times") >= 0) {
                jsonObject.put("contract_seq",prjQuotation.getString("contract_seq"));
                jsonObject.put("generated_source", ConContractCashflow.SOURCE_PRJ_QUOTATION);
                jsonObject.put("generated_source_doc_id", quotationId);
                jsonObject.put("cf_status", "RELEASE");
                jsonObject.put("cf_status_n", "下达");
                jsonObject.put("contract_id", changeReq.getLong("contract_id"));
                jsonObject.put("contract_status", contract.getString("contract_status"));
                jsonObject.put("write_off_flag", "NOT");
                jsonObject.put("write_off_flag_n", "未核销");
                jsonObject.put("outstanding_principalt", jsonObject.get("outstanding_principalt"));
                jsonObject.put("billing_status", "NOT");
                jsonObject.put("overdue_status", "N");
                jsonObject.put("penalty_process_status", "N");
                jsonObject.put("calc_date", jsonObject.get("calc_date")== null? jsonObject.get("due_date") : jsonObject.get("calc_date"));
                jsonObject.put("due_date", jsonObject.get("due_date") == null? jsonObject.get("calc_date") : jsonObject.get("due_date"));
                jsonObject.put("fin_income_date", jsonObject.get("due_date") == null? jsonObject.get("calc_date") : jsonObject.get("due_date"));
                jsonObject.put("quotation_id", quotationId);
                jsonObject.put("change_flag", "Y");
                jsonObject.put("_status", "insert");
                cfDirections.forEach(item -> {
                    if (item.get("code_value").equals(jsonObject.get("cf_direction").toString())) {
                        jsonObject.put("cf_direction_n", item.get("meaning"));
                    }
                });
                hlsCashflowItems.forEach(item -> {
                    if (item.getCfItem().trim().equals(jsonObject.get("cf_item").toString())) {
                        jsonObject.put("cf_item_n", item.getDescription());
                    }
                });

                SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
                detail.setParentTableName("prj_quotation");
                detail.setParentPkValue(String.valueOf(quotationId));
                detail.setTableName("con_contract_cashflow");
                detail.setTablePkValue(UUID.randomUUID().toString());
                detail.setHistoryData(jsonObject.toJSONString());
                detail.setHistoryId(historyId);
                sysDocumentHistoryDetailMapper.insertSelective(detail);
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


    public JSONArray extractLineDataFromSheet(String sheets, String priceList) {
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
                String columnName = configLn.getColumnName().toLowerCase();
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

    public JSONArray extractHeadDataFromSheet(String sheets, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLn(priceList);
        JSONArray array = new JSONArray();
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        readSheet(wb, sheet, JSONArray.parseArray(sheets).getJSONObject(0), true);
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        for (HlsPriceListConfigLn ln : lns) {
            JSONObject jsonObject = new JSONObject();
            String columnCode = ln.getColumnCode();
            //价目表字段转换为小写，前台配置很多字段大小写不一致
            String columnName = ln.getColumnName().toLowerCase();

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

    private List<HlsPriceListConfigLn> getPriceListConfigLns(String priceList, String type){
        HlsPriceListConfigLn ln = new HlsPriceListConfigLn();

        ln.setTableType(type);
        ln.setPriceList(priceList);
        List<HlsPriceListConfigLn> listConfigLns = configLnMapper.selectHlsPriceListConfiglineByPriceList(ln);
        return listConfigLns;
    }

    public HlsPriceListConfigHd getPriceListConfigLn(String priceList, String type) {
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
        hd.setHlsPriceListConfigLns(configLnMapper.selectHlsPriceListConfiglineByPriceList(ln));
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

    public void writeBack(XSSFWorkbook wb, XSSFSheet sheet, JSONObject sheetObject, String priceList) {
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
//                String rawValue = getRawValue(cell);
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
            JSONObject rowObject = getRowObject(rowsObject, i);
            if (rowObject == null) {
                continue;
            }
            JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
            for (int j = 0; j < cellsObject.size(); j++) {
                XSSFCell cell = sheet.getRow(i).getCell(cellsObject.getJSONObject(j).getIntValue(KEY_INDEX));
                if (cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    CellValue cellValue;
                    Object rawValue;
                    try {
                        cellValue = evaluator.evaluate(cell);
                        rawValue = getRawValue(cellValue);
                    } catch (Exception e) {
                        throw new RuntimeException("变更后报价计算错误" + cell.getReference());
                    }
                    cellsObject.getJSONObject(j).put(KEY_VALUE, rawValue);
                }
            }
        }
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
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, PRICE_TYPE_MULTI_LINE);

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

    protected Object getRawValue(XSSFCell cell) {
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

    protected Object getRawValue(CellValue cell) {
        Object rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = cell.getNumberValue();
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
        detail.setTableName("prj_quotation");
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

    public void updateSheet(JSONArray cells, Map<String, Object> data, XSSFSheet sheet, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLn(priceList);
        Map<String, HlsPriceListConfigLn> lnMap = new HashMap<>();
        for (HlsPriceListConfigLn ln : lns) {
            lnMap.put(ln.getColumnName().toLowerCase(), ln);
        }
        for (int i = 0; cells != null && i < cells.size(); i++) {
            JSONObject row = cells.getJSONObject(i);
            String fieldName = row.getString(KEY_FIELD).toLowerCase();
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

    public void updateSheetLines(JSONArray lines, Map<String, Object> data, XSSFSheet sheet, String priceList) {
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

    public void updateSheetLines(JSONArray lines, Map<String, Object> data, XSSFWorkbook wb, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLns(priceList,PRICE_TYPE_SINGLE);
        HlsPriceListConfigLn singleLine = lns.get(0);
        XSSFSheet sheet = wb.getSheet(singleLine.getSheetName());

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
                Object value = dataObject.get(KEY_VALUE);
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
        /*if (StringUtils.isEmpty(value.toString())) {
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
        }*/

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
//                logger.debug("类型异常{}", e);
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
     * 行下标从0开始
     * 列下标从0开始
     *
     * @param position
     * @return
     */
    public CellPosition parsePosition(String position) {
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
                //列定位BUG修复
                value = (value + 1) * 26;
            }
            cellIndex += value;
        }
        cellPosition.setRowIndex(Integer.valueOf(rowString) - 1);
        cellPosition.setCellIndex(cellIndex);
        cellPosition.setCellPosition(position);
        return cellPosition;
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

    public void readSheet(XSSFWorkbook wb, XSSFSheet sheet, JSONObject jsonObject) {
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
                    setCellValue(cell, value, rowCell);
                }
            }
        }
    }

    class CellPosition {

        private int rowIndex = -1;
        private int cellIndex = -1;

        private String cellPosition;

        public String getCellPosition() {
            return cellPosition;
        }

        public void setCellPosition(String cellPosition) {
            this.cellPosition = cellPosition;
        }

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

    public HlsCusPrjQuotation updatePrjQutationProfile(HlsCusPrjQuotation prjQuotationDto, IRequest iRequest) {
        return null;
    }

    /**
     * 根据报价 CODE获取 EXCEL 中的行信息并封装到指定类型
     *
     * @param priceList 报价 CODE
     * @param lines     行
     * @param <T>       指定实体类
     * @return 实体类列表
     */
    public <T> List<T> getRowsByPriceList(String priceList, JSONObject lines, Class<T> clazz) {
        List<T> answer = new ArrayList<>();
        List<Map<String, Object>> rowsByPriceList = getRowsByPriceList(priceList, lines);
        for (Map<String, Object> stringObjectMap : rowsByPriceList) {
            try {
                T ta = BeanUtil.fillBeanWithMap(stringObjectMap, clazz.newInstance(), true, CopyOptions.create());
                answer.add(ta);
            } catch (Exception e) {
                logger.error("error when fill bean");
            }
        }
        return answer;
    }

    /**
     * 根据报价 CODE获取 EXCEL 中的行信息并封装到Map
     *
     * @param priceList 报价 CODE
     * @param lines     行
     * @return Map列表
     */
    public List<Map<String, Object>> getRowsByPriceList(String priceList, JSONObject lines) {
        HlsPriceListConfigHd hd = getPriceListConfigLn(priceList, PRICE_TYPE_MULTI_LINE);
        String multiLineFrom = hd.getMultiLineFrom();
        String multiLineTo = hd.getMultiLineTo();
        List<HlsPriceListConfigLn> singleLines = hd.getHlsPriceListConfigLns();
        if (StringUtils.isEmpty(multiLineFrom)) {
            logger.warn("Found empty multiLineFrom", multiLineFrom);
            return null;
        }
        if (CollectionUtils.isEmpty(singleLines)) {
            logger.warn("Found empty HlsPriceListConfigLn of multi-line");
            return null;
        }
        JSONArray rowsObject = lines.getJSONArray(KEY_ROWS);
        Integer from = Integer.valueOf(multiLineFrom);
        Integer to = Integer.valueOf(multiLineTo);
        List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();
        List<Map<String, Object>> valueList = new ArrayList<>();
        for (Integer i = from; i <= to; i++) {
            Map<String, Object> cellMap = new HashMap<>();

            for (HlsPriceListConfigLn ln : configLns) {
                String columnCode = ln.getColumnCode();
                CellPosition cellPosition = parsePosition(columnCode + i);
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
                for (int cellIndex = 0; cellIndex < cellsObject.size(); cellIndex++) {
                    JSONObject cellObject = cellsObject.getJSONObject(cellIndex);
                    int intValue = cellObject.getIntValue(KEY_INDEX);
                    if (intValue == cellPosition.getCellIndex()) {
                        Object value = cellObject.get("value");
                        if (value != null && !value.toString().equals("")) {
                            if (ArraysUtil.contains(DOUBLE_FORMATS, cellObject.getString(KEY_FORMAT))) {
                                try {
                                    cellMap.put(ln.getColumnName(), Double.valueOf(Double.parseDouble(value.toString())));
                                } catch (Exception e) {
                                    cellMap.put(ln.getColumnName(), value.toString());
                                }
                            } else if (ArraysUtil.contains(DATE_FORMATS, cellObject.getString(KEY_FORMAT))) {
                                if (NumberUtils.isNumber(value.toString())) {
                                    cellMap.put(ln.getColumnName(), HlsBeanRefUtilServiceImpl.parseDate(String.valueOf(Math.round(Double.parseDouble(value.toString())))));
                                } else {
                                    if (value instanceof Date) {
                                        cellMap.put(ln.getColumnName(), (Date) value);
                                    } else {
                                        cellMap.put(ln.getColumnName(), HlsBeanRefUtilServiceImpl.parseDate(value.toString()));
                                    }

                                }
                            } else {
                                if (NumberUtils.isNumber(value.toString())) {
                                    cellMap.put(ln.getColumnName(), Double.valueOf(value.toString()));
                                } else {
                                    cellMap.put(ln.getColumnName(), value.toString());
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if (MapUtils.isNotEmpty(cellMap)) {
                valueList.add(cellMap);
            }
        }
        return valueList;
    }



    public void contractChangeCalcCashflow(IRequest iRequest,Long documentId,String documentCategory,Long quotationId,
                                           Long historyId,JSONArray config,String ccrPriceList) throws Exception {
        SysDocumentHistoryDetail detail = queryDetail(historyId, quotationId);
        if (detail == null) {
            throw new HlsCusException("无法获取原报价信息！");
        }
        JSONObject data = getData(detail);
        String oldPriceList;
        if (StringUtils.isNotEmpty(ccrPriceList)) {
            oldPriceList = getPriceList(data);
            if (StringUtils.isEmpty(oldPriceList)) {
                throw new HlsCusException("无法获取报价方案！");
            }
        } else {
            throw new HlsCusException("报价方案不可为空！");
        }

        //获取前台传入变更参数
        JSONObject configJson = new JSONObject();
        for(int i=0;i < config.size();i++){
            JSONObject configDetail = config.getJSONObject(i);
            if("change_start_date".equals(configDetail.getString("field"))){
                configJson.put("changeStartDate",configDetail.getDate("value"));
            }else if("change_principal".equals(configDetail.getString("field"))){
                configJson.put("changePrincipal",configDetail.getDouble("value"));
            }else if("ccr_start_times".equals(configDetail.getString("field"))){
                configJson.put("ccrStartTimes",configDetail.getDouble("value"));
            }else if("times".equals(configDetail.getString("field"))){
                configJson.put("ccrTimes",configDetail.getDouble("value"));
            }else if("lease_charge".equals(configDetail.getString("field"))){
                //手续费
                configJson.put("leaseCharge",configDetail.getDouble("value"));
            }

        }

        //计算需要变更期数
        configJson.put("ccrDelayTimes",configJson.getLong("ccrTimes") - configJson.getLong("ccrStartTimes")  + 1);
        //提前还本标识
        configJson.put("partialPrepaymentFlag","Y");


        HlsCusConContractChangeReq hlsCusConContractChangeReq = contractChangeReqMapper.selectByPrimaryKey(documentId);
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(hlsCusConContractChangeReq.getContractId());
        List<HlsCusConContractCashflow> oldCashflows = hlsCusConContractCashflowMapper.select(hlsCusConContractCashflow);


        //获取本金分摊-期限不变变更后现金流
        List<HlsCusConContractCashflow> newCashflows = calculateNewCashflowEqual(oldCashflows,oldPriceList,ccrPriceList,configJson,documentId,quotationId);

        JSONObject prjQuotation = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "prj_quotation").get(0).getJSONObject("data");
        JSONObject contract = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "con_contract").get(0).getJSONObject("data");
        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentCategory(documentCategory);
        history.setDocumentId(documentId);

        SysDocumentHistory changeHistory = documentHistoryMapper.selectDocumentHistory(DOCUMENT_CATEGORY_CONTRACT_CHANGE, documentId, null);
        SysDocumentHistoryDetail sysDocumentHistoryDetail = new SysDocumentHistoryDetail();
        sysDocumentHistoryDetail.setHistoryId(changeHistory.getHistoryId());
        sysDocumentHistoryDetail.setTableName("con_contract_change_req");
        sysDocumentHistoryDetail.setTablePkValue(String.valueOf(documentId));
        List<SysDocumentHistoryDetail> detailList = sysDocumentHistoryDetailMapper.select(sysDocumentHistoryDetail);
        sysDocumentHistoryDetail = detailList.get(0);

        String historyData = sysDocumentHistoryDetail.getHistoryData();
        JSONObject historyJsonObject = JSON.parseObject(historyData);

        HlsCusConContract hlsCusConContract = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractChangeReq.getContractId());
        HlsCusPrjQuotation hlsCusPrjQuotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(hlsCusConContract.getQuotationId());
        Double changeTerm = MathUtil.div(MathUtil.mul(configJson.getLong("ccrTimes"),12L),hlsCusPrjQuotation.getAnnualPayTimes(),0);

        HlsCusConContract conContract = hlsCusConContractMapper.selectByPrimaryKey(contract.get("contract_id"));


        historyJsonObject.put("change_times", configJson.getLong("ccrTimes"));
        historyJsonObject.put("change_term", changeTerm.longValue());
        historyJsonObject.put("change_principal", configJson.getDouble("changePrincipal"));
        historyJsonObject.put("lease_charge", configJson.getDouble("leaseCharge"));

        sysDocumentHistoryDetail.setHistoryData(historyJsonObject.toJSONString());
        sysDocumentHistoryDetailMapper.updateByPrimaryKeySelective(sysDocumentHistoryDetail);

        hlsCusConContractChangeReq.setChangeTimes(configJson.getLong("ccrTimes"));
        hlsCusConContractChangeReq.setChangeTerm(changeTerm.longValue());
        hlsCusConContractChangeReq.setChangePrincipal(configJson.getDouble("changePrincipal"));
        hlsCusConContractChangeReq.setCcrStartTimes(configJson.getLong("ccrStartTimes"));
        contractChangeReqMapper.updateByPrimaryKey(hlsCusConContractChangeReq);


        List<JSONObject> contractCashflows = getOldContractCashFlows(iRequest, documentId, documentCategory, quotationId);

        //删除变更前现金流中，变更起始期数之后部分
        for (JSONObject item : contractCashflows) {
            Long id = item.getLong("id");
            JSONObject itemData = item.getJSONObject("data");
//          fixme 变更开始期数
            if (itemData.getLong("times") >= configJson.getLong("ccrStartTimes")) {
                //            if (data.getLong("times") >= 0) {
                if ("insert".equals(itemData.getString("_status"))) {
                    sysDocumentHistoryDetailMapper.deleteByPrimaryKey(id);
                } else if (!"delete".equals(itemData.getString("_status"))) {
                    itemData.put("_status", "delete");
                    sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, documentCategory, id, itemData.toJSONString());
                }

            }
        }

        List<Map> cfDirections = sysCodeValueMapper.queryCodeDetails("HLS.CASHFLOW_DIRECTION");
        List<Map> cfStatus = sysCodeValueMapper.queryCodeDetails("CON.CF_STATUS");
        List<Map> writeOffFlags = sysCodeValueMapper.queryCodeDetails("CON.CASHFLOW_WRITE_OFF_FLAG");
        List<HlsCashflowItem> hlsCashflowItems = cfItemMapper.selectAll();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Comparator<HlsCusConContractCashflow> byTimesAsc = Comparator.comparing(HlsCusConContractCashflow::getTimes);
        Comparator<HlsCusConContractCashflow> byCfItemAsc = Comparator.comparing(HlsCusConContractCashflow::getCfItem);
        Comparator<HlsCusConContractCashflow> finalComparator = byTimesAsc.thenComparing(byCfItemAsc);
        List<HlsCusConContractCashflow> newCashflowsSorted = newCashflows.stream().sorted(finalComparator).collect(Collectors.toList());
        //新增变更后现金流中，变更起始期数之后部分
        for (HlsCusConContractCashflow newCashflow : newCashflowsSorted) {
            //插入现金流
//           fixme 变更开始期数
            if (newCashflow.getTimes() >= configJson.getLong("ccrStartTimes")&&newCashflow.getDueAmount() > 0D) {
                JSONObject newCashflowObject = new JSONObject();
                newCashflowObject.put("unreceived_principal",newCashflow.getUnreceivedPrincipal());
                newCashflowObject.put("contract_id",newCashflow.getContractId());
                newCashflowObject.put("vat_principal",newCashflow.getVatPrincipal());
                newCashflowObject.put("_tls",newCashflow.get__tls());
                newCashflowObject.put("cf_direction",newCashflow.getCfDirection());
                //newCashflowObject.put("surplus_received_principal",newCashflow.getSurplusReceivedPrincipal());
                newCashflowObject.put("calc_date",format.format(newCashflow.getCalcDate()));
                newCashflowObject.put("cf_type",newCashflow.getCfType());
                newCashflowObject.put("cf_status",newCashflow.getCfStatus());
                newCashflowObject.put("unreceived_amount",newCashflow.getUnreceivedAmount());
                newCashflowObject.put("generated_source_doc_id",newCashflow.getGeneratedSourceDocId());
                newCashflowObject.put("net_due_amount",newCashflow.getNetDueAmount());
                newCashflowObject.put("vat_interest",newCashflow.getVatInterest());
                newCashflowObject.put("net_interest",newCashflow.getNetInterest());
                newCashflowObject.put("write_off_flag",newCashflow.getWriteOffFlag());
                newCashflowObject.put("principal_reply",newCashflow.getPrincipalReply());
                newCashflowObject.put("query_times",newCashflow.getQueryTimes());
                //newCashflowObject.put("print_notice_flag",newCashflow.getPrintNoticeFlag());
                newCashflowObject.put("unreceived_interest",newCashflow.getUnreceivedInterest());
                newCashflowObject.put("change_flag",newCashflow.getChangeFlag());
                newCashflowObject.put("program_id",newCashflow.getProgramId());
                newCashflowObject.put("received_amount",newCashflow.getReceivedAmount());
                newCashflowObject.put("reply_fee_interest",newCashflow.getReplyFeeInterest());
                newCashflowObject.put("outstanding_principal",newCashflow.getOutstandingPrincipal());
                newCashflowObject.put("net_principal",newCashflow.getNetPrincipal());
                newCashflowObject.put("principal",newCashflow.getPrincipal());
                newCashflowObject.put("times",newCashflow.getTimes());
                newCashflowObject.put("generated_source_doc_line_id",newCashflow.getGeneratedSourceDocLineId());
                newCashflowObject.put("interest",newCashflow.getInterest());
                newCashflowObject.put("billing_status",newCashflow.getBillingStatus());
                //newCashflowObject.put("payment_apply_status",newCashflow.getPaymentApplyStatus());
                newCashflowObject.put("due_date",format.format(newCashflow.getDueDate()));
                newCashflowObject.put("fin_income_date",format.format(newCashflow.getFinIncomeDate()));
                newCashflowObject.put("quotation_id",newCashflow.getQuotationId());
                //newCashflowObject.put("version_id",newCashflow.getVersionId());
                newCashflowObject.put("cf_item",newCashflow.getCfItem());
                newCashflowObject.put("vat_due_amount",newCashflow.getVatDueAmount());
                newCashflowObject.put("cashflow_id",newCashflow.getCashflowId());
                newCashflowObject.put("token",newCashflow.get_token());
                newCashflowObject.put("penalty_process_status",newCashflow.getPenaltyProcessStatus());
                newCashflowObject.put("due_amount",newCashflow.getDueAmount());
                newCashflowObject.put("generated_source",newCashflow.getGeneratedSource());
                newCashflowObject.put("outstanding_principal_reply",newCashflow.getOutstandingPrincipalReply());
                newCashflowObject.put("due_amount_reply",newCashflow.getDueAmountReply());
                newCashflowObject.put("overdue_status",newCashflow.getOverdueStatus());
                //newCashflowObject.put("cf_direction_pic",newCashflow.getCfDirectionPic());
                newCashflowObject.put("request_id",newCashflow.getRequestId());
                newCashflowObject.put("inner_map",newCashflow.getInnerMap());
                newCashflowObject.put("contract_seq",prjQuotation.getString("contract_seq"));
                newCashflowObject.put("outstanding_principalt", newCashflowObject.get("outstandingPrincipalt"));
                newCashflowObject.put("_status", "insert");
                cfDirections.forEach(item -> {
                    if (item.get("code_value").equals(newCashflowObject.get("cf_direction").toString())) {
                        newCashflowObject.put("cf_direction_n", item.get("meaning"));
                    }
                });
                cfStatus.forEach(item -> {
                    if (item.get("code_value").equals(newCashflowObject.get("cf_status").toString())) {
                        newCashflowObject.put("cf_status_n", item.get("meaning"));
                    }
                });
                writeOffFlags.forEach(item -> {
                    if (item.get("code_value").equals(newCashflowObject.get("write_off_flag").toString())) {
                        newCashflowObject.put("write_off_flag_n", item.get("meaning"));
                    }
                });
                hlsCashflowItems.forEach(item -> {
                    if (item.getCfItem().trim().equals(newCashflowObject.get("cf_item").toString())) {
                        newCashflowObject.put("cf_item_n", item.getDescription());
                    }
                });

                SysDocumentHistoryDetail documentHistoryDetail = new SysDocumentHistoryDetail();
                documentHistoryDetail.setParentTableName("con_contract");
                documentHistoryDetail.setParentPkValue(hlsCusConContractChangeReq.getContractId().toString());
                documentHistoryDetail.setTableName("con_contract_cashflow");
                documentHistoryDetail.setTablePkValue(UUID.randomUUID().toString());
                documentHistoryDetail.setHistoryData(newCashflowObject.toJSONString());
                documentHistoryDetail.setHistoryId(historyId);
                sysDocumentHistoryDetailMapper.insertSelective(documentHistoryDetail);
            }
        }
    }
}