package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusContractTermination;

public interface HlsCusContractTerminationMapper extends Mapper<HlsCusContractTermination> {

    HlsCusContractTermination queryTerminationByContractId(Long contractId);
}