package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBusinessConditionReceivePay;

import java.util.List;

public interface HlsBusinessConditionReceivePayMapper extends Mapper<HlsBusinessConditionReceivePay>{
    List<HlsBusinessConditionReceivePay> queryAllByConditionId(HlsBusinessConditionReceivePay hlsBusinessConditionReceivePay);
}