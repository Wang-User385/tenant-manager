package com.hand.hls.wfl.service.impl;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.csh.dto.HlsCusCshTransactionRefund;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description
 * @author dql
 * @date 2024/9/5 13:48:39
 */
@Service
public class HlsVirtualConFactoringServiceTask implements JavaDelegate, IActivitiBean {
    private static final String APPROVED = "APPROVED";

    private static final String REJECTED = "REJECTED";
    private static final String TEMPLATE_CODE = "VIRTUAL_CON_TABLE";

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(projectId);
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, hlsCusPrjProject);
        if (!APPROVED.equalsIgnoreCase(hlsCusPrjProject.getContractStatus()) && !REJECTED.equalsIgnoreCase(hlsCusPrjProject.getContractStatus())) {
            if (APPROVED.equalsIgnoreCase(result)) {
                //自动生成合同审查表
                try {
                    HlsCusPrjProject prjProject = new HlsCusPrjProject();
                    prjProject.setProjectId(projectId);
                    hlsCusPrjProjectService.contextFactoringCreateMultiple(requestCtx, prjProject, TEMPLATE_CODE, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                hlsCusPrjProject.setLastUpdateDate(new Date());
                hlsCusPrjProject.setContractEndDate(new Date());
                hlsCusPrjProject.setContractStatus(APPROVED);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
            } else if (REJECTED.equalsIgnoreCase(result)) {
                hlsCusPrjProject.setLastUpdateDate(new Date());
                hlsCusPrjProject.setContractEndDate(new Date());
                hlsCusPrjProject.setContractStatus(REJECTED);
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, hlsCusPrjProject);
            }
        }
    }
}
