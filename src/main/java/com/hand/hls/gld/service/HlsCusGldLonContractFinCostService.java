package com.hand.hls.gld.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;

import java.util.List;

public interface HlsCusGldLonContractFinCostService extends IBaseService<HlsCusGldLonContractFinCost>, ProxySelf<HlsCusGldLonContractFinCostService> {

    List<HlsCusGldLonContractFinCost> selectCostAccrual(HlsCusGldLonContractFinCost dto);
}
