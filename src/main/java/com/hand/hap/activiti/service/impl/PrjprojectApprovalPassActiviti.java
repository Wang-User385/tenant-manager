package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.fin.dto.HlsCusLonContractAttachment;
import com.hand.hls.fin.mapper.HlsCusLonContractAttachmentMapper;
import com.hand.hls.fin.service.LonContractAttachmentService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
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
public class PrjprojectApprovalPassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";

    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        HlsCusPrjProject hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(hlsCusPrjProject);

        if (PASS.equals(hlsCusProcess.getType())) {
            hlsCusPrjProject.setProjectStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusPrjProject.setProjectStatus("APPROVED_RETURN");

        }
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
    }
}
