package com.hand.hls.csh.mapper;

import com.hand.hls.csh.dto.HlsCusConDebtExemptionReq;

import java.util.List;

public interface HlsCusConDebtExemptionReqMapper extends ConDebtExemptionReqMapper<HlsCusConDebtExemptionReq> {
    List<HlsCusConDebtExemptionReq> selectReqByChangeReqId(HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq);
}