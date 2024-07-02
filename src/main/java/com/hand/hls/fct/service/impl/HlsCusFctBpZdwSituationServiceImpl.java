package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusFctBpZdwSituation;
import com.hand.hls.fct.mapper.HlsCusFctBpZdwSituationMapper;
import com.hand.hls.fct.service.HlsCusFctBpZdwSituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctBpZdwSituationServiceImpl extends BaseServiceImpl<HlsCusFctBpZdwSituation> implements HlsCusFctBpZdwSituationService {

    @Autowired
    private HlsCusFctBpZdwSituationMapper mapper;

    @Override
    public List<HlsCusFctBpZdwSituation> fctBpZdwSituationInfoQuery(IRequest iRequest, HlsCusFctBpZdwSituation hlsCusFctBpZdwSituation, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusFctBpZdwSituation> hlsCusFctBpZdwSituationList = new ArrayList<>();
        hlsCusFctBpZdwSituationList = mapper.fctBpZdwSituationInfoQuery(hlsCusFctBpZdwSituation);
        return hlsCusFctBpZdwSituationList;
    }
}