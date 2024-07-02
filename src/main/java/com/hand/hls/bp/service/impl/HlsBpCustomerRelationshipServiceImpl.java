package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpCustomerRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpCustomerRelationship;
import com.hand.hls.bp.service.HlsBpCustomerRelationshipService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpCustomerRelationshipServiceImpl extends BaseServiceImpl<HlsBpCustomerRelationship> implements HlsBpCustomerRelationshipService{

    @Autowired
    private HlsBpCustomerRelationshipMapper mapper;

    @Override
    public List<HlsBpCustomerRelationship> selectAll(IRequest requestContext, HlsBpCustomerRelationship hlsBpCustomerRelationship, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryBpCustomerRelationship(hlsBpCustomerRelationship);
    }

}