package com.hand.hls.gld.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.fin.dto.HlsCusLonContractQuotation;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.service.HlsCusLonContractQuotationService;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.mapper.HlsCusGldLonContractFinCostMapper;
import com.hand.hls.gld.mapper.HlsCusLonContractRepaymentMergeMapper;
import com.hand.hls.gld.service.GldLonContractFinCostService;
import com.hand.hls.gld.utils.IrrUtil;
import com.hand.hls.utils.HlsCusMathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.gld.dto.HlsCusLonContractRepaymentMerge;
import com.hand.hls.gld.service.IHlsCusLonContractRepaymentMergeService;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractRepaymentMergeServiceImpl extends BaseServiceImpl<HlsCusLonContractRepaymentMerge> implements IHlsCusLonContractRepaymentMergeService{

    @Autowired
    private HlsCusLonContractRepaymentMergeMapper hlsCusLonContractRepaymentMergeMapper;
    @Autowired
    private GldLonContractFinCostService gldLonContractFinCostService;
    @Autowired
    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;
    @Autowired
    private HlsCusLonContractQuotationService hlsCusLonContractQuotationService;
    @Autowired
    private HlsCusLonContractRepaymentService hlsCusLonContractRepaymentService;
    @Autowired
    private HlsCusGldLonContractFinCostMapper hlsCusGldLonContractFinCostMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Override
    public void calcLonConWithdrawFinCostNew(IRequest request, HlsCusLonContractWithdraw lonContractWithdraw, String type){
        //查询交易信息
        HlsCusLonContractQuotation hlsCusLonContractQuotationTemp = new HlsCusLonContractQuotation();
        hlsCusLonContractQuotationTemp.setWithdrawId(lonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractQuotation> hlsCusLonContractQuotations = hlsCusLonContractQuotationService.select(request, hlsCusLonContractQuotationTemp, 1, 9999);
        HlsCusLonContractQuotation hlsCusLonContractQuotation = hlsCusLonContractQuotations.get(0);
        //获取变更起始期数
        Long changeTerm = hlsCusLonContractQuotation.getChangeTerm();
        //还款审批变更起始期数取已经确认过的计提日期的后一期现金流
        if("REPAYMENT".equals(type)){
            Long changeTermQuery = hlsCusLonContractRepaymentMergeMapper.queryChangeTermRepayment(lonContractWithdraw.getWithdrawId());
            if(changeTermQuery != null){
                changeTerm = changeTermQuery;
            }else{
                changeTerm = 0L;
            }
        }
        //先删除之前计算的表(只删除变更期数之后的表)
        HlsCusLonContractRepaymentMerge para = new HlsCusLonContractRepaymentMerge();
        para.setWithdrawId(lonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractRepaymentMerge> deleteResult = this.select(request,para,1,9999999);
        for(HlsCusLonContractRepaymentMerge item :deleteResult){
            if(item.getTimes() >= changeTerm){
                this.deleteByPrimaryKey(item);
            }
        }


        //删除未确认计提数据
        HlsCusGldLonContractFinCost gldLonContractFinCostTmp = new HlsCusGldLonContractFinCost();
        gldLonContractFinCostTmp.setContractId(lonContractWithdraw.getContractId());
        gldLonContractFinCostTmp.setWithdrawId(lonContractWithdraw.getWithdrawId());
        gldLonContractFinCostTmp.setPostFlag("N");
        List<HlsCusGldLonContractFinCost> gldLonContractFinCostList = gldLonContractFinCostService.select(request, gldLonContractFinCostTmp, 1, 999999);
        gldLonContractFinCostService.batchDelete(gldLonContractFinCostList);


        List<HlsCusLonContractRepaymentMerge> repayments = hlsCusLonContractRepaymentMergeMapper.queryMergeRepayment(lonContractWithdraw.getWithdrawId());
        Double SumPlannedDueAmount = 0D;
        Double SumProfit = 0D;
        HlsCusLonContractRepayment repay = new HlsCusLonContractRepayment();
        repay.setWithdrawId(lonContractWithdraw.getWithdrawId());
        List<HlsCusLonContractRepayment> repays =  hlsCusLonContractRepaymentService.select(request,repay,1,1000);
        for(int i=0;i<repays.size();i++){
            if(repays.get(i).getCfItem()==302l){
                SumPlannedDueAmount=SumPlannedDueAmount+repays.get(i).getDueAmount();
            }
        }

        HlsCusGldLonContractFinCost gldLonContractFinCost = new HlsCusGldLonContractFinCost();
        //计息开始日
        Calendar cStart = Calendar.getInstance();
        //计息结束日
        Calendar cEnd = Calendar.getInstance();
        cStart.setTime(repayments.get(0).getPlannedDueDate());
        cEnd.setTime(repayments.get(repayments.size()-1).getPlannedDueDate());
        long month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH);
        Calendar pStart = Calendar.getInstance();

        //计息结束日
        Calendar pEnd = Calendar.getInstance();
        for (int j = 0; j < month; j++) {
            Double profit = 0d;
            if(j==0){
                pStart.setTime(cStart.getTime());
                pEnd.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),pStart.getActualMaximum(Calendar.DAY_OF_MONTH),0,0,0);
                //如果第一期和第0期在同一个月
                Calendar temp1 = Calendar.getInstance();
                Calendar temp0 = Calendar.getInstance();
                temp1.setTime(repayments.get(1).getPlannedDueDate());
                temp0.setTime(repayments.get(0).getPlannedDueDate());

                if(temp1.get(Calendar.MONTH)==temp0.get(Calendar.MONTH)&&temp1.get(Calendar.YEAR)==temp0.get(Calendar.YEAR)){
                    Long calcInterestDays0 = (repayments.get(1).getPlannedDueDate().getTime() - pStart.getTimeInMillis()) / (1000 * 3600 * 24);
                    Long calcInterestDays1 = (pEnd.getTimeInMillis()-repayments.get(1).getPlannedDueDate().getTime() ) / (1000 * 3600 * 24);
                    profit= HlsCusMathUtil.add(repayments.get(0).getCurrentNetAmount()*lonContractWithdraw.getIntRate()*calcInterestDays0/360,repayments.get(1).getCurrentNetAmount()*lonContractWithdraw.getIntRate()*calcInterestDays1/360,2) ;
                }else{
                    Long calcInterestDays1 = (pEnd.getTimeInMillis()-pStart.getTimeInMillis() ) / (1000 * 3600 * 24);
                    profit= HlsCusMathUtil.add(0,repayments.get(0).getCurrentNetAmount()*lonContractWithdraw.getIntRate()*calcInterestDays1/360,2) ;
                }
                SumProfit=HlsCusMathUtil.add(SumProfit,profit,2);

                //不在同一个月
            }else if(j==month-1){
                pStart.add(Calendar.MONTH,1);
                pEnd.setTime(cEnd.getTime());
                HlsCusLonContractRepaymentMerge last= repayments.get(repayments.size()-1);
                Long calcInterestDays = (last.getPlannedDueDate().getTime()-pStart.getTimeInMillis()) / (1000 * 3600 * 24);
                profit = HlsCusMathUtil.sub(SumPlannedDueAmount,SumProfit,2) ;

            }
            else{
                    pStart.add(Calendar.MONTH,1);
                    pStart.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),1);
                pEnd.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),pStart.getActualMaximum(Calendar.DAY_OF_MONTH));
                //查询这个月是否有还款
