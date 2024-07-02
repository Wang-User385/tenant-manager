package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.service.IContentNumberHeadService;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.service.IProjectCreditConditionService;
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
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/4/28 15:03
 */
@Service
@Transactional
public class HlsCusPrjCreditConditionActivitiStartServiceImpl implements IActivitiCommonService {

    private static final String WORK_FLOW_TYPE = "CSH_PAYMENT_CONDITION_WFL";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private IProjectCreditConditionService service;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);
        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        List<String> creditConditionIds =(List<String>) params.get("creditConditionIds");
        creditConditionIds.forEach(item->{
            Long id = Long.valueOf(item);
            ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();
            projectCreditCondition.setCreditConditionId(id);
            projectCreditCondition.setApprovalFlag(CANCEL_STATUS);
            service.updateByPrimaryKeySelective(iRequest, projectCreditCondition);
        });
    }
}
