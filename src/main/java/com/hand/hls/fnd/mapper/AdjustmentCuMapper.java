package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.fnd.dto.AdjustmentCu;

import java.util.List;

public interface AdjustmentCuMapper extends Mapper<AdjustmentCu>{
    List<AdjustmentCu> queryByBp(AdjustmentCu cu);
}