package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBusinessConditionSale;

import java.util.List;

public interface HlsBusinessConditionSaleMapper extends Mapper<HlsBusinessConditionSale>{
    List<HlsBusinessConditionSale> queryAllByConditionId(HlsBusinessConditionSale hlsBusinessConditionSale);
}