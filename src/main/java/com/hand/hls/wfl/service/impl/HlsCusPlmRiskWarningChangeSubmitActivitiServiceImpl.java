package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationContractMapper;
import com.hand.hls.plm.fc.mapper.HlsCusFiveClassificationMapper;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.rw.dto.HlsCusRiskWarning;
import com.hand.hls.rw.service.HlsCusIRiskWarningService;
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
 * @Description:风险预警变更工作流
 * @Author:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmRiskWarningChangeSubmitActivitiServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "RW_RISK_WARNING_CHANGE_RELEASE_WFL";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusIRiskWarningService hlsCusIRiskWarningService;
    @Autowired
    private HlsCusFiveClassificationMapper fiveClassificationMapper;
    @Autowired
    private HlsCusIFiveClassificationService fiveClassificationService;

    @Autowired
    private HlsCusFiveClassificationContractMapper fiveClassificationContractMapper;

    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(iRequest, (HlsCusRiskWarning) list.get(0));
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(IRequest iRequest, HlsCusRiskWarning riskWarning) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        riskWarning.setFiveClassification(null);
        riskWarning.setRiskWarningInfo(null);
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("RW_RISK_WARNING_CHANGE_RELEASE_WFL", "RW_RISK_WARNING_CHANGE_RELEASE");
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
        restVariable2.setName("iRequest");
        restVariable2.setValue(iRequest);
        variables.add(restVariable2);

        RestVariable restVariable3 = new RestVariable();
        restVariable3.setName("startUserName");
        restVariable3.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable3);

        RestVariable restVariable4 = new RestVariable();
        restVariable4.setName("riskWarning");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(riskWarning));
        restVariable4.setValue(jsonObject.toString());
        variables.add(restVariable4);

        RestVariable restVariable5 = new RestVariable();
        restVariable5.setName("startUserDescription");
        restVariable5.setValue(sysUser.getDescription());
        variables.add(restVariable5);

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("pName");
        restVariable6.setValue(name);
        variables.add(restVariable6);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("riskWarningId");
        restVariable8.setValue(riskWarning.getRiskWarningId().toString());
        variables.add(restVariable8);


        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("companyId");
        restVariable10.setValue(iRequest.getCompanyId().toString());
        variables.add(restVariable10);

        //将documentName设置成商业伙伴名称
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(riskWarning.getBpId());
        hlsCusBpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);


        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("documentName");
        restVariable11.setValue(hlsCusBpMaster.getBpName());
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentCategory");
        restVariable12.setValue(riskWarning.getDocumentCategory());
        variables.add(restVariable12);
        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentType");
        restVariable13.setValue(riskWarning.getDocumentType());
        variables.add(restVariable13);
        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("workflowType");
        restVariable14.setValue(WORK_FLOW_TYPE);
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("unitId");
        restVariable15.setValue(hlsCusEmployee.getUnitId());
        variables.add(restVariable15);

        List<Long> employeeAssignId = fiveClassificationContractMapper.selectProjectEmployeeAssignId(riskWarning.getBpId(), riskWarning.getCompanyId());
        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("employeeAssignIdList");
        restVariable16.setValue(JSON.toJSONString(employeeAssignId));
        variables.add(restVariable16);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long riskWarningId = Long.parseLong(businessKey);
        HlsCusRiskWarning returnRiskWarning = new HlsCusRiskWarning();
        returnRiskWarning.setRiskWarningId(riskWarningId);
        returnRiskWarning.setStatus("APPROVED");
        hlsCusIRiskWarningService.updateByPrimaryKeySelective(iRequest, returnRiskWarning);
    }
}
