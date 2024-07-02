package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.mapper.HlsCusRiskWarningMapper;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:风险预警提交工作流
 * @Author: wty
 * @Date: Created in 10:34 2018/5/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmRiskWarningSubmitActivitiServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "RISK_WARNING_WFL";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusIRiskWarningService hlsCusIRiskWarningService;

    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Autowired
    private HlsCusRiskWarningMapper hlsCusRiskWarningMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private HlsCusFiveClassificationContractMapper fiveClassificationContractMapper;
    @Autowired
    private HlsCusPrjProjectMapper prjProjectMapper;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsCusIRiskWarningService riskWarningService;
    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
//        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(iRequest, (HlsCusRiskWarning) list.get(0));
//        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, map);
//        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, map);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
        // 回写工作流实例ID
//        Long riskWarningId = (Long) map.get(IActivitiCommonService.BUSINESS_KEY);
//        HlsCusRiskWarning warning = new HlsCusRiskWarning();
//        warning.setRiskWarningId(riskWarningId);
//        warning.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
//        riskWarningService.updateByPrimaryKeySelective(iRequest,warning);
//        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        //获取完整数据
        riskWarning = hlsCusRiskWarningMapper.selectByPrimaryKey(riskWarning);
        riskWarning.setFiveClassification(null);
        riskWarning.setRiskWarningInfo(null);
        //ReProcdef reProcdefs = reProcdefService.queryReProcdef("RW_RISK_WARNING_WORK_FLOW", "PLMANAGEMENT");
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("RISK_WARNING_WFL", "RISK_WARNING_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(riskWarning.getRiskWarningId().toString());

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setCompanyId(iRequest.getCompanyId());
        hlsCusEmployee.setEmployeeCode(iRequest.getEmployeeCode());
        hlsCusEmployee = hlsCusEmployeeMapper.selectUnitIdByEmployeeCodeAndCompanyId(hlsCusEmployee).get(0);

        //工作流参数
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
        restVariable5.setName("riskWarning");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(riskWarning));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue("RISK_WARNING_WFL");
        variables.add(restVariable6);

        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(riskWarning.getDocumentType());
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(riskWarning.getRiskWarningId());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("workflowType");
        restVariable10.setValue(WORK_FLOW_TYPE);
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("documentName");
        String documentName = "风险预警审批流程" + riskWarning.getRiskWarningNumber();
        restVariable11.setValue(documentName);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentNumber");
        restVariable12.setValue(riskWarning.getRiskWarningNumber());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("companyId");
        restVariable13.setValue(riskWarning.getCompanyId().toString());
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("businessKey");
        restVariable14.setValue(riskWarning.getRiskWarningId() );
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("riskWarningId");
        restVariable15.setValue(riskWarning.getRiskWarningId() );
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("BUSINESS_KEY");
        restVariable16.setValue(riskWarning.getRiskWarningId() );
        variables.add(restVariable16);

        //将documentName设置成商业伙伴名称
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(riskWarning.getBpId());
        hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);


        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("unitId");
        restVariable17.setValue(hlsCusEmployee.getUnitId());
        variables.add(restVariable17);

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
//        prjProject.setProjectId(riskWarning);
        List<Long> employeeAssignId = fiveClassificationContractMapper.selectProjectEmployeeAssignId(riskWarning.getBpId(), riskWarning.getCompanyId());

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("employeeAssignIdList");
        restVariable18.setValue(JSON.toJSONString(employeeAssignId));
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("attType");
        restVariable19.setValue("RISK_WARNING_WFL");
        variables.add(restVariable19);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
        String processInstanceId = (String) params.get("processInstanceId");
        long riskWarningId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        HlsCusRiskWarning returnRiskWarning = new HlsCusRiskWarning();
        returnRiskWarning.setRiskWarningId(riskWarningId);
        returnRiskWarning = hlsCusIRiskWarningService.selectByPrimaryKey(iRequest, returnRiskWarning);
        returnRiskWarning.setStatus("NEW");
        returnRiskWarning.setProcessInstanceId(prcId);
        hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest, returnRiskWarning);
    }
}
