package com.hand.hls.partner.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.partner.service.IAlipayService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.web.logs.dto.HlsWsRequests;
import com.hand.hls.web.logs.service.IHlsWsRequestsService;
import hls.core.utils.exception.HlsCusException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DisallowConcurrentExecution
@Transactional(rollbackFor = Exception.class)
public class AlipayJob extends AbstractJobWithIRequest{
    private static final Logger logger = LoggerFactory.getLogger(AlipayJob.class);
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private IAlipayService iAlipayService;
    @Autowired
    private IHlsWsRequestsService logService;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest)  {
        try {
            RequestHelper.setCurrentRequest(iRequest);

            List<HlsCusPrjProject> projectList = prjProjectMapper.selectProjectForAlipaySignQuery();
            for (HlsCusPrjProject project : projectList) {
                iAlipayService.signQuery(project.getProjectId());
            }
        } catch (HlsCusException e) {
            if ("20000".equals(e.getCode()) || "系统繁忙".equals(e.getMessage())) {
                logger.warn("Ignored HlsCusException during alipayJob signQuery execution: {}", e.getMessage());
            } else {
                logger.error("Unexpected HlsCusException during alipayJob signQuery execution", e);

            }
        }


    }
}
