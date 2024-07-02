package com.hand.hap.activiti.components;


import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjChangeMeetingUpdateServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    public HlsCusPrjChangeMeetingUpdateServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {

        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String hlsCusPrjProjectPramsNew = (String) delegateExecution.getVariable("hlsCusPrjProjectNew");
        HlsCusPrjProject hlsCusPrjProjectNew = JSON.parseObject(hlsCusPrjProjectPramsNew, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProjectNew = new HlsCusPrjProject();

        resultHlsCusPrjProjectNew.setProjectId(hlsCusPrjProjectNew.getProjectId());
        resultHlsCusPrjProjectNew = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProjectNew);
        databaseLockProvider.lock(resultHlsCusPrjProjectNew);

        if(resultHlsCusPrjProjectNew.getProjectId() != null){
            resultHlsCusPrjProjectNew.setApprovalStatus("MEETING");
            resultHlsCusPrjProjectNew.setProcessInstanceId(Long.parseLong(delegateExecution.getProcessInstanceId()));
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx,resultHlsCusPrjProjectNew);

        }

    }
}
