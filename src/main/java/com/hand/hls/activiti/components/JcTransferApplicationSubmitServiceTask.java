package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.avs.dto.LitigationManagement;
import com.hand.hls.avs.mapper.LitigationManagementMapper;
import com.hand.hls.avs.service.ILitigationManagementService;
import com.hand.hls.taa.dto.JcTransferApplication;
import com.hand.hls.taa.mapper.JcTransferApplicationMapper;
import com.hand.hls.taa.service.IJcTransferApplicationService;
import com.hand.hls.user.service.LoginUserInfoService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class JcTransferApplicationSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private JcTransferApplicationMapper jcTransferApplicationMapper;
    @Autowired
    private IJcTransferApplicationService jcTransferApplicationService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;

    @Autowired
    private SysEventService sysEventService;
    public JcTransferApplicationSubmitServiceTask() {
    }
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String transferApplicationId = delegateExecution.getProcessInstanceBusinessKey();

        JcTransferApplication jcTransferApplication = jcTransferApplicationMapper.selectByPrimaryKey(transferApplicationId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            jcTransferApplication.setStatus("APPROVED");
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            jcTransferApplication.setStatus("REJECTED");
        }
        jcTransferApplicationService.updateByPrimaryKeySelective(requestCtx, jcTransferApplication);
    }

}
