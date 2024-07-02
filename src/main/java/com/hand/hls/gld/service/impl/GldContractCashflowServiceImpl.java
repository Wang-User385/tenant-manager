package com.hand.hls.gld.service.impl;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:
 * @author: congweijing
 * @date: 2021/5/6 10:51
 */
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.gld.dto.GldContractCashflow;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.mapper.GldContractCashflowMapper;
import com.hand.hls.gld.mapper.HlsCusContractFinanceIncomeMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.HlsCusContractFinanceIncomeService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.gld.utils.IrrUtil;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.utils.HlsCusXirr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GldContractCashflowServiceImpl extends BaseServiceImpl<GldContractCashflow> implements IGldContractCashflowService {
    @Autowired
    private GldContractCashflowMapper gldContractCashflowMapper;
    @Autowired
    private HlsCusContractFinanceIncomeService contractFinanceIncomeService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;
    @Autowired
    private HlsCusContractFinanceIncomeMapper contractFinanceIncomeMapper;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    public GldContractCashflowServiceImpl() {
    }

    /**
     * 446,305用这两个id来测试
     * @param request
     * @param contractId
     * @param vatRate
     * @param xirr
     */
    @Override
    public void clacFinanceIncome(IRequest request, Long contractId, Double vatRate, Double xirr){
        //获取变更起始期数(changeTerm默认为零，如果是变更需要查询变更起始期数，则变更期数之前的不变，之后的重新分摊)
        Long changeTerm = 0L;
        Long changeTermQuery = gldContractCashflowMapper.queryChangeTermCashflow(contractId);
        if(changeTermQuery != null){
            changeTerm = changeTermQuery;
        }
        //先删除之前计算的表(只删除变更期数之后的表)
        GldContractCashflow para = new GldContractCashflow();
        para.setContractId(contractId);
         List<GldContractCashflow> deleteResult = this.select(request,para,1,9999999);
        for(GldContractCashflow item:deleteResult){
            if(item.getTimes() >= changeTerm){
                this.deleteByPrimaryKey(item);
            }
        }
        //这里没有算上cf_item=52的现金流,在calcIrr里面有考虑的
        List<GldContractCashflow> gldContractCashflows = gldContractCashflowMapper.queryMergeCashflow(contractId);
        Double totalIncome = gldContractCashflowMapper.queryTotalIncome(contractId);

        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = hlsCusConContractService.selectByPrimaryKey(request,c);
        //Double irr = c.getIrr();
        Double irr = calcIrr(gldContractCashflows);
        //xirr = (double) Math.round(xirr * 100000000) / 100000000;
        Double SumPlannedDueAmount = 0D;
        Double SumProfit = 0D;
        for(int i = 0;i< gldContractCashflows.size();i++){
            //如果是变更前取变更前的记录，变更后的重新计算
            if(gldContractCashflows.get(i).getTimes()<changeTerm){
                GldContractCashflow hlsCusGldContractCashflow = new GldContractCashflow();
                hlsCusGldContractCashflow.setContractId(contractId);
                hlsCusGldContractCashflow.setTimes(gldContractCashflows.get(i).getTimes());
                List<GldContractCashflow> timeResult = gldContractCashflowMapper.queryMergeCashflowLast(hlsCusGldContractCashflow);
                if(timeResult.size()>0){
                    gldContractCashflows.set(i,timeResult.get(0));
                    if(i != 0 && i!= gldContractCashflows.size()-1){
                        SumProfit = SumProfit + gldContractCashflows.get(i).getFinanceIncome();
                    }
                }
            }else if(i == 0){
                gldContractCashflows.get(i).setCurrentNetAmount(-gldContractCashflows.get(i).getDueAmount());
                gldContractCashflows.get(i).setFinanceIncome(0d);
            }else if(i == gldContractCashflows.size()-1){
                //最后一期计算收益、减少额、净额
                Double profit =  (double) Math.round((totalIncome - SumProfit) * 100) / 100;
//                Double reduction = (double) Math.round((gldContractCashflows.get(i).getDueAmount() - profit) * 100) / 100;
//                Double currentNetAmount = (double) Math.round((gldContractCashflows.get(i-1).getCurrentNetAmount() - reduction) * 100) / 100;

                gldContractCashflows.get(i).setFinanceIncome(profit);
                gldContractCashflows.get(i).setNetIncome((double) Math.round((gldContractCashflows.get(i).getDueAmount()/(1+vatRate)) * 100) / 100);

            }else{

                Double profit = gldContractCashflows.get(i).getFinanceIncome();
                gldContractCashflows.get(i).setFinanceIncome(profit);
                gldContractCashflows.get(i).setNetIncome((double) Math.round((gldContractCashflows.get(i).getDueAmount()/(1+vatRate)) * 100) / 100);
                SumProfit = SumProfit + profit;
            }
            gldContractCashflows.get(i).setContractId(contractId);
            if(gldContractCashflows.get(i).getTimes()>=changeTerm){
                self().insert(request,gldContractCashflows.get(i));
            }

            gldContractCashflows = gldContractCashflowMapper.queryMergeCashflow(contractId);




        }

        try {
            //分摊算法
            this.contractFinanceIncome(request,gldContractCashflows, contractId,irr);

        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    @Override
    public void clacFinanceIncomeRetail(IRequest request, Long contractId, Double vatRate, Double xirr) {
        //获取变更起始期数(changeTerm默认为零，如果是变更需要查询变更起始期数，则变更期数之前的不变，之后的重新分摊)
        Long changeTerm = 0L;
        Long changeTermQuery = gldContractCashflowMapper.queryChangeTermCashflow(contractId);
        if(changeTermQuery != null){
            changeTerm = changeTermQuery;
        }
        //先删除之前计算的表(只删除变更期数之后的表)
        GldContractCashflow para = new GldContractCashflow();
        para.setContractId(contractId);
        List<GldContractCashflow> deleteResult = this.select(request,para,1,9999999);
        for(GldContractCashflow item:deleteResult){
            if(item.getTimes() >= changeTerm){
                this.deleteByPrimaryKey(item);
            }
        }
        //这里没有算上cf_item=52的现金流,在calcIrr里面有考虑的
        List<GldContractCashflow> gldContractCashflows = gldContractCashflowMapper.queryMergeCashflowRetail(contractId);
        Double totalIncome = gldContractCashflowMapper.queryTotalIncome(contractId);

        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = hlsCusConContractService.selectByPrimaryKey(request,c);
        Double irr = calcIrrRetail(gldContractCashflows);
        Double SumProfit = 0D;
        for(int i = 0;i< gldContractCashflows.size();i++){
            //如果是变更前取变更前的记录，变更后的重新计算
            if(gldContractCashflows.get(i).getTimes()<changeTerm){
                GldContractCashflow hlsCusGldContractCashflow = new GldContractCashflow();
                hlsCusGldContractCashflow.setContractId(contractId);
                hlsCusGldContractCashflow.setTimes(gldContractCashflows.get(i).getTimes());
                List<GldContractCashflow> timeResult = gldContractCashflowMapper.queryMergeCashflowLast(hlsCusGldContractCashflow);
                if(timeResult.size()>0){
                    gldContractCashflows.set(i,timeResult.get(0));
                    if(i != 0 && i!= gldContractCashflows.size()-1){
                        SumProfit = SumProfit + gldContractCashflows.get(i).getFinanceIncome();
                    }
                }
            }else if(i == 0){
                gldContractCashflows.get(i).setCurrentNetAmount(-gldContractCashflows.get(i).getDueAmount());
                gldContractCashflows.get(i).setFinanceIncome(0d);
            }else if(i == gldContractCashflows.size()-1){
                //最后一期计算收益、减少额、净额
                Double profit =  (double) Math.round((totalIncome - SumProfit) * 100) / 100;
                gldContractCashflows.get(i).setFinanceIncome(profit);
                gldContractCashflows.get(i).setNetIncome((double) Math.round((gldContractCashflows.get(i).getDueAmount()/(1+vatRate)) * 100) / 100);

            }else{

                Double profit = gldContractCashflows.get(i).getFinanceIncome();
                gldContractCashflows.get(i).setFinanceIncome(profit);
                gldContractCashflows.get(i).setNetIncome((double) Math.round((gldContractCashflows.get(i).getDueAmount()/(1+vatRate)) * 100) / 100);
                SumProfit = SumProfit + profit;
            }
            gldContractCashflows.get(i).setContractId(contractId);
            if(gldContractCashflows.get(i).getTimes()>=changeTerm){
                self().insert(request,gldContractCashflows.get(i));
            }

            gldContractCashflows = gldContractCashflowMapper.queryMergeCashflowRetail(contractId);
        }

        try {
            //分摊算法
            this.contractFinanceIncomeRetail(request,gldContractCashflows, contractId,irr);

        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * 计算IRR
     * 计算IRR
     * @param gldContractCashflows
     * @return
     */
    private Double calcIrr(List<GldContractCashflow> gldContractCashflows){
        List<Double> dueAmountList = new ArrayList<Double>();
        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();
        Calendar pStart = Calendar.getInstance();
        Calendar pEnd = Calendar.getInstance();

        GldContractCashflow firstCashflow = gldContractCashflows.get(0);
        GldContractCashflow lastCashflow = gldContractCashflows.get(gldContractCashflows.size()-1);
        cStart.setTime(firstCashflow.getFinIncomeDate());
        cEnd.setTime(lastCashflow.getFinIncomeDate());
        pStart.setTime(cStart.getTime());
        pStart.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),1);
        pEnd.setTime(pStart.getTime());
        pEnd.add(Calendar.MONTH,1);
        pEnd.add(Calendar.DATE,-1);
        int month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH)+1;
        for (int i = 0; i <= month; i++){
            if(i==0){
                dueAmountList.add(gldContractCashflows.get(0).getDueAmount());
                continue;
            }
            //这个地方的逻辑意思是如果第0期是当月最后一天,那么算irr的时候就是一个月一条数据,如果不是最后一天,第一个月就有两条数据,不然算出的irr会有比较大的差异
            if(i==1){
                //首期租金和0期现金流同月的情况
                List<GldContractCashflow> nCash = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis()
                        && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
                if(nCash.size()>0){
                    dueAmountList.add(nCash.get(0).getDueAmount());
                    pStart.add(Calendar.MONTH,1);
                    pEnd.add(Calendar.MONTH,1);
                    continue;
                }else{
                    Calendar temp = Calendar.getInstance();
                    temp.setTime(firstCashflow.getFinIncomeDate());
                    temp.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),pStart.getActualMaximum(Calendar.DAY_OF_MONTH));
                    if(temp.getTimeInMillis()==cStart.getTimeInMillis()){
                        pStart.add(Calendar.MONTH,1);
                        pEnd.add(Calendar.MONTH,1);
                        continue;
                    }
                    dueAmountList.add(0d);
                    pStart.add(Calendar.MONTH,1);
                    pEnd.add(Calendar.MONTH,1);
                    continue;
                }
            }
            List<GldContractCashflow> l = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis() && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
            if(l.size()==0){
                dueAmountList.add(0d);
            }else{
                if(i==month){
                    double lastAmount = gldContractCashflowMapper.queryLastCf(gldContractCashflows.get(0).getContractId());
                    dueAmountList.add(HlsCusMathUtil.add(l.get(0).getDueAmount(),lastAmount,2));
                }else{
                    dueAmountList.add(l.get(0).getDueAmount());

                }

            }
            pStart.add(Calendar.MONTH,1);
            pEnd.add(Calendar.MONTH,1);
            pEnd.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),pStart.getActualMaximum(Calendar.DAY_OF_MONTH));

        }

        //计算xirr
        for(int i=0;i<dueAmountList.size();i++){
            System.out.println("irr============"+dueAmountList.get(i).toString());
        }

        //计算xirr
        for(int i=0;i<dueAmountList.size();i++){
            System.out.println("irr=========="+dueAmountList.get(i).toString());
        }
        Double irr = IrrUtil.irr(dueAmountList) ;// irr利率
        return irr;
    }

    /**
     * 计算IRR
     * 计算IRR
     * @param gldContractCashflows
     * @return
     */
    private Double calcIrrRetail(List<GldContractCashflow> gldContractCashflows){
        List<Double> dueAmountList = new ArrayList<Double>();
        Calendar cStart = Calendar.getInstance();
        Calendar cEnd = Calendar.getInstance();
        Calendar pStart = Calendar.getInstance();
        Calendar pEnd = Calendar.getInstance();

        GldContractCashflow firstCashflow = gldContractCashflows.get(0);
        GldContractCashflow lastCashflow = gldContractCashflows.get(gldContractCashflows.size()-1);
        cStart.setTime(firstCashflow.getFinIncomeDate());
        cEnd.setTime(lastCashflow.getFinIncomeDate());
        pStart.setTime(cStart.getTime());
        pStart.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),1);
        pEnd.setTime(pStart.getTime());
        pEnd.add(Calendar.MONTH,1);
        pEnd.add(Calendar.DATE,-1);
        int month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH)+1;
        for (int i = 0; i <= month; i++){
            if(i==0){
                dueAmountList.add(gldContractCashflows.get(0).getDueAmount());
                continue;
            }
            //这个地方的逻辑意思是如果第0期是当月最后一天,那么算irr的时候就是一个月一条数据,如果不是最后一天,第一个月就有两条数据,不然算出的irr会有比较大的差异
            if(i==1){
                //首期租金和0期现金流同月的情况
                List<GldContractCashflow> nCash = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis()
                        && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
                if(nCash.size()>0){
                    dueAmountList.add(nCash.get(0).getDueAmount());
                    pStart.add(Calendar.MONTH,1);
                    pEnd.add(Calendar.MONTH,1);
                    continue;
                }else{
                    Calendar temp = Calendar.getInstance();
                    temp.setTime(firstCashflow.getFinIncomeDate());
                    temp.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),pStart.getActualMaximum(Calendar.DAY_OF_MONTH));
                    if(temp.getTimeInMillis()==cStart.getTimeInMillis()){
                        pStart.add(Calendar.MONTH,1);
                        pEnd.add(Calendar.MONTH,1);
                        continue;
                    }
                    dueAmountList.add(0d);
                    pStart.add(Calendar.MONTH,1);
                    pEnd.add(Calendar.MONTH,1);
                    continue;
                }
            }
            List<GldContractCashflow> l = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis() && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
            if(l.size()==0){
                dueAmountList.add(0d);
            }else{
                if(i==month){
                    double lastAmount = gldContractCashflowMapper.queryLastCfRetail(gldContractCashflows.get(0).getContractId());
                    dueAmountList.add(HlsCusMathUtil.add(l.get(0).getDueAmount(),lastAmount,2));
                }else{
                    dueAmountList.add(l.get(0).getDueAmount());

                }

            }
            pStart.add(Calendar.MONTH,1);
            pEnd.add(Calendar.MONTH,1);
            pEnd.set(pStart.get(Calendar.YEAR),pStart.get(Calendar.MONTH),pStart.getActualMaximum(Calendar.DAY_OF_MONTH));

        }

        //计算xirr
        for(int i=0;i<dueAmountList.size();i++){
            System.out.println("irr============"+dueAmountList.get(i).toString());
        }

        //计算xirr
        for(int i=0;i<dueAmountList.size();i++){
            System.out.println("irr=========="+dueAmountList.get(i).toString());
        }
        Double irr = IrrUtil.irr(dueAmountList) ;// irr利率
        return irr;
    }

    private void contractFinanceIncome(IRequest request,List<GldContractCashflow> gldContractCashflows,Long contractId,double irr){
        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = hlsCusConContractService.selectByPrimaryKey(request,c);
        if (gldContractCashflows.size() > 0 ) {
            int month;
            //不同期次下的总起始日
            Calendar cStart = Calendar.getInstance();
            Calendar cEnd = Calendar.getInstance();
            double n01=0;
            double n02=0;
            double n03=0;
            double n04=0;
            double n05=0;
            double n06=0;
            double n07=0;
            double n08=0;

            //删除未确认计提数据
            HlsCusContractFinanceIncome contractFinanceIncomeTmp = new HlsCusContractFinanceIncome();
            contractFinanceIncomeTmp.setContractId(contractId);
            contractFinanceIncomeTmp.setPostFlag("N");
            List<HlsCusContractFinanceIncome> contractFinanceIncomeList = contractFinanceIncomeService.select(request, contractFinanceIncomeTmp, 1, 999999);
            contractFinanceIncomeService.batchDelete(contractFinanceIncomeList);
            List<HlsCusContractFinanceIncome> gldContractFinIncomes = new ArrayList<>();
            //初始化待插入计提数据
            HlsCusContractFinanceIncome gldLonContractFinCost = new HlsCusContractFinanceIncome();
            HlsCusConContract hc = new HlsCusConContract();
            hc.setContractId(contractId);
            hc=hlsCusConContractService.selectByPrimaryKey(request,hc);
            Double totalIncome = gldContractCashflowMapper.queryTotalIncome(contractId);
            GldContractCashflow firstCashflow = gldContractCashflows.get(0);
            GldContractCashflow lastCashflow = gldContractCashflows.get(gldContractCashflows.size()-1);
            cStart.setTime(firstCashflow.getFinIncomeDate());
            cEnd.setTime(lastCashflow.getFinIncomeDate());
            month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH)+1;
            double sumFin = 0;
            double finIncome = 0;
            Calendar pStart =  Calendar.getInstance();
            Calendar pEnd =  Calendar.getInstance();
            pStart.setTime(cStart.getTime());
            pEnd.setTime(cStart.getTime());
            //算法如下:当期(比方说第3期)的收益计算公式:上一期(第2期)净余额*irr/12
            //上期(第2期)的净余额=上上期(第1期)净余额+上期(第2期)确认金额-上期(第2期)应还租金
            for (int i = 0; i <= month; i++) {
                if(i==0) {
                    if(pStart.get(Calendar.DATE)==pStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                        // 如果起始期是当月最后一天，需要少算一期分摊
                        month--;
                    }
                    finIncome = 0;
                    n05 = firstCashflow.getDueAmount()*-1;
                    n02 = gldContractCashflowMapper.query0Times(contractId);
                    n01 = firstCashflow.getDueAmount();
                }
              else if(i==month){
                    finIncome=HlsCusMathUtil.sub(totalIncome,sumFin,2);
                    pStart.setTime(pEnd.getTime());
                    pStart.add(Calendar.DATE,1);
                    pEnd.setTime(lastCashflow.getFinIncomeDate());
                    finIncome=HlsCusMathUtil.mul(n05,irr,2);
                    n03=HlsCusMathUtil.mul(n05,irr,2);
                    List<GldContractCashflow> l = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis() && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
                    double lastAmount = gldContractCashflowMapper.queryLastCf(contractId);

                    if(l.size()==0){
                        n02 = 0;
                        n01 = n02;
                    }else{
                        n02 = HlsCusMathUtil.add(l.get(0).getDueAmount(),lastAmount,2);
                        n01 = n02;
                    }

                    n04=HlsCusMathUtil.sub(n02,n03,2);
                    n05=HlsCusMathUtil.sub(n05,n04,2);
                    if(n05!=0){
                        n03=HlsCusMathUtil.sub(n03,n05);
                        n04=HlsCusMathUtil.sub(n02,n03,2);

                    }
                    n05=0d;
                }else{
                  // 判断是否为1、2期在同一月的情况
                    boolean dataFlag = false;
                    if(i==1){
                        pEnd.set(pEnd.get(Calendar.YEAR),pEnd.get(Calendar.MONTH),pEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                        if(pEnd.getTime().equals(pStart.getTime())){
                            pStart.add(Calendar.DATE,1);
                            pEnd.add(Calendar.MONTH,1);
                        }else {
                            // 不相等代表为同一月
                            dataFlag = true;
                        }
                    }else{
                        pStart.setTime(pEnd.getTime());
                        pStart.add(Calendar.DATE,1);
                        pEnd.add(Calendar.MONTH,1);
                        pEnd.set(pEnd.get(Calendar.YEAR),pEnd.get(Calendar.MONTH),pEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }

                    List<GldContractCashflow> l = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis() && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
                    if(l.size()==0){
                        n02 = 0;
                        n01 = n02;
                    }else{
                        n02 = l.get(0).getDueAmount();
                        n01 = n02;
                    }

                    finIncome=HlsCusMathUtil.mul(n05,irr,2);

                    // irr需要取4位小数计算
//                    n03=HlsCusMathUtil.mul(n05,irr,2);
                    BigDecimal b1 = new BigDecimal(Double.toString(n05));
                    BigDecimal b2 = new BigDecimal(Double.toString(irr));
                    b2 = b2.setScale(12, RoundingMode.HALF_UP);
                    n03 =  b1.multiply(b2).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    // 按天计息
                    if (dataFlag){
                        n03 = HlsCusMathUtil.div(n03,30D,2);
                        n03 = HlsCusMathUtil.mul(n03, DateUtil.between(pStart.getTime(), pEnd.getTime(), DateUnit.DAY),2);
                    }
                    n04=HlsCusMathUtil.sub(n02,n03,2);
                    n05=HlsCusMathUtil.sub(n05,n04,2);

                }

//                if(i!=0){
                    sumFin= HlsCusMathUtil.add(sumFin,finIncome);

                    gldLonContractFinCost.setContractId(contractId);
                    gldLonContractFinCost.setStartDate(pStart.getTime());
                    gldLonContractFinCost.setEndDate(pEnd.getTime());
                    String periodName;
                    if (pEnd.get(Calendar.MONTH) + 1 < 10) {
                        periodName = pEnd.get(Calendar.YEAR) + "-0" + (pEnd.get(Calendar.MONTH) + 1);
                    } else {
                        periodName = pEnd.get(Calendar.YEAR) + "-" + (pEnd.get(Calendar.MONTH) + 1);
                    }
                    gldLonContractFinCost.setPeriodName(periodName);
                    gldLonContractFinCost.setFinanceIncomeInclud(n03);
                    n08=HlsCusMathUtil.mul(n03,hc.getVatRate()/(1+hc.getVatRate()),2);
                    n07=HlsCusMathUtil.sub(n03,n08);
                    //判断是不是pstart是不是第一天是第一天才需要加1,如果不是第一天则不需要加1
                long dayBalance=0;
                if(pStart.get(Calendar.DATE)==1){
                    dayBalance = pEnd.get(Calendar.DAY_OF_YEAR) - pStart.get(Calendar.DAY_OF_YEAR)+1;
                }else{
                    dayBalance = pEnd.get(Calendar.DAY_OF_YEAR) - pStart.get(Calendar.DAY_OF_YEAR);
                }


                    Calendar temp = Calendar.getInstance();

                    n06=HlsCusMathUtil.add(c.getLeaseItemAmount()*c.getIntRate()*(dayBalance+1)/360,0,2);
                    gldLonContractFinCost.setRefN01(n01);
                    gldLonContractFinCost.setRefN02(n02);
                    gldLonContractFinCost.setRefN03(n03);
                    gldLonContractFinCost.setRefN04(n04);
                    gldLonContractFinCost.setRefN05(n05);
                    if(n03==0){
                        n06=0;
                    }
                    gldLonContractFinCost.setRefN06(n06);
                    gldLonContractFinCost.setRefN07(n07);
                    gldLonContractFinCost.setRefN08(n08);
                    gldLonContractFinCost.setRefN09(irr);
                    gldLonContractFinCost.setFinanceIncome(n07);
                    gldLonContractFinCost.setFinanceIncomeVat(n08);
                    gldLonContractFinCost.setPostFlag("N");
                    contractFinanceIncomeService.insert(request,gldLonContractFinCost);
//                }


            }

        }
    }


    private void contractFinanceIncomeRetail(IRequest request,List<GldContractCashflow> gldContractCashflows,Long contractId,double irr){
        HlsCusConContract c = new HlsCusConContract();
        c.setContractId(contractId);
        c = hlsCusConContractService.selectByPrimaryKey(request,c);
        if (gldContractCashflows.size() > 0 ) {
            int month;
            //不同期次下的总起始日
            Calendar cStart = Calendar.getInstance();
            Calendar cEnd = Calendar.getInstance();
            double n01=0;
            double n02=0;
            double n03=0;
            double n04=0;
            double n05=0;
            double n06=0;
            double n07=0;
            double n08=0;

            //删除未确认计提数据
            HlsCusContractFinanceIncome contractFinanceIncomeTmp = new HlsCusContractFinanceIncome();
            contractFinanceIncomeTmp.setContractId(contractId);
            contractFinanceIncomeTmp.setPostFlag("N");
            List<HlsCusContractFinanceIncome> contractFinanceIncomeList = contractFinanceIncomeService.select(request, contractFinanceIncomeTmp, 1, 999999);
            contractFinanceIncomeService.batchDelete(contractFinanceIncomeList);
            List<HlsCusContractFinanceIncome> gldContractFinIncomes = new ArrayList<>();
            //初始化待插入计提数据
            HlsCusContractFinanceIncome gldLonContractFinCost = new HlsCusContractFinanceIncome();
            HlsCusConContract hc = new HlsCusConContract();
            hc.setContractId(contractId);
            hc=hlsCusConContractService.selectByPrimaryKey(request,hc);
            Double totalIncome = gldContractCashflowMapper.queryTotalIncome(contractId);
            GldContractCashflow firstCashflow = gldContractCashflows.get(0);
            GldContractCashflow lastCashflow = gldContractCashflows.get(gldContractCashflows.size()-1);
            cStart.setTime(firstCashflow.getFinIncomeDate());
            cEnd.setTime(lastCashflow.getFinIncomeDate());
            month = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)) * 12 + cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH)+1;
            double sumFin = 0;
            double finIncome = 0;
            Calendar pStart =  Calendar.getInstance();
            Calendar pEnd =  Calendar.getInstance();
            pStart.setTime(cStart.getTime());
            pEnd.setTime(cStart.getTime());
            //算法如下:当期(比方说第3期)的收益计算公式:上一期(第2期)净余额*irr/12
            //上期(第2期)的净余额=上上期(第1期)净余额+上期(第2期)确认金额-上期(第2期)应还租金
            for (int i = 0; i <= month; i++) {
                if(i==0) {
                    if(pStart.get(Calendar.DATE)==pStart.getActualMaximum(Calendar.DAY_OF_MONTH)){
                        // 如果起始期是当月最后一天，需要少算一期分摊
                        month--;
                    }
                    finIncome = 0;
                    n05 = firstCashflow.getDueAmount()*-1;
                    n02 = gldContractCashflowMapper.query0Times(contractId);
                    n01 = firstCashflow.getDueAmount();
                }
                else if(i==month){
                    finIncome=HlsCusMathUtil.sub(totalIncome,sumFin,2);
                    pStart.setTime(pEnd.getTime());
                    pStart.add(Calendar.DATE,1);
                    pEnd.setTime(lastCashflow.getFinIncomeDate());
                    finIncome=HlsCusMathUtil.mul(n05,irr,2);
                    n03=HlsCusMathUtil.mul(n05,irr,2);
                    List<GldContractCashflow> l = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis() && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
                    double lastAmount = gldContractCashflowMapper.queryLastCfRetail(contractId);

                    if(l.size()==0){
                        n02 = 0;
                        n01 = n02;
                    }else{
                        n02 = HlsCusMathUtil.add(l.get(0).getDueAmount(),lastAmount,2);
                        n01 = n02;
                    }

                    n04=HlsCusMathUtil.sub(n02,n03,2);
                    n05=HlsCusMathUtil.sub(n05,n04,2);
                    if(n05!=0){
                        n03=HlsCusMathUtil.sub(n03,n05);
                        n04=HlsCusMathUtil.sub(n02,n03,2);

                    }
                    n05=0d;
                }else{
                    // 判断是否为1、2期在同一月的情况
                    boolean dataFlag = false;
                    if(i==1){
                        pEnd.set(pEnd.get(Calendar.YEAR),pEnd.get(Calendar.MONTH),pEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                        if(pEnd.getTime().equals(pStart.getTime())){
                            pStart.add(Calendar.DATE,1);
                            pEnd.add(Calendar.MONTH,1);
                        }else {
                            // 不相等代表为同一月
                            dataFlag = true;
                        }
                    }else{
                        pStart.setTime(pEnd.getTime());
                        pStart.add(Calendar.DATE,1);
                        pEnd.add(Calendar.MONTH,1);
                        pEnd.set(pEnd.get(Calendar.YEAR),pEnd.get(Calendar.MONTH),pEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }

                    List<GldContractCashflow> l = gldContractCashflows.stream().filter(a-> a.getTimes()>0&&a.getFinIncomeDate().getTime()<=pEnd.getTimeInMillis() && a.getFinIncomeDate().getTime()>=pStart.getTimeInMillis()).collect(Collectors.toList());
                    if(l.size()==0){
                        n02 = 0;
                        n01 = n02;
                    }else{
                        n02 = l.get(0).getDueAmount();
                        n01 = n02;
                    }

                    finIncome=HlsCusMathUtil.mul(n05,irr,2);

                    // irr需要取4位小数计算
//                    n03=HlsCusMathUtil.mul(n05,irr,2);
                    BigDecimal b1 = new BigDecimal(Double.toString(n05));
                    BigDecimal b2 = new BigDecimal(Double.toString(irr));
                    b2 = b2.setScale(12, RoundingMode.HALF_UP);
                    n03 =  b1.multiply(b2).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    // 按天计息
                    if (dataFlag){
                        n03 = HlsCusMathUtil.div(n03,30D,2);
                        n03 = HlsCusMathUtil.mul(n03, DateUtil.between(pStart.getTime(), pEnd.getTime(), DateUnit.DAY),2);
                    }
                    n04=HlsCusMathUtil.sub(n02,n03,2);
                    n05=HlsCusMathUtil.sub(n05,n04,2);

                }

//                if(i!=0){
                sumFin= HlsCusMathUtil.add(sumFin,finIncome);

                gldLonContractFinCost.setContractId(contractId);
                gldLonContractFinCost.setStartDate(pStart.getTime());
                gldLonContractFinCost.setEndDate(pEnd.getTime());
                String periodName;
                if (pEnd.get(Calendar.MONTH) + 1 < 10) {
                    periodName = pEnd.get(Calendar.YEAR) + "-0" + (pEnd.get(Calendar.MONTH) + 1);
                } else {
                    periodName = pEnd.get(Calendar.YEAR) + "-" + (pEnd.get(Calendar.MONTH) + 1);
                }
                gldLonContractFinCost.setPeriodName(periodName);
                gldLonContractFinCost.setFinanceIncomeInclud(n03);
                n08=HlsCusMathUtil.mul(n03,hc.getVatRate()/(1+hc.getVatRate()),2);
                n07=HlsCusMathUtil.sub(n03,n08);
                //判断是不是pstart是不是第一天是第一天才需要加1,如果不是第一天则不需要加1
                long dayBalance=0;
                if(pStart.get(Calendar.DATE)==1){
                    dayBalance = pEnd.get(Calendar.DAY_OF_YEAR) - pStart.get(Calendar.DAY_OF_YEAR)+1;
                }else{
                    dayBalance = pEnd.get(Calendar.DAY_OF_YEAR) - pStart.get(Calendar.DAY_OF_YEAR);
                }


                Calendar temp = Calendar.getInstance();

                n06=HlsCusMathUtil.add(c.getLeaseItemAmount()*c.getIntRate()*(dayBalance+1)/360,0,2);
                gldLonContractFinCost.setRefN01(n01);
                gldLonContractFinCost.setRefN02(n02);
                gldLonContractFinCost.setRefN03(n03);
                gldLonContractFinCost.setRefN04(n04);
                gldLonContractFinCost.setRefN05(n05);
                if(n03==0){
                    n06=0;
                }
                gldLonContractFinCost.setRefN06(n06);
                gldLonContractFinCost.setRefN07(n07);
                gldLonContractFinCost.setRefN08(n08);
                gldLonContractFinCost.setRefN09(irr);
                gldLonContractFinCost.setFinanceIncome(n07);
                gldLonContractFinCost.setFinanceIncomeVat(n08);
                gldLonContractFinCost.setPostFlag("N");
                contractFinanceIncomeService.insert(request,gldLonContractFinCost);
//                }


            }

        }
    }


    /**
     * 合并相同期间的月份
     * @param gldContractFinIncomes
     * @return
     */
    private List<HlsCusContractFinanceIncome>  mergeFinanceCostSamePeriod(List<HlsCusContractFinanceIncome> gldContractFinIncomes){
        List<HlsCusContractFinanceIncome> returnCost = new ArrayList<>();
        for(int i = 0; i<gldContractFinIncomes.size();i++){
            Boolean sameFlag = false;
            for(int j = 0;j<returnCost.size();j++){
                if(returnCost.get(j).getPeriodName().equals(gldContractFinIncomes.get(i).getPeriodName())){
                    Double sumCost = CalculateUtil.add(gldContractFinIncomes.get(i).getFinanceIncome(),returnCost.get(j).getFinanceIncome());
                    Long sumDays = gldContractFinIncomes.get(i).getDays()+returnCost.get(j).getDays();
                    returnCost.get(j).setFinanceIncome(sumCost);
                    returnCost.get(j).setFinanceIncomeInclud(sumCost);
                    returnCost.get(j).setFinanceIncomeVat(sumCost);
                    returnCost.get(j).setDays(sumDays);
                    returnCost.get(j).setEndDate(gldContractFinIncomes.get(i).getEndDate());
                    sameFlag = true;
                }
            }
            if(!sameFlag){
                returnCost.add(gldContractFinIncomes.get(i));
            }
        }
        return returnCost;
    }


    /**
     * 插入计提表,如果已经确认就把差额算到下一期分摊金额中
     */
    private void insertFinCost(IRequest request,List<HlsCusContractFinanceIncome> returnCost){
        Double diffAmount = 0.00D;
        for(int i = 0;i<returnCost.size();i++){
            HlsCusContractFinanceIncome gldLonContractFinCostExists = new HlsCusContractFinanceIncome();
            gldLonContractFinCostExists.setContractId(returnCost.get(i).getContractId());
            gldLonContractFinCostExists.setPeriodName(returnCost.get(i).getPeriodName());
            List<HlsCusContractFinanceIncome> gldLonContractFinCostExistsList = contractFinanceIncomeMapper.select(gldLonContractFinCostExists);

            if (gldLonContractFinCostExistsList.size() == 0) {
                Double finCost = CalculateUtil.add(returnCost.get(i).getFinanceIncome(),diffAmount);
                returnCost.get(i).setFinanceIncome(finCost);
                returnCost.get(i).setFinanceIncomeVat(finCost);
                returnCost.get(i).setFinanceIncomeInclud(finCost);
                contractFinanceIncomeService.insert(request,returnCost.get(i));
                diffAmount = 0.00D;
            }else{
                diffAmount = CalculateUtil.add(diffAmount,CalculateUtil.sub(returnCost.get(i).getFinanceIncome(),gldLonContractFinCostExistsList.get(0).getFinanceIncome()));
            }
        }
    }

}
