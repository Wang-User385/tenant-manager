package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.mapper.HlsCusConFloatingRateReqMapper;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Transactional(
        rollbackFor = {Exception.class}
)
public class ConFloatingRateReqStartServiceImpl implements IActivitiCommonService {
    public static final String WORK_FLOW_TYPE = "CON_FLOAT_RATE";
    private static final String WORK_FLOAT_DEMO = "CON";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsCusConFloatingRateReqMapper hlsCusConFloatingRateReqMapper;

    public ConFloatingRateReqStartServiceImpl() {
    }

    public String getWorkFlowType() {
        return "CON_FLOAT_RATE";
    }

    public void process(IRequest iRequest, List list, Map params) {
        params.put("WORK_FLOW", "CON_CONTRACT_INTEREST_ADJUSTMENT");
        params.put("DEMO", "CON_CONTRACT");
        ProcessInstanceCreateRequest processInstanceCreateRequest = this.wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        this.activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    public void cancel(IRequest iRequest, Map params) {
        Long businessKey = (Long)params.get("BUSINESS_KEY");
        HlsCusConFloatingRateReq rateReq = new HlsCusConFloatingRateReq();
        rateReq.setFltReqId(businessKey);
        rateReq = (HlsCusConFloatingRateReq)this.hlsCusConFloatingRateReqMapper.selectByPrimaryKey(rateReq);
        rateReq.setStatus("CANCEL");
        this.hlsCusConFloatingRateReqMapper.updateByPrimaryKeySelective(rateReq);
    }
}
