package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusHlsBpBorrowerInfo;

import java.util.List;

public interface HlsCusHlsBpBorrowerInfoService extends IBaseService<HlsCusHlsBpBorrowerInfo>, ProxySelf<HlsCusHlsBpBorrowerInfoService> {
    List<HlsCusHlsBpBorrowerInfo> queryAll(HlsCusHlsBpBorrowerInfo dto, IRequest requestContext, int page, int pagesize);
}