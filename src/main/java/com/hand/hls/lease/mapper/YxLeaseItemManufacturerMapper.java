package com.hand.hls.lease.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.lease.dto.YxLeaseItemClassify;
import com.hand.hls.lease.dto.YxLeaseItemManufacturer;

import java.util.List;

public interface YxLeaseItemManufacturerMapper extends Mapper<YxLeaseItemManufacturer> {
    List<YxLeaseItemManufacturer> yxLeaseItemManufacturerQuery(YxLeaseItemManufacturer yxLeaseItemManufacturer);

}