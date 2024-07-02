package com.hand.hls.wfl.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fnd.dto.HlsEmployeeAssigns;
import com.hand.hls.fnd.mapper.HlsEmployeeAssignsMapper;
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
 * @Date: Created in 15:46 2018/10/12
 * @Description: 授信立项开始工作流程
 * @Description: copy from GDXF by yy.chen
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCreditChanceActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CREDIT_CHANCE_CREATE_WFL";
    private static final String namespace = "CREDIT_CHANCE_CREATE_WFL";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsEmployeeAssignsMapper hlsEmployeeAssignsMapper;

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusHlsCreditLineChance) list.get(0), iRequest);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        //获取最新的头部信息
        //第一个参数是工作流主键，第二个参数是流程命名空间
        ReProcdef reProcdefs = reProcdefService.queryReProcdef(workFlowType, namespace);
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusHlsCreditLineChance.getChanceId().toString());
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
        restVariable5.setName("hlsCusHlsCreditLineChance");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusHlsCreditLineChance));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(hlsCusHlsCreditLineChance.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusHlsCreditLineChance.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusHlsCreditLineChance.getChanceId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("creditLineNumber");
        restVariable9.setValue(hlsCusHlsCreditLineChance.getCreditLineNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("creditLineName");
        restVariable10.setValue(hlsCusHlsCreditLineChance.getCreditLineName());
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
        restVariable13.setValue(hlsCusHlsCreditLineChance.getCreditLineName());
        variables.add(restVariable13);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("documentNumber");
        restVariable20.setValue(hlsCusHlsCreditLineChance.getCreditLineNumber());
        variables.add(restVariable20);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("employeeAssistantAssignsId");
        restVariable14.setValue(hlsCusHlsCreditLineChance.getProjectAssistantAssignsId());
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("companyId");
        restVariable15.setValue(hlsCusHlsCreditLineChance.getCompanyId());
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("employeeManagerAssignsId");
        restVariable16.setValue(hlsCusHlsCreditLineChance.getProposerEmployeeAssignId());
        variables.add(restVariable16);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("unitId");
        restVariable17.setValue(hlsCusHlsCreditLineChance.getUnitId());
        variables.add(restVariable17);

        //经办人projectId
        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("projectId");
        restVariable18.setValue(hlsCusHlsCreditLineChance.getChanceId());
        variables.add(restVariable18);

        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("projectDocumentCategory");
        restVariable19.setValue("HLS_CREDIT_LINE");
        variables.add(restVariable19);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("businessKey");
        restVariable21.setValue(hlsCusHlsCreditLineChance.getChanceId());
        variables.add(restVariable21);

        //协办经理
        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("projectAssistant");
        restVariable22.setValue(hlsCusHlsCreditLineChance.getProjectAssistant());
        variables.add(restVariable22);

        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("chanceId");
        restVariable23.setValue(hlsCusHlsCreditLineChance.getChanceId());
        variables.add(restVariable23);

        RestVariable restVariable24 = new RestVariable();
        restVariable24.setName("BUSINESS_KEY");
        restVariable24.setValue(hlsCusHlsCreditLineChance.getChanceId());
        variables.add(restVariable24);

        RestVariable restVariable25 = new RestVariable();
        restVariable25.setName("creditFlag");
        restVariable25.setValue(hlsCusHlsCreditLineChance.getCreditFlag());
        variables.add(restVariable25);

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
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long chanceId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = new HlsCusHlsCreditLineChance();
        hlsCusHlsCreditLineChance.setChanceId(chanceId);
        //修改流程事件的状态
        String status = "NEW";
        hlsCusHlsCreditLineChance.setCreditLineStatus(status);
        hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(iRequest, hlsCusHlsCreditLineChance);
    }
}
