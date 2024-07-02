package com.hand.hls.hn.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.hn.dto.PrjCheckProjectApproveSchedule;
import com.hand.hls.hn.dto.PrjCheckProjectSchedule;

import java.util.List;

public interface PrjCheckProjectApproveScheduleMapper extends Mapper<PrjCheckProjectApproveSchedule>{
    List<PrjCheckProjectApproveSchedule> queryList(PrjCheckProjectApproveSchedule prjCheckProjectApproveSchedule);
}