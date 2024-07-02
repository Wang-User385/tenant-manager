package com.hand.hls.interfacePlatform.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class HlsQueryEbsCodeJob extends AbstractJob {

    @Autowired
    private FinanceBaseUtils financeBaseUtils;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest requestCtx = RequestHelper.newEmptyRequest();

        Object bpName= jobExecutionContext.getMergedJobDataMap().get("bpName");
        String bpNameStr="";
        HlsCusBpMaster master = new HlsCusBpMaster();
        if (null != bpName){
            bpNameStr=bpName.toString();
            master.setBpName(bpNameStr);
        }
        financeBaseUtils.shareVendorQueryItfc(requestCtx,master);
    }
}
