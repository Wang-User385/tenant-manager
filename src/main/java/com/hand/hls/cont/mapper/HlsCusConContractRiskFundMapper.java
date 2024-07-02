package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractRiskFund;

public interface HlsCusConContractRiskFundMapper extends Mapper<HlsCusConContractRiskFund> {

    void riskFundAccrual(HlsCusConContractRiskFund dt);
}
