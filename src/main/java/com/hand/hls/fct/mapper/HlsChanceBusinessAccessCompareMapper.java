package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;

import java.util.List;

public interface HlsChanceBusinessAccessCompareMapper extends Mapper<HlsChanceBusinessAccessCompare>{
    Long selectSeqId();

    void addChanceCompare(HlsChanceBusinessAccessCompare dto);

    List<HlsChanceBusinessAccessCompare> queryChanceCompare(HlsChanceBusinessAccessCompare dto);
    List<HlsChanceBusinessAccessCompare> queryChanceCompareByChanceId(HlsCusHlsCreditLineChance dto);

    /***
     * 保理业务审核信息
     * @param hlsCusHlsCreditLineChance
     * @return
     */
    List<HlsChanceBusinessAccessCompare> findFactoringInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance);
}