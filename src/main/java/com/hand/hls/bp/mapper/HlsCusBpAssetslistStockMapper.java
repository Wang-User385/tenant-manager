package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpAssetslistStock;

import java.util.List;

public interface HlsCusBpAssetslistStockMapper extends Mapper<HlsCusBpAssetslistStock> {

    List<HlsCusBpAssetslistStock> queryAll(HlsCusBpAssetslistStock bpAssetslistStock);

}