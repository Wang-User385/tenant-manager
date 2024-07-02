package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpSeniorPersion;

import java.util.List;

public interface HlsCusBpSeniorPersionMapper extends Mapper<HlsCusBpSeniorPersion> {
    /*高管及主要人员查询*/
    List<HlsCusBpSeniorPersion> querySeniorByBpId(HlsCusBpSeniorPersion hlsCusBpSeniorPersion);
}