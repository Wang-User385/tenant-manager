package com.hand.hls.fin.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.fin.dto.HlsCusLonContractWithdrawPlan;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawPlanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractWithdrawPlanServiceImpl extends BaseServiceImpl<HlsCusLonContractWithdrawPlan> implements HlsCusLonContractWithdrawPlanService {


    @Override
    public void batchDeleteWithdrawPlan(IRequest iRequest, List<HlsCusLonContractWithdrawPlan> dto) {

    }

    @Override
    public void calcRepaymentPlan(IRequest iRequest, HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlan) {

    }

    @Override
    public void updateAllPrincipalOutStd(IRequest iRequest, HlsCusLonContractWithdrawPlan hlsCusLonContractWithdrawPlan) {

    }
}
