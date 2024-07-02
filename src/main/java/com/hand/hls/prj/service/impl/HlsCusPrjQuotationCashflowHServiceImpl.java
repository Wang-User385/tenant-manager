package com.hand.hls.prj.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.HlsCusCalcPmt;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflowH;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowHMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 17:06
 * @Description:
 * @Purpose:
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationCashflowHServiceImpl extends BaseServiceImpl<HlsCusPrjQuotationCashflowH> implements HlsCusPrjQuotationCashflowHService {
    @Autowired
    private HlsCusPrjQuotationCashflowHMapper hlsCusPrjQuotationCashflowHMapper;

    /*EQUAL_INTEREST  等额租金 计算报价*/
    @Override
    public void calcEqualInterest(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException {
        /*清除报价数据*/
        HlsCusPrjQuotationCashflowH prjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
        prjQuotationCashflowH.setQuotationId(quotationId);
        prjQuotationCashflowH.setCalcWay(calcWay);
        prjQuotationCashflowH.setSubsectionId(subsectionId);
        hlsCusPrjQuotationCashflowHMapper.deleteQuotationCalcData(prjQuotationCashflowH);
        Double pRate = intRate / 12 * annualPayTimes;
        Double pNper = leaseTerm/annualPayTimes;
        Double pPv = outstandingPrincipal;
        Double pFv = 0D;  //默认0
        Long pType = payType;
        /*计算pmt*/
        Double pmt = HlsCusCalcPmt.Pmt(pRate,pNper,pPv,pFv,pType);
        /*计算报价*/
        Double times = Math.floor(leaseTerm/annualPayTimes);//期数向上取整
        for(int i=1;i<=times.intValue();i++){
            int coefficient;
            if (fristTimesCount.compareTo(0L) == 0) {
                coefficient = i - 1;
            } else {
                coefficient = i;
            }
            HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
            hlsCusPrjQuotationCashflowH.setQuotationId(quotationId);
            hlsCusPrjQuotationCashflowH.setCfItem(1L);
            hlsCusPrjQuotationCashflowH.setCfType(1L);
            hlsCusPrjQuotationCashflowH.setCfDirection("INFLOW");
            hlsCusPrjQuotationCashflowH.setCfStatus("RELEASE");
            hlsCusPrjQuotationCashflowH.setTimes(Long.valueOf(i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(leaseStartDate);
            calendar.add(calendar.MONTH, coefficient * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setDueDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setCalcDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setFinIncomeDate(calendar.getTime());

            Calendar calendarPlan = Calendar.getInstance();
            calendarPlan.setTime(inceptionOfLease);
            calendarPlan.add(calendarPlan.MONTH, i * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setPlanDueDate(calendarPlan.getTime());
            /*租金*/
            hlsCusPrjQuotationCashflowH.setDueAmount(transfor(-pmt));
            if(i<times.intValue()){
                /*利息*/
                if(i==1 && payType == 1L){
                    hlsCusPrjQuotationCashflowH.setInterest(0D);
                    hlsCusPrjQuotationCashflowH.setNetInterest(0D);
                    hlsCusPrjQuotationCashflowH.setVatInterest(0D);
                }else{
                    hlsCusPrjQuotationCashflowH.setInterest(transfor(outstandingPrincipal*intRate/(12/annualPayTimes)));
                    hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                    hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());
                }
                /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getInterest());
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }
                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal()-hlsCusPrjQuotationCashflowH.getNetPrincipal());

                outstandingPrincipal=outstandingPrincipal-hlsCusPrjQuotationCashflowH.getPrincipal();
                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(outstandingPrincipal);
            }else{
                /*最后一期倒减*/
                 /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(outstandingPrincipal);
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }
                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal()-hlsCusPrjQuotationCashflowH.getNetPrincipal());

                 /*利息*/
                hlsCusPrjQuotationCashflowH.setInterest(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getPrincipal());
                hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());
                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(0D);
            }
            hlsCusPrjQuotationCashflowH.setNetDueAmount(hlsCusPrjQuotationCashflowH.getNetPrincipal()+hlsCusPrjQuotationCashflowH.getNetInterest());
            hlsCusPrjQuotationCashflowH.setVatDueAmount(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getNetDueAmount());
            hlsCusPrjQuotationCashflowH.setOutstandingRental(0D);
            hlsCusPrjQuotationCashflowH.setOutstandingInterest(0D);
            hlsCusPrjQuotationCashflowH.setInterestAccrualBalance(0D);
            hlsCusPrjQuotationCashflowH.setAccumulatedUnpaidInterest(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffAmount(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffDays(0D);
            hlsCusPrjQuotationCashflowH.setChangeFineAmount(0D);
            hlsCusPrjQuotationCashflowH.setBillingVatRate(vatRate);
            hlsCusPrjQuotationCashflowH.setCalcWay(calcWay);
            hlsCusPrjQuotationCashflowH.setSubsectionId(subsectionId);
            self().insertSelective(iRequest,hlsCusPrjQuotationCashflowH);
        }
    };

    /*EQUAL_PRINCIPAL 等额本金 计算报价*/
    @Override
    public void calcEqualPrincipal(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException {
        /*清除报价数据*/
        HlsCusPrjQuotationCashflowH prjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
        prjQuotationCashflowH.setQuotationId(quotationId);
        prjQuotationCashflowH.setCalcWay(calcWay);
        prjQuotationCashflowH.setSubsectionId(subsectionId);
        hlsCusPrjQuotationCashflowHMapper.deleteQuotationCalcData(prjQuotationCashflowH);

        Double t = leaseTerm/annualPayTimes;
        /*本金*/
        Double principal = transfor(outstandingPrincipal/t);

        /*计算报价*/
        Double times = Math.floor(leaseTerm/annualPayTimes);//期数向上取整
        for(int i=1;i<=times.intValue();i++){
            int coefficient;
            if (fristTimesCount.compareTo(0L) == 0) {
                coefficient = i - 1;
            } else {
                coefficient = i;
            }
            HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
            hlsCusPrjQuotationCashflowH.setQuotationId(quotationId);
            hlsCusPrjQuotationCashflowH.setCfItem(1L);
            hlsCusPrjQuotationCashflowH.setCfType(1L);
            hlsCusPrjQuotationCashflowH.setCfDirection("INFLOW");
            hlsCusPrjQuotationCashflowH.setCfStatus("RELEASE");
            hlsCusPrjQuotationCashflowH.setTimes(Long.valueOf(i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(leaseStartDate);
            calendar.add(calendar.MONTH, coefficient * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setDueDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setCalcDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setFinIncomeDate(calendar.getTime());
            Calendar calendarPlan = Calendar.getInstance();
            calendarPlan.setTime(inceptionOfLease);
            calendarPlan.add(calendarPlan.MONTH, i * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setPlanDueDate(calendarPlan.getTime());

            if(i<times.intValue()){
                 /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(principal);
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }
                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal()-hlsCusPrjQuotationCashflowH.getNetPrincipal());

                /*利息*/
                if(i==1 && payType == 1L){
                    hlsCusPrjQuotationCashflowH.setInterest(0D);
                    hlsCusPrjQuotationCashflowH.setNetInterest(0D);
                    hlsCusPrjQuotationCashflowH.setVatInterest(0D);
                }else{
                    hlsCusPrjQuotationCashflowH.setInterest(transfor(outstandingPrincipal*intRate/(12/annualPayTimes)));
                    hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                    hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());
                }
                outstandingPrincipal=outstandingPrincipal-hlsCusPrjQuotationCashflowH.getPrincipal();
                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(outstandingPrincipal);
            }else{
                /*最后一期倒减*/
                /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(outstandingPrincipal);
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }
                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal()-hlsCusPrjQuotationCashflowH.getNetPrincipal());

                 /*利息*/
                hlsCusPrjQuotationCashflowH.setInterest(transfor(outstandingPrincipal*intRate/(12/annualPayTimes)));
                hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());

                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(0D);
            }
            /*租金*/
            hlsCusPrjQuotationCashflowH.setDueAmount(hlsCusPrjQuotationCashflowH.getPrincipal()+hlsCusPrjQuotationCashflowH.getInterest());
            hlsCusPrjQuotationCashflowH.setNetDueAmount(hlsCusPrjQuotationCashflowH.getNetPrincipal()+hlsCusPrjQuotationCashflowH.getNetInterest());
            hlsCusPrjQuotationCashflowH.setVatDueAmount(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getNetDueAmount());

            hlsCusPrjQuotationCashflowH.setOutstandingRental(0D);
            hlsCusPrjQuotationCashflowH.setOutstandingInterest(0D);
            hlsCusPrjQuotationCashflowH.setInterestAccrualBalance(0D);
            hlsCusPrjQuotationCashflowH.setAccumulatedUnpaidInterest(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffAmount(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffDays(0D);
            hlsCusPrjQuotationCashflowH.setChangeFineAmount(0D);
            hlsCusPrjQuotationCashflowH.setBillingVatRate(vatRate);
            hlsCusPrjQuotationCashflowH.setCalcWay(calcWay);
            hlsCusPrjQuotationCashflowH.setSubsectionId(subsectionId);
            self().insertSelective(iRequest,hlsCusPrjQuotationCashflowH);
        }
    };

    /*DESIGNATED_RENT 指定租金 计算报价*/
    @Override
    public void calcDesignatedRent(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Double dueAmount, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException {
        /*清除报价数据*/
        HlsCusPrjQuotationCashflowH prjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
        prjQuotationCashflowH.setQuotationId(quotationId);
        prjQuotationCashflowH.setCalcWay(calcWay);
        prjQuotationCashflowH.setSubsectionId(subsectionId);
        hlsCusPrjQuotationCashflowHMapper.deleteQuotationCalcData(prjQuotationCashflowH);

        Double times = Math.floor(leaseTerm/annualPayTimes);//期数向上取整
         /*计算报价*/
        for(int i=1;i<=times.intValue();i++){
            int coefficient;
            if (fristTimesCount.compareTo(0L) == 0) {
                coefficient = i - 1;
            } else {
                coefficient = i;
            }
            HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
            hlsCusPrjQuotationCashflowH.setQuotationId(quotationId);
            hlsCusPrjQuotationCashflowH.setCfItem(1L);
            hlsCusPrjQuotationCashflowH.setCfType(1L);
            hlsCusPrjQuotationCashflowH.setCfDirection("INFLOW");
            hlsCusPrjQuotationCashflowH.setCfStatus("RELEASE");
            hlsCusPrjQuotationCashflowH.setTimes(Long.valueOf(i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(leaseStartDate);
            calendar.add(calendar.MONTH, coefficient * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setDueDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setCalcDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setFinIncomeDate(calendar.getTime());
            Calendar calendarPlan = Calendar.getInstance();
            calendarPlan.setTime(inceptionOfLease);
            calendarPlan.add(calendarPlan.MONTH, i * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setPlanDueDate(calendarPlan.getTime());
            /*租金*/
            hlsCusPrjQuotationCashflowH.setDueAmount(transfor(dueAmount));
            if(i<times.intValue()){
                /*利息*/
                if(i==1 && payType == 1L){
                    hlsCusPrjQuotationCashflowH.setInterest(0D);
                    hlsCusPrjQuotationCashflowH.setNetInterest(0D);
                    hlsCusPrjQuotationCashflowH.setVatInterest(0D);
                }else{
                    hlsCusPrjQuotationCashflowH.setInterest(transfor(outstandingPrincipal*intRate/(12/annualPayTimes)));
                    hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                    hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());
                }

                /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getInterest());
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }
                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal()-hlsCusPrjQuotationCashflowH.getNetPrincipal());

                outstandingPrincipal=outstandingPrincipal-hlsCusPrjQuotationCashflowH.getPrincipal();
                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(outstandingPrincipal);
            }else{
                /*最后一期倒减*/
                 /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(outstandingPrincipal);
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }
                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal()-hlsCusPrjQuotationCashflowH.getNetPrincipal());

                 /*利息*/
                hlsCusPrjQuotationCashflowH.setInterest(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getPrincipal());
                hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());
                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(0D);
            }
            hlsCusPrjQuotationCashflowH.setNetDueAmount(hlsCusPrjQuotationCashflowH.getNetPrincipal()+hlsCusPrjQuotationCashflowH.getNetInterest());
            hlsCusPrjQuotationCashflowH.setVatDueAmount(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getNetDueAmount());
            hlsCusPrjQuotationCashflowH.setOutstandingRental(0D);
            hlsCusPrjQuotationCashflowH.setOutstandingInterest(0D);
            hlsCusPrjQuotationCashflowH.setInterestAccrualBalance(0D);
            hlsCusPrjQuotationCashflowH.setAccumulatedUnpaidInterest(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffAmount(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffDays(0D);
            hlsCusPrjQuotationCashflowH.setChangeFineAmount(0D);
            hlsCusPrjQuotationCashflowH.setBillingVatRate(vatRate);
            hlsCusPrjQuotationCashflowH.setCalcWay(calcWay);
            hlsCusPrjQuotationCashflowH.setSubsectionId(subsectionId);
            self().insertSelective(iRequest,hlsCusPrjQuotationCashflowH);
        }
    };

    /*DESIGNATED_PRINCIPAL  指定本金 计算报价*/
    @Override
    public void calcDesignatedPrincipal(IRequest iRequest, Long quotationId, Double outstandingPrincipal, Date leaseStartDate, Double leaseTerm, Long annualPayTimes, Double vatRate, Double intRate, Double principal, Long payType, Long subsectionId, String calcWay, String businessType, Long fristTimesCount, Date inceptionOfLease) throws IllegalArgumentException {
        /*清除报价数据*/
        HlsCusPrjQuotationCashflowH prjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
        prjQuotationCashflowH.setQuotationId(quotationId);
        prjQuotationCashflowH.setCalcWay(calcWay);
        prjQuotationCashflowH.setSubsectionId(subsectionId);
        hlsCusPrjQuotationCashflowHMapper.deleteQuotationCalcData(prjQuotationCashflowH);

        Double times = Math.floor(leaseTerm/annualPayTimes);//期数向上取整
        /*计算报价*/
        for(int i=1;i<=times.intValue();i++){
            int coefficient;
            if (fristTimesCount.compareTo(0L) == 0) {
                coefficient = i - 1;
            } else {
                coefficient = i;
            }
            HlsCusPrjQuotationCashflowH hlsCusPrjQuotationCashflowH = new HlsCusPrjQuotationCashflowH();
            hlsCusPrjQuotationCashflowH.setQuotationId(quotationId);
            hlsCusPrjQuotationCashflowH.setCfItem(1L);
            hlsCusPrjQuotationCashflowH.setCfType(1L);
            hlsCusPrjQuotationCashflowH.setCfDirection("INFLOW");
            hlsCusPrjQuotationCashflowH.setCfStatus("RELEASE");
            hlsCusPrjQuotationCashflowH.setTimes(Long.valueOf(i));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(leaseStartDate);
            calendar.add(calendar.MONTH, coefficient * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setDueDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setCalcDate(calendar.getTime());
            hlsCusPrjQuotationCashflowH.setFinIncomeDate(calendar.getTime());
            Calendar calendarPlan = Calendar.getInstance();
            calendarPlan.setTime(inceptionOfLease);
            calendarPlan.add(calendarPlan.MONTH, i * (annualPayTimes.intValue()));
            hlsCusPrjQuotationCashflowH.setPlanDueDate(calendarPlan.getTime());
            if (principal == null) {
                principal = 0D;
            }
            if(i<times.intValue()){
                 /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(principal);
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }
                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal()-hlsCusPrjQuotationCashflowH.getNetPrincipal());

                /*利息*/
                if(i==1 && payType == 1L){
                    hlsCusPrjQuotationCashflowH.setInterest(0D);
                    hlsCusPrjQuotationCashflowH.setNetInterest(0D);
                    hlsCusPrjQuotationCashflowH.setVatInterest(0D);
                }else{
                    hlsCusPrjQuotationCashflowH.setInterest(transfor(outstandingPrincipal*intRate/(12/annualPayTimes)));
                    hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                    hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());
                }

                outstandingPrincipal=outstandingPrincipal-hlsCusPrjQuotationCashflowH.getPrincipal();
                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(outstandingPrincipal);
            }else{
                /*最后一期倒减*/
                /*本金*/
                hlsCusPrjQuotationCashflowH.setPrincipal(outstandingPrincipal);
                if("LEASEBACK".equals(businessType)){
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal());
                }else{
                    hlsCusPrjQuotationCashflowH.setNetPrincipal(transfor(hlsCusPrjQuotationCashflowH.getPrincipal()/(1+vatRate)));
                }

                hlsCusPrjQuotationCashflowH.setVatPrincipal(hlsCusPrjQuotationCashflowH.getPrincipal() == null ? 0D : hlsCusPrjQuotationCashflowH.getPrincipal() - hlsCusPrjQuotationCashflowH.getNetPrincipal());



                 /*利息*/
                hlsCusPrjQuotationCashflowH.setInterest(transfor(outstandingPrincipal*intRate/(12/annualPayTimes)));
                hlsCusPrjQuotationCashflowH.setNetInterest(transfor(hlsCusPrjQuotationCashflowH.getInterest()/(1+vatRate)));
                hlsCusPrjQuotationCashflowH.setVatInterest(hlsCusPrjQuotationCashflowH.getInterest()-hlsCusPrjQuotationCashflowH.getNetInterest());

                hlsCusPrjQuotationCashflowH.setOutstandingPrincipal(0D);
            }
             /*租金*/
            hlsCusPrjQuotationCashflowH.setDueAmount(hlsCusPrjQuotationCashflowH.getPrincipal()+hlsCusPrjQuotationCashflowH.getInterest());
            hlsCusPrjQuotationCashflowH.setNetDueAmount(hlsCusPrjQuotationCashflowH.getNetPrincipal()+hlsCusPrjQuotationCashflowH.getNetInterest());
            hlsCusPrjQuotationCashflowH.setVatDueAmount(hlsCusPrjQuotationCashflowH.getDueAmount()-hlsCusPrjQuotationCashflowH.getNetDueAmount());

            hlsCusPrjQuotationCashflowH.setOutstandingRental(0D);
            hlsCusPrjQuotationCashflowH.setOutstandingInterest(0D);
            hlsCusPrjQuotationCashflowH.setInterestAccrualBalance(0D);
            hlsCusPrjQuotationCashflowH.setAccumulatedUnpaidInterest(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffAmount(0D);
            hlsCusPrjQuotationCashflowH.setWriteOffDays(0D);
            hlsCusPrjQuotationCashflowH.setChangeFineAmount(0D);
            hlsCusPrjQuotationCashflowH.setBillingVatRate(vatRate);
            hlsCusPrjQuotationCashflowH.setCalcWay(calcWay);
            hlsCusPrjQuotationCashflowH.setSubsectionId(subsectionId);
            self().insertSelective(iRequest,hlsCusPrjQuotationCashflowH);
        }
    };

    //保留两位小数
    public Double transfor(Double amount) {
        BigDecimal bg = new BigDecimal(amount);
        double num = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }
}
