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
//        if ("APPROVED".equalsIgnoreCase(result)) {
//            Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
//            HlsCusHlsCreditLineChance chance = chanceMapper.selectByPrimaryKey(chanceId);
//            String meetingType = chance.getMeetingType();
//            delegateExecution.setVariable("meetingType",meetingType);
//        }
        if(!StringUtils.isEmpty(result)){
            Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
            HlsCreditLineChanceApprover chanceApprover = new HlsCreditLineChanceApprover();
            chanceApprover.setChanceId(chanceId);
            HlsCreditLineChanceApprover approver = approverMapper.selectOne(chanceApprover);
            String voteStatus = approver.getVoteStatus();
            delegateExecution.setVariable("voteStatus",voteStatus);
        }


    }
}
