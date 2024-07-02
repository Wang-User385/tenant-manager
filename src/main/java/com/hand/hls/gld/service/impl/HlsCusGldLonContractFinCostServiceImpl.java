package com.hand.hls.gld.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.mapper.HlsCusGldLonContractFinCostMapper;
import com.hand.hls.gld.service.HlsCusGldLonContractFinCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusGldLonContractFinCostServiceImpl extends BaseServiceImpl<HlsCusGldLonContractFinCost> implements HlsCusGldLonContractFinCostService {

    @Autowired
    private HlsCusGldLonContractFinCostMapper mapper;

    @Override
    public List<HlsCusGldLonContractFinCost> selectCostAccrual(HlsCusGldLonContractFinCost dto) {
        return mapper.selectCostAccrual(dto);
    }
}
