package com.hand.hls.gld.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.formbean.AmortizationCost;
import com.hand.hls.gld.service.IGldDayCostUtilService;
import com.hand.hls.gld.service.IGldDayInterestUtilService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayCommonService;
import com.hand.hls.sys.utils.OracleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/12
 * @description: 费用摊销 利息占比法
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GldFinCostProportionServiceImpl implements IGldFinanceIncomeDayCommonService {
    public static final String SHARE_TYPE = "COST_PROPORTION";
    @Autowired
    private IGldDayInterestUtilService utilService;
    @Autowired
    private IGldDayCostUtilService costUtilService;
    @Autowired
    private HlsCusConContractMapper contractMapper;

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

            HlsCusConContract conContract = contractMapper.selectByPrimaryKey(contract.getContractId());

            //查询合同下该费用所有现金流
            List<HlsCusConContractCashflow> cashflowList = costUtilService.getAllCashflow(contract.getContractId(), cfItem, SHARE_TYPE);

            for (HlsCusConContractCashflow cashflow : cashflowList) {

                //获取所有的核销记录
                List<HlsCusCshWriteOff> allWriteOff = costUtilService.getAllWriteOff(cashflow.getCashflowId());

                if (allWriteOff.size() > 0) {
                    //按收款日日排序
                    allWriteOff = allWriteOff.stream().sorted(Comparator.comparing(HlsCusCshWriteOff::getTransactionDate)).collect(Collectors.toList());

                    //首次放款日
                    Date firstLoanDate = costUtilService.firstLoanDate(contract.getContractId());

                    //比较放款日 和 费用 收款日的 大小
                    for (HlsCusCshWriteOff cshWriteOff : allWriteOff) {
                        if (firstLoanDate.compareTo(cshWriteOff.getTransactionDate()) > 0) {
                            cshWriteOff.setCalcDate(firstLoanDate);
                        } else {
                            cshWriteOff.setCalcDate(cshWriteOff.getTransactionDate());
                        }
                    }
                    Date firstCalcDate = allWriteOff.stream().sorted(Comparator.comparing(HlsCusCshWriteOff::getCalcDate)).findFirst().get().getCalcDate();

                    //税率
                    Double taxTypeRate = OracleUtils.nvl(cashflow.getTaxTypeRate(), conContract.getVatRate());

                    //筛选所有需要计算的分摊日记录
                    List<GldFinanceIncomeDay> gldFinanceIncomeDayList = costUtilService.getAmortizationIncomeDay(contract);

                    //找到 日期 >= 最早需要分摊的费用应收日 按日期排序
                    gldFinanceIncomeDayList = gldFinanceIncomeDayList.stream().
                            filter(item -> item.getAmortizationDate().compareTo(firstCalcDate) > -1).
                            sorted(Comparator.comparing(GldFinanceIncomeDay::getAmortizationDate)).collect(Collectors.toList());

                    Map<Date, Double> surplusInterestSumMap = costUtilService.surplusInterestSumMap(gldFinanceIncomeDayList, allWriteOff);

                    List<AmortizationCost> amortizationCostList = costUtilService.calcAmortizationCost(gldFinanceIncomeDayList, allWriteOff, surplusInterestSumMap, taxTypeRate, cashflow);

                    //插入 未实现融资收益日表
                    List<GldFinanceIncomeDay> dayList = costUtilService.insertGldFinanceIncomeDay(iRequest, amortizationCostList, contract.getContractId(), cfItem);
                    //插入 未实现融资收益表
                    utilService.insertGldContractFinanceIncome(iRequest, contract.getContractId(), dayList, cfItem);
                }
            }
        }
    }

    //借款合同 分摊(暂时没有业务)
    public void shareLoan(IRequest iRequest, List<HlsCusLonContract> list, Map params) {

    }

}
