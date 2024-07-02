package com.hand.hls.vat.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.vat.dto.HlsCusAcrReceiptLn;
import com.hand.hls.vat.mapper.HlsCusAcrReceiptLnMapper;
import com.hand.hls.vat.service.IAcrReceiptLnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AcrReceiptLnServiceImpl extends BaseServiceImpl<HlsCusAcrReceiptLn> implements IAcrReceiptLnService {

    @Autowired
    private HlsCusAcrReceiptLnMapper acrReceiptLnMapper;

}