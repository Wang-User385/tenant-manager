package com.hand.hls.fin.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fin.dto.HlsCusLonConRepaymentBatch;

import java.util.List;
import java.util.Map;

public interface HlsCusLonConRepaymentBatchMapper extends Mapper<HlsCusLonConRepaymentBatch>{

    List<HlsCusLonConRepaymentBatch> selectLonConRepaymentBatchConfirm (HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch);

    List<HlsCusLonConRepaymentBatch> selectLonConRepaymentBatchQuery (HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch);

    List<Map> selectLonConRepaymentBatchDetail(HlsCusLonConRepaymentBatch hlsCusLonConRepaymentBatch);
}