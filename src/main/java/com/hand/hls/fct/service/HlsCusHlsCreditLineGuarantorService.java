package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineGuarantor;

import java.util.List;

public interface HlsCusHlsCreditLineGuarantorService extends IBaseService<HlsCusHlsCreditLineGuarantor>, ProxySelf<HlsCusHlsCreditLineGuarantorService> {

    List<HlsCusHlsCreditLineGuarantor> queryCreditLineGuarantor(IRequest iRequest, HlsCusHlsCreditLineGuarantor hlsCusHlsCreditLineGuarantor, int page, int pageSize);

}
