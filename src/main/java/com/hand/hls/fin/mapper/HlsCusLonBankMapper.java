package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMaster;

import java.util.List;
import java.util.Map;


public interface HlsCusLonBankMapper<T extends HlsCusBpMaster> extends Mapper<HlsCusBpMaster> {
    List<HlsCusBpMaster> lonBankQuery(Map<String, Object> params);
}