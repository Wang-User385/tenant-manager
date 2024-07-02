package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsChanceBusinessAccessCompare;

import java.util.List;

public interface HlsChanceBusinessAccessCompareMapper extends Mapper<HlsChanceBusinessAccessCompare>{
    Long selectSeqId();

    void addChanceCompare(HlsChanceBusinessAccessCompare dto);

    List<HlsChanceBusinessAccessCompare> queryChanceCompare(HlsChanceBusinessAccessCompare dto);
}