package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpCreditRate;

import java.util.List;

public interface BpCreditRateMapper extends Mapper<BpCreditRate>{

    List<BpCreditRate> queryAll(BpCreditRate bpCreditRate);

}