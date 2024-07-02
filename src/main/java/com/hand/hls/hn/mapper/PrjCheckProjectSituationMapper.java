package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckProjectSituation;
import com.hand.hls.hn.dto.PrjCheckPutPay;

import java.util.List;

public interface PrjCheckProjectSituationMapper extends Mapper<PrjCheckProjectSituation>{
    List<PrjCheckProjectSituation> queryList(PrjCheckProjectSituation prjCheckProjectSituation);
}