package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpAssetsList;

import java.util.List;

public interface HlsBpAssetsListMapper extends Mapper<HlsBpAssetsList>{

    List<HlsBpAssetsList> queryAll(HlsBpAssetsList hlsBpAssetsList);

}