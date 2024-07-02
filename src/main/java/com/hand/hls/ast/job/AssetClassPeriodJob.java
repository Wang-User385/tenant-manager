package com.hand.hls.ast.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.ast.service.IAssetClassHeadService;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@DisallowConcurrentExecution
@Transactional(rollbackFor = Exception.class)
public class AssetClassPeriodJob extends AbstractJobWithIRequest {

    @Autowired
    private IAssetClassHeadService assetClassHeadService;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) {
        //定期自动生成资产分类数据
        assetClassHeadService.periodCreateAssetClassInfo(iRequest);
    }
}
