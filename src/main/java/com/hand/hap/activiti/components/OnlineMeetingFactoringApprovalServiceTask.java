package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.fct.dto.HlsCreditLineChanceApprover;
import com.hand.hls.fct.mapper.HlsCreditLineChanceApproverMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.axis.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/9/3 16:01
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class OnlineMeetingFactoringApprovalServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private HlsCusHlsCreditLineChanceMapper chanceMapper;
    @Autowired
    private HlsCreditLineChanceApproverMapper approverMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String result = (String) delegateExecution.getVariable("approveResult");
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
