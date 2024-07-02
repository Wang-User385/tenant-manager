package com.hand.hls.fct.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fct.dto.HlsCusHlsCreditChanceGuarantor;

import java.util.List;

public interface HlsCusHlsCreditChanceGuarantorMapper extends Mapper<HlsCusHlsCreditChanceGuarantor> {

    List<HlsCusHlsCreditChanceGuarantor> selectByForeignKey(HlsCusHlsCreditChanceGuarantor chanceGuarantor);
}