package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.GENER.dto.HlsGeneralIssue;
import com.hand.hls.GENER.mapper.HlsGeneralIssueMapper;
import com.hand.hls.GENER.service.IHlsGeneralIssueService;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bill.service.IhlsBillRequestService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusGeneralIssueSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsGeneralIssueMapper hlsgeneralissuemapper;
    @Autowired
    private IHlsGeneralIssueService hlsgeneralissueservice;
    public HlsCusGeneralIssueSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String generalId = delegateExecution.getProcessInstanceBusinessKey();

        HlsGeneralIssue hlsgeneralissue = hlsgeneralissuemapper.selectByPrimaryKey(generalId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            hlsgeneralissue.setStatus("APPROVED");
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            hlsgeneralissue.setStatus("REJECTED");
        }
        hlsgeneralissueservice.updateByPrimaryKeySelective(requestCtx, hlsgeneralissue);
    }

}
