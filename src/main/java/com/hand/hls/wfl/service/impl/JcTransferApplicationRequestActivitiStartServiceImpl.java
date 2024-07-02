package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.mapper.LitigationManagementMapper;
import com.hand.hls.avs.service.ILitigationManagementService;
import com.hand.hls.taa.dto.JcTransferApplication;
import com.hand.hls.taa.mapper.JcTransferApplicationMapper;
import com.hand.hls.taa.service.IJcTransferApplicationService;
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
public class JcTransferApplicationRequestActivitiStartServiceImpl implements IActivitiCommonService {

    @Autowired
    private JcTransferApplicationMapper jcTransferApplicationMapper;
    @Autowired
    private IJcTransferApplicationService jcTransferApplicationService;
    private static final String WORK_FLOW_TYPE = "TRANSFER_APPLICATION_WFL";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;



    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);

        // 回写工作流实例ID
        Long transferApplicationId = (Long) params.get(IActivitiCommonService.BUSINESS_KEY);
        JcTransferApplication jcTransferApplication = new JcTransferApplication();
        jcTransferApplication.setTransferApplicationId(transferApplicationId);
        jcTransferApplication.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        jcTransferApplicationService.updateByPrimaryKeySelective(iRequest,jcTransferApplication);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        JcTransferApplication jcTransferApplication = new JcTransferApplication();
        jcTransferApplication.setTransferApplicationId(id);
        jcTransferApplication.setStatus(CANCEL_STATUS);
        jcTransferApplicationService.updateByPrimaryKeySelective(iRequest, jcTransferApplication);
    }
}
