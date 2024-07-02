package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface HlsScoreCalculationMapper extends Mapper<HlsScoreCalculation>{
    List<HlsScoreCalculation> selectScoreCalculation(HlsScoreCalculation hlsScoreCalculation);

    List<HlsScoreCalculation> selectListOfHsc1(HlsScoreCalculation var1);
    //同年 评级数量统计
    List<HlsScoreCalculation> selectScoreCount(HlsScoreCalculation hlsScoreCalculation);
    List<HlsScoreCalculation> selectScoreMes(@Param("bpId")Long bpId,@Param("maxToDate")Date maxToDate);
    List<HlsScoreCalculation> selectScoreCountF(@Param("bpId")Long bpId,@Param("toDate")Date toDate);
    List<HlsScoreCalculation> selectScoreCountM(Date toDate);
}