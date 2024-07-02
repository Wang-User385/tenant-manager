package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.hn.dto.PrjCheck;
import com.hand.hls.hn.mapper.PrjCheckMapper;
import com.hand.hls.hn.service.IPrjCheckService;
import com.hand.hls.plm.rc.dto.HlsCusRentCollection;
import com.hand.hls.plm.rc.mapper.HlsCusRentCollectionMapper;
import com.hand.hls.plm.rc.service.HlsCusIRentCollectionService;
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
public class HlsCusPlmRentCollectionApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsCusIRentCollectionService rentCollectionService;
    @Autowired
    private HlsCusRentCollectionMapper hlsCusRentCollectionMapper;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        HlsCusRentCollection hlsCusRentCollection = hlsCusRentCollectionMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(hlsCusRentCollection);

        if (PASS.equals(hlsCusProcess.getType())) {
            hlsCusRentCollection.setCollectionStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusRentCollection.setCollectionStatus("REJECTED");

        }
        rentCollectionService.updateByPrimaryKeySelective(iRequest, hlsCusRentCollection);
    }
}
