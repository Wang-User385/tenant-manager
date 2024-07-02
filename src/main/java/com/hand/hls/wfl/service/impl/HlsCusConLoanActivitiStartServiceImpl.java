package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import com.hand.hls.csh.service.ICshPaymentReqHdService;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
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

/**
 * Created by Yenick
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConLoanActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CON_PAYMENT_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private CshPaymentReqLnService cshPaymentReqLnService;
    @Autowired
    private HlsCusConContractService service;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        HlsCusCshPaymentReqHd t = (HlsCusCshPaymentReqHd) list.get(0);
       /* HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(t.getContractId());*/
/*
        Long projectId = service.selectByPrimaryKey(iRequest, hlsCusConContract).getProjectId();
*/

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(t.getSourceDocId());
        hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject.getProjectId());

        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = new HlsCusHlsCreditLineChance();
        hlsCusHlsCreditLineChance.setChanceId(hlsCusPrjProject.getChanceId());
        hlsCusHlsCreditLineChance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(hlsCusHlsCreditLineChance);

        params.put("workFlowType", "CON_PAYMENT_WFL");
        params.put(WORK_FLOW_NAME, "CON_PAYMENT_WFL");
        params.put(DEMO_NAME, "CON_CONTRACT");
        params.put(BUSINESS_KEY, t.getPaymentReqId());
        //params.put("projectId", t.getProjectId());
        params.put("projectId", hlsCusPrjProject.getProjectId());
        params.put("contractId", t.getContractId());
        params.put("companyId",  t.getCompanyId());
        params.put("unitId", hlsCusPrjProject.getHostUnitId());
        params.put("documentCategory", "CON_CONTRACT");
        params.put("fundingPlanId", t.getFundingPlanId());
        params.put("documentName", hlsCusPrjProject.getContractName());
        params.put("documentNumber", hlsCusPrjProject.getContractNumber());

        params.put("projectAssistant", hlsCusPrjProject.getAssistProjectManager());
        params.put("creditFlag", hlsCusHlsCreditLineChance.getCreditFlag());
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd, IRequest iRequest) {
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(hlsCusCshPaymentReqHd.getSourceDocId());
        Long contractId = hlsCusCshPaymentReqHd.getSourceDocId();
        Long projectId = service.selectByPrimaryKey(iRequest, hlsCusConContract).getProjectId();

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);

        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
        cshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(iRequest, cshPaymentReqHd);

        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CON_PAYMENT_WFL", "CON_PAYMENT_WFL_SPACE");

        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        //createRequest.setBusinessKey(hlsCusCshPaymentReqLn.getPayment_req_ln_id().toString());
        createRequest.setBusinessKey(hlsCusCshPaymentReqHd.getPaymentReqId().toString());
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
        restVariable5.setName("hlsCusCshPaymentReqLn");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusCshPaymentReqHd));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("hlsCusPrjProject");
        JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(hlsCusPrjProject));
        restVariable7.setValue(jsonObject1.toString());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusCshPaymentReqHd.getPaymentReqId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("paymentReqNumber");
        restVariable9.setValue(cshPaymentReqHd.getPaymentReqNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("contractId");
        restVariable10.setValue(contractId);
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workflowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("projectId");
        restVariable13.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable13);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("projectDocumentCategory");
        restVariable17.setValue("PRJ_PROJECT");
        variables.add(restVariable17);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("businessType");
        restVariable14.setValue("LEASING");
        variables.add(restVariable14);

        /*RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("employeeAssistantAssignsId");
        restVariable15.setValue(hlsCusPrjProject.getProjectAssistantAssignsId());
        variables.add(restVariable15);*/


        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("companyId");
        restVariable16.setValue(hlsCusPrjProject.getCompanyId());
        variables.add(restVariable16);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("attType");
        restVariable18.setValue("CON_PAYMENT_WFL");
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("pName");
        restVariable19.setValue(name);
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("documentName");
        restVariable20.setValue(hlsCusPrjProject.getContractName());
        variables.add(restVariable20);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("unitId");
        restVariable21.setValue(hlsCusPrjProject.getUnitId());
        variables.add(restVariable21);

        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("contractId");
        restVariable22.setValue(hlsCusCshPaymentReqHd.getSourceDocId());
        variables.add(restVariable21);

        HlsCusEmployee managerAssign = new HlsCusEmployee();
        managerAssign.setEmployeeId(hlsCusPrjProject.getEmployeeId());
        managerAssign.setCompanyId(hlsCusPrjProject.getCompanyId());
        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("employeeManagerAssignsId");
        restVariable23.setValue(hlsCusEmployeeMapper.selectEmployeeAssignIdByEmployeeId(managerAssign).get(0).getEmployeeAssignId());
        variables.add(restVariable23);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long paymentReqHdId = Long.parseLong(businessKey);


        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(paymentReqHdId);
        cshPaymentReqHd.setPaymentReqStatus("NEW");
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, cshPaymentReqHd);
    }
}
