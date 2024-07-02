package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;

import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqService;
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
 * Created by zhangyu on 2018/6/4.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConFloatingRateReqActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CT_FLOATING_RATE_REQ_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HlsCusConFloatingRateReqService hlsCusConFloatingRateReqService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusConFloatingRateReq)list.get(0),iRequest);

        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusConFloatingRateReq conFloatingRateReq, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CT_LON_CONTRACT_WITHDRAW_WFL","CON_FLOATING_RATE_REQ");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(conFloatingRateReq.getFltReqId().toString());
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
        restVariable5.setName("conFloatingRateReq");
        JSONObject jsonObject= JSON.parseObject(JSON.toJSONString(conFloatingRateReq));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(conFloatingRateReq.getDocumentCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(conFloatingRateReq.getDocumentType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(conFloatingRateReq.getFltReqId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("fltReqNumber");
        restVariable10.setValue(conFloatingRateReq.getFltReqNumber());
        variables.add(restVariable10);

        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workflowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(conFloatingRateReq.getFltReqNumber());
        variables.add(restVariable12);

        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusConFloatingRateReq conFloatingRateReq = new HlsCusConFloatingRateReq();
        conFloatingRateReq.setFltReqId(id);
        conFloatingRateReq.setStatus(CANCEL_STATUS);
        hlsCusConFloatingRateReqService.updateByPrimaryKeySelective(iRequest,conFloatingRateReq);
    }

    }
