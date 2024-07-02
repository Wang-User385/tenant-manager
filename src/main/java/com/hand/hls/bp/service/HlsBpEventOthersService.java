package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpEventOthers;

import java.util.List;

public interface HlsBpEventOthersService extends IBaseService<HlsBpEventOthers>, ProxySelf<HlsBpEventOthersService>{

    List<HlsBpEventOthers> selectAll(IRequest iRequest,HlsBpEventOthers hlsBpEventOthers, int page, int pageSize);
}