package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.IPrjProjectService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrjProjectUpdateStatus implements TaskListener, IActivitiBean {

    @Autowired
    private IPrjProjectService prjProjectService;
    private static final String BUSINESSAPPROVED = "BUSINESSAPPROVED";
    private static final String APPROVEDRETURN = "APPROVED_RETURN";
    private static final String APPROVING = "APPROVING";

    @Override
    public void notify(DelegateTask delegateTask) {
        String clickButtonEvent = delegateTask.getVariable("approveResult").toString();
        String prj = (String) delegateTask.getVariable("project");
        HlsCusPrjProject prjProject = JSON.parseObject(prj, HlsCusPrjProject.class);
        IRequest requestCtx = (IRequest) delegateTask.getVariable("iRequest");
        HlsCusPrjProject project = prjProjectService.selectByPrimaryKey(requestCtx, prjProject);
        if (APPROVEDRETURN.equals(clickButtonEvent)) {
            // 退回将状态改为审批中
            project.setProjectStatus(APPROVING);
        } else {
            // 更改状态为业务审核通过
            project.setProjectStatus(BUSINESSAPPROVED);
        }
        prjProjectService.updateByPrimaryKeySelective(requestCtx, project);
    }
}
