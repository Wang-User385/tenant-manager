package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional(rollbackFor = Exception.class)
public class PrjReviewRiskSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        String assignee = (String) delegateExecution.getVariables().get("assignee");
        if(StringUtils.isNotEmpty(assignee)){
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(projectId);
            hlsCusPrjProject.setRiskHost(Long.parseLong(assignee));
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);
        }
    }

}