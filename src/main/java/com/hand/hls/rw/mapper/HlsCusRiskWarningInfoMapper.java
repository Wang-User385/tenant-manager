package com.hand.hls.rw.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.rw.dto.HlsCusRiskWarningInfo;

import java.util.List;

public interface HlsCusRiskWarningInfoMapper extends Mapper<HlsCusRiskWarningInfo> {
    List<HlsCusRiskWarningInfo> selectRiskWarningInfo(HlsCusRiskWarningInfo riskWarningInfo);

    List<HlsCusRiskWarningInfo> selectMaxChangeTime(HlsCusRiskWarningInfo riskWarningInfo);

    void updateRiskInfoChangeIq(HlsCusRiskWarningInfo riskWarningInfo);
    List<HlsCusRiskWarningInfo> queryAll (HlsCusRiskWarningInfo riskWarningInfo);
    List<HlsCusRiskWarningInfo> queryPrjCon (HlsCusRiskWarningInfo riskWarningInfo);
}