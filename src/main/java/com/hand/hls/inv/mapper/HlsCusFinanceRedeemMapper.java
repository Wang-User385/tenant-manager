package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusFinanceRedeem;

import java.util.List;

public interface HlsCusFinanceRedeemMapper extends Mapper<HlsCusFinanceRedeem> {
    List<HlsCusFinanceRedeem> queryAll(HlsCusFinanceRedeem hlsCusFinanceRedeem);

    List<HlsCusFinanceRedeem> checkRedeemNewOrReturn(HlsCusFinanceRedeem hlsCusFinanceRedeem);

    //查询赎回明细信息
    List<HlsCusFinanceRedeem> queryRedeemDetail(HlsCusFinanceRedeem hlsCusFinanceRedeem);

}