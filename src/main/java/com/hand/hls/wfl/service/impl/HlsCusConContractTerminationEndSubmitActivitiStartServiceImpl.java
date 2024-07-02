package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusContractTermination;
import com.hand.hls.cont.service.HlsCusContractTerminationService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractTerminationEndSubmitActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CON_CONTRACT_END_WFL";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusContractTerminationService hlsCusContractTerminationService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        HlsCusConContract t = (HlsCusConContract) list.get(0);
        HlsCusConContract hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest, t);
        params.put(WORK_FLOW_NAME, "CON_CONTRACT_END_WFL");
        params.put(DEMO_NAME, "CON_CONTRACT");
        params.put(BUSINESS_KEY, t.getContractId());
        params.put("projectId", hlsCusConContract.getProjectId());
        params.put("documentCategory", "CON_CONTRACT");
        params.put("documentName", hlsCusConContract.getContractName());
        params.put("documentNumber", hlsCusConContract.getContractNumber());
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusConContract hlsCusConContract, Map map, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        //第一个参数是工作流主键，第二个参数是工作流的类别
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CON_CONTRACT_END_WFL", "CON_CONTRACT_END_WFL_SPACE");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusConContract.getContractId().toString());

        //获取合同结束信息
        Long contractTerminationId = Long.parseLong(map.get("contractTerminationId").toString());
        HlsCusContractTermination hlsCusContractTermination = new HlsCusContractTermination();
        hlsCusContractTermination.setContractTerminationId(contractTerminationId);
        hlsCusContractTermination = hlsCusContractTerminationService.selectByPrimaryKey(iRequest, hlsCusContractTermination);
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusConContract.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        //设置基础参数
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
        //传输的对象是主表信息，用于传输对象，将对象转成JSON格式，方便获取
        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("hlsCusConContract");
        JSONObject jsonObjectA = JSON.parseObject(JSON.toJSONString(hlsCusConContract));
        restVariable5.setValue(jsonObjectA.toString());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("hlsCusContractTermination");
        JSONObject jsonObjectB = JSON.parseObject(JSON.toJSONString(hlsCusContractTermination));
        restVariable5.setValue(jsonObjectB.toString());
        variables.add(restVariable6);

        //流程的名称为pName
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("pName");
        restVariable7.setValue(name);
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("contractNumber");
        restVariable8.setValue(hlsCusConContract.getContractNumber());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("contractName");
        restVariable9.setValue(hlsCusConContract.getContractName());
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("documentCategory");
        restVariable10.setValue(hlsCusConContract.getDocumentCategory());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("documentType");
        restVariable11.setValue(hlsCusConContract.getDocumentType());
        variables.add(restVariable11);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("documentName");
        restVariable18.setValue(hlsCusConContract.getContractName());
        variables.add(restVariable18);

        //这个参数必须，接收的是主表的ID
        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentId");
        restVariable12.setValue(hlsCusConContract.getContractId());
        variables.add(restVariable12);

//商业类型
//        RestVariable restVariable7 = new RestVariable();
//        restVariable7.setName("businessType");
//        restVariable7.setValue("FACTOR");
//        variables.add(restVariable7);

//表单类型
        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName(HlsCusConstant.WORKFLOW_PARAMS.PROJECT_DOCUMENT_CATEGORY);
        restVariable13.setValue("CON_CONTRACT_TERMINATION");
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("contractTerminationId");
        restVariable14.setValue(contractTerminationId);
        variables.add(restVariable14);


        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName(HlsCusConstant.WORKFLOW_PARAMS.WORKFLOW_TYPE);
        restVariable15.setValue(workFlowType);
        variables.add(restVariable15);


        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("companyId");
        restVariable17.setValue(hlsCusConContract.getCompanyId());
        variables.add(restVariable17);

        /*RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("employeeAssistantAssignsId");
        restVariable20.setValue(hlsCusPrjProject.getProjectAssistantAssignsId());
        variables.add(restVariable20);*/
        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("unitId");
        restVariable21.setValue(hlsCusConContract.getUnitId());
        variables.add(restVariable21);
        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);
        return createRequest;
    }

    @Override
    /**
     * 退回事件
     */
    public void cancel(IRequest iRequest, Map params) {
        //获取流程事件的ID
        String businessKey = (String) params.get(HlsCusConstant.WORKFLOW_PARAMS.BUSINESS_KEY);
        String processInstanceId = (String) params.get(HlsCusConstant.WORKFLOW_PARAMS.PROCESS_INSTANCE_ID);
        long contractTerminationId = Long.parseLong((String) params.get("contractTerminationId"));
        long contractId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusContractTermination hlsCusContractTermination = new HlsCusContractTermination();
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(contractId);
        hlsCusContractTermination.setContractTerminationId(contractTerminationId);
        hlsCusContractTermination.setStatus("NEW");
        hlsCusContractTerminationService.updateByPrimaryKeySelective(iRequest, hlsCusContractTermination);
    }
}
