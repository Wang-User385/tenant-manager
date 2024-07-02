package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.BpCreditMotherline;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 14:42
 */
public interface HlsCusBpCreditMotherlineService extends IBaseService<BpCreditMotherline>, ProxySelf<HlsCusBpCreditMotherlineService> {

    List<BpCreditMotherline> selectCreditMotherlineByBpId(BpCreditMotherline bpCreditMotherline, IRequest requestContext, int page, int pagesize);
}
