package com.hand.hls.avs.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.avs.dto.LitigationManagement;

import java.util.List;

public interface LitigationManagementMapper extends Mapper<LitigationManagement>{

    List<LitigationManagement> queryLitigationManagement(LitigationManagement litigationManagement);

    List<String> getAllocationIds();
}