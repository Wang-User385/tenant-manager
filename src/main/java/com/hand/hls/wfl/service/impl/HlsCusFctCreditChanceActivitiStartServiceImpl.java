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
public class HlsCusFctCreditChanceActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "FCT_PROJECTCREATE_WFL"; //CREDIT_LINE_CHANCE_SUBMIT

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
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("FCT_PROJECTCREATE_WFL", "FCT_PROJECTCREATE");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusHlsCreditLineChance.getChanceId().toString());
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
        restVariable5.setName("hlsCusHlsCreditLineChance");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusHlsCreditLineChance));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("workFlowType");
        restVariable6.setValue(workFlowType);
        variables.add(restVariable6);
        //流程的名称为pName
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("pName");
        restVariable7.setValue(name);
        variables.add(restVariable7);

        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("creditLineNumber");
        restVariable8.setValue(hlsCusHlsCreditLineChance.getCreditLineNumber());
        variables.add(restVariable8);

        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("creditLineName");
        restVariable9.setValue(hlsCusHlsCreditLineChance.getCreditLineName());
        variables.add(restVariable9);

        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("documentCategory");
        restVariable10.setValue(hlsCusHlsCreditLineChance.getDocumentCategory());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("documentType");
        restVariable11.setValue(hlsCusHlsCreditLineChance.getDocumentType());
        variables.add(restVariable11);

        //这个参数必须，接收的是主表的ID
        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentId");
        restVariable12.setValue(hlsCusHlsCreditLineChance.getChanceId());
        variables.add(restVariable12);


//表单类型
        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("projectDocumentCategory");
        restVariable13.setValue("HLS_CREDIT_LINE_CHANCE");
        variables.add(restVariable13);
//单据名称
        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("documentName");
        restVariable14.setValue(hlsCusHlsCreditLineChance.getCreditLineName());
        variables.add(restVariable14);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("documentNumber");
        restVariable18.setValue(hlsCusHlsCreditLineChance.getCreditLineNumber());
        variables.add(restVariable18);


        /** 工作流审批规则需要的值*/
//      部门分管领导
        if (hlsCusHlsCreditLineChance.getUnitId() == null || hlsCusHlsCreditLineChance.getUnitId() == 0) {
            HlsEmployeeAssigns hlsEmployeeAssigns = new HlsEmployeeAssigns();
            hlsEmployeeAssigns.setEmployeeId(hlsCusHlsCreditLineChance.getProposerEmployeeId());
            hlsCusHlsCreditLineChance.setUnitId(hlsEmployeeAssignsMapper.select(hlsEmployeeAssigns).get(0).getUnitId());
            hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(iRequest, hlsCusHlsCreditLineChance);
        }

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("unitId");
        restVariable15.setValue(hlsCusHlsCreditLineChance.getUnitId());
        variables.add(restVariable15);

//      主办项目经理
        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("companyId");
        restVariable16.setValue(hlsCusHlsCreditLineChance.getCompanyId());
        variables.add(restVariable16);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("employeeManagerAssignsId");
        restVariable17.setValue(hlsCusHlsCreditLineChance.getProposerEmployeeAssignId());
        variables.add(restVariable17);

        //传协办项目经理
        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("projectAssistant");
        restVariable19.setValue(hlsCusHlsCreditLineChance.getProjectAssistant());
        variables.add(restVariable19);

        //传协办部门id
        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("assistUnitId");
        restVariable20.setValue(hlsCusHlsCreditLineChance.getAssitUnitId());
        variables.add(restVariable20);

        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("BUSINESS_KEY");
        restVariable22.setValue(hlsCusHlsCreditLineChance.getChanceId());
        variables.add(restVariable22);

        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("aviationFlag");
        Long unitId = hlsCusHlsCreditLineChance.getUnitId();
        String aviationFlag = "N";
        if(unitId == 114L){
            //航空管理部
            aviationFlag = "Y";
        }
        restVariable23.setValue(aviationFlag);
        variables.add(restVariable23);



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
