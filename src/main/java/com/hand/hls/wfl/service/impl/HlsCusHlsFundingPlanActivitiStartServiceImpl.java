package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.HlsCusFundingPlan;
import com.hand.hls.hls.service.IFundingPlanService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author : qzk
 * @date : 2020/4/11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsFundingPlanActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "FUNDING_PLAN_WFL";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    IFundingPlanService fundingPlanService;


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
        HlsCusFundingPlan hlsCusFundingPlan = new HlsCusFundingPlan();
        hlsCusFundingPlan.setFundingPlanId(id);
        hlsCusFundingPlan.setStatus(CANCEL_STATUS);
        fundingPlanService.updateByPrimaryKeySelective(iRequest, hlsCusFundingPlan);
    }
}
