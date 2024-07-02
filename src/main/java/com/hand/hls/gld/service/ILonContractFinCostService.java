package com.hand.hls.gld.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import com.hand.hls.gld.dto.LonContractFinCost;

public interface ILonContractFinCostService extends IBaseService<LonContractFinCost>, ProxySelf<ILonContractFinCostService>{
    /**
     * queryLonContractFinCost
     *
     * @param iRequest IRequest
     * @param lonContractFinCost lonContractFinCost
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @return Result<queryResult>
     */
    List<LonContractFinCost> queryLonContractFinCost(IRequest iRequest, LonContractFinCost lonContractFinCost, int pageNum, int pageSize);

    /**
     * queryCfItemForComb
     *
     * @param var1 request
     * @param var2 LonContractFinCost
     * @param var3 var3
     * @param var4 var4
     * @return Result<query cf item for comb>
     */
    List<LonContractFinCost> queryCfItemForComb(IRequest var1, LonContractFinCost var2, int var3, int var4);

    /**
     * queryLonFinCostByKey
     *
     * @param iRequest IRequest
     * @param hlsCusGldLonContractFinCost HlsCusGldLonContractFinCost
     * @return Result<select value by key>
     */
    HlsCusGldLonContractFinCost queryLonFinCostByKey(IRequest iRequest, HlsCusGldLonContractFinCost hlsCusGldLonContractFinCost);

    /**
     * 融资计提查询
     * @param iRequest
     * @param lonContractFinCost
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<HlsCusGldLonContractFinCost> monthFinCostQuery(IRequest iRequest, HlsCusGldLonContractFinCost lonContractFinCost, int pageNum, int pageSize);

}