package com.hand.hls.plm.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.dto.HlsCusShips;

import java.util.List;

public interface HlsCusShipsService extends IBaseService<HlsCusShips>, ProxySelf<HlsCusShipsService> {

    List<HlsCusShips> selectShip(IRequest requestContext, HlsCusShips dto, int page, int pageSize);

    /**
     * 根据合同ID删除船舶信息
     * @param contractId
     * @return
     */
    int deleteByPliContractId(Long contractId);
}
