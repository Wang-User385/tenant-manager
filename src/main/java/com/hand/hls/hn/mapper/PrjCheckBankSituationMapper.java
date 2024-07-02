package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckBankSituation;
import com.hand.hls.hn.dto.PrjCheckDetail;

import java.util.List;

public interface PrjCheckBankSituationMapper extends Mapper<PrjCheckBankSituation>{
    List<PrjCheckBankSituation> queryList(PrjCheckBankSituation prjCheckBankSituation);
}