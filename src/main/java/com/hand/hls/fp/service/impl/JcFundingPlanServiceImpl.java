package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.mapper.JcFundingPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundingPlan;
import com.hand.hls.fp.service.JcFundingPlanService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundingPlanServiceImpl extends BaseServiceImpl<JcFundingPlan> implements JcFundingPlanService{

    @Autowired
    private JcFundingPlanMapper mapper;
    @Override
    public List<JcFundingPlan> selectAll(IRequest iRequest, JcFundingPlan jcFundingPlan, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(jcFundingPlan);
    }
}