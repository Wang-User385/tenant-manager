package com.hand.hls.pam.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.pam.dto.HlsLeaseBuildDeliverInfo;

import java.util.List;

public interface HlsLeaseBuildDeliverInfoMapper extends Mapper<HlsLeaseBuildDeliverInfo>{
    List<HlsLeaseBuildDeliverInfo> selectBuildDeliverInfo();
}