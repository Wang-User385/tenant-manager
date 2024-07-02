package com.hand.hls.fp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.mapper.JcFundingPlanOneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.JcFundingPlanOne;
import com.hand.hls.fp.service.JcFundingPlanOneService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class JcFundingPlanOneServiceImpl extends BaseServiceImpl<JcFundingPlanOne> implements JcFundingPlanOneService{
     @Autowired
     private JcFundingPlanOneMapper mapper;
     @Override
    public List<JcFundingPlanOne> selectAll(IRequest iRequest,JcFundingPlanOne jcFundingPlanOne,int page,int pageSize){
         PageHelper.startPage(page,pageSize);
         return mapper.queryAll(jcFundingPlanOne);
     }
}