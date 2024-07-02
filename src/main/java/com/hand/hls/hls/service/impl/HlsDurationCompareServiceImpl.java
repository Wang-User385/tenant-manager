package com.hand.hls.hls.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.hls.dto.HlsDurationCompare;
import com.hand.hls.hls.dto.HlsDurationLn;
import com.hand.hls.hls.mapper.HlsDurationCompareMapper;
import com.hand.hls.hls.service.HlsDurationCompareService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import static com.hand.hls.sys.utils.OracleUtils.nvl;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/6/21
 * @description:
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsDurationCompareServiceImpl extends BaseServiceImpl<HlsDurationCompare> implements HlsDurationCompareService {

    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;
    @Autowired
    private HlsCusPrjQuotationService prjQuotationService;
    @Autowired
    private HlsDurationCompareMapper hlsDurationCompareMapper;


    @Override
    public void createCompare(IRequest iRequest, HlsDurationLn ln) {
        HlsDurationCompare compare = new HlsDurationCompare();
        compare.setHdId(ln.getHdId());
        compare.setLnId(ln.getLnId());


        String sourceType = ln.getSourceType();
        Long quotationId = ln.getEtQuotationId();
        if ("ET".equals(sourceType)) {
            quotationId = ln.getEtQuotationId();
        } else if ("PREPAYMENT".equals(sourceType)) {
            quotationId = ln.getPrepaymentQuotationId();
        } else if ("FINANCIAL_TERMS".equals(sourceType)) {
            quotationId = ln.getChangeQuotationId();
        }


        //变更前 取con_contract_cashflow
        HlsCusConContract conContract = contractMapper.selectByPrimaryKey(ln.getContractId());
        HlsCusPrjQuotation oldPrjQuotation = prjQuotationMapper.selectByPrimaryKey(conContract.getQuotationId());

        Long oldTimes = oldPrjQuotation.getLeaseTimes();
        Date oldLeaseEndDate = conContract.getLeaseEndDate();

        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(ln.getContractId());
        List<HlsCusConContractCashflow> cashflowList = cashflowMapper.select(cashflow);

        cashflowList.stream().forEach(item -> {
            item.setDueAmount(nvl(item.getDueAmount(), 0.0));
            item.setPrincipal(nvl(item.getPrincipal(), 0.0));
            item.setInterest(nvl(item.getInterest(), 0.0));
            item.setNetInterest(nvl(item.getNetInterest(), 0.0));
            item.setVatInterest(nvl(item.getVatInterest(), 0.0));
        });

        //租金 租前息
        List<HlsCusConContractCashflow> rentalCashflowList = cashflowList.stream().
                filter(item -> (item.getCfItem().compareTo(1L) == 0 || item.getCfItem().compareTo(10L) == 0) && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.toList());


        Double oldTotalRental = rentalCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount));
        Double oldTotalPrincipal = rentalCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getPrincipal));
        Double oldTotalInterest = rentalCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getInterest));
        Double oldNetInterest = rentalCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getNetInterest));
        Double oldVatInterest = rentalCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getVatInterest));


        //保证金 保证金利息 风险金
        List<HlsCusConContractCashflow> depositCashflowList = cashflowList.stream().
                filter(item -> (item.getCfItem().compareTo(51L) == 0 || item.getCfItem().compareTo(501L) == 0 || item.getCfItem().compareTo(53L) == 0) && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.toList());

        Double oldDepositAmount = depositCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount));

        //手续费 资产管理费 咨询费
        List<HlsCusConContractCashflow> feeCashflowList = cashflowList.stream().
                filter(item -> (item.getCfItem().compareTo(3L) == 0 || item.getCfItem().compareTo(41L) == 0 || item.getCfItem().compareTo(66L) == 0 || item.getCfItem().compareTo(69L) == 0 || item.getCfItem().compareTo(4L) == 0) && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.toList());

        Double oldFeeAmount = feeCashflowList.stream().collect(Collectors.summingDouble(HlsCusConContractCashflow::getDueAmount));

        /*HlsCusPrjQuotation oldPrjQuotation = prjQuotationMapper.selectByPrimaryKey(conContract.getQuotationId());

        Double oldXirr = oldPrjQuotation.getXirr();
        Double oldXirrNet = oldPrjQuotation.getXirrNet();*/


        compare.setOldTimes(oldTimes);
        compare.setOldLeaseEndDate(oldLeaseEndDate);
        compare.setOldTotalRental(oldTotalRental);
        compare.setOldTotalPrincipal(oldTotalPrincipal);
        compare.setOldTotalInterest(oldTotalInterest);
        compare.setOldNetInterest(oldNetInterest);
        compare.setOldVatInterest(oldVatInterest);
        compare.setOldDepositAmount(oldDepositAmount);
        compare.setOldFeeAmount(oldFeeAmount);
        compare.setOldXirr(oldPrjQuotation.getXirr());
        compare.setOldXirrNet(oldPrjQuotation.getXirrNet());

        //变更后 取prj_quotation
        HlsCusPrjQuotation prjQuotation = prjQuotationMapper.selectByPrimaryKey(quotationId);


        compare.setTimes(prjQuotation.getLeaseTimes());
        compare.setLeaseEndDate(prjQuotation.getLeaseEndDate());

        HlsCusPrjQuotationCashflow prjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        prjQuotationCashflow.setQuotationId(quotationId);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflow);

        prjQuotationCashflowList.stream().forEach(item -> {
            item.setDueAmount(nvl(item.getDueAmount(), 0.0));
            item.setPrincipal(nvl(item.getPrincipal(), 0.0));
            item.setInterest(nvl(item.getInterest(), 0.0));
            item.setNetInterest(nvl(item.getNetInterest(), 0.0));
            item.setVatInterest(nvl(item.getVatInterest(), 0.0));
        });

        //租金 租前息
        List<HlsCusPrjQuotationCashflow> rentalPrjCashflowList = prjQuotationCashflowList.stream().
                filter(item -> (item.getCfItem().compareTo(1L) == 0 || item.getCfItem().compareTo(10L) == 0) && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.toList());


        Double totalRental = rentalPrjCashflowList.stream().collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount));
        Double totalPrincipal = rentalPrjCashflowList.stream().collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getPrincipal));
        Double totalInterest = rentalPrjCashflowList.stream().collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getInterest));
        Double netInterest = rentalPrjCashflowList.stream().collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getNetInterest));
        Double vatInterest = rentalPrjCashflowList.stream().collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getVatInterest));

        //保证金 保证金利息 风险金
        List<HlsCusPrjQuotationCashflow> depositPrjCashflowList = prjQuotationCashflowList.stream().
                filter(item -> (item.getCfItem().compareTo(51L) == 0 || item.getCfItem().compareTo(501L) == 0 || item.getCfItem().compareTo(53L) == 0) && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.toList());

        Double depositAmount = depositPrjCashflowList.stream().collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount));

        //手续费 资产管理费 咨询费
        List<HlsCusPrjQuotationCashflow> feePrjCashflowList = prjQuotationCashflowList.stream().
                filter(item -> (item.getCfItem().compareTo(3L) == 0 || item.getCfItem().compareTo(41L) == 0 || item.getCfItem().compareTo(66L) == 0 || item.getCfItem().compareTo(69L) == 0 || item.getCfItem().compareTo(4L) == 0) && item.getCfStatus().equals("RELEASE")).
                collect(Collectors.toList());

        Double feeAmount = feePrjCashflowList.stream().collect(Collectors.summingDouble(HlsCusPrjQuotationCashflow::getDueAmount));

        compare.setTotalRental(totalRental);
        compare.setTotalPrincipal(totalPrincipal);
        compare.setTotalInterest(totalInterest);
        compare.setNetInterest(netInterest);
        compare.setVatInterest(vatInterest);
        compare.setDepositAmount(depositAmount);
        compare.setFeeAmount(feeAmount);

        //计算新xirr xirr_net
        prjQuotationService.updateXirr(iRequest, prjQuotation);

        compare.setXirr(prjQuotation.getXirr());
        compare.setXirrNet(prjQuotation.getXirrNet());

        HlsDurationCompare hlsDurationCompare = new HlsDurationCompare();
        hlsDurationCompare.setLnId(ln.getLnId());
        List<HlsDurationCompare> hlsDurationCompareList = hlsDurationCompareMapper.select(hlsDurationCompare);
        self().batchDelete(hlsDurationCompareList);

        self().insertSelective(iRequest, compare);
    }
}