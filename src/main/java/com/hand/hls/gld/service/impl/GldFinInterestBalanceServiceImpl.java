package com.hand.hls.gld.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.formbean.AmortizationInterest;
import com.hand.hls.gld.service.IGldDayInterestUtilService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/7
 * @description: 本金余额法
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GldFinInterestBalanceServiceImpl implements IGldFinanceIncomeDayCommonService {

    public static final String SHARE_TYPE = "INTEREST_BALANCE";

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
            //针对于 利息 收益分摊（包含2部分 租金 和 租前息）
            //构造AmortizationInterest 方便后续插入业务表
            AmortizationInterest amortizationInterest = utilService.getAmortizationInterest(contract.getContractId(), SHARE_TYPE);
            //必须有利息才摊销
            if (amortizationInterest.getNetInterestTotal() > 0) {
                //插入 未实现融资收益日表
                List<GldFinanceIncomeDay> gldFinanceIncomeDayList = utilService.insertGldFinanceIncomeDay(iRequest, contract.getContractId(), amortizationInterest, SHARE_TYPE);
                //插入 未实现融资收益表
                utilService.insertGldContractFinanceIncome(iRequest, contract.getContractId(), gldFinanceIncomeDayList, cfItem);
            }

        }
    }

    //借款合同 分摊(暂时没有业务)
    public void shareLoan(IRequest iRequest, List<HlsCusLonContract> list, Map params) {

    }

}
