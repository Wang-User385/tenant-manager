package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpLitigationFinal;

import java.util.List;

public interface HlsBpLitigationFinalService extends IBaseService<HlsBpLitigationFinal>, ProxySelf<HlsBpLitigationFinalService>{

    List<HlsBpLitigationFinal> selectAll(IRequest iRequest, HlsBpLitigationFinal hlsBpLitigationFinal, int page, int pageSize);
}