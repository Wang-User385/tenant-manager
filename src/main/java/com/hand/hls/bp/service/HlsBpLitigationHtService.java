package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpLitigationHt;

import java.util.List;

public interface HlsBpLitigationHtService extends IBaseService<HlsBpLitigationHt>, ProxySelf<HlsBpLitigationHtService>{

    List<HlsBpLitigationHt> selectAll(IRequest iRequest, HlsBpLitigationHt hlsBpLitigationHt, int page, int pageSize);
}