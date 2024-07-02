package com.hand.hls.fp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fp.dto.FundingExecute;
import com.hand.hls.fp.service.JcFundFillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.hand.hls.fp.dto.FundingExecuteLn;
import com.hand.hls.fp.service.FundingExecuteLnService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FundingExecuteLnServiceImpl extends BaseServiceImpl<FundingExecuteLn> implements FundingExecuteLnService {
    @Autowired
    private JcFundFillingService fundFillingService;
    private static final String INTEGRATED_MGT_DEPT = "INTEGRATED_MGT_DEPT";

    @Override
    public List<FundingExecute> executeAmount(IRequest request, FundingExecute execute) {
        List<FundingExecute> executeList = new ArrayList<>();
        fundFillingService.initExecut(request, execute, execute.getSummaryFlag());
        executeList.add(execute);
        return executeList;
    }
}