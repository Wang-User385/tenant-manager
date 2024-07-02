package com.hand.hls.abs.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.abs.dto.HlsCusAbsContractFinish;
import com.hand.hls.abs.mapper.HlsCusAbsContractFinishMapper;
import com.hand.hls.abs.service.HlsCusAbsContractFinishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsContractFinishServiceImpl extends BaseServiceImpl<HlsCusAbsContractFinish> implements HlsCusAbsContractFinishService {

    @Autowired
    private HlsCusAbsContractFinishMapper hlsCusAbsContractFinishMapper;
//    @Autowired
//    private HlsCusAbsPropertyContractMapper hlsCusAbsPropertyContractMapper;
//    @Autowired
//    private HlsCusAbsPropertyContractService hlsCusAbsPropertyContractService;
//    @Autowired
//    private HlsCusAbsProductService hlsCusAbsProductService;

    @Override
    public List<HlsCusAbsContractFinish> queryAbsContractFinish(IRequest request, HlsCusAbsContractFinish hlsCusAbsContractFinish, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusAbsContractFinish> list = hlsCusAbsContractFinishMapper.queryAbsContractFinish(hlsCusAbsContractFinish);
        return list;
    }
}
