package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.csh.service.CshTransactionRefundService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.wfl.components.WflGetProcessInstanceComponents;
import com.hand.hls.wfl.service.IActivitiCommonService;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.activiti.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description
 * @author dql
 * @date 2024/8/30 10:13:39
 */
@Service
public class TransactionRefundPaymentActivitiStartServiceImpl implements IActivitiCommonService {
    private static final String WORK_FLOW_TYPE = "REFUND_PAYMENT_WORK_FLOW";

    @Autowired
    private IActivitiService activitiService;
    @Autowired
    WflGetProcessInstanceComponents wflGetProcessInstanceComponents;
    @Autowired
    private CshTransactionRefundService cshTransactionRefundService;
    @Override
    public String getWorkFlowType() {
        return WORK_FLOW_TYPE;
    }

    @Override
    public void process(IRequest iRequest, List list, Map map) {
        ProcessInstanceCreateRequest processInstanceCreateRequest = wflGetProcessInstanceComponents.getProcessInstance(iRequest, map);

        ProcessInstanceResponse processInstanceResponse = activitiService.startProcess(iRequest, processInstanceCreateRequest);
        // 回写工作流实例ID到项目退款头表并更新工作流状态
        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund.setProcessInstanceId(Long.valueOf(processInstanceResponse.getId()));
        transactionRefund.setRefundId(((HlsCusCshTransactionRefund) list.get(0)).getRefundId());
        transactionRefund.setRefundStatus("APPROVING");
        cshTransactionRefundService.updateByPrimaryKeySelective(iRequest, transactionRefund);
    }

    @Override
    public void cancel(IRequest iRequest, Map params) {
        String businessKey = (String) params.get("businessKey");
        long id = Long.parseLong(businessKey);
        HlsCusCshTransactionRefund transactionRefund = new HlsCusCshTransactionRefund();
        transactionRefund.setRefundId(id);
        transactionRefund.setRefundStatus(HlsCusConstant.WORKFLOW_STATUS.CANCEL);
        cshTransactionRefundService.updateByPrimaryKeySelective(iRequest, transactionRefund);
    }
}
