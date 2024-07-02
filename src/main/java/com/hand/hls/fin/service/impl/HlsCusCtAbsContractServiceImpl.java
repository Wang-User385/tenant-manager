package com.hand.hls.fin.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusCtAbsContract;
import com.hand.hls.fin.service.HlsCusCtAbsContractService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
public class HlsCusCtAbsContractServiceImpl extends BaseServiceImpl<HlsCusCtAbsContract> implements HlsCusCtAbsContractService {


    @Override
    public List<HlsCusCtAbsContract> selectLonAbsContract(IRequest request, HlsCusCtAbsContract hlsCusCtAbsContract, int page, int pageSize) {
        return null;
    }
}
