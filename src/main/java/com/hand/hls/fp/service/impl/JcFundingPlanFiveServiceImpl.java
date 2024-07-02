package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.mapper.JcFundingPlanFiveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundingPlanFive;
import com.hand.hls.fp.service.JcFundingPlanFiveService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundingPlanFiveServiceImpl extends BaseServiceImpl<JcFundingPlanFive> implements JcFundingPlanFiveService{

    @Autowired
    private JcFundingPlanFiveMapper mapper;
    @Override
    public List<JcFundingPlanFive> selectAll(IRequest iRequest,JcFundingPlanFive jcFundingPlanFive,int page,int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(jcFundingPlanFive);
    }
}