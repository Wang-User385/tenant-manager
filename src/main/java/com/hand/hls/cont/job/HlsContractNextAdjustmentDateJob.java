package com.hand.hls.cont.job;

import com.hand.hap.activiti.components.HlsCusConFloatingRateReqSubmitServiceTask;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReqLn;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.cont.service.impl.ConFloatingRateReqLnServiceImpl;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.IContractFinanceIncomeService;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class HlsContractNextAdjustmentDateJob extends AbstractJob {

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;


    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {

        IRequest requestCtx = RequestHelper.getCurrentRequest(true);
        //查询出 所有 下一调息日+30日后还未进行调息的合同
        List<HlsCusConContract> hlsCusConContractList = hlsCusPrjQuotationMapper.queryContractRateReq();
        for(HlsCusConContract hlsCusConContract: hlsCusConContractList){
            //下次调息日期 = 本次调息日 + 1年
            Calendar cal = Calendar.getInstance();
            cal.setTime(hlsCusConContract.getNextAdjustmentDate());
            cal.add(Calendar.YEAR,1);
            Date nextAdjustmentDate = cal.getTime();
            hlsCusConContract.setNextAdjustmentDate(nextAdjustmentDate);

            HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
            cusPrjQuotation.setQuotationId(hlsCusConContract.getQuotationId());
            HlsCusPrjQuotation prjQuotationOld =  hlsCusPrjQuotationMapper.selectByPrimaryKey(cusPrjQuotation);
            prjQuotationOld.setNextAdjustmentDate(nextAdjustmentDate);
            hlsCusPrjQuotationService.updateByPrimaryKeySelective(requestCtx , prjQuotationOld);
        }
    }
}
