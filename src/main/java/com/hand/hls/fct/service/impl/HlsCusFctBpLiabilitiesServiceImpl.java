package com.hand.hls.fct.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fct.dto.HlsCusFctBpLiabilities;
import com.hand.hls.fct.mapper.HlsCusFctBpLiabilitiesMapper;
import com.hand.hls.fct.service.HlsCusFctBpLiabilitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctBpLiabilitiesServiceImpl extends BaseServiceImpl<HlsCusFctBpLiabilities> implements HlsCusFctBpLiabilitiesService {

    @Autowired
    private HlsCusFctBpLiabilitiesMapper mapper;


    @Override
    public List<HlsCusFctBpLiabilities> fctBpLiabilitiesInfoQuery(IRequest iRequest, HlsCusFctBpLiabilities hlsCusFctBpLiabilities, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusFctBpLiabilities> hlsCusPrjBpFinancingSituationList = new ArrayList<>();
        hlsCusPrjBpFinancingSituationList = mapper.fctBpLiabilitiesInfoQuery(hlsCusFctBpLiabilities);
        return hlsCusPrjBpFinancingSituationList;
    }
}