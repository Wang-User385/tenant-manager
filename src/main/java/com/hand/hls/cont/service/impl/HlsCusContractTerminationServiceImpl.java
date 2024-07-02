package com.hand.hls.cont.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusContractTermination;
import com.hand.hls.cont.mapper.HlsCusContractTerminationMapper;
import com.hand.hls.cont.service.HlsCusContractTerminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractTerminationServiceImpl extends BaseServiceImpl<HlsCusContractTermination> implements HlsCusContractTerminationService {
    @Autowired
    HlsCusContractTerminationMapper hlsCusContractTerminationMapper;

    @Override
    public HlsCusContractTermination queryTerminationByContractId(Long contractId) {
        return hlsCusContractTerminationMapper.queryTerminationByContractId(contractId);
    }

    @Override
    public HlsCusContractTerminationService self() {
        return null;
    }
}