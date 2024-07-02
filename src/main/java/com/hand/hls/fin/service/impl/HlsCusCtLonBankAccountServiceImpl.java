package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonBankAccount;
import com.hand.hls.fin.mapper.HlsCusCtLonContractRefBankInfoMapper;
import com.hand.hls.fin.service.HlsCusCtLonBankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=Exception.class)
public class HlsCusCtLonBankAccountServiceImpl extends BaseServiceImpl<HlsCusCtLonBankAccount> implements HlsCusCtLonBankAccountService {

    @Autowired
    HlsCusCtLonContractRefBankInfoMapper hlsCusCtLonContractRefBankInfoMapper;

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }
}
