package com.hand.hls.fp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fp.dto.JcFundPlanSchedule;

import java.util.List;

public interface JcFundPlanScheduleMapper extends Mapper<JcFundPlanSchedule>{
    List<JcFundPlanSchedule> queryAll(JcFundPlanSchedule schedule);

}