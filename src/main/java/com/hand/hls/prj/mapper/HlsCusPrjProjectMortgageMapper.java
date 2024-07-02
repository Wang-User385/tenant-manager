package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectMortgage;

public interface HlsCusPrjProjectMortgageMapper extends Mapper<HlsCusPrjProjectMortgage> {

    void deleteMortgageByProjectId(Long projectId);

}