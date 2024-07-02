package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import java.util.List;
import java.util.Map;

public interface IHlsScoreCalculationService extends IBaseService<HlsScoreCalculation>, ProxySelf<IHlsScoreCalculationService>{
    List<HlsScoreCalculation> selectListOfHsc(HlsScoreCalculation var1);

    List<HlsScoreCalculation> conInceptSubmit(IRequest iRequest, HlsScoreCalculation hlsScoreCalculation) throws ResMessageException, ParameterNullException;

    String generateAuthorityString(IRequest iRequest);

    Map clacRiskMoney(IRequest iRequest, HlsScoreCalculation hlsScoreCalculation) throws ResMessageException, ParameterNullException;
    /**
     * 评分计算
     *
     * @param iRequest
     * @param hlsScoreCalculation
     * @param reCalcFlag
     * @return
     */
    HlsScoreCalculation hlsScoreCalculation(IRequest iRequest, HlsScoreCalculation hlsScoreCalculation,
                                            String reCalcFlag);
}