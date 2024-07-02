package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
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
 * @Description：二期功能：付款申请发起工作流实现类
 * @Author：liangxian.chen@hand-china.com
 * @Date：2022/11/29 15:24
 * @Version：1.0
 */
@Service
public class CshPaymentReqActivitiStartImpl implements IActivitiCommonService {
    /**
     * 用于代码获取该实现类
     */
    private static final String workFlowType = "PAYMENT_APPLICATION_WFL";

    /**
     * 对应工作流引擎-流程设计页面配置中的流程编码
     */
    private static final String WORK_FLOW_KEY = "PAYMENT_APPLICATION_WFL";
    /**
     * 对应工作流页面配置的分类
     */
    private static final String DEMO = "PAYMENT";

    private static final String BUSINESS_KEY = "BUSINESS_KEY";

    private static final String business_key = "businessKey";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private CshPaymentReqHdService cshPaymentReqHdService;



    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(IRequest iRequest, List list, Map params) {
        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = (HlsCusCshPaymentReqHd) list.get(0);
        params.put(BUSINESS_KEY, hlsCusCshPaymentReqHd.getPaymentReqId());
        params.put(WORK_FLOW_NAME, WORK_FLOW_KEY);
        params.put(DEMO_NAME, DEMO);

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, params);

        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get(business_key);
        long id = Long.parseLong(businessKey);
        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(id);
        cshPaymentReqHd.setPaymentReqStatus("BACK");
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, cshPaymentReqHd);

    }
}
