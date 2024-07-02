package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusFinanceRedeemBak;

import java.util.List;

public interface HlsCusIFinanceRedeemBakService extends IBaseService<HlsCusFinanceRedeemBak>, ProxySelf<HlsCusIFinanceRedeemBakService> {
    HlsCusFinanceRedeemBak submitBak(IRequest iRequest, HlsCusFinanceRedeemBak hlsCusFinanceRedeemBak);

    List<HlsCusFinanceRedeemBak> queryAll(IRequest iRequest, HlsCusFinanceRedeemBak hlsCusFinanceRedeemBak, int page, int pageSize);

    List<HlsCusFinanceRedeemBak> submitWfl(IRequest iRequest, HlsCusFinanceRedeemBak hlsCusFinanceRedeemBak);
}