package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.GovernmentFinancPlatform;

import java.util.List;

public interface GovernmentFinancPlatformMapper extends Mapper<GovernmentFinancPlatform>{
    List<GovernmentFinancPlatform> queryAll(GovernmentFinancPlatform dto);
}