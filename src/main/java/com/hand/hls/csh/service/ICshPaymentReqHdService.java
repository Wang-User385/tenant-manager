package com.hand.hls.csh.service;


import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;

import java.util.List;

public interface ICshPaymentReqHdService extends IBaseService<HlsCusCshPaymentReqHd>, ProxySelf<ICshPaymentReqHdService> {
    List<HlsCusCshPaymentReqHd> queryAll(HlsCusCshPaymentReqHd var1, int var2, int var3);

    HlsCusCshPaymentReqHd queryCshPaymentHdById(Long var1);

    List<HlsCusCshPaymentReqHd> queryApply(Long var1);
}
