package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusContractRepaymentLn;
import com.hand.hls.fin.mapper.HlsCusContractRepaymentLnMapper;
import com.hand.hls.fin.service.HlsCusContractRepaymentLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusContractRepaymentLnServiceImpl extends BaseServiceImpl<HlsCusContractRepaymentLn> implements HlsCusContractRepaymentLnService {
    @Autowired
    private HlsCusContractRepaymentLnMapper hlsCusContractRepaymentLnMapper;

    @Override
    public List<HlsCusContractRepaymentLn> selectData(IRequest request, HlsCusContractRepaymentLn hlsCusContractRepaymentLn, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hlsCusContractRepaymentLnMapper.selectData(hlsCusContractRepaymentLn);
    }

    @Override
    public HlsCusContractRepaymentLn selectRepaymentLnAmountSum(Long repaymentId) {
        return hlsCusContractRepaymentLnMapper.selectRepaymentLnAmountSum(repaymentId);
    }
}