package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.prj.dto.HlsCreditPlan;
import com.hand.hls.prj.service.HlsCreditPlanService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
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
 * Created by huangzaipeng on 2018/04/19.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjChanceChangeActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CRL_CHANGE_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusHlsCreditLineChanceService chanceService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    @Autowired
    HlsCreditPlanService hlsCreditPlanService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusHlsCreditLineChance) list.get(0), iRequest,params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusHlsCreditLineChance hlsCusPrjChance, IRequest iRequest, Map param) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CRL_CHANGE_WFL","CRL_CHANGE_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(hlsCusPrjChance.getChanceId().toString());

        Long chanceIdOld = hlsCusPrjChance.getRefChanceId();
        Long chanceIdNew = hlsCusPrjChance.getChanceId();
        HlsCusHlsCreditLineChance hlsCusPrjChanceOld = new HlsCusHlsCreditLineChance();
        hlsCusPrjChanceOld.setChanceId(chanceIdOld);
        hlsCusPrjChanceOld = chanceService.selectByPrimaryKey(iRequest, hlsCusPrjChanceOld);
        HlsCusHlsCreditLineChance hlsCusPrjChanceNew = new HlsCusHlsCreditLineChance();
        hlsCusPrjChanceNew.setChanceId(chanceIdNew);
        hlsCusPrjChanceNew = chanceService.selectByPrimaryKey(iRequest, hlsCusPrjChanceNew);

        HlsCusEmployee hlsCusEmployee = new HlsCusEmployee();
        hlsCusEmployee.setEmployeeCode(iRequest.getEmployeeCode());
        hlsCusEmployee.setCompanyId(hlsCusPrjChance.getCompanyId());
        hlsCusEmployee = hlsCusEmployeeMapper.selectUnitIdByEmployeeCodeAndCompanyId(hlsCusEmployee).get(0);
        //设置参数
        List<RestVariable> variables = new ArrayList<RestVariable>();
        List<RestVariable> transientVariables = new ArrayList<RestVariable>();

        RestVariable restVariable0 = new RestVariable();
        restVariable0.setName("BUSINESS_KEY");
        restVariable0.setValue(hlsCusPrjChance.getChanceId().toString());
        variables.add(restVariable0);

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
        restVariable5.setName("hlsCusPrjChance");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hlsCusPrjChance));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(hlsCusPrjChance.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(hlsCusPrjChance.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(hlsCusPrjChance.getChanceId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("creditLineNumber");
        restVariable9.setValue(hlsCusPrjChance.getCreditLineNumber());
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("creditLineName");
        restVariable10.setValue(hlsCusPrjChance.getCreditLineName());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);


        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("chanceId");
        restVariable13.setValue(hlsCusPrjChance.getChanceId());
        variables.add(restVariable13);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("projectDocumentCategory");
        restVariable17.setValue("PRJ_PROJECT");
        variables.add(restVariable17);


        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("companyId");
        restVariable15.setValue(hlsCusPrjChance.getCompanyId());
        variables.add(restVariable15);


        RestVariable restVariable19 = new RestVariable();
        restVariable19.setName("hlsCusPrjProjectOld");
        JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(hlsCusPrjChanceOld));
        restVariable19.setValue(jsonObject1.toString());
        variables.add(restVariable19);

        RestVariable restVariable20 = new RestVariable();
        restVariable20.setName("hlsCusPrjProjectNew");
        JSONObject jsonObject2 = JSON.parseObject(JSON.toJSONString(hlsCusPrjChanceNew));
        restVariable20.setValue(jsonObject2.toString());
        variables.add(restVariable20);

        RestVariable restVariable21 = new RestVariable();
        restVariable21.setName("chanceIdOld");
        restVariable21.setValue(hlsCusPrjChance.getRefChanceId());
        variables.add(restVariable21);

        RestVariable restVariable22 = new RestVariable();
        restVariable22.setName("chanceIdNew");
        restVariable22.setValue(hlsCusPrjChance.getChanceId());
        variables.add(restVariable22);

        RestVariable restVariable23 = new RestVariable();
        restVariable23.setName("pName");
        restVariable23.setValue(name);
        variables.add(restVariable23);


        RestVariable restVariable25 = new RestVariable();
        restVariable25.setName("documentName");
        restVariable25.setValue(hlsCusPrjChance.getCreditLineName());
        variables.add(restVariable25);

        RestVariable restVariable35 = new RestVariable();
        restVariable35.setName("documentNumber");
        restVariable35.setValue(hlsCusPrjChance.getCreditLineNumber());
        variables.add(restVariable35);

        RestVariable restVariable26 = new RestVariable();
        restVariable26.setName("unitId");
        restVariable26.setValue(hlsCusEmployee.getUnitId());
        variables.add(restVariable26);

        /*HlsCusEmployee managerAssign = new HlsCusEmployee();
        managerAssign.setEmployeeId(Long.valueOf(hlsCusPrjChance.getEmployeeId()));
        managerAssign.setCompanyId(hlsCusPrjChance.getCompanyId());
        RestVariable restVariable27 = new RestVariable();
        restVariable27.setName("employeeManagerAssignsId");
        restVariable27.setValue(hlsCusEmployeeMapper.selectEmployeeAssignIdByEmployeeId(managerAssign).get(0).getEmployeeAssignId());
        variables.add(restVariable27);*/

        RestVariable restVariable28 = new RestVariable();
        restVariable28.setName("assistUnitId");
        restVariable28.setValue(hlsCusPrjChanceNew.getProjectAssistant());
        variables.add(restVariable28);


        RestVariable restVariable37 = new RestVariable();
        restVariable37.setName("versionFlag");
        restVariable37.setValue("N");
        variables.add(restVariable37);

        RestVariable restVariable38 = new RestVariable();
        restVariable38.setName("refProcessInstanceId");
        restVariable38.setValue(hlsCusPrjChanceOld.getProcessInstanceId());
        variables.add(restVariable38);

        RestVariable restVariable39 = new RestVariable();
        restVariable39.setName("projectAssistant");
        restVariable39.setValue(hlsCusPrjChanceOld.getProjectAssistant());
        variables.add(restVariable39);

        //是否延期授信
        HlsCreditPlan hlsCreditPlanOld = new HlsCreditPlan();
        HlsCreditPlan hlsCreditPlanNew = new HlsCreditPlan();
        hlsCreditPlanOld.setSourceDocumentId(chanceIdOld);
        hlsCreditPlanOld.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCreditPlanNew.setSourceDocumentId(chanceIdNew);
        hlsCreditPlanNew.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        List<HlsCreditPlan> planOldSelect = hlsCreditPlanService.select(iRequest, hlsCreditPlanOld, 1, 99);
        List<HlsCreditPlan> planNewSelect = hlsCreditPlanService.select(iRequest, hlsCreditPlanNew, 1, 99);
        RestVariable restVariable40 = new RestVariable();
        restVariable40.setName("creditExtend");
        if(planOldSelect.size() > 0 && planNewSelect.size() > 0){
            restVariable40.setValue(planNewSelect.get(0).getDateTo().compareTo(planOldSelect.get(0).getDateTo()) > 0 ? "Y" : "N");
        }else{
            restVariable40.setValue("N");
        }
        variables.add(restVariable40);

        RestVariable restVariable41 = new RestVariable();
        restVariable41.setName("creditFlag");
        restVariable41.setValue(hlsCusPrjChanceOld.getCreditFlag() == null ? "Y" : hlsCusPrjChanceOld.getCreditFlag());
        variables.add(restVariable41);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");//BasicTrxId
        String processInstanceId = (String) params.get("processInstanceId");
        long projectId = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);

        //修改变更信息表中的申请状态，原状态新建
        HlsCusChangeReqInfo changeReqInfo = new HlsCusChangeReqInfo();
        changeReqInfo.setDocumentId(projectId);
        changeReqInfo.setDocumentCategory("PRJ_PROJECT");
        changeReqInfo.setStatus("APPROVING");
        List<HlsCusChangeReqInfo> changeReqInfos = hlsCusChangeReqInfoService.select(iRequest, changeReqInfo, 1, 99999);
        HlsCusChangeReqInfo info = new HlsCusChangeReqInfo();
        if (changeReqInfos != null && changeReqInfos.size() == 1) {
            info.setChangeReqId(changeReqInfos.get(0).getChangeReqId());
            info.setStatus("NEW");
            hlsCusChangeReqInfoService.updateByPrimaryKeySelective(iRequest, info);
        }
    }
}
