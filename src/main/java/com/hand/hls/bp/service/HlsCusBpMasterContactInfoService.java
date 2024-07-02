package com.hand.hls.bp.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMasterContactInfo;

import java.util.List;

public interface HlsCusBpMasterContactInfoService extends IBaseService<HlsCusBpMasterContactInfo>, ProxySelf<HlsCusBpMasterContactInfoService> {

    List<HlsCusBpMasterContactInfo> queryAll(HlsCusBpMasterContactInfo dto, int page, int pagesize);

    List<HlsCusBpMasterContactInfo> queryAllAuthorize(HlsCusBpMasterContactInfo dto, int page, int pagesize);

    List<HlsCusBpMasterContactInfo> queryAllBenifit(HlsCusBpMasterContactInfo dto, int page, int pagesize);

}
