package com.hand.hls.inv.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.inv.dto.HlsCusInvFinancialProducts;
import com.hand.hls.inv.mapper.HlsCusInvFinancialProductsMapper;
import com.hand.hls.inv.service.HlsCusInvFinancialProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusInvFinancialProductsServiceImpl extends BaseServiceImpl<HlsCusInvFinancialProducts> implements HlsCusInvFinancialProductsService {

    @Autowired
    private HlsCusInvFinancialProductsMapper hlsCusInvFinancialProductsMapper;

    @Override
    public List<HlsCusInvFinancialProducts> queryProductsList(IRequest iRequest, HlsCusInvFinancialProducts hlsCusInvFinancialProducts, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return hlsCusInvFinancialProductsMapper.queryProductsList(hlsCusInvFinancialProducts);
    }
}