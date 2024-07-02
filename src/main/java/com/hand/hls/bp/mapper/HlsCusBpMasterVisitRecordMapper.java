package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMasterVisitRecord;

import java.util.List;

public interface HlsCusBpMasterVisitRecordMapper extends Mapper<HlsCusBpMasterVisitRecord> {
    List<HlsCusBpMasterVisitRecord> selectByBpid(HlsCusBpMasterVisitRecord dto);
}