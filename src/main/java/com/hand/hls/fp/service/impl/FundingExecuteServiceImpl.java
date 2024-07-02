package com.hand.hls.fp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.mapper.FundingExecuteMapper;
import com.hand.hls.fp.mapper.JcFundFillingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.FundingExecute;
import com.hand.hls.fp.service.FundingExecuteService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingExecuteServiceImpl extends BaseServiceImpl<FundingExecute> implements FundingExecuteService {
    @Autowired
    private FundingExecuteMapper executeMapper;
    @Override
    public List<FundingExecute> queryAllByUnit(IRequest request, FundingExecute execute, int page, int pageSize) {
        return executeMapper.queryAllByUnit(execute);
    }

}