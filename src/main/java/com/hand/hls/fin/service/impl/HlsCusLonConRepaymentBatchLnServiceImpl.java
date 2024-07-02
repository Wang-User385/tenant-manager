package com.hand.hls.fin.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatchLn;
import com.hand.hls.fin.mapper.HlsCusLonConRepaymentBatchLnMapper;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonConRepaymentBatchLnServiceImpl extends BaseServiceImpl<HlsCusLonConRepaymentBatchLn> implements IHlsCusLonConRepaymentBatchLnService {


    @Autowired
    private HlsCusLonConRepaymentBatchLnMapper hlsCusLonConRepaymentBatchLnMapper;

    @Override
    public List<HlsCusLonConRepaymentBatchLn> queryWithdrawAccount(HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn) {
       return  hlsCusLonConRepaymentBatchLnMapper.queryWithdrawAccount(hlsCusLonConRepaymentBatchLn);
    }

}