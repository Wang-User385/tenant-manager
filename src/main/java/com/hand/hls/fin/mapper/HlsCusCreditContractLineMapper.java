package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusCreditContractLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusCreditContractLineMapper extends Mapper<HlsCusCreditContractLine> {

    /**
     * 查询
     * @param hlsCusCreditContractLine
     * @return
     */
    List<HlsCusCreditContractLine> selectCreditContractLine(HlsCusCreditContractLine hlsCusCreditContractLine);


    /**
     * 额度汇总
     * @param creditContractId
     * @return
     */
    Double selectCreditLineAmtSum(@Param("creditContractId") Long creditContractId);


    /**
     * 总额度剩余
     * @param creditContractId
     * @return
     */
    Double selectRetainCreditAmt(@Param("creditContractId") Long creditContractId, @Param("creditLineId") Long creditLineId);


    Double selectForecastCreditAmt(@Param("creditContractId") Long creditContractId, @Param("creditLineId") Long creditLineId);


    /**
     * 授信的提款个数
     * @param creditContractId
     * @param creditLineId
     * @return
     */
    int selectCreditWithdrawCount(@Param("creditContractId") Long creditContractId, @Param("creditLineId") Long creditLineId, @Param("withdrawFlag") String withdrawFlag);


    /**
     * 选择相同的融资机构带出
     * @param hlsCusCreditContractLine
     * @return
     */
    List<HlsCusCreditContractLine>  selectCreditLineCarry(HlsCusCreditContractLine hlsCusCreditContractLine);
}