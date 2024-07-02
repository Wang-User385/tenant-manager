package com.hand.hls.inv.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseBak;

import java.util.List;

public interface HlsCusIFinancePurchaseBakService extends IBaseService<HlsCusFinancePurchaseBak>, ProxySelf<HlsCusIFinancePurchaseBakService> {
    HlsCusFinancePurchaseBak submitBak(IRequest iRequest, HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak);

    List<HlsCusFinancePurchaseBak> queryAll(IRequest iRequest, HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak, int page, int pageSize);

    List<HlsCusFinancePurchaseBak> submitWfl(IRequest iRequest, HlsCusFinancePurchaseBak hlsCusFinancePurchaseBak);
}