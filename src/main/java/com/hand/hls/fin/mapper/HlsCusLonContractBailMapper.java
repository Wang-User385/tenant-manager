package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusCtLonContractMortgage;
import com.hand.hls.fin.dto.HlsCusLonContractBail;

import java.util.List;

public interface HlsCusLonContractBailMapper extends Mapper<HlsCusLonContractBail> {

    List<HlsCusLonContractBail> selectLonContractBail(HlsCusLonContractBail hlsCusLonContractBail);
}