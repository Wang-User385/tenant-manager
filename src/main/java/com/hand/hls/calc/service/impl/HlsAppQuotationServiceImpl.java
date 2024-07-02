package com.hand.hls.calc.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.calc.service.QuotationCommon;
import com.hand.hls.gld.utils.IrrUtil;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.utils.HlsCusXirr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HlsAppQuotationServiceImpl implements QuotationCommon {

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

    private static final String sourceDocumentCategory = "APP_CALCULATE";
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;
    @Autowired
    HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;


    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String getSourceDocumentCategory() {
        return sourceDocumentCategory;
    }

    @Override
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
        prjQuotationDto.setDataClass(prjQuotation.getDataClass());
        prjQuotationDto.setFirstReleaseDate(prjQuotationDto.getLeaseStartDate());
        prjQuotationDto.setIrrAfterTax(prjQuotationDto.getIrrAfterTax());
        prjQuotationDto.setXirrPaynote(prjQuotationDto.getXirrPaynote());
        prjQuotationDto.setXirr(prjQuotationDto.getXirr());
        //为空时没有回写
        if(prjQuotationDto.getAptBillTerm() == null){
            prjQuotationDto.setAptBillTerm(" ");
        }
        if(prjQuotationDto.getLfCreditTerm() == null){
            prjQuotationDto.setLfCreditTerm(" ");
        }


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
        hlsCusPrjQuotationCashflowService.saveCalc2PrjQuotationCashflow(iRequest, prjQuotationDto);
        //更新xirr
        hlsCusPrjQuotationService.updateXirr(iRequest, prjQuotationDto);

        //插入手续费现金流
        hlsCusPrjQuotationCashflowService.calculateLeaseChargeCashflow(iRequest, prjQuotationDto);

        //计算irr
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowListJC = hlsCusPrjQuotationCashflowMapper.selectIrrQuotationJCForApp(hlsCusPrjQuotationCashflow);

        if (prjQuotationCashflowListJC.size() > 0) {
            double[] dueAmountList = new double[prjQuotationCashflowListJC.size()];
            List<Double> paymentList = new ArrayList<>();
            prjQuotationCashflowListJC.forEach(item -> {
                paymentList.add(item.getCashflowIrr());
            });

            //含税irr
            Double irr = IrrUtil.irr(paymentList);
            if(!Double.isNaN(irr) && !Double.isInfinite(irr)) {

                BigDecimal b = new BigDecimal(irr);
                irr = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                irr = irr * 12/Double.valueOf(prjQuotationDto.getRentingFrequency());
                prjQuotationDto.setIrr(irr);
            }
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest,prjQuotationDto);

        }

        return prjQuotationDto;
    }
}
