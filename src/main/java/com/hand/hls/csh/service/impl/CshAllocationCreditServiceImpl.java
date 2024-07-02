package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.CshAllocationCredit;
import com.hand.hls.csh.mapper.CshAllocationCreditMapper;
import com.hand.hls.csh.service.ICshAllocationCreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshAllocationCreditServiceImpl extends BaseServiceImpl<CshAllocationCredit> implements ICshAllocationCreditService {
    @Autowired
    private CshAllocationCreditMapper cshAllocationCreditMapper;

    @Override
    public List<CshAllocationCredit> creditQuery(IRequest iRequest, CshAllocationCredit cshAllocationCredit, int page, int pageSize) {
        //PageHelper.startPage(page, pageSize);
        return cshAllocationCreditMapper.creditQuery(cshAllocationCredit);
    }
}