package com.hand.hls.cont.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.mapper.SysUserAllocationMapper;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description: 合同结束通知
 * @author: congweijing
 * @date: 2021/5/20 11:05
 */
@Component
public class HlsContractTerminateJob extends AbstractJobWithIRequest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) {
        try {
            //查询已经结束的合同
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            List<HlsCusPrjProject> terminateProjects = hlsCusPrjProjectMapper.queryContractTerminateGrid(hlsCusPrjProject);
            if (terminateProjects.size() > 0) {
                for (HlsCusPrjProject terminateProject : terminateProjects) {
                    Long projectId = terminateProject.getProjectId();
                    HlsCusPrjProject hlsCusPrjProjectp = hlsCusPrjProjectMapper.selectByPrimaryKey(projectId);
                    //限制只能发一次结束的通知
                    if(hlsCusPrjProjectp.getTerminateNoticeTimes() == null){
                        SysUserAllocation sysUserAllocation = new SysUserAllocation();
                        sysUserAllocation.setUserId(terminateProject.getHostProjectManager());
                        List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.select(sysUserAllocation);
                        for (SysUserAllocation people : sysUserAllocations) {
                            Map<String, Object> evenParams = new HashMap();
                            evenParams.put("message", "编号为"+ terminateProject.getContractNumber() + "的合同已经结束，请注意查看！");
                            evenParams.put("noticeTitle", "合同结束通知");
                            evenParams.put("url", "");
                            evenParams.put("level", 1L);
                            evenParams.put("noticeType", "NOTICE");
                            evenParams.put("allocation_id", people.getAllocationId());

                            String documentCategory = "CONTRACT_TERMINATE";
                            String documentType = "CONTRACT_TERMINATE";
                            iRequest.setUserId(people.getUserId());
                            this.sysEventService.createEvent(iRequest, terminateProject.getProjectId(), documentCategory, documentType, evenParams);

                        }
                        hlsCusPrjProjectp.setTerminateNoticeTimes(1L);
                        hlsCusPrjProjectMapper.updateByPrimaryKey(hlsCusPrjProjectp);
                    }

                }

            }
            logger.info("===================================================================================");
            logger.info("系统已执行合同结束通知，当前执行时间为：" + new Date().toString());
            logger.info("===================================================================================");
            setExecutionSummary("success");
        } catch (Exception e) {
            exception = e;
            logger.error("InsuranceOverdueNoticeJob execute error 通知任务执行时出现问题 : ", e.getMessage());
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
