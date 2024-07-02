package com.hand.hls.abs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.abs.dto.HlsCusAbsContractFinish;

import java.util.List;

public interface HlsCusAbsContractFinishMapper extends Mapper<HlsCusAbsContractFinish> {

    /**
     * 资产变更提前结清
     */
    List<HlsCusAbsContractFinish> queryAbsContractFinish(HlsCusAbsContractFinish hlsCusAbsContractFinish);
}
