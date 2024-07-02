package com.hand.hls.vat.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.vat.dto.HandoverApplication;

public interface HandoverApplicationMapper extends Mapper<HandoverApplication>{
    int updateHandoverStatus(HandoverApplication handoverapplication);
    int updateHandoverStatusReview(HandoverApplication handoverapplication);
    int updateHandoverRejectStatusReview(HandoverApplication handoverapplication);

}