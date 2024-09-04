package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 保理理想审批终止
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/9/4 17:17
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsFactoringApprovalTerminateServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";

    //点对点提交
    private static final String PEER_REJECTED = "PEER_REJECTED";

    //移交
    private static final String DELEGATE = "DELEGATE";

    //终止
    private static final String TERMINATE = "TERMINATE";

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        //Long processInstanceId = Long.parseLong(delegateExecution.getProcessInstanceId());
        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, prjProject);
        if (isValid(prjProject)) {
            if (APPROVED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(APPROVED);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(TERMINATE);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            } else if (PEER_REJECTED.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(PEER_REJECTED);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            } else if (DELEGATE.equalsIgnoreCase(result)) {
                prjProject.setProjectStatus(DELEGATE);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);
            }
        }
    }

    private boolean isValid(HlsCusPrjProject prjProject) {
        return !APPROVED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !REJECTED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !PEER_REJECTED.equalsIgnoreCase(prjProject.getProjectStatus()) &&
                !DELEGATE.equalsIgnoreCase(prjProject.getProjectStatus());
    }

}
