package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.FundingExecuteLn;

import java.util.List;

public interface FundingExecuteLnMapper extends Mapper<FundingExecuteLn> {
    FundingExecuteLn queryAmount(FundingExecuteLn executeLn);

    FundingExecuteLn queryAmountSummary(FundingExecuteLn executeLn);

    FundingExecuteLn queryAmountSummarys(FundingExecuteLn executeLn);

    FundingExecuteLn queryAmountOut(FundingExecuteLn executeLn);

    FundingExecuteLn queryAmountSummaryOut(FundingExecuteLn executeLn);

    FundingExecuteLn queryAmountSummarysOut(FundingExecuteLn executeLn);

    List<FundingExecuteLn> queryAll(FundingExecuteLn executeLn);
}