package com.hand.hls.fp.job;

import com.hand.hap.core.IRequest;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.mapper.JcFundFillingMapper;
import com.hand.hls.rpt.job.AbstractJobWithIRequest;
import com.hand.hls.vat.dto.AcrInvoiceHdMid;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.mapper.AcrInvoiceHdMidMapper;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import hls.core.sys.event.service.SysEventService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description:发票传回
 * @author: congweijing
 * @date: 2021-08-18 9:06
 */
@Component
public class HlsCusFillingJob extends AbstractJobWithIRequest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Exception exception = null;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private static final String WEEK_THREE = "3";
    @Autowired
    private static final String DAY = "15";
    @Autowired
    private static final String DAY_END = "1";
    @Autowired
    private static final String MON1 = "3";
    @Autowired
    private static final String MON2 = "6";
    @Autowired
    private static final String MON3 = "9";
    @Autowired
    private static final String MON4 = "12";
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private JcFundFillingMapper fundFillingMapper;

    @Override
    public void safeExecuteWithIRequest(JobExecutionContext jobExecutionContext, IRequest iRequest) throws Exception {
        try {
            //资金计划填报  年计划每天12月1日提醒项目经理于12月15日前进行填报
            //              每季季末当月15日提箱项目经理于20日前季度进行填报
            //              每月15日提醒项目经理于2o日前进行月计划填报
            //              每周周一提醒项目经理于周三前进行填报
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String week = sdf.format(date);
            String day = String.format("%td", date);
            String mon = String.format("%tm", date);
            //获取所有项目经理
            List<JcFundFilling> fillingList = fundFillingMapper.queryUserInfo();
            if (fillingList.size() > 0) {
                for (JcFundFilling fundFilling : fillingList) {
                    //每周三
                    if (week.equalsIgnoreCase(WEEK_THREE)) {
                        Map<String, Object> evenParams = new HashMap();
                        String str = "您好：请于周三前进行下周周资金计划填报！";
                        evenParams.put("message", str);
                        evenParams.put("noticeTitle", "合同保险到期");
                        evenParams.put("url", "");
                        evenParams.put("level", 1L);
                        evenParams.put("noticeType", "NOTICE");
                        evenParams.put("allocation_id", fundFilling.getAllocationId());
                        evenParams.put("eventUserId", fundFilling.getUserId());
                        sysEventService.eventSave(iRequest, fundFilling.getUserId(), "JC_FUND_FILLING", "JC_FUND_FILLING", "FILLING", "JC_FUND_FILLING", "P2D", evenParams);
                    }
                    //每月十五号
                    if (day.equalsIgnoreCase(DAY)) {
                        Map<String, Object> evenParams1 = new HashMap();
                        String str1 = "您好：请于本月20日前进行下个月月资金计划填报！";
                        evenParams1.put("message", str1);
                        evenParams1.put("noticeTitle", "合同保险到期");
                        evenParams1.put("url", "");
                        evenParams1.put("level", 1L);
                        evenParams1.put("noticeType", "NOTICE");
                        evenParams1.put("allocation_id", fundFilling.getAllocationId());
                        evenParams1.put("eventUserId", fundFilling.getUserId());
                        sysEventService.eventSave(iRequest, fundFilling.getUserId(), "JC_FUND_FILLING", "JC_FUND_FILLING", "FILLING", "JC_FUND_FILLING", "P2D", evenParams1);
                        //每季度十五号
                        if (mon.equalsIgnoreCase(MON1) || mon.equalsIgnoreCase(MON2) || mon.equalsIgnoreCase(MON3) || mon.equalsIgnoreCase(MON4)) {
                            Map<String, Object> evenParams2 = new HashMap();
                            String str2 = "您好：请于本季度20日前进行下季度季资金计划填报！";
                            evenParams2.put("message", str2);
                            evenParams2.put("noticeTitle", "合同保险到期");
                            evenParams2.put("url", "");
                            evenParams2.put("level", 1L);
                            evenParams2.put("noticeType", "NOTICE");
                            evenParams2.put("allocation_id", fundFilling.getAllocationId());
                            evenParams2.put("eventUserId", fundFilling.getUserId());
                            sysEventService.eventSave(iRequest, fundFilling.getUserId(), "JC_FUND_FILLING", "JC_FUND_FILLING", "FILLING", "JC_FUND_FILLING", "P2D", evenParams2);
                        }
                    }
                    //每年十二月一号
                    if (mon.equalsIgnoreCase(MON4)) {
                        if (day.equalsIgnoreCase(DAY_END)) {
                            Map<String, Object> evenParams3 = new HashMap();
                            String str3 = "您好：请于本年度15日前进行下一年年资金计划填报！";
                            evenParams3.put("message", str3);
                            evenParams3.put("noticeTitle", "合同保险到期");
                            evenParams3.put("url", "");
                            evenParams3.put("level", 1L);
                            evenParams3.put("noticeType", "NOTICE");
                            evenParams3.put("allocation_id", fundFilling.getAllocationId());
                            evenParams3.put("eventUserId", fundFilling.getUserId());
                            sysEventService.eventSave(iRequest, fundFilling.getUserId(), "JC_FUND_FILLING", "JC_FUND_FILLING", "FILLING", "JC_FUND_FILLING", "P2D", evenParams3);
                        }
                    }
                }
            }

            setExecutionSummary("success");
        } catch (Exception e) {
            exception = e;
            logger.info("===================================================================================");
            logger.info("资金计划填报失败:" + e);
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
