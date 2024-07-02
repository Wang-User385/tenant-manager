package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpLitigationHt;

import java.util.List;

public interface HlsBpLitigationHtMapper extends Mapper<HlsBpLitigationHt>{

    List<HlsBpLitigationHt> queryAll(HlsBpLitigationHt hlsBpLitigationHt);
}