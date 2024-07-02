package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpSanction;

import java.util.List;

public interface HlsBpSanctionMapper extends Mapper<HlsBpSanction>{

    List<HlsBpSanction> queryAll(HlsBpSanction hlsBpSanction);

}