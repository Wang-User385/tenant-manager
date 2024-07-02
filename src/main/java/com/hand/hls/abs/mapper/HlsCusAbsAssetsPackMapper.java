package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsAssetsPack;

import java.util.List;

public interface HlsCusAbsAssetsPackMapper extends Mapper<HlsCusAbsAssetsPack> {


    /**
     * 资产打包查询
     * @param assetsPack
     * @return
     */
    List<HlsCusAbsAssetsPack>  selectAssetsPack(HlsCusAbsAssetsPack assetsPack);

    /**
     * 封包日LOV查询
     * @param assetsPack
     * @return
     */
    List<HlsCusAbsAssetsPack> selectProjectAssetsPack(HlsCusAbsAssetsPack assetsPack);
}
