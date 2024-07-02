package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectSupplement;

import java.util.List;

public interface HlsCusPrjProjectSupplementMapper extends Mapper<HlsCusPrjProjectSupplement>{

    List<HlsCusPrjProjectSupplement> querySuppleByProjectId(HlsCusPrjProjectSupplement hlsCusPrjProjectSupplement);

}