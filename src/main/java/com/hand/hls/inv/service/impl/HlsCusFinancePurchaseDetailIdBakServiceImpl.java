package com.hand.hls.inv.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.inv.dto.HlsCusFinancePurchaseDetailIdBak;
import com.hand.hls.inv.service.HlsCusIFinancePurchaseDetailIdBakService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFinancePurchaseDetailIdBakServiceImpl extends BaseServiceImpl<HlsCusFinancePurchaseDetailIdBak> implements HlsCusIFinancePurchaseDetailIdBakService {

}