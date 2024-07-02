package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpLitigation;

import java.util.List;

public interface HlsBpLitigationService extends IBaseService<HlsBpLitigation>, ProxySelf<HlsBpLitigationService>{

    List<HlsBpLitigation> selectAll (IRequest iRequest, HlsBpLitigation hlsBpLitigation, int page, int pageSize);
}