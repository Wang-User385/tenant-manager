package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusHlsBpSeniorInstitution;

import java.util.List;

public interface HlsCusHlsBpSeniorInstitutionMapper extends Mapper<HlsCusHlsBpSeniorInstitution> {
    List<HlsCusHlsBpSeniorInstitution> queryAll(HlsCusHlsBpSeniorInstitution dto);

}