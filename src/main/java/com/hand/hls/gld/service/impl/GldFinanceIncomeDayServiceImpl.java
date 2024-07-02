package com.hand.hls.gld.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.gld.dto.GldFinanceIncomeDay;
import com.hand.hls.gld.mapper.GldFinanceIncomeDayMapper;
import com.hand.hls.gld.service.IGldFinanceIncomeDayService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class GldFinanceIncomeDayServiceImpl extends BaseServiceImpl<GldFinanceIncomeDay> implements IGldFinanceIncomeDayService {

    @Autowired
    private GldFinanceIncomeDayMapper mapper;

    @Override
    public List<GldFinanceIncomeDay> reportQuery(IRequest iRequest, GldFinanceIncomeDay gldFinanceIncomeDay, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return mapper.reportQuery(gldFinanceIncomeDay);
    }

    @Override
    public List<GldFinanceIncomeDay> queryPreLeaseInterestByContractId(HlsCusPrjProject hlsCusPrjProject) {
        return mapper.queryPreLeaseInterestByContractId(hlsCusPrjProject);
    }
}