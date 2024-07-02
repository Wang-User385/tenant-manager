package com.hand.hls.csh.mapper;

import com.hand.hls.csh.dto.HlsCusConDebtExemptionReqCf;

import java.util.List;
import java.util.Map;

public interface HlsCusConDebtExemptionReqCfMapper extends ConDebtExemptionReqCfMapper<HlsCusConDebtExemptionReqCf> {

    List<HlsCusConDebtExemptionReqCf> selectReqCfByCashflowId(HlsCusConDebtExemptionReqCf var1);

    List<HlsCusConDebtExemptionReqCf> selectConDebtExemptionReqCf (HlsCusConDebtExemptionReqCf hlsCusConDebtExemptionReqCf);

    List<Map> selectConDebtExemptionReqCf1 (HlsCusConDebtExemptionReqCf hlsCusConDebtExemptionReqCf);
}