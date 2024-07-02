package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.BpCreditRate;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.BpCreditRateMapper;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.bp.service.HlsCusBpCreditRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 13:51
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpCreditRateServiceImpl extends BaseServiceImpl<BpCreditRate> implements HlsCusBpCreditRateService {

    @Autowired
    private BpCreditRateMapper bpCreditRateMapper;
    @Autowired
    private HlsScoreCalculationMapper calculationMapper;


    @Override
    public List<BpCreditRate> selectCreditRateByBpId(BpCreditRate bpCreditRate, IRequest requestContext, int page, int pagesize) {

        return bpCreditRateMapper.queryAll(bpCreditRate);
    }
    @Override
    public List<HlsScoreCalculation> selectCreditRateByBpIdPrj(HlsScoreCalculation bpCreditRate, IRequest requestContext, int page, int pagesize) {

        return calculationMapper.selectScoreCalculation(bpCreditRate);
    }
}
