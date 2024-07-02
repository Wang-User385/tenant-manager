package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fp.controllers.JcFundingPlanController;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.dto.JcFundFillingLn;
import com.hand.hls.fp.dto.JcFundPlanSchedule;
import com.hand.hls.fp.mapper.JcFundFillingLnMapper;
import com.hand.hls.fp.mapper.JcFundFillingMapper;
import com.hand.hls.fp.mapper.JcFundPlanScheduleMapper;
import com.hand.hls.fp.service.JcFundFillingService;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportBpMapper;
import com.hand.hls.hls.mapper.HlsCusHlsReportAttachmentMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportBpService;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author : qzk
 * @date : 2020/4/11
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusJcFundFillingServiceTask implements JavaDelegate, IActivitiBean {

    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String APPROVING = "APPROVING";
    @Autowired
    JcFundFillingService service;
    @Autowired
    private JcFundFillingMapper mapper;
    @Autowired
    private JcFundPlanScheduleMapper scheduleMapper;
    @Autowired
    private JcFundFillingService fillingService;
    @Autowired
    private JcFundFillingLnMapper lnMapper;
    private static final String SUMMARY_FLAG = "N";

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void initSummary(IRequest requestCtx, JcFundFilling filling) {
//        List<JcFundPlanSchedule> scheduleList = scheduleMapper.queryAll();
        //wflFlag用于区别 工作流和功能
        filling.setWflFlag("Y");
        if ("YEAR".equalsIgnoreCase(filling.getFillType())) {
            fillingService.initSummaryYear(requestCtx, filling);
        }
        if ("QUARTER".equalsIgnoreCase(filling.getFillType())) {
            fillingService.initSummaryQuarter(requestCtx, filling);
        }
        if ("MON".equalsIgnoreCase(filling.getFillType())) {
            fillingService.initSummaryMon(requestCtx, filling);
        }
        if ("WEEK".equalsIgnoreCase(filling.getFillType())) {
            fillingService.initSummaryWeek(requestCtx, filling);
        }
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = delegateExecution.getVariable("approveResult").toString();
        String jcFundFilling = (String) delegateExecution.getVariable("jcFundFilling");
        JcFundFilling fundFilling = JSON.parseObject(jcFundFilling, JcFundFilling.class);
        JcFundFilling filling = mapper.selectByPrimaryKey(fundFilling);

        if (APPROVING.equalsIgnoreCase(filling.getPlanStatus())) {
            if (APPROVED.equalsIgnoreCase(result)) {
                flag = APPROVED;
            } else {
                flag = REJECTED;
            }
            filling.setPlanStatus(flag);
            service.updateByPrimaryKeySelective(requestCtx, filling);
            //生成报表汇总数据
            if (APPROVED.equalsIgnoreCase(flag) && !"YEAR".equalsIgnoreCase(filling.getFillType())) {
                //需要生产fp400 执行数据 季度 月 周
                fillingService.initExecutInit(requestCtx, filling,SUMMARY_FLAG);
                initSummary(requestCtx, filling);
            }
        }
    }
}
