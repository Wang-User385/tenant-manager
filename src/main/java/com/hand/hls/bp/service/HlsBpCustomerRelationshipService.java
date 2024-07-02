package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpCustomerRelationship;

import java.util.List;

public interface HlsBpCustomerRelationshipService extends IBaseService<HlsBpCustomerRelationship>, ProxySelf<HlsBpCustomerRelationshipService>{

    List<HlsBpCustomerRelationship> selectAll(IRequest requestContext,HlsBpCustomerRelationship hlsBpCustomerRelationship, int page, int pageSize);
}