package com.hand.hls.plm.pli.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.PliLeaseItem;

import java.util.List;

public interface IPliLeaseItemService extends IBaseService<PliLeaseItem>, ProxySelf<IPliLeaseItemService> {
    List<PliLeaseItem> queryPliLeaseItemInfo(PliLeaseItem dto, int page, int pageSize);
}