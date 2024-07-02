package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bill.dto.hlsBillRequest;
import com.hand.hls.bill.mapper.hlsBillRequestMapper;
import com.hand.hls.bill.service.IhlsBillRequestService;
import com.hand.hls.pam.dto.LeaseAssetHd;
import com.hand.hls.pam.mapper.LeaseAssetHdMapper;
import com.hand.hls.pam.service.ILeaseAssetHdService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kalvin
 * @version 1.0
 * @Date 2021/3/18 10:10
 * @Description
 **/
@Component
@Transactional(rollbackFor = Exception.class)
public class LeaseAssetHdSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private LeaseAssetHdMapper leaseAssetHdMapper;
    @Autowired
    private ILeaseAssetHdService leaseAssetHdService;
    public LeaseAssetHdSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String billId = delegateExecution.getProcessInstanceBusinessKey();

        LeaseAssetHd leaseAssetHd = leaseAssetHdMapper.selectByPrimaryKey(billId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            leaseAssetHd.setAssetStatus("APPROVED");
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            leaseAssetHd.setAssetStatus("REJECTED");
        }else if("STORED".equalsIgnoreCase(result)){
            leaseAssetHd.setAssetStatus("STORED");

        }else{
            leaseAssetHd.setAssetStatus("APPROVED_RETURN");

        }
        leaseAssetHdService.updateByPrimaryKeySelective(requestCtx, leaseAssetHd);
    }

}

