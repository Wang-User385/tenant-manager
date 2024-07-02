package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpLiabilities;

import java.util.List;
import java.util.Map;

public interface HlsCusBpLiabilitiesMapper extends Mapper<HlsCusBpLiabilities> {

    List<HlsCusBpLiabilities> queryAll(HlsCusBpLiabilities bpLiabilities);

    void deleteLiabilitiesByBpId(Long bpId);

    List<Map> selectSysCodeValue(String sysCode);
}