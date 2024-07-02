package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckProjectSchedule;
import com.hand.hls.hn.dto.PrjCheckProjectSituation;

import java.util.List;

public interface PrjCheckProjectScheduleMapper extends Mapper<PrjCheckProjectSchedule>{
    List<PrjCheckProjectSchedule> queryList(PrjCheckProjectSchedule prjCheckProjectSchedule);
}