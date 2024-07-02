package com.hand.hls.hls.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hls.dto.HlsDurationDepositAccount;

import java.util.List;

public interface HlsDurationDepositAccountMapper extends Mapper<HlsDurationDepositAccount> {
    List<HlsDurationDepositAccount> hlsDurationDepositAccountDetailQuery(HlsDurationDepositAccount hlsDurationDepositAccount);

    Double getRefundAmount(Long depositId);
}