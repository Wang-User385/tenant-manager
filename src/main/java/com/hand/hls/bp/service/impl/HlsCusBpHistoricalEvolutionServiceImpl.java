package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpHistoricalEvolution;
import com.hand.hls.bp.mapper.HlsCusBpHistoricalEvolutionMapper;
import com.hand.hls.bp.service.HlsCusIBpHistoricalEvolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpHistoricalEvolutionServiceImpl extends BaseServiceImpl<HlsCusBpHistoricalEvolution> implements HlsCusIBpHistoricalEvolutionService {

    @Autowired
    private HlsCusBpHistoricalEvolutionMapper mapper;

    @Override
    public List<HlsCusBpHistoricalEvolution> selectAll(IRequest requestContext, HlsCusBpHistoricalEvolution bpHistoricalEvolution, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(bpHistoricalEvolution);
    }
}