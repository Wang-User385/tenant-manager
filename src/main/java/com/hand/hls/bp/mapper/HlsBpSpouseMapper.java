package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpSpouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsBpSpouseMapper extends Mapper<HlsBpSpouse>{

    List<HlsBpSpouse> queryAll(HlsBpSpouse hlsBpSpouse);

    List<HlsBpSpouse> selectByBpId(@Param("bpId") Long bpId);
}