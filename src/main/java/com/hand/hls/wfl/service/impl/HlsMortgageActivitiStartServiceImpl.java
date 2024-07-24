package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/22 19:42
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsMortgageActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CAR_MORTGAGE";

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
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }


    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(Long.parseLong(businessKey));
        conContract.setMortgageStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
        hlsCusConContractService.updateByPrimaryKeySelective(iRequest,conContract);
    }

}
