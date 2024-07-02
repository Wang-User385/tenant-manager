package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjLeaseCheck;
import com.hand.hls.hn.dto.PrjLeaseFactoryBuilding;

import java.util.List;

public interface PrjLeaseFactoryBuildingMapper extends Mapper<PrjLeaseFactoryBuilding>{

    List<PrjLeaseFactoryBuilding> selectPrjLeaseFactoryBuilding(PrjLeaseFactoryBuilding prjLeaseFactoryBuilding);

}