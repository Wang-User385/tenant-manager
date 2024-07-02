package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusFctBpFinancingSituation;
import com.hand.hls.fct.mapper.HlsCusFctBpFinancingSituationMapper;
import com.hand.hls.fct.service.HlsCusFctBpFinancingSituationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctBpFinancingSituationServiceImpl extends BaseServiceImpl<HlsCusFctBpFinancingSituation> implements HlsCusFctBpFinancingSituationService {

    @Autowired
    private HlsCusFctBpFinancingSituationMapper mapper;

    @Override
    public List<HlsCusFctBpFinancingSituation> fctBpFinancingSituationInfoQuery(IRequest iRequest, HlsCusFctBpFinancingSituation dto, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusFctBpFinancingSituation> hlsCusPrjBpFinancingSituationList = new ArrayList<>();
        hlsCusPrjBpFinancingSituationList = mapper.fctBpFinancingSituationInfoQuery(dto);
        return hlsCusPrjBpFinancingSituationList;
    }
}