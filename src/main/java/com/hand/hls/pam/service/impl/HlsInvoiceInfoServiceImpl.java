package com.hand.hls.pam.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.pam.dto.HlsInvoiceInfo;
import com.hand.hls.pam.service.IHlsInvoiceInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsInvoiceInfoServiceImpl extends BaseServiceImpl<HlsInvoiceInfo> implements IHlsInvoiceInfoService{

}