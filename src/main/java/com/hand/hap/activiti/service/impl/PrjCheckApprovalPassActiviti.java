package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
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
public class PrjCheckApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private IPrjCheckService prjCheckService;
    @Autowired
    private PrjCheckMapper prjCheckMapper;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        PrjCheck prjCheck = prjCheckMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        //databaseLockProvider.lock(prjCheck);

        if (PASS.equals(hlsCusProcess.getType())) {
            prjCheck.setApproveSuggest("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            prjCheck.setApproveSuggest("REJECTED");

        }
        prjCheckService.updateByPrimaryKeySelective(iRequest, prjCheck);
    }
}
