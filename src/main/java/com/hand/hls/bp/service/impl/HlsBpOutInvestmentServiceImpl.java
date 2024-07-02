package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpOutInvestmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpOutInvestment;
import com.hand.hls.bp.service.HlsBpOutInvestmentService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpOutInvestmentServiceImpl extends BaseServiceImpl<HlsBpOutInvestment> implements HlsBpOutInvestmentService{

    @Autowired
    private HlsBpOutInvestmentMapper mapper;

    @Override
    public List<HlsBpOutInvestment> selectAll(IRequest requestContext, HlsBpOutInvestment hlsBpOutInvestment,int page,int pagesize){
        PageHelper.startPage(page, pagesize);
        return mapper.queryHlsBpOutInvestment(hlsBpOutInvestment);
    }

}