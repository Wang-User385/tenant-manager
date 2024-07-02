package com.hand.hls.vat.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceRelationship;

import java.util.Map;

public interface AcrInvoiceRelationshipMapper<T extends HlsCusAcrInvoiceRelationship> extends Mapper<HlsCusAcrInvoiceRelationship> {
    int insertRelationship(Map var1);
}
