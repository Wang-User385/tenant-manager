package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.plm.fc.dto.HlsCusFiveClassification;
import com.hand.hls.plm.fc.service.HlsCusIFiveClassificationService;
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
 * @Description:五级分类工作流
 * @Author: wty
 * @Date: Created in 10:34 2018/5/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPlmFiveClassificationActivitiServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "PLM_FC_WORK_FLOW";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusIFiveClassificationService service;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest(iRequest, (HlsCusFiveClassification) list.get(0));
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(IRequest iRequest, HlsCusFiveClassification fiveClassification) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        fiveClassification.setFiveClassificationContracts(null);
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("FIVE_LEVEL_CLASSIFICATION_WFL", "FIVE_CLASSIFICATION");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(fiveClassification.getFiveClassificationId().toString());

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
        restVariable4.setName("fiveClassification");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(fiveClassification));
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


        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("projectDocumentCategory");
        restVariable7.setValue("PLM_FIVE_CLASSIFICATION");
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("postLoanId");
        restVariable8.setValue(fiveClassification.getFiveClassificationId().toString());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("plmType");
        restVariable9.setValue("FC");
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
        restVariable12.setValue(fiveClassification.getContractNames());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("employeeCode");
        restVariable13.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable13);


        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("documentCategory");
        restVariable14.setValue("FIVE_CLASSIFICATION");
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("documentType");
        restVariable15.setValue("FIVE_CLASSIFICATION");
        variables.add(restVariable15);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        HlsCusFiveClassification dto = new HlsCusFiveClassification();
        dto.setFiveClassificationId(id);
        dto.setStatus("NEW");
        service.updateByPrimaryKeySelective(iRequest, dto);
    }
}
