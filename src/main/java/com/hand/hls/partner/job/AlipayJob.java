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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DisallowConcurrentExecution
@Transactional(rollbackFor = Exception.class)
public class AlipayJob extends AbstractJobWithIRequest{

    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    private IAlipayService iAlipayService;
    @Autowired
    private IHlsWsRequestsService logService;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) throws HlsCusException {
        RequestHelper.setCurrentRequest(iRequest);

        List<HlsCusPrjProject> projectList = prjProjectMapper.selectProjectForAlipaySignQuery();
        for (HlsCusPrjProject project : projectList) {
            try {
                iAlipayService.signQuery(project.getProjectId());

            } catch (HlsCusException e) {
                if ("20000".equals(e.getCode()) || "系统繁忙".equals(e.getMessage())) {
                    // 特殊处理 "系统繁忙" 的情况
                    String message = "Alipay sign query failed due to system busy. Project ID: " + project.getProjectId();
                    HlsWsRequests hlsWsRequests = new HlsWsRequests();
                    hlsWsRequests.setResponseJson(message);
                    logService.interfaceSave(hlsWsRequests, iRequest);
                } else {
                    // 其他所有 HlsCusException 异常，记录错误信息
                    HlsWsRequests hlsWsRequests = new HlsWsRequests();
                    hlsWsRequests.setResponseJson("Alipay sign query failed due to system busy. Project ID: {}" + project.getProjectId());
                    logService.interfaceSave(hlsWsRequests, iRequest);
                    throw e;
                }
            }
        }
    }
}
