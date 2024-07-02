package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.abs.dto.HlsCusAbsInformationDisclosure;
import com.hand.hls.abs.service.HlsCusAbsInformationDisclosureService;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @Description: 信息披露审批工作流结束监听
 * @author: congweijing
 * @date: 2021/6/28 10:08
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusInformationDisclosureSubmitServiceTask implements JavaDelegate, IActivitiBean ,IHlsCusActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusAbsInformationDisclosureService hlsCusAbsInformationDisclosureService;
    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";
    /**
     * 结束监听
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        String result = (String) delegateExecution.getVariable("approveResult");
        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());


        HlsCusAbsInformationDisclosure hlsCusAbsInformationDisclosure = new HlsCusAbsInformationDisclosure();
        String disclosureId = delegateExecution.getProcessInstanceBusinessKey();
        hlsCusAbsInformationDisclosure.setDisclosureId(Long.parseLong(disclosureId));
        databaseLockProvider.lock(hlsCusAbsInformationDisclosure);

        hlsCusAbsInformationDisclosure.setApprovalStatus(result);
        hlsCusAbsInformationDisclosureService.updateByPrimaryKeySelective(requestCtx,hlsCusAbsInformationDisclosure);

    }

    /**
     * 一键通过/一键拒绝
     * @param iRequest
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {
        HlsCusAbsInformationDisclosure hlsCusAbsInformationDisclosure = new HlsCusAbsInformationDisclosure();
        String disclosureId = hlsCusProcess.getBussinessKey();
        Long processInstanceId = Long.parseLong(hlsCusProcess.getProcessMap().get(0).get("proc_inst_id_").toString());

        hlsCusAbsInformationDisclosure.setDisclosureId(Long.parseLong(disclosureId));
        databaseLockProvider.lock(hlsCusAbsInformationDisclosure);

        if (PASS.equals(hlsCusProcess.getType())) {
            hlsCusAbsInformationDisclosure.setApprovalStatus("APPROVED");
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            hlsCusAbsInformationDisclosure.setApprovalStatus("REJECTED");
        }
        hlsCusAbsInformationDisclosureService.updateByPrimaryKeySelective(iRequest,hlsCusAbsInformationDisclosure);
    }
}
