package com.hand.hls.gld.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.HlsCusContractFinanceIncome;
import com.hand.hls.gld.mapper.HlsCusContractFinanceIncomeMapper;
import com.hand.hls.gld.service.HlsCusContractFinanceIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractFinanceIncomeServiceImpl extends BaseServiceImpl<HlsCusContractFinanceIncome> implements HlsCusContractFinanceIncomeService {

    @Autowired
    private HlsCusContractFinanceIncomeMapper mapper;

}