package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.GENER.dto.HlsGeneralIssue;

import com.hand.hls.GENER.service.IHlsGeneralIssueService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
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
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: ljj
 * @date: 2021/2/24
 * @description: 票据申请启动
 */
@Service
public class HlsCusGeneralIssueActivitiStartServiceImpl implements IActivitiCommonService {


    private static final String workFlowType = "GENERAL_MATTERS_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private IHlsGeneralIssueService hlsgeneralissueservice;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        HlsGeneralIssue hlsgeneralissue = (HlsGeneralIssue) list.get(0);
        params.put(WORK_FLOW_NAME, "GENERAL_MATTERS_WFL");
        params.put(DEMO_NAME, "GENERAL_MATTERS_WFL");
        params.put(BUSINESS_KEY, hlsgeneralissue.getGeneralId());
        params.put("generalId", hlsgeneralissue.getGeneralId());

        params.put("documentCategory", "CON_CONTRACT");
        params.put("documentName", "通用事项审批流程");
        params.put("companyId", iRequest.getCompanyId());
        params.put("documentNumber", hlsgeneralissue.getDescription());
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsGeneralIssue hlsgeneralissue, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("GENERAL_MATTERS_WFL", "GENERAL_MATTERS_WFL");

        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsgeneralissue.getGeneralId().toString());

        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());

        String employeeCode = employee.getEmployeeCode();

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setEmployeeCode(employeeCode);
/*
        hlsCusEmployee.setCompanyId(hlsgeneralissue.getCompanyId());
*/
        hlsCusEmployee = hlsCusEmployeeMapper.selectUnitIdByEmployeeCodeAndCompanyId(hlsCusEmployee).get(0);

        //设置参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();
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
        restVariable5.setName("hlsgeneralissue");

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsgeneralissue));


        restVariable5.setValue(jsonObject.toString());

        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentId");
        restVariable6.setValue(hlsgeneralissue.getGeneralId());
        variables.add(restVariable6);


        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("workflowType");
        restVariable7.setValue(workFlowType);
        variables.add(restVariable7);




        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("generalId");
        restVariable8.setValue(hlsgeneralissue.getGeneralId().toString());
        variables.add(restVariable8);


        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("attType");
        restVariable16.setValue("CON_CONTRACT_SIGN_WFL");
        variables.add(restVariable16);



        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("pName");
        restVariable18.setValue(name);
        variables.add(restVariable18);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("documentName");
        restVariable20.setValue(hlsgeneralissue.getDescription());
        variables.add(restVariable20);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("unitId");
        restVariable21.setValue(hlsCusEmployee.getUnitId());
        variables.add(restVariable21);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
        String processInstanceId = (String) params.get("processInstanceId");
        long generalId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        HlsGeneralIssue hlsgeneralissue=new HlsGeneralIssue();
        hlsgeneralissue.setStatus("NEW");
        hlsgeneralissue.setGeneralId(generalId);
        hlsgeneralissueservice.updateByPrimaryKeySelective(iRequest,hlsgeneralissue);
    };
}
