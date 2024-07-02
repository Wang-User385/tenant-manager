package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.activiti.service.IActivitiService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContractChangeReq;
import com.hand.hls.cont.service.IConContractChangeReqService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.NoSuchObjectException;

import static com.hand.hls.utils.HlsCusConstant.WORKFLOW_PARAMS.APPROVE_RESULT;
import static com.hand.hls.utils.HlsCusConstant.WORKFLOW_PARAMS.IREQUEST;
import static com.hand.hls.utils.HlsCusConstant.WORKFLOW_STATUS.APPROVED;


/**
 * @Description：大单提前部分还本审批流程结束监听器
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/22 14:42
 * @Version：1.0
 */
@Component
public class ConChangePartialPrepaymentServiceTask implements JavaDelegate, IActivitiBean {

    Logger logger = LoggerFactory.getLogger(ConChangePartialPrepaymentServiceTask.class);

    @Autowired
    private IConContractChangeReqService contractChangeReqService;

    @Autowired
    private IActivitiService activitiService;


    public ConChangePartialPrepaymentServiceTask(){

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution delegateExecution) {
        logger.info("--------大单提前部分还本审批流程 结束监听:ConChangePartialPrepaymentServiceTask--------");

        IRequest requestCtx = (IRequest) delegateExecution.getVariable(IREQUEST);
        String result = (String) delegateExecution.getVariable(APPROVE_RESULT);
        String userId = String.valueOf(delegateExecution.getVariable("startUserId"));
        String contractChangeReqStr = (String) delegateExecution.getVariable("contractChangeReq");
        HlsCusConContractChangeReq changeReq = JSON.parseObject(contractChangeReqStr, HlsCusConContractChangeReq.class);

        requestCtx.setAttribute("authorityRuleFlag", "N");

        //TODO 审批结束逻辑
        try {
            contractChangeReqService.changeReqPartialPrepaymentApproved(requestCtx, result, changeReq, delegateExecution.getProcessInstanceId());
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        }
        try {
            activitiService.contractChangeEndDataTransferTask(result,changeReq.getChangeReqId(),"CONTRACT_CHANGE");
        }catch (Exception e) {
            logger.info("迁移变更数据异常，{}",e.getMessage());
        }
    }

}
