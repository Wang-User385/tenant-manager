package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.prj.dto.HlsCusProjectCreditNotice;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.IHlsCusProjectCreditNoticeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by xuju on 2018/04/19.
 * modify by xuju on 2018/05/22
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjProjectSaveNoticeTimeServiceTask implements JavaDelegate, IActivitiBean {

    private static final String APPROVED_TASK_ID = "sid-E29qxrN6-j1Uy-4hLi-8A2L-XkeLN3rKMUku";
    private static final String SUSPEND_TASK_ID = "sid-3jTzeSep-p9BK-4DO3-82cX-jUNcUGLox8P2";
    private static final String REJECT_TASK_ID = "sid-3jTzeSep-p9BK-4DO3-82cX-jUNcUGLox8P9";

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private IHlsCusProjectCreditNoticeService hlsCusProjectCreditNoticeService;



    public HlsCusPrjProjectSaveNoticeTimeServiceTask(){}
    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        Long projectId = Long.parseLong(delegateExecution.getVariable("projectId").toString());

        String currentActivityId = delegateExecution.getCurrentActivityId();
        String result = (String) delegateExecution.getVariable("approveResult");
        Long instanceId = Long.parseLong(delegateExecution.getProcessInstanceId());

        HlsCusProjectCreditNotice hlsCusProjectCreditNotice = new HlsCusProjectCreditNotice();
        hlsCusProjectCreditNotice.setInstanceId(instanceId);
        hlsCusProjectCreditNotice.setProjectId(projectId);
        List<HlsCusProjectCreditNotice> hlsCusProjectCreditNoticeList = hlsCusProjectCreditNoticeService.QueryAllByInstanceId(hlsCusProjectCreditNotice);


        Date sysDate = new Date();
        for(HlsCusProjectCreditNotice dt:hlsCusProjectCreditNoticeList){
            dt.setNoticeCreateDate(sysDate);
            hlsCusProjectCreditNoticeService.updateByPrimaryKeySelective(iRequest,dt);
        }


    }

}
