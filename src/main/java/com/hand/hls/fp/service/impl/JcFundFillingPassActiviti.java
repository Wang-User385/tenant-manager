package com.hand.hls.fp.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.mapper.JcFundFillingMapper;
import com.hand.hls.fp.service.JcFundFillingService;
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
public class JcFundFillingPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private JcFundFillingMapper mapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private JcFundFillingService service;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";
    private final static String APPROVING = "APPROVING";
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        JcFundFilling filling = mapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(filling);

        if(APPROVING.equalsIgnoreCase(filling.getPlanStatus())){
            if (PASS.equals(hlsCusProcess.getType())) {
                filling.setPlanStatus("APPROVED");
            } else if (REJECT.equals(hlsCusProcess.getType())) {
                filling.setPlanStatus("REJECTED");

            }
            service.updateByPrimaryKeySelective(iRequest, filling);
        }

    }
}
