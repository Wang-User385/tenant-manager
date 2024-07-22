package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.partner.dto.LeasingNotice;

public interface ILeasingNoticeService extends IBaseService<LeasingNotice>, ProxySelf<ILeasingNoticeService> {
    void insertNoticeMsg(IRequest iRequest, LeasingNotice leasingNotice);

    void updateLog(IRequest iRequest, LeasingNotice leasingNotice);

    void noticeRePush(LeasingNotice leasingNotice, IRequest iRequest);
}
