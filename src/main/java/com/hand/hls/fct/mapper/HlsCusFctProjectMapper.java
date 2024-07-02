package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusFctProject;

import java.util.List;

public interface HlsCusFctProjectMapper extends Mapper<HlsCusFctProject> {

    List<HlsCusFctProject> selectManagerByUnitId(HlsCusFctProject fctProject);

}
