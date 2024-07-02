package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.HlsCusDepositDeductionHd;
import com.hand.hls.csh.service.HlsCusDepositDeductionHdService;
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
 * @ClassName HlsCusDepositDeductActivitiStartServiceImpl
 * @Description //保证金抵扣
 * @Author yuan.yuan01@hand-china.com
 * @Date 2019/3/7 7:45 PM
 * @Version 1.0
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositDeductActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String workFlowType = "CM_DEPOSIT_DEDUCTION_WFL";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private IReProcdefService reProcdefService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private HlsCusDepositDeductionHdService depositDeductionHdService;

    @Autowired
    private HlsCusFctContractMapper fctContractMapper;

    @Autowired
    private HlsCusConContractMapper conContractMapper;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }


    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = getProcessInstanceCreateRequest((HlsCusDepositDeductionHd) list.get(0), iRequest);

        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    private ProcessInstanceCreateRequest getProcessInstanceCreateRequest(HlsCusDepositDeductionHd depositDeductionHd, IRequest iRequest) {
        ProcessInstanceCreateRequest createRequest = new ProcessInstanceCreateRequest();
        ReProcdef reProcdefs = reProcdefService.queryReProcdef("CM_DEPOSIT_DEDUCTION_WFL", "CM_DEPOSIT_DEDUCTION_WFL");
        SysUser sysUser = sysUserService.selectUserById(iRequest.getUserId());
        String id = reProcdefs.getId_();
        String name = reProcdefs.getName_();
        createRequest.setProcessDefinitionId(id);
        createRequest.setBusinessKey(depositDeductionHd.getDepositDeductionHdId().toString());
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
        restVariable5.setName("depositDeductionHd");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(depositDeductionHd));
        restVariable5.setValue(jsonObject.toString());
        variables.add(restVariable5);
        RestVariable restVariable6 = new RestVariable();
        restVariable6.setName("documentCategory");
        restVariable6.setValue(depositDeductionHd.getDeductionDocCategory());
        variables.add(restVariable6);
        RestVariable restVariable7 = new RestVariable();
        restVariable7.setName("documentType");
        restVariable7.setValue(depositDeductionHd.getDeductionType());
        variables.add(restVariable7);
        RestVariable restVariable8 = new RestVariable();
        restVariable8.setName("documentId");
        restVariable8.setValue(depositDeductionHd.getDepositDeductionHdId());
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
        if ("CON_CONTRACT".equals(depositDeductionHd.getDeductionDocCategory())) {
            HlsCusConContract conContract = conContractMapper.selectByPrimaryKey(depositDeductionHd.getContractId());
            restVariable12.setValue(conContract.getContractNumber() + conContract.getContractName());
        }
        if ("FCT_CONTRACT".equals(depositDeductionHd.getDeductionDocCategory())) {
            HlsCusFctContract fctContract = fctContractMapper.selectByPrimaryKey(depositDeductionHd.getContractId());
            restVariable12.setValue(fctContract.getContractNumber() + fctContract.getContractName());
        }
        variables.add(restVariable12);

        Map<String, Long> unitAndCompany = depositDeductionHdService.selectProjectUnitAndCompany(depositDeductionHd.getDeductionDocCategory(), depositDeductionHd.getContractId());
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
        Long id = Long.parseLong(businessKey);
        HlsCusDepositDeductionHd depositDeductionHd = new HlsCusDepositDeductionHd();
        depositDeductionHd.setDepositDeductionHdId(id);
        depositDeductionHd.setDepositStatus("NEW");
        depositDeductionHdService.updateByPrimaryKeySelective(iRequest, depositDeductionHd);
    }
}
