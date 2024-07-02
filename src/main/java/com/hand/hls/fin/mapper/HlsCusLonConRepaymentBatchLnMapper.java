package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatchLn;

import java.util.List;

public interface HlsCusLonConRepaymentBatchLnMapper extends Mapper<HlsCusLonConRepaymentBatchLn>{

    List<HlsCusLonConRepaymentBatchLn> queryWithdrawAccount(HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn);

    void deleteBatchLnByBatchId(HlsCusLonConRepaymentBatchLn hlsCusLonConRepaymentBatchLn);
}