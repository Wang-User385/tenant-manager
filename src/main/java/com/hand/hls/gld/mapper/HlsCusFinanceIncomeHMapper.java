package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.HlsCusFinanceIncomeH;

import java.util.List;

public interface HlsCusFinanceIncomeHMapper extends Mapper<HlsCusFinanceIncomeH>{

    Long selectVersionCount(HlsCusFinanceIncomeH hlsCusFinanceIncomeH);

    HlsCusFinanceIncomeH selectRecordByVersionId(HlsCusFinanceIncomeH hlsCusFinanceIncomeH);

    List<HlsCusFinanceIncomeH> selectHistoryBySourceFinanceIncomeId(HlsCusFinanceIncomeH hlsCusFinanceIncomeH);

    List<HlsCusFinanceIncomeH> selectHistoryByBatch(HlsCusFinanceIncomeH hlsCusFinanceIncomeH);

    List<HlsCusFinanceIncomeH> selectHistoryByFinanceIncomeId(HlsCusFinanceIncomeH hlsCusFinanceIncomeH);

}