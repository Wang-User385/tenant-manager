package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsCusBpZdwSituation;
import com.hand.hls.bp.mapper.HlsCusBpZdwSituationMapper;
import com.hand.hls.bp.service.HlsCusIBpZdwSituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpZdwSituationServiceImpl extends BaseServiceImpl<HlsCusBpZdwSituation> implements HlsCusIBpZdwSituationService {

    @Autowired
    private HlsCusBpZdwSituationMapper mapper;

    @Override
    public List<HlsCusBpZdwSituation> selectAll(IRequest requestContext, HlsCusBpZdwSituation bpZdwSituation, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return mapper.queryAll(bpZdwSituation);
    }
}