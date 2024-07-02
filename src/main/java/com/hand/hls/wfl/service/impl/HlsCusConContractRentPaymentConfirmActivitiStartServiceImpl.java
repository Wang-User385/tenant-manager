package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractRentPaymentConfirm;
import com.hand.hls.cont.service.IHlsCusConContractRentPaymentConfirmService;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.gld.service.HlsCusConContractService;
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
 * Auther:congweijing
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractRentPaymentConfirmActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CON_CONTRACT_CASHCONFIRM";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;
    @Autowired
    private IHlsCusConContractRentPaymentConfirmService conContractRentPaymentConfirmService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private SysUserService sysUserService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusConContractRentPaymentConfirm)list.get(0),iRequest,params);

        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusConContractRentPaymentConfirm conContractRentPaymentConfirm, IRequest iRequest,Map params) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(conContractRentPaymentConfirm.getContractId());
        contract = hlsCusConContractService.selectByPrimaryKey(iRequest,contract);

        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CON_CONTRACT_CASHCONFIRM","CON_CONTRACT_CASHCONFIRM");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(conContractRentPaymentConfirm.getPaymentConfirmId().toString());
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

        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue("CON_CONTRACT_CASHCONFIRM");
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue("");
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(conContractRentPaymentConfirm.getPaymentConfirmId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("BUSINESS_KEY");
        restVariable10.setValue(conContractRentPaymentConfirm.getPaymentConfirmId());
        variables.add(restVariable10);
        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workFlowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        restVariable12.setValue(contract.getContractName());
        variables.add(restVariable12);

        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("documentNumber");
        restVariable13.setValue(contract.getContractNumber());
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("contractId");
        restVariable14.setValue(contract.getContractId());
        variables.add(restVariable14);

        RestVariable restVariable15 = new RestVariable();
        restVariable15.setName("projectId");
        restVariable15.setValue(contract.getProjectId());
        variables.add(restVariable15);

        RestVariable restVariable16 = new RestVariable();
        restVariable16.setName("unitId");
        restVariable16.setValue(contract.getUnitId());
        variables.add(restVariable16);

        RestVariable restVariable17 = new RestVariable();
        restVariable17.setName("companyId");
        restVariable17.setValue(contract.getCompanyId());
        variables.add(restVariable17);

        RestVariable restVariable18 = new RestVariable();
        restVariable18.setName("projectAssistant");
        restVariable18.setValue(params.get("projectAssistant"));
        variables.add(restVariable18);

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
        HlsCusConContractRentPaymentConfirm rentPaymentConfirm = new HlsCusConContractRentPaymentConfirm();
        rentPaymentConfirm.setPaymentConfirmId(id);
        rentPaymentConfirm.setStatus(CANCEL_STATUS);
        conContractRentPaymentConfirmService.updateByPrimaryKeySelective(iRequest,rentPaymentConfirm);
    }

    }
