package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 终止事件
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/8/30 15:45
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringTerminateServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";

    //移交
    private static final String DELEGATE = "DELEGATE";

    //终止
    private static final String TERMINATE = "TERMINATE";

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
                chance.setCreditLineStatus(TERMINATE);
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
