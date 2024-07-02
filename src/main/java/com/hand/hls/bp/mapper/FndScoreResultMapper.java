package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.FndScoreResult;

import java.util.List;

public interface FndScoreResultMapper extends Mapper<FndScoreResult>{

    List<FndScoreResult> selectScoreResult1(FndScoreResult fndScoreResult);
    List<FndScoreResult> queryFndScoreResult1(Long id);
}