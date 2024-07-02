package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.BpCreditRate;
import com.hand.hls.bp.dto.HlsScoreCalculation;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 13:47
 */
public interface HlsCusBpCreditRateService extends IBaseService<BpCreditRate>, ProxySelf<HlsCusBpCreditRateService> {

    List<BpCreditRate> selectCreditRateByBpId(BpCreditRate bpCreditRate, IRequest requestContext, int page, int pagesize);
    List<HlsScoreCalculation> selectCreditRateByBpIdPrj(HlsScoreCalculation bpCreditRate, IRequest requestContext, int page, int pagesize);
}
