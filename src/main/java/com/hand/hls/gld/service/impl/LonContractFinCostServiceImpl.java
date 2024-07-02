package com.hand.hls.gld.service.impl;

import java.util.List;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.dto.LonContractFinCost;
import com.hand.hls.gld.mapper.HlsCusGldLonContractFinCostMapper;
import com.hand.hls.gld.mapper.LonContractFinCostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.gld.service.ILonContractFinCostService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class LonContractFinCostServiceImpl extends BaseServiceImpl<LonContractFinCost> implements ILonContractFinCostService{

    @Autowired
    private LonContractFinCostMapper lonContractFinCostMapper;
    @Autowired
    private HlsCusGldLonContractFinCostMapper hlsCusGldLonContractFinCostMapper;

    @Override
    public List<LonContractFinCost> queryLonContractFinCost(IRequest iRequest, LonContractFinCost lonContractFinCost, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return lonContractFinCostMapper.queryLonContractFinCost(lonContractFinCost);
    }

    @Override
    public List<LonContractFinCost> queryCfItemForComb(IRequest request, LonContractFinCost condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.lonContractFinCostMapper.queryCfItemForComb(condition);
    }

    @Override
    public HlsCusGldLonContractFinCost queryLonFinCostByKey(IRequest iRequest, HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost) {
        return lonContractFinCostMapper.queryLonFinCostByKey(hlsCusGldLonContractFinCost);
    }

    @Override
    public List<HlsCusGldLonContractFinCost> monthFinCostQuery(IRequest iRequest, HlsCusGldLonContractFinCost lonContractFinCost, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return hlsCusGldLonContractFinCostMapper.monthFinCostQuery(lonContractFinCost);
    }
}