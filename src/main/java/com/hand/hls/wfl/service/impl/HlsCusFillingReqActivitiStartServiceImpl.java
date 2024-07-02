package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fp.dto.JcFundFilling;
import com.hand.hls.fp.service.JcFundFillingService;
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
public class HlsCusFillingReqActivitiStartServiceImpl implements IActivitiCommonService {


    private static final String WORK_FLOW_TYPE = "FUNDING_PLAN_WFL_REQ";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;


    @Autowired
    private JcFundFillingService service;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);

        // 回写工作流实例ID
        Long fillingId = (Long) params.get(IActivitiCommonService.BUSINESS_KEY);
        JcFundFilling fundFilling = new JcFundFilling();
        fundFilling.setFillingId(fillingId);
        fundFilling.setReqStatus("APPROVING");

        fundFilling.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        service.updateByPrimaryKeySelective(iRequest,fundFilling);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        JcFundFilling filling = new JcFundFilling();
        filling.setFillingId(id);
        filling.setReqStatus("REJECTED");
        service.updateByPrimaryKeySelective(iRequest, filling);
    }
}
