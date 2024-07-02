package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/12 19:32
 */
@Service
public class HlsCusPrjAprrovalNoticeActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "FCT_MAKEAPPROVELNOTICE_WFL";
    private static final String PRJ_PROJECT_CHANGEWILL_WFL = "PRJ_PROJECT_CHANGEWILL_WFL";

    private static final String VOTED = "VOTED";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusPrjProject) list.get(0), iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusPrjProject hlsCusPrjProject, IRequest iRequest, Map param) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(param.get("wflKey").toString(), param.get("wflKey").toString());
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusPrjProject.getProjectId().toString());

        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusPrjProject.getProjectId().toString());
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
        restVariable5.setName("hlsCusPrjProject");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusPrjProject));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(param.get("wflKey"));
        variables.add(restVariable6);

        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(param.get("wflKey"));
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("approvalId");
        restVariable8.setValue(hlsCusPrjProject.getApprovalId());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("projectId");
        restVariable9.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("companyId");
        restVariable10.setValue(hlsCusPrjProject.getCompanyId());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(param.get("wflKey").toString());
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("functionUsage");
        restVariable12.setValue("");
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("maintainType");
        restVariable13.setValue("");
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("allocationIdRisk");
        restVariable14.setValue(hlsCusPrjProject.getAllocationIdRisk());
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("documentName");
        restVariable15.setValue(hlsCusPrjProject.getProjectName());
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("documentNumber");
        restVariable16.setValue(hlsCusPrjProject.getProjectNumber());
        variables.add(restVariable16);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("versionFlag");
        restVariable17.setValue("N");
        variables.add(restVariable17);

        String aviationFlag = "N";
        if (hlsCusPrjProject.getUnitId() == 114L) {
            //航空管理部
            aviationFlag = "Y";
        }
        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("aviationFlag");
        restVariable18.setValue(aviationFlag);
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("pName");
        restVariable19.setValue(name);
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("startEmpName");
        restVariable20.setValue(iRequest.getEmployeeName());
        variables.add(restVariable20);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("businessKey");
        restVariable21.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable21);

        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("projectIdOld");
        restVariable22.setValue(hlsCusPrjProject.getRefProjectId());
        variables.add(restVariable22);

        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("refProcessInstanceId");
        String refProcessInstanceId = "";
        if(hlsCusPrjProject.getRefProjectId() != null){
            HlsCusPrjProject prjProjectOld = new HlsCusPrjProject();
            prjProjectOld.setProjectId(hlsCusPrjProject.getRefProjectId());
            prjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,prjProjectOld);
            if(prjProjectOld.getMeetingProcessInstanceId() != null){
                refProcessInstanceId = prjProjectOld.getMeetingProcessInstanceId();
            }
        }

        restVariable23.setValue(refProcessInstanceId);
        variables.add(restVariable23);

        if(PRJ_PROJECT_CHANGEWILL_WFL.equals(param.get("wflKey").toString())){
            HlsCusPrjProject hlsCusPrjProjectOld = new HlsCusPrjProject();
            hlsCusPrjProjectOld.setProjectId(hlsCusPrjProject.getRefProjectId());
            hlsCusPrjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProjectOld);

            HlsCusPrjProject hlsCusPrjProjectNew = new HlsCusPrjProject();
            hlsCusPrjProjectNew.setProjectId(hlsCusPrjProject.getProjectId());
            hlsCusPrjProjectNew = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProjectNew);

            RestVariable restVariable24 = new RestVariable();
            restVariable24.setName("hlsCusPrjProjectOld");
            JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectOld));
            restVariable24.setValue(jsonObject1.toString());
            variables.add(restVariable24);

            RestVariable restVariable25 = new RestVariable();
            restVariable25.setName("hlsCusPrjProjectNew");
            JSONObject jsonObject2 = JSON.parseObject(JSON.toJSONString(hlsCusPrjProjectNew));
            restVariable25.setValue(jsonObject2.toString());
            variables.add(restVariable25);
        }

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        //projectId
        String businessKey = (String) params.get("businessKey");
        long projectId = Long.parseLong(businessKey);

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject.setApprovalStatus(VOTED);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
    }

}
