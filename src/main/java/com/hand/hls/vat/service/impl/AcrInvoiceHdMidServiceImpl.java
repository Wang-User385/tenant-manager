package com.hand.hls.vat.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.vat.dto.AcrInvoiceHdMid;
import com.hand.hls.vat.service.AcrInvoiceHdMidService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AcrInvoiceHdMidServiceImpl extends BaseServiceImpl<AcrInvoiceHdMid> implements AcrInvoiceHdMidService{

}