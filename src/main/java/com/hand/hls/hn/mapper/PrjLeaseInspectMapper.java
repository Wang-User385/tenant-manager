package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjLeaseInspect;

import java.util.List;

public interface PrjLeaseInspectMapper extends Mapper<PrjLeaseInspect>{
    List<PrjLeaseInspect> queryList(PrjLeaseInspect prjLeaseInspect);
}