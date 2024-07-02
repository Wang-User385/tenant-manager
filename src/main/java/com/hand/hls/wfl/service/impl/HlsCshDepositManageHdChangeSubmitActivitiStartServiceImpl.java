package com.hand.hls.wfl.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.DepositManageHd;
import com.hand.hls.csh.service.IDepositManageHdService;
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
 * Created by junL on 2022年8月5日.
 * 保证金处理方式变更审批流程
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCshDepositManageHdChangeSubmitActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "BZJ_CLFSBG";
    private static final String workFlowTypeDesc = "-保证金处理方式变更-";
    private static final String WORK_FLOW_CATEGORY = "BZJ_CLFSBG";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    //@Autowired
    //private IDepositM hlsCreditLineService;
    @Autowired
    private IDepositManageHdService depositManageHdService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((DepositManageHd) list.get(0), iRequest);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(DepositManageHd depositManageHd, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(workFlowType, WORK_FLOW_CATEGORY);
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(depositManageHd.getManageHdId().toString());
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
        restVariable5.setName("depositManageHd");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(depositManageHd));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue("BZJ_TREATMENT");
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue("BZJ_TREATMENT");
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(depositManageHd.getManageHdId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("manageNumber");
        restVariable9.setValue(depositManageHd.getManageNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("contractName");
        restVariable10.setValue(depositManageHd.getContractName());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("pName");
        restVariable12.setValue(name);
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentName");
        String documentName = depositManageHd.getManageNumber()+workFlowTypeDesc + depositManageHd.getContractNumber();
        restVariable13.setValue(documentName);
        variables.add(restVariable13);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("documentNumber");
        restVariable20.setValue(depositManageHd.getManageNumber());
        variables.add(restVariable20);

        /*RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("employeeAssistantAssignsId");
        restVariable14.setValue(depositManageHd.getProjectAssistantAssignsId());
        variables.add(restVariable14);*/

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("companyId");
        restVariable15.setValue(iRequest.getCompanyId());
        variables.add(restVariable15);

        /*RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("employeeManagerAssignsId");
        restVariable16.setValue(depositManageHd.getProposerEmployeeAssignId());
        variables.add(restVariable16);*/

        /*RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("unitId");
        restVariable17.setValue(depositManageHd.getUnitId());
        variables.add(restVariable17);*/
        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("contractNumber");
        restVariable16.setValue(depositManageHd.getContractNumber());
        variables.add(restVariable16);
        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("manageHdId");
        restVariable17.setValue(depositManageHd.getManageHdId());
        variables.add(restVariable17);

        //经办人projectId
        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("projectId");
        restVariable18.setValue(depositManageHd.getProjectId());
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("projectDocumentCategory");
        restVariable19.setValue("HLS_CREDIT_LINE");
        variables.add(restVariable19);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("businessKey");
        restVariable21.setValue(depositManageHd.getManageHdId());
        variables.add(restVariable21);

        //协办经理
        /*RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("projectAssistant");
        restVariable22.setValue(depositManageHd.getProjectAssistant());
        variables.add(restVariable22);

        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("chanceId");
        restVariable23.setValue(depositManageHd.getChanceId());
        variables.add(restVariable23);*/

        RestVariable restVariable24 = new RestVariable();
        restVariable24.setName("BUSINESS_KEY");
        restVariable24.setValue(depositManageHd.getManageHdId());
        variables.add(restVariable24);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
//        String processInstanceId = (String) params.get("processInstanceId");
        long chanceId = Long.parseLong(businessKey);
//        long prcId = Long.parseLong(processInstanceId);
        DepositManageHd hlsCusHlsCreditLine = new DepositManageHd();
        hlsCusHlsCreditLine.setManageHdId(chanceId);
        hlsCusHlsCreditLine.setExecutionResult("CANCEL");
        hlsCusHlsCreditLine = depositManageHdService.updateByPrimaryKeySelective(iRequest, hlsCusHlsCreditLine);
    }
}
