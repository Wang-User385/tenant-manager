package com.hand.hls.fin.mapper;

import com.hand.hls.fin.dto.HlsCusCreditContract;
import org.apache.ibatis.annotations.Param;

public interface HlsCusCreditContractMapper extends CreditContractMapper<HlsCusCreditContract> {

    /**
     * 更新授信头上已授信金额
     * @param creditContractId
     * @return
     */
    int updateCreditContractExposureAmt(@Param("creditContractId")  Long creditContractId, @Param("creditLineId") Long creditLineId);


    /**
     * 更新头上未占用额度
     * @param creditContractId
     * @return
     */
    int updateCreditContractUnExposureAmt(@Param("creditContractId")  Long creditContractId ,@Param("creditLineId") Long creditLineId);
    HlsCusCreditContract lonContractCheckQueryAmt(@Param("creditContractId") Long contractId);

}
