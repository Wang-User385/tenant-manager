package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;

import com.hand.hls.csh.dto.HlsCusCshPaymentReqHd;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.csh.mapper.ProjectCreditConditionMapper;
import com.hand.hls.csh.service.ICshPaymentReqHdService;

import com.hand.hls.utils.ResMessageException;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Yenick
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractLoanAuditSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;

    @Autowired
    private ProjectCreditConditionMapper projectCreditConditionMapper;





    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");

        HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
        hlsCusCshPaymentReqHd.setPaymentReqId(Long.parseLong(delegateExecution.getProcessInstanceBusinessKey()));
        databaseLockProvider.lock(hlsCusCshPaymentReqHd);
        hlsCusCshPaymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(requestCtx, hlsCusCshPaymentReqHd);

        //更新支付表状态

        if ("APPROVED".equalsIgnoreCase(result)) {

            ProjectCreditCondition projectCreditCondition = new ProjectCreditCondition();

            projectCreditCondition.setProjectId(hlsCusCshPaymentReqHd.getPaymentReqId());
            List<ProjectCreditCondition> projectCreditConditions = projectCreditConditionMapper.queryByPrj(projectCreditCondition);
            for (int i = 0; i < projectCreditConditions.size(); i++) {
                if(!"Y".equals(projectCreditConditions.get(i).getImplementationLease()) && !"Y".equals(projectCreditConditions.get(i).getIfPracticable())){
                    throw new ResMessageException("付款前提条件，需勾选已落实!");

                }
            }





        }






    }
}