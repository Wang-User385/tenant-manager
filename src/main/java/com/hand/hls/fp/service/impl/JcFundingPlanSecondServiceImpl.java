package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.mapper.JcFundingPlanSecondMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundingPlanSecond;
import com.hand.hls.fp.service.JcFundingPlanSecondService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundingPlanSecondServiceImpl extends BaseServiceImpl<JcFundingPlanSecond> implements JcFundingPlanSecondService{
    @Autowired
    private JcFundingPlanSecondMapper mapper;
    @Override
    public List<JcFundingPlanSecond> selectAll(IRequest iRequest, JcFundingPlanSecond jcFundingPlanSecond, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(jcFundingPlanSecond);
    }

}