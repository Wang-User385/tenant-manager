package com.hand.hls.gld.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.formbean.AmortizationCost;
import com.hand.hls.gld.service.IGldDayCostUtilService;
import com.hand.hls.gld.service.IGldDayInterestUtilService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/13
 * @description: 费用摊销 直线法
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GldFinCostStraightServiceImpl implements IGldFinanceIncomeDayCommonService {
    public static final String SHARE_TYPE = "COST_STRAIGHT";
    @Autowired
    private IGldDayCostUtilService costUtilService;
    @Autowired
    private IGldDayInterestUtilService utilService;

    @Override
    public String getShareType() {
        return SHARE_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) throws Exception {
        String bizType = (String) params.get("bizType");

        if ("LEASE".equalsIgnoreCase(bizType)) {
            shareLease(iRequest, list, params);
        } else if ("LOAN".equalsIgnoreCase(bizType)) {
            shareLoan(iRequest, list, params);
        }
    }

    /**
     * @Title: shareLease
     * @Discription: 租赁合同 分摊 主函数
     * @Param: [iRequest, list, params]
     * @Return: void
     */
    public void shareLease(IRequest iRequest, List<HlsCusConContract> list, Map params) throws ParseException {
        Long cfItem = Long.parseLong(params.get("cfItem").toString());
        for (HlsCusConContract contract : list) {
            //查询合同下该费用所有现金流
            List<HlsCusConContractCashflow> cashflowList = costUtilService.getAllCashflow(contract.getContractId(), cfItem, SHARE_TYPE);
            // 计算每日的分摊
            List<AmortizationCost> amortizationCostList = costUtilService.calcAmortizationCost(contract.getContractId(), cashflowList);
            //插入 未实现融资收益日表
            List<GldFinanceIncomeDay> dayList = costUtilService.insertGldFinanceIncomeDay(iRequest, amortizationCostList, contract.getContractId(), cfItem);
            //插入 未实现融资收益表
            utilService.insertGldContractFinanceIncome(iRequest, contract.getContractId(), dayList,cfItem);
        }
    }


    //借款合同 分摊(暂时没有业务)
    public void shareLoan(IRequest iRequest, List<HlsCusLonContract> list, Map params) {

    }
}
