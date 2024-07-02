package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusBpMasterVisit;

import java.util.List;

public interface HlsCusBpMasterVisitMapper extends Mapper<HlsCusBpMasterVisit>{
    List<HlsCusBpMasterVisit> bpMasterVisitQuery();
}