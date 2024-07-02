package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
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
 * Created by huqingtao on 2019/06/28.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectSuppleActivitiStartServiceImpl implements IActivitiCommonService {
    //private static final String workFlowType = "LEASE_PRO_REVIEW_WF";
    private static final String workFlowType = "PROJECT_REPORT_SUPPLEMENT";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusPrjProject) list.get(0), iRequest);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusPrjProject hlsCusPrjProject, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        //ReProcdef reProcdefs = reProcdefService.queryReProcdef("LEASE_PRO_REVIEW_WF","LEASE_PRO_REVIEW_WF");
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("PROJECT_REPORT_SUPPLEMENT","PROJECT_REPORT_SUPPLEMENT");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusPrjProject.getProjectId().toString());

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setEmployeeCode(iRequest.getEmployeeCode());
        hlsCusEmployee.setCompanyId(hlsCusPrjProject.getCompanyId());
        hlsCusEmployee = hlsCusEmployeeMapper.selectUnitIdByEmployeeCodeAndCompanyId(hlsCusEmployee).get(0);

        //设置参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusPrjProject.getPrjSuppleId().toString());
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
        restVariable6.setValue(hlsCusPrjProject.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusPrjProject.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusPrjProject.getChanceId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("projectNumber");
        restVariable9.setValue(hlsCusPrjProject.getProjectNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("projectName");
        restVariable10.setValue(hlsCusPrjProject.getProjectName());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        /*RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("employeeAssistantAssignsId");
        restVariable12.setValue(hlsCusPrjProject.getProjectAssistantAssignsId());
        variables.add(restVariable12);*/

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("projectId");
        restVariable13.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("businessType");
        restVariable14.setValue("FACTOR");
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("companyId");
        restVariable15.setValue(hlsCusPrjProject.getCompanyId());
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("attType");
        restVariable16.setValue("PRJ_CREDIT_WFL");
        variables.add(restVariable16);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("projectDocumentCategory");
        restVariable17.setValue("PRJ_PROJECT");
        variables.add(restVariable17);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("pName");
        restVariable18.setValue(name);
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("attTypeFin");
        restVariable19.setValue("PRJ_CREDIT_WFL_FIN");
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("documentName");
        restVariable20.setValue(hlsCusPrjProject.getProjectName());
        variables.add(restVariable20);

        RestVariable restVariable50 = new RestVariable();
        restVariable50.setName("documentNumber");
        restVariable50.setValue(hlsCusPrjProject.getProjectNumber());
        variables.add(restVariable50);


        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("unitId");
//        restVariable21.setValue(hlsCusEmployee.getUnitId());
        restVariable21.setValue(hlsCusPrjProject.getHostUnitId());

        variables.add(restVariable21);

        if (hlsCusPrjProject.getBusinessType().equalsIgnoreCase("OPERATING_LEASE")) {
            RestVariable restVariable23 = new RestVariable();
            restVariable23.setName("isOperatingLease");
            restVariable23.setValue("true");
            variables.add(restVariable23);
        } else {
            RestVariable restVariable23 = new RestVariable();
            restVariable23.setName("isOperatingLease");
            restVariable23.setValue("false");
            variables.add(restVariable23);
        }

        //额度id,用来查询
        RestVariable restVariable24 = new RestVariable();
        restVariable24.setName("creditLineId");
        restVariable24.setValue(hlsCusPrjProject.getCreditLineId());
        variables.add(restVariable24);

        //财务报表头id,用来查询
        RestVariable restVariable25 = new RestVariable();
        restVariable25.setName("finStatementHdId");
        restVariable25.setValue(hlsCusPrjProject.getFinStatementHdId());
        variables.add(restVariable25);


        HlsCusEmployee managerAssign = new HlsCusEmployee();
        managerAssign.setEmployeeId(hlsCusPrjProject.getEmployeeId());
        managerAssign.setCompanyId(hlsCusPrjProject.getCompanyId());
        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("employeeManagerAssignsId");
        restVariable22.setValue(hlsCusEmployeeMapper.selectEmployeeAssignIdByEmployeeId(managerAssign).get(0).getEmployeeAssignId());
        variables.add(restVariable22);

        RestVariable restVariable27 = new RestVariable();
        restVariable27.setName("assistUnitId");
        restVariable27.setValue(hlsCusPrjProject.getAssistUnitId());
        variables.add(restVariable27);


        RestVariable restVariable28 = new RestVariable();
        restVariable28.setName("prjSuppleId");
        restVariable28.setValue(hlsCusPrjProject.getPrjSuppleId().toString());
        variables.add(restVariable28);

        RestVariable restVariable29 = new RestVariable();
        restVariable29.setName("allocationIdRisk");
        restVariable29.setValue(hlsCusPrjProject.getAllocationIdRisk().toString());
        variables.add(restVariable29);

        RestVariable restVariable30 = new RestVariable();
        restVariable30.setName("employeeEnableFlag");
        restVariable30.setValue(hlsCusPrjProject.getEmployeeEnableFlag().toString());
        variables.add(restVariable30);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);


        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
        String processInstanceId = (String) params.get("processInstanceId");
        long projectId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        HlsCusPrjProject hlsCusPrjProject=new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject.setProjectStatus("NEW");
        //项目审批原状态为新建
        hlsCusPrjProject = hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest,hlsCusPrjProject);
    }
    }
