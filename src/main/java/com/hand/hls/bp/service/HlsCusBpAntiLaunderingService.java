package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.BpAntiLaundering;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 15:03
 */
public interface HlsCusBpAntiLaunderingService extends IBaseService<BpAntiLaundering>, ProxySelf<HlsCusBpAntiLaunderingService> {

    List<BpAntiLaundering> selectAntiLaunderingByBpId(BpAntiLaundering bpAntiLaundering, IRequest requestContext, int page, int pagesize);
}
