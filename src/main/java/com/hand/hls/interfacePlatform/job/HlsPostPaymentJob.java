package com.hand.hls.interfacePlatform.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchLnService;
import com.hand.hls.fin.service.IHlsCusLonConRepaymentBatchService;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HlsPostPaymentJob extends AbstractJob {

    @Autowired
    private FinanceBaseUtils financeBaseUtils;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;

    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest requestCtx = RequestHelper.newEmptyRequest();

        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPostItfcFlag("N");
        cshPaymentReqHd.setPaymentReqStatus("APPROVED");
        List<HlsCusCshPaymentReqHd> cshPaymentReqHdList=cshPaymentReqHdService.select(requestCtx,cshPaymentReqHd,1,999);
        for(HlsCusCshPaymentReqHd paymentHd:cshPaymentReqHdList){
            financeBaseUtils.postPaymentItfc(requestCtx,paymentHd.getPaymentReqId());
        }
    }
}
