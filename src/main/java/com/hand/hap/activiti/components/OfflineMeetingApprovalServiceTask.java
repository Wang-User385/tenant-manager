package com.hand.hap.activiti.components;


import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCreditLineChanceApproverMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.axis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class OfflineMeetingApprovalServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceMapper chanceMapper;
    @Autowired
    private HlsCreditLineChanceApproverMapper approverMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void execute(DelegateExecution delegateExecution) {
        String result = (String) delegateExecution.getVariable("approveResult");
        if(!StringUtils.isEmpty(result)){
            Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
            HlsCusHlsCreditLineChance lineChance = new HlsCusHlsCreditLineChance();
            lineChance.setChanceId(chanceId);
            lineChance = chanceMapper.selectCreditLineChanceById(lineChance);
            if (lineChance != null) {
                delegateExecution.setVariable("voteStatus", lineChance.getVotingResult());
            } else {

                logger.warn("No HlsCusHlsCreditLineChance found for chanceId: " + chanceId);
            }
        }


    }
}
