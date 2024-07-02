package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductReceipt;
import com.hand.hls.abs.mapper.HlsCusAbsProductReceiptMapper;
import com.hand.hls.abs.service.HlsCusAbsProductReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductReceiptServiceImpl extends BaseServiceImpl<HlsCusAbsProductReceipt> implements HlsCusAbsProductReceiptService {

    @Autowired
    private HlsCusAbsProductReceiptMapper productReceiptMapper;

    @Override
    public List<HlsCusAbsProductReceipt> selectProductReceiptData(IRequest iRequest, HlsCusAbsProductReceipt productReceipt, int page, int pageSize) {

        PageHelper.startPage(page,pageSize);

        return productReceiptMapper.selectProductReceiptData(productReceipt);
    }
}