package com.hand.hls.csh.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.csh.dto.CshAllocationReceipt;
import com.hand.hls.csh.mapper.CshAllocationReceiptMapper;
import com.hand.hls.csh.service.ICshAllocationReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CshAllocationReceiptServiceImpl extends BaseServiceImpl<CshAllocationReceipt> implements ICshAllocationReceiptService {
    @Autowired
    private CshAllocationReceiptMapper cshAllocationReceiptMapper;

    @Override
    public List<CshAllocationReceipt> receiptQuery(IRequest iRequest, CshAllocationReceipt cshAllocationReceipt, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return cshAllocationReceiptMapper.receiptQuery(cshAllocationReceipt);
    }
}