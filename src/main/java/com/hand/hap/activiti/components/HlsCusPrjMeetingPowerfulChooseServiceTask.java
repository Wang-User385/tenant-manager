package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * <p>
 * description
 * </p>
 *
 * @author dengyu.li@hand-china.com 2020/5/20 19:25
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjMeetingPowerfulChooseServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService service;

    public HlsCusPrjMeetingPowerfulChooseServiceTask() {
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long projectId = (Long) delegateExecution.getVariable("projectId");

        if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equals(result)) {
            if (projectId != null) {
                HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
                hlsCusPrjProject.setProjectId(projectId);
                hlsCusPrjProject = service.selectByPrimaryKey(requestCtx, hlsCusPrjProject);
                String powerfulPerson = hlsCusPrjProject.getPowerfulPerson();
                if (powerfulPerson != null) {
                    delegateExecution.setVariable("powerfulPerson", powerfulPerson);
                } else {
//                    throw new RuntimeException(new ResMessageException("请填写必输字段！"));
                    throw new HlsCusException("请选择有权人！");
                }
            }
        }
    }
}
