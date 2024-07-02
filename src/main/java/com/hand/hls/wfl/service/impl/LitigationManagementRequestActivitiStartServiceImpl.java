package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.ast.dto.AstFcEstimate;
import com.hand.hls.ast.service.IAstFcEstimateService;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.mapper.LitigationManagementMapper;
import com.hand.hls.avs.service.ILitigationManagementService;
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
public class LitigationManagementRequestActivitiStartServiceImpl implements IActivitiCommonService {

    @Autowired
    private LitigationManagementMapper litigationManagementMapper;
    @Autowired
    private ILitigationManagementService litigationManagementService;
    private static final String WORK_FLOW_TYPE = "LAWSUITS_MANAGEMENT_WFL";
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
        Long billId = (Long) params.get(IActivitiCommonService.BUSINESS_KEY);
        LitigationManagement litigationManagement = new LitigationManagement();
        litigationManagement.setLitigationManagementId(billId);
        litigationManagement.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        litigationManagementService.updateByPrimaryKeySelective(iRequest,litigationManagement);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        LitigationManagement litigationManagement = new LitigationManagement();
        litigationManagement.setLitigationManagementId(id);
        litigationManagement.setStatus(CANCEL_STATUS);
        litigationManagementService.updateByPrimaryKeySelective(iRequest, litigationManagement);
    }
}
