package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
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
public class RiskApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private HlsCusIRiskWarningService warningService;
    @Autowired
    private HlsCusRiskWarningMapper warningMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        HlsCusRiskWarning hlsCusRiskWarning = warningMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(hlsCusRiskWarning);

        if (PASS.equals(hlsCusProcess.getType())) {
            hlsCusRiskWarning.setStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusRiskWarning.setStatus("REJECTED");

        }
        warningService.updateByPrimaryKeySelective(iRequest, hlsCusRiskWarning);
    }
}
