package com.hand.hap.activiti.components;


import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCreditLineChanceApproverMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import jodd.util.StringUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.axis.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class OnlineMeetingServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceMapper chanceMapper;
    @Autowired
    private HlsCreditLineChanceApproverMapper approverMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String result = (String) delegateExecution.getVariable("approveResult");
        if(!StringUtils.isEmpty(result)){
            Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
            HlsCusHlsCreditLineChance lineChance = new HlsCusHlsCreditLineChance();
            lineChance.setChanceId(chanceId);
            lineChance = chanceMapper.selectCreditLineChanceById(lineChance);
            delegateExecution.setVariable("voteStatus",lineChance.getVotingResult());
        }


    }
}
