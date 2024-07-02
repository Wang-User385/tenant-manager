package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.AssetsDisposalDetail;

import java.util.List;

public interface AssetsDisposalDetailMapper extends Mapper<AssetsDisposalDetail>{

    //
    List<AssetsDisposalDetail> queryLeaseItemByProjectId(Long projectId);
    //
    List<AssetsDisposalDetail> queryAssetsDisposalDetail(AssetsDisposalDetail assetsDisposalDetail);
}