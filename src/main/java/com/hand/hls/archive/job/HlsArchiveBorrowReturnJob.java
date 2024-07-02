package com.hand.hls.archive.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.archive.dto.HlsCusArchiveBorrow;
import com.hand.hls.archive.mapper.HlsCusArchiveBorrowMapper;
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
 * @Description: 档案借阅归还通知
 * @author: congweijing
 * @date: 2021/8/9 11:05
 */
@Component
public class HlsArchiveBorrowReturnJob extends AbstractJobWithIRequest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private SysUserAllocationMapper sysUserAllocationMapper;
    @Autowired
    private HlsCusArchiveBorrowMapper hlsCusArchiveBorrowMapper;
    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) {
        try {
            //根据“预计归还日期”提前1天提醒借阅人
            HlsCusArchiveBorrow hlsCusArchiveBorrow = new HlsCusArchiveBorrow();
            List<HlsCusArchiveBorrow> hlsCusArchiveBorrows = hlsCusArchiveBorrowMapper.queryArchiveBorrowEndNotice(hlsCusArchiveBorrow);
            if (hlsCusArchiveBorrows.size() > 0) {
                for (HlsCusArchiveBorrow archiveBorrow : hlsCusArchiveBorrows) {
                    SysUserAllocation sysUserAllocation = new SysUserAllocation();
                    sysUserAllocation.setUserId(archiveBorrow.getCreatedBy());
                    List<SysUserAllocation> sysUserAllocations = sysUserAllocationMapper.select(sysUserAllocation);
                    for (SysUserAllocation people : sysUserAllocations) {
                        Map<String, Object> evenParams = new HashMap();
                        evenParams.put("message", "需归还"+ archiveBorrow.getProjectNumber() +"项目名称"+ archiveBorrow.getProjectName()+ "的档案资料！");
                        evenParams.put("noticeTitle", "档案归还通知");
                        evenParams.put("url", "");
                        evenParams.put("level", 1L);
                        evenParams.put("noticeType", "NOTICE");
                        evenParams.put("allocation_id", people.getAllocationId());

                        String documentCategory = "ARCHIVE_RETURN";
                        String documentType = "ARCHIVE_RETURN";
                        iRequest.setUserId(people.getUserId());
                        this.sysEventService.createEvent(iRequest, archiveBorrow.getProjectId(), documentCategory, documentType, evenParams);
                    }
                }

            }
            logger.info("===================================================================================");
            logger.info("系统已执行档案归还通知，当前执行时间为：" + new Date().toString());
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
