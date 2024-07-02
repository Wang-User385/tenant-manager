package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusDepositDeduction;

import java.util.List;

public interface HlsCusDepositDeductionMapper extends Mapper<HlsCusDepositDeduction> {

    /**
     * 抵扣查询
     * @param hlsCusDepositDeduction
     * @return
     */
    List<HlsCusDepositDeduction> selectDepositDedctionData(HlsCusDepositDeduction hlsCusDepositDeduction);


    /**
     * 已保存未抵扣的金额
     * @param hlsCusDepositDeduction
     * @return
     */
    Double selectNewDectionAmountSum(HlsCusDepositDeduction hlsCusDepositDeduction);


    int selectCashFlowCount(HlsCusDepositDeduction hlsCusDepositDeduction);

}