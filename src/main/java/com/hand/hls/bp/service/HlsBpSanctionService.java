package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpSanction;

import java.util.List;

public interface HlsBpSanctionService extends IBaseService<HlsBpSanction>, ProxySelf<HlsBpSanctionService>{

    List<HlsBpSanction> selectAll(IRequest iRequest, HlsBpSanction hlsBpSanction, int page, int pageSize);
}