package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.hn.dto.PrjLeaseInspect;
import com.hand.hls.hn.mapper.PrjLeaseInspectMapper;
import com.hand.hls.hn.service.IPrjLeaseInspectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class PrjLeaseInspectSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private PrjLeaseInspectMapper prjLeaseInspectMapper;
    @Autowired
    private IPrjLeaseInspectService prjLeaseInspectService;
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";

    public  PrjLeaseInspectSubmitServiceTask(){

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long prjLeaseId = Long.valueOf(delegateExecution.getProcessInstanceBusinessKey());

        PrjLeaseInspect prjLeaseInspect = prjLeaseInspectMapper.selectByPrimaryKey(prjLeaseId);

        if (APPROVED.equalsIgnoreCase(result)) {
            prjLeaseInspect.setApproveStatus("APPROVED");
        } else if (REJECTED.equalsIgnoreCase(result)) {
            prjLeaseInspect.setApproveStatus("REJECTED");
        }
        prjLeaseInspectService.updateByPrimaryKeySelective(requestCtx, prjLeaseInspect);
    }


}