//                List<HlsCusLonContractRepaymentMerge> one = repayments.stream().filter(a-> !a.getPlannedDueDate().after(pEnd.getTime())&&!a.getPlannedDueDate().before(pEnd.getTime() )).collect(Collectors.toList());
                int currentTimes=0;
                for(int k=1;k<repayments.size();k++){
                    HlsCusLonContractRepaymentMerge a = repayments.get(k);
                    if(!a.getPlannedDueDate().after(pEnd.getTime())&&!a.getPlannedDueDate().before(pEnd.getTime())) {
                        currentTimes=k;
                    }
                }

                if(currentTimes>0){
                    Long calcInterestDays1 = (repayments.get(currentTimes).getPlannedDueDate().getTime() - pStart.getTimeInMillis()) / (1000 * 3600 * 24);
                    Long calcInterestDays2 = (pEnd.getTimeInMillis()-repayments.get(currentTimes).getPlannedDueDate().getTime() ) / (1000 * 3600 * 24);
                    profit= HlsCusMathUtil.add(repayments.get(currentTimes-1).getCurrentNetAmount()*lonContractWithdraw.getIntRate()*calcInterestDays1/360,repayments.get(currentTimes).getCurrentNetAmount()*lonContractWithdraw.getIntRate()*calcInterestDays2/360,2) ;

                }else{
                    Long calcInterestDays = (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
                    List<HlsCusLonContractRepaymentMerge> one = repayments.stream().filter(a-> a.getPlannedDueDate().before(pEnd.getTime()) ).collect(Collectors.toList());
                    profit= HlsCusMathUtil.add(one.get(one.size()-1).getCurrentNetAmount()*lonContractWithdraw.getIntRate()*calcInterestDays/360,0,2);
                }

                SumProfit=HlsCusMathUtil.add(SumProfit,profit,2);


            }

            gldLonContractFinCost.setContractId(lonContractWithdraw.getContractId());
            gldLonContractFinCost.setWithdrawId(lonContractWithdraw.getWithdrawId());
            gldLonContractFinCost.setGldPaymentId(null);
            gldLonContractFinCost.setStartDate(pStart.getTime());
            gldLonContractFinCost.setEndDate(pEnd.getTime());
            String periodName;
            if (pEnd.get(Calendar.MONTH) + 1 < 10) {
                periodName = pEnd.get(Calendar.YEAR) + "-0" + (pEnd.get(Calendar.MONTH) + 1);

            } else {
                periodName = pEnd.get(Calendar.YEAR) + "-" + (pEnd.get(Calendar.MONTH) + 1);
            }
            gldLonContractFinCost.setPeriodName(periodName);
            gldLonContractFinCost.setDays((cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24));
            gldLonContractFinCost.setFinanceIncomeInclud(profit);
            gldLonContractFinCost.setFinanceIncomeVat(HlsCusMathUtil.div(HlsCusMathUtil.mul(profit,0.06),(1+0.06),2));
            gldLonContractFinCost.setFinanceCost(gldLonContractFinCost.getFinanceIncomeInclud()-gldLonContractFinCost.getFinanceIncomeVat());
            gldLonContractFinCost.setPostFlag("N");
            gldLonContractFinCost.setFinanceCostId(null);


            gldLonContractFinCostService.insert(request,gldLonContractFinCost);



        }







