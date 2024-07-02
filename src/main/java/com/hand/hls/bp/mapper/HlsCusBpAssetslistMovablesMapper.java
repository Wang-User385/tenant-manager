package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpAssetslistMovables;

import java.util.List;

public interface HlsCusBpAssetslistMovablesMapper extends Mapper<HlsCusBpAssetslistMovables> {

    List<HlsCusBpAssetslistMovables> queryAll(HlsCusBpAssetslistMovables bpAssetslistMovables);

}