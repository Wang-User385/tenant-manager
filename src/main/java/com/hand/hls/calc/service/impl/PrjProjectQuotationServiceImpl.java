package com.hand.hls.calc.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.calc.service.QuotationCommon;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationDetailsService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import hls.core.utils.exception.HlsCusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrjProjectQuotationServiceImpl implements QuotationCommon {

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
    private static final String dataClassContractPlan = "CONTRACT_PLAN";
    private static final String sourceDocumentCategory = "PRJ_PROJECT";
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

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    HlsCusChangeReqInfoMapper hlsCusChangeReqInfoMapper;

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
        if(dataClassContractPlan.equals(prjQuotation.getDataClass())){
            prjQuotationDto.setDataClass(prjQuotation.getDataClass());
        }

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



        //判断是否租前现金流变更里点的保存，是则校验除可修改的字段(租期,投放日,基准利率)外的字段是否发送改变
        HlsCusPrjQuotation pqRecord = new HlsCusPrjQuotation();
        pqRecord.setQuotationId(prjQuotationDto.getQuotationId());
        pqRecord = prjQuotationMapper.selectByPrimaryKey(pqRecord);
        if("PRJ_PROJECT".equals(pqRecord.getSourceDocumentCategory()) && "VIRTUAL_CON".equals(pqRecord.getDataClass())){
            HlsCusPrjProject prjRecord = new HlsCusPrjProject();
            prjRecord.setProjectId(pqRecord.getSourceDocumentId());
            prjRecord = hlsCusPrjProjectMapper.selectByPrimaryKey(prjRecord);
            //判断是否变更的单据
            if("CHANGE_REQ".equals(prjRecord.getDataType()) && prjQuotationDto.getFinanceAmount() != null){
                //还款计划变更excel报价不做校验
                HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
                hlsCusChangeReqInfo.setChangeReqId(prjRecord.getChangeReqId());
                hlsCusChangeReqInfo = hlsCusChangeReqInfoMapper.selectByPrimaryKey(hlsCusChangeReqInfo);
                if("BUSINESS_CHANGE_BEFORE".equals(hlsCusChangeReqInfo.getChangeType())){
                    //对比pqRecord与hlsCusPrjQuotation校验修改的字段
                    //项目总额 FINANCE_AMOUNT
                    if(BigDecimal.valueOf(pqRecord.getFinanceAmount()==null?0:pqRecord.getFinanceAmount()).compareTo(BigDecimal.valueOf(prjQuotationDto.getFinanceAmount()==null?0:prjQuotationDto.getFinanceAmount())) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //首付款 DOWN_PAYMENT
                    if(BigDecimal.valueOf(pqRecord.getDownPayment()==null?0:pqRecord.getDownPayment()).compareTo(BigDecimal.valueOf(prjQuotationDto.getDownPayment()==null?0:prjQuotationDto.getDownPayment())) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //保证金 DEPOSIT
                    if(BigDecimal.valueOf(pqRecord.getDeposit()==null?0:pqRecord.getDeposit() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getDeposit()==null?0:prjQuotationDto.getDeposit() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //手续费 LEASE_CHARGE
                    if(BigDecimal.valueOf(pqRecord.getLeaseCharge()==null?0:pqRecord.getLeaseCharge() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getLeaseCharge()==null?0:prjQuotationDto.getLeaseCharge() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //其他支出 OTHER_EXPENSES
                    if(BigDecimal.valueOf(pqRecord.getOtherExpenses()==null?0:pqRecord.getOtherExpenses() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getOtherExpenses()==null?0:prjQuotationDto.getOtherExpenses() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //留购价款 RESIDUAL_RATIO
                    /*if(BigDecimal.valueOf(pqRecord.getResidualRatio()==null?0:pqRecord.getResidualRatio() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getResidualRatio()==null?0:prjQuotationDto.getResidualRatio() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }*/
                    //保证金处理方式 DEPOSIT_RETURN_METHOD DEPOSIT_RETURN_METHOD
                    /*if(!(pqRecord.getDepositReturnMethod()==null?"":pqRecord.getDepositReturnMethod() ).equals(prjQuotationDto.getDepositReturnMethod()==null?"": prjQuotationDto.getDepositReturnMethod())){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }*/
                    //租金支付日 PAYMENT_DAY
                    /*if(BigDecimal.valueOf(pqRecord.getPaymentDay()==null?0:pqRecord.getPaymentDay() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getPaymentDay()==null?0:prjQuotationDto.getPaymentDay() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }*/
                    //其他收入 OTHER_INCOME
                    if(BigDecimal.valueOf(pqRecord.getOtherIncome()==null?0:pqRecord.getOtherIncome() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getOtherIncome()==null?0:prjQuotationDto.getOtherIncome() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //宽限期 GRACE_TERM
                    if(BigDecimal.valueOf(pqRecord.getGraceTerm()==null?0:pqRecord.getGraceTerm() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getGraceTerm()==null?0:prjQuotationDto.getGraceTerm() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //宽限期利率 GRACE_PERIOD_INTEREST_RATE
                    if(BigDecimal.valueOf(pqRecord.getGracePeriodInterestRate()==null?0:pqRecord.getGracePeriodInterestRate() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getGracePeriodInterestRate()==null?0:prjQuotationDto.getGracePeriodInterestRate() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //宽限期收息间隔月份 GRACE_FREQUENCY
                    if(BigDecimal.valueOf(pqRecord.getGraceFrequency()==null?0:pqRecord.getGraceFrequency() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getGraceFrequency()==null?0:prjQuotationDto.getGraceFrequency() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //利率浮动值 FLOATING_WAY_RATE
                    if(BigDecimal.valueOf(pqRecord.getFloatingWayRate()==null?0:pqRecord.getFloatingWayRate() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getFloatingWayRate()==null?0:prjQuotationDto.getFloatingWayRate() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //增值税税率 VAT_RATE
                    if(BigDecimal.valueOf(pqRecord.getVatRate()==null?0:pqRecord.getVatRate() ).compareTo(BigDecimal.valueOf(prjQuotationDto.getVatRate()==null?0:prjQuotationDto.getVatRate() )) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //资产余值 ASSETS_SURPLUS_VALUE
                    if(BigDecimal.valueOf(pqRecord.getAssetsSurplusValue()==null?0:pqRecord.getAssetsSurplusValue()).compareTo(BigDecimal.valueOf(prjQuotationDto.getAssetsSurplusValue()==null?0:prjQuotationDto.getAssetsSurplusValue())) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }
                    //租赁期数 LEASE_TIMES 与可修改字段公式联通,不校验
                    /*if(BigDecimal.valueOf(pqRecord.getLeaseTimes()).compareTo(BigDecimal.valueOf(hlsCusPrjQuotation.getLeaseTimes())) != 0){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }*/
                    //利息计算方式 PAYMENT_METHOD
                    if(!(pqRecord.getPaymentMethod()==null?"":pqRecord.getPaymentMethod()).equals(prjQuotationDto.getPaymentMethod()==null?"":prjQuotationDto.getPaymentMethod())){
                        throw new HlsCusException("变更报价只可修改租期,投放日,基准利率字段");
                    }

                    //每期租(本)金 与可修改字段公式联通,不校验
                }
            }
        }

        hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest,prjQuotationDto);

        //反写项目表字段lease_item_amount
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(prjQuotationDto.getSourceDocumentId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);
        if(!dataClassContractPlan.equals(prjQuotation.getDataClass())){
            hlsCusPrjProject.setLeaseItemAmount(prjQuotationDto.getLeaseItemAmount());
            hlsCusPrjProject.setFinanceAmount(prjQuotationDto.getFinanceAmount());
            hlsCusPrjProject.setTaxStructure(prjQuotationDto.getTaxStructure());
            hlsCusPrjProject.setLeaseItemProperty(prjQuotationDto.getLeaseItemProperty());
            hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest,hlsCusPrjProject);
        }
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
        hlsCusPrjQuotationService.updateIrr(iRequest, prjQuotationDto);
        return prjQuotationDto;
    }
}
