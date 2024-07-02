package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusCtLonContractPledge;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HlsCusCtLonContractPledgeMapper<T extends HlsCusCtLonContractPledge> extends Mapper<HlsCusCtLonContractPledge> {


    /**
     * 质押查询
     * @param lonContractPledge
     * @return
     */
    List<HlsCusCtLonContractPledge> selectLonContractPledge(HlsCusCtLonContractPledge lonContractPledge);


    /**
     * 剩余金额查询 带出
     * @param lonContractPledge
     * @return
     */
    List<HlsCusCtLonContractPledge> selectContractSurpusAmount(HlsCusCtLonContractPledge lonContractPledge);


    /**
     * 合同质押选择期数lov
     * @param lonContractPledge
     * @return
     */
    List<HlsCusCtLonContractPledge> selectContractCashFlow(HlsCusCtLonContractPledge lonContractPledge);

    /**
     * 更新保理合同质押状态
     * @return
     */
    int updateFctContractPledgeFlag();


    /**
     * 更新租赁合同质押状态
     * @return
     */
    int updateConContractPledgeFlag();


    /**
     * 更新质押是否释放状态
     * @return
     */
    int updatePledgeReleaseFlag();

    Map<String,Object> selectPledgeMaxAndMinTimes(@Param("pledgeId") Long pledgeId);
    Double queryForAmount(@Param("contractId") Long contractId,@Param("pledgeTimesFrom") Date pledgeTimesFrom,@Param("pledgeTimesTo") Date pledgeTimesTo);

    List<HlsCusCtLonContractPledge> selectByContractNumber(String  contractNumber);
}