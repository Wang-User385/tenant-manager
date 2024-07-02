package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.pam.mapper.LeaseAssetHdMapper;
import com.hand.hls.pam.service.ILeaseAssetHdService;
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
public class HlsScoreCalRequestActivitiStartServiceImpl implements IActivitiCommonService {
    @Autowired
    private IHlsScoreCalculationService hlsScoreCalculationService;
    private static final String WORK_FLOW_TYPE = "CUSTOMER_RATE_WFL";
    @Autowired
    private IActivitiService activitiService;


    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsScoreCalculationMapper hlsScoreCalculationMapper;


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
        HlsScoreCalculation hlsScoreCalculation = new HlsScoreCalculation();
        hlsScoreCalculation.setScoreId(billId);

        hlsScoreCalculation.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        hlsScoreCalculationService.updateByPrimaryKeySelective(iRequest,hlsScoreCalculation);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsScoreCalculation hlsScoreCalculation = new HlsScoreCalculation();
        hlsScoreCalculation.setScoreId(id);
        hlsScoreCalculation.setGradeStatus(CANCEL_STATUS);
        hlsScoreCalculationService.updateByPrimaryKeySelective(iRequest, hlsScoreCalculation);
    }
}
