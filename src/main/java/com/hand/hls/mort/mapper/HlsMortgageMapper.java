package com.hand.hls.mort.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.lease.dto.YxLeaseItemClassify;
import com.hand.hls.mort.dto.HlsMortgage;

import java.util.List;
import java.util.Map;

public interface HlsMortgageMapper extends Mapper<HlsMortgage> {
    List<YxLeaseItemClassify> HlsContractListQuery(Map map);
    List<YxLeaseItemClassify> HlsMortgageDetailQuery(Map map);
    List<YxLeaseItemClassify> HlsMortgageVehicleQuery(Map map);
    List<YxLeaseItemClassify> HlsMortgageMachineQuery(Map map);
}