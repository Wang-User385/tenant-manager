package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusBpShareholderInformation;

import java.util.List;

public interface HlsCusBpShareholderInformationMapper extends Mapper<HlsCusBpShareholderInformation> {

    List<HlsCusBpShareholderInformation> queryAll(HlsCusBpShareholderInformation bpShareholderInformation);

}