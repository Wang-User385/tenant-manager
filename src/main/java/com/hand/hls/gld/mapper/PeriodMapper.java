package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.gld.dto.Period;

import java.util.List;
import java.util.Map;

public interface PeriodMapper extends Mapper<Period> {

    List<Period> getPeriodBetween(Map map);
    /*查询当前打开的区间*/
    List<Period> periodQuery4Lov(Period period);

    List<Period> periodNameLov(Period period);
}