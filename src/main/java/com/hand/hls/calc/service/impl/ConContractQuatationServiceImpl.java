package com.hand.hls.calc.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.calc.service.QuotationCommon;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.mapper.HlsCfItemMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.dto.HlsCusPrjQuotationDetails;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.mapper.SysDocumentHistoryBlobMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.utils.MathUtil;
import hls.core.sys.mapper.SysCodeValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * program: leaf-parent
 * description: ${description}
 * author: huangtianyang
 * create: 2019-03-12 11:32
 **/

@Service
public class ConContractQuatationServiceImpl implements QuotationCommon {

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
    private HlsCusPrjQuotationMapper prjQuotationMapper;
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
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;

    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;

    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;

    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;


    private static final String sourceDocumentCategory = "CON_CONTRACT";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CELLS = "cells";
    private static final String KEY_ROWS = "rows";
    private static final String KEY_VALUE = "value";
    private static final String KEY_FORMULA = "formula";
    private static final String KEY_FIELD = "field";
    public static final String PRICE_TYPE_SINGLE = "SINGLE";
    public static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    private static final String KEY_FORMAT = "format";
    private static final String[] DOUBLE_FORMATS = {"0%", "0.00", "#,##0.00"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy", "yyyy/m/d"};

    private static final String FLOATING_TYPE_FLOAT = "FLOATING";
    private static final String LPR_ADJUST_PERIOD_THIS = "THIS_PERIOD";
    private static final String LPR_ADJUST_PERIOD_NEXT = "NEXT_PERIOD";
    private static final String LPR_BASE_DATE_RELEASE_DAY = "RELEASE_DAY";
    private static final String LPR_BASE_DATE_LEASE_DAY = "LEASE_DAY";
    private static final String LPR_BASE_DATE_NAME_DAY = "NAME_DAY";

    private static final String INCEPT = "INCEPT";
    private static final String FUNDED = "FUNDED";

    private static final Long NINE_TEEN = 90L;
    private static final String OUTFLOW = "OUTFLOW";
    private static final String NOTE = "NOTE";
    private static final String TT = "TT";

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

        String interestAmortizationMethod  = prjQuotationDto.getInterestAmortizationMethod();

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

        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(prjQuotationDto.getSourceDocumentId());
        contract = hlsCusConContractMapper.selectByPrimaryKey(contract);

        //先删除原来报价对应的现金流
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(prjQuotationDto.getSourceDocumentId());
        cashflow.setGeneratedSource("PRJ_QUOTATION");
        cashflow.setGeneratedSourceDocId(prjQuotationDto.getQuotationId());
        List<HlsCusConContractCashflow> cashflowList = new ArrayList<>();


        //合同状态为INCEPT,FUNDED的时候只删除刷新租金跟租前息
        Boolean contractRemoveFlag = false;
        if(INCEPT.equals(contract.getContractStatus()) || FUNDED.equals(contract.getContractStatus())){
            cashflowList = contractCashflowMapper.selectCashflowRental(cashflow);
            contractRemoveFlag = true;
        }else{
            cashflowList = contractCashflowMapper.select(cashflow);
        }

        //判断是否被核销
        for(HlsCusConContractCashflow flow : cashflowList){
            String writeOffFlag =  contractCashflowMapper.queryCashflowByFlag(flow);
            if("NOT".equalsIgnoreCase(writeOffFlag)){
                contractCashflowMapper.deleteByCashflowId(flow);
            }

        }
//        conContractCashflowService.batchDelete(cashflowList);

        //将报价现金流更新合同现金流
        saveCashflowFromQuotationCashflow(iRequest, prjQuotationDto.getSourceDocumentId(), prjQuotationDto.getQuotationId(),interestAmortizationMethod,contractRemoveFlag);

        //更新合同表
        contract = hlsCusConContractMapper.selectByPrimaryKey(contract);
        contract.setTaxStructure(prjQuotationDto.getTaxStructure());
        contract.setTaxTypeCode(prjQuotationDto.getTaxTypeCode());
        contract.setLeaseItemAmount(prjQuotationDto.getLeaseItemAmount());
        contract.setFinanceAmount(prjQuotationDto.getFinanceAmount());
        contract.setIntRate(prjQuotationDto.getIntRate());
        contract.setVatRate(prjQuotationDto.getVatRate());
        contract.setLeaseItemProperty(prjQuotationDto.getLeaseItemProperty());

        contract.setPriceList(prjQuotationDto.getPriceList());
        contract.setLeaseTimes(prjQuotationDto.getLeaseTimes());
        contract.setAnnualPayTimes(prjQuotationDto.getAnnualPayTimes());
        contract.setLeaseTerm(prjQuotationDto.getLeaseTerm());
        contract.setPayType(prjQuotationDto.getPayType());
        contract.setVatFlag(prjQuotationDto.getVatFlag());
        contract.setVatInput(prjQuotationDto.getVatInput());
        contract.setVatInputTaxTypeId(prjQuotationDto.getVatInputTaxTypeId());
        contract.setVatInputTaxTypeRate(prjQuotationDto.getVatInputTaxTypeRate());
        contract.setDownPaymentRatio(prjQuotationDto.getDownPaymentRatio());
        contract.setDownPayment(prjQuotationDto.getDownPayment());
        contract.setNetDownPayment(prjQuotationDto.getNetDownPayment());
        contract.setVatDownPayment(prjQuotationDto.getVatDownPayment());
        contract.setNetFinanceAmount(prjQuotationDto.getNetFinanceAmount());
        contract.setVatFinanceAmount(prjQuotationDto.getVatFinanceAmount());
        contract.setTotalRental(prjQuotationDto.getTotalRental());
        contract.setNetTotalRental(prjQuotationDto.getNetTotalRental());
        contract.setVatTotalRental(prjQuotationDto.getVatTotalRental());
        contract.setTotalInterest(prjQuotationDto.getTotalInterest());
        contract.setNetTotalInterest(prjQuotationDto.getNetTotalInterest());
        contract.setVatTotalInterest(prjQuotationDto.getVatTotalInterest());
        contract.setLeaseChargeRatio(prjQuotationDto.getLeaseChargeRatio());
        contract.setLeaseCharge(prjQuotationDto.getLeaseCharge());
        contract.setNetLeaseCharge(prjQuotationDto.getNetLeaseCharge());
        contract.setVatLeaseCharge(prjQuotationDto.getVatLeaseCharge());
        contract.setLeaseMgtFeeRatio(prjQuotationDto.getLeaseMgtFeeRatio());
        contract.setLeaseMgtFee(prjQuotationDto.getLeaseMgtFee());
        contract.setNetLeaseMgtFee(prjQuotationDto.getNetLeaseMgtFee());
        contract.setVatLeaseMgtFee(prjQuotationDto.getVatLeaseMgtFee());
        contract.setLeaseMgtFeeRule(prjQuotationDto.getLeaseMgtFeeRule());
        contract.setDepositRatio(prjQuotationDto.getDepositRatio());
        contract.setDeposit(prjQuotationDto.getDeposit());
        contract.setDepositDeduction(prjQuotationDto.getDepositDeduction());
        contract.setResidualRatio(prjQuotationDto.getResidualRatio());
        contract.setResidualValue(prjQuotationDto.getResidualValue());
        contract.setNetResidualValue(prjQuotationDto.getNetResidualValue());
        contract.setVatResidualValue(prjQuotationDto.getVatResidualValue());
        contract.setBaseRateType(prjQuotationDto.getBaseRateType());
        contract.setBaseRate(prjQuotationDto.getBaseRate());
        contract.setIntRateType(prjQuotationDto.getIntRateType());
        contract.setFloatingWayRate(prjQuotationDto.getFloatingWayRate());
        contract.setInterestYearDays(prjQuotationDto.getInterestYearDays());
        contract.setInterestAmortizationMethod(prjQuotationDto.getInterestAmortizationMethod());
        contract.setLeaseStartDate(prjQuotationDto.getLeaseStartDate());
        contract.setQuotationId(prjQuotationDto.getQuotationId());
        contract.setCurrency(prjQuotationDto.getCurrency());

        HlsCusPrjQuotation hlsCusPrjQuotationNew = new HlsCusPrjQuotation();
        hlsCusPrjQuotationNew = prjQuotationMapper.selectByPrimaryKey(prjQuotationDto);
        //浮动利率-
        if(FLOATING_TYPE_FLOAT.equalsIgnoreCase(hlsCusPrjQuotationNew.getIntRateType())){
            if(hlsCusPrjQuotationNew.getLprLinkDate() != null){
                contract.setLprLinkDate(hlsCusPrjQuotationNew.getLprLinkDate());

                if(LPR_ADJUST_PERIOD_THIS.equalsIgnoreCase(hlsCusPrjQuotationNew.getLprAdjustmentPeriod())){
                    //下次调息日期 - 本期情况下，lpr挂钩日期加上调息期限  - 放款日/起租日
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(hlsCusPrjQuotationNew.getLprLinkDate());
                    cal.add(Calendar.MONTH, Integer.parseInt(hlsCusPrjQuotationNew.getLprAdjustmentTerm()));
                    Date nextAdjustmentDate = cal.getTime();
                    contract.setNextAdjustmentDate(nextAdjustmentDate);

                    hlsCusPrjQuotationNew.setNextAdjustmentDate(nextAdjustmentDate);
                    hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest , hlsCusPrjQuotationNew);

                }else if(LPR_ADJUST_PERIOD_NEXT.equalsIgnoreCase(hlsCusPrjQuotationNew.getLprAdjustmentPeriod())){
                    //下次调息日期 - 次期情况下 ,算出日期在的期次，该期次的计算日即为下次调息日期
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(hlsCusPrjQuotationNew.getLprLinkDate());
                    cal.add(Calendar.MONTH, Integer.parseInt(hlsCusPrjQuotationNew.getLprAdjustmentTerm()));
                    Date nextAdjustmentDate = cal.getTime();

                    HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                    hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotationNew.getQuotationId());
                    hlsCusPrjQuotationCashflow.setSourceDocumentId(hlsCusPrjQuotationNew.getSourceDocumentId());
                    hlsCusPrjQuotationCashflow.setLprCalcDate(nextAdjustmentDate);

                    List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList =  prjQuotationCashflowMapper.queryPrjCashflowByDate(hlsCusPrjQuotationCashflow);
                    if(prjQuotationCashflowList.size() > 0){
                         nextAdjustmentDate = prjQuotationCashflowList.get(0).getCalcDate();
                    }
                    contract.setNextAdjustmentDate(nextAdjustmentDate);

                    hlsCusPrjQuotationNew.setNextAdjustmentDate(nextAdjustmentDate);
                    hlsCusPrjQuotationService.updateByPrimaryKeySelective(iRequest , hlsCusPrjQuotationNew);

                }

            }
        }

        hlsCusConContractMapper.updateByPrimaryKeySelective(contract);

        if(!chekcContractLeaseItem(iRequest,contract)){
            throw new HlsCusException("拆分金额总和超过当前合同的租赁物总价款！");
        }

        return prjQuotationDto;
    }

    Boolean chekcContractLeaseItem(IRequest iRequest,HlsCusConContract cusConContract) throws HlsCusException {

        //校验总金额不能超过项目总金额

        Double sumAmount = 0D;
        HlsCusConContract contract = new HlsCusConContract();
        contract.setProjectId(cusConContract.getProjectId());

        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(cusConContract.getProjectId());
        project = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,project);
        Double financeAmount = project.getFinanceAmount();

        //航空事业部的不校验金额; 二期更新-update 20230111 :粤海不需要这个校验
        /*if(project.getHostUnitId() == 114L){
            return true;
        }*/

        if(financeAmount == null){
            throw new HlsCusException("融资额获取失败!");
        }

        List<HlsCusConContract> existList = hlsCusConContractMapper.select(contract);


        for(int i = 0; i < existList.size(); i++){
            sumAmount = MathUtil.add(sumAmount,existList.get(i).getFinanceAmount());
        }

        if(sumAmount.compareTo(financeAmount) != 1){
            return true;
        }

        return false;
    }


    void saveCashflowFromQuotationCashflow(IRequest iRequest, Long contractId, Long quotationId,String interestAmortizationMethod,Boolean contractRemoveFlag) {
        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = hlsCusConContractMapper.selectByPrimaryKey(c);
        List<HlsCusConContractCashflow> conContractCashflowList = new ArrayList<>();
        HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflowParameter.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = new ArrayList<>();
        if(contractRemoveFlag){
            prjQuotationCashflowParameter.setCfItem(1L);
            List<HlsCusPrjQuotationCashflow> cashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
            prjQuotationCashflowList.addAll(cashflowList);

            prjQuotationCashflowParameter.setCfItem(10L);
            List<HlsCusPrjQuotationCashflow> paynoteCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
            prjQuotationCashflowList.addAll(paynoteCashflowList);
        }else {
            prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
        }
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
//            conContractCashflow.setWriteOffFlag("NOT");
            conContractCashflow.setBillingStatus("NOT");
            conContractCashflow.setOverdueStatus("N");
            conContractCashflow.setPenaltyProcessStatus("N");
            conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
            conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
            conContractCashflow.setGeneratedSourceDocId(quotationId);
            conContractCashflow.setGeneratedSourceDocLineId(prjQuotationCashflowList.get(i).getQuotationCashflowId());

            //新加字段
            conContractCashflow.setPaymentMethod(prjQuotationCashflowList.get(i).getReceiptType());

            if(conContractCashflow.getCfItem() == 1L){
                conContractCashflow.setAmortizationMethod(interestAmortizationMethod);
            }

            //根据  due_amount ,contract_id , cf_item ,times ,判断 已经存在的则不插入，进行更新
            if(contractRemoveFlag) {
                List<HlsCusConContractCashflow> contractCashflows = contractCashflowMapper.queryOldCashflow(conContractCashflow);
                if (contractCashflows.size() > 0) {
                    for (HlsCusConContractCashflow cash : contractCashflows) {
                        conContractCashflow.setCashflowId(cash.getCashflowId());
                        conContractCashflow.set__status("update");
                    }

                } else {
                    conContractCashflow.setWriteOffFlag("NOT");
                    conContractCashflow.set__status("insert");

                    //新加字段 流出现金流cf_item=90,NOTE-汇票，否则 TT-电汇
                    conContractCashflow.setPaymentMethod(prjQuotationCashflowList.get(i).getReceiptType());
                    if(OUTFLOW.equals(conContractCashflow.getCfDirection())){
                        if(NINE_TEEN.equals(conContractCashflow.getCfItem())){
                            conContractCashflow.setPaymentMethod(NOTE);
                        }else{
                            conContractCashflow.setPaymentMethod(TT);
                        }
                    }
                }
            }else{
                conContractCashflow.setWriteOffFlag("NOT");
                conContractCashflow.set__status("insert");

                //新加字段 流出现金流cf_item=90,NOTE-汇票，否则 TT-电汇
                conContractCashflow.setPaymentMethod(prjQuotationCashflowList.get(i).getReceiptType());
                if(OUTFLOW.equals(conContractCashflow.getCfDirection())){
                    if(NINE_TEEN.equals(conContractCashflow.getCfItem())){
                        conContractCashflow.setPaymentMethod(NOTE);
                    }else{
                        conContractCashflow.setPaymentMethod(TT);
                    }
                }
            }
//            conContractCashflowService.insertSelective(iRequest, conContractCashflow);
            conContractCashflowList.add(conContractCashflow);
        }
        conContractCashflowService.batchUpdate(iRequest, conContractCashflowList);

        hlsCusConContractMapper.updateByPrimaryKey(c);
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
}
