package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckFinanceSituation;
import com.hand.hls.hn.dto.PrjCheckGuarantMortgage;

import java.util.List;

public interface PrjCheckFinanceSituationMapper extends Mapper<PrjCheckFinanceSituation>{
    List<PrjCheckFinanceSituation> queryList(PrjCheckFinanceSituation prjCheckFinanceSituation);
}