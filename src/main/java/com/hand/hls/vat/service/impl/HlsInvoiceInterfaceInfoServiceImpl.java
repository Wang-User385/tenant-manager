package com.hand.hls.vat.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.hand.hls.vat.dto.HlsInvoiceInterfaceInfo;
import com.hand.hls.vat.service.IHlsInvoiceInterfaceInfoService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsInvoiceInterfaceInfoServiceImpl extends BaseServiceImpl<HlsInvoiceInterfaceInfo> implements IHlsInvoiceInterfaceInfoService{

}