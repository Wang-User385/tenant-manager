package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsCusBpMasterAddress;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-05-06 18:44
 */
public interface HlsCusBpMasterAddressService extends IBaseService<HlsCusBpMasterAddress>, ProxySelf<HlsCusBpMasterAddressService> {

    List<HlsCusBpMasterAddress> selectbpAddress(HlsCusBpMasterAddress hlsCusBpMasterAddress, IRequest requestContext, int page, int pagesize);
}
