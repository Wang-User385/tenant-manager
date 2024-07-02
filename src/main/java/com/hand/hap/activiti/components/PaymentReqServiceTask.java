package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description：二期功能：付款申请工作流结束监听器
 * @Author：liangxian.chen@hand-china.com
 * @Date：2022/11/29 14:54
 * @Version：1.0
 */
@Component
public class PaymentReqServiceTask  implements JavaDelegate, IActivitiBean {

    Logger logger = LoggerFactory.getLogger(PaymentReqServiceTask.class);

    private static final String PROJECT = "project";

    private static final String IREQUEST = "iRequest";
    private static final String APPROVE_RESULT = "approveResult";
    private static final String START_USER_ID = "startUserId";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    @Autowired
    private CshPaymentReqHdService paymentReqHdService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(DelegateExecution execution) {
        logger.info("--------付款申请工作流 结束监听:PaymentReqServiceTask--------");

        IRequest requestCtx = (IRequest) execution.getVariable(IREQUEST);
        String result = (String) execution.getVariable(APPROVE_RESULT);
        String userId = String.valueOf(execution.getVariable(START_USER_ID));
        Long paymentReqId = (Long) execution.getVariable("paymentReqId");
        requestCtx.setUserId(Long.valueOf(userId));

        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(paymentReqId);
        paymentReqHdService.selectByPrimaryKey(requestCtx, cshPaymentReqHd);
        if (APPROVED.equalsIgnoreCase(result)){
            cshPaymentReqHd.setPaymentReqStatus(APPROVED);
        }else if (REJECTED.equalsIgnoreCase(result)){
            cshPaymentReqHd.setPaymentReqStatus(REJECTED);
        }
        paymentReqHdService.updateByPrimaryKeySelective(requestCtx, cshPaymentReqHd);
    }
}
