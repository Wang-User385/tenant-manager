package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpCreditHistorical;

import java.util.List;

public interface BpCreditHistoricalMapper extends Mapper<BpCreditHistorical> {

    List<BpCreditHistorical> queryAll(BpCreditHistorical bpCreditHistorical);

}