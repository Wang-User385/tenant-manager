package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.BpMasterChangeReq;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author: ximufeng
 * @version: v1.0
 * @description:
 * @date:2019/7/11
 */
@Service
@Transactional
public class HlsBpMasterChangeActivitiStartServiceImpl implements IActivitiCommonService{

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    com.hand.hls.prj.service.impl.BpMasterChangeReqServiceImpl BpMasterChangeReqServiceImpl;

    public HlsBpMasterChangeActivitiStartServiceImpl(){

    }

    private static final String WORK_FLOW_TYPE = "BP_CHANGE_WORK_FLOW";

    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    /**
     * 商业伙伴变更工作流启动
     * @param iRequest
     * @param list
     * @param params
     */
    public void process(IRequest iRequest, List list, Map params) {
        params.put(BUSINESS_KEY, ((BpMasterChangeReq)list.get(0)).getChangeReqId());
        params.put("WORK_FLOW", WORK_FLOW_TYPE);
        params.put("DEMO", "BP");
        ProcessInstanceCreateRequest processInstanceCreateRequest = this.wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        this.activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String)params.get("businessKey");
        long id = Long.parseLong(businessKey);
        BpMasterChangeReq bpMasterChangeReq = new BpMasterChangeReq();
        bpMasterChangeReq.setChangeReqId(id);
        bpMasterChangeReq.setStatus("CANCEL");
        this.BpMasterChangeReqServiceImpl.updateByPrimaryKeySelective(iRequest, bpMasterChangeReq);
    }

}
