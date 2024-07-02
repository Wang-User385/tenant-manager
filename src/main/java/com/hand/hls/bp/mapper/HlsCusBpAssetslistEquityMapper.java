package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpAssetslistEquity;

import java.util.List;

public interface HlsCusBpAssetslistEquityMapper extends Mapper<HlsCusBpAssetslistEquity> {

    List<HlsCusBpAssetslistEquity> queryAll(HlsCusBpAssetslistEquity bpAssetslistEquity);
}