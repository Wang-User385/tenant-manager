package com.hand.hls.csh.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.csh.service.ICshDepositDeductJobService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 自动抵扣
 *
 */
@DisallowConcurrentExecution
public class CshDepositDeductJob extends AbstractJobWithIRequest {
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(CshDepositDeductJob.class);

    /**
     * 当前UUID
     */
    private static final String REQUEST_ID = "requestId";

    /**
     * 日结计算类型
     */
    public static final String CONTRACT_NUMBER = "contractNumber";

    @Autowired
    private ICshDepositDeductJobService cshDepositDeductJobService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) {
        String contractNumber  = jobExecutionContext.getMergedJobDataMap().getString(CONTRACT_NUMBER);
        logger.info("CshDepositDeductJob-start");
        try {
            if(StringUtils.isEmpty(MDC.get(REQUEST_ID))){
                MDC.put(REQUEST_ID, UUID.randomUUID().toString().replace("-", ""));
            }
            iRequest.setAttribute("authorityRuleFlag", "N");
            cshDepositDeductJobService.start(iRequest,contractNumber);
        } catch (Exception e) {
            logger.error("CshDepositDeductJob-error", e);
        }
        logger.info("CshDepositDeductJob-end");

    }
}
