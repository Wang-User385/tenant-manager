package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
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
public class ConContractLoanApprovalPassActiviti implements IHlsCusActivitiBean {


    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = hlsCusCshPaymentReqHdMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);

        if (PASS.equals(hlsCusProcess.getType())) {
            hlsCusCshPaymentReqHd.setPaymentReqStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusCshPaymentReqHd.setPaymentReqStatus("REJECTED");

        }
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqHd);
    }
}
