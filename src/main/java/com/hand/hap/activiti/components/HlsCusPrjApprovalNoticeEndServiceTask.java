package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/12 19:57
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjApprovalNoticeEndServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED = "APPROVED";
    private static final String APPROVED_RETURN = "APPROVED_RETURN";
    private static final String REJECTED = "REJECTED";
    private static final String SUSPEND = "SUSPEND";


    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        prjProject = hlsCusPrjProjectMapper.selectByPrimaryKey(prjProject);
        databaseLockProvider.lock(prjProject);

        if (APPROVED.equalsIgnoreCase(result)) {

            prjProject.setProjectStatus("NOTICE_APPROVED");
            prjProject.setApprovalStatus("MEETED");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);

        } else if (APPROVED_RETURN.equalsIgnoreCase(result)) {

            prjProject.setProjectStatus("NOTICE_VETO");
            prjProject.setApprovalStatus("VOTED");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);


        } else if (REJECTED.equalsIgnoreCase(result)) {

            prjProject.setProjectStatus("NOTICE_VETO");
            prjProject.setApprovalStatus("VOTED");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);

        }else if (SUSPEND.equalsIgnoreCase(result)) {

            prjProject.setProjectStatus("NOTICE_HOLD");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProject);

        }

    }

}
