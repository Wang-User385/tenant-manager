package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.BpCreditHistorical;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 13:47
 */
public interface HlsCusBpCreditHistoricalService extends IBaseService<BpCreditHistorical>, ProxySelf<HlsCusBpCreditHistoricalService> {

    List<BpCreditHistorical> selectCreditHistoricalByBpId(BpCreditHistorical bpCreditHistorical, IRequest requestContext, int page, int pagesize);
}
