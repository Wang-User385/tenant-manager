package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckPlan;
import com.hand.hls.hn.dto.PrjLeaseCapacityUse;

import java.util.List;

public interface PrjLeaseCapacityUseMapper extends Mapper<PrjLeaseCapacityUse>{

    List<PrjLeaseCapacityUse> selectCapacityUse(PrjLeaseCapacityUse prjLeaseCapacityUse);

}