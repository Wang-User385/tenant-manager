package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpAssetslistRealestate;

import java.util.List;

public interface HlsCusBpAssetslistRealestateMapper extends Mapper<HlsCusBpAssetslistRealestate> {

    List<HlsCusBpAssetslistRealestate> queryAll(HlsCusBpAssetslistRealestate bpAssetslistRealestate);

}