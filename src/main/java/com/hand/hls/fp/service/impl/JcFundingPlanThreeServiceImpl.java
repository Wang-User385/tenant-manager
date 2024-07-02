package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.mapper.JcFundingPlanThreeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundingPlanThree;
import com.hand.hls.fp.service.JcFundingPlanThreeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundingPlanThreeServiceImpl extends BaseServiceImpl<JcFundingPlanThree> implements JcFundingPlanThreeService{
    @Autowired
    private JcFundingPlanThreeMapper mapper;
    @Override
    public List<JcFundingPlanThree> selectAll(IRequest iRequest,JcFundingPlanThree jcFundingPlanThree,int page,int pageSize){
        PageHelper.offsetPage(page,pageSize);
        return mapper.queryAll(jcFundingPlanThree);
    }
}