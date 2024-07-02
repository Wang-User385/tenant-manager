package com.hand.hls.rw.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.rw.dto.HlsCusRiskWarning;

import java.util.List;
import java.util.Map;

public interface HlsCusRiskWarningMapper extends Mapper<HlsCusRiskWarning> {

    List<HlsCusRiskWarning> queryAll(HlsCusRiskWarning riskWarning);

    List<HlsCusRiskWarning> homeThirdQuery(HlsCusRiskWarning riskWarning);

    List<HlsCusRiskWarning> selectIsReleasing(HlsCusRiskWarning riskWarning);

    List<Map> selectHomeChart(HlsCusRiskWarning riskWarning);

    List<HlsCusRiskWarning> selectRiskWarningALL(HlsCusRiskWarning riskWarning);
    List<HlsCusRiskWarning> queryAllNew (HlsCusRiskWarning riskWarning);

    HlsCusRiskWarning queryHlsCusRiskWarningByDocumentId(HlsCusRiskWarning hlsCusRiskWarning);

    int updateRiskWarningStatus(HlsCusRiskWarning hlsCusRiskWarning);

    int updateRiskWarningStatusByRiskWarningNumber(HlsCusRiskWarning hlsCusRiskWarning);


}