package com.hand.hls.bp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.BpCreditHistorical;
import com.hand.hls.bp.mapper.BpCreditHistoricalMapper;
import com.hand.hls.bp.service.HlsCusBpCreditHistoricalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 胡兴恒
 * @Time 2020-04-30 14:36
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpCreditHistoricalServiceImpl extends BaseServiceImpl<BpCreditHistorical> implements HlsCusBpCreditHistoricalService {

    @Autowired
    private BpCreditHistoricalMapper bpCreditHistoricalMapper;

    @Override
    public List<BpCreditHistorical> selectCreditHistoricalByBpId(BpCreditHistorical bpCreditHistorical, IRequest requestContext, int page, int pagesize) {
        return bpCreditHistoricalMapper.queryAll(bpCreditHistorical);
    }
}
