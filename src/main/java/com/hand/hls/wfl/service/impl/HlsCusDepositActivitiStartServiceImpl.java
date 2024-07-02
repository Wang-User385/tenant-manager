package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.hls.dto.HlsDurationDeposit;
import com.hand.hls.hls.service.HlsDurationDepositService;
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
 * description 保证金执行工作流
 *
 * @author Eugene Song 2020年6月11日
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositActivitiStartServiceImpl implements IActivitiCommonService {


    private static final String workFlowType = "DEPOSIT_WFL";

    @Autowired
    private IActivitiService activitiService;

    @Autowired
    private HlsDurationDepositService service;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        HlsDurationDeposit deposit = new HlsDurationDeposit();
        deposit.setSourceId(Long.parseLong(businessKey));
        deposit.setExecuteStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
        service.updateByPrimaryKeySelective(iRequest, deposit);
    }
}
