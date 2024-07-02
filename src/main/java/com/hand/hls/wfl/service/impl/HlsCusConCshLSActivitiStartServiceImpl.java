package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusConDebtExemptionReq;
import com.hand.hls.csh.service.IConDebtExemptionReqService;
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

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConCshLSActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CON_CONTRACT_CSH_WFL_LS";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private IConDebtExemptionReqService conDebtExemptionReqService;
    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        HlsCusConDebtExemptionReq dto = (HlsCusConDebtExemptionReq) list.get(0);
        HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq = conDebtExemptionReqService.selectByPrimaryKey(iRequest,dto);
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(hlsCusConDebtExemptionReq.getDocumentId());
        hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest,hlsCusConContract);
        params.put(WORK_FLOW_NAME, "CON_CONTRACT_CSH_WFL_LS");
        params.put(DEMO_NAME, "CON_CONTRACT");
        params.put(BUSINESS_KEY, dto.getChangeReqId());
        params.put("contractId", hlsCusConContract.getContractId());
        params.put("documentCategory", "CON_PENALTY_REDUCE");
        //params.put("documentName", hlsCusConContract.getContractName());
        params.put("documentNumber", hlsCusConContract.getContractNumber());

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusConContract.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(hlsCusPrjProject.getProjectId());
        params.put("projectAssistant", hlsCusPrjProject.getAssistProjectManager());

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusConDebtExemptionReq hlsCusConDebtExemptionReq, IRequest iRequest) {
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(hlsCusConDebtExemptionReq.getDocumentId());
        hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(iRequest,hlsCusConContract);
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CON_CONTRACT_CSH_WFL", "CON_CONTRACT_CSH_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusConDebtExemptionReq.getChangeReqId().toString());

        //查询对应项目信息
        HlsCusPrjProject hlsCusPrjProject=new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusConContract.getProjectId());
        hlsCusPrjProject=hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setEmployeeCode(iRequest.getEmployeeCode());
        hlsCusEmployee.setCompanyId(hlsCusPrjProject.getCompanyId());
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
        restVariable5.setName("hlsCusConContract");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusConContract));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(hlsCusConDebtExemptionReq.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusConDebtExemptionReq.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusConDebtExemptionReq.getChangeReqId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("contractNumber");
        restVariable9.setValue(hlsCusConContract.getContractNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("contractName");
        restVariable10.setValue(hlsCusConContract.getContractName());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workflowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("hlsCusConDebtExemptionReq");
        restVariable12.setValue(hlsCusConDebtExemptionReq);
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("projectId");
        restVariable13.setValue(hlsCusPrjProject.getRefProjectId());
        variables.add(restVariable13);



        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("businessType");
        restVariable14.setValue("FACTOR");
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("companyId");
        restVariable15.setValue(hlsCusConDebtExemptionReq.getCompanyId());
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("attType");
        restVariable16.setValue("CON_CONTRACT_CSH_WFL_ATT");
        variables.add(restVariable16);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("projectDocumentCategory");
        restVariable17.setValue("PRJ_PROJECT");
        variables.add(restVariable17);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("conProjectId");
        restVariable18.setValue(hlsCusPrjProject.getProjectId());
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("documentName");
        restVariable19.setValue(hlsCusConContract.getContractName());
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("unitId");
        restVariable20.setValue(hlsCusConContract.getUnitId());
        variables.add(restVariable20);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
        String processInstanceId = (String) params.get("processInstanceId");
        long contractId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusConContractCashflow hlsCusConContractCashflow=new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(contractId);
        hlsCusConContractCashflow.setCfItem(9L);
        hlsCusConContractCashflow.setFineStatus("APPROVING");
        hlsCusConContractCashflow.setFineReduceReq("Y");
        //查询出申请罚息减免的罚息现金流
        List<HlsCusConContractCashflow> list=hlsCusConContractCashflowService.select(iRequest,hlsCusConContractCashflow,1,1000);
        for(HlsCusConContractCashflow dt:list){
            dt.setFineStatus("NEW");
            //修改状态为原状态新建
            dt=hlsCusConContractCashflowService.updateByPrimaryKeySelective(iRequest,dt);
        }

        HlsCusConContract hlsCusConContract=new HlsCusConContract();
        hlsCusConContract.setContractId(contractId);
        //修改实际合同状态,原状态起租。
        hlsCusConContract.setContractStatus("INCEPT");
        hlsCusConContract = hlsCusConContractService.updateByPrimaryKeySelective(iRequest,hlsCusConContract);
    }
}
