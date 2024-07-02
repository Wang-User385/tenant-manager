package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjLeaseCapacityUse;
import com.hand.hls.hn.dto.PrjLeaseCheck;

import java.util.List;

public interface PrjLeaseCheckMapper extends Mapper<PrjLeaseCheck>{

    List<PrjLeaseCheck> selectPrjLeaseCheck(PrjLeaseCheck prjLeaseCheck);

}