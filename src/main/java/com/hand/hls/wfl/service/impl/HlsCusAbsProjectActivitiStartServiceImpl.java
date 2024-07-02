package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.abs.dto.HlsCusAbsProject;
import com.hand.hls.abs.service.HlsCusAbsProjectService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * ABS立项审批流程
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProjectActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = HlsCusConstant.ABS_WFL.PROJECT_WFL;
    private final String NAME_SPACE = "BOND_ISSUE_RECRUITMENT";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusAbsProjectService hlsCusAbsProjectService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusAbsProject) list.get(0), iRequest);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusAbsProject hlsCusAbsProject, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(HlsCusConstant.ABS_WFL.PROJECT_WFL, NAME_SPACE);
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusAbsProject.getProjectId().toString());
        //设置参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusAbsProject.getProjectId());
        variables.add(restVariable0);

        RestVariable restVariable1 = new RestVariable();
        restVariable1.setName("processDefinitionId");
        restVariable1.setValue(id);
        variables.add(restVariable1);

        RestVariable restVariable2 = new RestVariable();
        restVariable2.setName("startUserDescription");
        restVariable2.setValue(sysUser.getDescription());
        variables.add(restVariable2);

        RestVariable restVariable3 = new RestVariable();
        restVariable3.setName("iRequest");
        restVariable3.setValue(iRequest);
        variables.add(restVariable3);

        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("startUserName");
        restVariable4.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable4);

        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("hlsCusAbsProject");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusAbsProject));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(hlsCusAbsProject.getDocumentCategory());
        variables.add(restVariable6);

        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusAbsProject.getDocumentType());
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusAbsProject.getProjectId());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("projectNumber");
        restVariable9.setValue(hlsCusAbsProject.getProjectNumber());
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("projectName");
        restVariable10.setValue(hlsCusAbsProject.getProjectName());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("businessType");
        restVariable12.setValue(hlsCusAbsProject.getBusinessType());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("pName");
        restVariable13.setValue(name);
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("documentName");
        restVariable14.setValue(hlsCusAbsProject.getProjectNumber()+hlsCusAbsProject.getProjectShortName());
        variables.add(restVariable14);
        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("documentNumber");
        restVariable15.setValue(hlsCusAbsProject.getProjectNumber());
        variables.add(restVariable15);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);
        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long projectId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusAbsProject hlsCusAbsProject = new HlsCusAbsProject();
        hlsCusAbsProject.setProjectId(Long.valueOf(projectId));
        String status = "NEW";
        hlsCusAbsProject.setProjectStatus(status);
        hlsCusAbsProjectService.updateByPrimaryKeySelective(iRequest, hlsCusAbsProject);
    }
}
