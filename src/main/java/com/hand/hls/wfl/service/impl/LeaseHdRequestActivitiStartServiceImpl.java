package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.pam.mapper.LeaseAssetHdMapper;
import com.hand.hls.pam.service.ILeaseAssetHdService;
import com.hand.hls.pam.service.impl.LeaseAssetHdServiceImpl;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author kalvin
 * @version 1.0
 * @Date 2021/3/18 9:23
 * @Description
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaseHdRequestActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "PROPERTY_EVALUATE_WFL";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private LeaseAssetHdMapper leaseAssetHdMapper;
    @Autowired
    private ILeaseAssetHdService leaseAssetHdService;


    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);

        // 回写工作流实例ID
        Long billId = (Long) params.get(IActivitiCommonService.BUSINESS_KEY);
        LeaseAssetHd leaseAssetHd = new LeaseAssetHd();
        leaseAssetHd.setAssetHdId(billId);

        leaseAssetHd.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        leaseAssetHdService.updateByPrimaryKeySelective(iRequest,leaseAssetHd);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        LeaseAssetHd leaseAssetHd = new LeaseAssetHd();
        leaseAssetHd.setAssetHdId(id);
        leaseAssetHd.setAssetStatus(CANCEL_STATUS);
        leaseAssetHdService.updateByPrimaryKeySelective(iRequest, leaseAssetHd);
    }
}
