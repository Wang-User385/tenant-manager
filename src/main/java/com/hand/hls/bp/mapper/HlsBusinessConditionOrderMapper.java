package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBusinessConditionOrder;

import java.util.List;


public interface HlsBusinessConditionOrderMapper extends Mapper<HlsBusinessConditionOrder>{
    List<HlsBusinessConditionOrder> queryAllByConditionId(HlsBusinessConditionOrder hlsBusinessConditionOrder);
}