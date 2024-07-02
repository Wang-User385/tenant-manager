package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonContractOtherPurpose;
import com.hand.hls.fin.mapper.HlsCusCtLonContractOtherPurposeMapper;
import com.hand.hls.fin.service.HlsCusCtLonContractOtherPurposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCtLonContractOtherPurposeServiceImpl extends BaseServiceImpl<HlsCusCtLonContractOtherPurpose> implements HlsCusCtLonContractOtherPurposeService {

    @Autowired
    private HlsCusCtLonContractOtherPurposeMapper mapper;

}