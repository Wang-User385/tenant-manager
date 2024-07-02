package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpAssetsList;

import java.util.List;

public interface HlsBpAssetsListService extends IBaseService<HlsBpAssetsList>, ProxySelf<HlsBpAssetsListService>{

    List<HlsBpAssetsList> selectAll(IRequest iRequest, HlsBpAssetsList hlsBpAssetsList, int page, int pageSize);
}