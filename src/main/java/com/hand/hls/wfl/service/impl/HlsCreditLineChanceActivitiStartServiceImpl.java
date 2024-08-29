package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fin.service.HlsCusCreditContractLineService;
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
public class HlsCreditLineChanceActivitiStartServiceImpl implements IActivitiCommonService{

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsCusHlsCreditLineChanceService CreditContractLineService;

    public HlsCreditLineChanceActivitiStartServiceImpl(){

    }

    private static final String WORK_FLOW_TYPE = "CREDIT_CHANCE_CREATE_WFL";

    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    /**
     * 授信立项工作流启动
     * @param iRequest
     * @param list
     * @param params
     */
    public void process(IRequest iRequest, List list, Map params) {
        params.put(BUSINESS_KEY, ((HlsCusHlsCreditLineChance)list.get(0)).getChanceId());
        params.put("WORK_FLOW", WORK_FLOW_TYPE);
        params.put("DEMO", "Credit");
        ProcessInstanceCreateRequest processInstanceCreateRequest = this.wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        this.activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String)params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(id);
        chance.setCreditLineStatus("CANCEL");
        CreditContractLineService.updateByPrimaryKeySelective(iRequest,chance);
    }

}
