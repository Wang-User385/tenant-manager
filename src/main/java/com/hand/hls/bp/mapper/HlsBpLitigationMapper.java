package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpLitigation;

import java.util.List;

public interface HlsBpLitigationMapper extends Mapper<HlsBpLitigation>{

    List<HlsBpLitigation> queryAll(HlsBpLitigation hlsBpLitigation);
}