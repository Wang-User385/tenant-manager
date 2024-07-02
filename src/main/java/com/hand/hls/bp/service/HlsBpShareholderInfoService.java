package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpShareholderInfo;

import java.util.List;

public interface HlsBpShareholderInfoService extends IBaseService<HlsBpShareholderInfo>, ProxySelf<HlsBpShareholderInfoService>{

    List<HlsBpShareholderInfo> selectAll(IRequest requestContext, HlsBpShareholderInfo hlsBpShareholderInfo, int page, int pagesize);
}