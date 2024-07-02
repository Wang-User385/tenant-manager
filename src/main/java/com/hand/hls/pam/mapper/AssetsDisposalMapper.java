package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.AssetsDisposal;

import java.util.List;

public interface AssetsDisposalMapper extends Mapper<AssetsDisposal>{
    //资产处置入口页面查询
    List<AssetsDisposal> queryAssetsDisposal(AssetsDisposal assetsDisposal);

}