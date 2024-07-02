package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectChanceMp;

import java.util.List;

public interface HlsCusPrjProjectChanceMpMapper extends Mapper<HlsCusPrjProjectChanceMp>{
    List<HlsCusPrjProjectChanceMp> queryPrjProjectChance(HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp);
    List<HlsCusPrjProjectChanceMp> queryPrjProjectChanceChangeBefore(/*HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp*/);
    List<HlsCusPrjProjectChanceMp> queryPrjProjectChanceChangeBefore1(HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp);
}