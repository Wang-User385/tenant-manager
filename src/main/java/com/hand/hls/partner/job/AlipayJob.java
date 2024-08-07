package com.hand.hls.partner.job;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.partner.service.IAlipayService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
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

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) {
        RequestHelper.setCurrentRequest(iRequest);

        List<HlsCusPrjProject> projectList = prjProjectMapper.selectProjectForAlipaySignQuery();
        for(HlsCusPrjProject project : projectList){
            iAlipayService.signQuery(project.getProjectId());
        }
    }
}
