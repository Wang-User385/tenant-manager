package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshWriteOffSlipLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CshWriteOffSlipLnMapper extends Mapper<CshWriteOffSlipLn>{
    /**
     * 查询收款单行数据
     */
    List<CshWriteOffSlipLn> query(CshWriteOffSlipLn cshWriteOffSlipLn);

    /**
     * 退款的冻结明细
     */
    List<CshWriteOffSlipLn> queryRefundBlockDetail(@Param("transactionId") Long transactionId);
}
