package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.mapper.JcFundingPlanFourMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundingPlanFour;
import com.hand.hls.fp.service.JcFundingPlanFourService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundingPlanFourServiceImpl extends BaseServiceImpl<JcFundingPlanFour> implements JcFundingPlanFourService{
    @Autowired
    private JcFundingPlanFourMapper mapper;
    @Override
    public List<JcFundingPlanFour> selectAll(IRequest iRequest,JcFundingPlanFour jcFundingPlanFour,int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(jcFundingPlanFour);
    }
}