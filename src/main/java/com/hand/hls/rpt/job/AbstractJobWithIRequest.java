//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.rpt.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import org.quartz.JobExecutionContext;

public abstract class AbstractJobWithIRequest extends AbstractJob {
    public AbstractJobWithIRequest() {
    }

    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest request = (IRequest)jobExecutionContext.getMergedJobDataMap().get("requestContext");
        this.safeExecuteWithIRequest(jobExecutionContext, request);
    }

    public abstract void safeExecuteWithIRequest(JobExecutionContext var1, IRequest var2)throws Exception;
}
