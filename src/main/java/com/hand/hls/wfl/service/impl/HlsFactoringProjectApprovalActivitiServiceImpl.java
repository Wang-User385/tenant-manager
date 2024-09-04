package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 保理项目审批工作流启动入口
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/9/4 13:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringProjectApprovalActivitiServiceImpl implements IActivitiCommonService {

    private static final String workFlowType = "FACTORING_BUSINESS_APPROVAL";

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
        prjProject.setProjectStatus("APPROVING");
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
