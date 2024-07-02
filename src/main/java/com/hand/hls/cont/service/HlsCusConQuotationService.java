package com.hand.hls.cont.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConQuotation;

public interface HlsCusConQuotationService extends IBaseService<HlsCusConQuotation>, ProxySelf<HlsCusConQuotationService> {

    HlsCusConQuotation conQuotationSave(IRequest iRequest, HlsCusConQuotation hlsCusConQuotation);

}