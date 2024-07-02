package com.hand.hls.fin.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusCreditContract;

import java.util.List;

public interface CreditContractMapper<T extends HlsCusCreditContract> extends Mapper<HlsCusCreditContract> {
    List<HlsCusCreditContract> unitSelect(HlsCusCreditContract var1);
}
