package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/8/30 9:28
 */
@Component
public class HlsFactoringServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";

    //移交
    private static final String DELEGATE = "DELEGATE";

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        //Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(chanceId);
        chance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(requestCtx, chance);
        if (isValid(chance)) {
            if (APPROVED.equalsIgnoreCase(result)) {
                chance.setCreditLineStatus(APPROVED);
                hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                chance.setCreditLineStatus(REJECTED);
                hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);
            } else if (PEER_REJECTED.equalsIgnoreCase(chance.getCreditLineStatus())) {
                chance.setCreditLineStatus(PEER_REJECTED);
                hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);
            } else if (DELEGATE.equalsIgnoreCase(chance.getCreditLineStatus())) {
                chance.setCreditLineStatus(DELEGATE);
                hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, chance);
            }
        }
    }

    private boolean isValid(HlsCusHlsCreditLineChance chance) {
        return !APPROVED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !PEER_REJECTED.equalsIgnoreCase(chance.getCreditLineStatus()) &&
                !DELEGATE.equalsIgnoreCase(chance.getCreditLineStatus());
    }

}
