package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.mapper.CshAllocationAdvanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.csh.dto.CshAllocationAdvance;
import com.hand.hls.csh.service.ICshAllocationAdvanceService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CshAllocationAdvanceServiceImpl extends BaseServiceImpl<CshAllocationAdvance> implements ICshAllocationAdvanceService{

    @Autowired
    private CshAllocationAdvanceMapper cshAllocationAdvanceMapper;

    /**
     * 查询核销为预收款的数据
     * @param cshAllocationAdvance
     * @return
     */
    @Override
    public List<CshAllocationAdvance> queryAllocationAdvance(CshAllocationAdvance cshAllocationAdvance,int page,int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cshAllocationAdvanceMapper.queryAllocationAdvance(cshAllocationAdvance);
    }
}