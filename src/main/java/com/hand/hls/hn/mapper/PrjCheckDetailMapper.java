package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckDetail;
import com.hand.hls.hn.dto.PrjCheckFinanceSituation;

import java.util.List;

public interface PrjCheckDetailMapper extends Mapper<PrjCheckDetail>{
    List<PrjCheckDetail> queryList(PrjCheckDetail prjCheckDetail);
}