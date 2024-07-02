package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.mapper.AstFcEstimateMapper;
import com.hand.hls.ast.service.IAstFcEstimateService;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: quzongkai
 * @date: 2020/6/12
 * @description: 项目审查工作流一键操作
 */
@Component
public class AstFctimateBApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private AstFcEstimateMapper astFcEstimateMapper;

    @Autowired
    private IAstFcEstimateService astFcEstimateService;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        AstFcEstimate astFcEstimate = astFcEstimateMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(astFcEstimate);

        if (PASS.equals(hlsCusProcess.getType())) {
            astFcEstimate.setNowCountStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            astFcEstimate.setNowCountStatus("REJECTED");

        }
        astFcEstimateService.updateByPrimaryKeySelective(iRequest, astFcEstimate);
    }
}
