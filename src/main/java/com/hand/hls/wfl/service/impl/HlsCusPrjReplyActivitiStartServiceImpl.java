package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.service.JcFundFillingService;
import com.hand.hls.hls.dto.HlsDurationHd;
import com.hand.hls.hls.service.HlsDurationHdService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: ljj
 * @date: 2021/2/24
 * @description: 票据申请启动
 */
@Service
public class HlsCusPrjReplyActivitiStartServiceImpl implements IActivitiCommonService {


    private static final String WORK_FLOW_TYPE = "FCT_PROJECT_CHANGE_WFL";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;


    @Autowired
    private HlsDurationHdService service;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);

        // 回写工作流实例ID
        Long hdId = (Long) params.get(IActivitiCommonService.BUSINESS_KEY);
        HlsDurationHd hd = new HlsDurationHd();
        hd.setHdId(hdId);
        hd.setDurationStatus("APPROVING");

        service.updateByPrimaryKeySelective(iRequest,hd);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsDurationHd filling = new HlsDurationHd();
        filling.setHdId(id);
        filling.setDurationStatus("REJECTED");
        service.updateByPrimaryKeySelective(iRequest, filling);
    }
}
