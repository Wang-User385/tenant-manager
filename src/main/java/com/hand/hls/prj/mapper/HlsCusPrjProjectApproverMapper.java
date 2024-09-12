package com.hand.hls.prj.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.prj.dto.HlsCusPrjProjectApprover;
import org.apache.ibatis.annotations.Param;

public interface HlsCusPrjProjectApproverMapper extends Mapper<HlsCusPrjProjectApprover>{
    HlsCusPrjProjectApprover queryProjectApprover(HlsCusPrjProjectApprover hlsCusPrjProjectApprover);

}