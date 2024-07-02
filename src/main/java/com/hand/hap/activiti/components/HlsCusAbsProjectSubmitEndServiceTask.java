package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.abs.dto.HlsCusAbsProject;
import com.hand.hls.abs.service.HlsCusAbsProjectService;
import hls.core.sys.event.service.SysEventService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by cyy on 2018/12/06.
 * ABS立项审批结束
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusAbsProjectSubmitEndServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusAbsProjectService hlsCusAbsProjectService;
    @Autowired
    private SysEventService sysEventService;

    public static final String PROPERTY_ABS_PROJECT_APPROVED = "ABS_PROJECT.APPROVED";
    public static final String PROPERTY_ABS_PROJECT_APPROVED_RETURN = "ABS_PROJECT.APPROVED_RETURN";


    public HlsCusAbsProjectSubmitEndServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        String flagDesc = null;
        String PROPERTY_ABS_PROJECT = "";
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String hlsCusAbsProjectPrams = (String) delegateExecution.getVariable("hlsCusAbsProject");
        HlsCusAbsProject hlsCusAbsProject = JSON.parseObject(hlsCusAbsProjectPrams, HlsCusAbsProject.class);
        HlsCusAbsProject resultHlsCusAbsProject = new HlsCusAbsProject();

        resultHlsCusAbsProject.setProjectId(hlsCusAbsProject.getProjectId());
        resultHlsCusAbsProject = hlsCusAbsProjectService.selectByPrimaryKey(requestCtx, resultHlsCusAbsProject);
        databaseLockProvider.lock(resultHlsCusAbsProject);
        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
            flagDesc = "立项成功";
            PROPERTY_ABS_PROJECT = PROPERTY_ABS_PROJECT_APPROVED;
            resultHlsCusAbsProject.setProjectStatus(flag);
            hlsCusAbsProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusAbsProject);
        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            flag = "APPROVED_RETURN";
            flagDesc = "立项退回";
            PROPERTY_ABS_PROJECT = PROPERTY_ABS_PROJECT_APPROVED_RETURN;
            resultHlsCusAbsProject.setProjectStatus(flag);
            hlsCusAbsProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusAbsProject);
        }
//        Map<String, Object> evenParams = new HashMap<>();
//        evenParams.put("message", "保理ABS" + resultHlsCusAbsProject.getProjectNumber() + flagDesc);
//        evenParams.put("noticeTitle", "保理ABS审批");
//        evenParams.put("url", "");
//        evenParams.put("level", 1L);
//        evenParams.put("noticeType", "NOTICE");
//        sysEventService.eventSave(requestCtx, resultHlsCusAbsProject.getProjectId(), resultHlsCusAbsProject.getDocumentCategory(), resultHlsCusAbsProject.getDocumentType()
//                , "FCT", PROPERTY_ABS_PROJECT, "P2D", evenParams);
    }
}
