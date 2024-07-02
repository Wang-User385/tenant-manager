package com.hand.hls.bill.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bill.dto.hlsRefundDeposit;

import java.util.List;

public interface hlsRefundDepositMapper extends Mapper<hlsRefundDeposit>{
    List<hlsRefundDeposit> queryHlsRefundDeposit(hlsRefundDeposit hlsCusFundingPlanLn);

}