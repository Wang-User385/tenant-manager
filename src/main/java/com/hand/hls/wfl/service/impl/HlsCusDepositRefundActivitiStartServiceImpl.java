package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusDepositRefund;
import com.hand.hls.csh.service.HlsCusDepositDeductionHdService;
import com.hand.hls.csh.service.HlsCusDepositRefundService;
import com.hand.hls.fct.dto.HlsCusFctContract;
import com.hand.hls.fct.mapper.HlsCusFctContractMapper;
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
 * @ClassName HlsCusDepositRefundActivitiStartServiceImpl
 * @Description //保证金退款
 * @Author yuan.yuan01@hand-china.com
 * @Date 2019/3/7 7:51 PM
 * @Version 1.0
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositRefundActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String workFlowType = "CM_DEPOSIT_REFUND_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusDepositRefundService depositRefundService;

    @Autowired
    private HlsCusFctContractMapper fctContractMapper;

    @Autowired
    private HlsCusConContractMapper conContractMapper;

    @Autowired
    private HlsCusDepositDeductionHdService depositDeductionHdService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }


    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusDepositRefund) list.get(0), iRequest);

        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusDepositRefund depositRefund, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CM_DEPOSIT_REFUND_WFL", "CM_DEPOSIT_REFUND_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(depositRefund.getDepositRefundId().toString());
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
        restVariable5.setName("depositRefund");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(depositRefund));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(depositRefund.getRefundDocCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(depositRefund.getRefundType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(depositRefund.getDepositRefundId());
        variables.add(restVariable8);
        RestVariable restVariable9 = new RestVariable();
        restVariable9.setName("pName");
        restVariable9.setValue(name);
        variables.add(restVariable9);
        RestVariable restVariable10 = new RestVariable();
        restVariable10.setName("employeeCode");
        restVariable10.setValue(iRequest.getEmployeeCode());
        variables.add(restVariable10);
        RestVariable restVariable11 = new RestVariable();
        restVariable11.setName("workflowType");
        restVariable11.setValue(workFlowType);
        variables.add(restVariable11);

        RestVariable restVariable12 = new RestVariable();
        restVariable12.setName("documentName");
        if ("CON_CONTRACT".equals(depositRefund.getRefundDocCategory())) {
            HlsCusConContract conContract = conContractMapper.selectByPrimaryKey(depositRefund.getContractId());
            restVariable12.setValue(conContract.getContractNumber() + conContract.getContractName());
        }
        if ("FCT_CONTRACT".equals(depositRefund.getRefundDocCategory())) {
            HlsCusFctContract fctContract = fctContractMapper.selectByPrimaryKey(depositRefund.getContractId());
            restVariable12.setValue(fctContract.getContractNumber() + fctContract.getContractName());
        }
        variables.add(restVariable12);

        Map<String, Long> unitAndCompany = depositDeductionHdService.selectProjectUnitAndCompany(depositRefund.getRefundDocCategory(), depositRefund.getContractId());
        RestVariable restVariable13 = new RestVariable();
        restVariable13.setName("unitId");
        restVariable13.setValue(unitAndCompany.get("UNIT_ID"));
        variables.add(restVariable13);

        RestVariable restVariable14 = new RestVariable();
        restVariable14.setName("companyId");
        restVariable14.setValue(unitAndCompany.get("COMPANY_ID"));
        variables.add(restVariable14);


        createRequest.setVariables(variables);
        createRequest.setTransientVariables(transientVariables);

        return createRequest;
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsCusDepositRefund depositRefund = new HlsCusDepositRefund();
        depositRefund.setDepositRefundId(id);
        depositRefund.setRefundStatus("NEW");
        depositRefundService.updateByPrimaryKeySelective(iRequest, depositRefund);
    }
}
