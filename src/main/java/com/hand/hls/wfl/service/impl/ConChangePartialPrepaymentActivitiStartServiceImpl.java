package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
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
 * @Description：大单提前部分还本审批流程发起实现类
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/22 14:23
 * @Version：1.0
 */
@Service
public class ConChangePartialPrepaymentActivitiStartServiceImpl implements IActivitiCommonService {

    /**
     * 用于代码获取该实现类
     */
    private static final String WORK_FLOW_TYPE = "CON_PARTIAL_PREPAYMENT_WFL";

    /**
     * 对应工作流引擎-流程设计页面配置中的流程编码
     */
    private static final String WORK_FLOW_KEY = "CON_PARTIAL_PREPAYMENT_WFL";
    /**
     * 对应工作流页面配置的分类
     */
    private static final String DEMO = "CON_CONTRACT";

    private static final String BUSINESS_KEY = "BUSINESS_KEY";

    private static final String business_key = "businessKey";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(IRequest iRequest, List list, Map params) {
        //TODO 设置业务主键值
        //params.put(BUSINESS_KEY, null);
        params.put(WORK_FLOW_NAME, WORK_FLOW_KEY);
        params.put(DEMO_NAME, DEMO);

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);

        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(IRequest iRequest, Map params) {

    }
}
