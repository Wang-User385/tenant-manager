package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBusinessConditionTax;

import java.util.List;

public interface HlsBusinessConditionTaxMapper extends Mapper<HlsBusinessConditionTax>{
    List<HlsBusinessConditionTax> queryAllByConditionId(HlsBusinessConditionTax hlsBusinessConditionTax);
}