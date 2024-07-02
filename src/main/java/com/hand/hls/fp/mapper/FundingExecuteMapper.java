package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.FundingExecute;
import com.hand.hls.fp.dto.FundingExecuteLn;

import java.util.List;

public interface FundingExecuteMapper extends Mapper<FundingExecute> {
    List<FundingExecute> queryAll(FundingExecute execute);

    List<FundingExecute> queryAllByUnit(FundingExecute execute);

    List<FundingExecute> queryAllConfirm(FundingExecute execute);

    List<FundingExecute> queryByfillingId(FundingExecute execute);
}