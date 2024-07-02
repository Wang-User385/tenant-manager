package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusCtLonContractMortgage;
import com.hand.hls.fin.dto.HlsCusLonContract;

import java.util.List;

public interface HlsCusCtLonContractMortgageMapper<T extends HlsCusCtLonContractMortgage> extends Mapper<HlsCusCtLonContractMortgage> {


    List<HlsCusCtLonContractMortgage> selectLonContractMortgage(HlsCusCtLonContractMortgage hlsCusCtLonContractMortgage);
}