package com.hand.hls.vat.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.interfacePlatform.utils.InvoiceBaseUtils;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import com.hand.hls.vat.service.IAcrInvoiceHdService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:发票URL查询以及附件下载
 * @author: zhangdan
 * @date: 2022-10-14
 */
@Component
public class HlsAcrInvoiceQueryUrlItfcJob extends AbstractJobWithIRequest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;

    @Autowired
    private IAcrInvoiceHdService invoiceHdService;
    @Autowired
    private HlsCusAcrInvoiceHdMapper acrInvoiceHdMapper;
    @Autowired
    private InvoiceBaseUtils invoiceBaseUtils;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext context, IRequest requestCtx) throws Exception {
        try {
            Object invoiceDocumentNumber = context.getMergedJobDataMap().get("invoice_document_number");

            HlsCusAcrInvoiceHd acrInvoiceHd = new HlsCusAcrInvoiceHd();
            if (null != invoiceDocumentNumber) {
                acrInvoiceHd.setDocumentNumber(invoiceDocumentNumber.toString());
                List<HlsCusAcrInvoiceHd> list = acrInvoiceHdMapper.queryAcrInvoiceHdDetailNew(acrInvoiceHd);
                if (list.size() > 0) {
                    acrInvoiceHd = list.get(0);
                }
            }
            invoiceBaseUtils.queryInvoiceUrlItfc(requestCtx, acrInvoiceHd);
            setExecutionSummary("success");
        } catch (Exception e) {
            exception = e;
            logger.info("===================================================================================");
            logger.info("电子发票下载失败:" + e);
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
