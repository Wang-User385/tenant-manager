package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.MarketingReportChange;
import com.hand.hls.hls.service.IMarketingReportChangeService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2019/7/2
 * @description: 融资合同变更启动方法
 */
@Service
public class MarketingReportChangeActivitiStartServiceImpl implements IActivitiCommonService {


    private static final String WORK_FLOW_TYPE = "MARKETING_REPORT_CHANGE";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    IMarketingReportChangeService marketingReportChangeService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        MarketingReportChange marketingReportChange = new MarketingReportChange();
        marketingReportChange.setChangeReqId(id);
        marketingReportChange.setStatus(CANCEL_STATUS);
        marketingReportChangeService.updateByPrimaryKeySelective(iRequest, marketingReportChange);
    }
}
