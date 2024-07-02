package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpLitigationFinal;

import java.util.List;

public interface HlsBpLitigationFinalMapper extends Mapper<HlsBpLitigationFinal>{

    List<HlsBpLitigationFinal> queryAll(HlsBpLitigationFinal hlsBpLitigationFinal);

    int insertHlsBpLitigationFinal(HlsBpLitigationFinal hlsBpLitigationFinal);
}