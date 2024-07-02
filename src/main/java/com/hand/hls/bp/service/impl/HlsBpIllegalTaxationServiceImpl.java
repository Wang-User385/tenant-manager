package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpIllegalTaxationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpIllegalTaxation;
import com.hand.hls.bp.service.HlsBpIllegalTaxationService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpIllegalTaxationServiceImpl extends BaseServiceImpl<HlsBpIllegalTaxation> implements HlsBpIllegalTaxationService{

    @Autowired
    private HlsBpIllegalTaxationMapper mapper;
    @Override
    public List<HlsBpIllegalTaxation> selectAll(IRequest iRequest, HlsBpIllegalTaxation hlsBpIllegalTaxation, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpIllegalTaxation);
    }

}