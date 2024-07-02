package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cap.dto.HlsCusCapitalInvestmentPlanHd;
import com.hand.hls.cap.service.HlsCusCapitalInvestmentPlanHdService;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.apache.commons.collections.CollectionUtils;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusCapInvestmentPlanActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "FUNDING";
    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private HlsCusCapitalInvestmentPlanHdService hlsCusCapitalInvestmentPlanHdService;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        HlsCusCapitalInvestmentPlanHd hlsCusCapitalInvestmentPlanHd = (HlsCusCapitalInvestmentPlanHd)list.get(0);
        if (CollectionUtils.isNotEmpty(list) && list.get(0) instanceof HlsCusCapitalInvestmentPlanHd) {
            params.put("BUSINESS_KEY", hlsCusCapitalInvestmentPlanHd.getPlanHeadId());
        }

        params.put("WORK_FLOW", "FUNDING_PLAN_WFL");
        params.put("DEMO", "FUNDING_PLAN_WFL");
        params.put("documentCategory", hlsCusCapitalInvestmentPlanHd.getDocumentCategory());
        params.put("documentType", hlsCusCapitalInvestmentPlanHd.getDocumentType());
        params.put("documentNumber", hlsCusCapitalInvestmentPlanHd.getPlanNumber());
        params.put("documentName", hlsCusCapitalInvestmentPlanHd.getPlanName());
        params.put("business_key", hlsCusCapitalInvestmentPlanHd.getPlanHeadId());
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        if (processInstanceResponse.getId() != null) {
            hlsCusCapitalInvestmentPlanHd.setProcessInstanceId(Long.parseLong(processInstanceResponse.getId()));
            hlsCusCapitalInvestmentPlanHdService.updateByPrimaryKey(iRequest, hlsCusCapitalInvestmentPlanHd);
        }
    }


    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long planHeadId = Long.parseLong(businessKey);

        HlsCusCapitalInvestmentPlanHd hlsCusCapitalInvestmentPlanHd = new HlsCusCapitalInvestmentPlanHd();
        hlsCusCapitalInvestmentPlanHd.setApproveStatus("CANCEL");
        hlsCusCapitalInvestmentPlanHd.setPlanHeadId(planHeadId);
        //审批状态为新建
        hlsCusCapitalInvestmentPlanHdService.updateByPrimaryKeySelective(iRequest,hlsCusCapitalInvestmentPlanHd);
    }
}
