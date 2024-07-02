package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.dto.*;
import uncertain.composite.CompositeMap;

import java.util.List;

/**
 * Demo class
 *
 * @author wujun
 * @date 2020/02/06
 * @description 租赁收入确认
 */

public interface ContractFinanceIncomeMapper<T extends HlsCusContractFinanceIncome> extends Mapper<HlsCusContractFinanceIncome> {
    /**
     * fetch data by rule id
     *
     * @param hlsCusContractFinanceIncome HlsCusContractFinanceIncome
     * @return Result<all>
     */
    List<HlsCusContractFinanceIncome> queryContractFinanceIncome(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

    /**
     * query period name for combox
     *
     * @param var1 period_name
     * @return Result<all>
     */
    List<Period> periodNameQueryForComb(Period var1);

    /**
     * query period name for combox
     *
     * @param compositeMap compositeMap
     * @return Result<default value>
     */
    List<CompositeMap> selectDefaultPeriodName(CompositeMap compositeMap);

    /**
     * queryConFinanceIncomeByKey
     *
     * @param hlsCusContractFinanceIncome HlsCusContractFinanceIncome
     * @return Result<all>
     */
    HlsCusContractFinanceIncome queryConFinanceIncomeByKey(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

    /**
     * queryCtDocumentFinIncomeByCondition
     *
     * @param hlsCusCtDocumentFinIncome HlsCusCtDocumentFinIncome
     * @return Result<all>
     */
    List<HlsCusCtDocumentFinIncome> queryCtDocumentFinIncomeByCondition(HlsCusCtDocumentFinIncome hlsCusCtDocumentFinIncome);


    List<HlsCusContractFinanceIncome> reportQuery(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);


    List<HlsCusContractFinanceIncome> selectFeeByCfItem(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

    List<HlsCusContractFinanceIncome> selectFeeByCfItemForPreLeaseInterest(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

    List<HlsCusContractFinanceIncome> selectFeeByCfItemForInterest(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

    List<HlsCusConContract> selectStampDuty(HlsCusConContract hlsCusConContract);

    List<HlsCusContractFinanceIncome> queryByRentDelay(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

    List<HlsCusContractFinanceIncome> conContractFinancialDetailQuery(HlsCusContractFinanceIncome  hlsCusContractFinanceIncome);

    List<HlsCusContractFinanceIncome> conContractCostPlanQuery(HlsCusContractFinanceIncome  hlsCusContractFinanceIncome);

    List<HlsCusContractFinanceIncome> conContractDepositDetailQuery(HlsCusContractFinanceIncome  hlsCusContractFinanceIncome);

    void deleteGldContractFinanceIncomeInterest(HlsCusContractFinanceIncome contractFinanceIncome);

    void deleteGldContractFinanceIncomeCost(HlsCusContractFinanceIncome contractFinanceIncome);


    int insertGldContractFinanceIncomeBatch(List<HlsCusContractFinanceIncome> hlsCusContractFinanceIncomeList);

    List<HlsCusContractFinanceIncome> selectImportTempList(Long headerId);

    List<HlsCusContractFinanceIncome> selectFinanceIncome(HlsCusContractFinanceIncome hlsCusContractFinanceIncome);

}
