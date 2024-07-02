package com.hand.hls.inv.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.inv.dto.HlsCusInvFinanceDetail;

import java.util.List;

public interface HlsCusInvFinanceDetailMapper extends Mapper<HlsCusInvFinanceDetail> {

    //财务投资理财已办查询
    List<HlsCusInvFinanceDetail> queryInvDoneFinanceList(HlsCusInvFinanceDetail hlsCusInvFinanceDetail);

    List<HlsCusInvFinanceDetail> queryInvFinanceDetal(HlsCusInvFinanceDetail hlsCusInvFinanceDetail);

}