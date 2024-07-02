package com.hand.hls.wfl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.dto.ReProcdef;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.activiti.service.IReProcdefService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.ContentNumberHead;
import com.hand.hls.cont.dto.HlsCusConFloatingRateReq;
import com.hand.hls.cont.service.HlsCusConFloatingRateReqService;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.service.SysUserService;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Eugene Song on 2019/4/24.
 * 收款退款工作流
 */
@Service
@Transactional
public class HlsCusCshTransacationRefundActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String workFlowType = "CSH_TRANSACTION_REFUND_WFL";
    private static final String DEMO = "CSH_TRANSACTION_REFUND_WFL";
    public static final String APPROVING = "APPROVING";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;

    @Override
    public String getWorkFlowType() {
        return workFlowType;
    }

    @Override
    public void process(IRequest iRequest, List list, Map params) {

        params.put(BUSINESS_KEY, ((HlsCusCshTransactionRefund) list.get(0)).getRefundId());
        params.put(WORK_FLOW_NAME, workFlowType);
        params.put(DEMO_NAME, DEMO);

        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest,params);
        activitiService.startProcess(iRequest, processInstanceCreateRequest);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        String processInstanceId = (String) params.get("processInstanceId");
        long id = Long.parseLong(businessKey);
        long prcId = Long.parseLong(processInstanceId);
        HlsCusCshTransactionRefund cshTransactionRefund = new HlsCusCshTransactionRefund();
        cshTransactionRefund.setRefundId(id);
        cshTransactionRefund.setRefundStatus(CANCEL_STATUS);
        cshTransactionRefundService.updateByPrimaryKeySelective(iRequest,cshTransactionRefund);
    }

}