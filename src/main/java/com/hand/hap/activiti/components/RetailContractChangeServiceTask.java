package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContractChangeReq;
import com.hand.hls.cont.service.IConContractChangeReqService;
import com.hand.hls.cont.service.IConContractService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 零售合同变更工作流结束任务监听器
 */
@Component
public class RetailContractChangeServiceTask implements JavaDelegate, IActivitiBean {

    private static final Logger logger = LoggerFactory.getLogger(RetailContractChangeServiceTask.class);
    public static final String APPROVED = "APPROVED";
    private static final String DOCUMENT_CATEGORY_CON = "CONTRACT_CHANGE";

    @Autowired
    private IConContractService contractService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private IConContractChangeReqService contractChangeReqService;
    @Autowired
    private IActivitiService activitiService;

    public RetailContractChangeServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
//        String flag;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String contractChangeReq = (String) delegateExecution.getVariable("contractChangeReq");
        HlsCusConContractChangeReq changeReq = JSON.parseObject(contractChangeReq, HlsCusConContractChangeReq.class);

        requestCtx.setAttribute("authorityRuleFlag", "N");

        Long contractId = changeReq.getContractId();
        String changeType = changeReq.getChangeType();
        Long changeReqId = changeReq.getChangeReqId();

        String depositDeductFlag = contractChangeReqService.contractChange(requestCtx, result, changeReq, delegateExecution.getProcessInstanceId());

        if (APPROVED.equalsIgnoreCase(result)) {
            taskExecutor.execute(() -> {
                contractChangeReqService.dealInterfaceWithContractChangeApproved(requestCtx, contractId, changeType, depositDeductFlag, changeReqId);
            });
        }
        try {
            activitiService.contractChangeEndDataTransferTask(result,changeReqId,DOCUMENT_CATEGORY_CON);
        }catch (Exception e) {
            logger.info("迁移变更数据异常，{}",e.getMessage());
        }
    }
}
