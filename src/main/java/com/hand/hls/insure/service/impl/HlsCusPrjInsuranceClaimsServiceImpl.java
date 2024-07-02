package com.hand.hls.insure.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.insure.dto.HlsCusPrjInsuranceClaims;
import com.hand.hls.insure.mapper.HlsCusPrjInsuranceClaimsMapper;
import com.hand.hls.insure.service.HlsCusPrjInsuranceClaimsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjInsuranceClaimsServiceImpl extends BaseServiceImpl<HlsCusPrjInsuranceClaims> implements HlsCusPrjInsuranceClaimsService {

    @Autowired
    private HlsCusPrjInsuranceClaimsMapper hlsCusPrjInsuranceClaimsMapper;

    @Override
    public List<HlsCusPrjInsuranceClaims> queryAllFile(IRequest request, HlsCusPrjInsuranceClaims dto) {
        return hlsCusPrjInsuranceClaimsMapper.queryAllFile(dto);
    }
}