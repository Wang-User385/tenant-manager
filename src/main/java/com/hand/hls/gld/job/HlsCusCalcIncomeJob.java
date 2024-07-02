package com.hand.hls.gld.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.vat.dto.AcrInvoiceHdMid;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.mapper.AcrInvoiceHdMidMapper;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:发票传回
 * @author: congweijing
 * @date: 2021-08-18 9:06
 */
@Component
public class HlsCusCalcIncomeJob extends AbstractJobWithIRequest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;

    @Autowired
    private AcrInvoiceHdMidMapper acrInvoiceHdMidMapper;
    @Autowired
    private HlsCusConContractMapper contractMapper;
    @Autowired
    private IGldContractCashflowService gldContractCashflowService;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) throws Exception {
        try {
            //查询合同
            String contractNumber = jobExecutionContext.getMergedJobDataMap().getString("contractNumber");
            HlsCusConContract cusConContract = new HlsCusConContract();
            cusConContract.setContractNumber(contractNumber);
            List<HlsCusConContract> conContractList = contractMapper.queryConIncome(cusConContract);
            if (conContractList.size() > 0) {
                //执行分摊逻辑
                gldContractCashflowService.clacFinanceIncome(iRequest, conContractList.get(0).getContractId(), conContractList.get(0).getVatRate(), conContractList.get(0).getXirr());
            }
            setExecutionSummary("success");
        } catch (Exception e) {
            exception = e;
            logger.info("===================================================================================");
            logger.info("分摊重算失败:" + e);
            logger.info("===================================================================================");
            this.setExecutionSummary(this.exception.getClass().getName() + ":" + this.exception.getMessage());
        }
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        //任务发生异常时候进行的动作
        //false 挂起JOB等待处理
        //true 继续执行
        return false;
    }
}
