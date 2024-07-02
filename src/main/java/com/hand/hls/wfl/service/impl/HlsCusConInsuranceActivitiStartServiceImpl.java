package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProjectInsure;
import com.hand.hls.prj.service.HlsCusPrjProjectInsureService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/15 14:44
 */
@Service
public class HlsCusConInsuranceActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String workFlowType = "CON_INSURANCE_WFL";

    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private HlsCusPrjProjectInsureService hlsCusPrjProjectInsureService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long insureId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        HlsCusPrjProjectInsure prjProjectInsure = new HlsCusPrjProjectInsure();
        prjProjectInsure.setInsureId(insureId);
        prjProjectInsure.setApprovalStatus("NEW");
        hlsCusPrjProjectInsureService.updateByPrimaryKeySelective(iRequest, prjProjectInsure);
    }

}
