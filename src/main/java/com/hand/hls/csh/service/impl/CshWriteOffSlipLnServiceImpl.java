package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.CshWriteOffSlipLn;
import com.hand.hls.csh.mapper.CshWriteOffSlipLnMapper;
import com.hand.hls.csh.service.ICshWriteOffSlipLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ferry
 * @date 2019-08-08
 * @description 收款单明细业务实现
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class CshWriteOffSlipLnServiceImpl extends BaseServiceImpl<CshWriteOffSlipLn> implements ICshWriteOffSlipLnService {
    @Autowired
    private CshWriteOffSlipLnMapper cshWriteOffSlipLnMapper;

    @Override
    public List<CshWriteOffSlipLn> query(IRequest iRequest, CshWriteOffSlipLn cshWriteOffSlipLn, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return cshWriteOffSlipLnMapper.query(cshWriteOffSlipLn);
    }

    @Override
    public List<CshWriteOffSlipLn> queryRefundBlockDetail(IRequest iRequest, Long transactionId) {
        return cshWriteOffSlipLnMapper.queryRefundBlockDetail(transactionId);
    }

    @Override
    public int deleteBySlipId(Long slipId) {
        CshWriteOffSlipLn slipLn = new CshWriteOffSlipLn();
        slipLn.setSlipId(slipId);
        return cshWriteOffSlipLnMapper.delete(slipLn);
    }
}
