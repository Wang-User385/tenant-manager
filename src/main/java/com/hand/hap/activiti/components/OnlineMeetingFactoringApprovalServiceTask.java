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

import java.util.List;

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
    private HlsCreditLineChanceApproverMapper hlsCreditLineChanceApproverMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String result = (String) delegateExecution.getVariable("approveResult");
        if (!StringUtils.isEmpty(result)) {
            Long chanceId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
            HlsCreditLineChanceApprover chanceApprover = new HlsCreditLineChanceApprover();
            chanceApprover.setChanceId(chanceId);
            List<HlsCreditLineChanceApprover> hlsCreditLineChanceApproverList = hlsCreditLineChanceApproverMapper.findVoteInfo(chanceApprover);
            //投票最多的是通过
            int pass = 0;
            int noPass = 0;
            int condition = 0;
            String voteStatus = "PASS";
            for (HlsCreditLineChanceApprover v : hlsCreditLineChanceApproverList) {
                if ("10".equals(v.getVoteStatus())) {
                    pass++;
                } else if ("20".equals(v.getVoteStatus())) {
                    noPass++;
                } else if ("30".equals(v.getVoteStatus())) {
                    condition++;
                }
            }
            int max = Math.max(Math.max(pass, noPass), condition);
            voteStatus = max == pass ? "PASS" :
                    max == noPass ? "NO_PASS" : "CONDITION";
            delegateExecution.setVariable("voteStatus", voteStatus);
        }
    }
}
