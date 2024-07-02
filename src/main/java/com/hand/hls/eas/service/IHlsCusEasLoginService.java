package com.hand.hls.eas.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.eas.dto.HlsCusEasBasicData;
import com.hand.hls.eas.dto.HlsCusEasLogin;

public interface IHlsCusEasLoginService extends IBaseService<HlsCusEasLogin>, ProxySelf<IHlsCusEasLoginService>{
    HlsCusEasLogin easDoLogin(IRequest iRequest, HlsCusEasLogin dto);

    void easBasicSyn(IRequest iRequest, HlsCusEasBasicData dto,String sourceTable,Long sourceId);


    void easCredentialsSynchronization(IRequest iRequest,Long outboundId);

    void easCredentialsDelete(IRequest iRequest,String voucherId, String comOrgNum,Long sourceId);

    void easBankAccountSyn(IRequest iRequest,Long outboundId);

    void easCheckAccountSyn(IRequest iRequest,Long outboundId);

    void  easDataSyn(IRequest iRequest, Long contractId);

    void easVenderDataSyn(IRequest iRequest, Long paymentReqId);

    void easCheckAccountSynNew(IRequest iRequest,String checkDate);
}