package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.FndRiskRatioSet;

import java.util.List;

public interface FndRiskRatioSetMapper extends Mapper<FndRiskRatioSet>{
    List<FndRiskRatioSet> query(FndRiskRatioSet var1);
}