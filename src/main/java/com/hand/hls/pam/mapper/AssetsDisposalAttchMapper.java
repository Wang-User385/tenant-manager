package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.AssetsDisposalAttch;

import java.util.List;

public interface AssetsDisposalAttchMapper extends Mapper<AssetsDisposalAttch>{

    //
    List<AssetsDisposalAttch> queryAssetsDisposalAttch(AssetsDisposalAttch assetsDisposalAttch);
}