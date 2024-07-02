package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsCusHlsBpAffiEnterprises;

import java.util.List;

public interface HlsCusHlsBpAffiEnterprisesMapper extends Mapper<HlsCusHlsBpAffiEnterprises> {
    List<HlsCusHlsBpAffiEnterprises> queryAll(HlsCusHlsBpAffiEnterprises dto);
}