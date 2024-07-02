package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.FndBaseRate;

import java.util.List;


public interface FndBaseRateMapper extends Mapper<FndBaseRate> {
    List<FndBaseRate> selectByBaseRateSet(String var1);

    List<FndBaseRate> selectByBaseRateType(String var1);

    List<FndBaseRate> selectLatestByBaseRateType(String var1);
}
