package com.hand.hls.csh.mapper;

import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 保证金抵扣日志表mapper
 *
 * @author wuyicheng
 * @date 2020/05/17
 */
public interface CshDepositDeductJobMapper {

    /**
     * 查询本次跑批的所有合同
     * @param contractNumber 单独的合同编号（如果有）
     * @return 本次跑批的所有合同
     */
    List<HlsCusConContract> selectAllContract(@Param("contractNumber") String contractNumber);

    /**
     * 查询某个合同下可以用于抵扣的所有保证金现金事务
     * @param contractId 合同ID
     * @return 保证金现金事务
     */
    List<HlsCusCshTransaction> selectTransactionBelongContract(@Param("contractId") Long contractId);

}
