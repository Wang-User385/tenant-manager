package com.hand.hls.partner.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.job.AbstractJob;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;



public class AssetNeedNoticeJob extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AssetNeedNoticeJob.class);

    @Autowired
    private IYLMessageNoticeService iylMessageNoticeService;

    private Exception exception = null;

    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {
        try {
        //调用消息推送接口，推送逾期超过30天未超过85天的现金流数据
        IRequest request = (IRequest) context.getMergedJobDataMap().get("requestContext");
        iylMessageNoticeService.assetNeedBuyback(null, request);

        //关单结果通知
        iylMessageNoticeService.orderClosedNotify(request);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            exception = e;
            throw e;
        }
        if (exception != null) {
            setExecutionSummary(exception.getClass().getName() + ":" + exception.getMessage());
        } else {
            setExecutionSummary("执行完成！");
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
