package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.HlsCusDepositRefund;

import java.util.List;

public interface HlsCusDepositRefundMapper extends Mapper<HlsCusDepositRefund> {


    List<HlsCusDepositRefund> selectDepositRefundData(HlsCusDepositRefund hlsCusDepositRefund);

}