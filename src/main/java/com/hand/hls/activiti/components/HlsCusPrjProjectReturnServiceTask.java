package com.hand.hls.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectReturnServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;


    public HlsCusPrjProjectReturnServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String currentActivityId = delegateExecution.getCurrentActivityId();

        String flag = null;
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        IRequest requestCtx = (IRequest)delegateExecution.getVariable("iRequest");
        String result = (String)delegateExecution.getVariable("approveResult");
        String employeeCode = (String)delegateExecution.getVariable("startUserName");
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProject");
        HlsCusPrjProject hlsCusPrjProject = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProject = new HlsCusPrjProject();
        //根据状态修改立项信息
        resultHlsCusPrjProject.setProjectId(hlsCusPrjProject.getProjectId());
        IRequest request = RequestHelper.getCurrentRequest(true);
        request.setAttribute("wflRuleControlFlag", "Y");
        resultHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, resultHlsCusPrjProject);
        databaseLockProvider.lock(resultHlsCusPrjProject);


        //更新项目流程结束标志
        String instanceEndFlag = "Y";
        resultHlsCusPrjProject.setInstanceEndFlag(instanceEndFlag);
        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);

        if("RETURN_RESEND".equalsIgnoreCase(result)){
            flag = "NEW";
            resultHlsCusPrjProject.setProjectStatus(flag);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, resultHlsCusPrjProject);
        }

    }

}
