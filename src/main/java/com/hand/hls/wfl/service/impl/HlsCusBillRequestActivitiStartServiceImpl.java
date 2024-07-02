package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.service.IhlsBillRequestService;
import  com.hand.hls.bill.mapper.hlsBillRequestMapper;
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
public class HlsCusBillRequestActivitiStartServiceImpl implements IActivitiCommonService {


    private static final String WORK_FLOW_TYPE = "BILL_APPLICATION_WFL";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

   
    @Autowired
    IhlsBillRequestService hlsBillRequestService;
    @Autowired
    hlsBillRequestMapper hlsBillRequestMapper;


    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);

        // 回写工作流实例ID
        Long billId = (Long) params.get(IActivitiCommonService.BUSINESS_KEY);
        hlsBillRequest hlsBillRequest = new hlsBillRequest();
        hlsBillRequest.setBillId(billId);

        hlsBillRequest.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        hlsBillRequestService.updateByPrimaryKeySelective(iRequest,hlsBillRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        hlsBillRequest hlsBillRequest = new hlsBillRequest();
        hlsBillRequest.setBillId(id);
        hlsBillRequest.setBillStatus(CANCEL_STATUS);
        hlsBillRequestService.updateByPrimaryKeySelective(iRequest, hlsBillRequest);
    }
}
