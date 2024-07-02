package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.mapper.PrjLeaseInspectMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.hn.service.IPrjLeaseInspectService;
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
public class PrjLeaseInspectApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private PrjLeaseInspectMapper prjLeaseInspectMapper;
    @Autowired
    private IPrjLeaseInspectService prjLeaseInspectService;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        PrjLeaseInspect prjLeaseInspect = prjLeaseInspectMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(prjLeaseInspect);

        if (PASS.equals(hlsCusProcess.getType())) {
            prjLeaseInspect.setApproveStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            prjLeaseInspect.setApproveStatus("REJECTED");

        }
        prjLeaseInspectService.updateByPrimaryKeySelective(iRequest, prjLeaseInspect);
    }
}
