package com.hand.hap.activiti.components;


import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCreditLineChanceApproverMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
public class ProjectMeetingVoteStatusServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceMapper chanceMapper;
    @Autowired
    private HlsCreditLineChanceApproverMapper approverMapper;
    @Autowired
    private HlsCusPrjProjectMapper projectMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void execute(DelegateExecution delegateExecution) {
        String result = (String) delegateExecution.getVariable("approveResult");
        if(!StringUtils.isEmpty(result)){
            Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
            HlsCusPrjProject prjProject = new HlsCusPrjProject();
            prjProject.setChanceId(projectId);
            List<HlsCusPrjProject> hlsCusPrjProjects = projectMapper.queryProjectById(prjProject);
            if (CollectionUtils.isNotEmpty(hlsCusPrjProjects)) {
                delegateExecution.setVariable("voteResult", prjProject.getVotingResult());
            } else {

                logger.warn("No HlsCusHlsCreditLineChance found for projectId: " + projectId);
            }
        }


    }
}
