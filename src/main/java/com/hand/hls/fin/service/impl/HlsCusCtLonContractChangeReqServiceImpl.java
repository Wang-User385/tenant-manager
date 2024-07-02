package com.hand.hls.fin.service.impl;


import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtLonContractChangeReq;
import com.hand.hls.fin.service.HlsCusCtLonContractChangeReqService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCtLonContractChangeReqServiceImpl extends BaseServiceImpl<HlsCusCtLonContractChangeReq> implements HlsCusCtLonContractChangeReqService {


    @Override
    public List<HlsCusCtLonContractChangeReq> selectAllChangeReq(HlsCusCtLonContractChangeReq lonContractChangeReq, int page, int pageSize) {
        return null;
    }

    @Override
    public HlsCusCtLonContractChangeReq selectLonConChangeReqChart(IRequest requestContext) {
        return null;
    }

    @Override
    public void updateChangeReq(HlsCusCtLonContractChangeReq hlsCusCtLonContractChangeReq) {

    }
}
