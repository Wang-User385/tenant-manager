package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.mapper.CshAllocationDepositMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.csh.dto.CshAllocationDeposit;
import com.hand.hls.csh.service.ICshAllocationDepositService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CshAllocationDepositServiceImpl extends BaseServiceImpl<CshAllocationDeposit> implements ICshAllocationDepositService{
    @Autowired
    private CshAllocationDepositMapper cshAllocationDepositMapper;

    /**
     * 二期功能：查询核销为保证金详情
     *
     * @param requestCtx
     * @param cshAllocationDeposit
     * @param pagenum
     * @param pagesize
     * @return
     */
    @Override
    public List<CshAllocationDeposit> getDepositList(IRequest requestCtx, CshAllocationDeposit cshAllocationDeposit, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return cshAllocationDepositMapper.getDepositList(cshAllocationDeposit);
    }
}