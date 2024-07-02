package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpEventOthers;

import java.util.List;

public interface HlsBpEventOthersMapper extends Mapper<HlsBpEventOthers>{

    List<HlsBpEventOthers> queryAll (HlsBpEventOthers hlsBpEventOthers);
}