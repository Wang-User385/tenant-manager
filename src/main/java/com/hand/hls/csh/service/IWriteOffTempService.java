package com.hand.hls.csh.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.WriteOffTemp;

public interface IWriteOffTempService extends IBaseService<HlsCusCshPaymentReqHd>, ProxySelf<IWriteOffTempService>{
    HlsCusCshPaymentReqHd cshHdCreate(IRequest iRequest, HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd);

}