package com.hand.hls.hn.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.hn.dto.CheckPlanConContract;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.SelectAllConContractMapper;
import com.hand.hls.hn.service.ISelectAllConContractService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SelectAllConContractServiceImpl extends BaseServiceImpl<CheckPlanConContract> implements ISelectAllConContractService {

    @Autowired
    private SelectAllConContractMapper selectAllConContractMapper;

    @Override
    public List<CheckPlanConContract> queryAllApproveContract(CheckPlanConContract checkPlanConContract) {
        return selectAllConContractMapper.queryAllApproveContract(checkPlanConContract);
    }
}