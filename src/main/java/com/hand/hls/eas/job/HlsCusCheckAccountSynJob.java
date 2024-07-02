package com.hand.hls.eas.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description
 *  财务对账同步接口
 * @author wangchao 2020/05/15 5:56 PM
 */
public class HlsCusCheckAccountSynJob extends AbstractJob {



    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = (IRequest) jobExecutionContext.getMergedJobDataMap().get("requestContext");

        Long outboundId=null;
        hlsCusEasLoginService.easCheckAccountSyn(iRequest,outboundId);

        }


}
