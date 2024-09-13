package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description
 * @author dql
 * @date 2024/9/13 16:40:59
 */
@Service
public class HlsConFactoringLoanActivitiServiceImpl implements IActivitiCommonService {

    private static final String workFlowType = "FACTORING_BUSINESS_LOAN";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Autowired
    private HlsCusConContractService hlsCusConContractService;


    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(((HlsCusConContract) list.get(0)).getContractId());
        contract.setContractStatus("APPROVING");
        contract.setLoanProcInstatnce(Long.valueOf(processInstanceResponse.getId()));
        hlsCusConContractService.updateByPrimaryKeySelective(iRequest,contract);
    }


    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(Long.parseLong(businessKey));
        contract.setContractStatus("CANCEL");
        hlsCusConContractService.updateByPrimaryKeySelective(iRequest,contract);
    }
}
