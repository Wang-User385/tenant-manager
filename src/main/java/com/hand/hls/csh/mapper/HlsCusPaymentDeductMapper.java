package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusPaymentDeduct;

import java.util.List;

public interface HlsCusPaymentDeductMapper extends Mapper<HlsCusPaymentDeduct> {
    //  *  付款申请- 坐扣明细查询
    List<HlsCusPaymentDeduct> queryCanDeductDetailFromCashflow(HlsCusPaymentDeduct hlsCusCshPaymentDeduct);
}