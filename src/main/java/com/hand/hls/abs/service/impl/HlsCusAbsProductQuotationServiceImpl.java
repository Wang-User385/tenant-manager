package com.hand.hls.abs.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsProductQuotation;
import com.hand.hls.abs.service.HlsCusAbsProductQuotationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProductQuotationServiceImpl extends BaseServiceImpl<HlsCusAbsProductQuotation> implements HlsCusAbsProductQuotationService {

}