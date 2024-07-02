package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.AssetsDisposalCost;

import java.util.List;

public interface AssetsDisposalCostMapper extends Mapper<AssetsDisposalCost>{

    //
    List<AssetsDisposalCost> queryAssetsDisposalCost(AssetsDisposalCost assetsDisposalCost);
}