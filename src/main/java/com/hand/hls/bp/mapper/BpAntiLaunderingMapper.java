package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.BpAntiLaundering;

import java.util.List;

public interface BpAntiLaunderingMapper extends Mapper<BpAntiLaundering>{
    List<BpAntiLaundering> queryAll(BpAntiLaundering bpAntiLaundering);
}