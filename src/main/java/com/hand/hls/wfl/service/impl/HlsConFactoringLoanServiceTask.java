package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description
 * @author dql
 * @date 2024/9/13 16:51:05
 */
@Service
public class HlsConFactoringLoanServiceTask implements JavaDelegate, IActivitiBean {
    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long contractId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusConContract hlsCusConContract = new HlsCusConContract();
        hlsCusConContract.setContractId(contractId);
        hlsCusConContract = hlsCusConContractService.selectByPrimaryKey(requestCtx, hlsCusConContract);
        if (!APPROVED.equalsIgnoreCase(hlsCusConContract.getContractStatus()) && !REJECTED.equalsIgnoreCase(hlsCusConContract.getContractStatus())) {
            if (APPROVED.equalsIgnoreCase(result)) {
                hlsCusConContract.setLoanApprovedDate(new Date());
                hlsCusConContract.setContractStatus(APPROVED);
                hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContract);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                hlsCusConContract.setLoanApprovedDate(new Date());
                hlsCusConContract.setContractStatus(REJECTED);
                hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContract);
            }
        }
    }
}