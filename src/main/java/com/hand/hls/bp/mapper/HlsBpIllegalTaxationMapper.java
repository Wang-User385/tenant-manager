package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpIllegalTaxation;

import java.util.List;

public interface HlsBpIllegalTaxationMapper extends Mapper<HlsBpIllegalTaxation>{

    List<HlsBpIllegalTaxation> queryAll(HlsBpIllegalTaxation hlsBpIllegalTaxation);
}