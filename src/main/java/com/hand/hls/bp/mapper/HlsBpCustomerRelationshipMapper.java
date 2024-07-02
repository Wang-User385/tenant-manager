package com.hand.hls.bp.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.bp.dto.HlsBpCustomerRelationship;

import java.util.List;

public interface HlsBpCustomerRelationshipMapper extends Mapper<HlsBpCustomerRelationship>{

    List<HlsBpCustomerRelationship> queryBpCustomerRelationship(HlsBpCustomerRelationship hlsBpCustomerRelationship);
}