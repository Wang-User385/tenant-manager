package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <p>
 *车辆抵押结束监听器
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/24 9:30
 */
@Component
public class HlsMortgageServiceTask implements JavaDelegate, IActivitiBean {
    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    @Autowired
    private HlsCusConContractService hlsCusConContractService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private IYLMessageNoticeService iylMessageNoticeService;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = "";
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long contractId = (Long) delegateExecution.getVariable("contractId");

        Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsCusConContract conContract = new HlsCusConContract();
        conContract.setContractId(contractId);
        conContract = hlsCusConContractService.selectByPrimaryKey(requestCtx, conContract);
        if (!APPROVED.equalsIgnoreCase(conContract.getMortgageStatus()) && !REJECTED.equalsIgnoreCase(conContract.getMortgageStatus())){
            conContract.setMortgageInstanceId(processInstanceId);
            if (APPROVED.equalsIgnoreCase(result)) {
                conContract.setMortgageApprovedDate(new Date());
                flag = "APPROVED";
            } else if (REJECTED.equalsIgnoreCase(result)) {
                flag = "REJECTED";
            }
            conContract.setMortgageStatus(flag);
            hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, conContract);
            //调用车辆审核通知接口
            iylMessageNoticeService.orderAuditResult(contractId,"MORTGAGE_MATERIAL_AUDIT",requestCtx);
        }
    }
}
