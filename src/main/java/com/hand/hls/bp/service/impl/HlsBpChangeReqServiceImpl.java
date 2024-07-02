package com.hand.hls.bp.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsBpChangeReqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.bp.dto.HlsBpChangeReq;
import com.hand.hls.bp.service.HlsBpChangeReqService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsBpChangeReqServiceImpl extends BaseServiceImpl<HlsBpChangeReq> implements HlsBpChangeReqService{

    @Autowired
    private HlsBpChangeReqMapper mapper;
    @Override
    public List<HlsBpChangeReq> selectAll(IRequest iRequest, HlsBpChangeReq hlsBpChangeReq, int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return mapper.queryAll(hlsBpChangeReq);
    }
}