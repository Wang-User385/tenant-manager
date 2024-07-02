package com.hand.hls.gld.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsCashflowItem;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.dto.LonContractFinCost;

public interface LonContractFinCostMapper extends Mapper<LonContractFinCost>{
    /**
     * queryLonContractFinCost
     *
     * @param lonContractFinCost lonContractFinCost
     * @return Result<all>
     */
    List<LonContractFinCost> queryLonContractFinCost(LonContractFinCost lonContractFinCost);

    /**
     * queryCfItemForComb
     *
     * @param var1 var1
     * @return Result<all>
     */
    List<LonContractFinCost> queryCfItemForComb(LonContractFinCost var1);

    /**
     * queryLonFinCostByKey
     *
     * @param hlsCusGldLonContractFinCost HlsCusGldLonContractFinCost
     * @return Result<all>
     */
    HlsCusGldLonContractFinCost queryLonFinCostByKey(HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost);
}