package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.IProjectMeetingApproverService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * description 评委会委员表决
 *
 * @author MengXi Liu 2021年12月2日
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectRiskAppointServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private IProjectMeetingApproverService approverService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    public HlsCusPrjProjectRiskAppointServiceTask() {

    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        //从流程变量中取出allocationIdRisk设置进prj_project表risk_allocation_id字段
        //获取当前的审批意见, projectId
        Long allocationIdRisk = (Long) delegateExecution.getVariables().get("allocationIdRisk");
        Long projectId = (Long) delegateExecution.getVariables().get("projectId");

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject.setRiskAllocationId(allocationIdRisk);
        hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProject);

        //设置一个指定风险岗跳过标识
        delegateExecution.setVariable("riskAppointSkip", "Y");
    }
}
