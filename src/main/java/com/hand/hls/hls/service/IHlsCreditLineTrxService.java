package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsCusHlsCreditLineTrx;

/**
 * @Author: ever
 * @DATE: 2020-02-14 14:22
 */
public interface IHlsCreditLineTrxService extends IBaseService<HlsCusHlsCreditLineTrx>, ProxySelf<IHlsCreditLineTrxService> {

    void calcCreditMethod(IRequest var1, Long var2);
}
