package com.hand.hls.common.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.common.dto.HlsCusItfcBankFlow;

import java.util.List;

public interface HlsCusItfcBankFlowMapper extends Mapper<HlsCusItfcBankFlow>{
    List<HlsCusItfcBankFlow> queryBankFlowInfo(HlsCusItfcBankFlow hlsCusItfcBankFlow);
}