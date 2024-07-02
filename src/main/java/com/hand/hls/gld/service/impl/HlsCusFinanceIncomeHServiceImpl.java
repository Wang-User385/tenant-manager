package com.hand.hls.gld.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.HlsCusFinanceIncomeH;
import com.hand.hls.gld.mapper.HlsCusFinanceIncomeHMapper;
import com.hand.hls.gld.service.IHlsCusFinanceIncomeHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFinanceIncomeHServiceImpl extends BaseServiceImpl<HlsCusFinanceIncomeH> implements IHlsCusFinanceIncomeHService{

    @Autowired
    private HlsCusFinanceIncomeHMapper hlsCusFinanceIncomeHMapper;

    @Override
    public Long selectVersionCount(HlsCusFinanceIncomeH hlsCusFinanceIncomeH) {
        return hlsCusFinanceIncomeHMapper.selectVersionCount(hlsCusFinanceIncomeH);
    }

    @Override
    public HlsCusFinanceIncomeH selectRecordByVersionId(HlsCusFinanceIncomeH hlsCusFinanceIncomeH) {
        return hlsCusFinanceIncomeHMapper.selectRecordByVersionId(hlsCusFinanceIncomeH);
    }
}