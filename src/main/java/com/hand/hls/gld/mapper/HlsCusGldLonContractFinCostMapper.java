package com.hand.hls.gld.mapper;


import com.hand.hls.gld.dto.HlsCusGldLonContractFinCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusGldLonContractFinCostMapper extends GldLonContractFinCostMapper<HlsCusGldLonContractFinCost> {
    /*计提查询*/
    List<HlsCusGldLonContractFinCost> countDrawQuery(HlsCusGldLonContractFinCost dto);

    /*获取已计提的利息总和*/
    List<HlsCusGldLonContractFinCost> querySumCost(HlsCusGldLonContractFinCost dto);


    /**
     * 提款最后一期的id
     *
     * @param withdrawId
     * @return
     */
    Long selectWithdrawLastTimeId(@Param("withdrawId") Long withdrawId);


    Double selectFincomeInCludSumExcept(@Param("withdrawId") Long withdrawId, @Param("financeCostId") Long financeCostId);


    /**
     * 更新分摊金额
     *
     * @param financeCostId
     * @param financeIncomeInclud
     * @return
     */
    int updateFinCostIncome(@Param("financeCostId") Long financeCostId, @Param("financeIncomeInclud") Double financeIncomeInclud);


    /**
     * 计提查询,只查询实际利率法更新的数据
     * @param dto
     * @return
     */
    List<HlsCusGldLonContractFinCost>queryLonContractFinCostForHistoryJc(HlsCusGldLonContractFinCost dto);

    /**
     * 融资月结查询
     * @param dto
     * @return
     */
    List<HlsCusGldLonContractFinCost> monthFinCostQuery(HlsCusGldLonContractFinCost dto);


    List<HlsCusGldLonContractFinCost> selectCostAccrual(HlsCusGldLonContractFinCost dto);
}
