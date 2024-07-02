package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.ecif.dto.HlsCusEcifBpMasterChange;
import com.hand.hls.ecif.service.HlsCusEcifBpMasterChangeService;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:客户变更工作流
 * @Author: wangchao
 * @Date: Created in 10:34 2020/4/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusBpMasterEcifChangeActivitiServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "BP_ECIF_CHANGE_WORK_FLOW";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusEcifBpMasterChangeService service;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange= (HlsCusEcifBpMasterChange) list.get(0);

        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(iRequest, (HlsCusEcifBpMasterChange) list.get(0));
        ProcessInstanceResponse processInstanceResponse=  activitiService.startProcess(iRequest, processInstanceCreateRequest);

        if (processInstanceResponse.getId() != null) {
            hlsCusEcifBpMasterChange.setWflInstanceId(Long.parseLong(processInstanceResponse.getId()));
            hlsCusEcifBpMasterChange.setSubmitDate(new Date());
            //hlsCusEcifBpMasterChange.setSubmitUserId(iRequest.getUserId());
            service.updateByPrimaryKeySelective(iRequest, hlsCusEcifBpMasterChange);
        }

    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(IRequest iRequest, HlsCusEcifBpMasterChange hlsCusEcifBpMasterChange) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("BP_ECIF_CHANGE_WFL", "BP_ECIF_CHANGE");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusEcifBpMasterChange.getEcifChangeId().toString());

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
        restVariable4.setName("hlsCusEcifBpMasterChange");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusEcifBpMasterChange));
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
        restVariable8.setName("ecifChangeId");
        restVariable8.setValue(hlsCusEcifBpMasterChange.getEcifChangeId().toString());
        variables.add(restVariable8);


        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("bpId");
        restVariable9.setValue(hlsCusEcifBpMasterChange.getBpId().toString());
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("companyId");
        restVariable10.setValue(iRequest.getCompanyId().toString());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workflowType");
        restVariable11.setValue(WORK_FLOW_TYPE);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(hlsCusEcifBpMasterChange.getCstNmS());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("employeeCode");
        restVariable13.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable13);


        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("documentCategory");
        restVariable14.setValue("BP_ECIF_CHANGE");
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("documentType");
        restVariable15.setValue("BP_ECIF_CHANGE");
        variables.add(restVariable15);


        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("documentNumber");
        restVariable16.setValue(hlsCusEcifBpMasterChange.getChangeNumber());
        variables.add(restVariable16);


        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("unitId");
        restVariable17.setValue(iRequest.getAttribute("unitId"));
        variables.add(restVariable17);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        HlsCusEcifBpMasterChange dto = new HlsCusEcifBpMasterChange();
        dto.setEcifChangeId(id);
        dto.setWflStatus("NEW");
        service.updateByPrimaryKeySelective(iRequest, dto);
    }
}
