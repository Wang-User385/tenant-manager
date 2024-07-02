package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusHlsBpBorrowerInfo;

import java.util.List;

public interface HlsCusHlsBpBorrowerInfoMapper extends Mapper<HlsCusHlsBpBorrowerInfo> {
    List<HlsCusHlsBpBorrowerInfo> queryAll(HlsCusHlsBpBorrowerInfo dto);
}