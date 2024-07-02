package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpSpouse;

import java.util.List;

public interface HlsBpSpouseMapper extends Mapper<HlsBpSpouse>{

    List<HlsBpSpouse> queryAll(HlsBpSpouse hlsBpSpouse);
}