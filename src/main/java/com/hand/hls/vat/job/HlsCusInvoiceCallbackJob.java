package com.hand.hls.vat.job;

import com.hand.hap.core.IRequest;
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
public class HlsCusInvoiceCallbackJob extends AbstractJobWithIRequest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;

    @Autowired
    private AcrInvoiceHdMidMapper acrInvoiceHdMidMapper;
    @Autowired
    private HlsCusAcrInvoiceHdMapper hlsCusAcrInvoiceHdMapper;
    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest)  throws Exception {
        try {
            //查询已经开票但是没有传回发票代码的数据
            List<AcrInvoiceHdMid> acrInvoiceHdMids = acrInvoiceHdMidMapper.queryAcrInvoiceHdMidForUpdate();
            if(!acrInvoiceHdMids.isEmpty()){
                for(AcrInvoiceHdMid acrInvoiceHdMid : acrInvoiceHdMids){
                    HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd = new HlsCusAcrInvoiceHd();
                    hlsCusAcrInvoiceHd.setInvoiceHdId(acrInvoiceHdMid.getInvoiceHdId());
                    hlsCusAcrInvoiceHd.setLastUpdateDate(new Date());
                    hlsCusAcrInvoiceHd.setInvoiceCode(acrInvoiceHdMid.getInvoiceCode());
                    hlsCusAcrInvoiceHd.setInvoiceNumber(acrInvoiceHdMid.getInvoiceNumber());
                    hlsCusAcrInvoiceHd.setInvoiceStatus("IMPORTED");
                    hlsCusAcrInvoiceHdMapper.updateByPrimaryKeySelective(hlsCusAcrInvoiceHd);
                    acrInvoiceHdMid.setInvoiceStatus("IMPORTED");
                    acrInvoiceHdMidMapper.updateByPrimaryKeySelective(acrInvoiceHdMid);
                    logger.info("===================================================================================");
                    logger.info("更新发票代码，发票号码，发票状态成功:" + hlsCusAcrInvoiceHd.getDocumentNumber());
                    logger.info("===================================================================================");
                }
            }
            setExecutionSummary("success");
        } catch (Exception e) {
            exception = e;
            logger.info("===================================================================================");
            logger.info("更新发票代码，发票号码，发票状态失败:" + e);
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
