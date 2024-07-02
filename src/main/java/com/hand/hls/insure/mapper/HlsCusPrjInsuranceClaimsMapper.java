package com.hand.hls.insure.mapper;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.insure.dto.HlsCusPrjInsuranceClaims;

import java.util.List;

public interface HlsCusPrjInsuranceClaimsMapper extends Mapper<HlsCusPrjInsuranceClaims> {
    List<HlsCusPrjInsuranceClaims> queryAllFile(HlsCusPrjInsuranceClaims dto);
}