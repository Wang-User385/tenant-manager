package com.hand.hls.plm.pli.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.PlmPliCheckItem;

import java.util.List;

public interface PlmPliCheckItemService extends IBaseService<PlmPliCheckItem>, ProxySelf<PlmPliCheckItemService> {

    List<PlmPliCheckItem> createItemForPli(PlmPliCheckItem dto, String[] type);

    int delete(PlmPliCheckItem dto);
}
