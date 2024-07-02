package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.GENER.mapper.HlsGeneralIssueMapper;
import com.hand.hls.GENER.service.IHlsGeneralIssueService;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: quzongkai
 * @date: 2020/6/12
 * @description: 风险预警工作流一键操作
 */
@Component
public class HlsCusIssueGeneraalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private IHlsGeneralIssueService hlsgeneralissueservice;
    @Autowired
    private HlsGeneralIssueMapper hlsgeneralissuemapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        HlsGeneralIssue report = hlsgeneralissuemapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(report);

        if (PASS.equals(hlsCusProcess.getType())) {
            report.setStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            report.setStatus("REJECTED");

        }
        hlsgeneralissueservice.updateByPrimaryKeySelective(iRequest, report);
    }
}
