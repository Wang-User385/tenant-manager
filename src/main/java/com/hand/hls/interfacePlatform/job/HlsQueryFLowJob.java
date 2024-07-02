package com.hand.hls.interfacePlatform.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HlsQueryFLowJob extends AbstractJob {
    private Logger logger = LoggerFactory.getLogger(HlsQueryFLowJob.class);
    @Autowired
    private FinanceBaseUtils financeBaseUtils;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) {
        IRequest requestCtx = RequestHelper.newEmptyRequest();

        //String ioCode = jobExecutionContext.getMergedJobDataMap().get("ioCode").toString();
        Object queryDate= jobExecutionContext.getMergedJobDataMap().get("queryDate");
        String queryDateStr="";
        if (null != queryDate){
            queryDateStr=queryDate.toString();
        }

        List<String> ioCodeList = new ArrayList<>();
        ioCodeList.add("AL001");
        ioCodeList.add("AL002");

        try {
            financeBaseUtils.queryFlowItfc(requestCtx,ioCodeList,queryDateStr);
        } catch (Exception e) {
            this.setExecutionSummary(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        //任务发生异常时候进行的动作
        //false 挂起JOB等待处理
        //true 继续执行
        return true;
    }
}
