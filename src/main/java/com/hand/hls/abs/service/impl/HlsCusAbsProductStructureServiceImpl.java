package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductStructure;
import com.hand.hls.abs.mapper.HlsCusAbsProductStructureMapper;
import com.hand.hls.abs.service.HlsCusAbsProductStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductStructureServiceImpl extends BaseServiceImpl<HlsCusAbsProductStructure> implements HlsCusAbsProductStructureService {

    @Autowired
    private HlsCusAbsProductStructureMapper productStructureMapper;

    @Override
    public List<HlsCusAbsProductStructure> selectProductStructureData(IRequest iRequest, HlsCusAbsProductStructure productStructure, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return productStructureMapper.selectProductStructureData(productStructure);
    }

    @Override
    public HlsCusAbsProductStructure selectCashStructure(IRequest iRequest, Long structureId, String dataClass, Long times) {

        return productStructureMapper.selectCashStructure(structureId,dataClass,times);
    }

    @Override
    public BigDecimal selectStructureCashInterestSum(Long productId, Long interestPeriodDays) {

        return productStructureMapper.selectStructureCashInterestSum(productId,interestPeriodDays);
    }


    @Override
    public int selectStructureQuoteCount(HlsCusAbsProductStructure productStructure) {

        return productStructureMapper.selectStructureQuoteCount(productStructure);
    }
}