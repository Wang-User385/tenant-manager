package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonContractConsignmentSalesInfo;
import com.hand.hls.fin.service.HlsCusCtLonContractConsignmentSalesInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
public class HlsCusCtLonContractConsignmentSalesInfoServiceImpl extends BaseServiceImpl<HlsCusCtLonContractConsignmentSalesInfo> implements HlsCusCtLonContractConsignmentSalesInfoService {


    @Override
    public List<HlsCusCtLonContractConsignmentSalesInfo> selectConsignmentSalesList(HlsCusCtLonContractConsignmentSalesInfo hlsCusCtLonContractConsignmentSalesInfo) {
        return null;
    }
}