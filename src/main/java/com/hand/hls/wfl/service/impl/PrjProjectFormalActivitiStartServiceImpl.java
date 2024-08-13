package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.IPrjProjectService;
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
 * @Description: 进件正审流程工作流发起事件
 * @Author: lipan
 * @Date: Created in 16:51 2024/7/21
 */
@Service
@Transactional
public class PrjProjectFormalActivitiStartServiceImpl implements IActivitiCommonService {
    //获取实现类
    private static final String WORK_FLOW_TYPE = "FORMAL_APPROVAL_WORK_FLOW";


    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    IPrjProjectService prjProjectService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);

        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);

    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(id);
        prjProject.setProjectStatus(CANCEL_STATUS);
        prjProjectService.updateByPrimaryKeySelective(iRequest, prjProject);
    }
}
