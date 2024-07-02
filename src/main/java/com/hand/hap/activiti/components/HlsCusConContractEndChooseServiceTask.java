package com.hand.hap.activiti.components;


import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import hls.core.utils.exception.HlsCusException;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractEndChooseServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    public HlsCusConContractEndChooseServiceTask() {
    }

    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String projectId = delegateExecution.getProcessInstanceBusinessKey();

        HlsCusPrjProject resultHlsCusPrjProject = new HlsCusPrjProject();
        //根据状态修改合同信息
        resultHlsCusPrjProject.setProjectId(Long.parseLong(projectId));
        resultHlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProject);
        String conEndTask = resultHlsCusPrjProject.getConEndTask();
        if ("APPROVED".equalsIgnoreCase(result)) {
            if(conEndTask == null || conEndTask.trim().length() == 0){
                throw new HlsCusException("请填写必输字段！");
            }else{
                delegateExecution.setVariable("conEndTask",conEndTask);
            }
        }
    }
}
