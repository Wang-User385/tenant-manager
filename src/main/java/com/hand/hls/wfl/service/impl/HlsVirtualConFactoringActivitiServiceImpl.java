package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description
 * @author dql
 * @date 2024/9/5 13:35:12
 */
@Service
public class HlsVirtualConFactoringActivitiServiceImpl implements IActivitiCommonService {

    private static final String workFlowType = "CONTRACT_APPROVAL";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;


    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(((HlsCusPrjProject) list.get(0)).getProjectId());
        prjProject.setContractStatus("APPROVING");
        prjProject.setVirConProcessInstatnceId(Long.valueOf(processInstanceResponse.getId()));
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest,prjProject);
    }


    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(Long.parseLong(businessKey));
        prjProject.setProjectStatus("CANCEL");
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest,prjProject);
    }
}