//        for(int i = 0;i< repayments.size();i++){
//            SumPlannedDueAmount = SumPlannedDueAmount + repayments.get(i).getPlannedDueAmount();
//            //如果是变更前取变更前的记录，变更后的重新计算
//            if(repayments.get(i).getTimes()<changeTerm){
//                HlsCusLonContractRepaymentMerge hlsCusLonContractRepaymentMerge = new HlsCusLonContractRepaymentMerge();
//                hlsCusLonContractRepaymentMerge.setWithdrawId(lonContractWithdraw.getWithdrawId());
//                hlsCusLonContractRepaymentMerge.setTimes(repayments.get(i).getTimes());
//                List<HlsCusLonContractRepaymentMerge> timeResult = hlsCusLonContractRepaymentMergeMapper.queryMergeRepaymentLast(hlsCusLonContractRepaymentMerge);
//                if(timeResult.size()>0){
//                    repayments.set(i,timeResult.get(0));
//                    if(i != 0 && i!= repayments.size()-1){
//                        SumProfit = SumProfit + repayments.get(i).getProfit();
//                    }
//                }
//            }else if(i == 0){
//                repayments.get(i).setCurrentNetAmount(-repayments.get(i).getPlannedDueAmount());
//            }else if(i == repayments.size()-1){
//                //最后一期计算收益、减少额、净额
//                Double profit =  (double) Math.round((SumPlannedDueAmount - SumProfit) * 100) / 100;
//                Double reduction = (double) Math.round((repayments.get(i).getPlannedDueAmount() - profit) * 100) / 100;
//                Double currentNetAmount = (double) Math.round((repayments.get(i-1).getCurrentNetAmount() - reduction) * 100) / 100;
//
//                repayments.get(i).setProfit(profit);
//                repayments.get(i).setReduction(reduction);
//                repayments.get(i).setCurrentNetAmount(currentNetAmount);
//            }else{
//                //计息开始日
//                Calendar cStart = Calendar.getInstance();
//                //计息结束日
//                Calendar cEnd = Calendar.getInstance();
//                //计息天数
//                Long calcInterestDays = 0L;
//                cStart.setTime(repayments.get(i-1).getPlannedDueDate());
//                cEnd.setTime(repayments.get(i).getPlannedDueDate());
//                calcInterestDays = (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
//
//                //从第二期开始计算收益、减少额、净额
//                Double profit = 0.00D;
//                if(repayments.get(i).getProfit()!=null){
//                    profit = repayments.get(i).getProfit();
//                }else{
////                    profit = (double) Math.round((repayments.get(i-1).getCurrentNetAmount()*irr*calcInterestDays/365) * 100) / 100;
//                }
//                Double reduction = (double) Math.round((repayments.get(i).getPlannedDueAmount() - profit)* 100) / 100;
//                Double currentNetAmount = (double) Math.round((repayments.get(i-1).getCurrentNetAmount() - reduction) * 100) / 100;
//
//
//                repayments.get(i).setProfit(profit);
//                repayments.get(i).setReduction(reduction);
//                repayments.get(i).setCurrentNetAmount(currentNetAmount);
//                SumProfit = SumProfit + profit;
//            }
//            repayments.get(i).setContractId(lonContractWithdraw.getContractId());
//            repayments.get(i).setWithdrawId(lonContractWithdraw.getWithdrawId());
//            if(repayments.get(i).getTimes()>=changeTerm){
//                self().insert(request,repayments.get(i));
//            }
//        }

        try {
            //分摊算法
//            this.withdrawFinanceCost(request,repayments, lonContractWithdraw);


        } catch (IllegalArgumentException e) {
            throw e;
        }

    }

    /**
     * 合并手续费，担保费到最近一期的本金利息营收日上
     * @param request
     * @param repayments
     * @param lonContractWithdraw
     * @param cfItem
     */
    private void handleOtherFee(IRequest request,List<HlsCusLonContractRepaymentMerge> repayments,HlsCusLonContractWithdraw lonContractWithdraw,Long cfItem){
        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
        lonContractRepayment.setWithdrawId(lonContractWithdraw.getWithdrawId());
        lonContractRepayment.setCfItem(cfItem);
        List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentService.select(request, lonContractRepayment, 1, 999999);
        //合并到距离最近的一期还款上
        lonContractRepaymentList.forEach(contractRepayment ->{
            int choose_key = 0;
            Long minDay = 0L;
            for(int i=0;i<repayments.size();i++){
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();
                from.setTime(contractRepayment.getPlannedDueDate());
                to.setTime(repayments.get(i).getPlannedDueDate());
                Long days = Math.abs(hlsCusLonContractWithdrawService.getCalcDays(from.getTime(), to.getTime())-1);
                if(i == 0){
                    minDay = days;
                    choose_key = i;
                }else if(days < minDay){
                    minDay = days;
                    choose_key = i;
                }
            }
            repayments.get(choose_key).setPlannedDueAmount(repayments.get(choose_key).getPlannedDueAmount()+contractRepayment.getPlannedDueAmount());
        });

    }

    /**
     * 计算IRR
     * @param repayments irr现金流
     * @param interestCycle 还款频率
     * @return
     */
    private Double calcIrr(List<HlsCusLonContractRepaymentMerge> repayments,String interestCycle){
        List<Double> irrArray = new ArrayList<>();
        repayments.forEach(repayment -> {
            irrArray.add(repayment.getPlannedDueAmount());
        });
        //计算irr
        Double repayment_frequency = 1D;
        if ("YEAR".equalsIgnoreCase(interestCycle)) {
            repayment_frequency = 1D;
        } else if ("HALF_A_YEAR".equalsIgnoreCase(interestCycle)) {
            repayment_frequency = 2D;
        } else if ("QUARTER".equalsIgnoreCase(interestCycle)) {
            repayment_frequency = 4D;
        } else if ("MONTH".equalsIgnoreCase(interestCycle)) {
            repayment_frequency = 12D;
        }
        Double irr = IrrUtil.irr(irrArray) * repayment_frequency;//irr利率
        if ("NaN".equals(irr.toString())) {
            irr = 0D;
        }
        irr = (double) Math.round(irr * 1000000) / 1000000;
        return irr;
    }

    /**
     * 分摊
     * @param request
     * @param repayments
     * @param lonContractWithdraw
     */
    private void withdrawFinanceCost(IRequest request,List<HlsCusLonContractRepaymentMerge> repayments,HlsCusLonContractWithdraw lonContractWithdraw){
        if (repayments.size() > 0 ) {
            int month;
            //不同期次下的总起始日
            Calendar cStart = Calendar.getInstance();
            Calendar cEnd = Calendar.getInstance();

            //删除未确认计提数据
            HlsCusGldLonContractFinCost gldLonContractFinCostTmp = new HlsCusGldLonContractFinCost();
            gldLonContractFinCostTmp.setContractId(lonContractWithdraw.getContractId());
            gldLonContractFinCostTmp.setWithdrawId(lonContractWithdraw.getWithdrawId());
            gldLonContractFinCostTmp.setPostFlag("N");
            List<HlsCusGldLonContractFinCost> gldLonContractFinCostList = gldLonContractFinCostService.select(request, gldLonContractFinCostTmp, 1, 999999);
            gldLonContractFinCostService.batchDelete(gldLonContractFinCostList);
            List<HlsCusGldLonContractFinCost> gldLonContractFinCosts = new ArrayList<>();
            //初始化待插入计提数据
            HlsCusGldLonContractFinCost gldLonContractFinCost = new HlsCusGldLonContractFinCost();
            //从第一期开始
            for (int k = 1; k < repayments.size(); k++) {
                HlsCusLonContractRepaymentMerge repayment = repayments.get(k);
                Date calcStartDate = repayments.get(k-1).getPlannedDueDate();
                Double sumFinCost = 0.00D;
                Double finCost;
                Long calcDays;
                //总天数
                Long SumCalcDays;

                cStart.setTime(calcStartDate);
                cEnd.setTime(repayment.getPlannedDueDate());
                month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH);
                SumCalcDays = (cEnd.getTimeInMillis() - cStart.getTimeInMillis()) / (1000 * 3600 * 24);
                //相同期次下的每个月分摊起始日
                Calendar calcStart = Calendar.getInstance();
                Calendar calcEnd = Calendar.getInstance();
                calcStart.setTime(calcStartDate);

                //循环相差月份
                for (int i = 0; i <= month; i++) {
                    //还款当月前半段提款日至还款日，倒减计算
                    if (i == 0 && month == 0) {
                        //获取该还款期内计算开始日之前所有计提总和
                        calcEnd.setTime(repayment.getPlannedDueDate());
                        calcEnd.add(Calendar.DATE, -1);
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        //利息=当期总收益*天数/当月总天数
                        finCost = repayment.getProfit();
                        finCost = (double) Math.round(finCost * 100) / 100;
                    }
                    //还款当月后半段至月底，按天数权重计算
                    else if (i == 0 && month != 0) {
                        calcEnd.setTime(hlsCusLonContractWithdrawService.getMonthEndDate(calcStartDate));
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStartDate, calcEnd.getTime());
                        //利息=当期总收益*天数/当月总天数
                        finCost = repayment.getProfit() * calcDays / SumCalcDays;
                        finCost = (double) Math.round(finCost * 100) / 100;
                    }
                    //还款当月前半段月初至还款日，倒减计算
                    else if (i == month && month > 0) {
                        calcEnd.setTime(repayment.getPlannedDueDate());
                        calcEnd.add(Calendar.DATE, -1);
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        finCost = repayment.getProfit()- sumFinCost;
                        finCost = (double) Math.round(finCost * 100) / 100;

                    }
                    //整月计算，按天数权重计算
                    else {
                        calcEnd.setTime(hlsCusLonContractWithdrawService.getMonthEndDate(calcStart.getTime()));
                        calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
                        //利息=当期总收益*天数/当月总天数
                        finCost = repayment.getProfit() * calcDays / SumCalcDays;
                        finCost = (double) Math.round(finCost * 100) / 100;
                    }
                    gldLonContractFinCost.setContractId(lonContractWithdraw.getContractId());
                    gldLonContractFinCost.setWithdrawId(lonContractWithdraw.getWithdrawId());
                    gldLonContractFinCost.setGldPaymentId(repayment.getGldPaymentId());
                    gldLonContractFinCost.setStartDate(calcStart.getTime());
                    gldLonContractFinCost.setEndDate(calcEnd.getTime());
                    String periodName;
                    if (calcEnd.get(Calendar.MONTH) + 1 < 10) {
                        periodName = calcEnd.get(Calendar.YEAR) + "-0" + (calcEnd.get(Calendar.MONTH) + 1);

                    } else {
                        periodName = calcEnd.get(Calendar.YEAR) + "-" + (calcEnd.get(Calendar.MONTH) + 1);
                    }
                    gldLonContractFinCost.setPeriodName(periodName);
                    gldLonContractFinCost.setDays(calcDays);
                    gldLonContractFinCost.setFinanceCost(finCost);
                    gldLonContractFinCost.setFinanceIncomeInclud(finCost);
                    gldLonContractFinCost.setFinanceIncomeVat(finCost);
                    gldLonContractFinCost.setPostFlag("N");
                    gldLonContractFinCost.setFinanceCostId(null);


                    HlsCusGldLonContractFinCost newCost = new HlsCusGldLonContractFinCost();
                    Map<String, String> mapNewCost = hlsBeanRefUtilService.getFieldValueMap(gldLonContractFinCost);
                    hlsBeanRefUtilService.setFieldValue(newCost, mapNewCost);
                    newCost.set__status("insert");
                    gldLonContractFinCosts.add(newCost);
//                        gldLonContractFinCostService.insertSelective(request, gldLonContractFinCost);
                    sumFinCost = sumFinCost + finCost;
                    calcEnd.add(Calendar.DATE,1);
                    calcStart.setTime(calcEnd.getTime());
                }
            }

            //合并相同月份的期间
            List<HlsCusGldLonContractFinCost> returnCosts = mergeFinanceCostSamePeriod(gldLonContractFinCosts);
            insertFinCost(request,returnCosts);
        }
    }

    /**
     * 合并相同期间的月份
     * @param gldLonContractFinCosts
     * @return
     */
    private List<HlsCusGldLonContractFinCost>  mergeFinanceCostSamePeriod(List<HlsCusGldLonContractFinCost> gldLonContractFinCosts){
        List<HlsCusGldLonContractFinCost> returnCost = new ArrayList<>();
        for(int i = 0; i<gldLonContractFinCosts.size();i++){
            Boolean sameFlag = false;
            for(int j = 0;j<returnCost.size();j++){
                if(returnCost.get(j).getPeriodName().equals(gldLonContractFinCosts.get(i).getPeriodName())){
                    Double sumCost = CalculateUtil.add(gldLonContractFinCosts.get(i).getFinanceCost(),returnCost.get(j).getFinanceCost());
                    Long sumDays = gldLonContractFinCosts.get(i).getDays()+returnCost.get(j).getDays();
                    returnCost.get(j).setFinanceCost(sumCost);
                    returnCost.get(j).setFinanceIncomeInclud(sumCost);
                    returnCost.get(j).setFinanceIncomeVat(sumCost);
                    returnCost.get(j).setDays(sumDays);
                    returnCost.get(j).setEndDate(gldLonContractFinCosts.get(i).getEndDate());
                    sameFlag = true;
                }
            }
            if(!sameFlag){
                returnCost.add(gldLonContractFinCosts.get(i));
            }
        }
        return returnCost;
    }

    /**
     * 插入计提表,如果已经确认就把差额算到下一期分摊金额中
     */
    private void insertFinCost(IRequest request,List<HlsCusGldLonContractFinCost> returnCost){
        Double diffAmount = 0.00D;
        for(int i = 0;i<returnCost.size();i++){
            HlsCusGldLonContractFinCost gldLonContractFinCostExists = new HlsCusGldLonContractFinCost();
            gldLonContractFinCostExists.setContractId(returnCost.get(i).getContractId());
            gldLonContractFinCostExists.setWithdrawId(returnCost.get(i).getWithdrawId());
            gldLonContractFinCostExists.setPeriodName(returnCost.get(i).getPeriodName());
            List<HlsCusGldLonContractFinCost> gldLonContractFinCostExistsList = hlsCusGldLonContractFinCostMapper.queryLonContractFinCostForHistoryJc(gldLonContractFinCostExists);

            if (gldLonContractFinCostExistsList.size() == 0) {
                Double finCost = CalculateUtil.add(returnCost.get(i).getFinanceCost(),diffAmount);
                returnCost.get(i).setFinanceCost(finCost);
                returnCost.get(i).setFinanceIncomeVat(finCost);
                returnCost.get(i).setFinanceIncomeInclud(finCost);
                gldLonContractFinCostService.insert(request,returnCost.get(i));
                diffAmount = 0.00D;
            }else{
                diffAmount = CalculateUtil.add(diffAmount,CalculateUtil.sub(returnCost.get(i).getFinanceCost(),gldLonContractFinCostExistsList.get(0).getFinanceCost()));
            }
        }
    }

    /**
     * 一次性分摊分摊手续费/担保费
     * @param request
     * @param lonContractWithdraw
     */
    private void withdrawOtherFinanceCost(IRequest request,HlsCusLonContractWithdraw lonContractWithdraw,Long cfItem){
        //不同期次下的总起始日
        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();

        HlsCusLonContractRepayment lonContractRepayment = new HlsCusLonContractRepayment();
        lonContractRepayment.setWithdrawId(lonContractWithdraw.getWithdrawId());
        lonContractRepayment.setCfItem(cfItem);
        List<HlsCusLonContractRepayment> lonContractRepaymentList = hlsCusLonContractRepaymentService.select(request, lonContractRepayment, 1, 999999);
        lonContractRepaymentList.forEach(repayment ->{
            Calendar calcStart = Calendar.getInstance();
            Calendar calcEnd = Calendar.getInstance();
            calcStart.setTime(repayment.getPlannedDueDate());
            calcEnd.setTime(hlsCusLonContractWithdrawService.getMonthEndDate(repayment.getPlannedDueDate()));
            Long calcDays = hlsCusLonContractWithdrawService.getCalcDays(calcStart.getTime(), calcEnd.getTime());
            HlsCusGldLonContractFinCost gldLonContractFinCost = new HlsCusGldLonContractFinCost();
            gldLonContractFinCost.setContractId(lonContractWithdraw.getContractId());
            gldLonContractFinCost.setWithdrawId(lonContractWithdraw.getWithdrawId());
            gldLonContractFinCost.setRepaymentId(repayment.getRepaymentId());
            gldLonContractFinCost.setStartDate(calcStart.getTime());
            gldLonContractFinCost.setEndDate(calcEnd.getTime());
            String periodName;
            if (calcEnd.get(Calendar.MONTH) + 1 < 10) {
                periodName = calcEnd.get(Calendar.YEAR) + "-0" + (calcEnd.get(Calendar.MONTH) + 1);
            } else {
                periodName = calcEnd.get(Calendar.YEAR) + "-" + (calcEnd.get(Calendar.MONTH) + 1);
            }
            gldLonContractFinCost.setPeriodName(periodName);
            gldLonContractFinCost.setDays(calcDays);
            gldLonContractFinCost.setFinanceCost(repayment.getPlannedDueAmount());
            gldLonContractFinCost.setFinanceIncomeInclud(repayment.getPlannedDueAmount());
            gldLonContractFinCost.setFinanceIncomeVat(repayment.getPlannedDueAmount());
            gldLonContractFinCost.setCfItem(cfItem);
            gldLonContractFinCost.setPostFlag("N");
            gldLonContractFinCost.setFinanceCostId(null);

            HlsCusGldLonContractFinCost gldLonContractFinCostExists = new HlsCusGldLonContractFinCost();
            gldLonContractFinCostExists.setContractId(lonContractWithdraw.getContractId());
            gldLonContractFinCostExists.setWithdrawId(lonContractWithdraw.getWithdrawId());
            gldLonContractFinCostExists.setStartDate(calcStart.getTime());
            gldLonContractFinCostExists.setEndDate(calcEnd.getTime());
            gldLonContractFinCostExists.setCfItem(cfItem);
            gldLonContractFinCostExists.setRepaymentId(repayment.getRepaymentId());
            List<HlsCusGldLonContractFinCost> gldLonContractFinCostExistsList = gldLonContractFinCostService.select(request, gldLonContractFinCostExists, 1, 999999);

            if (gldLonContractFinCostExistsList.size() == 0) {
                gldLonContractFinCostService.insertSelective(request, gldLonContractFinCost);
            }

        });
    }

}