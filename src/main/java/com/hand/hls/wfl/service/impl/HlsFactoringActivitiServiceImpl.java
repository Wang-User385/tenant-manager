package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/8/28 9:14
 */
public class HlsFactoringActivitiServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "FACTORING_PROJRCT_PROPOSAL";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;


    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(((HlsCusHlsCreditLineChance) list.get(0)).getChanceId());
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(iRequest,chance);
    }


    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(Long.parseLong(businessKey));
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(iRequest,chance);
    }

}
